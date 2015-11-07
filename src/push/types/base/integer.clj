(ns push.types.base.integer
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:use [push.instructions.dsl])
  )


;; arithmetic


(def int-add
  (core/build-instruction
    integer-add
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (push-onto :integer :sum)))


(def int-dec
  (core/build-instruction
    integer-dec
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1] #(dec' %1) :as :next)
    (push-onto :integer :next)))


(def int-divide
  (core/build-instruction
    integer-divide
    :tags #{:arithmetic :base :dangerous}
    (consume-top-of :integer :as :denominator)
    (consume-top-of :integer :as :numerator)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %1 (int (/ %2 %1))) :as :quotient)
    (push-these-onto :integer [:replacement :quotient])))


(def int-inc
  (core/build-instruction
    integer-inc
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1] #(inc' %1) :as :next)
    (push-onto :integer :next)))


(def int-mod
  (core/build-instruction
    integer-mod
    :tags #{:arithmetic :base :dangerous}
    (consume-top-of :integer :as :denominator)
    (consume-top-of :integer :as :numerator)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %1 (mod %2 %1)) :as :remainder)
    (push-these-onto :integer [:replacement :remainder])))


(def int-multiply
  (core/build-instruction
    integer-multiply
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(*' %1 %2) :as :prod)
    (push-onto :integer :prod)))


(def int-subtract
  (core/build-instruction
    integer-subtract
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(-' %1 %2) :as :diff)
    (push-onto :integer :diff)))


;; conversion


(def int-fromboolean
  (core/build-instruction
    integer-fromboolean
    :tags #{:base :conversion}
    (consume-top-of :boolean :as :arg1)
    (calculate [:arg1] #(if %1 1 0) :as :logic)
    (push-onto :integer :logic)))


(def int-fromfloat
  (core/build-instruction
    integer-fromfloat
    :tags #{:numeric :base :conversion}
    (consume-top-of :float :as :arg1)
    (calculate [:arg1] #(bigint %1) :as :int)
    (push-onto :integer :int)))


(def int-fromchar
  (core/build-instruction
    integer-fromchar
    :tags #{:base :conversion}
    (consume-top-of :char :as :arg1)
    (calculate [:arg1] #(int %1) :as :int)
    (push-onto :integer :int)))




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

