(ns push.instructions.aspects.storable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.storable)
  )


;; storable instructions


(fact "save-instruction returns an Instruction with the correct stuff"
  (let [foo-save (save-instruction (make-type :foo))]
    
    (class foo-save) => push.instructions.core.Instruction
    
    (:needs foo-save) => {:foo 1, :ref 1}
    
    (:token foo-save) => :foo-save

    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '(111 2) :ref '(:x)}) foo-save)
      :foo-save)) => {:x '(111)}
    
    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '(111 2) :ref '()}) foo-save)
      :foo-save)) => {}

    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :bindings {:x 0} :stacks {:foo '(111 2) :ref '(:x)}) foo-save)
      :foo-save)) => {:x '(111 0)}))



(fact "`make-storable` takes adds the :storable attribute to a PushType record"
  (:attributes (make-storable (make-type :foo))) => #{:storable})


(fact "`make-storable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-storable (make-type :foo)))) => '(:foo-save :foo-savestack :foo-store :foo-storestack))



(fact "savestack-instruction returns an Instruction with the correct stuff"
  (let [foo-ss (savestack-instruction (make-type :foo))]
    
    (class foo-ss) => push.instructions.core.Instruction
    
    (:needs foo-ss) => {:foo 0 :ref 1}
    
    (:token foo-ss) => :foo-savestack

    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '(111 2) :ref '(:x)}) foo-ss)
      :foo-savestack)) => {:x '(111 2)}

    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter
          :stacks {:foo '(111 2) :ref '(:x)}
          :bindings {:x '(:a :b :c)}) foo-ss)
      :foo-savestack)) => {:x '(111 2)}
    
    (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '()}) foo-ss)
      :foo-savestack)) => {}))



(fact "storestack-instruction returns an Instruction with the correct stuff"
  (let [foo-ss (storestack-instruction (make-type :foo))]
    
    (class foo-ss) => push.instructions.core.Instruction
    
    (:needs foo-ss) => {:foo 0}
    
    (:token foo-ss) => :foo-storestack

    (first (vals (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '(111 2) :ref '(:x)}) foo-ss)
      :foo-storestack)))) => '(111 2)
    
    (first (vals (:bindings (i/execute-instruction
      (i/register-instruction 
        (m/basic-interpreter :stacks {:foo '()}) foo-ss)
      :foo-storestack)))) => '()))


