(ns push.instructions.standard.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t]
            [push.util.numerics :as n])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.definitions.tagspace])
  (:use [push.type.item.tagspace])
  (:use [push.instructions.aspects])
  )




(tabular
  (fact ":tagspace-new pushes a new tagspace"
    (register-type-and-check-instruction
        ?set-stack ?items tagspace-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction    ?get-stack     ?expected
    :tagspace   '()         :tagspace-new    :exec    (list (make-tagspace)))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-count` pushes the number of stored items to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:tagspace (list taggy)}  :tagspace-count   {:exec  (list (list 3 taggy))
                                                 :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                              :tagspace-count   {:exec
                                                 (list (list 0 (make-tagspace)))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-keys` pushes the keys and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks         ?instruction       ?expected
    {:tagspace
      (list taggy)}     :tagspace-keys     {:exec  (list (list '(-4 2 6) taggy))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                        :tagspace-keys     {:exec  (list (list '() (make-tagspace)))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-keyset` pushes the keys (as a set) and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks         ?instruction       ?expected
    {:tagspace
      (list taggy)}     :tagspace-keyset     {:exec  (list (list #{-4 2 6} taggy))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                        :tagspace-keyset     {:exec  (list (list #{} (make-tagspace)))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-keyvector` pushes the keys (as a vector) and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks         ?instruction       ?expected
    {:tagspace
      (list taggy)}     :tagspace-keyvector     {:exec  (list (list [-4 2 6] taggy))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                        :tagspace-keyvector     {:exec  (list (list [] (make-tagspace)))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let
  [taggy (make-tagspace {8 1 6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-valuefilter` retains values present in a set"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:tagspace (list taggy)
     :set '(#{1 5})}          :tagspace-valuefilter
                                                       {:exec
                                                        (list
                                                          (make-tagspace {2 1, 6 5, 8 1}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{:a :b})}        :tagspace-valuefilter
                                                       {:exec
                                                        (list
                                                          (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{})}             :tagspace-valuefilter
                                                       {:exec
                                                        (list
                                                          (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))






(let
  [taggy (make-tagspace {8 1 6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-valueremove` removes values present in a set"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:tagspace (list taggy)
     :set '(#{2 3 4})}          :tagspace-valueremove
                                                       {:exec
                                                        (list
                                                          (make-tagspace {2 1,6 5,8 1}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{:a})}             :tagspace-valueremove
                                                       {:exec
                                                        (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{1})}              :tagspace-valueremove
                                                       {:exec
                                                        (list
                                                          (make-tagspace {-4 3, 6 5}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {8 1 6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-valuesplit` returns two :tagspaces"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:tagspace (list taggy)
     :set '(#{2 3 4})}      :tagspace-valuesplit
                                                      {:exec (list (list
                                                        (make-tagspace {-4 3})
                                                        (make-tagspace {2 1,6 5,8 1})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{1})}          :tagspace-valuesplit
                                                      {:exec (list (list
                                                        (make-tagspace {8 1,2 1})
                                                        (make-tagspace {6 5,-4 3})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy)
     :set '(#{:foo})}          :tagspace-valuesplit
                                                      {:exec (list (list
                                                        (make-tagspace)
                                                        taggy))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-values` pushes the values and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected

    {:tagspace (list taggy)}  :tagspace-values   {:exec (list (list '(3 1 5) taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                              :tagspace-values   {:exec (list (list '()
                                                          (make-tagspace)))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-valueset` pushes the values and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected

    {:tagspace (list taggy)}  :tagspace-valueset   {:exec (list (list #{3 1 5} taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                              :tagspace-valueset   {:exec (list (list #{}
                                                          (make-tagspace)))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-valuevector` pushes the values and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected

    {:tagspace (list taggy)}  :tagspace-valuevector   {:exec (list (list [3 1 5] taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                              :tagspace-valuevector   {:exec (list (list []
                                                          (make-tagspace)))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookup` pops a :scalar and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction          ?expected
    {:exec '()
     :scalar '(3)
     :tagspace (list taggy)}  :tagspace-lookup    {:exec (list (list taggy 4))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(6)
     :tagspace (list taggy)}    :tagspace-lookup  {:exec (list (list taggy 2))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(9.1)
     :tagspace (list taggy)}    :tagspace-lookup   {:exec (list (list taggy 6))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(5/2)
     :tagspace (list taggy)}    :tagspace-lookup  {:exec (list (list taggy 4))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar (list n/∞)
     :tagspace (list taggy)}    :tagspace-lookup  {:exec (list (list taggy 2))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar (list n/-∞)
     :tagspace (list taggy)}    :tagspace-lookup  {:exec (list (list taggy 2))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(-1007777778176487612847628374682734823M)
     :tagspace (list taggy)}    :tagspace-lookup  {:exec (list (list taggy 2))
                                                      :scalar  '()
                                                      :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(133)
     :tagspace (list (make-tagspace))}
                                :tagspace-lookup  {:exec (list (list (make-tagspace)))
                                                      :scalar '()
                                                      :tagspace '()
                                                      }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupscalars` pops a :scalars and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :scalars '([-3 0 3 6 9])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec  (list (list taggy (list 4 2 4 2 4)))
                                                          :scalars  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalars '([])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec  (list (list taggy ()))
                                                          :scalars  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalars '([-3 0 3 6 9])
     :tagspace (list (make-tagspace))}
                                :tagspace-lookupscalars  {:exec  (list (list (make-tagspace) '()))
                                                          :scalars  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let
  [taggy (make-tagspace {1M 2 3M 4})]
(tabular
  (fact "`:tagspace-lookupscalars` benefits unusually from the safety of `find-in-tagspace` stability"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :scalars '([1/3 2/7])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec (list (list taggy '(2 2)))
                                                          :scalars  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalars (list [13/3 n/∞])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec (list (list taggy '(2 2)))
                                                          :scalars  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2.2 3 4.4 5 6.6})]
(tabular
  (fact "`:tagspace-lookupvector` pops an :vector and a :tagspace and retrieves ALL the indicated items if they are numbers; non-numeric items are retained"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :vector '([-11 1 3 5 7 11 22])
     :tagspace (list taggy)}    :tagspace-lookupvector

              {:exec     (list (list taggy '(2.2 2.2 4.4 6.6 2.2 6.6 6.6)))
                                                          :vector  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([-11 :foo 1 :bar 3 [5 7 11 22]])
     :tagspace (list taggy)}    :tagspace-lookupvector

            {:exec (list (list  taggy '(2.2 :foo 2.2 :bar 4.4 [5 7 11 22])))
                                                          :vector  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([:foo :bar :baz])
     :tagspace (list taggy)}    :tagspace-lookupvector

                          {:exec     (list (list taggy '(:foo :bar :baz)))
                                                          :vector  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector (list [n/∞ n/-∞])
     :tagspace (list taggy)}    :tagspace-lookupvector

                                {:exec     (list (list taggy '(2.2 2.2)))
                                                          :vector  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([])
     :tagspace (list taggy)}    :tagspace-lookupvector

                                        {:exec     (list (list taggy '()))
                                                          :vector  '()
                                                          :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy1  (make-tagspace {1 2 3 4 5 6})
   taggy2  (make-tagspace {1 -2 3 -4 5.5 6.6})]

(tabular
  (fact "`:tagspace-merge` merges two tagspaces"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge    {:exec (list (make-tagspace
                                                    {5.5 6.6, 1 2, 3 4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 taggy1)}
                               :tagspace-merge    {:exec (list (make-tagspace
                                                    {5.5 6.6, 1 -2, 3 -4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 (make-tagspace))}
                               :tagspace-merge    {:exec (list (make-tagspace
                                                    {5.5 6.6, 1 -2, 3 -4}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let
  [taggy1  (make-tagspace {1M 2})
   taggy2  (make-tagspace {1/3 2/3})]

(tabular
  (fact "`:tagspace-merge` consumes arguments but captures runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge    {:exec '()
                                                   :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tag` pops a :scalar and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:foo '("bar")
     :scalar '(71.71)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '()
                                              :exec (list
                                                (assoc-in taggy
                                                          [:contents 71.71]
                                                          "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :scalar '(71.71)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '(71.71)
                                              :error '({:item ":foo-tag missing arguments" :step 0})
                                              :tagspace (list taggy)}
    ))





(let
  [foo-type     (make-taggable (t/make-type :foo))]

(tabular
  (fact "`:foo-tagstack` makes a new :tagspace from the :foo stack"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:foo '(9 9 9 9 9 9)}    :foo-tagstack   {:foo '(9 9 9 9 9 9)
                                              :exec (list
                                                (make-tagspace
                                                  {0 9 1 9 2 9 3 9 4 9 5 9}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()}               :foo-tagstack   {:foo '()
                                              :exec (list
                                                (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {}                       :foo-tagstack   {:foo '()
                                              :exec (list
                                                (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))







(let
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace {1M 2})]

(tabular
  (fact "`:foo-tag` consumes arguments but reports runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:foo '("bar")
     :scalar '(1/7)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '()
                                              :tagspace '()
                                              :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let
  [foo-type     (make-taggable (t/make-type :foo))
   giant-foo    (take 130000 (repeat 1))
   taggy        (make-tagspace {0 giant-foo})
   ]

(tabular
  (fact "`:foo-tag` fails when the result would be oversized"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:foo (list giant-foo)
     :scalar '(1)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '()
                                              :error '({:item "foo-tag failed: oversized result", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-max` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:tagspace (list taggy)}    :tagspace-max  {:exec  (list (list 6 taggy))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                                :tagspace-max  {:exec  (list (make-tagspace))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-min` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    {:tagspace (list taggy)}    :tagspace-min    {:exec (list (list -4 taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}
                                :tagspace-min    {:exec (list (make-tagspace))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-offset` pops a :scalar and a :tagspace and moves the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected
    {:scalar '(17)
     :tagspace (list taggy)}  :tagspace-offset     {:scalar  '()
                                                    :exec (list (make-tagspace
                                                      {18 2, 20 4, 22 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-121)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :exec (list (make-tagspace
                                                      {-120 2, -118 4, -116 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3/2)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :exec (list (make-tagspace
                                                      {5/2 2, 9/2 4, 13/2 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-12.345M)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :exec (list (make-tagspace
                                                      {-11.345M 2,
                                                        -9.345M 4,
                                                        -7.345M 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :exec (list (make-tagspace
                                                      {1 2 3 4 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1/3 2 3/5 4 5/7 6})]
(tabular
  (fact "`:tagspace-offset` creates an `:error` if the arguments cause a runtime error"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected
    {:scalar '(17M)
     :tagspace (list taggy)}  :tagspace-offset     {:scalar  '()
                                                    :tagspace '()
                                                    :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-scale` pops a :scalar and a :tagspace and scales all the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar '(3)
     :tagspace (list taggy)}   :tagspace-scale   {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {3 2, 9 4, 15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-3)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {-3 2, -9 4, -15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(1/3)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {1/3 2, 5/3 6, 1 4}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-0.25)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {-1.25 6, -0.75 4, -0.25 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar '()
                                                  :exec (list (make-tagspace
                                                    {0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1/3 2 3/7 4 5/9 6})]
(tabular
  (fact "`:tagspace-scale` catches runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar '(3M)
     :tagspace (list taggy)}   :tagspace-scale   {:scalar  '()
                                                  :tagspace '()
                                                  :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-cutoff` pops a :scalar and a :tagspace and produces two new tagspaces (pushed to :exec)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:scalar '(3)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2})
                                                  (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(4)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2 3 4})
                                                  (make-tagspace {5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(5/2)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2})
                                                  (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-22)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {})
                                                  (make-tagspace {1 2 3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(77123)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2 3 4 5 6})
                                                  (make-tagspace {})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let
  [taggy (make-tagspace {1M 2})]
(tabular
  (fact "`:tagspace-cutoff` consumes arguments but captures runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:scalar '(1/3)
     :tagspace (list taggy)}  :tagspace-cutoff  {:scalar  '()
                                                :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidy` pops two :scalars and a :tagspace and cleans up the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:scalar '(321 2/3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {2/3 2, 965/6 4, 321N 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2.7 3.7)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {-2.7 6, 0.5 4, 3.7 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3 3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {3 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3/5 1/5)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {1/5 2, 2/5 4, 3/5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2 3.75)
     :tagspace (list (make-tagspace {}))}
                                :tagspace-tidy   {:scalar  '()
                                                  :exec (list (make-tagspace {}))}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidy` consumes arguments but reports runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:scalar '(321M 2/3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace '()
                                                  :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-normalize` pops a :tagspace and cleans up the keys by setting the first to 0, and the rest to the following integer values"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:tagspace (list taggy)}    :tagspace-normalize
                                                 {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {0 2, 1 4, 2 6}))}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace {9 2 17 4 -2 6}))}
                                :tagspace-normalize
                                                 {:scalar  '()
                                                  :exec (list (make-tagspace
                                                    {0 6, 1 2, 2 4}))}

    ))
