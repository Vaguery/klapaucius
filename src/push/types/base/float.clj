(ns push.types.base.float
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


; float_fromboolean
; float_frominteger
; float_fromstring
; float_fromchar


(def float-add (t/simple-2-in-1-out-instruction :float "add" '+'))


(def float-cosine (t/simple-1-in-1-out-instruction :float "cosine" #(Math/cos %1)))


(def float-dec (t/simple-1-in-1-out-instruction :float "dec" 'dec'))


(def float-divide
  (core/build-instruction
    float-divide
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :denominator)
    (d/consume-top-of :float :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (/ %2 %1)) :as :quotient)
    (d/push-these-onto :float [:replacement :quotient])))


(def float-inc (t/simple-1-in-1-out-instruction :float "inc" 'inc'))


(def float-mod
  (core/build-instruction
    float-mod
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :denominator)
    (d/consume-top-of :float :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (mod %2 %1)) :as :quotient)
    (d/push-these-onto :float [:replacement :quotient])))


(def float-subtract (t/simple-2-in-1-out-instruction :float "subtract" '-'))




(def float-multiply (t/simple-2-in-1-out-instruction :float "multiply" '*'))


(def float-sign (t/simple-1-in-1-out-instruction :float "sign" #(float (compare %1 0.0))))


(def float-sine (t/simple-1-in-1-out-instruction :float "sine" #(Math/sin %1)))


(def float-tangent (t/simple-1-in-1-out-instruction :float "tangent" #(Math/tan %1)))


(def classic-float-type
  ( ->  (t/make-type  :float
                      :recognizer float?
                      :attributes #{:numeric :base})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , float-add)
        (t/attach-instruction , float-cosine)
        (t/attach-instruction , float-dec)
        (t/attach-instruction , float-divide)
        (t/attach-instruction , float-inc)
        (t/attach-instruction , float-mod)
        (t/attach-instruction , float-multiply)
        (t/attach-instruction , float-sine)
        (t/attach-instruction , float-sign)
        (t/attach-instruction , float-subtract)
        (t/attach-instruction , float-tangent)
        ))

