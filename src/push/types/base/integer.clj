(ns push.types.base.integer
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; arithmetic


(defn sign [i] (compare i 0))


(def integer-add (t/simple-2-in-1-out-instruction :integer "add" '+'))


(def integer-dec (t/simple-1-in-1-out-instruction :integer "dec" 'dec'))


(def integer-divide
  (core/build-instruction
    integer-divide
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (long (/ %2 %1))) :as :quotient)
    (d/push-these-onto :integer [:replacement :quotient])))


(def integer-inc (t/simple-1-in-1-out-instruction :integer "inc" 'inc'))


(def integer-mod
  (core/build-instruction
    integer-mod
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (mod %2 %1)) :as :remainder)
    (d/push-these-onto :integer [:replacement :remainder])))


(def integer-multiply (t/simple-2-in-1-out-instruction :integer  "multiply" '*'))


(def integer-sign (t/simple-1-in-1-out-instruction :integer  "sign" 'sign))


(def integer-subtract (t/simple-2-in-1-out-instruction :integer "subtract" '-'))


;; conversion


(def integer-fromboolean
  (core/build-instruction
    integer-fromboolean
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :integer :logic)))


(def integer-signfromboolean
  (core/build-instruction
    integer-signfromboolean
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (d/push-onto :integer :logic)))


(def integer-fromfloat
  (core/build-instruction
    integer-fromfloat
    :tags #{:numeric :base :conversion}
    (d/consume-top-of :float :as :arg1)
    (d/calculate [:arg1] #(bigint %1) :as :int)
    (d/push-onto :integer :int)))


(def integer-fromchar
  (core/build-instruction
    integer-fromchar
    :tags #{:base :conversion}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(bigint %1) :as :int)
    (d/push-onto :integer :int)))


(def integer-fromstring
  (core/build-instruction
    integer-fromstring
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] 
      #(try (Long/parseLong %1) (catch NumberFormatException _ nil))
        :as :result)
    (d/push-onto :integer :result)))



(def classic-integer-type
  ( ->  (t/make-type  :integer
                      :recognizer integer?
                      :attributes #{:numeric})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , integer-add)
        (t/attach-instruction , integer-dec)
        (t/attach-instruction , integer-divide)
        (t/attach-instruction , integer-fromboolean)
        (t/attach-instruction , integer-fromchar)
        (t/attach-instruction , integer-fromfloat)
        (t/attach-instruction , integer-fromstring)
        (t/attach-instruction , integer-inc)
        (t/attach-instruction , integer-mod)
        (t/attach-instruction , integer-multiply)
        (t/attach-instruction , integer-sign)
        (t/attach-instruction , integer-signfromboolean)
        (t/attach-instruction , integer-subtract)
        ))

