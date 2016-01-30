(ns push.instructions.extra.random-numbers-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:require [push.util.code-wrangling :as u])
  (:use push.instructions.extra.random-numbers)
  )



(tabular
  (fact ":integer-uniform returns a random integer between 0 and the top :integer value"
    (prerequisite (rand-int anything) => 33)
    (register-type-and-check-instruction
      ?set-stack ?items random-numbers-module ?instruction ?get-stack) => ?expected)


    ?set-stack   ?items    ?instruction     ?get-stack     ?expected    
    :integer     '(77)     :integer-uniform     :integer       '(33)     
    :integer     '(8)      :integer-uniform     :integer       '(33)
    :integer     '(0)      :integer-uniform     :integer       '(0)
    :integer     '(-100)   :integer-uniform     :integer       '(-33))


(tabular
  (fact ":float-uniform returns a random float between 0 and the top :float value"
    (prerequisite (rand) => 0.5)
    (register-type-and-check-instruction
      ?set-stack ?items random-numbers-module ?instruction ?get-stack) => ?expected)


    ?set-stack   ?items    ?instruction     ?get-stack     ?expected    
    :float      '(50.0)    :float-uniform      :float         '(25.0)     
    :float      '(8.0)     :float-uniform      :float         '(4.0)
    :float      '(0.0)     :float-uniform      :float         '(0.0)
    :float      '(-100.0)  :float-uniform      :float         '(-50.0))

