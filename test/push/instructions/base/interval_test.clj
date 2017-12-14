(ns push.instructions.base.interval_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.type.definitions.interval :as s])
  (:use [push.type.item.interval])
  (:use [push.util.numerics :only [∞,-∞]])
  )


(tabular
  (fact ":interval-add returns the sum of two intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-interval 4 6))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval -3 -1))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-interval -1 2))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -2 3)
                       (s/make-interval -3 2))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-interval -5 5))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-open-interval 4 6))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval -3 -2))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-open-interval -1 1))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 3 3 :min-open? true)
                       (s/make-interval 2 2 :max-open? true))
                             :interval-add
                                                 :interval  (list)

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )

(tabular
  (fact ":interval-add treats empty intervals as if they were zero"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 2 :min-open? true))
                             :interval-add
                                                 :interval  (list
                                                              (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )






(tabular
  (fact ":interval-crossover returns the FOIL crossovers of two intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 3 4)
                       (s/make-interval 1 2))

                             :interval-crossover

                                                :exec
                                                    (list
                                                      (list (s/make-interval 1 3)
                                                            (s/make-interval 1 4)
                                                            (s/make-interval 2 3)
                                                            (s/make-interval 2 4)
                                                            ))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 3 4)
                       (s/make-interval 1 2))

                             :interval-crossover

                                                :exec
                                                    (list
                                                      (list (s/make-interval 1 3
                                                               :max-open? true)
                                                            (s/make-interval 1 4
                                                               :max-open? true)
                                                            (s/make-interval 2 3
                                                               :max-open? true)
                                                            (s/make-interval 2 4
                                                               :max-open? true)
                                                            ))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 33 -12 )
                       (s/make-interval 1 2 :max-open? true))
                       ;; [1,2) x (-12,33)

                             :interval-crossover

                                                :exec
                                                    (list
                                                      (list (s/make-interval -12 1
                                                               :max-open? true)
                                                            (s/make-interval 1 33
                                                               :max-open? true)
                                                            (s/make-open-interval -12 2)
                                                            (s/make-open-interval 2 33)
                                                            ))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 33 -12 :min-open? true)
                       (s/make-interval 1 2 :max-open? true))
                       ;; [1,2) x (-12,33]

                             :interval-crossover

                                                :exec
                                                    (list
                                                      (list (s/make-interval -12 1
                                                              :max-open? true)
                                                            (s/make-interval 1 33)
                                                            (s/make-open-interval -12 2)
                                                            (s/make-interval 2 33
                                                              :min-open? true)
                                                            ))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-divide returns a continuation to produce the quotient of two simple intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-divide
                                                 :exec  (list
             (list (s/make-interval 2 3) (s/make-interval 1/3 1/2) :interval-multiply))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval -2 -4))
                             :interval-divide
                                                 :exec  (list
             (list (s/make-interval -2 -4) (s/make-interval 1/3 1/2) :interval-multiply))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-divide works for zero-ended intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval -2 0)
                       (s/make-interval 2 3))
                             :interval-divide
                                                 :exec  (list
             (list (s/make-interval 2 3) (s/make-interval -∞ -1/2) :interval-multiply))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 0 3)
                       (s/make-interval -2 -4))
                             :interval-divide
                                                 :exec  (list
             (list (s/make-interval -2 -4) (s/make-interval 1/3 ∞) :interval-multiply))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-divide works for zero-spanning intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval -2 3)
                       (s/make-interval 2 3))
                             :interval-divide
                                                 :exec  (list
            (list
              (s/make-interval 2 3)
              (s/make-interval -∞ -1/2)
              :interval-multiply
              (s/make-interval 2 3)
              (s/make-interval 1/3 ∞)
              :interval-multiply))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-empty? returns true if the start and end are identical and at least one is open"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3))
                             :interval-empty?     :boolean          '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 7 7))
                             :interval-empty?     :boolean          '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 7 7 :min-open? true))
                             :interval-empty?     :boolean          '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-hull returns a new interval overlapping both its args"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 1 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-interval 1 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 12 13))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-interval 2 13))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-open-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval -2 -3)
                       (s/make-open-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-open-interval -3 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval -2 -2)
                       (s/make-open-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-open-interval -2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3 :max-open? true)
                       (s/make-open-interval 2 3))
                             :interval-hull
                                                 :interval  (list
                                                              (s/make-interval 2 3 :max-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )





(tabular
  (fact ":interval-include? says whether a :scalar falls in a :interval"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(2)}           :interval-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(4)}           :interval-include?            {:boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(2.7)}         :interval-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalar  '(2)}           :interval-include?            {:boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalar  '(3)}           :interval-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :max-open? true))
     :scalar  '(3)}           :interval-include?            {:boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-open-interval 3 3))
     :scalar  '(3)}           :interval-include?            {:boolean '(false)}
    )



(tabular
  (fact ":interval-intersection returns a new interval that is the intersection of its args, or nil"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 1 3))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 12 13))
                             :interval-intersection
                                                 :interval  '()
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-open-interval 2 3))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval -2 -3)
                       (s/make-open-interval 2 3))
                             :interval-intersection
                                                 :interval  '()
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 3)
                       (s/make-interval 3 4))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-interval 3 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3 :max-open? true)
                       (s/make-open-interval 2 3))
                             :interval-intersection
                                                 :interval  (list
                                                              (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )




(tabular
  (fact ":interval-min pushes expected result"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 7 9))
                             :interval-min
                                                 :scalar  '(7)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -27 9))
                             :interval-min
                                                 :scalar  '(-27)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -∞ 9))
                             :interval-min
                                                 :scalar  (list -∞)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-max pushes expected result"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 7 9))
                             :interval-max
                                                 :scalar  '(9)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -27 9))
                             :interval-max
                                                 :scalar  '(9)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval ∞ 9))
                             :interval-max
                                                 :scalar  (list ∞)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )






(tabular
  (fact ":interval-multiply returns the product of two intervals' endpoints"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-interval 4 9))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval -3 -1))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-interval -9 -2))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-open-interval 4 9))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval -3 -2))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-open-interval -9 -4))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2 :min-open? true)
                       (s/make-interval 3 4 :max-open? true))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-open-interval 3 8))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2 :max-open? true)
                       (s/make-interval 3 4 :max-open? true))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-interval 3 8
                                                                :max-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2)
                       (s/make-interval 3 4 :min-open? true))
                             :interval-multiply
                                                 :interval  (list
                                                              (s/make-interval 3 8
                                                                :min-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":interval-multiply returns `nil` if either argument is empty"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 3 4)
                       (s/make-interval 1 1 :max-open? true))
                             :interval-multiply
                                                 :interval  (list)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 3 4)
                       (s/make-interval 1 1 :min-open? true))
                             :interval-multiply
                                                 :interval  (list)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-new builds one out of two :scalar values"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction     ?get-stack  ?expected

    :scalar    '(2 3)        :interval-new     :interval  (list (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :scalar    '(3 2)        :interval-new     :interval  (list (s/make-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :scalar    '(3 3)        :interval-new     :interval  (list (s/make-interval 3 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-newopen builds one out of two :scalar values"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction     ?get-stack  ?expected

    :scalar    '(2 3)   :interval-newopen   :interval  (list (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :scalar    '(3 2)   :interval-newopen   :interval  (list (s/make-open-interval 2 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :scalar    '(3 3)   :interval-newopen   :interval  (list (s/make-open-interval 3 3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-rebracket resets the brackets to two boolean values"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :boolean  '(false false)}
                              :interval-rebracket    {:interval
                                                      (list (s/make-interval 2 3))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :boolean  '(false true)}
                              :interval-rebracket    {:interval
                                                      (list (s/make-interval 2 3 :min-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :boolean  '(true true)}
                              :interval-rebracket    {:interval
                                                      (list (s/make-open-interval 2 3 ))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :boolean  '(true false)}
                              :interval-rebracket    {:interval
                                                      (list (s/make-interval 2 3 :max-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )







(tabular
  (fact ":interval-overlap? returns the true if the top interval shares even one point with the second"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3) (s/make-interval 3 4))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3) (s/make-open-interval 3 4))
                             :interval-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3 :min-open? true) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3) (s/make-open-interval 2 3))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 3 3) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 3 3) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 3 3 :min-open? true) (s/make-interval 2 3))
                             :interval-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 5) (s/make-interval 3 4))
                             :interval-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-recenter pushes expected results for simple cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 7 9))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-interval -1 1))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -4 -3))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-interval -1/2 1/2))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 20 20))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-interval 0 0))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":interval-recenter pushes expected results for open-ended cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-open-interval 7 9))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-open-interval -1 1))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -4 -3 :min-open? true))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-interval -1/2 1/2
                                                            :min-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-recenter pushes expected results for infinite cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 7 ∞))
                             :interval-recenter
                                                 :interval  (list
                                                          (s/make-interval
                                                            -∞
                                                            ∞))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-reciprocal pushes expected results for simple cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 3 4))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval 1/4 1/3))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -4 -3))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval -1/3 -1/4))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-reciprocal pushes expected results for open cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 3 4 :min-open? true))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval 1/4 1/3 :max-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -4 -3 :min-open? true))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval -1/3 -1/4 :max-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-reciprocal pushes expected results for 0-ended cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 0 0))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval -∞ ∞))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 0 10))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval 1/10 ∞))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 0 -10))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval -∞ -1/10))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-reciprocal pushes expected results for 0-spanning cases"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval -2 2))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (list
                                                          (s/make-interval -∞ -1/2)
                                                          (s/make-interval 1/2 ∞)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 0 0))
                             :interval-reciprocal
                                                 :exec  (list
                                                          (s/make-interval -∞ ∞))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":interval-reflect returns the arg flipped across 0.0"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval -2 3))
                             :interval-reflect
                                                 :interval  (list
                                                              (s/make-interval -3 2))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval -2 3 :max-open? true))
                             :interval-reflect
                                                 :interval  (list
                                                              (s/make-interval -3 2
                                                                :min-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-scale multiplies the bounds by a scalar"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(11)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval 22 33))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3))
     :scalar  '(1/2)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -1 3/2))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 5))
     :scalar  '(0.1)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -0.2 0.5))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":interval-scale flips bounds as appropriate with negative arg"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(-1)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -3 -2))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :min-open? true))
     :scalar  '(-1)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -3 -2
                                                            :max-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3 :max-open? true))
     :scalar  '(-1)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -3 -2
                                                            :min-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval ∞ 7 :min-open? true))
     :scalar  '(-1)}
                              :interval-scale    {:interval
                                                      (list (s/make-interval -∞ -7
                                                            :max-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":interval-shift multiplies the bounds by a scalar"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 2 3))
     :scalar  '(11)}
                              :interval-shift    {:interval
                                                      (list (s/make-interval 13 14))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 3))
     :scalar  (list 1/2)}
                              :interval-shift    {:interval
                                                      (list (s/make-interval -3/2 7/2))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval -2 5))
     :scalar  '(0.25)}
                              :interval-shift    {:interval
                                                      (list (s/make-interval -1.75 5.25))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-subset? returns the true if the top interval is a subset of the second"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3) (s/make-interval 2 3))
                             :interval-subset?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3) (s/make-interval 2 4))
                             :interval-subset?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3) (s/make-interval 2 3))
                             :interval-subset?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3) (s/make-interval 2 3 :min-open? true))
                             :interval-subset?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3) (s/make-interval 2 3 :min-open? true))
                             :interval-subset?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-subtract returns the difference of two intervals"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 2)
                       (s/make-interval 2 2))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 2 2)
                                                          (s/make-interval -2 -2)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 2 3)
                                                          (s/make-interval -2 -3)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval -3 -2))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval -2 -3)
                                                          (s/make-interval -2 -3)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2)
                       (s/make-interval 3 4))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 3 4)
                                                          (s/make-interval -1 -2)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 2 3)
                                                          (s/make-open-interval -2 -3)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval -3 -2))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval -2 -3)
                                                          (s/make-open-interval -2 -3)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2 :min-open? true)
                       (s/make-interval 2 3 :max-open? true))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 2 3 :max-open? true)
                                                          (s/make-interval -1 -2 :max-open? true)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 2 :max-open? true)
                       (s/make-interval 2 3 :min-open? true))
                             :interval-subtract
                                                 :exec  (list (list
                                                          (s/make-interval 2 3 :min-open? true)
                                                          (s/make-interval -1 -2 :min-open? true)
                                                          :interval-add))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )






(tabular
  (fact ":interval-union returns a list containing the union of its args"
    (register-type-and-check-instruction
        ?set-stack ?items interval-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 2 3)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 1 3))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 1 3)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3)
                       (s/make-interval 12 13))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 12 13)
                                                            (s/make-interval 2 3)))

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-interval 2 3))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 2 3)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-open-interval 2 3)
                       (s/make-open-interval 2 3))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-open-interval 2 3)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 1 3)
                       (s/make-interval 3 4))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 1 4)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :interval    (list (s/make-interval 2 3 :max-open? true)
                       (s/make-interval 3 4))
                             :interval-union
                                                 :exec  (list
                                                          (list
                                                            (s/make-interval 2 4)))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )







(tabular
  (fact ":interval-construct is created by the buildable aspect"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(8 2)
     :boolean  '(true false)}
                              :interval-construct
                                                    {:exec
                                                      (list
                                                        (s/make-interval 2 8
                                                          :max-open? true))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":interval-parts is created by the buildable aspect"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks interval-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:interval (list (s/make-interval 13 14 :min-open? true))}
                              :interval-parts      {:exec '((false true 14 13))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
