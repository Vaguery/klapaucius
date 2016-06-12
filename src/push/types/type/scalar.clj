(ns push.types.type.scalar
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math]
            ))



;; INSTRUCTIONS

(def scalar-abs (t/simple-1-in-1-out-instruction
  "`:scalar-abs` pushes the abs of the top `:scalar` item"
  :scalar "abs" 'math/abs))



(def scalar-add (t/simple-2-in-1-out-instruction
  "`:scalar-add` pops the top two `:scalar` values, and pushes their sum"
  :scalar "add" '+'))



(def scalar-dec (t/simple-1-in-1-out-instruction
  ":`scalar-dec` reduces the top `:scalar` value by 1"
  :scalar "dec" 'dec'))



(def scalar-divide
  (core/build-instruction
    scalar-divide
    "`:scalar-divide` pops the top two `:scalar` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0, it consumes the arguments but pushes an `:error`; if not, it pushes their quotient."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :scalar :as :denominator)
    (d/consume-top-of :scalar :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) nil (/ %2 %1)) :as :quotient)
    (d/push-onto :scalar :quotient)
    (d/calculate [:denominator]
      #(if (zero? %1) ":scalar-divide 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def scalar-E
  (core/build-instruction
    scalar-E
    "`:scalar-E` pushes the value E to the :scalar stack."
    :tags #{:arithmetic :base}
    (d/calculate [] #(Math/E) :as :e)
    (d/push-onto :scalar :e)))



(def scalar-inc (t/simple-1-in-1-out-instruction
  "`:scalar-inc` adds 1 to the top `:scalar` item"
  :scalar "inc" 'inc'))



(def scalar-modulo
  (core/build-instruction
    scalar-modulo
    "`:scalar-modulo` pops the top two `:scalar` values (call them `denominator` and `numerator`, respectively). If `denominator` is zero, it discards the arguments and produces an `:error`. Otherwise, it pushes `(mod numerator denominator)`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :scalar :as :denominator)
    (d/consume-top-of :scalar :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) nil (mod %2 %1)) :as :quotient)
    (d/push-onto :scalar :quotient)
    (d/calculate [:denominator]
      #(if (zero? %1) ":scalar-modulo 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def scalar-multiply (t/simple-2-in-1-out-instruction
  "`:scalar-multiply` pops the top two `:scalar` items, and pushes the product"
  :scalar "multiply" '*'))


(def scalar-π
  (core/build-instruction
    scalar-π
    "`:scalar-π` pushes the value π to the :scalar stack."
    :tags #{:arithmetic :base}
    (d/calculate [] #(Math/PI) :as :pi)
    (d/push-onto :scalar :pi)))



(def scalar-sign (t/simple-1-in-1-out-instruction
  "`:scalar-sign` pops the top `:scalar` item and pushes -1 if it's negative, 0 if it's zero, and 1 if it's positive"
  :scalar "sign" #(compare %1 0)))



(def scalar-subtract (t/simple-2-in-1-out-instruction
  "`:scalar-subtract` pops the top two `:scalar` items, and pushes the difference of the top item subtracted from the second"
  :scalar "subtract" '-'))





; ;; CONVERSIONS



(def scalar-type
  ( ->  (t/make-type  :scalar
                      :recognized-by number?
                      :attributes #{:numeric})
        aspects/make-comparable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
        (t/attach-instruction , scalar-abs)
        (t/attach-instruction , scalar-add)
        (t/attach-instruction , scalar-dec)
        (t/attach-instruction , scalar-divide)
        (t/attach-instruction , scalar-E)
        (t/attach-instruction , scalar-inc)
        (t/attach-instruction , scalar-modulo)
        (t/attach-instruction , scalar-multiply)
        (t/attach-instruction , scalar-π)
        (t/attach-instruction , scalar-sign)
        (t/attach-instruction , scalar-subtract)
        ))

