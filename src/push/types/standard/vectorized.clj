(ns push.types.standard.vectorized
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl])
  (:require [push.instructions.modules.print :as print])
  (:require [push.instructions.modules.environment :as env])
  )


;; TODO

; vector_X_conj
; vector_X_take
; vector_X_subvec
; vector_X_first
; vector_X_last
; vector_X_nth
; vector_X_rest
; vector_X_butlast
; vector_X_length
; vector_X_reverse
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


(defn vector-of-type?
  [item type]
  (let [checker (:recognizer type)]
    (and  (vector? item)
          (every? #(checker %) item))))


(defn build-vectorized-type
  "creates a vector [sub]type for another Push type"
  [content-type]
  (let [typename (keyword (str (name (:name content-type)) "s"))]
    ( ->  (t/make-type  typename
                        :recognizer #(vector-of-type? % content-type)
                        :attributes #{:vector})
          t/make-visible
          t/make-equatable
          t/make-movable
          print/make-printable
          env/make-returnable
          (t/attach-instruction , (x-concat-instruction typename)))))

