(ns push.instructions.base.boolean_test
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.util.test-helpers])
  (:use [push.instructions.base.boolean])
  (:use midje.sweet))


(tabular
  (fact ":boolean-and returns the binary AND over the top two :boolean values"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  boolean-and   :boolean     '(false)
    :boolean    '(false true)   boolean-and   :boolean     '(false)
    :boolean    '(true false)   boolean-and   :boolean     '(false)
    :boolean    '(true true)    boolean-and   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        boolean-and   :boolean     '(false)
    :boolean    '()             boolean-and   :boolean     '())


(tabular
  (fact ":boolean-not returns the binary NOT of the top :boolean value"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  boolean-not   :boolean     '(true false)
    :boolean    '(false true)   boolean-not   :boolean     '(true true)
    ;; missing args
    :boolean    '()             boolean-not   :boolean     '())


(tabular
  (fact ":boolean-or returns the binary OR over the top two :boolean values"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  boolean-or   :boolean     '(false)
    :boolean    '(false true)   boolean-or   :boolean     '(true)
    :boolean    '(true false)   boolean-or   :boolean     '(true)
    :boolean    '(true true)    boolean-or   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        boolean-or   :boolean     '(false)
    :boolean    '()             boolean-or   :boolean     '())


(tabular
  (fact ":boolean-xor returns the binary XOR over the top two :boolean values"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  boolean-xor   :boolean     '(false)
    :boolean    '(false true)   boolean-xor   :boolean     '(true)
    :boolean    '(true false)   boolean-xor   :boolean     '(true)
    :boolean    '(true true)    boolean-xor   :boolean     '(false)
    ;; missing args
    :boolean    '(false)        boolean-xor   :boolean     '(false)
    :boolean    '()             boolean-xor   :boolean     '())


(tabular
  (future-fact ":boolean-dup duplicates the top item from :boolean"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   boolean-dup      :boolean       '(false false true)
    :boolean    '(true)         boolean-dup      :boolean       '(true true)
    ;; missing args 
    :boolean    '()             boolean-dup      :boolean       '())
