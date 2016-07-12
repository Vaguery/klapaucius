(ns push.type.item.vectorized
  (:use     [push.instructions.core :only (build-instruction)]
            [push.instructions.dsl]
            [push.type.core]
            )
  (:require 
            [push.instructions.aspects :as aspects]
            [push.util.numerics :as num]
            [clojure.math.numeric-tower :as nt]
            [push.util.code-wrangling :as fix]
            [inflections.core :as inflect]
            ))


;; SUPPORT


(defn replacefirst
  "Takes a vector, and replaces the first occurrence of the target (if it appears) with the substitute. Returns a vector."
  [coll target substitute]
  (let [is-here (boolean (some #{target} coll))
        [front back] (split-with (complement #{target}) coll)
        new-tail (if is-here (conj (rest back) substitute) back)]
    (into [] (concat front new-tail))))



(defn vector-of-type?
  [item type]
  (let [checker (:recognizer (:router type))]
    (and  (vector? item)
          (boolean (seq item))
          (every? #(checker %) item))))


;; EXTERNAL TYPES


(defn x-build-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-build")]
    (eval (list
      `push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-build` pops the top `:scalar` item, and calculates an index modulo the size of the `:" rootname "` stack. It takes the stack, down to that index, and pushes a new `:" typename "` vector out of those elements (top element last in the vector).")
      :tags #{:vector}

      `(consume-top-of :scalar :as :where)
      `(consume-stack ~rootname :as :items) 
      `(calculate [:where :items]
          #(if (empty? %2)
            0
            (num/scalar-to-index %1 (count %2))) :as :idx)
      `(calculate [:idx :items]
          #(into [] (take %1 %2)) :as :new-vector)
      `(calculate [:idx :items] 
          #(drop %1 %2) :as :new-stack)
      `(replace-stack ~rootname :new-stack)
      `(push-onto ~typename :new-vector)
      ))))



(defn x-butlast-instruction
  [typename]
  (let [instruction-name (str (name typename) "-butlast")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-butlast` pops the top `" typename "` item and pushes the same vector lacking its last item (or nothing, if it ends up empty).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(into [] (butlast %1)) :as :most)
      `(push-onto ~typename :most)
      ))))



(defn x-byexample-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-byexample")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-byexample` pops the top `" typename "` item and takes an equal number of items from `" rootname "` to build a new vector of the same type. Both vectors are pushed back onto `" typename "`, newest on top. If there aren't enough items on `" rootname "`, the template vector is replaced.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :template)
      `(consume-stack ~rootname :as :pieces)
      `(calculate [:template :pieces]
          #(<= (count %1) (count %2)) :as :enough?)
      `(calculate [:enough? :template :pieces]
          #(if %1 (into [] (take (count %2) %3)) nil) :as :new-vector)
      `(calculate [:enough? :template :pieces]
          #(if %1
            (into '() (reverse (drop (count %2) %3))) 
            %3) :as :new-pieces)
      `(push-onto ~typename :template)
      `(push-onto ~typename :new-vector)
      `(replace-stack ~rootname :new-pieces)
      ))))



(defn x-concat-instruction
  [typename]
  (let [instruction-name (str (name typename) "-concat")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-concat` pops the top two `" typename "` items and pushes the the concatenation of the top one onto the second one.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2]
          #(into [] (concat %1 %2)) :as :concatted)
      `(save-max-collection-size :as :limit)
      `(calculate [:concatted :limit]
          #(if (< (count %1) %2)
            %1
            nil) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn x-conj-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-conj")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-conj` pops the top `" typename "` item and the top `" rootname "` item, and appends the latter to the former, pushing the result.")
      :tags #{:vector}

      `(consume-top-of ~rootname :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2]
          #(conj %1 %2) :as :conjed)
      `(push-onto ~typename :conjed)
      ))))




(defn includes-this?
  "predicate used in x-contains?-instruction"
  [v item]
  (boolean (some #(= % item) v)))



(defn x-contains?-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-contains?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-contains?` pops the top `" typename "` item and the top `" rootname "` item, and pushes `true` to the `:boolean` stack if the latter is present in the former.")
      :tags #{:vector}

      `(consume-top-of ~rootname :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2]
          #(includes-this? %1 %2) :as :found)
      `(push-onto :boolean :found)
      ))))




(defn x-cyclevector-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-cyclevector")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-cyclevector` saves a copy of the `" rootname "` stack and pops two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value. Items from the `" rootname "` stack are included, cycling to fill if needed, and the result is pushed to `" typename"`. If the `" rootname "` stack is empty, an empty vector is the result.")
      :tags #{:vector}

      `(save-stack ~rootname :as :stack)
      `(consume-top-of :scalar :as :scale)
      `(consume-top-of :scalar :as :raw-count)
      `(calculate [:scale]
          #(nth [10 100 1000 10000] (num/scalar-to-index %1 4)) :as :relative)
      `(calculate [:raw-count :relative]
          #(num/scalar-to-index %1 %2) :as :size)
      `(calculate [:size :stack]
          #(into [] (take %1 (cycle %2))) :as :result)
      `(push-onto ~typename :result)
      ))))





(defn x-distinct-instruction
  [typename]
  (let [instruction-name (str (name typename) "-distinct")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-distinct` pops the top `" typename "` item, then pushes a new `" typename "` item containing only the first copy of each item in the original.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
          #(into [] (distinct %1)) :as :result)
      `(push-onto ~typename :result)
      ))))




(defn x-do*each-instruction
  [typename]
  (let [instruction-name (str (name typename) "-do*each")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-do*each` pops the top `" typename "` item and the top of the `:exec` stack, and pushes this continuation form to `:exec`: `([first vector] [exec item] [rest vector] " instruction-name " [exec item])`. If the vector is empty, it is simply consumed with no result.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :exec :as :actor)
      `(calculate [:arg :actor]
          #(if (empty? %1)
            nil
            (list (first %1)
                   %2
                   (into [] (rest %1))
                   (keyword ~instruction-name)
                   %2))
          :as :continuation)
      `(push-onto :exec :continuation)
      ))))



(defn x-emptyitem?-instruction
  [typename]
  (let [instruction-name (str (name typename) "-emptyitem?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-emptyitem?` pops the top `" typename "` item and pushes `true` to the `:boolean` stack if it's empty.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(boolean (empty? %1)) :as :empty)
      `(push-onto :boolean :empty)
      ))))




(defn x-fillvector-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-fillvector")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-fillvector` pops the top `" rootname "` item and two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of copies of the " rootname "` are made into a single `" typename "` and pushed there.")
      :tags #{:vector}

      `(consume-top-of ~rootname :as :item)
      `(consume-top-of :scalar :as :scale)
      `(consume-top-of :scalar :as :raw-count)
      `(calculate [:scale]
          #(nth [10 100 1000 10000] (num/scalar-to-index %1 4)) :as :relative)
      `(calculate [:raw-count :relative]
          #(num/scalar-to-index %1 %2) :as :size)
      `(calculate [:item :size]
          #(into [] (take %2 (repeat %1))) :as :result)
      `(push-onto ~typename :result)
      ))))





(defn x-first-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-first")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the first element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(first %1) :as :top)
      `(push-onto ~rootname :top)
      ))))



(defn x-generalize-instruction
  [typename]
  (let [instruction-name (str (name typename) "-generalize")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-generalize` pops the top `" typename "` item, and pushes it onto the `:vector` stack.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(push-onto :vector :arg)
      ))))



(defn x-generalizeall-instruction
  [typename]
  (let [instruction-name (str (name typename) "-generalizeall")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-generalizeall` puts the entire `" typename "` stack onto the `:vector` stack.")
      :tags #{:vector}

      `(consume-stack ~typename :as :stack)
      `(consume-stack :vector :as :old-vectors)
      `(calculate [:stack :old-vectors]
          #(into '() (reverse (concat %1 %2))) :as :new-vectors)
      `(replace-stack :vector :new-vectors)
      ))))



(defn x-indexof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-indexof")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-indexof` pops the top `" typename "` item and the top `" rootname "` item, and pushes a `:scalar` (integer) indicating the first appearance of the latter in the former (or -1 if it's not found).")
      :tags #{:vector}

      `(consume-top-of ~rootname :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2]
          #(.indexOf %1 %2) :as :where)
      `(push-onto :scalar :where)
      ))))



(defn x-items-instruction
  [typename]
  (let [instruction-name (str (name typename) "-items")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-items` pops the top `" typename "` item, and pushes its contents onto `:exec` as a code block.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
          #(seq %1) :as :items)
      `(push-onto :exec :items)
      ))))



(defn x-last-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-last")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-last` pops the top `" typename "` item and pushes the last element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(last %1) :as :tail)
      `(push-onto ~rootname :tail)
      ))))



(defn x-length-instruction
  [typename]
  (let [instruction-name (str (name typename) "-length")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-length` pops the top `" typename "` item and pushes its count to the `:scalar` stack.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(count %1) :as :len)
      `(push-onto :scalar :len)
      ))))



(defn x-new-instruction
  [typename]
  (let [instruction-name (str (name typename) "-new")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-new` pushes an empty vector to the stack.")
      :tags #{:vector}

      `(calculate []
          (fn [] (vector)) :as :nuttin)
      `(push-onto ~typename :nuttin)
      ))))



(defn x-nth-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-nth")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-nth` pops the top `" typename "` item and the the top `:scalar`. It converts the `:scalar` value into an index (modulo the vector's length) then pushes the indexed element to the `" rootname "` stack.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of :scalar :as :int)
      `(calculate [:arg1 :int]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
      `(calculate [:arg1 :idx]
          #(if (empty? %1) nil (nth %1 %2)) :as :result)
      `(push-onto ~rootname :result)
      ))))



(defn x-occurrencesof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-occurrencesof")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-occurrencesof` pops the top `" typename "` item and the the top `" rootname "`. It pushes a `:scalar` (integer) indicating how many copies of the latter appear in the former.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :vec)
      `(consume-top-of ~rootname :as :item)
      `(calculate [:vec :item]
          #(get (frequencies %1) %2 0) :as :result)
      `(push-onto :scalar :result)
      ))))



(defn x-pt-crossover-instruction
  [typename]
  (let [instruction-name (str (name typename) "-pt-crossover")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-pt-crossover` pops the top `" typename "` two items (call them `B` and `A` respectively) and the the top two `:scalar` values. It converts the `:scalar` values into indices (modulo each vector's length), then pushes two new vectors to the `" typename "` stack. The first contains the front of `A` and the back of `B`, and the other vice versa, using the two indices as cutpoints")
      :tags #{:vector}

      `(consume-top-of ~typename :as :B)
      `(consume-top-of ~typename :as :A)
      `(consume-top-of :scalar :as :idxB)
      `(consume-top-of :scalar :as :idxA)
      `(calculate [:A :idxA]
          #(if (empty? %1)
            0
            (num/scalar-to-index %2 (count %1))) :as :cutA)
      `(calculate [:B :idxB]
          #(if (empty? %1)
            0
            (num/scalar-to-index %2 (count %1))) :as :cutB)
      `(calculate [:A  :B :cutA :cutB]
          #(into [] (concat (take %3 %1) (drop %4 %2))) :as :resultA)
      `(calculate [:A  :B :cutA :cutB]
          #(into [] (concat (take %4 %2) (drop %3 %1))) :as :resultB)
      `(push-onto ~typename :resultA)
      `(push-onto ~typename :resultB)
      ))))



(defn x-portion-instruction
  [typename]
  (let [instruction-name (str (name typename) "-portion")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-portion` pops the top `" typename "` item and the the top two `:scalar` values (call these `A` and `B`). It pushes a new `" typename "` which only includes items between `A` and `B` (which are rounded and coerced to fall in range by truncation; see code).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :scalar :as :a)
      `(consume-top-of :scalar :as :b)
      `(calculate [:arg :a]
          #(min (count %1) (max 0 (nt/round %2))) :as :cropped-a)
      `(calculate [:arg :b]
          #(min (count %1) (max 0 (nt/round %2))) :as :cropped-b)
      `(calculate [:arg :cropped-a :cropped-b]
          #(into [] (drop (min %2 %3) (take (max %2 %3) %1))) :as :subvector)
      `(push-onto ~typename :subvector)
      ))))



(defn x-remove-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-remove")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-remove` pops the top `" typename "` item and the the top `" rootname "`. It pushes a new `" typename "` which has all occurrences of the `" rootname "` eliminated.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of ~rootname :as :purge)
      `(calculate [:arg1 :purge]
          #(into [] (remove #{%2} %1)) :as :less)
      `(push-onto ~typename :less)
      ))))



(defn x-replace-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replace")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-replace` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has all occurrences of `B` replaced with `A`.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of ~rootname :as :a)
      `(consume-top-of ~rootname :as :b)
      `(calculate [:arg1 :a :b]
          #(into [] (replace {%3 %2} %1)) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn x-replacefirst-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replacefirst")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-replacefirst` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has the first occurrence of `B` replaced with `A`.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of ~rootname :as :a)
      `(consume-top-of ~rootname :as :b)
      `(calculate [:arg1 :a :b]
          #(replacefirst %1 %3 %2) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn x-rest-instruction
  [typename]
  (let [instruction-name (str (name typename) "-rest")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-rest` pops the top `" typename "` item and pushes a vector containing all but the first element to the `" typename "` stack (or an empty vector, if it's empty; NOTE difference from `first` and others).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(into [] (rest %1)) :as :end)
      `(push-onto ~typename :end)
      ))))



(defn x-reverse-instruction
  [typename]
  (let [instruction-name (str (name typename) "-reverse")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the reversed vector to the `" typename "` stack.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
          #(into [] (reverse %1)) :as :bw)
      `(push-onto ~typename :bw)
      ))))



(defn x-set-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-set")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-set` pops the top `" typename "` item, the top `:scalar` and the the top `" rootname "` item. It pushes a new `" typename "` which has the indexed item (modulo length) replaced with the new element. If the `" typename "` is empty, it is returned but the other arguments are consumed.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of ~rootname :as :subst)
      `(consume-top-of :scalar :as :which)
      `(calculate [:arg :which]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
      `(calculate [:arg :idx :subst]
          #(if (empty? %1) %1 (into [] (assoc %1 %2 %3))) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn x-shatter-instruction  ;; was -pushall
  [typename rootname]
  (let [instruction-name (str (name typename) "-shatter")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-shatter` pops the top `" typename "` item and pushes each of its items in turn to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-stack ~rootname :as :old-stack)
      `(calculate [:arg1 :old-stack]
          #(into '() (reverse (concat %1 %2))) :as :new-stack)
      `(replace-stack ~rootname :new-stack)
      ))))



(defn x-take-instruction
  [typename]
  (let [instruction-name (str (name typename) "-take")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-take` pops the top `" typename "` item and the the top `:scalar`. It converts the `:scalar` value into an index (modulo one more than the vector's length) then pushes a new `" typename "` item containing only the items in the original from the start up to the indexed point.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of :scalar :as :int)
      `(calculate [:arg1 :int]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (inc (count %1)))) :as :idx)
      `(calculate [:arg1 :idx]
          #(into [] (take %2 %1)) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn filter-vector-with-vector
  [v f]
  (into [] (filter #((set f) %1) v)))



(defn x-vfilter-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vfilter")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-vfilter` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a new `" typename "` that only contains items in `A` that are also in `B`.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :filter)
      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg :filter]
          #(filter-vector-with-vector %1 %2) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn remove-vector-with-vector
  [v f]
  (into [] (remove #((set f) %1) v)))



(defn x-vremove-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vremove")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-vremove` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a new `" typename "` that only contains items in `A` that are NOT also in `B`.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :filter)
      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg :filter]
          #(remove-vector-with-vector %1 %2) :as :result)
      `(push-onto ~typename :result)
      ))))



(defn x-vsplit-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vsplit")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`" typename "-vsplit` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a code block to `:exec` containing two new `" typename "` items: The first only contains items in `A` that are also in `B`, and the second only contains items in `A` NOT in `B`.")
      :tags #{:vector}

      `(consume-top-of ~typename :as :filter)
      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg :filter]
          #(list
            (filter-vector-with-vector %1 %2)
            (remove-vector-with-vector %1 %2)) :as :result)
      `(push-onto :exec :result)
      ))))




(defn build-vectorized-type
  "creates a vector [sub]type for another Push type"
  [content-type]
  (let [typename (keyword (inflect/plural (name (:name content-type))))
        rootname (keyword (name (:name content-type)))]
    ( ->  (make-type  typename
                        :recognized-by #(vector-of-type? % content-type)
                        :attributes #{:vector})
          aspects/make-set-able
          aspects/make-cycling
          aspects/make-equatable
          aspects/make-into-tagspaces
          aspects/make-movable
          aspects/make-printable
          aspects/make-quotable
          aspects/make-repeatable
          aspects/make-returnable
          aspects/make-storable
          aspects/make-taggable
          aspects/make-visible
          (attach-instruction , (x-build-instruction typename rootname))
          (attach-instruction , (x-butlast-instruction typename))
          (attach-instruction , (x-byexample-instruction typename rootname))
          (attach-instruction , (x-concat-instruction typename))
          (attach-instruction , (x-conj-instruction typename rootname))
          (attach-instruction , (x-contains?-instruction typename rootname))
          (attach-instruction , (x-cyclevector-instruction typename rootname))
          (attach-instruction , (x-distinct-instruction typename))
          (attach-instruction , (x-do*each-instruction typename))
          (attach-instruction , (x-emptyitem?-instruction typename))
          (attach-instruction , (x-fillvector-instruction typename rootname))
          (attach-instruction , (x-first-instruction typename rootname))
          (attach-instruction , (x-generalize-instruction typename))
          (attach-instruction , (x-generalizeall-instruction typename))
          (attach-instruction , (x-indexof-instruction typename rootname))
          (attach-instruction , (x-items-instruction typename))
          (attach-instruction , (x-last-instruction typename rootname))
          (attach-instruction , (x-length-instruction typename))
          (attach-instruction , (x-new-instruction typename))
          (attach-instruction , (x-nth-instruction typename rootname))
          (attach-instruction , (x-occurrencesof-instruction typename rootname))
          (attach-instruction , (x-portion-instruction typename))
          (attach-instruction , (x-pt-crossover-instruction typename))
          (attach-instruction , (x-shatter-instruction typename rootname))
          (attach-instruction , (x-remove-instruction typename rootname))
          (attach-instruction , (x-replace-instruction typename rootname))
          (attach-instruction , (x-replacefirst-instruction typename rootname))
          (attach-instruction , (x-rest-instruction typename))
          (attach-instruction , (x-set-instruction typename rootname))
          (attach-instruction , (x-take-instruction typename))
          (attach-instruction , (x-reverse-instruction typename))
          (attach-instruction , (x-vfilter-instruction typename))
          (attach-instruction , (x-vremove-instruction typename))
          (attach-instruction , (x-vsplit-instruction typename))
          )))

