(ns push.interpreter.basic-interpreter-test
  (:use midje.sweet)
  (:use push.interpreter.basic))


(fact "make-basic-interpreter creates a new Interpreter"
  (class (make-basic-interpreter)) => push.interpreter.core.Interpreter)