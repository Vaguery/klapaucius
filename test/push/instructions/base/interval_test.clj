(ns push.instructions.base.interval_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.type.definitions.interval :as s])
  (:use [push.type.item.interval])
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

