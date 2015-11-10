(ns push.types.base.float
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; float-specific


; float_sub
; float_mult
; float_div
; float_mod
; float_dec
; float_cos
; float_tan
; float_fromboolean
; float_frominteger
; float_fromstring
; float_fromchar


(def float-add (t/simple-2-in-1-out-instruction :float "add" '+'))


(def float-inc (t/simple-1-in-1-out-instruction :float "inc" 'inc'))


(def float-sign (t/simple-1-in-1-out-instruction :float "sign" #(float (compare %1 0.0))))


(def float-sine (t/simple-1-in-1-out-instruction :float "sine" #(Math/sin %1)))


(def classic-float-type
  ( ->  (t/make-type  :float
                      :recognizer float?
                      :attributes #{:numeric :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , float-add)
        (t/attach-instruction , float-inc)
        (t/attach-instruction , float-sign)
        (t/attach-instruction , float-sine)
        ))

