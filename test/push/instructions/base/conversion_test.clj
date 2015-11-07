(ns push.instructions.base.conversion_test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.base.conversion])
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
    ;; range
    :float    '(-22222222222222222222222222222.3333333333M)
                              integer-fromfloat      :integer       '(-22222222222222222222222222222N)
    ;; missing args 
    :float    '()             integer-fromfloat      :integer       '()
    :float    '()             integer-fromfloat      :float         '())




(tabular
  (fact ":integer-fromchar takes a :char value, and converts it to an :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :char    '(\0)           integer-fromchar      :integer       '(48)
    :char    '(\r)           integer-fromchar      :integer       '(114)
    :char    '(\newline)     integer-fromchar      :integer       '(10)
    :char    '(\uF021)       integer-fromchar      :integer       '(61473)
    ;; consumes arg
    :char    '(\0)           integer-fromchar      :char          '()
    ;; missing args 
    :char    '()             integer-fromchar      :integer       '()
    :char    '()             integer-fromchar      :char          '())


