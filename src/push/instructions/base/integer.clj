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


;; comparison


(def integer-lt
  (core/build-instruction
    integer-lt
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(< %1 %2) :as :less?)
    (push-onto :boolean :less?)))


(def integer-lte
  (core/build-instruction
    integer-lte
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(<= %1 %2) :as :lte?)
    (push-onto :boolean :lte?)))


(def integer-gt
  (core/build-instruction
    integer-gt
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(> %1 %2) :as :more?)
    (push-onto :boolean :more?)))


(def integer-gte
  (core/build-instruction
    integer-gte
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(>= %1 %2) :as :more?)
    (push-onto :boolean :more?)))


(def integer-eq
  (core/build-instruction
    integer-eq
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg2 :arg1] #(= %1 %2) :as :same?)
    (push-onto :boolean :same?)))