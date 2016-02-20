(ns push.instructions.extra.numeric-scaling
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d])
  )


(def integer-few
  (core/build-instruction
    integer-few
    "`:integer-few` pops the top `:integer` value, and calculates `(mod 10 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10) :as :scaled)
    (d/push-onto :integer :scaled)))


(def integer-some
  (core/build-instruction
    integer-some
    "`:integer-some` pops the top `:integer` value, and calculates `(mod 100 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 100) :as :scaled)
    (d/push-onto :integer :scaled)))


(def integer-many
  (core/build-instruction
    integer-many
    "`:integer-many` pops the top `:integer` value, and calculates `(mod 1000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 1000) :as :scaled)
    (d/push-onto :integer :scaled)))


(def integer-lots
  (core/build-instruction
    integer-lots
    "`:integer-lots` pops the top `:integer` value, and calculates `(mod 10000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10000) :as :scaled)
    (d/push-onto :integer :scaled)))


;;;;;;;;;;;;;;;;;


(def numeric-scaling-module
  ( ->  (t/make-module  :numeric-scaling
                        :attributes #{:numeric})

        (t/attach-instruction , integer-few)
        (t/attach-instruction , integer-lots)
        (t/attach-instruction , integer-many)
        (t/attach-instruction , integer-some)
        ))
