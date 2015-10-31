(ns push.instructions.base.conversion
  (:require [push.instructions.instructions-core :as core])
  (:use [push.instructions.dsl]))


(def integer-fromboolean
  (core/build-instruction
    integer-fromboolean
    :tags #{:numeric :base :combinator}
    (consume-top-of :boolean :as :arg1)
    (calculate [:arg1] #(if %1 1 0) :as :logic)
    (push-onto :integer :logic)))
