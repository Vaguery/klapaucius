(ns push.types.base.float
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(def float-add (t/simple-2-in-1-out-instruction :float "add" '+'))


(def float-cosine (t/simple-1-in-1-out-instruction
  "`:float-cosine` returns the cosine of the top `:float` item, read as radians"
  :float "cosine" #(Math/cos %1)))


(def float-dec (t/simple-1-in-1-out-instruction
  ":`float-dec` reduces the top `:float` value by 1.0"
  :float "dec" 'dec'))


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


(def float-fromboolean
  (core/build-instruction
    float-fromboolean
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :boolean :as :arg)
    (d/calculate [:arg] #(if %1 1.0 0.0) :as :result)
    (d/push-onto :float :result)))


(def float-fromchar
  (core/build-instruction
    float-fromchar
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :char :as :arg)
    (d/calculate [:arg] #(double (long %1)) :as :result)
    (d/push-onto :float :result)))


(def float-frominteger
  (core/build-instruction
    float-frominteger
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(double %1) :as :result)
    (d/push-onto :float :result)))


(def float-fromstring
  (core/build-instruction
    float-fromstring
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] 
      #(try (Double/parseDouble %1) (catch NumberFormatException _ nil))
        :as :result)
    (d/push-onto :float :result)))


(def float-signfromboolean
  (core/build-instruction
    float-signfromboolean
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


(def float-sign (t/simple-1-in-1-out-instruction
  "`:float-sign` returns examines the top `:float` item and returns -1.0 if it's negative, 0.0 if it's zero, and 1.0 if it's positive"
  :float "sign" #(float (compare %1 0.0))))


(def float-sine (t/simple-1-in-1-out-instruction
  "`:float-sine` returns the trigonometric sine of the top `:float` item, read as an angle in radians"
  :float "sine" #(Math/sin %1)))


(def float-tangent (t/simple-1-in-1-out-instruction
  "`:float-tangent` returns the tangent of the top `:float` item, read as an angle in radians"
  :float "tangent" #(Math/tan %1)))


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
        (t/attach-instruction , float-fromboolean)
        (t/attach-instruction , float-fromchar)
        (t/attach-instruction , float-frominteger)
        (t/attach-instruction , float-fromstring)
        (t/attach-instruction , float-inc)
        (t/attach-instruction , float-mod)
        (t/attach-instruction , float-multiply)
        (t/attach-instruction , float-sine)
        (t/attach-instruction , float-sign)
        (t/attach-instruction , float-signfromboolean)
        (t/attach-instruction , float-subtract)
        (t/attach-instruction , float-tangent)
        ))

