(ns push.types.base.float
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math])
  (:use push.types.extra.generator)
  )



(def float-abs (t/simple-1-in-1-out-instruction
  "`:float-abs` pushes the abs of the top `:float` item"
  :float "abs" 'math/abs))



(def float-add (t/simple-2-in-1-out-instruction
  "`:float-add` pops the top two `:float` values, and pushes their sum"
  :float "add" '+'))



(def float-cosine (t/simple-1-in-1-out-instruction
  "`:float-cosine` pushes the cosine of the top `:float` item, read as radians"
  :float "cosine" #(Math/cos %1)))



(def float-arccosine
  (core/build-instruction
    float-arccosine
    "`:float-arccosine` pops the top `:float` value, and if it is between -1.0 and 1.0 it returns the arccos(theta), between 0.0 and π; otherwise it replaces the argument and adds an :error"
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(or (> %1 1.0) (< %1 -1.0)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/acos %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-arccosine bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))



(def float-arcsine
  (core/build-instruction
    float-arcsine
    "`:float-arcsine` pops the top `:float` value, and if it is between -1.0 and 1.0 it returns the arcsin(theta), between 0.0 and π; otherwise it replaces the argument and adds an :error"
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(or (> %1 1.0) (< %1 -1.0)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/asin %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-arcsine bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))



(def float-arctangent (t/simple-1-in-1-out-instruction
  ":`float-arctangent` pops the top `:float` and pushes atan(theta) (assuming the angle lies between ±π/2) to `:float`"
  :float "arctangent" #(Math/atan %)))



(def float-dec (t/simple-1-in-1-out-instruction
  ":`float-dec` reduces the top `:float` value by 1.0"
  :float "dec" 'dec'))



(def float-divide
  (core/build-instruction
    float-divide
    "`:float-divide` pops the top two `:float` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0.0, it replaces the two `:float` values; if not, it pushes their quotient."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :denominator)
    (d/consume-top-of :float :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (/ %2 %1)) :as :quotient)
    (d/push-these-onto :float [:replacement :quotient])
    (d/calculate [:denominator]
      #(if (zero? %1) ":float-divide 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def boolean->float
  (core/build-instruction
    boolean->float
    "`:boolean->float` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `0.0`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 0.0) :as :result)
    (d/push-onto :float :result)))



(def char->float
  (core/build-instruction
    char->float
    "`:char->float` pops the top `:char`, converts this to an integer, and pushes that value typecast to a `:float`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :char :as :arg)
    (d/calculate [:arg] #(double (long %1)) :as :result)
    (d/push-onto :float :result)))



(def integer->float
  (core/build-instruction
    integer->float
    "`:integer->float` pops the top `:integer`, and typecasts it to a (double) `:float` value"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(double %1) :as :result)
    (d/push-onto :float :result)))



(def string->float
  (core/build-instruction
    string->float
    "`:string->float` pops the top `:string` item, and applies `Double/parseDouble` to attempt to convert it to a floating-point value. If successful (that is, if no exception is raised), the result is pushed to `:float`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] 
      #(try (Double/parseDouble %1) (catch NumberFormatException _ nil))
        :as :result)
    (d/push-onto :float :result)))



(def boolean->signedfloat
  (core/build-instruction
    boolean->signedfloat
    "`:boolean->signedfloat` pops the top `:boolean` value; if it is `true`, it pushes 1.0, and if `false` it pushes `-1.0`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 -1.0) :as :result)
    (d/push-onto :float :result)))



(def float-E
  (core/build-instruction
    float-E
    "`:float-E` pushes the value e (the base of the natural logarithms)."
    :tags #{:arithmetic :base}
    (d/calculate [] #(Math/E) :as :e)
    (d/push-onto :float :e)))



(def float-π
  (core/build-instruction
    float-π
    "`:float-π` pushes the value e (the base of the natural logarithms)."
    :tags #{:arithmetic :base}
    (d/calculate [] #(Math/PI) :as :pi)
    (d/push-onto :float :pi)))



(def float-inc (t/simple-1-in-1-out-instruction
  "`:float-inc` adds 1.0 to the top `:float` item"
  :float "inc" 'inc'))



(def float-ln
  (core/build-instruction
    float-ln
    "`:float-ln` pops the top `:float` value. If it is a positive (non-zero) value, its natural logarithm is pushed; otherwise, it replaces the argument and an error is pushed to the :error stack."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #( (complement pos?) %1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/log %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-ln bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))



(def float-ln1p
  (core/build-instruction
    float-ln1p
    "`:float-ln1p` pops the top `:float` value. If it is a value greater than -1.0, `(Math/log1p x)` is pushed; otherwise, it replaces the argument and an error is pushed to the :error stack."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(<= %1 -1.0) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/log1p %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-ln1p bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))


(def float-log10
  (core/build-instruction
    float-log10
    "`:float-log10` pops the top `:float` value. If it is a positive (non-zero) value, its base-10 logarithm is pushed; otherwise, it replaces the argument and an error is pushed to the :error stack."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #( (complement pos?) %1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/log10 %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-log10 bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))



(def float-mod
  (core/build-instruction
    float-mod
    "`:float-mod` pops the top two `:float` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0.0, it replaces the two `:float` values; if not, it pushes `(mod numerator denominator)`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :denominator)
    (d/consume-top-of :float :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (mod %2 %1)) :as :quotient)
    (d/push-these-onto :float [:replacement :quotient])
    (d/calculate [:denominator]
      #(if (zero? %1) ":float-mod 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def float-power
  (core/build-instruction
    float-power
    "`:float-power` pops the top two `:float` values (call them `base` and `exponent` respectively). It calculates `(Math/pow base exponent)`. If the result is a `Double`, it is pushed to `:float`; otherwise, the arguments are pushed back where they were, and an `:error` is pushed."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :base)
    (d/consume-top-of :float :as :exp)
    (d/calculate [:base :exp] #(Math/pow %1 %2) :as :prelim)
    (d/calculate [:prelim] #(or (Double/isNaN %1) (Double/isInfinite %1)) :as :bad-arg?)
    (d/calculate [:bad-arg? :prelim :base] #(if %1 %3 %2) :as :result1)
    (d/calculate [:bad-arg? :prelim :exp] #(if %1 %3 nil) :as :result2)
    (d/calculate [:bad-arg? :prelim :base :exp]
      #(if %1 (str "(Double/pow " %3 " " %4 ") is " %2) nil) :as :warning)
    (d/push-these-onto :float [:result2 :result1])
    (d/record-an-error :from :warning)
  ))



(def float-sqrt
  (core/build-instruction
    float-sqrt
    "`:float-sqrt` pops the top `:float` value. If it's not negative, its square root is pushed; otherwise, the argument is replaced on `:float` and an error is pushed to the `:error` stack."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(neg? %1) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/sqrt %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-sqrt bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))



(def float-subtract (t/simple-2-in-1-out-instruction
  "`:float-subtract` pops the top two `:float` items, and pushes the difference of the top item subtracted from the second"
  :float "subtract" '-'))



(def float-multiply (t/simple-2-in-1-out-instruction
  "`:float-multiply` pops the top two `:float` items, and pushes the product"
  :float "multiply" '*'))



(def float-sign (t/simple-1-in-1-out-instruction
  "`:float-sign` pops the top `:float` item and pushes -1.0 if it's negative, 0.0 if it's zero, and 1.0 if it's positive"
  :float "sign" #(float (compare %1 0.0))))



(def float-sine (t/simple-1-in-1-out-instruction
  "`:float-sine` pushes the sine of the top `:float` item, read as an angle in radians"
  :float "sine" #(Math/sin %1)))



(def float-tangent
  (core/build-instruction
    float-tangent
    "`:float-tangent` pops the top `:float` value and calculates tan(theta). If the result is a non-infinite number, it pushes that to :float; otherwise, it returns the argument to :float and pushes an :error"
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(Double/isNaN (Math/tan %1)) :as :bad-arg?)
    (d/calculate [:bad-arg? :arg] #(if %1 %2 (Math/tan %2)) :as :result)
    (d/calculate [:bad-arg? :arg]
      #(if %1 (str ":float-tangent bad arg: " %2) nil) :as :warning)
    (d/push-onto :float :result)
    (d/record-an-error :from :warning)))




(def float-type
  ( ->  (t/make-type  :float
                      :recognizer float?
                      :attributes #{:numeric :base})
        aspects/make-visible 
        aspects/make-equatable
        aspects/make-comparable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        (t/attach-instruction , float-abs)
        (t/attach-instruction , float-add)
        (t/attach-instruction , float-arccosine)
        (t/attach-instruction , float-arcsine)
        (t/attach-instruction , float-arctangent)
        (t/attach-instruction , float-cosine)
        (t/attach-instruction , float-dec)
        (t/attach-instruction , float-divide)
        (t/attach-instruction , boolean->float)
        (t/attach-instruction , char->float)
        (t/attach-instruction , integer->float)
        (t/attach-instruction , string->float)
        (t/attach-instruction , float-E)
        (t/attach-instruction , float-π)
        (t/attach-instruction , float-inc)
        (t/attach-instruction , float-ln)
        (t/attach-instruction , float-ln1p)
        (t/attach-instruction , float-log10)
        (t/attach-instruction , float-mod)
        (t/attach-instruction , float-multiply)
        (t/attach-instruction , float-sine)
        (t/attach-instruction , float-power)
        (t/attach-instruction , float-sign)
        (t/attach-instruction , float-sqrt)
        (t/attach-instruction , boolean->signedfloat)
        (t/attach-instruction , float-subtract)
        (t/attach-instruction , float-tangent)
        ))

