(ns push.instructions.extra.numeric-scaling-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t]
            [push.util.code-wrangling :as u])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.instructions.extra.numeric-scaling)
  )


;;; a fixture


(tabular
  (fact ":integer-few reduces the top :integer mod 10"
    (register-type-and-check-instruction
      ?set-stack ?items numeric-scaling-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(77)     :integer-few     :integer       '(7)
    :integer     '(-2)     :integer-few     :integer       '(8)
    :integer     '(8)      :integer-few     :integer       '(8)
    :integer     '(0)      :integer-few     :integer       '(0))


(tabular
  (fact ":integer-some reduces the top :integer mod 100"
    (register-type-and-check-instruction
      ?set-stack ?items numeric-scaling-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(677)    :integer-some    :integer       '(77)
    :integer     '(-2912)  :integer-some    :integer       '(88)
    :integer     '(79)     :integer-some    :integer       '(79)
    :integer     '(0)      :integer-some    :integer       '(0))


(tabular
  (fact ":integer-many reduces the top :integer mod 1000"
    (register-type-and-check-instruction
      ?set-stack ?items numeric-scaling-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(2677)    :integer-many    :integer       '(677)
    :integer     '(-22212)  :integer-many    :integer       '(788)
    :integer     '(79)      :integer-many    :integer       '(79)
    :integer     '(0)       :integer-many    :integer       '(0))


(tabular
  (fact ":integer-lots reduces the top :integer mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items numeric-scaling-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(32677)   :integer-lots    :integer       '(2677)
    :integer     '(-22212)  :integer-lots    :integer       '(7788)
    :integer     '(79)      :integer-lots    :integer       '(79)
    :integer     '(0)       :integer-lots    :integer       '(0))
