(ns push.types.comparable-test
  (:use midje.sweet)
  (:use [push.types.core])
  (:require [push.interpreter.interpreter-core :as i]))


;; comparable instructions


(fact "lessthan?-instruction returns an Instruction with the correct stuff"
  (let [foo-lessthan (lessthan?-instruction (make-type :foo))]
    (class foo-lessthan) => push.instructions.instructions_core.Instruction
    (:tags foo-lessthan) => #{:comparison}
    (:needs foo-lessthan) => {:foo 2, :boolean 0}
    (:token foo-lessthan) => :foo<?
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-lessthan)
        :foo<?)
      :boolean) => '(false)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-lessthan)
        :foo<?)
      :boolean) => '(false)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-lessthan)
        :foo<?)
      :boolean) => '(true)))


(fact "lessthanorequal?-instruction returns an Instruction with the correct stuff"
  (let [foo-lte (lessthanorequal?-instruction (make-type :foo))]
    (class foo-lte) => push.instructions.instructions_core.Instruction
    (:tags foo-lte) => #{:comparison}
    (:needs foo-lte) => {:foo 2, :boolean 0}
    (:token foo-lte) => :foo≤?
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-lte)
        :foo≤?)
      :boolean) => '(false)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-lte)
        :foo≤?)
      :boolean) => '(true)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-lte)
        :foo≤?)
      :boolean) => '(true)))


(fact "greaterthanorequal?-instruction returns an Instruction with the correct stuff"
  (let [foo-gte (greaterthanorequal?-instruction (make-type :foo))]
    (class foo-gte) => push.instructions.instructions_core.Instruction
    (:tags foo-gte) => #{:comparison}
    (:needs foo-gte) => {:foo 2, :boolean 0}
    (:token foo-gte) => :foo≥?
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-gte)
        :foo≥?)
      :boolean) => '(true)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-gte)
        :foo≥?)
      :boolean) => '(true)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-gte)
        :foo≥?)
      :boolean) => '(false)))



(fact "greaterthan?-instruction returns an Instruction with the correct stuff"
  (let [foo-gt (greaterthan?-instruction (make-type :foo))]
    (class foo-gt) => push.instructions.instructions_core.Instruction
    (:tags foo-gt) => #{:comparison}
    (:needs foo-gt) => {:foo 2, :boolean 0}
    (:token foo-gt) => :foo>?
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-gt)
        :foo>?)
      :boolean) => '(true)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-gt)
        :foo>?)
      :boolean) => '(false)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-gt)
        :foo>?)
      :boolean) => '(false)))


(fact "min-instruction returns an Instruction with the correct stuff"
  (let [foo-min (min-instruction (make-type :foo))]
    (class foo-min) => push.instructions.instructions_core.Instruction
    (:tags foo-min) => #{:comparison}
    (:needs foo-min) => {:foo 2}
    (:token foo-min) => :foo-min
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-min)
        :foo-min)
      :foo) => '(1)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-min)
        :foo-min)
      :foo) => '(1)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-min)
        :foo-min)
      :foo) => '(2)))


(fact "max-instruction returns an Instruction with the correct stuff"
  (let [foo-max (max-instruction (make-type :foo))]
    (class foo-max) => push.instructions.instructions_core.Instruction
    (:tags foo-max) => #{:comparison}
    (:needs foo-max) => {:foo 2}
    (:token foo-max) => :foo-max
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-max)
        :foo-max)
      :foo) => '(2)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 1)}) foo-max)
        :foo-max)
      :foo) => '(1)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(3 2)}) foo-max)
        :foo-max)
      :foo) => '(3)))


(fact "`make-comparable` takes adds the :comparable attribute to a PushType record"
  (:attributes (make-comparable (make-type :foo))) => #{:comparable})


(fact "`make-comparable` takes adds appropriate instructions to a PushType record"
  (map :token (:instructions
    (make-comparable (make-type :foo)))) =>
      '(:foo<? :foo≤? :foo>? :foo≥? :foo-min :foo-max))
