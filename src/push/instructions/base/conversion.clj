(ns push.instructions.base.conversion
  (:require [push.instructions.instructions-core :as core])
  (:use [push.instructions.dsl]))


(def integer-fromboolean
  (core/build-instruction
    integer-fromboolean
    :tags #{:base :conversion}
    (consume-top-of :boolean :as :arg1)
    (calculate [:arg1] #(if %1 1 0) :as :logic)
    (push-onto :integer :logic)))


(def integer-fromfloat
  (core/build-instruction
    integer-fromfloat
    :tags #{:numeric :base :conversion}
    (consume-top-of :float :as :arg1)
    (calculate [:arg1] #(int %1) :as :int)
    (push-onto :integer :int)))
