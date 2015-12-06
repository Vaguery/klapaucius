(ns push.types.base.float
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )


(def float-add (t/simple-2-in-1-out-instruction
  "`:float-add` pops the top two `:float` values, and pushes their sum"
  :float "add" '+'))


(def float-cosine (t/simple-1-in-1-out-instruction
  "`:float-cosine` pushes the cosine of the top `:float` item, read as radians"
  :float "cosine" #(Math/cos %1)))


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


(def float-inc (t/simple-1-in-1-out-instruction
  "`:float-inc` adds 1.0 to the top `:float` item"
  :float "inc" 'inc'))


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
    (d/record-an-error :from :warning)
))


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


(def float-tangent (t/simple-1-in-1-out-instruction
  "`:float-tangent` pushes the tangent of the top `:float` item, read as an angle in radians"
  :float "tangent" #(Math/tan %1)))


(def classic-float-type
  ( ->  (t/make-type  :float
                      :recognizer float?
                      :attributes #{:numeric :base})
        aspects/make-visible 
        aspects/make-equatable
        aspects/make-comparable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        (t/attach-instruction , float-add)
        (t/attach-instruction , float-cosine)
        (t/attach-instruction , float-dec)
        (t/attach-instruction , float-divide)
        (t/attach-instruction , boolean->float)
        (t/attach-instruction , char->float)
        (t/attach-instruction , integer->float)
        (t/attach-instruction , string->float)
        (t/attach-instruction , float-inc)
        (t/attach-instruction , float-mod)
        (t/attach-instruction , float-multiply)
        (t/attach-instruction , float-sine)
        (t/attach-instruction , float-sign)
        (t/attach-instruction , boolean->signedfloat)
        (t/attach-instruction , float-subtract)
        (t/attach-instruction , float-tangent)
        ))

