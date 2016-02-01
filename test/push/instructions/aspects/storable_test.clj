(ns push.instructions.aspects.storable-test
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:require [push.interpreter.core :as i])
  (:require [push.interpreter.templates.minimum :as m])
  (:use push.types.core)
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
    (make-storable (make-type :foo)))) => '(:foo-save :foo-store))

