(ns push.instructions.aspects.equatable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.types.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.equatable)
  )


;; equatable instructions


(fact "equal?-instruction returns an Instruction with the correct stuff"
  (let [foo-equal (equal?-instruction (make-type :foo))]
    (class foo-equal) => push.instructions.core.Instruction
    (:needs foo-equal) => {:foo 2, :boolean 0}
    (:token foo-equal) => :foo-equal?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-equal)
        :foo-equal?)
      :boolean) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-equal)
        :foo-equal?)
      :boolean) => '(true)))



(fact "notequal?-instruction returns an Instruction with the correct stuff"
  (let [foo-notequal (notequal?-instruction (make-type :foo))]
    (class foo-notequal) => push.instructions.core.Instruction
    (:needs foo-notequal) => {:foo 2, :boolean 0}
    (:token foo-notequal) => :foo-notequal?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-notequal)
        :foo-notequal?)
      :boolean) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-notequal)
        :foo-notequal?)
      :boolean) => '(false)))



(fact "`make-equatable` takes adds the :equatable attribute to a PushType record"
  (:attributes (make-equatable (make-type :foo))) => #{:equatable})


(fact "`make-equatable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-equatable (make-type :foo)))) => '(:foo-equal? :foo-notequal?))

