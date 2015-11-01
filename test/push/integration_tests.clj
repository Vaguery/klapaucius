(ns push.integration-tests
  (:use midje.sweet)
  (:require [push.instructions.base])
  (:use [push.instructions.dsl])
  (:use [push.interpreter.interpreter-core]))

;; First release

; (future-fact "I can create an Interpreter that will run a Push program from Clojure code")

; (future-fact "The Interpreter recognizes all the Clojush :integer and :boolean instructions")

; (future-fact "The Interpreter accepts inputs and uses them when running code")

; (future-fact "An Interpreter running a program needs an :until argument to take any steps at all")

; (future-fact "An Interpreter counts the steps it takes")

; (future-fact "A Clojush program can be translated into a Push-in-Clojure program with a preprocessor")