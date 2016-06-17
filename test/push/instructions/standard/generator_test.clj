(ns push.instructions.standard.generator-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t]
            [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.generator])
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
  (fact ":generator-counter pops a :scalar and makes an incrementer from it"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks generator-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(17)
     :generator '()}           
                            :generator-counter       
                                                  {:scalar   '()
                                                   :generator (list (make-generator 17 inc'))} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '()
     :generator '()}           
                            :generator-counter       
                                                  {:scalar   '()
                                                   :generator '()} 
                                                   )


(fact ":generator-totalistic3 works with non-integer arguments by converting them to `bigint` values"
  (let [program '[1000000/7
                  :generator-totalistic3   ;; consumes scalar
                  :generator-again         ;; pushes state = 142857N
                  :generator-next          ;; increment totalistic3 = 745032
                  :generator-next          ;; 698523 ...
                  :generator-next          
                  :generator-next ] ]
    (push/get-stack (push/run (push/interpreter) program 1000) :scalar) => 
      '(76923 325018 698523 745032 142857N)
  ))



(fact ":generator-totalistic3 works with negative :scalar values too"
  (let [program '[-1000000.7
                  :generator-totalistic3   ;; consumes scalar
                  :generator-again         ;; pushes state = -1000000N
                  :generator-next          ;; increment totalistic3 = -1000011
                  :generator-next          ;; -1001232
                  :generator-next          ;; -1136763 ...
                  :generator-next ] ]
    (push/get-stack (push/run (push/interpreter) program 1000) :scalar) => 
      '(-5069605 -1136763 -1001232 -1000011 -1000000N)
  ))



(fact ":generator-stepper creates a new generator that jumps by increments"
  (let [gstep (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:scalar '(3 -8)})]

    (push.core/get-stack (i/execute-instruction gstep :generator-stepper) :scalar) => '()
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



(fact ":generator-jumpsome jumps ahead N steps, where N is the scalar mod 100"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:scalar '(712.7) :generator (list (make-generator 8 dec' 351))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-jumpsome) 
                    :generator))]
        (:state result) => -4  ;; note the jump was (mod 712.7 100) = 12N steps
        (:origin result) => 351
        ))




(fact ":generator-jumpsome is OK if the generator disappears"
  (let [g (push.interpreter.templates.one-with-everything/make-everything-interpreter
        :stacks {:scalar '(120) :generator
          (list 
            (make-generator 
              '(1 [2 3])
              (partial push.instructions.aspects.repeatable-and-cycling/dissect-step)
              '(1 [2 3])))})
        result  (first 
                  (push.core/get-stack 
                    (i/execute-instruction g :generator-jumpsome) 
                    :generator))]
        result => nil
        ))

