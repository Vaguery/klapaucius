(ns push.instructions.aspects.repeatable-test
  (:require [push.interpreter.core :as i]
            [push.core :as push])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.types.core)
  (:use push.instructions.aspects)
  (:use push.types.type.scalar)
  (:use push.instructions.aspects.repeatable-and-cycling)
  (:use push.types.module.environment)
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
      (push.types.type.generator/step-generator new-g) => new-g
))) 



(fact "echoall-instruction returns an Instruction with the correct stuff"

  (let [foo-echoall (echoall-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '(1234 56 (7) [8 9])) ) ]

    (class foo-echoall) => push.instructions.core.Instruction
    
    (:needs foo-echoall) => {:foo 0, :generator 0}
    (:products foo-echoall) => {:generator 1}

    (:token foo-echoall) => :foo-echoall

    (keys (:instructions (i/register-instruction context foo-echoall))) => (contains :foo-echoall)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-echoall) :foo-echoall)
      :foo) => '(1234 56 (7) [8 9])
    
    (let [stepped (i/execute-instruction
                            (i/register-instruction context foo-echoall) :foo-echoall)
          new-g (first (get-stack stepped :generator))]
      (push.types.type.generator/step-generator new-g) => new-g

      (:state new-g) => '(1234 56 (7) [8 9])
))) 



(fact "rerunall-instruction returns an Instruction with the correct stuff"

  (let [foo-rerunall (rerunall-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '(1234 56 (7) [8 9])) ) ]

    (class foo-rerunall) => push.instructions.core.Instruction
    
    (:needs foo-rerunall) => {:foo 0, :generator 0}
    (:products foo-rerunall) => {:generator 1}

    (:token foo-rerunall) => :foo-rerunall

    (keys (:instructions (i/register-instruction context foo-rerunall))) => (contains :foo-rerunall)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-rerunall) :foo-rerunall)
      :foo) => '(1234 56 (7) [8 9])
    
    (let [stepped (i/execute-instruction
                            (i/register-instruction context foo-rerunall) :foo-rerunall)
          new-g (first (get-stack stepped :generator))]

      (:state new-g) => '(1234 (56 (7) [8 9]))
      (:state (push.types.type.generator/step-generator new-g)) => '(56 ((7) [8 9]))

))) 

