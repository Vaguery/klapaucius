(ns push.instructions.standard.to_tagspace_test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.aspects])
  (:use push.types.type.tagspace)
  (:use push.instructions.aspects.to-tagspace)
  )


(let 
  [foo-type     (t/make-type :foo)
   fixed-foo    (t/attach-instruction foo-type (to-tagspaceint foo-type))]
(tabular
  (fact "`:foo->tagspaceint` pops a :foo and two :integer values, and makes a new :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks fixed-foo ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '([1 2 3 4 5])
     :integer '(7 2)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {2 1, 13/4 2, 9/2 3, 23/4 4, 7 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :integer '(2 7)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {7 1, 13/4 4, 9/2 3, 23/4 2, 2 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :integer '(7 8)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {8 1, 29/4 4, 15/2 3, 31/4 2, 7 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :integer '(8 8)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace {8 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
))



(let 
  [foo-type     (t/make-type :foo)
   fixed-foo    (t/attach-instruction foo-type (to-tagspacefloat foo-type))]
(tabular
  (fact "`:foo->tagspacefloat` pops a :foo and two :integer values, and makes a new :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks fixed-foo ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '("hello")
     :float '(1.25 2.5)
     :tagspace '()}            :foo->tagspacefloat       {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {1.25 \o, 1.5625 \l, 1.875 \l, 2.1875 \e, 2.5 \h}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("")
     :float '(2.0 8.0)
     :tagspace '()}            :foo->tagspacefloat            {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("live")
     :float '(-1.5 9.0)
     :tagspace '()}            :foo->tagspacefloat            {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {-1.5 \e, 2.0 \v, 5.5 \i, 9.0 \l}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("fire")
     :float '(10.25 7.25)
     :tagspace '()}            :foo->tagspacefloat            {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace
                                                              {7.25 \f, 8.25 \i, 9.25 \r, 10.25 \e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("fire")
     :float '(10.25 10.25)
     :tagspace '()}            :foo->tagspacefloat            {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace
                                                              {10.25 \e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
))
