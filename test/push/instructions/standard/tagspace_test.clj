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
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupint` pops an :integer and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :integer '(3)
     :tagspace (list taggy)}    :tagspace-lookupint      {:exec     '(4)
                                                          :integer  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integer '(33)
     :tagspace (list taggy)}    :tagspace-lookupint      {:exec     '(2)
                                                          :integer  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :integer '(33)
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupint      {:exec     '()
                                                          :integer  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))


(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupfloat` pops an :float and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :float '(2.2)
     :tagspace (list taggy)}    :tagspace-lookupfloat    {:exec    '(4)
                                                          :float   '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :float '(33.0)
     :tagspace (list taggy)}    :tagspace-lookupfloat    {:exec    '(2)
                                                          :float   '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :float '(-33.0)
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupfloat    {:exec    '()
                                                          :float   '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



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
  [taggy1  (make-tagspace {1 2 3 4 5 6})
   taggy2  (make-tagspace {1 -2 3 -4 5.5 6.6})]

(tabular
  (fact "`:tagspace-merge` merges two tagspaces"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge          {:tagspace (list
                                                          (make-tagspace {5.5 6.6, 1 2, 3 4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 taggy1)}
                               :tagspace-merge          {:tagspace (list
                                                          (make-tagspace {5.5 6.6, 1 -2, 3 -4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
