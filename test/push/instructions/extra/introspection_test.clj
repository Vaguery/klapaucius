(ns push.instructions.extra.introspection-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:require [push.util.code-wrangling :as u])
  (:use push.instructions.extra.introspection)
  )


;;; some fixtures

(def simple-case 
  (i/register-type
    (i/make-classic-interpreter :inputs {:c 1 :b false} :counter 77)
    standard-introspection-module))



(tabular
  (fact ":push-counter pushes the current counter value to the :integer stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '()       :push-counter    :integer       '(77))


(tabular
  (fact ":push-inputs pushes the input names to the :code stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :code        '()       :push-inputs    :code          '((:b :c)))


(tabular
  (fact ":push-inputset pushes the input names to the :set stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :set          '()      :push-inputset    :set            '(#{:b :c}))


(def what-simple-case-knows (into #{} (keys (:instructions simple-case))))


(tabular
  (fact ":push-instructionset pushes the instruction names to the :set stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items ?instruction     ?get-stack   ?expected
    :set       '()    :push-instructionset    
                                       :set     (list what-simple-case-knows))
