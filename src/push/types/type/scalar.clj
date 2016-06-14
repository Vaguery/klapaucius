(ns push.types.type.scalar
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math]
            [push.util.exotics :as x]
            ))



;; INSTRUCTIONS

(def scalar-abs (t/simple-1-in-1-out-instruction
  "`:scalar-abs` pushes the abs of the top `:scalar` item"
  :scalar "abs" 'math/abs))



(def scalar-add (t/simple-2-in-1-out-instruction
  "`:scalar-add` pops the top two `:scalar` values, and pushes their sum"
  :scalar "add" '+'))



(def scalar-ceiling (t/simple-1-in-1-out-instruction
  "`:scalar-ceiling` pops the top `:scalar` value, and pushes the next-largest integer value"
  :scalar "ceiling" 'math/ceil))



(def scalar-cosine (t/simple-1-in-1-out-instruction
  "`:scalar-cosine` pushes the cosine of the top `:scalar` item, read as radians"
  :scalar "cosine" #(Math/cos %)))



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



(def scalar-sine (t/simple-1-in-1-out-instruction
  "`:scalar-sine` pushes the sine of the top `:scalar` item, read as an angle in radians"
  :scalar "sine" #(Math/sin %1)))



(def scalar-subtract (t/simple-2-in-1-out-instruction
  "`:scalar-subtract` pops the top two `:scalar` items, and pushes the difference of the top item subtracted from the second"
  :scalar "subtract" '-'))



(def integer-totalistic3
  (core/build-instruction
    integer-totalistic3
    "`:integer-totalistic3` pops the top `:scalar`. It is turned into an integer using `(bigint x)`. Then each digit is replaced by the sum of its current value and the two neighbors to the right, modulo 10, wrapping cyclically around the number."
    :tags #{:numeric :exotic}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(x/rewrite-digits (bigint %1) 3) :as :result)
    (d/push-onto :scalar :result)))



; ;; CONVERSIONS



(def boolean->float
  (core/build-instruction
    boolean->float
    "`:boolean->float` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `0.0`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 0.0) :as :result)
    (d/push-onto :scalar :result)))



(def boolean->signedfloat
  (core/build-instruction
    boolean->signedfloat
    "`:boolean->signedfloat` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `-1.0`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 -1.0) :as :result)
    (d/push-onto :scalar :result)))



(def boolean->integer
  (core/build-instruction
    boolean->integer
    "`:boolean->integer` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes 0."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :scalar :logic)))



(def boolean->signedint
  (core/build-instruction
    boolean->signedint
    "`:boolean->signedint` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes -1."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (d/push-onto :scalar :logic)))




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
        (t/attach-instruction , integer-totalistic3)
        (t/attach-instruction , scalar-abs)
        (t/attach-instruction , scalar-add)
        (t/attach-instruction , scalar-ceiling)
        (t/attach-instruction , scalar-cosine)
        (t/attach-instruction , scalar-dec)
        (t/attach-instruction , scalar-divide)
        (t/attach-instruction , scalar-E)
        (t/attach-instruction , scalar-inc)
        (t/attach-instruction , scalar-modulo)
        (t/attach-instruction , scalar-multiply)
        (t/attach-instruction , scalar-π)
        (t/attach-instruction , scalar-sign)
        (t/attach-instruction , scalar-sine)
        (t/attach-instruction , scalar-subtract)
        (t/attach-instruction , boolean->float)
        (t/attach-instruction , boolean->signedfloat)
        (t/attach-instruction , boolean->integer)
        (t/attach-instruction , boolean->signedint)
        ))

