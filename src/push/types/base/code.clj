(ns push.types.base.code
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; code-specific

; code_discrepancy
; code_overlap
; code_noop
; code_append
; code_atom
; code_car
; code_cdr
; code_cons
; code_do
; code_do*
; code_do*range
; code_do*count
; code_do*times
; code_map
; code_fromboolean
; code_fromfloat
; code_frominteger
; code_quote
; code_if
; code_length
; code_list
; code_wrap
; code_member
; code_nth
; code_nthcdr
; code_null
; code_size
; code_extract
; code_insert
; code_subst
; code_contains
; code_container
; code_position
; code_fromzipnode
; code_fromziproot
; code_fromzipchildren
; code_fromziplefts
; code_fromziprights


(def classic-code-type
  ( ->  (t/make-type  :code
                      :recognizer #(and (list? %) (= (first %) 'quote))
                      :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        ))

