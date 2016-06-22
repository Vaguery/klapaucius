(ns push.instructions.extra.introspection-test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t]
            [push.util.code-wrangling :as u]
            [push.interpreter.templates.one-with-everything :as o])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.type.module.introspection)
  )


;;; some fixtures

(def simple-case 
  (i/register-type
    (o/make-everything-interpreter :bindings {:c 1 :b false} :counter 77)
    standard-introspection-module))



(tabular
  (fact ":push-counter pushes the current counter value to the :scalar stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :scalar     '()       :push-counter    :scalar       '(77))


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
  (fact ":push-bindingcount pushes the number of binding keys to :scalar"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items ?instruction     ?get-stack   ?expected
    :scalar       '()    :push-bindingcount    
                                       :scalar     '(2))



(tabular
  (fact ":push-nthref pushes indexed binding key (after sorting) to :ref"
    (register-type-and-check-instruction-in-this-interpreter
      simple-case
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items   ?instruction     ?get-stack   ?expected
    :scalar     '(1)    :push-nthref    
                                         :ref         '(:c)
    :scalar     '(0)    :push-nthref    
                                         :ref         '(:b)
    :scalar     '(11)   :push-nthref    
                                         :ref         '(:c)
    :scalar     '(-21)  :push-nthref    
                                         :ref         '(:c))


(tabular
  (fact ":push-nthref works when no :bindings are defined"
    (register-type-and-check-instruction-in-this-interpreter
      (assoc simple-case :bindings {})
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items   ?instruction     ?get-stack   ?expected
    :scalar     '(1)    :push-nthref    
                                         :ref         '()
    :scalar     '(0)    :push-nthref    
                                         :ref         '()
    :scalar     '(11)   :push-nthref    
                                         :ref         '()
    :scalar     '(-21)  :push-nthref    
                                         :ref         '())


(def knows-things (assoc simple-case :bindings {:m '() :x '(2) :a '(1)}))

(tabular
  (fact ":push-refcycler creates a continuation to build a cycler"
    (register-type-and-check-instruction-in-this-interpreter
      knows-things
      ?set-stack ?items standard-introspection-module ?instruction ?get-stack) => ?expected)

    ?set-stack ?items   ?instruction     ?get-stack   ?expected
    :exec     '()       :push-refcycler    
                                         :exec       '((:push-bindings :code-cycler)))
