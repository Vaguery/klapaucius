(ns push.design-spikes.promiscuous-types
  (:use midje.sweet)
  (:use structural-typing.type)
  )

; (defrecord PushInteger [real])
; (defrecord PushRatio [whole nominator denominator])
; (defrecord PushFloat [real])
; (defrecord PushComplex [real imaginary])

; (defn fromInteger
;   [i]
;   { :ratio   (->PushRatio i 0 0)
;     :float   (->PushFloat i)
;     :complex (->PushComplex i 0)})
