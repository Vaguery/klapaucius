(ns push.instructions.aspects.visible-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.instructions.aspects.visible)
  (:use push.instructions.aspects)
  (:use push.type.core)
  )


;; :visible types


(fact "`make-visible` takes adds the :visible attribute to a PushType record"
  (:attributes (make-visible (make-type :scalar))) => #{:visible})


(fact "stackdepth-instruction returns an Instruction with the correct stuff"
  (let [foo-depth (stackdepth-instruction (make-type :foo))]
    (class foo-depth) => push.instructions.core.Instruction
    (:needs foo-depth) => {}
    (:token foo-depth) => :foo-stackdepth
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-depth)
        :foo-stackdepth)
      :exec) => '(2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(false [2] 3)}) foo-depth)
        :foo-stackdepth)
      :exec) => '(3)))


(fact "empty?-instruction returns an Instruction with the correct stuff"
  (let [foo-none? (empty?-instruction (make-type :foo))]
    (class foo-none?) => push.instructions.core.Instruction
    (:needs foo-none?) => {}
    (:token foo-none?) => :foo-empty?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-none?)
        :foo-empty?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-none?)
        :foo-empty?)
      :exec) => '(true)))


(fact "`make-visible` takes adds a x-stackdepth and x-empty? instructions to a PushType record"
  (keys (:instructions
    (make-visible (make-type :foo)))) => '(:foo-stackdepth :foo-empty?))
