(ns push.instructions.standard.to_tagspace_test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.aspects])
  (:use [push.type.definitions.tagspace])
  (:use push.type.item.tagspace)
  (:use push.instructions.aspects.to-tagspace)
  )


(let
  [foo-type     (t/make-type :foo)
   fixed-foo    (t/attach-instruction foo-type (to-tagspace foo-type))]
(tabular
  (fact "`:foo->tagspace` pops a :foo and two :scalar values, and makes a new :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks fixed-foo ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '([1 2 3 4 5])
     :scalar '(7 2)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {2 1, 13/4 2, 9/2 3, 23/4 4, 7 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :scalar '(2 7)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {7 1, 13/4 4, 9/2 3, 23/4 2, 2 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :scalar '(7 8)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {8 1, 29/4 4, 15/2 3, 31/4 2, 7 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :scalar '(8 8)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace {8 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4 5])
     :scalar '(10.5 5/2)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace {5/2 1, 4.5 2, 6.5 3, 8.5 4, 10.5 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    {:foo '("hello")
     :scalar '(1.25 2.5)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {1.25 \o, 1.5625 \l, 1.875 \l, 2.1875 \e, 2.5 \h}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("")
     :scalar '(2.0 8.0)
     :tagspace '()}            :foo->tagspace                 {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("live")
     :scalar '(-1.5 9.0)
     :tagspace '()}            :foo->tagspace                 {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {-1.5 \e, 2.0 \v, 5.5 \i, 9.0 \l}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("fire")
     :scalar '(10.25 7.25)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {7.25 \f, 8.25 \i, 9.25 \r, 10.25 \e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("fire")
     :scalar '(10.25 10.25)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list
                                                            (make-tagspace
                                                              {10.25 \e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
))





(let
  [foo-type     (t/make-type :foo)
   fixed-foo    (t/attach-instruction foo-type (to-tagspace foo-type))]
(tabular
  (fact "`:foo->tagspace` consumes arguments but reports runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks fixed-foo ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '([1 2 3 4 5])
     :scalar '(7M 2/3)
     :tagspace '()}            :foo->tagspace            {:foo '()
                                                          :scalar '()
                                                          :exec (list (make-tagspace))
                                                          :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))
