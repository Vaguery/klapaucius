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
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4))
                             :complex-add        :complex       (list (->Complex cljInf 14/3))
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

    :complex    (list (->Complex cljNinf 2/3) (->Complex cljInf 4N))
                             :complex-add        :complex       '()
    :complex    (list (->Complex cljNinf 2/3) (->Complex cljInf 4N))
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
    :complex    (list (->Complex 1.3 2/3) (->Complex cljInf 4))
                             :complex-divide        :complex       (list (->Complex cljInf cljNinf))
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


    :complex    (list (->Complex cljInf cljNinf) (->Complex cljNinf cljNinf))
                             :complex-divide        :complex       '()
    :complex    (list (->Complex cljInf cljNinf) (->Complex cljInf cljNinf))
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
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4))
                             :complex-multiply        :complex       (list (->Complex cljInf cljInf))
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
    :complex    (list (->Complex 1M 2/3) (->Complex cljInf 4))
                             :complex-subtract        :complex       (list (->Complex cljInf 10/3))
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



    :complex    (list (->Complex cljInf 2/3) (->Complex cljInf 4M))
                             :complex-subtract        :complex       '()
    :complex    (list (->Complex cljInf 2/3) (->Complex cljInf 4))
                             :complex-subtract        :error       '({:item ":complex-subtract produced NaN", :step 0})
   )
