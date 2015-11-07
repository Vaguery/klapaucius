(ns push.types.base.integer
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; arithmetic


(def int-add
  (core/build-instruction
    integer-add
    :tags #{:arithmetic :base}
    (d/consume-top-of :integer :as :arg1)
    (d/consume-top-of :integer :as :arg2)
    (d/calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (d/push-onto :integer :sum)))


(def int-dec
  (core/build-instruction
    integer-dec
    :tags #{:arithmetic :base}
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1] #(dec' %1) :as :next)
    (d/push-onto :integer :next)))


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


(def int-inc
  (core/build-instruction
    integer-inc
    :tags #{:arithmetic :base}
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1] #(inc' %1) :as :next)
    (d/push-onto :integer :next)))


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


(def int-multiply
  (core/build-instruction
    integer-multiply
    :tags #{:arithmetic :base}
    (d/consume-top-of :integer :as :arg1)
    (d/consume-top-of :integer :as :arg2)
    (d/calculate [:arg1 :arg2] #(*' %1 %2) :as :prod)
    (d/push-onto :integer :prod)))


(def int-subtract
  (core/build-instruction
    integer-subtract
    :tags #{:arithmetic :base}
    (d/consume-top-of :integer :as :arg2)
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1 :arg2] #(-' %1 %2) :as :diff)
    (d/push-onto :integer :diff)))


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
        (t/attach-instruction , int-subtract)
        (t/attach-instruction , int-multiply)
        (t/attach-instruction , int-divide)
        (t/attach-instruction , int-mod)
        (t/attach-instruction , int-dec)
        (t/attach-instruction , int-inc)
        (t/attach-instruction , int-fromboolean)
        (t/attach-instruction , int-fromfloat)
        (t/attach-instruction , int-fromchar)
        ))

