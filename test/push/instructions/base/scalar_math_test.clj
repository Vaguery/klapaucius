(ns push.instructions.base.scalar_math_test
  (:require [push.interpreter.core :as i]
            [push.type.definitions.complex :as cpx])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.scalar])
  (:use [push.util.numerics :only [∞,-∞]])
  )


;; fixtures

(def cljNaN  (Math/sin ∞))



(tabular
  (fact ":scalar-abs returns the absolute value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M)          :scalar-abs           :exec         '(92M)
    :scalar     '(-92M)         :scalar-abs           :exec         '(92M)
    :scalar     '(0)            :scalar-abs           :exec         '(0)
    :scalar     '(-0)           :scalar-abs           :exec         '(0)
    :scalar     '(92)           :scalar-abs           :exec         '(92)
    :scalar     '(-92)          :scalar-abs           :exec         '(92)
    :scalar     '(9.2)          :scalar-abs           :exec         '(9.2)
    :scalar     '(-9.2)         :scalar-abs           :exec         '(9.2)
    :scalar     '(92N)          :scalar-abs           :exec         '(92N)
    :scalar     '(-92N)         :scalar-abs           :exec         '(92N)
    :scalar     '(9/2)          :scalar-abs           :exec         '(9/2)
    :scalar     '(-9/2)         :scalar-abs           :exec         '(9/2)

    :scalar     (list ∞)        :scalar-abs           :exec         (list ∞)
    :scalar     (list -∞)       :scalar-abs           :exec         (list ∞)
    :scalar     '()             :scalar-abs           :exec         '()
    )



(tabular
  (fact ":scalar-add returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-add        :exec       '(100M)
    :scalar     '(-92M 8)       :scalar-add        :exec       '(-84M)
    :scalar     '(0 1)          :scalar-add        :exec       '(1)
    :scalar     '(-3 12)        :scalar-add        :exec       '(9)
    :scalar     '(92 -11/12)    :scalar-add        :exec       '(1093/12)
    :scalar     '(-92 -11/2)    :scalar-add        :exec       '(-195/2)
    :scalar     '(92N 8.7)      :scalar-add        :exec       '(100.7)
    :scalar     '(-92M 8.7)     :scalar-add        :exec       '(-83.3)
    :scalar     '(9/2 1.1)      :scalar-add        :exec       '(5.6)
    :scalar     '(-2/9 2.0)     :scalar-add        :exec       '(1.7777777777777777)


    :scalar     (list ∞ 1)      :scalar-add        :exec      (list ∞)
    :scalar     (list -∞ 1)     :scalar-add        :exec      (list -∞)
    :scalar     '(1e9999M 1e99) :scalar-add        :exec      (list ∞)
    :scalar     '()             :scalar-add        :exec      '()
    )





(tabular
  (fact ":scalar-add returns an `:error` when the arguments blow up"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack    ?expected
    :scalar     '(1M 1/3)        :scalar-add        :exec     '()
    :scalar     '(1M 1/3)        :scalar-add        :error      '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    )




(tabular
  (fact ":scalar-add creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list ∞ -∞)     :scalar-add        :exec         '()
    :scalar     (list ∞ -∞)     :scalar-add        :error         '({:step 0, :item ":scalar-add produced NaN"}))





(tabular
  (fact ":scalar-arccosine the arccosine of a :scalar that falls between -1 and 1 (inclusive)"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arccosine   :exec      '(1.5707963267948966)
    :scalar     '(1)
                          :scalar-arccosine   :exec      '(0.0)
    :scalar     '(-1)
                          :scalar-arccosine   :exec      '(3.141592653589793)
    :scalar     '(1/17)
                          :scalar-arccosine   :exec      '(1.5119388208478153)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arccosine   :exec      '(1.5707963267948966)


    :scalar     '(-2)     :scalar-arccosine   :exec      '()
    :scalar     '(-2)     :scalar-arccosine   :error       (list {:step 0,
                                                                  :item ":scalar-arccosine bad argument"})

    :scalar     (list ∞)  :scalar-arccosine   :exec      '()
    :scalar     (list ∞)  :scalar-arccosine   :error       (list {:step 0,
                                                                  :item ":scalar-arccosine bad argument"})
    :scalar     '()       :scalar-arccosine   :scalar      '()
    )



(tabular
  (fact ":scalar-arcsine the arcsine of a :scalar that falls between -1 and 1 (inclusive)"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arcsine     :exec       '(0.0)
    :scalar     '(1)
                          :scalar-arcsine     :exec       '(1.5707963267948966)
    :scalar     '(-1)
                          :scalar-arcsine     :exec       '(-1.5707963267948966)
    :scalar     '(1/17)
                          :scalar-arcsine     :exec       '(0.05885750594708124)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arcsine     :exec       (list (Math/asin 0.00000000000000000001M))


    :scalar     '(-2)     :scalar-arcsine     :exec       '()
    :scalar     '(-2)     :scalar-arcsine     :error       (list {:step 0,
                                                                  :item ":scalar-arcsine bad argument"})

    :scalar     (list ∞)  :scalar-arcsine     :exec       '()
    :scalar     (list ∞)  :scalar-arcsine     :error       (list {:step 0,
                                                                  :item ":scalar-arcsine bad argument"})
    :scalar     '()       :scalar-arcsine     :exec       '()
    )




(tabular
  (fact ":scalar-arctangent the arctangent of a :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arctangent  :exec       '(0.0)
    :scalar     '(1)
                          :scalar-arctangent  :exec       '(0.7853981633974483)
    :scalar     '(-1)
                          :scalar-arctangent  :exec      '(-0.7853981633974483)
    :scalar     '(1/17)
                          :scalar-arctangent  :exec       '(0.0587558227157227)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arctangent  :exec       (list (Math/asin 0.00000000000000000001M))

    :scalar     '(-2)     :scalar-arctangent  :exec      '(-1.1071487177940904)
    :scalar     (list Math/PI)
                          :scalar-arctangent  :exec     '(1.2626272556789115)

    :scalar     '()       :scalar-arctangent  :exec      '()
    )





(tabular
  (fact ":scalar-ceiling applies clojure.math.numeric-tower/ceil to the top :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-ceiling     :exec     '(0)
    :scalar     '(-1.0)    :scalar-ceiling     :exec     '(-1.0)
    :scalar     '(-1.0M)   :scalar-ceiling     :exec     '(-1N)
    :scalar     '(300.1M)  :scalar-ceiling     :exec     '(301N)
    :scalar     '(-300.1M) :scalar-ceiling     :exec     '(-300N)
    :scalar     '(300N)    :scalar-ceiling     :exec     '(300N)
    :scalar     '(1.7e83)  :scalar-ceiling     :exec     '(1.7E83)
    :scalar     '(7/3)     :scalar-ceiling     :exec     '(3N)

    :scalar     '(1.7e837M)
                           :scalar-ceiling     :exec    (list (bigint 1.7e837M))
    :scalar     (list ∞)   :scalar-ceiling     :exec    (list ∞)
    :scalar     (list -∞)  :scalar-ceiling     :exec    (list -∞)
    :scalar     '()        :scalar-ceiling     :exec    '()
    )



(tabular
  (fact ":scalar-cosine produces the cosine of the top :scalar, read as radians"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction      ?get-stack  ?expected
    :scalar     '(0)      :scalar-cosine     :exec     '(1.0)
    :scalar     '(-1.0)   :scalar-cosine     :exec     '(0.5403023058681398)
    :scalar     '(-1.0M)  :scalar-cosine     :exec     '(0.5403023058681398)
    :scalar     '(300.1M) :scalar-cosine     :exec     '(0.07782281308912051)
    :scalar     '(300M)   :scalar-cosine     :exec     '(-0.022096619278683942)
    :scalar     '(300N)   :scalar-cosine     :exec     '(-0.022096619278683942)
    :scalar     '(1.7e83) :scalar-cosine     :exec     '(-0.020932220708646424)
    :scalar     '(7/3)    :scalar-cosine     :exec     '(-0.6907581397498761)
    :scalar     (list Math/PI)
                          :scalar-cosine     :exec     '(-1.0)
    :scalar     '()       :scalar-cosine     :exec     '()
    )



(tabular
  (fact ":scalar-dec decrements the top :scalar by 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-dec           :exec         '(91M)
    :scalar     '(-92M)     :scalar-dec           :exec         '(-93M)
    :scalar     '(0)        :scalar-dec           :exec         '(-1)
    :scalar     '(-0)       :scalar-dec           :exec         '(-1)
    :scalar     '(92)       :scalar-dec           :exec         '(91)
    :scalar     '(-92)      :scalar-dec           :exec         '(-93)
    :scalar     '(9.2)      :scalar-dec           :exec         '(8.2)
    :scalar     '(-9.2)     :scalar-dec           :exec         '(-10.2)
    :scalar     '(92N)      :scalar-dec           :exec         '(91N)
    :scalar     '(-92N)     :scalar-dec           :exec         '(-93N)
    :scalar     '(9/2)      :scalar-dec           :exec         '(7/2)
    :scalar     '(-9/2)     :scalar-dec           :exec         '(-11/2)


    :scalar     (list ∞)    :scalar-dec           :exec         (list ∞)
    :scalar     (list -∞)   :scalar-dec           :exec         (list -∞)
    :scalar     '()         :scalar-dec           :exec         '()
    )



(tabular
  (fact ":scalar-divide produces the quotient of the top two :scalars, or an :error f the top one (numerator) is zero"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 8)}       :scalar-divide     {:exec '(2/23)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 0)}       :scalar-divide     {:exec '(0)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0 92)}       :scalar-divide     {:exec '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 -11/12)}  :scalar-divide     {:exec '(-11/1104)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-divide     {:exec '(9/7)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-divide     {:exec '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(9.2 -11/12)} :scalar-divide     {:exec (list (/ -11/12 9.2))
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2N 6/7)}     :scalar-divide     {:exec '(3/7)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0N 8)}       :scalar-divide     {:exec '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-divide     {:exec '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2M 3M)}       :scalar-divide     {:exec '(1.5M)
                                                   :error '()}


    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   (list ∞ 3M)}    :scalar-divide     {:exec '(0.0)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   (list 3M ∞)}    :scalar-divide     {:exec (list ∞)
                                                   :error '()}
)




(tabular
  (fact ":scalar-divide captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(1/3 10M)      :scalar-divide     :exec          '()
    :scalar     '(1/3 10M)      :scalar-divide     :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-divide creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list ∞ ∞)      :scalar-divide     :exec         '()
    :scalar     (list ∞ ∞)      :scalar-divide     :error         '({:step 0, :item ":scalar-divide produced NaN"}))




(tabular
  (fact ":scalar-E pushes e"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-E     :exec         '(2.718281828459045)
    )



(tabular
  (fact ":scalar-floor applies clojure.math.numeric-tower/floor to the top :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-floor     :exec       '(0)
    :scalar     '(-1.0)    :scalar-floor     :exec       '(-1.0)
    :scalar     '(-1.0M)   :scalar-floor     :exec       '(-1N)
    :scalar     '(300.1M)  :scalar-floor     :exec       '(300N)
    :scalar     '(-300.1M) :scalar-floor     :exec       '(-301N)
    :scalar     '(300N)    :scalar-floor     :exec       '(300N)
    :scalar     '(1.7e83)  :scalar-floor     :exec       '(1.7E83)
    :scalar     '(7/3)     :scalar-floor     :exec       '(2N)

    :scalar     (list (+ 0.1M (bigint 1.7e837M)))
                           :scalar-floor     :exec    (list (bigint 1.7e837M))
    :scalar     (list (+ 0.1 (bigint 1.7e837M)))
                           :scalar-floor     :exec      (list ∞)
    :scalar     (list ∞)   :scalar-floor     :exec      (list ∞)
    :scalar     (list -∞)  :scalar-floor     :exec      (list -∞)
    :scalar     '()        :scalar-floor     :exec      '()
    )



(tabular
  (fact ":scalar-fractional produces just the remainder mod 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction        ?get-stack  ?expected
    :scalar     '(0)      :scalar-fractional     :exec     '(0)
    :scalar     '(0.25)   :scalar-fractional     :exec     '(0.25)
    :scalar     '(11.25)  :scalar-fractional     :exec     '(0.25)
    :scalar     '(-11.25) :scalar-fractional     :exec     '(0.25)
    :scalar     '(1e-2)   :scalar-fractional     :exec     '(0.01)
    :scalar     '(-1e-2)  :scalar-fractional     :exec     '(0.01)

    :scalar     '(1/3)    :scalar-fractional     :exec     '(1/3)
    :scalar     '(11/3)   :scalar-fractional     :exec     '(2/3)
    :scalar     '(-4/3)   :scalar-fractional     :exec     '(1/3)

    :scalar     '(17N)    :scalar-fractional     :exec     '(0)

    :scalar     '(17M)    :scalar-fractional     :exec     '(0M)
    :scalar     '(17.2M)  :scalar-fractional     :exec     '(0.2M)
    :scalar     '(-7.2M)  :scalar-fractional     :exec     '(0.2M)
    :scalar     '(1e-2M)  :scalar-fractional     :exec     '(0.01M)
    )




(tabular
  (fact ":scalar-inc increments the top :scalar by 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-inc           :exec         '(93M)
    :scalar     '(-92M)     :scalar-inc           :exec         '(-91M)
    :scalar     '(0)        :scalar-inc           :exec         '(1)
    :scalar     '(-0)       :scalar-inc           :exec         '(1)
    :scalar     '(92)       :scalar-inc           :exec         '(93)
    :scalar     '(-92)      :scalar-inc           :exec         '(-91)
    :scalar     '(9.2)      :scalar-inc           :exec         '(10.2)
    :scalar     '(-9.2)     :scalar-inc           :exec         '(-8.2)
    :scalar     '(92N)      :scalar-inc           :exec         '(93N)
    :scalar     '(-92N)     :scalar-inc           :exec         '(-91N)
    :scalar     '(9/2)      :scalar-inc           :exec         '(11/2)
    :scalar     '(-9/2)     :scalar-inc           :exec         '(-7/2)



    :scalar     (list ∞)    :scalar-inc           :exec         (list ∞)
    :scalar     (list -∞)   :scalar-inc           :exec         (list -∞)
    :scalar     '()         :scalar-inc           :exec         '()
    )



(tabular
  (fact ":scalar-modulo produces the quotient of the top two :scalars, or an :error f the top one (numerator) is zero"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 8)}       :scalar-modulo     {:exec  '(8)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(8 93)}       :scalar-modulo     {:exec  '(5)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0 92)}       :scalar-modulo     {:exec  '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1 21/12)}  :scalar-modulo     {:exec  '(3/4)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-modulo     {:exec  '(4/21)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-modulo     {:exec  '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0.25 -11/8)} :scalar-modulo     {:exec  '(0.125)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(6/7 2N)}     :scalar-modulo     {:exec  '(2/7)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 3N)}     :scalar-modulo     {:exec  '(1/3)
                                                   :error '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0N 8)}       :scalar-modulo     {:exec  '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-modulo     {:exec  '()
                                                   :error '({:item "Divide by zero", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1.2M 32M)}    :scalar-modulo  {:exec  '(0.8M)
                                                   :error '()}
                                                   )



(tabular
  (fact ":scalar-modulo captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(3/7 10M)      :scalar-modulo     :exec          '()
    :scalar     '(3/7 10M)      :scalar-modulo     :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-modulo creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list ∞ ∞)      :scalar-modulo     :exec         '()
    :scalar     (list ∞ ∞)      :scalar-modulo     :error         '({:item "Infinite or NaN", :step 0}))



(tabular
  (fact ":scalar-ln the ln of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack   ?expected
    :scalar     (list Math/E)
                          :scalar-ln        :exec       '(1.0)
    :scalar     (list (* 2 Math/E))
                          :scalar-ln        :exec       '(1.6931471805599452)
    :scalar     (list (* Math/E Math/E))
                          :scalar-ln        :exec       '(2.0)
    :scalar     (list (* Math/E Math/E Math/E))
                          :scalar-ln        :exec       '(3.0)


    :scalar     '(0)      :scalar-ln        :exec       '()
    :scalar     '(0)      :scalar-ln        :error       '({:step 0, :item ":scalar-ln bad argument"})
    :scalar     '(-2712893)
                          :scalar-ln        :exec       '()
    :scalar     '(-2712893)
                          :scalar-ln        :error       '({:step 0, :item ":scalar-ln bad argument"})

    :scalar     '()       :scalar-ln        :exec       '()
    )




(tabular
  (fact ":scalar-ln1p the ln1p of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack   ?expected
    :scalar     (list (dec' Math/E))
                          :scalar-ln1p        :exec       '(1.0)
    :scalar     (list (dec' (* 2 Math/E)))
                          :scalar-ln1p        :exec       '(1.6931471805599454)
    :scalar     (list (dec' (* Math/E Math/E)))
                          :scalar-ln1p        :exec       '(2.0)
    :scalar     (list (dec' (* Math/E Math/E Math/E)))
                          :scalar-ln1p        :exec       '(3.0)

    :scalar     '(0)      :scalar-ln1p        :exec       '(0.0)


    :scalar     '(-1)      :scalar-ln1p       :exec       '()
    :scalar     '(-1)      :scalar-ln1p       :error       '({:step 0, :item ":scalar-ln1p bad argument"})
    :scalar     '(-2712893)
                          :scalar-ln1p        :exec       '()
    :scalar     '(-2712893)
                          :scalar-ln1p        :error       '({:step 0, :item ":scalar-ln1p bad argument"})

    :scalar     '()       :scalar-ln1p        :exec       '()
    )




(tabular
  (fact ":scalar-log10 the log10 of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?expected
    :scalar     '(1)      :scalar-log10        :exec       '(0.0)
    :scalar     '(1/100)  :scalar-log10        :exec       '(-2.0)
    :scalar     '(10)     :scalar-log10        :exec       '(1.0)
    :scalar     '(100)    :scalar-log10        :exec       '(2.0)
    :scalar     '(1000)   :scalar-log10        :exec       '(3.0)


    :scalar     '(0)      :scalar-log10        :exec       '()
    :scalar     '(0)      :scalar-log10        :error       '({:step 0, :item ":scalar-log10 bad argument"})
    :scalar     '(-2712893)
                          :scalar-log10        :exec       '()
    :scalar     '(-2712893)
                          :scalar-log10        :error       '({:step 0, :item ":scalar-log10 bad argument"})

    :scalar     '()       :scalar-log10        :exec       '()
    )




(tabular
  (fact ":scalar-multiply returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-multiply     :exec        '(736M)
    :scalar     '(-92M 8)       :scalar-multiply     :exec        '(-736M)
    :scalar     '(0 1)          :scalar-multiply     :exec        '(0)
    :scalar     '(-3 12)        :scalar-multiply     :exec        '(-36)
    :scalar     '(92 -11/12)    :scalar-multiply     :exec        '(-253/3)
    :scalar     '(-92 -11/2)    :scalar-multiply     :exec        '(506N)
    :scalar     '(92N 8.7)      :scalar-multiply     :exec        '(800.4)
    :scalar     '(8.7 92N)      :scalar-multiply     :exec        '(800.4)
    :scalar     '(-92M 8.7)     :scalar-multiply     :exec        '(-800.4)
    :scalar     '(9/2 1.1)      :scalar-multiply     :exec        '(4.95)
    :scalar     '(-2/9 2.0)     :scalar-multiply     :exec     '(-0.4444444444444444)
    :scalar     '()             :scalar-multiply     :exec        '()
    )




(tabular
  (fact ":scalar-multiply captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(10M 1/3)      :scalar-multiply     :exec       '()
    :scalar     '(10M 1/3)      :scalar-multiply     :error      '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-multiply creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list 0 ∞)      :scalar-multiply    :exec         '()
    :scalar     (list 0 ∞)      :scalar-multiply    :error         '({:step 0, :item ":scalar-multiply produced NaN"}))



(tabular
  (fact ":scalar-power produces the result of raising the second :scalar to the power of the first, or an :error if the result is not a scalar"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction     ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(2 8)}       :scalar-power     {:exec  '(64)
                                              :error '()}
    {:scalar '(3 3/7)}     :scalar-power     {:exec  '(27/343)
                                              :error '()}
    {:scalar '(1/2 100.0)} :scalar-power     {:exec  '(10.0)
                                              :error '()}
    {:scalar '(0.5 100.0)} :scalar-power     {:exec  '(10.0)
                                              :error '()}
    {:scalar '(3/7 100)}   :scalar-power     {:exec  '(7.196856730011521)
                                              :error '()}
    {:scalar '(1/2 0)}     :scalar-power     {:exec  '(0.0)
                                              :error '()}
    {:scalar '(-2.8 100)}  :scalar-power     {:exec  '(2.5118864315095823E-6)
                                              :error '()}
    {:scalar '(-1/2 100)}  :scalar-power     {:exec  '(0.1)
                                              :error '()}
    {:scalar '(-2 100)}    :scalar-power     {:exec  '(1/10000)
                                              :error '()}



    {:scalar '(-1e13 1e13)} :scalar-power    {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar '(0.5 -10.0)} :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar '(1/13 -10)}  :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar '(1e13 1e13)}  :scalar-power    {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar '(1e13 -1e13)} :scalar-power    {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
)



(tabular
  (fact ":scalar-power overflows when the scale cutoff is exceeded"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction     ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(2 8)}       :scalar-power     {:exec  '(64)
                                              :error '()}
    {:scalar '(33334 111121213M)}
                           :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar '(33334 0.0000000000111121213M)}
                           :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar (list 33334 ∞)}
                           :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar (list ∞ ∞)}
                           :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0})}
    {:scalar (list ∞ 0.0000000000111121213M)}
                           :scalar-power     {:exec  '(0.0)
                                              :error '()}
    {:scalar '(-3333 111121213M)}
                           :scalar-power     {:exec  '()
                                              :error '({:item ":scalar-power out of bounds", :step 0} {:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    )





(tabular
  (fact ":scalar-round applies clojure.math.numeric-tower/round to the top :scalar to produce a `long` result"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-round     :exec       '(0)
    :scalar     '(0.4)     :scalar-round     :exec       '(0)
    :scalar     '(0.6)     :scalar-round     :exec       '(1)
    :scalar     '(0.5)     :scalar-round     :exec       '(1)
    :scalar     '(1.5)     :scalar-round     :exec       '(2)
    :scalar     '(-1.1)    :scalar-round     :exec       '(-1)
    :scalar     '(-1.5)    :scalar-round     :exec       '(-1)
    :scalar     '(-1.0M)   :scalar-round     :exec       '(-1N)
    :scalar     '(300.1M)  :scalar-round     :exec       '(300N)
    :scalar     '(-300.1M) :scalar-round     :exec       '(-300N)
    :scalar     '(300M)    :scalar-round     :exec       '(300N)
    :scalar     '(1.7e83)  :scalar-round     :exec       '(9223372036854775807)
    :scalar     '(1.7e83M) :scalar-round     :exec       (list (bigint 1.7e83M))
    :scalar     '(7/3)     :scalar-round     :exec       '(2N)

    :scalar     (list (+ 0.1M (bigint 1.7e837M)))
                           :scalar-round     :exec     (list (bigint 1.7e837M))
    :scalar     (list (+ 0.1 (bigint 1.7e837M)))
                           :scalar-round     :exec       '(9223372036854775807)
    :scalar     (list ∞)   :scalar-round     :exec       '(9223372036854775807)
    :scalar     (list -∞)  :scalar-round     :exec       '(-9223372036854775808)
    :scalar     '()        :scalar-round     :exec       '()
    )





(tabular
  (fact ":scalar-sign returns -1 if :scalar is negative, 0 if zero, 1 if positive"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-sign           :exec        '(1)
    :scalar     '(-92M)     :scalar-sign           :exec        '(-1)
    :scalar     '(0)        :scalar-sign           :exec        '(0)
    :scalar     '(-0)       :scalar-sign           :exec        '(0)
    :scalar     '(92)       :scalar-sign           :exec        '(1)
    :scalar     '(-92)      :scalar-sign           :exec        '(-1)
    :scalar     '(9.2)      :scalar-sign           :exec        '(1)
    :scalar     '(-9.2)     :scalar-sign           :exec        '(-1)
    :scalar     '(92N)      :scalar-sign           :exec        '(1)
    :scalar     '(-92N)     :scalar-sign           :exec        '(-1)
    :scalar     '(9/2)      :scalar-sign           :exec        '(1)
    :scalar     '(-9/2)     :scalar-sign           :exec        '(-1)
    :scalar     '()         :scalar-sign           :exec        '()
    )




(tabular
  (fact ":scalar-sine produces the sine of the top :scalar, read as radians"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction      ?get-stack  ?expected
    :scalar     '(0)      :scalar-sine       :exec    (list (Math/sin 0))
    :scalar     '(-1.0)   :scalar-sine       :exec    (list (Math/sin -1.0))
    :scalar     '(-1.0M)  :scalar-sine       :exec    (list (Math/sin -1.0M))
    :scalar     '(300.1M) :scalar-sine       :exec    (list (Math/sin 300.1M))
    :scalar     '(300M)   :scalar-sine       :exec    (list (Math/sin 300M))
    :scalar     '(300N)   :scalar-sine       :exec    (list (Math/sin 300N))
    :scalar     '(1.7e83) :scalar-sine       :exec    (list (Math/sin 1.7e83))
    :scalar     '(7/3)    :scalar-sine       :exec    (list (Math/sin 7/3))
    :scalar     (list Math/PI)
                          :scalar-sine       :exec    (list (Math/sin Math/PI))
    :scalar     '()       :scalar-sine       :exec    '()
    )




(tabular
  (fact ":scalar-subtract returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-subtract     :exec        '(-84M)
    :scalar     '(-92M 8)       :scalar-subtract     :exec        '(100M)
    :scalar     '(0 1)          :scalar-subtract     :exec        '(1)
    :scalar     '(-3 12)        :scalar-subtract     :exec        '(15)
    :scalar     '(92 -11/12)    :scalar-subtract     :exec        '(-1115/12)
    :scalar     '(-92 -11/2)    :scalar-subtract     :exec        '(173/2)
    :scalar     '(92N 8.7)      :scalar-subtract     :exec        '(-83.3)
    :scalar     '(8.7 92N)      :scalar-subtract     :exec        '(83.3)
    :scalar     '(-92M 8.7)     :scalar-subtract     :exec        '(100.7)
    :scalar     '(9/2 1.1)      :scalar-subtract     :exec        '(-3.4)
    :scalar     '(1.1 9/2)      :scalar-subtract     :exec        '(3.4)
    :scalar     '(-2/9 2.0)     :scalar-subtract     :exec     '(2.2222222222222223)
    :scalar     '()             :scalar-subtract     :exec        '()
    )



(tabular
  (fact ":scalar-subtract passes along runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(1M 1/3)        :scalar-subtract     :exec       '()
    :scalar     '(1M 1/3)        :scalar-subtract     :error      '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    )




(tabular
  (fact ":scalar-subtract creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list ∞ ∞)      :scalar-subtract    :exec         '()
    :scalar     (list ∞ ∞)      :scalar-subtract    :error         '({:step 0, :item ":scalar-subtract produced NaN"}))



(tabular
  (fact ":scalar-π pushes pi"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-π     :exec         '(3.141592653589793)
    )




(tabular
  (fact ":scalar-reciprocal the reciprocal of top :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction         ?get-stack   ?expected
    :scalar     '(1)        :scalar-reciprocal        :exec       '(1)
    :scalar     '(4/9)      :scalar-reciprocal        :exec       '(9/4)
    :scalar     '(100)      :scalar-reciprocal        :exec       '(1/100)
    :scalar     '(81N)      :scalar-reciprocal        :exec       '(1/81)
    :scalar     '(10000.0)  :scalar-reciprocal        :exec       '(0.0001)
    :scalar     '(0.64)     :scalar-reciprocal        :exec       '(1.5625)

    :scalar     '(8.1M)     :scalar-reciprocal        :exec       '()
    :scalar     '(8.1M)     :scalar-reciprocal        :error      '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    :scalar     '(0)        :scalar-reciprocal        :exec       '()
    :scalar     '(0)        :scalar-reciprocal        :error      '({:item "Divide by zero", :step 0})

    :scalar     '()         :scalar-reciprocal        :exec       '()
    )




(tabular
  (fact ":scalar-sqrt pushes the sqrt of a :scalar, whether it is real or imaginary, to :exec"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?expected
    :scalar     '(1)      :scalar-sqrt        :exec        '(1)
    :scalar     '(4/9)    :scalar-sqrt        :exec        '(2/3)
    :scalar     '(100)    :scalar-sqrt        :exec        '(10)
    :scalar     '(81)     :scalar-sqrt        :exec        '(9)
    :scalar     '(10000)  :scalar-sqrt        :exec        '(100)
    :scalar     '(0.64)   :scalar-sqrt        :exec        '(0.8)


    :scalar     '(0)      :scalar-sqrt        :exec        '(0)
    :scalar     '(-16)    :scalar-sqrt        :exec        (list (cpx/complexify 0 4))
    :scalar     '(-1/4)   :scalar-sqrt        :exec        (list (cpx/complexify 0 1/2))
    :scalar     '(-100N)  :scalar-sqrt        :exec        (list (cpx/complexify 0 10N))
    :scalar     '(-6.25M) :scalar-sqrt        :exec        (list (cpx/complexify 0 2.5M))
    :scalar     '(-16e20) :scalar-sqrt        :exec        (list (cpx/complexify 0 4e10))

    :scalar     '()       :scalar-sqrt        :exec        '()
    )




(tabular
  (fact ":scalar-tangent the tangent of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?expected
    :scalar     '(1)      :scalar-tangent     :exec       (list (Math/tan 1))
    :scalar     '(0)      :scalar-tangent     :exec       '(0.0)


    :scalar     (list ∞)  :scalar-tangent     :exec       '()
    :scalar     (list ∞)  :scalar-tangent     :error       '({:step 0, :item ":scalar-tangent bad argument"})

    :scalar     '()       :scalar-tangent     :exec       '()
    )




(tabular
  (fact ":integer-totalistic3 does some weird stuff"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction             ?get-stack     ?expected
    :scalar     '(1182)     :integer-totalistic3   :exec        '(114)
    :scalar     '(-39812M)  :integer-totalistic3   :exec        '(-8164)
    :scalar     '(235235235)
                            :integer-totalistic3   :exec        '(0)
    :scalar     (list Long/MIN_VALUE)
                            :integer-totalistic3   :exec        '(-3783295979768903679)
    :scalar     '(123456788161617/826317623)
                            :integer-totalistic3   :exec        '(473960)
    :scalar     '(-123456788161617.826317623)
                            :integer-totalistic3   :exec    '(-692581375838490)
    )




(tabular
  (fact ":integer-infinite? checks for positive or negative infinite values"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction          ?get-stack     ?expected
    :scalar     '(1182)   :scalar-infinite?      :exec         '(false)
    :scalar     (list ∞)  :scalar-infinite?      :exec         '(true)
    :scalar     (list -∞) :scalar-infinite?      :exec         '(true)
    )
