(ns push.type.item.vectorized
  (:require [push.instructions.dsl      :as d]
            [push.instructions.core     :as i]
            [push.type.core             :as t]
            [push.util.code-wrangling   :as u]
            [push.instructions.aspects  :as aspects]
            [inflections.core           :as inflect]
            [clojure.math.numeric-tower :as nt]
            [push.util.numerics         :as num]
            [push.util.exotics          :as exotic]
            ))


(defn replacefirst
  "Takes a vector, and replaces the first occurrence of the target (if it appears) with the substitute. Returns a vector."
  [coll target substitute]
  (let [is-here (boolean (some #{target} coll))
        [front back] (split-with (complement #{target}) coll)
        new-tail (if is-here (conj (rest back) substitute) back)]
    (vec (concat front new-tail))))



(defn vector-of-type?
  [item type]
  (let [checker (:recognizer (:router type))]
    (and  (vector? item)
          (boolean (seq item))
          (every? checker item))))


;; EXTERNAL TYPES


(defn x-build-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-build")]
    (eval (list
      `push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-build` pops the top `:scalar` item, and d/calculates an index modulo the size of the `:" rootname "` stack. It takes the stack, down to that index, and pushes a new `:" typename "` vector out of those elements (top element last in the vector). Returns a code block containing the remaining items from the stack, then the vector. If there is an overflow error, the original consumed stack is returned to `:" rootname "`.")
      :tags #{:vector}

      `(d/consume-top-of :scalar :as :where)
      `(d/consume-stack ~rootname :as :old-stack)
      `(d/save-max-collection-size :as :limit)
      `(d/calculate [:where :old-stack]
          #(if (empty? %2)
            0
            (num/scalar-to-index %1 (count %2))) :as :idx)
      `(d/calculate [:idx :old-stack]
        #(into [] (take %1 %2)) :as :new-vector)
      `(d/calculate [:idx :old-stack]
        #(u/list! (reverse (drop %1 %2))) :as :reduced-stack)
      `(d/calculate [:limit :reduced-stack :new-vector]
        #(> (u/count-collection-points (list %2 %3)) %1) :as :oversized?)
      `(d/calculate [:oversized? :old-stack :reduced-stack  :new-vector]
        #(if %1 (u/list! (reverse %2)) (list %3 %4)) :as :results)
      `(d/calculate [:oversized? :old-stack]
        #(if %1 %2 (list)) :as :recovered-stack)
      `(d/replace-stack ~rootname :recovered-stack)
      `(d/return-item :results)
      ))))



(defn x-butlast-instruction
  [typename]
  (let [instruction-name (str (name typename) "-butlast")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-butlast` pops the top `" typename "` item and pushes the same vector lacking its last item (or nothing, if it ends up empty).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(into [] (butlast %1)) :as :most)
      `(d/return-item :most)
      ))))



(defn x-byexample-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-byexample")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-byexample` pops the top `" typename "` item and takes an equal number of items from `" rootname "` to build a new vector of the same type. Both vectors are pushed back onto `" typename "`, newest on top. If there aren't enough items on `" rootname "`, the template vector is replaced.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :template)
      `(d/consume-stack ~rootname :as :pieces)
      `(d/calculate [:template :pieces]
          #(<= (count %1) (count %2)) :as :enough?)
      `(d/calculate [:enough? :template :pieces]
          #(if %1 (into [] (take (count %2) %3)) nil) :as :new-vector)
      `(d/calculate [:enough? :template :pieces]
          #(if %1
            (u/list! (drop (count %2) %3))
            %3) :as :new-pieces)
      `(d/push-onto ~typename :template)
      `(d/push-onto ~typename :new-vector)
      `(d/replace-stack ~rootname :new-pieces)
      ))))



(defn x-concat-instruction
  [typename]
  (let [instruction-name (str (name typename) "-concat")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-concat` pops the top two `" typename "` items and pushes the the concatenation of the top one onto the second one.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg2)
      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1 :arg2]
          #(into [] (concat %1 %2)) :as :concatted)
      `(d/save-max-collection-size :as :limit)
      `(d/calculate [:concatted :limit]
          #(if (< (count %1) %2)
            %1
            nil) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn x-conj-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-conj")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-conj` pops the top `" typename "` item and the top `" rootname "` item, and appends the latter to the former, pushing the result.")
      :tags #{:vector}

      `(d/consume-top-of ~rootname :as :arg2)
      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1 :arg2]
          #(conj %1 %2) :as :conjed)
      `(d/push-onto ~typename :conjed)
      ))))




(defn includes-this?
  "predicate used in x-contains?-instruction"
  [v item]
  (boolean (some #(= % item) v)))



(defn x-contains?-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-contains?")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-contains?` pops the top `" typename "` item and the top `" rootname "` item, and pushes `true` to the `:boolean` stack if the latter is present in the former.")
      :tags #{:vector}

      `(d/consume-top-of ~rootname :as :arg2)
      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1 :arg2]
          #(includes-this? %1 %2) :as :found)
      `(d/push-onto :boolean :found)
      ))))




(defn x-cyclevector-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-cyclevector")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-cyclevector` saves a copy of the `" rootname "` stack and pops two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value. Items from the `" rootname "` stack are included, cycling to fill if needed, and the result is pushed to `" typename"`. If the `" rootname "` stack is empty, an empty vector is the result.")
      :tags #{:vector}

      `(d/save-stack ~rootname :as :stack)
      `(d/consume-top-of :scalar :as :scale)
      `(d/consume-top-of :scalar :as :raw-count)
      `(d/calculate [:scale] #(nth [10 100 1000] (num/scalar-to-index %1 3)) :as :relative)
      `(d/calculate [:raw-count :relative] #(num/scalar-to-index %1 %2) :as :size)
      `(d/calculate [:size :stack] #(into [] (take %1 (cycle %2))) :as :result)
      `(d/push-onto ~typename :result)
      ))))





(defn x-distinct-instruction
  [typename]
  (let [instruction-name (str (name typename) "-distinct")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-distinct` pops the top `" typename "` item, then pushes a new `" typename "` item containing only the first copy of each item in the original.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/calculate [:arg]
          #(into [] (distinct %1)) :as :result)
      `(d/push-onto ~typename :result)
      ))))




(defn x-do*each-instruction
  [typename]
  (let [instruction-name (str (name typename) "-do*each")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-do*each` pops the top `" typename "` item and the top of the `:exec` stack, and pushes this continuation form to `:exec`: `([first vector] [exec item] [rest vector] " instruction-name " [exec item])`. If the vector is empty, it is simply consumed with no result.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/consume-top-of :exec :as :actor)
      `(d/calculate [:arg :actor]
          #(if (empty? %1)
            nil
            (list (first %1)
                   %2
                   (into [] (rest %1))
                   (keyword ~instruction-name)
                   %2))
          :as :continuation)
      `(d/return-item :continuation)
      ))))



(defn x-emptyitem?-instruction
  [typename]
  (let [instruction-name (str (name typename) "-emptyitem?")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-emptyitem?` pops the top `" typename "` item and pushes `true` to the `:boolean` stack if it's empty.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(boolean (empty? %1)) :as :empty)
      `(d/push-onto :boolean :empty)
      ))))




(defn x-fillvector-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-fillvector")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-fillvector` pops the top `" rootname "` item and two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of copies of the " rootname "` are made into a single `" typename "` and pushed there. If the expected size of the resulting item (length of vector times number of code points in item) exceeds the `max-collection-size`, an error is pushed instead.")
      :tags #{:vector}

      `(d/consume-top-of ~rootname :as :item)
      `(d/consume-top-of :scalar :as :scale)
      `(d/consume-top-of :scalar :as :raw-count)
      `(d/save-max-collection-size :as :limit)
      `(d/calculate [:scale]
          #(nth [10 100 1000] (num/scalar-to-index %1 3)) :as :relative)
      `(d/calculate [:raw-count :relative]
          #(num/scalar-to-index %1 %2) :as :vector-length)
      `(d/calculate [:item :vector-length :limit]
          #(> (*' (u/count-collection-points %1) %2) %3) :as :oversized?)
      `(d/calculate [:oversized?]
          #(if %1 (str ~instruction-name " produced oversized result") nil) :as :warning)
      `(d/calculate [:oversized? :item :vector-length]
          #(if %1 nil (into [] (take %3 (repeat %2)))) :as :result)
      `(d/push-onto ~typename :result)
      `(d/record-an-error :from :warning)
      ))))





(defn x-first-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-first")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the first element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(first %1) :as :top)
      `(d/push-onto ~rootname :top)
      ))))



(defn x-generalize-instruction
  [typename]
  (let [instruction-name (str (name typename) "-generalize")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-generalize` pops the top `" typename "` item, and pushes it onto the `:vector` stack.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/push-onto :vector :arg)
      ))))



(defn x-generalizeall-instruction
  [typename]
  (let [instruction-name (str (name typename) "-generalizeall")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-generalizeall` puts the entire `" typename "` stack onto the `:vector` stack.")
      :tags #{:vector}

      `(d/consume-stack ~typename :as :stack)
      `(d/consume-stack :vector :as :old-vectors)
      `(d/calculate [:stack :old-vectors]
          #(u/list! (concat %1 %2)) :as :new-vectors)
      `(d/replace-stack :vector :new-vectors)
      ))))



(defn x-indexof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-indexof")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-indexof` pops the top `" typename "` item and the top `" rootname "` item, and pushes a `:scalar` (integer) indicating the first appearance of the latter in the former (or -1 if it's not found).")
      :tags #{:vector}

      `(d/consume-top-of ~rootname :as :arg2)
      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1 :arg2]
          #(.indexOf %1 %2) :as :where)
      `(d/push-onto :scalar :where)
      ))))



(defn x-items-instruction
  [typename]
  (let [instruction-name (str (name typename) "-items")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-items` pops the top `" typename "` item, and pushes its contents onto `:exec` as a code block.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/calculate [:arg]
          #(seq %1) :as :items)
      `(d/return-item :items)
      ))))



(defn x-last-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-last")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-last` pops the top `" typename "` item and pushes the last element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(last %1) :as :tail)
      `(d/push-onto ~rootname :tail)
      ))))



(defn x-length-instruction
  [typename]
  (let [instruction-name (str (name typename) "-length")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-length` pops the top `" typename "` item and pushes its count to the `:scalar` stack.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(count %1) :as :len)
      `(d/push-onto :scalar :len)
      ))))



(defn x-new-instruction
  [typename]
  (let [instruction-name (str (name typename) "-new")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-new` pushes an empty vector to the stack.")
      :tags #{:vector}

      `(d/calculate []
          (fn [] (vector)) :as :nuttin)
      `(d/push-onto ~typename :nuttin)
      ))))



(defn x-nth-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-nth")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-nth` pops the top `" typename "` item and the the top `:scalar`. It converts the `:scalar` value into an index (modulo the vector's length) then pushes the indexed element to the `" rootname "` stack.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of :scalar :as :int)
      `(d/calculate [:arg1 :int]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
      `(d/calculate [:arg1 :idx]
          #(if (empty? %1) nil (nth %1 %2)) :as :result)
      `(d/push-onto ~rootname :result)
      ))))



(defn x-occurrencesof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-occurrencesof")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-occurrencesof` pops the top `" typename "` item and the the top `" rootname "`. It pushes a `:scalar` (integer) indicating how many copies of the latter appear in the former.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :vec)
      `(d/consume-top-of ~rootname :as :item)
      `(d/calculate [:vec :item]
          #(get (frequencies %1) %2 0) :as :result)
      `(d/push-onto :scalar :result)
      ))))



(defn x-pt-crossover-instruction
  [typename]
  (let [instruction-name (str (name typename) "-pt-crossover")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-pt-crossover` pops the top `" typename "` two items (call them `B` and `A` respectively) and the the top two `:scalar` values. It converts the `:scalar` values into indices (modulo each vector's length), then pushes two new vectors to the `" typename "` stack. The first contains the front of `A` and the back of `B`, and the other vice versa, using the two indices as cutpoints")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :B)
      `(d/consume-top-of ~typename :as :A)
      `(d/consume-top-of :scalar :as :idxB)
      `(d/consume-top-of :scalar :as :idxA)
      `(d/calculate [:A :idxA]
          #(if (empty? %1)
            0
            (num/scalar-to-index %2 (count %1))) :as :cutA)
      `(d/calculate [:B :idxB]
          #(if (empty? %1)
            0
            (num/scalar-to-index %2 (count %1))) :as :cutB)
      `(d/calculate [:A  :B :cutA :cutB]
          #(into [] (concat (take %3 %1) (drop %4 %2))) :as :resultA)
      `(d/calculate [:A  :B :cutA :cutB]
          #(into [] (concat (take %4 %2) (drop %3 %1))) :as :resultB)
      `(d/push-onto ~typename :resultA)
      `(d/push-onto ~typename :resultB)
      ))))



(defn x-portion-instruction
  [typename]
  (let [instruction-name (str (name typename) "-portion")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-portion` pops the top `" typename "` item and the the top two `:scalar` values (call these `A` and `B`). It pushes a new `" typename "` which only includes items between `A` and `B` (which are rounded and coerced to fall in range by truncation; see code).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/consume-top-of :scalar :as :a)
      `(d/consume-top-of :scalar :as :b)
      `(d/calculate [:arg :a]
          #(min (count %1) (max 0 (nt/round %2))) :as :cropped-a)
      `(d/calculate [:arg :b]
          #(min (count %1) (max 0 (nt/round %2))) :as :cropped-b)
      `(d/calculate [:arg :cropped-a :cropped-b]
          #(into [] (drop (min %2 %3) (take (max %2 %3) %1))) :as :subvector)
      `(d/push-onto ~typename :subvector)
      ))))



(defn x-remove-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-remove")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-remove` pops the top `" typename "` item and the the top `" rootname "`. It pushes a new `" typename "` which has all occurrences of the `" rootname "` eliminated.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of ~rootname :as :purge)
      `(d/calculate [:arg1 :purge]
          #(into [] (remove #{%2} %1)) :as :less)
      `(d/push-onto ~typename :less)
      ))))



(defn x-replace-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replace")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-replace` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has all occurrences of `B` replaced with `A`.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of ~rootname :as :a)
      `(d/consume-top-of ~rootname :as :b)
      `(d/calculate [:arg1 :a :b]
          #(into [] (replace {%3 %2} %1)) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn x-replacefirst-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replacefirst")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-replacefirst` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has the first occurrence of `B` replaced with `A`.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of ~rootname :as :a)
      `(d/consume-top-of ~rootname :as :b)
      `(d/calculate [:arg1 :a :b]
          #(replacefirst %1 %3 %2) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn x-rest-instruction
  [typename]
  (let [instruction-name (str (name typename) "-rest")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-rest` pops the top `" typename "` item and pushes a vector containing all but the first element to the `" typename "` stack (or an empty vector, if it's empty; NOTE difference from `first` and others).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(into [] (rest %1)) :as :end)
      `(d/push-onto ~typename :end)
      ))))



(defn x-reverse-instruction
  [typename]
  (let [instruction-name (str (name typename) "-reverse")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the reversed vector to the `" typename "` stack.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1]
          #(into [] (reverse %1)) :as :bw)
      `(d/push-onto ~typename :bw)
      ))))



(defn x-set-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-set")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-set` pops the top `" typename "` item, the top `:scalar` and the the top `" rootname "` item. It pushes a new `" typename "` which has the indexed item (modulo length) replaced with the new element. If the `" typename "` is empty, it is returned but the other arguments are consumed.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg)
      `(d/consume-top-of ~rootname :as :subst)
      `(d/consume-top-of :scalar :as :which)
      `(d/calculate [:arg :which]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
      `(d/calculate [:arg :idx :subst]
          #(if (empty? %1)
            %1
            (into [] (assoc %1 (long %2) %3))) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn x-shatter-instruction  ;; was -pushall
  [typename rootname]
  (let [instruction-name (str (name typename) "-shatter")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-shatter` pops the top `" typename "` item and pushes each of its items in turn to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-stack ~rootname :as :old-stack)
      `(d/calculate [:arg1 :old-stack]
          #(u/list! (concat %1 %2)) :as :new-stack)
      `(d/replace-stack ~rootname :new-stack)
      ))))


(defn x-sort-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-sort")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-sort` pops the top `" typename "` item and sorts the elements before returning it to the stack. NOTE: this depends on the intrinsic ability of `" rootname "` to be sorted by Clojure.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1] #(into [] (sort %1)) :as :sorted)
      `(d/push-onto ~typename :sorted)
      ))))



(defn x-order-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-order")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-order` pops the top `" typename "` item and produces a new `:scalar` item containing a contextual order over the items. For example, the `:scalars` vector `[9 3 -2.1 -2.1 9 0]` would produce the order vector `[3 2 0 0 3 1]`. NOTE: this depends on the intrinsic ability of `" rootname "` to be sorted by Clojure.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1] #(into [] (exotic/vector->order %1)) :as :order)
      `(d/push-onto :scalars :order)
      ))))



(defn x-resample-instruction
  [typename]
  (let [instruction-name (str (name typename) "-resample")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-resample` pops the top `" typename "` item and the top `:scalars` item. The `:scalars` vector is used as a sequence of indices to construct a new `" typename "` item, which is pushed.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of :scalars :as :arg2)
      `(d/calculate [:arg1 :arg2] exotic/resample-vector :as :resampled)
      `(d/push-onto ~typename :resampled)
      ))))


(defn x-permute-instruction
  [typename]
  (let [instruction-name (str (name typename) "-permute")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-permute` pops the top `" typename "` item and the top `:scalars` item. The `:scalars` vector is used as a sequence of indices to construct a new `" typename "` item by removing each indexed item in turn and appending it to a new vector, which is then pushed.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of :scalars :as :arg2)
      `(d/calculate [:arg1 :arg2] exotic/permute-with-scalars :as :resampled)
      `(d/push-onto ~typename :resampled)
      ))))

(defn x-min-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-min")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-min` pops the top `" typename "` item and places the minimum-valued item in that vector onto `" rootname "`. NOTE: this depends on the intrinsic ability of `" rootname "` to be sorted by Clojure.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1] #(first (sort %1)) :as :smallest)
      `(d/push-onto ~rootname :smallest)
      ))))


(defn x-max-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-max")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-max` pops the top `" typename "` item and places the largest-valued item in that vector onto `" rootname "`. NOTE: this depends on the intrinsic ability of `" rootname "` to be sorted by Clojure.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/calculate [:arg1] #(last (sort %1)) :as :biggest)
      `(d/push-onto ~rootname :biggest)
      ))))




(defn x-take-instruction
  [typename]
  (let [instruction-name (str (name typename) "-take")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-take` pops the top `" typename "` item and the the top `:scalar`. It converts the `:scalar` value into an index (modulo one more than the vector's length) then pushes a new `" typename "` item containing only the items in the original from the start up to the indexed point.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :arg1)
      `(d/consume-top-of :scalar :as :int)
      `(d/calculate [:arg1 :int]
          #(if (empty? %1) 0 (num/scalar-to-index %2 (inc (count %1)))) :as :idx)
      `(d/calculate [:arg1 :idx]
          #(into [] (take %2 %1)) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn filter-vector-with-vector
  [v f]
  (filterv #((set f) %) v))



(defn x-vfilter-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vfilter")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-vfilter` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a new `" typename "` that only contains items in `A` that are also in `B`.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :filter)
      `(d/consume-top-of ~typename :as :arg)
      `(d/calculate [:arg :filter]
          #(filter-vector-with-vector %1 %2) :as :result)
      `(d/push-onto ~typename :result)
      ))))


(defn remove-vector-with-vector
  [v f]
  (vec (remove #((set f) %1) v)))



(defn x-vremove-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vremove")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-vremove` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a new `" typename "` that only contains items in `A` that are NOT also in `B`.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :filter)
      `(d/consume-top-of ~typename :as :arg)
      `(d/calculate [:arg :filter]
          #(remove-vector-with-vector %1 %2) :as :result)
      `(d/push-onto ~typename :result)
      ))))



(defn x-vsplit-instruction
  [typename]
  (let [instruction-name (str (name typename) "-vsplit")]
    (eval (list
      `i/build-instruction
      instruction-name
      (str "`" typename "-vsplit` pops the top two `" typename "` items (call them `B` and `A`, respectively) pushes a code block to `:exec` containing two new `" typename "` items: The first only contains items in `A` that are also in `B`, and the second only contains items in `A` NOT in `B`.")
      :tags #{:vector}

      `(d/consume-top-of ~typename :as :filter)
      `(d/consume-top-of ~typename :as :arg)
      `(d/calculate [:arg :filter]
          #(list
            (filter-vector-with-vector %1 %2)
            (remove-vector-with-vector %1 %2)) :as :result)
      `(d/return-item :result)
      ))))




(defn build-vectorized-type
  "creates a vector [sub]type for another Push type"
  [content-type]
  (let [typename (keyword (inflect/plural (name (:name content-type))))
        rootname (keyword (name (:name content-type)))]
    ( ->  (t/make-type  typename
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
          (t/attach-instruction , (x-build-instruction typename rootname))
          (t/attach-instruction , (x-butlast-instruction typename))
          (t/attach-instruction , (x-byexample-instruction typename rootname))
          (t/attach-instruction , (x-concat-instruction typename))
          (t/attach-instruction , (x-conj-instruction typename rootname))
          (t/attach-instruction , (x-contains?-instruction typename rootname))
          (t/attach-instruction , (x-cyclevector-instruction typename rootname))
          (t/attach-instruction , (x-distinct-instruction typename))
          (t/attach-instruction , (x-do*each-instruction typename))
          (t/attach-instruction , (x-emptyitem?-instruction typename))
          (t/attach-instruction , (x-fillvector-instruction typename rootname))
          (t/attach-instruction , (x-first-instruction typename rootname))
          (t/attach-instruction , (x-generalize-instruction typename))
          (t/attach-instruction , (x-generalizeall-instruction typename))
          (t/attach-instruction , (x-indexof-instruction typename rootname))
          (t/attach-instruction , (x-items-instruction typename))
          (t/attach-instruction , (x-last-instruction typename rootname))
          (t/attach-instruction , (x-length-instruction typename))
          (t/attach-instruction , (x-new-instruction typename))
          (t/attach-instruction , (x-nth-instruction typename rootname))
          (t/attach-instruction , (x-occurrencesof-instruction typename rootname))
          (t/attach-instruction , (x-permute-instruction typename))
          (t/attach-instruction , (x-portion-instruction typename))
          (t/attach-instruction , (x-pt-crossover-instruction typename))
          (t/attach-instruction , (x-shatter-instruction typename rootname))
          (t/attach-instruction , (x-remove-instruction typename rootname))
          (t/attach-instruction , (x-replace-instruction typename rootname))
          (t/attach-instruction , (x-replacefirst-instruction typename rootname))
          (t/attach-instruction , (x-resample-instruction typename))
          (t/attach-instruction , (x-rest-instruction typename))
          (t/attach-instruction , (x-set-instruction typename rootname))
          (t/attach-instruction , (x-take-instruction typename))
          (t/attach-instruction , (x-reverse-instruction typename))
          (t/attach-instruction , (x-vfilter-instruction typename))
          (t/attach-instruction , (x-vremove-instruction typename))
          (t/attach-instruction , (x-vsplit-instruction typename))

          (t/conditional-attach-instruction ,
            (some #(= % :comparable) (:attributes content-type))
            (x-sort-instruction typename rootname))

          (t/conditional-attach-instruction ,
            (some #(= % :comparable) (:attributes content-type))
            (x-order-instruction typename rootname))

          (t/conditional-attach-instruction ,
            (some #(= % :comparable) (:attributes content-type))
            (x-min-instruction typename rootname))

          (t/conditional-attach-instruction ,
            (some #(= % :comparable) (:attributes content-type))
            (x-max-instruction typename rootname))

            )))
