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
