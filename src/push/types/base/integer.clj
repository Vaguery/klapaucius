(ns push.types.base.integer
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; arithmetic


(defn sign [i] (compare i 0))


(def int-add (t/simple-2-in-1-out-instruction :integer "add" '+'))


(def int-dec (t/simple-1-in-1-out-instruction :integer "dec" 'dec'))


(def int-divide
  (core/build-instruction
    integer-divide
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (int (/ %2 %1))) :as :quotient)
    (d/push-these-onto :integer [:replacement :quotient])))


(def int-inc (t/simple-1-in-1-out-instruction :integer "inc" 'inc'))


(def int-mod
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


(def int-multiply (t/simple-2-in-1-out-instruction :integer  "multiply" '*'))


(def int-sign (t/simple-1-in-1-out-instruction :integer  "sign" 'sign))


(def int-subtract (t/simple-2-in-1-out-instruction :integer "subtract" '-'))


;; conversion


(def int-fromboolean
  (core/build-instruction
    integer-fromboolean
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :integer :logic)))


(def int-fromfloat
  (core/build-instruction
    integer-fromfloat
    :tags #{:numeric :base :conversion}
    (d/consume-top-of :float :as :arg1)
    (d/calculate [:arg1] #(bigint %1) :as :int)
    (d/push-onto :integer :int)))


(def int-fromchar
  (core/build-instruction
    integer-fromchar
    :tags #{:base :conversion}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(int %1) :as :int)
    (d/push-onto :integer :int)))




(def classic-integer-type
  ( ->  (t/make-type  :integer
                      :recognizer integer?
                      :attributes #{:numeric})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , int-add)
        (t/attach-instruction , int-dec)
        (t/attach-instruction , int-divide)
        (t/attach-instruction , int-fromboolean)
        (t/attach-instruction , int-fromchar)
        (t/attach-instruction , int-fromfloat)
        (t/attach-instruction , int-inc)
        (t/attach-instruction , int-mod)
        (t/attach-instruction , int-multiply)
        (t/attach-instruction , int-sign)
        (t/attach-instruction , int-subtract)
        ))

