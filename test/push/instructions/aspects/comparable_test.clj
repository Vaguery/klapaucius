(ns push.instructions.aspects.comparable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.comparable)
  )


;; comparable instructions


(fact "lessthan?-instruction returns an Instruction with the correct stuff"
  (let [foo-lessthan (lessthan?-instruction (make-type :foo))]
    (class foo-lessthan) => push.instructions.core.Instruction
    (:needs foo-lessthan) => {:foo 2}
    (:token foo-lessthan) => :foo<?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-lessthan)
        :foo<?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-lessthan)
        :foo<?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-lessthan)
        :foo<?)
      :exec) => '(true)))



(fact "lessthan?-instruction catches runtime errors"
  (let [foo-lessthan (lessthan?-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-lessthan)
        :foo<?)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-lessthan)
        :foo<?)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))




(fact "lessthanorequal?-instruction returns an Instruction with the correct stuff"
  (let [foo-lte (lessthanorequal?-instruction (make-type :foo))]
    (class foo-lte) => push.instructions.core.Instruction
    (:needs foo-lte) => {:foo 2}
    (:token foo-lte) => :foo≤?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-lte)
        :foo≤?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-lte)
        :foo≤?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-lte)
        :foo≤?)
      :exec) => '(true)))



(fact "lessthanorequal?-instruction catches runtime errors"
  (let [foo-lessthanorequal (lessthanorequal?-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-lessthanorequal)
        :foo≤?)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-lessthanorequal)
        :foo≤?)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))



(fact "greaterthanorequal?-instruction returns an Instruction with the correct stuff"
  (let [foo-gte (greaterthanorequal?-instruction (make-type :foo))]
    (class foo-gte) => push.instructions.core.Instruction
    (:needs foo-gte) => {:foo 2}
    (:token foo-gte) => :foo≥?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-gte)
        :foo≥?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-gte)
        :foo≥?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-gte)
        :foo≥?)
      :exec) => '(false)))




(fact "greaterthanorequal?-instruction catches runtime errors"
  (let [foo-greaterthanorequal (greaterthanorequal?-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-greaterthanorequal)
        :foo≥?)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-greaterthanorequal)
        :foo≥?)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))




(fact "greaterthan?-instruction returns an Instruction with the correct stuff"
  (let [foo-gt (greaterthan?-instruction (make-type :foo))]
    (class foo-gt) => push.instructions.core.Instruction
    (:needs foo-gt) => {:foo 2}
    (:token foo-gt) => :foo>?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-gt)
        :foo>?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-gt)
        :foo>?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-gt)
        :foo>?)
      :exec) => '(false)))





(fact "greaterthan?-instruction catches runtime errors"
  (let [foo-greaterthan (greaterthan?-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-greaterthan)
        :foo>?)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-greaterthan)
        :foo>?)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))




(fact "min-instruction returns an Instruction with the correct stuff"
  (let [foo-min (min-instruction (make-type :foo))]
    (class foo-min) => push.instructions.core.Instruction
    (:needs foo-min) => {:foo 2}
    (:token foo-min) => :foo-min
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-min)
        :foo-min)
      :exec) => '(1)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-min)
        :foo-min)
      :exec) => '(1)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-min)
        :foo-min)
      :exec) => '(2)
      ))



(fact "min-instruction catches runtime errors"
  (let [foo-min (min-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-min)
        :foo-min)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-min)
        :foo-min)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))



(fact "max-instruction returns an Instruction with the correct stuff"
  (let [foo-max (max-instruction (make-type :foo))]
    (class foo-max) => push.instructions.core.Instruction
    (:needs foo-max) => {:foo 2}
    (:token foo-max) => :foo-max
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-max)
        :foo-max)
      :exec) => '(2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 1)}) foo-max)
        :foo-max)
      :exec) => '(1)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(3 2)}) foo-max)
        :foo-max)
      :exec) => '(3)
      ))




(fact "max-instruction catches runtime errors"
  (let [foo-max (max-instruction (make-type :foo))]

    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-max)
        :foo-max)
      :exec) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1M 2/3)}) foo-max)
        :foo-max)
      :error) => '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    ))





(fact "`make-comparable` takes adds the :comparable attribute to a PushType record"
  (:attributes (make-comparable (make-type :foo))) => #{:comparable})




(fact "`make-comparable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-comparable (make-type :foo)))) =>
      '(:foo<? :foo≤? :foo>? :foo≥? :foo-min :foo-max))
