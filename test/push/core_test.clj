(ns push.core-test
  (:use midje.sweet)
  (:require [push.core :as push]))


(fact "I can create an owe interpreter"
  ; (push/interpreter) => 99
  )


(fact "I can run a Push program and get a named stack"
  (push/get-stack (push/run [1 2 :integer-add] 100) :integer) => '(3)
)