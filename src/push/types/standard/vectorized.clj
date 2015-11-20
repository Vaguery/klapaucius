(ns push.types.standard.vectorized
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl])
  (:require [push.instructions.modules.print :as print])
  (:require [push.instructions.modules.environment :as env])
  )


;; TODO

; vector_X_take
; vector_X_subvec
; vector_X_nth
; vector_X_pushall
; vector_X_emptyvector
; vector_X_contains
; vector_X_indexof
; vector_X_occurrencesof
; vector_X_set
; vector_X_replace
; vector_X_replacefirst
; vector_X_remove
; exec_do*vector_X


(defn x-butlast-instruction
  [typename]
  (let [instruction-name (str (name typename) "-butlast")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      "DOCSTRING"
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(butlast %1) :as :most)
      `(push.instructions.dsl/push-onto ~typename :most)))))


(defn x-concat-instruction
  [typename]
  (let [instruction-name (str (name typename) "-concat")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      "DOCSTRING"
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
      "DOCSTRING"
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~rootname :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2]
          #(conj %1 %2) :as :conjed)
      `(push.instructions.dsl/push-onto ~typename :conjed)))))


(defn x-first-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-first")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      "DOCSTRING"
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(first %1) :as :top)
      `(push.instructions.dsl/push-onto ~rootname :top)))))


(defn x-last-instruction
  [typename rootname]
  (let [instruction-name (str (name typename) "-last")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      "DOCSTRING"
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
      "DOCSTRING"
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(count %1) :as :len)
      `(push.instructions.dsl/push-onto :integer :len)))))


(defn x-rest-instruction
  [typename]
  (let [instruction-name (str (name typename) "-rest")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      "DOCSTRING"
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
      "DOCSTRING"
      :tags #{:vector}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1] #(reverse %1) :as :bw)
      `(push.instructions.dsl/push-onto ~typename :bw)))))


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
          (t/attach-instruction , (x-first-instruction typename rootname))
          (t/attach-instruction , (x-last-instruction typename rootname))
          (t/attach-instruction , (x-length-instruction typename))
          (t/attach-instruction , (x-rest-instruction typename))
          (t/attach-instruction , (x-reverse-instruction typename))
          )))

