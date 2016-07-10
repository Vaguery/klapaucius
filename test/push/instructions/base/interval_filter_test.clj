(ns push.instructions.base.interval_filter_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.type.definitions.interval :as s])
  (:require [push.type.definitions.tagspace :as ts])
  (:use [push.type.item.interval])
  (:use [push.util.numerics :only [∞,-∞]])
  )



(tabular
  (fact ":scalars-filter keeps elements of a `:scalars` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalars  '([1 2 3 4 5])}
                             :scalars-filter            {:scalars '([2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-filter            {:scalars '([3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-filter            {:scalars '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  (list [1 ∞ 3 4 5])}
                             :scalars-filter            {:scalars '([1 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3))
     :scalars  (list [1 2 3 4 5])}
                             :scalars-filter            {:scalars '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :scalars  '([1 2 3 4 5])}
                             :scalars-filter            {:scalars '([1 2 3 4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )






(tabular
  (fact ":scalars-remove deletes elements of a `:scalars` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalars  '([1 2 3 4 5])}
                             :scalars-remove            {:scalars '([1 4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-remove            {:scalars '([1 2 4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-remove            {:scalars '([4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  (list [1 ∞ 3 4 5])}
                             :scalars-remove            {:scalars (list [∞ 4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3))
     :scalars  (list [1 2 3 4 5])}
                             :scalars-remove            {:scalars '([1 2 3 4 5])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :scalars  '([1 2 3 4 5])}
                             :scalars-remove            {:scalars '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":scalars-split keeps elements of a `:scalars` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalars  '([1 2 3 4 5])}
                             :scalars-split            {:exec '(([2 3] [1 4 5]))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-split            {:exec '(([3] [1 2 4 5]))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  '([1 2 3 4 5])}
                             :scalars-split            {:exec '(([1 2 3] [4 5]))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :scalars  (list [1 ∞ 3 4 5])}
                             :scalars-split            {:exec (list (list [1 3] [∞ 4 5]))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3))
     :scalars  (list [1 2 3 4 5])}
                             :scalars-split            {:exec '(([] [1 2 3 4 5]))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :scalars  '([1 2 3 4 5])}
                             :scalars-split            {:exec '(([1 2 3 4 5] []))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":tagspace-filter deletes elements of a `:tagspace` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {2 :b 3 :c}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {3 :c}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a ∞ :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a 3 :c}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-filter          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c 4 :d 5 :e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )






(tabular
  (fact ":tagspace-remove deletes elements of a `:tagspace` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a  4 :d 5 :e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a 2 :b 4 :d 5 :e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {4 :d 5 :e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a ∞ :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {4 :d 5 :e ∞ :b}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c 4 :d 5 :e}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-remove          {:tagspace (list
                                                         (ts/make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":tagspace-split deletes elements of a `:tagspace` item that are within the `:interval`"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace
                                                          {2 :b 3 :c})
                                                        (ts/make-tagspace
                                                          {1 :a  4 :d 5 :e})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace
                                                          {3 :c})
                                                        (ts/make-tagspace
                                                          {1 :a 2 :b  4 :d 5 :e})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c})
                                                        (ts/make-tagspace
                                                          { 4 :d 5 :e})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a ∞ :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace
                                                          {1 :a 3 :c})
                                                        (ts/make-tagspace
                                                          { 4 :d 5 :e ∞ :b })))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 -3 :min-open? true))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace)
                                                        (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c 4 :d 5 :e})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -∞ ∞))
     :tagspace (list (ts/make-tagspace {1 :a 2 :b 3 :c 4 :d 5 :e}))}
                             :tagspace-split          {:exec (list (list
                                                        (ts/make-tagspace
                                                          {1 :a 2 :b 3 :c 4 :d 5 :e})
                                                        (ts/make-tagspace)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )