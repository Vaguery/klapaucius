(ns push.instructions.standard.generator-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.extra.generator])
  )



(tabular
  (fact ":generator-counter pops an :integer and makes an incrementer from it"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks generator-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(17)
     :generator '()}           
                            :generator-counter       
                                                  {:integer  '()
                                                   :generator (list (make-generator 17 inc))} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '()
     :generator '()}           
                            :generator-counter       
                                                  {:integer  '()
                                                   :generator '()} 
                                                   )



(fact ":generator-stepper creates a new generator that jumps by increments"
  (let [gstep (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:integer '(3 -8)})]

    (push.core/get-stack (i/execute-instruction gstep :generator-stepper) :integer) => '()
    (:state 
      (first 
        (push.core/get-stack 
          (i/execute-instruction gstep :generator-stepper) 
          :generator))) => -8
    (:origin 
      (first 
        (push.core/get-stack 
          (i/execute-instruction gstep :generator-stepper) 
          :generator))) => -8    
    (:state 
      (step-generator
        (first 
          (push.core/get-stack 
            (i/execute-instruction gstep :generator-stepper) 
            :generator)))) => -5))



(fact ":generator-next creates a list of the next state and the updated generator, pushed to :exec"
  (let [gstep (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:generator (list (make-generator 8 dec))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction gstep :generator-next) 
                    :exec))]
        (first result) => 7
        (second result) => generator?
        (:state (second result)) => 7
        (:origin (second result)) => 8
        ))


(fact ":generator-reset creates a resets a :generator to its :origin"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:generator (list (make-generator 8 dec 351))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-reset) 
                    :generator))]
        (:state result) => 351
        (:origin result) => 351
        ))
