(ns push.type.module.random-scalars
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.type.core :as t]
            [clojure.math.numeric-tower :as math]
            [push.instructions.dsl :as d]
            ))



(def integer-uniform
  (core/build-instruction
    integer-uniform
    "`:integer-uniform` pops the top `:scalar` value, and pushes a uniform random integer (typecase from a uniform number on the range `[0.0, :arg)`). If the scalar is negative, a negative result is returned; if the argument is out of bounds (larger than Long/MAX_VALUE), an `:error` is pushed instead of a value."
    :tags #{:numeric :random}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(< (math/abs %1) Long/MAX_VALUE) :as :valid)
    (d/calculate [:valid :arg] #(when %1 (long (rand %2))) :as :result)
    (d/calculate [:valid]
      #(when-not %1 ":integer-uniform argument out of range") :as :warning)
    (d/push-onto :scalar :result)
    (d/record-an-error :from :warning)))



(def float-uniform
  (core/build-instruction
    float-uniform
    "`:float-uniform` pops the top `:scalar` value, and pushes a random double uniformly sampled from the range [0,:f). If the float is negative, a negative result is returned."
    :tags #{:numeric :random}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(< (math/abs %1) Double/MAX_VALUE) :as :valid)
    (d/calculate [:valid :arg] #(when %1 (rand %2)) :as :result)
    (d/calculate [:valid]
      #(when-not %1 ":float-uniform argument out of range") :as :warning)
    (d/push-onto :scalar :result)
    (d/record-an-error :from :warning)))



(def boolean-faircoin
  (core/build-instruction
    boolean-faircoin
    "`:boolean-faircoin` pushes a random `:boolean` value, with equal probability `true` or `false`."
    :tags #{:logical :random}
    (d/calculate [] #(< (rand) 0.5) :as :result)
    (d/push-onto :boolean :result)))


;;;;;;;;;;;;;;;;;


(def random-scalars-module
  ( ->  (t/make-module  :random-scalars
                        :attributes #{:numeric :random})

        (t/attach-instruction , integer-uniform)
        (t/attach-instruction , float-uniform)
        (t/attach-instruction , boolean-faircoin)
        ))
