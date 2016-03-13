(ns push.instructions.extra.random-scalars-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t]
            [push.util.code-wrangling :as u])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.types.module.random-scalars)
  )



(tabular
  (fact ":integer-uniform returns a random integer between 0 and the top :integer value"
    (prerequisite (typesafe-rand-int anything) => ?diceroll)
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected         ?diceroll
    :integer     '(77)     :integer-uniform     :integer       '(33)           33
    :integer     '(0)      :integer-uniform     :integer       '(0)            812
    :integer     '(-100)   :integer-uniform     :integer       '(-13)          13
    )


(tabular
  (fact ":integer-uniform does not throw a java.lang.IllegalArgumentException error when called with a BigInt argument"
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) =not=>
          (throws))

    ?set-stack   ?items                                            ?instruction         ?get-stack
    :integer     '(10000000000000000000000000000000000000000000N)  :integer-uniform     :integer     
    :integer     '(115428383193968912)                             :integer-uniform     :integer     
    )




(tabular
  (fact ":float-uniform returns a random float between 0 and the top :float value"
    (prerequisite (rand) => ?diceroll)
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected     ?diceroll
    :float      '(50.0)    :float-uniform      :float         '(25.0)      0.5  
    :float      '(8.0)     :float-uniform      :float         '(2.0)       0.25
    :float      '(0.0)     :float-uniform      :float         '(0.0)       0.5
    :float      '(-100.0)  :float-uniform      :float         '(-26.0)     0.26)



(tabular
  (fact ":boolean-faircoin returns a random :boolean value"
    (prerequisite (rand) => ?diceroll )
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction         ?get-stack     ?expected     ?diceroll
    :boolean     '()       :boolean-faircoin    :boolean       '(true)          0.1
    :boolean     '()       :boolean-faircoin    :boolean       '(false)         0.9
    :boolean     '()       :boolean-faircoin    :boolean       '(false)         0.5
    :boolean     '()       :boolean-faircoin    :boolean       '(true)          0.0
    )
