(ns push.instructions.standard.set_test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.set])
  )


(fact "standard-set-type knows some instructions"
  (keys (:instructions standard-set-type)) =>
    (contains [:set-dup :set-print] :in-any-order :gaps-ok))


(tabular
  (fact ":set-union pushes the union of the top two :set items"
    (register-type-and-check-instruction
        ?set-stack ?items standard-set-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items             ?instruction  ?get-stack   ?expected
    :set        '(#{1 2} #{3 4})   :set-union    :exec       '(#{1 2 3 4})
    :set        '(#{1 2} #{2 4})   :set-union    :exec       '(#{1 2 4})
    :set        '(#{1 2} #{1 2})   :set-union    :exec       '(#{1 2})
    :set        '(#{} #{1})        :set-union    :exec       '(#{1})
    :set        '(#{1 2})          :set-union    :set        '(#{1 2})
    )


(tabular
  (fact ":set-intersection pushes the intersection of the top two :set items"
    (register-type-and-check-instruction
        ?set-stack ?items standard-set-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items             ?instruction  ?get-stack   ?expected
    :set        '(#{1 2} #{3 4})   :set-intersection
                                                  :exec        '(#{})
    :set        '(#{1 2} #{2 4})   :set-intersection
                                                  :exec        '(#{2})
    :set        '(#{1 2} #{1 2})   :set-intersection
                                                  :exec        '(#{1 2})
    :set        '(#{} #{})        :set-intersection
                                                  :exec        '(#{})
    :set        '(#{1 2})          :set-intersection
                                                  :set        '(#{1 2})
    )



(tabular
  (fact ":set-difference pushes the difference of the top two :set items"
    (register-type-and-check-instruction
        ?set-stack ?items standard-set-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items             ?instruction  ?get-stack   ?expected
    :set        '(#{1 2} #{3 4})   :set-difference
                                                  :exec        '(#{3 4})
    :set        '(#{1 2} #{2 4})   :set-difference
                                                  :exec        '(#{4})
    :set        '(#{1 2} #{1 2})   :set-difference
                                                  :exec        '(#{})
    :set        '(#{} #{})         :set-difference
                                                  :exec        '(#{})
    :set        '(#{1 2})          :set-difference
                                                  :set        '(#{1 2})
    )


(tabular
  (fact "`set-subset?` pushes true if the second item is a subset of the top one"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks standard-set-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:set     '(#{1 2} #{1 2 3})
     :boolean '()}             :set-subset?     {:set      '()
                                                 :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2} #{1 3})
     :boolean '()}             :set-subset?     {:set      '()
                                                 :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2} #{1 2})
     :boolean '()}             :set-subset?     {:set      '()
                                                 :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{} #{1 2 3})
     :boolean '()}             :set-subset?     {:set      '()
                                                 :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2} #{1})
     :boolean '()}             :set-subset?     {:set      '()
                                                 :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2})
     :boolean '()}             :set-subset?     {:set      '(#{1 2})
                                                 :boolean  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`set-superset?` pushes true if the second item is a superset of the top one"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks standard-set-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:set     '(#{1 2 3} #{1 2})
     :boolean '()}             :set-superset?     {:set      '()
                                                   :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2} #{1 2})
     :boolean '()}             :set-superset?     {:set      '()
                                                   :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2} #{})
     :boolean '()}             :set-superset?     {:set      '()
                                                   :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{} #{1 2 3})
     :boolean '()}             :set-superset?     {:set      '()
                                                   :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set     '(#{1 2})
     :boolean '()}             :set-superset?     {:set      '(#{1 2})
                                                   :boolean  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
