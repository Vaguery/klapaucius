(ns push.instructions.base.integer
  (:require [push.instructions.instructions-core :as core])
  (:use [push.instructions.dsl]))


(def integer-add
  (core/build-instruction
    integer-add
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (push-onto :integer :sum)))


(def integer-subtract
  (core/build-instruction
    integer-subtract
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(-' %1 %2) :as :diff)
    (push-onto :integer :diff)))
