(ns push.instructions.aspects.printable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.types.core)
  (:use push.instructions.aspects)
  (:use push.types.module.print)
  (:use push.instructions.aspects.printable)
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
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :print) => '(2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :foo) => '()
))
