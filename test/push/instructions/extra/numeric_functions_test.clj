(ns push.instructions.extra.numeric-functions-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:require [push.util.code-wrangling :as u])
  (:use push.instructions.extra.numeric-functions)
  )



; (tabular
;   (fact ":integer-uniform returns a random integer between 0 and the top :integer value"
;     (prerequisite (rand-int anything) => ?diceroll)
;     (register-type-and-check-instruction
;       ?set-stack ?items random-scalars-module ?instruction ?get-stack) => ?expected)

;     ?set-stack   ?items    ?instruction     ?get-stack     ?expected         ?diceroll
;     :integer     '(77)     :integer-uniform     :integer       '(33)           33
;     :integer     '(8)      :integer-uniform     :integer       '(2)            2
;     :integer     '(0)      :integer-uniform     :integer       '(0)            812
;     :integer     '(-100)   :integer-uniform     :integer       '(-13)          13)


