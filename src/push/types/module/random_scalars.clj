(ns push.types.module.random-scalars
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            ))


(defn typesafe-rand-int
  [arg]
  (cond
    (= (type arg) java.lang.Long) (long (rand arg))
    :else (rand-int arg)))



(def integer-uniform
  (core/build-instruction
    integer-uniform
    "`:integer-uniform` pops the top `:integer` value, and pushes a uniform random integer sampled from the range [0,:int). If the integer is negative, a negative result is returned; if the (Push) :integer value is larger than the Java `long` size limit, what is returned is the product of a random `Double` and the range argument, rounded down."
    :tags #{:numeric :random}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(cond
                          (neg? %1) (- (typesafe-rand-int %1))
                          (zero? %1) 0 
                          :else (typesafe-rand-int %1)) :as :result)
    (d/push-onto :integer :result)))



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
