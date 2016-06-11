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
     :integer '(2 7)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {7 1, 9 2, 11 3, 13 4, 15 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([])
     :integer '(2 7)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4])
     :integer '(-4 7)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace 
                                                              {-5 4, -1 3, 3 2, 7 1}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '([1 2 3 4])
     :integer '(0 7)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace (list
                                                            (make-tagspace {7 4}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
))



(let 
  [foo-type     (t/make-type :foo)
   fixed-foo    (t/attach-instruction foo-type (to-tagspaceint foo-type))]
(tabular
  (fact "`:foo->tagspaceint` generates an error if that keys fall out of range"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks fixed-foo ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:foo '([1 2 3 4 5])
     :integer (list Long/MAX_VALUE 2)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace '()
                                                          :error '({:item "foo->tagspaceint out of bounds", :step 0})}

    {:foo '([1 2 3 4 5])
     :integer (list Long/MIN_VALUE -2)
     :tagspace '()}            :foo->tagspaceint            {:foo '()
                                                          :integer '()
                                                          :tagspace '()
                                                          :error '({:item "foo->tagspaceint out of bounds", :step 0})}))


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
                                                              {2.5 \h, 3.75 \e,
                                                               5.0 \l, 6.25 \l,
                                                               7.5 \o}))}
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
                                                              {4.5 \e, 6.0 \v,
                                                               7.5 \i, 9.0 \l}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '("fire")
     :float '(0.0 7.25)
     :tagspace '()}            :foo->tagspacefloat            {:foo '()
                                                          :float '()
                                                          :tagspace (list
                                                            (make-tagspace {7.25 \e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
))
