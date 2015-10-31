(ns push.instructions.base.conversion_test
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.base.conversion])
  (:use [push.instructions.base.integer])
  (:use midje.sweet)
  (:use [push.util.test-helpers]))

;; conversion 



(tabular
  (fact ":integer-fromboolean takes a :boolean value, and returns 1 if true, 0 if false"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :boolean    '(false true)    integer-fromboolean      :integer       '(0)
    :boolean    '(false true)    integer-fromboolean      :boolean       '(true)
    :boolean    '(true false)    integer-fromboolean      :integer       '(1)
    :boolean    '(true false)    integer-fromboolean      :boolean       '(false)
    ;; missing args 
    :boolean    '()              integer-fromboolean      :integer       '()
    :boolean    '()              integer-fromboolean      :boolean       '())


(tabular
  (fact ":integer-fromfloat takes a :float value, and truncates it to an :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :float    '(0.0)          integer-fromfloat      :integer       '(0)
    :float    '(0.1)          integer-fromfloat      :integer       '(0)
    :float    '(0.9)          integer-fromfloat      :integer       '(0)
    :float    '(22.22)        integer-fromfloat      :integer       '(22)
    ;; consumes arg
    :float    '(22.22)        integer-fromfloat      :float         '()
    ;; edge cases 
    :float    '(-0.0)         integer-fromfloat      :integer       '(0)
    :float    '(-0.1)         integer-fromfloat      :integer       '(0)
    :float    '(-22.22)       integer-fromfloat      :integer       '(-22)
    ;; missing args 
    :float    '()             integer-fromfloat      :integer       '()
    :float    '()             integer-fromfloat      :float         '())
