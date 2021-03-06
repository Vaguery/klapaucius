(ns push.instructions.extra.random-scalars-test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t]
            [push.util.code-wrangling :as u])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.type.module.random-scalars)
  )



(tabular
  (fact ":integer-uniform returns a random integer between 0 and the top :scalar value"
    (prerequisite (rand 77/8) => 4.3)
    (prerequisite (rand 0)  => 0)
    (prerequisite (rand -10000.2) => -13.13)
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items        ?instruction       ?get-stack     ?expected
    :scalar      '(77/8)       :integer-uniform   :exec          '(4)
    :scalar      '(0)          :integer-uniform   :exec          '(0)
    :scalar      '(-10000.2)   :integer-uniform   :exec          '(-13)
    )


(tabular
  (fact ":integer-uniform creates an :error when called with an out-of-bounds argument"
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items          ?instruction         ?get-stack     ?expected

    :scalar    '(10000000000000000000000000000000000000000000N)
                                 :integer-uniform     :exec         '()
    :scalar    '(10000000000000000000000000000000000000000000N)
                                 :integer-uniform     :error          '({:item ":integer-uniform argument out of range", :step 0})

    :scalar    (list (+' Long/MIN_VALUE -1))
                                 :integer-uniform     :exec         '()
    :scalar    (list (+' Long/MIN_VALUE -1))
                                 :integer-uniform     :error          '({:item ":integer-uniform argument out of range", :step 0})
    )




(tabular
  (fact ":float-uniform returns a random float between 0 and the top :scalar value"
    (prerequisite (rand anything) => ?diceroll)
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected     ?diceroll
    :scalar     '(50.0)    :float-uniform      :exec       '(25.0)      25.0
    :scalar     '(8.0)     :float-uniform      :exec       '(2.0)       2.0
    :scalar     '(0.0)     :float-uniform      :exec       '(0.0)       0.0
    :scalar     '(-100.0)  :float-uniform      :exec       '(-26.0)     -26.0)



(tabular
  (fact ":float-uniform creates an :error when called with an out-of-bounds argument"
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items          ?instruction         ?get-stack     ?expected

    :scalar    '(1.0e861M)
                                 :float-uniform     :exec        '()
    :scalar    '(1.0e861M)
                                 :float-uniform     :error          '({:item ":float-uniform argument out of range", :step 0})

    :scalar    (list Double/MAX_VALUE)
                                 :float-uniform     :exec        '()
    :scalar    (list Double/MAX_VALUE)
                                 :float-uniform     :error          '({:item ":float-uniform argument out of range", :step 0})
    )



(tabular
  (fact ":boolean-faircoin returns a random :boolean value"
    (prerequisite (rand) => ?diceroll )
    (register-type-and-check-instruction
      ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction         ?get-stack     ?expected     ?diceroll
    :boolean     '()       :boolean-faircoin    :exec         '(true)          0.1
    :boolean     '()       :boolean-faircoin    :exec         '(false)         0.9
    :boolean     '()       :boolean-faircoin    :exec         '(false)         0.5
    :boolean     '()       :boolean-faircoin    :exec         '(true)          0.0
    )
