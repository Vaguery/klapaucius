(ns push.types.base.code
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

(defn push-code? [item] (and (list? item) (= (first item) 'quote)))


;; code-specific

; code_append
; code_atom
; code_car
; code_cdr
; code_cons
; code_container
; code_contains
; code_discrepancy
; code_do
; code_do*
; code_do*count
; code_do*range
; code_do*times
; code_extract
; code_fromboolean
; code_fromfloat
; code_frominteger
; code_fromzipchildren
; code_fromziplefts
; code_fromzipnode
; code_fromziprights
; code_fromziproot
; code_if
; code_insert
; code_length
; code_list
; code_map
; code_member
; code_nth
; code_nthcdr
; code_null
; code_overlap
; code_position
; code_size
; code_subst
; code_wrap


(def code-quote
  (core/build-instruction
    code-quote
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/push-onto :code :arg1)))



(def code-noop
  (core/build-instruction
    code-noop
    :tags #{:complex :base}))


(def classic-code-type
  ( ->  (t/make-type  :code
                      :recognizer push-code?
                      :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-quote)
        ))

