(ns push.instructions.aspects.repeatable-test
  (:require [push.interpreter.core :as i]
            [push.core :as push])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.types.core)
  (:use push.instructions.aspects)
  (:use push.types.base.integer)
  (:use push.instructions.aspects.repeatable)
  (:use push.types.modules.environment)
  )


;; :repeatable types


(fact "`make-repeatable` takes adds the :repeatable attribute to a PushType record"
  (:attributes (make-repeatable (make-type :thingie))) => #{:repeatable})



(fact "echo-instruction returns an Instruction with the correct stuff"

  (let [foo-echo (echo-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '(1234)) ) ]

    (class foo-echo) => push.instructions.core.Instruction
    
    (:needs foo-echo) => {:foo 1, :generator 0}
    (:products foo-echo) => {:generator 1}

    (:token foo-echo) => :foo-echo

    (keys (:instructions (i/register-instruction context foo-echo))) => (contains :foo-echo)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-echo) :foo-echo)
      :foo) => '()
    
    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction context foo-echo) :foo-echo)
                          :generator))]
      (push.types.extra.generator/step-generator new-g) => new-g
))) 

