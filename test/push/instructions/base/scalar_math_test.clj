(ns push.instructions.base.scalar_math_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )



(tabular
  (fact ":scalar-abs returns the absolute value"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-abs           :scalar        '(92M)
    :scalar     '(-92M)     :scalar-abs           :scalar        '(92M)
    :scalar     '(0)        :scalar-abs           :scalar        '(0)
    :scalar     '(-0)       :scalar-abs           :scalar        '(0)
    :scalar     '(92)       :scalar-abs           :scalar        '(92)
    :scalar     '(-92)      :scalar-abs           :scalar        '(92)
    :scalar     '(9.2)      :scalar-abs           :scalar        '(9.2)
    :scalar     '(-9.2)     :scalar-abs           :scalar        '(9.2)
    :scalar     '(92N)      :scalar-abs           :scalar        '(92N)
    :scalar     '(-92N)     :scalar-abs           :scalar        '(92N)
    :scalar     '(9/2)      :scalar-abs           :scalar        '(9/2)
    :scalar     '(-9/2)     :scalar-abs           :scalar        '(9/2)
    :scalar     '()         :scalar-abs           :scalar        '()
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
    :scalar     '()             :scalar-add        :scalar      '()
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
)


;; out of bounds/edge cases
    ; {:scalar   '(92M 8)}       :scalar-divide     {:scalar '()
    ;                                                :error '()} 



(tabular
  (fact ":scalar-E pushes e"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    :scalar     '()        :scalar-E     :scalar        '(2.718281828459045)
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
    {:scalar   '(1.2M 32M)}       :scalar-modulo  {:scalar '(0.8M)
                                                   :error '()} 
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


;; out-of-bounds and edge cases

    ; :scalar     '(-2/9 2.0M)     :scalar-multiply     :scalar   '(-0.4444444444444444)



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