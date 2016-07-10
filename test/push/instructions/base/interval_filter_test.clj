(ns push.instructions.base.interval_filter_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.type.definitions.interval :as s])
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
  (fact ":scalars-remove keeps elements of a `:scalars` item that are within the `:interval`"
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
