(ns push.instructions.base.scalar_math_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
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


;    :scalar     (list cljInf cljNinf)
                                ; :scalar-add        :scalar      Double/isNaN


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
                                                   :error '({:item ":scalar-divide 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(92 -11/12)}  :scalar-divide     {:scalar '(-11/1104)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-divide     {:scalar '(9/7)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-divide     {:scalar '()
                                                   :error '({:item ":scalar-divide 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(9.2 -11/12)} :scalar-divide     {:scalar (list (/ -11/12 9.2))
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2N 6/7)}     :scalar-divide     {:scalar '(3/7)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0N 8)}       :scalar-divide     {:scalar '()
                                                   :error '({:item ":scalar-divide 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-divide     {:scalar '()
                                                   :error '({:item ":scalar-divide 0 denominator", :step 0})} 
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


;; out of bounds/edge cases
    ; {:scalar   '(92M 8)}       :scalar-divide     {:scalar '()
    ;                                                :error '()} 

    ;     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; {:scalar   (list cljInf cljInf)}
    ;                            :scalar-divide     {:scalar '(0.0)
    ;                                                :error '()} 




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
                                                   :error '({:item ":scalar-modulo 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1 21/12)}  :scalar-modulo     {:scalar '(3/4)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(2/3 6/7)}    :scalar-modulo     {:scalar '(4/21)
                                                   :error '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0/3 1/2)}    :scalar-modulo     {:scalar '()
                                                   :error '({:item ":scalar-modulo 0 denominator", :step 0})} 
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
                                                   :error '({:item ":scalar-modulo 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(0M 0N)}       :scalar-modulo     {:scalar '()
                                                   :error '({:item ":scalar-modulo 0 denominator", :step 0})} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar   '(1.2M 32M)}    :scalar-modulo  {:scalar '(0.8M)
                                                   :error '()} 
                                                   )


; ;;     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;     {:scalar   (list cljInf 32M)}       :scalar-modulo  {:scalar '(0.8M)
;                                                    :error '()} 

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; {:scalar   (list 1.2 cljInf)}
    ;                            :scalar-modulo  {:scalar '(0.8M)
    ;                                                :error '()} 




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


;; out-of-bounds and edge cases

    ; :scalar     '(-2/9 2.0M)     :scalar-multiply     :scalar   '(-0.4444444444444444)





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
  (fact ":scalar-π pushes pi"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-π     :scalar        '(3.141592653589793)
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