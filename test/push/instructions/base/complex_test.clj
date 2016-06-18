(ns push.instructions.base.complex_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.definitions.complex])
  (:use [push.types.type.complex])
  )


;; fixtures

(def cljInf  Double/POSITIVE_INFINITY)
(def cljNinf Double/NEGATIVE_INFINITY)
(def cljNaN  (Math/sin Double/POSITIVE_INFINITY))




(tabular
  (fact ":complex-add returns the sum of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-add        :complex       (list (->Complex 4 6))
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-add        :complex       (list (->Complex 10/7 14/3))
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4M))
                             :complex-add        :complex       (list (->Complex cljInf 14/3))
    )



(tabular
  (fact ":complex-add returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex cljNinf 2/3) (->Complex cljInf 4M))
                             :complex-add        :complex       '()
    :complex    (list (->Complex cljNinf 2/3) (->Complex cljInf 4M))
                             :complex-add        :error       '({:item ":complex-add produced NaN", :step 0})
   )




(tabular
  (fact ":complex-divide returns the quotient of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-divide        :complex       (list (->Complex 11/5 -2/5))
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-divide        :complex       (list (->Complex 15/7 18/7))
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4M))
                             :complex-divide        :complex       (list (->Complex cljInf cljNinf))
    )



(tabular
  (fact ":complex-divide returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex cljInf cljNinf) (->Complex cljNinf cljNinf))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex cljInf cljNinf) (->Complex cljInf cljNinf))
                             :complex-divide        :error       '({:item ":complex-divide produced NaN", :step 0})
    :complex    (list (->Complex 0 0) (->Complex 2 7))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex 0 0) (->Complex 2 7))
                             :complex-divide        :error       '({:item ":complex-divide Div0", :step 0})
   )





(tabular
  (fact ":complex-multiply returns the product of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-multiply        :complex       (list (->Complex -5 10))
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-multiply        :complex       (list (->Complex -47/21 30/7))
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4M))
                             :complex-multiply        :complex       (list (->Complex cljInf cljInf))
    )



(tabular
  (fact ":complex-multiply returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex cljNinf 1) (->Complex cljInf 3))
                             :complex-multiply        :complex       '()
    :complex    (list (->Complex cljNinf 1) (->Complex cljInf 3))
                             :complex-multiply        :error       '({:item ":complex-multiply produced NaN", :step 0})
   )




(tabular
  (fact ":complex-subtract returns the difference of two Complex records"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected
    :complex    (list (->Complex 1 2) (->Complex 3 4))
                             :complex-subtract        :complex       (list (->Complex 2 2))
    :complex    (list (->Complex 1M 2/3) (->Complex 3/7 4M))
                             :complex-subtract        :complex       (list (->Complex -4/7 10/3))
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4M))
                             :complex-subtract        :complex       (list (->Complex cljInf 10/3))
    )



(tabular
  (fact ":complex-subtract returns reasonable `:error` results"
    (register-type-and-check-instruction
        ?set-stack ?items complex-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction        ?get-stack     ?expected

    :complex    (list (->Complex cljInf 2/3) (->Complex cljInf 4M))
                             :complex-subtract        :complex       '()
    :complex    (list (->Complex cljInf 2/3) (->Complex cljInf 4M))
                             :complex-subtract        :error       '({:item ":complex-subtract produced NaN", :step 0})
   )

