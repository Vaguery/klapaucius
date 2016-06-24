(ns push.instructions.base.scalar_math_test
  (:require [push.interpreter.core :as i]
            [push.type.definitions.complex :as cpx])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.scalar])
  )


;; fixtures

(def cljInf  Double/POSITIVE_INFINITY)
(def cljNinf Double/NEGATIVE_INFINITY)
(def cljNaN  (Math/sin Double/POSITIVE_INFINITY))



(tabular
  (fact ":scalar-abs returns the absolute value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M)          :scalar-abs           :scalar        '(92M)
    :scalar     '(-92M)         :scalar-abs           :scalar        '(92M)
    :scalar     '(0)            :scalar-abs           :scalar        '(0)
    :scalar     '(-0)           :scalar-abs           :scalar        '(0)
    :scalar     '(92)           :scalar-abs           :scalar        '(92)
    :scalar     '(-92)          :scalar-abs           :scalar        '(92)
    :scalar     '(9.2)          :scalar-abs           :scalar        '(9.2)
    :scalar     '(-9.2)         :scalar-abs           :scalar        '(9.2)
    :scalar     '(92N)          :scalar-abs           :scalar        '(92N)
    :scalar     '(-92N)         :scalar-abs           :scalar        '(92N)
    :scalar     '(9/2)          :scalar-abs           :scalar        '(9/2)
    :scalar     '(-9/2)         :scalar-abs           :scalar        '(9/2)

    :scalar     (list cljInf)
                                :scalar-abs           :scalar        (list cljInf)
    :scalar     (list cljNinf)
                                :scalar-abs           :scalar        (list cljInf)
    :scalar     '()             :scalar-abs           :scalar        '()
    )



(tabular
  (fact ":scalar-add returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-add        :scalar      '(100M)
    :scalar     '(-92M 8)       :scalar-add        :scalar      '(-84M)
    :scalar     '(0 1)          :scalar-add        :scalar      '(1)
    :scalar     '(-3 12)        :scalar-add        :scalar      '(9)
    :scalar     '(92 -11/12)    :scalar-add        :scalar      '(1093/12)
    :scalar     '(-92 -11/2)    :scalar-add        :scalar      '(-195/2)
    :scalar     '(92N 8.7)      :scalar-add        :scalar      '(100.7)
    :scalar     '(-92M 8.7)     :scalar-add        :scalar      '(-83.3)
    :scalar     '(9/2 1.1)      :scalar-add        :scalar      '(5.6)
    :scalar     '(-2/9 2.0)     :scalar-add        :scalar      '(1.7777777777777777)


    :scalar     (list cljInf 1)
                                :scalar-add        :scalar      (list cljInf)
    :scalar     (list cljNinf 1)
                                :scalar-add        :scalar      (list cljNinf)
    :scalar     '(1e9999M 1e99)
                                :scalar-add        :scalar      (list cljInf)
    :scalar     '()             :scalar-add        :scalar      '()
    )





(tabular
  (fact ":scalar-add returns an `:error` when the arguments blow up"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack    ?expected
    :scalar     '(1M 1/3)        :scalar-add        :scalar     '()
    :scalar     '(1M 1/3)        :scalar-add        :error      '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    )




(tabular
  (fact ":scalar-add creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list cljInf cljNinf)
                                :scalar-add        :scalar        '()
    :scalar     (list cljInf cljNinf)
                                :scalar-add        :error         '({:step 0, :item ":scalar-add produced NaN"}))





(tabular
  (fact ":scalar-arccosine the arccosine of a :scalar that falls between -1 and 1 (inclusive)"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arccosine   :scalar      '(1.5707963267948966)
    :scalar     '(1)
                          :scalar-arccosine   :scalar      '(0.0)
    :scalar     '(-1)
                          :scalar-arccosine   :scalar      '(3.141592653589793)
    :scalar     '(1/17)
                          :scalar-arccosine   :scalar      '(1.5119388208478153)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arccosine   :scalar      '(1.5707963267948966)


    :scalar     '(-2)     :scalar-arccosine   :scalar      '()
    :scalar     '(-2)     :scalar-arccosine   :error       (list {:step 0, 
                                                                  :item ":scalar-arccosine bad argument"})

    :scalar     (list cljInf)
                          :scalar-arccosine   :scalar      '()
    :scalar     (list cljInf)
                          :scalar-arccosine   :error       (list {:step 0,
                                                                  :item ":scalar-arccosine bad argument"})
    :scalar     '()       :scalar-arccosine   :scalar      '()
    )



(tabular
  (fact ":scalar-arcsine the arcsine of a :scalar that falls between -1 and 1 (inclusive)"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arcsine     :scalar      '(0.0)
    :scalar     '(1)
                          :scalar-arcsine     :scalar      '(1.5707963267948966)
    :scalar     '(-1)
                          :scalar-arcsine     :scalar      '(-1.5707963267948966)
    :scalar     '(1/17)
                          :scalar-arcsine     :scalar      '(0.05885750594708124)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arcsine     :scalar      (list (Math/asin 0.00000000000000000001M))


    :scalar     '(-2)     :scalar-arcsine     :scalar      '()
    :scalar     '(-2)     :scalar-arcsine     :error       (list {:step 0, 
                                                                  :item ":scalar-arcsine bad argument"})

    :scalar     (list cljInf)
                          :scalar-arcsine     :scalar      '()
    :scalar     (list cljInf)
                          :scalar-arcsine     :error       (list {:step 0,
                                                                  :item ":scalar-arcsine bad argument"})
    :scalar     '()       :scalar-arcsine     :scalar      '()
    )




(tabular
  (fact ":scalar-arctangent the arctangent of a :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction        ?get-stack   ?expected
    :scalar     '(0.0)    :scalar-arctangent  :scalar      '(0.0)
    :scalar     '(1)
                          :scalar-arctangent  :scalar      '(0.7853981633974483)
    :scalar     '(-1)
                          :scalar-arctangent  :scalar      '(-0.7853981633974483)
    :scalar     '(1/17)
                          :scalar-arctangent  :scalar      '(0.0587558227157227)
    :scalar     '(0.00000000000000000001M)
                          :scalar-arctangent  :scalar      (list (Math/asin 0.00000000000000000001M))

    :scalar     '(-2)     :scalar-arctangent  :scalar     '(-1.1071487177940904)
    :scalar     (list Math/PI)
                          :scalar-arctangent  :scalar     '(1.2626272556789115)

    :scalar     '()       :scalar-arctangent     :scalar      '()
    )





(tabular
  (fact ":scalar-ceiling applies clojure.math.numeric-tower/ceil to the top :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-ceiling     :scalar    '(0)
    :scalar     '(-1.0)    :scalar-ceiling     :scalar    '(-1.0)
    :scalar     '(-1.0M)   :scalar-ceiling     :scalar    '(-1N)
    :scalar     '(300.1M)  :scalar-ceiling     :scalar    '(301N)
    :scalar     '(-300.1M) :scalar-ceiling     :scalar    '(-300N)
    :scalar     '(300N)    :scalar-ceiling     :scalar    '(300N)
    :scalar     '(1.7e83)  :scalar-ceiling     :scalar    '(1.7E83)
    :scalar     '(7/3)     :scalar-ceiling     :scalar    '(3N)
    
    :scalar     '(1.7e837M)
                           :scalar-ceiling     :scalar    (list (bigint 1.7e837M))
    :scalar     (list cljInf)
                           :scalar-ceiling     :scalar    (list cljInf)
    :scalar     (list cljNinf)
                           :scalar-ceiling     :scalar    (list cljNinf)
    :scalar     '()        :scalar-ceiling     :scalar    '()
    )



(tabular
  (fact ":scalar-cosine produces the cosine of the top :scalar, read as radians"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction      ?get-stack  ?expected
    :scalar     '(0)      :scalar-cosine     :scalar    '(1.0)
    :scalar     '(-1.0)   :scalar-cosine     :scalar    '(0.5403023058681398)
    :scalar     '(-1.0M)  :scalar-cosine     :scalar    '(0.5403023058681398)
    :scalar     '(300.1M) :scalar-cosine     :scalar    '(0.07782281308912051)
    :scalar     '(300M)   :scalar-cosine     :scalar    '(-0.022096619278683942)
    :scalar     '(300N)   :scalar-cosine     :scalar    '(-0.022096619278683942)
    :scalar     '(1.7e83) :scalar-cosine     :scalar    '(-0.020932220708646424)
    :scalar     '(7/3)    :scalar-cosine     :scalar    '(-0.6907581397498761)
    :scalar     (list Math/PI)
                          :scalar-cosine     :scalar    '(-1.0)
    :scalar     '()       :scalar-cosine     :scalar    '()
    )



(tabular
  (fact ":scalar-dec decrements the top :scalar by 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-dec           :scalar        '(91M)
    :scalar     '(-92M)     :scalar-dec           :scalar        '(-93M)
    :scalar     '(0)        :scalar-dec           :scalar        '(-1)
    :scalar     '(-0)       :scalar-dec           :scalar        '(-1)
    :scalar     '(92)       :scalar-dec           :scalar        '(91)
    :scalar     '(-92)      :scalar-dec           :scalar        '(-93)
    :scalar     '(9.2)      :scalar-dec           :scalar        '(8.2)
    :scalar     '(-9.2)     :scalar-dec           :scalar        '(-10.2)
    :scalar     '(92N)      :scalar-dec           :scalar        '(91N)
    :scalar     '(-92N)     :scalar-dec           :scalar        '(-93N)
    :scalar     '(9/2)      :scalar-dec           :scalar        '(7/2)
    :scalar     '(-9/2)     :scalar-dec           :scalar        '(-11/2)


    :scalar     (list cljInf)
                            :scalar-dec           :scalar        (list cljInf)
    :scalar     (list cljNinf)
                            :scalar-dec           :scalar        (list cljNinf)
    :scalar     '()         :scalar-dec           :scalar        '()
    )



(tabular
  (fact ":scalar-divide produces the quotient of the top two :scalars, or an :error f the top one (numerator) is zero"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 8)}       :scalar-divide     {:scalar '(2/23)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 0)}       :scalar-divide     {:scalar '(0)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0 92)}       :scalar-divide     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 -11/12)}  :scalar-divide     {:scalar '(-11/1104)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-divide     {:scalar '(9/7)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-divide     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(9.2 -11/12)} :scalar-divide     {:scalar (list (/ -11/12 9.2))
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2N 6/7)}     :scalar-divide     {:scalar '(3/7)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0N 8)}       :scalar-divide     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-divide     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2M 3M)}       :scalar-divide     {:scalar '(1.5M)
                                                   :error '()} 


    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   (list cljInf 3M)}
                               :scalar-divide     {:scalar '(0.0)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   (list 3M cljInf)}
                               :scalar-divide     {:scalar (list cljInf)
                                                   :error '()}
)




(tabular
  (fact ":scalar-divide captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(1/3 10M)      :scalar-divide     :scalar         '()
    :scalar     '(1/3 10M)      :scalar-divide     :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-divide creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list cljInf cljInf)
                                :scalar-divide        :scalar        '()
    :scalar     (list cljInf cljInf)
                                :scalar-divide        :error         '({:step 0, :item ":scalar-divide produced NaN"}))




(tabular
  (fact ":scalar-E pushes e"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-E     :scalar        '(2.718281828459045)
    )



(tabular
  (fact ":scalar-floor applies clojure.math.numeric-tower/floor to the top :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-floor     :scalar    '(0)
    :scalar     '(-1.0)    :scalar-floor     :scalar    '(-1.0)
    :scalar     '(-1.0M)   :scalar-floor     :scalar    '(-1N)
    :scalar     '(300.1M)  :scalar-floor     :scalar    '(300N)
    :scalar     '(-300.1M) :scalar-floor     :scalar    '(-301N)
    :scalar     '(300N)    :scalar-floor     :scalar    '(300N)
    :scalar     '(1.7e83)  :scalar-floor     :scalar    '(1.7E83)
    :scalar     '(7/3)     :scalar-floor     :scalar    '(2N)
    
    :scalar     (list (+ 0.1M (bigint 1.7e837M)))
                           :scalar-floor     :scalar    (list (bigint 1.7e837M))
    :scalar     (list (+ 0.1 (bigint 1.7e837M)))
                           :scalar-floor     :scalar    (list cljInf)
    :scalar     (list cljInf)
                           :scalar-floor     :scalar    (list cljInf)
    :scalar     (list cljNinf)
                           :scalar-floor     :scalar    (list cljNinf)
    :scalar     '()        :scalar-floor     :scalar    '()
    )



(tabular
  (fact ":scalar-fractional produces just the remainder mod 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction        ?get-stack  ?expected
    :scalar     '(0)      :scalar-fractional     :scalar    '(0)
    :scalar     '(0.25)   :scalar-fractional     :scalar    '(0.25)
    :scalar     '(11.25)  :scalar-fractional     :scalar    '(0.25)
    :scalar     '(-11.25) :scalar-fractional     :scalar    '(0.25)
    :scalar     '(1e-2)   :scalar-fractional     :scalar    '(0.01)
    :scalar     '(-1e-2)  :scalar-fractional     :scalar    '(0.01)
    
    :scalar     '(1/3)    :scalar-fractional     :scalar    '(1/3)
    :scalar     '(11/3)   :scalar-fractional     :scalar    '(2/3)
    :scalar     '(-4/3)   :scalar-fractional     :scalar    '(1/3)

    :scalar     '(17N)    :scalar-fractional     :scalar    '(0)

    :scalar     '(17M)    :scalar-fractional     :scalar    '(0M)
    :scalar     '(17.2M)  :scalar-fractional     :scalar    '(0.2M)
    :scalar     '(-7.2M)  :scalar-fractional     :scalar    '(0.2M)
    :scalar     '(1e-2M)  :scalar-fractional     :scalar    '(0.01M)
    )




(tabular
  (fact ":scalar-inc increments the top :scalar by 1"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-inc           :scalar        '(93M)
    :scalar     '(-92M)     :scalar-inc           :scalar        '(-91M)
    :scalar     '(0)        :scalar-inc           :scalar        '(1)
    :scalar     '(-0)       :scalar-inc           :scalar        '(1)
    :scalar     '(92)       :scalar-inc           :scalar        '(93)
    :scalar     '(-92)      :scalar-inc           :scalar        '(-91)
    :scalar     '(9.2)      :scalar-inc           :scalar        '(10.2)
    :scalar     '(-9.2)     :scalar-inc           :scalar        '(-8.2)
    :scalar     '(92N)      :scalar-inc           :scalar        '(93N)
    :scalar     '(-92N)     :scalar-inc           :scalar        '(-91N)
    :scalar     '(9/2)      :scalar-inc           :scalar        '(11/2)
    :scalar     '(-9/2)     :scalar-inc           :scalar        '(-7/2)



    :scalar     (list cljInf)         
                            :scalar-inc           :scalar        (list cljInf)
    :scalar     '()         :scalar-inc           :scalar        '()
    )



(tabular
  (fact ":scalar-modulo produces the quotient of the top two :scalars, or an :error f the top one (numerator) is zero"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 8)}       :scalar-modulo     {:scalar '(8)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(8 93)}       :scalar-modulo     {:scalar '(5)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0 92)}       :scalar-modulo     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1 21/12)}  :scalar-modulo     {:scalar '(3/4)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-modulo     {:scalar '(4/21)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-modulo     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0.25 -11/8)} :scalar-modulo     {:scalar '(0.125)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(6/7 2N)}     :scalar-modulo     {:scalar '(2/7)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 3N)}     :scalar-modulo     {:scalar '(1/3)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0N 8)}       :scalar-modulo     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-modulo     {:scalar '()
                                                   :error '({:item "Divide by zero", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1.2M 32M)}    :scalar-modulo  {:scalar '(0.8M)
                                                   :error '()} 
                                                   )



(tabular
  (fact ":scalar-modulo captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(3/7 10M)      :scalar-modulo     :scalar         '()
    :scalar     '(3/7 10M)      :scalar-modulo     :error         '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-modulo creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list cljInf cljInf)
                                :scalar-modulo        :scalar        '()
    :scalar     (list cljInf cljInf)
                                :scalar-modulo        :error         '({:item "Infinite or NaN", :step 0}))



(tabular
  (fact ":scalar-ln the ln of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack   ?expected
    :scalar     (list Math/E)
                          :scalar-ln        :scalar      '(1.0)
    :scalar     (list (* 2 Math/E))
                          :scalar-ln        :scalar      '(1.6931471805599452)
    :scalar     (list (* Math/E Math/E))
                          :scalar-ln        :scalar      '(2.0)
    :scalar     (list (* Math/E Math/E Math/E))
                          :scalar-ln        :scalar      '(3.0)


    :scalar     '(0)      :scalar-ln        :scalar      '()
    :scalar     '(0)      :scalar-ln        :error       '({:step 0, :item ":scalar-ln bad argument"})
    :scalar     '(-2712893)      
                          :scalar-ln        :scalar      '()
    :scalar     '(-2712893)      
                          :scalar-ln        :error       '({:step 0, :item ":scalar-ln bad argument"})

    :scalar     '()       :scalar-ln        :scalar      '()
    )




(tabular
  (fact ":scalar-ln1p the ln1p of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction     ?get-stack   ?expected
    :scalar     (list (dec' Math/E))
                          :scalar-ln1p        :scalar      '(1.0)
    :scalar     (list (dec' (* 2 Math/E)))
                          :scalar-ln1p        :scalar      '(1.6931471805599454)
    :scalar     (list (dec' (* Math/E Math/E)))
                          :scalar-ln1p        :scalar      '(2.0)
    :scalar     (list (dec' (* Math/E Math/E Math/E)))
                          :scalar-ln1p        :scalar      '(3.0)

    :scalar     '(0)      :scalar-ln1p        :scalar      '(0.0)


    :scalar     '(-1)      :scalar-ln1p        :scalar      '()
    :scalar     '(-1)      :scalar-ln1p        :error       '({:step 0, :item ":scalar-ln1p bad argument"})
    :scalar     '(-2712893)      
                          :scalar-ln1p        :scalar      '()
    :scalar     '(-2712893)      
                          :scalar-ln1p        :error       '({:step 0, :item ":scalar-ln1p bad argument"})

    :scalar     '()       :scalar-ln1p        :scalar      '()
    )




(tabular
  (fact ":scalar-log10 the log10 of a :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack   ?expected
    :scalar     '(1)      :scalar-log10        :scalar      '(0.0)
    :scalar     '(1/100)  :scalar-log10        :scalar      '(-2.0)
    :scalar     '(10)     :scalar-log10        :scalar      '(1.0)
    :scalar     '(100)    :scalar-log10        :scalar      '(2.0)
    :scalar     '(1000)   :scalar-log10        :scalar      '(3.0)


    :scalar     '(0)      :scalar-log10        :scalar      '()
    :scalar     '(0)      :scalar-log10        :error       '({:step 0, :item ":scalar-log10 bad argument"})
    :scalar     '(-2712893)      
                          :scalar-log10        :scalar      '()
    :scalar     '(-2712893)      
                          :scalar-log10        :error       '({:step 0, :item ":scalar-log10 bad argument"})

    :scalar     '()       :scalar-log10        :scalar      '()
    )




(tabular
  (fact ":scalar-multiply returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-multiply     :scalar      '(736M)
    :scalar     '(-92M 8)       :scalar-multiply     :scalar      '(-736M)
    :scalar     '(0 1)          :scalar-multiply     :scalar      '(0)
    :scalar     '(-3 12)        :scalar-multiply     :scalar      '(-36)
    :scalar     '(92 -11/12)    :scalar-multiply     :scalar      '(-253/3)
    :scalar     '(-92 -11/2)    :scalar-multiply     :scalar      '(506N)
    :scalar     '(92N 8.7)      :scalar-multiply     :scalar      '(800.4)
    :scalar     '(8.7 92N)      :scalar-multiply     :scalar      '(800.4)
    :scalar     '(-92M 8.7)     :scalar-multiply     :scalar      '(-800.4)
    :scalar     '(9/2 1.1)      :scalar-multiply     :scalar      '(4.95)
    :scalar     '(-2/9 2.0)     :scalar-multiply     :scalar   '(-0.4444444444444444)
    :scalar     '()             :scalar-multiply     :scalar      '()
    )




(tabular
  (fact ":scalar-multiply captures runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(10M 1/3)      :scalar-multiply     :scalar      '()
    :scalar     '(10M 1/3)      :scalar-multiply     :error      '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    )




(tabular
  (fact ":scalar-multiply creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list 0 cljInf)
                                :scalar-multiply        :scalar        '()
    :scalar     (list 0 cljInf)
                                :scalar-multiply        :error         '({:step 0, :item ":scalar-multiply produced NaN"}))



(tabular
  (future-fact ":scalar-power produces the result of raising the second :scalar to the power of the first, or an :error if the result is not a scalar"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks scalar-type ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction     ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(2 8)}       :scalar-power     {:scalar '(64)
                                              :error '()} 
    {:scalar '(3 3/7)}     :scalar-power     {:scalar '(27/343)
                                              :error '()} 
    {:scalar '(1/2 100.0)} :scalar-power     {:scalar '(10.0)
                                              :error '()} 
    {:scalar '(0.5 100.0)} :scalar-power     {:scalar '(10.0)
                                              :error '()} 
    {:scalar '(3/7 100)}   :scalar-power     {:scalar '(7.196856730011521)
                                              :error '()} 
    {:scalar '(1/2 0)}     :scalar-power     {:scalar '(0.0)
                                              :error '()} 
    {:scalar '(-2.8 100)}  :scalar-power     {:scalar '(2.5118864315095823E-6)
                                              :error '()} 
    {:scalar '(-1/2 100)}  :scalar-power     {:scalar '(0.1)
                                              :error '()} 
    {:scalar '(-2 100)}    :scalar-power     {:scalar '(1/10000)
                                              :error '()} 
    {:scalar '(-1e13 1e13)} :scalar-power    {:scalar '(0.0)
                                              :error '()}


    {:scalar '(0.5 -10.0)} :scalar-power     {:scalar '()
                                              :error '({:item ":scalar-power did not produce a :scalar result", :step 0})}
    {:scalar '(1/13 -10)}  :scalar-power     {:scalar '()
                                              :error '({:item ":scalar-power did not produce a :scalar result", :step 0})}
    {:scalar '(1e13 1e13)}  :scalar-power    {:scalar '()
                                              :error '({:item ":scalar-power did not produce a :scalar result", :step 0})}
    {:scalar '(1e13 -1e13)} :scalar-power    {:scalar '()
                                              :error '({:item ":scalar-power did not produce a :scalar result", :step 0})}
)





(tabular
  (fact ":scalar-round applies clojure.math.numeric-tower/round to the top :scalar to produce a `long` result"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction      ?get-stack  ?expected
    :scalar     '(0)       :scalar-round     :scalar    '(0)
    :scalar     '(0.4)     :scalar-round     :scalar    '(0)
    :scalar     '(0.6)     :scalar-round     :scalar    '(1)
    :scalar     '(0.5)     :scalar-round     :scalar    '(1)
    :scalar     '(1.5)     :scalar-round     :scalar    '(2)
    :scalar     '(-1.1)    :scalar-round     :scalar    '(-1)
    :scalar     '(-1.5)    :scalar-round     :scalar    '(-1)
    :scalar     '(-1.0M)   :scalar-round     :scalar    '(-1N)
    :scalar     '(300.1M)  :scalar-round     :scalar    '(300N)
    :scalar     '(-300.1M) :scalar-round     :scalar    '(-300N)
    :scalar     '(300M)    :scalar-round     :scalar    '(300N)
    :scalar     '(1.7e83)  :scalar-round     :scalar    '(9223372036854775807)
    :scalar     '(1.7e83M) :scalar-round     :scalar    (list (bigint 1.7e83M))
    :scalar     '(7/3)     :scalar-round     :scalar    '(2N)
    
    :scalar     (list (+ 0.1M (bigint 1.7e837M)))
                           :scalar-round     :scalar    (list (bigint 1.7e837M))
    :scalar     (list (+ 0.1 (bigint 1.7e837M)))
                           :scalar-round     :scalar    '(9223372036854775807)
    :scalar     (list cljInf)
                           :scalar-round     :scalar    '(9223372036854775807)
    :scalar     (list cljNinf)
                           :scalar-round     :scalar    '(-9223372036854775808)
    :scalar     '()        :scalar-round     :scalar    '()
    )





(tabular
  (fact ":scalar-sign returns -1 if :scalar is negative, 0 if zero, 1 if positive"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction         ?get-stack     ?expected
    :scalar     '(92M)      :scalar-sign           :scalar        '(1)
    :scalar     '(-92M)     :scalar-sign           :scalar        '(-1)
    :scalar     '(0)        :scalar-sign           :scalar        '(0)
    :scalar     '(-0)       :scalar-sign           :scalar        '(0)
    :scalar     '(92)       :scalar-sign           :scalar        '(1)
    :scalar     '(-92)      :scalar-sign           :scalar        '(-1)
    :scalar     '(9.2)      :scalar-sign           :scalar        '(1)
    :scalar     '(-9.2)     :scalar-sign           :scalar        '(-1)
    :scalar     '(92N)      :scalar-sign           :scalar        '(1)
    :scalar     '(-92N)     :scalar-sign           :scalar        '(-1)
    :scalar     '(9/2)      :scalar-sign           :scalar        '(1)
    :scalar     '(-9/2)     :scalar-sign           :scalar        '(-1)
    :scalar     '()         :scalar-sign           :scalar        '()
    )




(tabular
  (fact ":scalar-sine produces the sine of the top :scalar, read as radians"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction      ?get-stack  ?expected
    :scalar     '(0)      :scalar-sine       :scalar    (list (Math/sin 0))
    :scalar     '(-1.0)   :scalar-sine       :scalar    (list (Math/sin -1.0))
    :scalar     '(-1.0M)  :scalar-sine       :scalar    (list (Math/sin -1.0M))
    :scalar     '(300.1M) :scalar-sine       :scalar    (list (Math/sin 300.1M))
    :scalar     '(300M)   :scalar-sine       :scalar    (list (Math/sin 300M))
    :scalar     '(300N)   :scalar-sine       :scalar    (list (Math/sin 300N))
    :scalar     '(1.7e83) :scalar-sine       :scalar    (list (Math/sin 1.7e83))
    :scalar     '(7/3)    :scalar-sine       :scalar    (list (Math/sin 7/3))
    :scalar     (list Math/PI)
                          :scalar-sine       :scalar    (list (Math/sin Math/PI))
    :scalar     '()       :scalar-sine       :scalar    '()
    )




(tabular
  (fact ":scalar-subtract returns the sum of the top two :scalar items"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction        ?get-stack     ?expected
    :scalar     '(92M 8)        :scalar-subtract     :scalar      '(-84M)
    :scalar     '(-92M 8)       :scalar-subtract     :scalar      '(100M)
    :scalar     '(0 1)          :scalar-subtract     :scalar      '(1)
    :scalar     '(-3 12)        :scalar-subtract     :scalar      '(15)
    :scalar     '(92 -11/12)    :scalar-subtract     :scalar      '(-1115/12)
    :scalar     '(-92 -11/2)    :scalar-subtract     :scalar      '(173/2)
    :scalar     '(92N 8.7)      :scalar-subtract     :scalar      '(-83.3)
    :scalar     '(8.7 92N)      :scalar-subtract     :scalar      '(83.3)
    :scalar     '(-92M 8.7)     :scalar-subtract     :scalar      '(100.7)
    :scalar     '(9/2 1.1)      :scalar-subtract     :scalar      '(-3.4)
    :scalar     '(1.1 9/2)      :scalar-subtract     :scalar      '(3.4)
    :scalar     '(-2/9 2.0)     :scalar-subtract     :scalar   '(2.2222222222222223)
    :scalar     '()             :scalar-subtract     :scalar      '()
    )



(tabular
  (fact ":scalar-subtract passes along runtime errors"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack       ?expected
    :scalar     '(1M 1/3)        :scalar-subtract     :scalar      '()
    :scalar     '(1M 1/3)        :scalar-subtract     :error      '({:step 0, :item "Non-terminating decimal expansion; no exact representable decimal result."})
    )




(tabular
  (fact ":scalar-subtract creates an `:error` if the result would be a NaN value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     (list cljInf cljInf)
                                :scalar-subtract        :scalar        '()
    :scalar     (list cljInf cljInf)
                                :scalar-subtract        :error         '({:step 0, :item ":scalar-subtract produced NaN"}))



(tabular
  (fact ":scalar-π pushes pi"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-π     :scalar        '(3.141592653589793)
    )




(tabular
  (fact ":scalar-reciprocal the reciprocal of top :scalar, or an :error if out of range"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction         ?get-stack   ?expected
    :scalar     '(1)        :scalar-reciprocal        :scalar      '(1)
    :scalar     '(4/9)      :scalar-reciprocal        :scalar      '(9/4)
    :scalar     '(100)      :scalar-reciprocal        :scalar      '(1/100)
    :scalar     '(81N)      :scalar-reciprocal        :scalar      '(1/81)
    :scalar     '(10000.0)  :scalar-reciprocal        :scalar      '(0.0001)
    :scalar     '(0.64)     :scalar-reciprocal        :scalar      '(1.5625)
  
    :scalar     '(8.1M)     :scalar-reciprocal        :scalar      '()
    :scalar     '(8.1M)     :scalar-reciprocal        :error      '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})
    :scalar     '(0)        :scalar-reciprocal        :scalar      '()
    :scalar     '(0)        :scalar-reciprocal        :error      '({:item "Divide by zero", :step 0})
  
    :scalar     '()         :scalar-reciprocal        :scalar      '()
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
    :scalar     '(1)      :scalar-tangent     :scalar      (list (Math/tan 1))
    :scalar     '(0)      :scalar-tangent     :scalar      '(0.0)
    

    :scalar     (list cljInf)
                          :scalar-tangent     :scalar      '()
    :scalar     (list cljInf)
                          :scalar-tangent     :error       '({:step 0, :item ":scalar-tangent bad argument"})

    :scalar     '()       :scalar-tangent     :scalar      '()
    )




(tabular
  (fact ":integer-totalistic3 does some weird stuff"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction             ?get-stack     ?expected
    :scalar     '(1182)     :integer-totalistic3   :scalar       '(114)
    :scalar     '(-39812M)  :integer-totalistic3   :scalar       '(-8164)
    :scalar     '(235235235)       
                            :integer-totalistic3   :scalar       '(0)
    :scalar     (list Long/MIN_VALUE)       
                            :integer-totalistic3   :scalar       '(-3783295979768903679)
    :scalar     '(123456788161617/826317623) 
                            :integer-totalistic3   :scalar       '(473960)
    :scalar     '(-123456788161617.826317623) 
                            :integer-totalistic3   :scalar       '(-692581375838490)
    )