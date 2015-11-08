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
; float_sin
; float_fromboolean
; float_frominteger
; float_fromstring
; float_fromchar


(def float-add
  (core/build-instruction
    float-add
    :tags #{:arithmetic :base}
    (d/consume-top-of :float :as :arg1)
    (d/consume-top-of :float :as :arg2)
    (d/calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (d/push-onto :float :sum)))


(def float-inc
  (core/build-instruction
    float-inc
    :tags #{:arithmetic :base}
    (d/consume-top-of :float :as :arg1)
    (d/calculate [:arg1] #(inc' %1) :as :more)
    (d/push-onto :float :more)))


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
        ))

