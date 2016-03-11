(ns push.instructions.standard.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.extra.tagspace])
  (:use [push.instructions.aspects])
  )




(tabular
  (fact ":tagspace-new pushes a new tagspace"
    (register-type-and-check-instruction
        ?set-stack ?items tagspace-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction    ?get-stack     ?expected
    :tagspace   '()         :tagspace-new    :tagspace    (list (make-tagspace)))




(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tagwithinteger` pops an :integer and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '("bar")
     :integer '(71)
     :tagspace (list taggy)}    :foo-tagwithinteger      {:foo      '()
                                                          :integer  '()
                                                          :tagspace (list (assoc-in taggy [:contents 71] "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :integer '(71)
     :tagspace (list taggy)}    :foo-tagwithinteger      {:foo      '()
                                                          :integer  '(71)
                                                          :tagspace (list taggy)}
    ))



(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tagwithfloat` pops an :integfloater and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '("bar")
     :float '(71.71)
     :tagspace (list taggy)}    :foo-tagwithfloat      {:foo      '()
                                                          :float  '()
                                                          :tagspace (list (assoc-in taggy [:contents 71.71] "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :float '(71.71)
     :tagspace (list taggy)}    :foo-tagwithfloat      {:foo      '()
                                                          :float  '(71.71)
                                                          :tagspace (list taggy)}
    ))
