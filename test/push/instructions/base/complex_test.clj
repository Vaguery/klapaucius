(ns push.instructions.base.complex_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.definitions.complex :exclude [complex-infinite?]])
  (:use [push.type.item.complex])
  (:use [push.util.numerics])
  )


;; fixtures

(def cljNaN  (Math/sin ∞))



(tabular
  (fact ":complex-add returns the sum of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-add        :complex       (list (->Complex 4 6))
    :complex    (list (->Complex 1M 2/3) (->Complex ∞ 4))
                             :complex-add        :complex       (list (->Complex ∞ 14/3))
    )



(tabular
  (fact ":complex-add returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-add        :complex       '()
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-add        :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})

    :complex    (list (->Complex -∞ 2/3) (->Complex ∞ 4N))
                             :complex-add        :complex       '()
    :complex    (list (->Complex -∞ 2/3) (->Complex ∞ 4N))
                             :complex-add        :error       '({:item ":complex-add produced NaN", :step 0})
   )




(tabular
  (fact ":complex-conjugate returns the conjugate of a Complex record"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2))
                             :complex-conjugate  :complex       (list (->Complex 1 -2))
    :complex    (list (->Complex 0 0))
                             :complex-conjugate  :complex       (list (->Complex 0 0))
    )



(tabular
  (fact ":complex-divide returns the quotient of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-divide        :complex       (list (->Complex 11/5 -2/5))
    :complex    (list (->Complex 1.3 2/3) (->Complex ∞ 4))
                             :complex-divide        :complex       (list (->Complex ∞ -∞))
    )



(tabular
  (fact ":complex-divide returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-divide        :error       '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})


    :complex    (list (->Complex ∞ -∞) (->Complex -∞ -∞))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex ∞ -∞) (->Complex ∞ -∞))
                             :complex-divide        :error       '({:item ":complex-divide produced NaN", :step 0})


    :complex    (list (->Complex 0 0) (->Complex 2 7))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex 0 0) (->Complex 2 7))
                             :complex-divide        :error       '({:item "Divide by zero", :step 0})
   )





(tabular
  (fact ":complex-multiply returns the product of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-multiply        :complex       (list (->Complex -5 10))
    :complex    (list (->Complex 1M 2/3) (->Complex ∞ 4))
                             :complex-multiply        :complex       (list (->Complex ∞ ∞))
    )



(tabular
  (fact ":complex-multiply returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-multiply        :complex       '()
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-multiply        :error       '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})


    :complex    (list (->Complex -∞ 1) (->Complex ∞ 3))
                             :complex-multiply        :complex       '()
    :complex    (list (->Complex -∞ 1) (->Complex ∞ 3))
                             :complex-multiply        :error       '({:item ":complex-multiply produced NaN", :step 0})
   )





(tabular
  (fact ":complex-norm returns a the norm"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex 3 4))
                             :complex-norm        :scalar        '(5)
    :complex    (list (->Complex 0 0))
                             :complex-norm        :scalar        '(0)
    :complex    (list (->Complex 3/7 4/7))
                             :complex-norm        :scalar        '(5/7)
    :complex    (list (->Complex -3/7 -4/7))
                             :complex-norm        :scalar        '(5/7)
    )



(tabular
  (fact ":complex-norm deals with errors nicely"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex 1/3 4M))
                             :complex-norm        :scalar        '()
    :complex    (list (->Complex 1/3 4M))
                             :complex-norm        :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )





(tabular
  (fact ":complex-scale multiplies a :scalar by a :complex"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks complex-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2 3))
     :scalar  '(2)}           :complex-scale    {:complex (list (complexify 4 6))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2M 3))
     :scalar  '(3N)}           :complex-scale  {:complex (list (complexify 6M 9N))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":complex-scale handles errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks complex-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2M 3M))
     :scalar  '(2/3)}        :complex-scale    {:complex '()
                                                :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":complex-shift multiplies a :scalar by a :complex"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks complex-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2 3))
     :scalar  '(2)}           :complex-shift    {:complex (list (complexify 4 5))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2M 3))
     :scalar  '(3N)}           :complex-shift  {:complex (list (complexify 5M 6N))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":complex-shift handles errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks complex-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:complex (list (complexify 2M 3M))
     :scalar  '(2/3)}        :complex-shift    {:complex '()
                                                :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact ":complex-subtract returns the difference of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-subtract        :complex       (list (->Complex 2 2))
    :complex    (list (->Complex 1M 2/3) (->Complex ∞ 4))
                             :complex-subtract        :complex       (list (->Complex ∞ 10/3))
    )



(tabular
  (fact ":complex-subtract returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                         :complex-subtract        :complex       '()
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                         :complex-subtract        :error       '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})



    :complex    (list (->Complex ∞ 2/3) (->Complex ∞ 4M))
                             :complex-subtract        :complex       '()
    :complex    (list (->Complex ∞ 2/3) (->Complex ∞ 4))
                             :complex-subtract        :error       '({:item ":complex-subtract produced NaN", :step 0})
   )




(tabular
  (fact ":scalar-complexify returns a new :complex"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction     ?get-stack     ?expected
    :scalar     '(1 2)    :scalar-complexify   :complex   (list (->Complex 1 0))
    :scalar     '(9 0)    :scalar-complexify   :complex   (list (->Complex 9 0))
    :scalar     '()       :scalar-complexify   :complex   (list )
   )


(tabular
  (fact ":complex-zero returns a new :complex"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items   ?instruction     ?get-stack     ?expected
    :complex    '()      :complex-zero    :complex   (list (->Complex 0 0))
   )



(tabular
  (fact ":complex-reciprocal returns the quotient of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack     ?expected
    :complex    (list (->Complex 1 1))
                    :complex-reciprocal    :complex  (list (->Complex 1/2 -1/2))
    :complex    (list (->Complex 1/3 1/4))
                    :complex-reciprocal    :complex  (list (->Complex 48/25 -36/25))
    :complex    (list (->Complex 0 1))
                    :complex-reciprocal    :complex  (list (->Complex 0 -1))
    :complex    (list (->Complex 1M 2M))
                    :complex-reciprocal    :complex  (list (->Complex 0.2M -0.4M))
    )



(tabular
  (fact ":complex-reciprocal returns reasonable errors"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack     ?expected
    :complex    (list (->Complex 1 ∞))
                    :complex-reciprocal    :complex       '()
    :complex    (list (->Complex 1 ∞))
                    :complex-reciprocal    :error        '({:step 0, :item ":complex-reciprocal produced NaN"})


    :complex    (list (->Complex 1 cljNaN))
                    :complex-reciprocal    :complex       '()
    :complex    (list (->Complex 1 cljNaN))
                    :complex-reciprocal    :error        '({:step 0, :item ":complex-reciprocal produced NaN"})


    :complex    (list (->Complex 1M 1/3))
                    :complex-reciprocal    :complex       '()
    :complex    (list (->Complex 1M 1/3))
                    :complex-reciprocal    :error        '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})

    :complex    (list (->Complex 0 0))
                    :complex-reciprocal    :complex       '()
    :complex    (list (->Complex 0 0))
                    :complex-reciprocal    :error        '({:item "Divide by zero", :step 0})

)




(tabular
  (fact ":complex-infinite? works as a predicate"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack     ?expected
    :complex    (list (->Complex 1 1))
                    :complex-infinite?    :boolean          '(false)
    :complex    (list (->Complex ∞ 1))
                    :complex-infinite?    :boolean          '(true)
    :complex    (list (->Complex 1 ∞))
                    :complex-infinite?    :boolean          '(true)
    :complex    (list (->Complex -∞ ∞))
                    :complex-infinite?    :boolean          '(true)
)



(tabular
  (fact ":complex-parts is generated from the buildable aspect"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack     ?expected
    :complex    (list (->Complex 2 3))
                        :complex-parts          :exec      '((3 2))
    )




(tabular
  (fact ":complex-construct is generated from the buildable aspect"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack     ?expected
    :scalar    '(9 12)
                         :complex-construct :exec       (list (complexify 12 9))
    )
