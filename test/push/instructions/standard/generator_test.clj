(ns push.instructions.standard.generator-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.extra.generator])
  )


(fact ":generator-again creates a list of the current state and the unchanged generator, pushed to :exec"
  (let [gstep (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:generator (list (make-generator 8 dec))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction gstep :generator-again) 
                    :exec))]
        (first result) => 8
        (second result) => generator?
        (:state (second result)) => 8
        (:origin (second result)) => 8
        ))


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


(fact ":generator-next will return `nil` if the generator disappears during update"
  (let [gstep (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:generator (list (make-generator 8 (constantly nil)))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction gstep :generator-next) 
                    :exec))]
        result => nil
        ))



(fact ":generator-reset resets a :generator to its :origin"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:generator (list (make-generator 8 dec 351))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-reset) 
                    :generator))]
        (:state result) => 351
        (:origin result) => 351
        ))



(fact ":generator-jump jumps ahead N steps"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:integer '(711) :generator (list (make-generator 8 dec 351))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-jump) 
                    :generator))]
        (:state result) => -3  ;; note the jump was (mod 711 100) steps
        (:origin result) => 351
        ))


(fact ":generator-jump is OK if the generator disappears"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:integer '(120) :generator
          (list 
            (make-generator 
              '(1 [2 3])
              (partial push.instructions.aspects.repeatable-and-cycling/dissect-step)
              '(1 [2 3])))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-jump) 
                    :generator))]
        result => nil
        ))

