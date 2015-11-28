(ns push.instructions.standard.set_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.types.extra.set])
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
    :set        '(#{1 2} #{3 4})   :set-union    :set        '(#{1 2 3 4})
    :set        '(#{1 2} #{2 4})   :set-union    :set        '(#{1 2 4})
    :set        '(#{1 2} #{1 2})   :set-union    :set        '(#{1 2})
    :set        '(#{} #{1})        :set-union    :set        '(#{1})
    :set        '(#{1 2})          :set-union    :set        '(#{1 2})
    )


(tabular
  (fact ":set-intersection pushes the intersection of the top two :set items"
    (register-type-and-check-instruction
        ?set-stack ?items standard-set-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items             ?instruction  ?get-stack   ?expected
    :set        '(#{1 2} #{3 4})   :set-intersection
                                                  :set        '(#{})
    :set        '(#{1 2} #{2 4})   :set-intersection
                                                  :set        '(#{2})
    :set        '(#{1 2} #{1 2})   :set-intersection
                                                  :set        '(#{1 2})
    :set        '(#{} #{})        :set-intersection
                                                  :set        '(#{})
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
                                                  :set        '(#{3 4})
    :set        '(#{1 2} #{2 4})   :set-difference
                                                  :set        '(#{4})
    :set        '(#{1 2} #{1 2})   :set-difference
                                                  :set        '(#{})
    :set        '(#{} #{})         :set-difference
                                                  :set        '(#{})
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


(tabular
  (fact "`code->set` pops the top :code item; if it's not a list, it's made one, then the list is made into a set"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks standard-set-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:set  '()
     :code '((1 2 1 2))}       :code->set     {:set   '(#{1 2})
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :code '((1 (2 3)))}       :code->set     {:set   '(#{1 (2 3)})
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :code '([1 2 3])}         :code->set     {:set   '(#{[1 2 3]})
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :code '(2.3)}             :code->set     {:set   '(#{2.3})
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :code '(())}              :code->set     {:set   '(#{})    ;; note
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :code '()}                :code->set     {:set   '()
                                               :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector->set` pops the top :vector item and makes it into a set"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks standard-set-type ?instruction) => (contains ?expected))

    ?new-stacks                  ?instruction     ?expected

    {:set  '()
     :vector '([1 2 1 2])}       :vector->set     {:set   '(#{1 2})
                                                   :vector  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :vector '([1 (2 3)])}       :vector->set     {:set   '(#{1 (2 3)})
                                                   :vector  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :vector '([])}              :vector->set     {:set   '(#{})
                                                   :vector  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:set  '()
     :vector '()}                :vector->set     {:set   '()
                                                   :vector  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )