(ns push.instructions.aspects.printable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.type.module.print)
  (:use push.instructions.aspects.printable)
  (:use push.type.definitions.complex)
  )


;; :printable types


(fact "`make-printable` takes adds the :printable attribute to a PushType record"
  (:attributes (make-printable (make-type :thingie))) => #{:printable})


(fact "print-instruction produces an Instruction with the correct stuff"
  (let [foo-print (print-instruction (make-type :foo))]
    (class foo-print) => push.instructions.core.Instruction
    (:needs foo-print) => {:foo 1}
    (:token foo-print) => :foo-print
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :print) => '("2")
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-print)
        :foo-print)
      :foo) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo (list (complexify 9 -3.1))}) foo-print)
        :foo-print)
      :print) => '("#push.type.definitions.complex.Complex{:re 9, :im -3.1}")
))
