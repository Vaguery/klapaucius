(ns push.types.visible-test
  (:use midje.sweet)
  (:use [push.types.core])
  (:require [push.interpreter.core :as i]))


;; :visible types


(fact "`make-visible` takes adds the :visible attribute to a PushType record"
  (:attributes (make-visible (make-type :integer))) => #{:visible})


(fact "stackdepth-instruction returns an Instruction with the correct stuff"
  (let [foo-depth (stackdepth-instruction (make-type :foo))]
    (class foo-depth) => push.instructions.core.Instruction
    (:needs foo-depth) => {:foo 0, :integer 0}
    (:token foo-depth) => :foo-stackdepth
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-depth)
        :foo-stackdepth)
      :integer) => '(2)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(false [2] 3)}) foo-depth)
        :foo-stackdepth)
      :integer) => '(3)))


(fact "empty?-instruction returns an Instruction with the correct stuff"
  (let [foo-none? (empty?-instruction (make-type :foo))]
    (class foo-none?) => push.instructions.core.Instruction
    (:needs foo-none?) => {:boolean 0, :foo 0}
    (:token foo-none?) => :foo-empty?
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-none?)
        :foo-empty?)
      :boolean) => '(false)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '()}) foo-none?)
        :foo-empty?)
      :boolean) => '(true)))


(fact "`make-visible` takes adds a x-stackdepth and x-empty? instructions to a PushType record"
  (keys (:instructions
    (make-visible (make-type :foo)))) => '(:foo-stackdepth :foo-empty?))
