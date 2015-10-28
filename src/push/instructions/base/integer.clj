(ns push.instructions.base.integer
  (:require [push.instructions.instructions-core :as core])
  (:use [push.instructions.dsl]))


(def integer-add
  (core/build-instruction
    integer-add
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (push-onto :integer :sum)))


(def integer-multiply
  (core/build-instruction
    integer-multiply
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(*' %1 %2) :as :prod)
    (push-onto :integer :prod)))


(def integer-subtract
  (core/build-instruction
    integer-subtract
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(-' %1 %2) :as :diff)
    (push-onto :integer :diff)))


(def integer-divide
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


(def integer-mod
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
