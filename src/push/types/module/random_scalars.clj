(ns push.types.module.random-scalars
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            ))


(defn valid-integer-arg?
  [arg]
  (instance? java.lang.Long arg))



(def integer-uniform
  (core/build-instruction
    integer-uniform
    "`:integer-uniform` pops the top `:integer` value, and pushes a uniform random integer sampled from the range [0,:int). If the integer is negative, a negative result is returned; if the argument is out of bounds, an `:error` is pushed instead of a value."
    :tags #{:numeric :random}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(valid-integer-arg? %1) :as :valid)
    (d/calculate [:valid :arg] #(if %1 (long (rand %2)) nil) :as :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-uniform argument invalid") :as :warning)
    (d/push-onto :integer :result)
    (d/record-an-error :from :warning)))



(def float-uniform
  (core/build-instruction
    float-uniform
    "`:float-uniform` pops the top `:float` value, and pushes a random float uniformly sampled from the range [0,:f). If the float is negative, a negative result is returned."
    :tags #{:numeric :random}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(* (rand) %1) :as :result)
    (d/push-onto :float :result)))



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
