(ns push.core-test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:require [push.core :as p]))




(fact "I can run a Push program and get a named stack"
  (p/get-stack (p/run [1 2 :integer-add] 100) :integer) => '(3))


(future-fact "I can produce a gazetteer"
  (i/produce-gazetteer (p/run [1 2 :integer-add] 100)) => 8)