(ns push.instructions.extra.introspection-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t]
            [push.util.code-wrangling :as u]
            [push.interpreter.templates.classic :as c])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.types.module.introspection)
  )


;;; some fixtures

(def simple-case 
  (i/register-type
    (c/classic-interpreter :bindings {:c 1 :b false} :counter 77)
    standard-introspection-module))



(tabular
  (fact ":push-counter pushes the current counter value to the :integer stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '()       :push-counter    :integer       '(77))


(tabular
  (fact ":push-bindings pushes the binding names to the :code stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :code        '()       :push-bindings    :code          '((:b :c)))


(tabular
  (fact ":push-bindingset pushes the binding names to the :set stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :set          '()      :push-bindingset    :set            '(#{:b :c}))


(def what-simple-case-knows (into #{} (keys (:instructions simple-case))))


(tabular
  (fact ":push-instructionset pushes the instruction names to the :set stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items ?instruction     ?get-stack   ?expected
    :set       '()    :push-instructionset    
                                       :set     (list what-simple-case-knows))


(tabular
  (fact ":push-bindingcount pushes the number of binding keys to :integer"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items ?instruction     ?get-stack   ?expected
    :integer       '()    :push-bindingcount    
                                       :integer     '(2))
