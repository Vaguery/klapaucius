(ns push.instructions.aspects.returnable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.types.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.returnable)
  (:use push.types.modules.environment)
  )


;; :returnable types


(fact "`make-returnable` takes adds the :returnable attribute to a PushType record"
  (:attributes (make-returnable (make-type :thingie))) => #{:returnable})


(fact "return-instruction produces an Instruction with the correct stuff"
  (let [foo-return (return-instruction (make-type :foo))]
    (class foo-return) => push.instructions.core.Instruction
    (:needs foo-return) => {:foo 1, :return 0}
    (:token foo-return) => :foo-return
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-return)
        :foo-return)
      :return) => '(2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-return)
        :foo-return)
      :foo) => '()))


(fact "return-pop-instruction produces an Instruction with the correct stuff"
  (let [foo-return-pop (return-pop-instruction (make-type :foo))]
    (class foo-return-pop) => push.instructions.core.Instruction
    (:needs foo-return-pop) => {:return 0}
    (:token foo-return-pop) => :foo-return-pop
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-return-pop)
        :foo-return-pop)
      :return) => '(:foo-pop)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(2)}) foo-return-pop)
        :foo-return-pop)
      :foo) => '(2)))
