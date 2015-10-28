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


; (def integer-divide
;   (core/build-instruction
;     integer-divide
;     :tags #{:arithmetic :base :warning}
;     (consume-top-of :integer :as :denominator)
;     (consume-top-of :integer :as :numerator)
;     (calculate [:denominator] #(zero? %1) :as :boom)
;     (calculate 
;       [:boom :numerator :denominator] #(if %1 0 (int (/ %2 %3))) :as :quotient)
;     (push-onto :integer :quotient)))
