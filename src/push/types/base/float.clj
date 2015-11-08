(ns push.types.base.float
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; float-specific


; float_add
; float_sub
; float_mult
; float_div
; float_mod
; float_lt
; float_lte
; float_gt
; float_gte
; float_fromboolean
; float_frominteger
; float_fromstring
; float_fromchar
; float_min
; float_max
; float_cos
; float_tan
; float_inc
; float_dec



(def classic-float-type
  ( ->  (t/make-type  :float
                      :recognizer float?
                      :attributes #{:numeric :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        ))

