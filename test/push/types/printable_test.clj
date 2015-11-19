(ns push.types.printable-test
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:require [push.interpreter.core :as i])
  (:use push.types.core)
  (:use push.instructions.modules.print)
  )


;; :printable types


(fact "`make-printable` takes adds the :printable attribute to a PushType record"
  (:attributes (make-printable (make-type :thingie))) => #{:printable})


(fact "print-instruction produces an Instruction with the correct stuff"
  (let [foo-print (print-instruction (make-type :foo))]
    (class foo-print) => push.instructions.core.Instruction
    (:needs foo-print) => {:foo 1, :print 0}
    (:token foo-print) => :foo-print
    (get-stack
      (i/execute-instruction
        (i/register-instruction (i/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :print) => '(2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (i/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :foo) => '()
))
