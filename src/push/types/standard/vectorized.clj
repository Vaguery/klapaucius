(ns push.types.standard.vectorized
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl])
  (:require [push.instructions.modules.print :as print])
  (:require [push.instructions.modules.environment :as env])
  (:require [push.util.code-wrangling :as fix])
  )


;; helper


(defn replacefirst
  "Takes a vector, and replaces the first occurrence of the target (if it appears) with the substitute. Returns a vector."
  [coll target substitute]
  (let [is-here (boolean (some #{target} coll))
        [front back] (split-with (complement #{target}) coll)
        new-tail (if is-here (conj (rest back) substitute) back)]
    (into [] (concat front new-tail))))


;; instructions


(defn x-do*each-instruction
  ;; NOTE as a side-effect of the continuation form, there will be an empty vector at the end of the iteration; this will be sent to the :vector stack or the :unknown stack by the router, depending on what types are registered
  [typename]
  (let [instruction-name (str (name typename) "-do*each")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-do*each` pops the top `" typename "` item and the top of the `:exec` stack, and pushes this continuation form to `:exec`: `([first vector] [exec item] [rest vector] " instruction-name " [exec item])`.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :exec :as :actor)
      `(push.instructions.dsl/calculate [:arg :actor]
          #(list (or (first %1) '())
                 %2
                 (into [] (rest %1))
                 (keyword ~instruction-name)
                 %2)
          :as :continuation)
      `(push.instructions.dsl/push-onto :exec :continuation)))))


(defn x-butlast-instruction
  [typename]
  (let [instruction-name (str (name typename) "-butlast")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-butlast` pops the top `" typename "` item and pushes the same vector lacking its last item (or nothing, if it ends up empty).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(into [] (butlast %1)) :as :most)
      `(push.instructions.dsl/push-onto ~typename :most)))))


(defn x-concat-instruction
  [typename]
  (let [instruction-name (str (name typename) "-concat")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-concat` pops the top two `" typename "` items and pushes the the concatenation of the top one onto the second one.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2]
          #(into [] (concat %1 %2)) :as :concatted)
      `(push.instructions.dsl/push-onto ~typename :concatted)))))


(defn x-conj-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-conj")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-conj` pops the top `" typename "` item and the top `" rootname "` item, and appends the latter to the former, pushing the result.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~rootname :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2]
          #(conj %1 %2) :as :conjed)
      `(push.instructions.dsl/push-onto ~typename :conjed)))))


(defn x-contains?-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-contains?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-contains?` pops the top `" typename "` item and the top `" rootname "` item, and pushes `true` to the `:boolean` stack if the latter is present in the former.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~rootname :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2]
          #(boolean (some (fn [i] (= i %2)) %1)) :as :found)
      `(push.instructions.dsl/push-onto :boolean :found)))))



(defn x-emptyitem?-instruction
  [typename]
  (let [instruction-name (str (name typename) "-emptyitem?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-emptyitem?` pops the top `" typename "` item and pushes `true` to the `:boolean` stack if it's empty.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(boolean (empty? %1)) :as :empty)
      '(push.instructions.dsl/push-onto :boolean :empty)))))


(defn x-first-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-first")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the first element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(first %1) :as :top)
      `(push.instructions.dsl/push-onto ~rootname :top)))))


(defn x-indexof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-indexof")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-indexof` pops the top `" typename "` item and the top `" rootname "` item, and pushes an `:integer` indicating the first appearance of the latter in the former (or -1 if it's not found).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~rootname :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2]
          #(.indexOf %1 %2) :as :where)
      `(push.instructions.dsl/push-onto :integer :where)))))


(defn x-last-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-last")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-last` pops the top `" typename "` item and pushes the last element to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(last %1) :as :tail)
      `(push.instructions.dsl/push-onto ~rootname :tail)))))


(defn x-length-instruction
  [typename]
  (let [instruction-name (str (name typename) "-length")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-length` pops the top `" typename "` item and pushes its count to the `:integer` stack.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(count %1) :as :len)
      `(push.instructions.dsl/push-onto :integer :len)))))


(defn x-new-instruction
  [typename]
  (let [instruction-name (str (name typename) "-new")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-new` pushes an empty vector to the stack.")
      :tags #{:vector}
      `(push.instructions.dsl/calculate [] (fn [] (vector)) :as :nuttin)
      `(push.instructions.dsl/push-onto ~typename :nuttin)))))


(defn x-nth-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-nth")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-nth` pops the top `" typename "` item and the the top `:integer`. It converts the `:integer` value into an index (modulo the vector's length) then pushes the indexed element to the `" rootname "` stack.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/consume-top-of :integer :as :int)
      `(push.instructions.dsl/calculate 
        [:arg1 :int] #(fix/safe-mod %2 (count %1)) :as :idx)
      `(push.instructions.dsl/calculate 
        [:arg1 :idx] #(if (empty? %1) nil (nth %1 %2)) :as :result)
      `(push.instructions.dsl/push-onto ~rootname :result)))))



(defn x-occurrencesof-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-occurrencesof")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-occurrencesof` pops the top `" typename "` item and the the top `" rootname "`. It pushes an `:integer` indicating how many copies of the latter appear in the former.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :vec)
      `(push.instructions.dsl/consume-top-of ~rootname :as :item)
      `(push.instructions.dsl/calculate 
        [:vec :item] #(get (frequencies %1) %2 0) :as :result)
      `(push.instructions.dsl/push-onto :integer :result)))))



(defn x-portion-instruction
  [typename]
  (let [instruction-name (str (name typename) "-portion")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-portion` pops the top `" typename "` item and the the top two `:integer` values (call these `A` and `B`). It pushes a new `" typename "` which only includes items between `A` and `B` (which are coerced to fall in range by truncation; see code).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      '(push.instructions.dsl/consume-top-of :integer :as :a)
      '(push.instructions.dsl/consume-top-of :integer :as :b)
      `(push.instructions.dsl/calculate [:arg :a]
          #(min (count %1) (max 0 %2)) :as :cropped-a)
      `(push.instructions.dsl/calculate [:arg :b]
          #(min (count %1) (max 0 %2)) :as :cropped-b)
      `(push.instructions.dsl/calculate [:arg :cropped-a :cropped-b]
          #(into [] (drop (min %2 %3) (take (max %2 %3) %1))) :as :subvector)
      `(push.instructions.dsl/push-onto ~typename :subvector)))))



(defn x-remove-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-remove")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-remove` pops the top `" typename "` item and the the top `" rootname "`. It pushes a new `" typename "` which has all occurrences of the `" rootname "` eliminated.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~rootname :as :purge)
      `(push.instructions.dsl/calculate 
        [:arg1 :purge] #(into [] (remove #{%2} %1)) :as :less)
      `(push.instructions.dsl/push-onto ~typename :less)))))



(defn x-replace-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replace")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-replace` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has all occurrences of `B` replaced with `A`.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~rootname :as :a)
      `(push.instructions.dsl/consume-top-of ~rootname :as :b)
      `(push.instructions.dsl/calculate 
        [:arg1 :a :b] #(into [] (replace {%3 %2} %1)) :as :result)
      `(push.instructions.dsl/push-onto ~typename :result)))))



(defn x-replacefirst-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-replacefirst")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-replacefirst` pops the top `" typename "` item and the the top two `" rootname "` items (call these `A` and `B` respectively). It pushes a new `" typename "` which has the first occurrence of `B` replaced with `A`.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~rootname :as :a)
      `(push.instructions.dsl/consume-top-of ~rootname :as :b)
      `(push.instructions.dsl/calculate 
        [:arg1 :a :b] #(replacefirst %1 %3 %2) :as :result)
      `(push.instructions.dsl/push-onto ~typename :result)))))



(defn x-rest-instruction
  [typename]
  (let [instruction-name (str (name typename) "-rest")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-rest` pops the top `" typename "` item and pushes a vector containing all but the first element to the `" typename "` stack (or an empty vector, if it's empty; NOTE difference from `first` and others).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(into [] (rest %1)) :as :end)
      `(push.instructions.dsl/push-onto ~typename :end)))))


(defn x-reverse-instruction
  [typename]
  (let [instruction-name (str (name typename) "-reverse")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-first` pops the top `" typename "` item and pushes the reversed vector to the `" typename "` stack.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(into [] (reverse %1)) :as :bw)
      `(push.instructions.dsl/push-onto ~typename :bw)))))


(defn x-set-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-set")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-set` pops the top `" typename "` item, the top `:integer` and the the top `" rootname "` item. It pushes a new `" typename "` which has the indexed item (modulo length) replaced with the new element. If the `" typename "` is empty, it is returned but the other arguments are consumed.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of ~rootname :as :subst)
      `(push.instructions.dsl/consume-top-of :integer :as :which)
      `(push.instructions.dsl/calculate 
        [:arg :which] #(fix/safe-mod %2 (count %1)) :as :idx)
      `(push.instructions.dsl/calculate 
        [:arg :idx :subst] #(if (empty? %1) %1 (into [] (assoc %1 %2 %3))) :as :result)
      `(push.instructions.dsl/push-onto ~typename :result)))))


(defn x-shatter-instruction  ;; was -pushall
  [typename rootname]
  (let [instruction-name (str (name typename) "-shatter")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-shatter` pops the top `" typename "` item and pushes each of its items in turn to the `" rootname "` stack (or nothing, if it's empty).")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-stack ~rootname :as :old-stack)
      `(push.instructions.dsl/calculate [:arg1 :old-stack]
          #(into '() (reverse (concat %1 %2))) :as :new-stack)
      `(push.instructions.dsl/replace-stack ~rootname :new-stack)))))



(defn x-take-instruction
  [typename]
  (let [instruction-name (str (name typename) "-take")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`" typename "-take` pops the top `" typename "` item and the the top `:integer`. It converts the `:integer` value into an index (modulo one more than the vector's length) then pushes a new `" typename "` item containing only the items in the original from the start up to the indexed point.")
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of :integer :as :int)
      `(push.instructions.dsl/calculate 
        [:arg1 :int] #(fix/safe-mod %2 (inc (count %1))) :as :idx)
      '(push.instructions.dsl/calculate 
        [:arg1 :idx] #(into [] (take %2 %1)) :as :result)
      `(push.instructions.dsl/push-onto ~typename :result)))))


(defn vector-of-type?
  [item type]
  (let [checker (:recognizer type)]
    (and  (vector? item)
          (every? #(checker %) item))))


(defn build-vectorized-type
  "creates a vector [sub]type for another Push type"
  [content-type]
  (let [typename (keyword (str (name (:name content-type)) "s"))
        rootname (keyword (name (:name content-type)))]
    ( ->  (t/make-type  typename
                        :recognizer #(vector-of-type? % content-type)
                        :attributes #{:vector})
          t/make-visible
          t/make-equatable
          t/make-movable
          print/make-printable
          env/make-returnable
          (t/attach-instruction , (x-butlast-instruction typename))
          (t/attach-instruction , (x-concat-instruction typename))
          (t/attach-instruction , (x-conj-instruction typename rootname))
          (t/attach-instruction , (x-contains?-instruction typename rootname))
          (t/attach-instruction , (x-do*each-instruction typename))
          (t/attach-instruction , (x-emptyitem?-instruction typename))
          (t/attach-instruction , (x-first-instruction typename rootname))
          (t/attach-instruction , (x-indexof-instruction typename rootname))
          (t/attach-instruction , (x-last-instruction typename rootname))
          (t/attach-instruction , (x-length-instruction typename))
          (t/attach-instruction , (x-new-instruction typename))
          (t/attach-instruction , (x-nth-instruction typename rootname))
          (t/attach-instruction , (x-occurrencesof-instruction typename rootname))
          (t/attach-instruction , (x-portion-instruction typename))
          (t/attach-instruction , (x-shatter-instruction typename rootname))
          (t/attach-instruction , (x-remove-instruction typename rootname))
          (t/attach-instruction , (x-replace-instruction typename rootname))
          (t/attach-instruction , (x-replacefirst-instruction typename rootname))
          (t/attach-instruction , (x-rest-instruction typename))
          (t/attach-instruction , (x-set-instruction typename rootname))
          (t/attach-instruction , (x-take-instruction typename))
          (t/attach-instruction , (x-reverse-instruction typename))
          )))

