(ns push.types.extra.generator-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.generator])
  )


(fact "constructors"
  (:state (make-generator 3 inc)) => 3
  (:step-function (make-generator 3 inc)) => fn?
  (:origin (make-generator 3 inc)) => 3

  (:state (make-generator 3 inc 122)) => 3
  (:step-function (make-generator 3 inc 122)) => fn?
  (:origin (make-generator 3 inc 122)) => 122)



(fact "stepping"
  (let [g (make-generator 3 inc)]
    (:state (step-generator g)) => 4
    (:origin (step-generator g)) => 3
    (:state (step-generator (step-generator g))) => 5)

  (let [g (make-generator 3 (partial + -4))]
    (:state (step-generator g)) => -1
    (:origin (step-generator g)) => 3
    (:state (step-generator (step-generator g))) => -5)

  (let [g (make-generator \e #(char (inc (int %))))]
    (:state (step-generator g)) => \f
    (:origin (step-generator g)) => \e
    (:state (step-generator (step-generator g))) => \g)


  (let [g (make-generator "foo" (partial clojure.string/reverse))]
    (:state (step-generator g)) => "oof"
    (:origin (step-generator g)) => "foo"
    (:state (step-generator (step-generator g))) => "foo"))


(fact "stepping that returns nil destroys the generator itself"
  (let [g (make-generator 3 (fn [x] constantly nil))]
    (:state g) => 3
    (step-generator g) => nil
  ))


(fact "generator-type knows some instructions"
  (keys (:instructions generator-type)) =>
    (contains [:generator-dup :generator-save] :in-any-order :gaps-ok))


(fact ":generator type has the expected :attributes"
  (:attributes generator-type) =>
    (contains #{:generator :movable :quotable :returnable :storable :visible}))


(fact "generator-type knows the :visible instructions"
  (keys (:instructions generator-type)) =>
    (contains [:generator-stackdepth :generator-empty?] :in-any-order :gaps-ok))


(fact "generator-type knows the :movable instructions"
  (keys (:instructions generator-type)) =>
    (contains [:generator-shove :generator-pop :generator-dup :generator-rotate :generator-yank :generator-yankdup :generator-flush :generator-swap] :in-any-order :gaps-ok))


(fact "generator-type knows the :returnable instructions"
  (keys (:instructions generator-type)) => (contains [:generator-return]))

