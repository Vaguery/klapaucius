(ns push.instructions.base.span_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.type.definitions.span :as s])
  (:use [push.type.item.span])
  )



(tabular
  (fact ":span-coincide? returns the true if two Spans are equal or reversed orientation"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3) (s/make-span 2 3))
                             :span-coincide?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 2 4))
                             :span-coincide?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 3) (s/make-span 2 3))
                             :span-coincide?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 3 2))
                             :span-coincide?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3 :start-open? true)
                   (s/make-span 3 2 :end-open? true))
                             :span-coincide?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3 :start-open? true)
                   (s/make-span 3 2 :start-open? true))
                             :span-coincide?     :boolean       '(false)
    )




(tabular
  (fact ":span-direction returns -1, 0 or 1 depending on :start and :end"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3))
                             :span-direction     :scalar          '(1)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 7 7))
                             :span-direction     :scalar          '(0)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 3 2))
                             :span-direction     :scalar          '(-1)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":span-empty? returns true if the start and end are identical and at least one is open"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3))
                             :span-empty?     :boolean          '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 7 7))
                             :span-empty?     :boolean          '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 7 7 :start-open? true))
                             :span-empty?     :boolean          '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":span-include? says whether a :scalar falls in a :span"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks span-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-span 2 3))
     :scalar  '(2)}           :span-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-span 2 3))
     :scalar  '(4)}           :span-include?            {:boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-span 3 2))
     :scalar  '(2.7)}         :span-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-span 3 2 :start-open? true))
     :scalar  '(2)}           :span-include?            {:boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-span 3 2 :start-open? true))
     :scalar  '(3)}           :span-include?            {:boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:span (list (s/make-open-span 3 3))
     :scalar  '(3)}           :span-include?            {:boolean '(false)}
    )




(tabular
  (fact ":span-reverse returns the opposite of a span"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3))
                             :span-reverse     :span       (list (s/make-span 3 2))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3 :start-open? true))
                             :span-reverse     :span       (list (s/make-span 3 2 :end-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 7 7 :start-open? true))
                             :span-reverse     :span       (list (s/make-span 7 7 :end-open? true))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":span-overlap? returns the true if the top Span shares even one point with the second"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 3 2))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 3 4))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-open-span 3 4))
                             :span-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 3) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3 :start-open? true) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 3) (s/make-open-span 2 3))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 3 3) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 3 3) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 3 3 :start-open? true) (s/make-span 2 3))
                             :span-overlap?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 5) (s/make-span 3 4))
                             :span-overlap?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact ":span-surrounds? returns the true if the top Span is surrounded by the second"
    (register-type-and-check-instruction
        ?set-stack ?items span-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :span    (list (s/make-span 2 3) (s/make-span 2 3))
                             :span-surrounds?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 2 4))
                             :span-surrounds?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 3) (s/make-span 2 3))
                             :span-surrounds?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-span 2 3) (s/make-span 2 3 :start-open? true))
                             :span-surrounds?     :boolean       '(false)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :span    (list (s/make-open-span 2 3) (s/make-span 2 3 :start-open? true))
                             :span-surrounds?     :boolean       '(true)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )

