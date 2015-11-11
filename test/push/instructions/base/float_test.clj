(ns push.instructions.base.float_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.float])            ;; sets up classic-float-type
  )


;; these are tests of an Interpreter with the classic-float-type registered
;; the instructions under test are those stored IN THAT TYPE


(tabular
  (fact ":float-add returns the sum, auto-promoting overflows"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; adding
    :float    '(11.0 -5.0)  :float-add   :float     '(6.0)
    :float    '(-3.0 -5.0)  :float-add   :float     '(-8.0)
    ;; missing args
    :float    '(11.0)       :float-add   :float     '(11.0)
    :float    '()           :float-add   :float     '()
    ;; bigness
    :float    '(3.1e12 2.4e13)
                            :float-add   :float     '(2.71E13)
    ;; smallness
    :float    '(3.1e-88 2.4e-88)
                            :float-add   :float     '(5.5E-88))



(future-fact ":float-add deals with overflows")
(future-fact ":float-add deals with underflows")


(tabular
  (fact ":float-cosine returns the cosine(x)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; wavy
    :float    '(0.0)        :float-cosine    :float     '(1.0)
    :float    (list (/ Math/PI 1.0))
                            :float-cosine    :float     '(-1.0)

    :float    (list (* 2 (/ Math/PI -1.0)))       
                            :float-cosine    :float     '(1.0)
    ;; missing args
    :float    '()           :float-cosine    :float     '())



(tabular
  (fact ":float-divide returns the quotient, unless the denominator is 0.0"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; division
    :float    '(20.0 -5.0)  :float-divide   :float     '(-0.25)
    :float    '(-3.0 -15.0) :float-divide   :float     '(5.0)
    ;; zero
    :float    '(0.0 -5.0)   :float-divide   :float     '(0.0 -5.0)
    :float    '(-0.0 0.0)  :float-divide   :float      '(-0.0 0.0)
    ;; missing args
    :float    '(11.0)       :float-divide   :float     '(11.0)
    :float    '()           :float-divide   :float     '()
    ;; bigness
    :float    '(5.0e12 2.5e13)
                            :float-divide   :float     '(5.0)
    ;; smallness
    :float    '(3.0e-89 2.7e-88)
                            :float-divide   :float     '(9.0)
    :float    '(3.0 2.7e-88)
                            :float-divide   :float     '(9.0e-89)
    :float    '(3.0M 2.7e-88M)
                            :float-divide   :float     '(9.0e-89M))


(tabular
  (fact ":float-mod returns the modulo (positive remainder), unless the denominator is 0.0"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; leftovers
    :float    '(20.0 -5.0)  :float-mod   :float     '(15.0)
    :float    '(-3.0 -15.0) :float-mod   :float     '(0.0)
    ;; zero
    :float    '(0.0 -5.0)   :float-mod   :float     '(0.0 -5.0)
    :float    '(-0.0 0.0)  :float-mod   :float      '(-0.0 0.0)
    ;; missing args
    :float    '(11.0)       :float-mod   :float     '(11.0)
    :float    '()           :float-mod   :float     '()
    ;; bigness
    :float    '(5.0e12 2.4e13)
                            :float-mod   :float     '(4.0E12)
    ;; smallness
    :float    '(3.0e-89M 2.5e-88M)
                            :float-mod   :float     '(1.0E-89M)
    :float    '(3.0 2.7e-88)
                            :float-mod   :float     '(2.7e-88)
    :float    '(3.0M 2.7e-88M)
                            :float-mod   :float     '(2.7E-88M))



(tabular
  (fact ":float-inc returns the number increased by 1.0"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; up we go
    :float    '(11.0 -5.0)  :float-inc    :float     '(12.0 -5.0)
    :float    '(-3.0 -5.0)  :float-inc    :float     '(-2.0 -5.0)
    ;; missing args
    :float    '()           :float-inc    :float     '()
    ;; bigness
    :float    '(3.1e12)     :float-inc    :float     '(3.100000000001E12)
    ;; smallness
    :float    '(3.1e-77M)    :float-inc   :float
      '(1.000000000000000000000000000000000000000000000000000000000000000000000000000031M)
    :float    '(3.1e-88)     :float-inc   :float     '(1.0)) ;; hmm


(future-fact ":float-inc deals with overflows")
(future-fact ":float-inc deals with underflows")


(tabular
  (fact ":float-dec returns the value reduced by 1.0"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; down we go
    :float    '(11.0 -5.0)  :float-dec    :float     '(10.0 -5.0)
    :float    '(-3.0 -5.0)  :float-dec    :float     '(-4.0 -5.0)
    ;; missing args
    :float    '()           :float-dec    :float     '()
    ;; bigness
    :float    '(3.1e12)     :float-dec    :float     '(3.099999999999E12)
    ;; smallness
    :float    '(3.1e-77M)    :float-dec   :float     (list (dec' 3.1e-77M))
    :float    '(3.1e-88)     :float-dec   :float     '(-1.0)) ;; hmm


(tabular
  (fact ":float-multiply returns the sum, auto-promoting overflows"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; multiplying
    :float    '(11.0 -5.0)  :float-multiply   :float     '(-55.0)
    :float    '(-3.0 -5.0)  :float-multiply   :float     '(15.0)
    ;; missing args
    :float    '(11.0)       :float-multiply   :float     '(11.0)
    :float    '()           :float-multiply   :float     '()
    ;; bigness
    :float    '(3.0e12 2.5e13)
                            :float-multiply   :float     '(7.5E25)
    ;; smallness
    :float    '(3.0e-88 2.5e-88)
                            :float-multiply   :float     '(7.5E-176))



(tabular
  (fact ":float-sine returns the sine(x)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; up we go
    :float    '(0.0)        :float-sine    :float     '(0.0)
    :float    (list (/ Math/PI 2))
                            :float-sine    :float     '(1.0)

    :float    '(-3.0)  :float-sine    :float     '(-0.1411200080598672)
    ;; missing args
    :float    '()           :float-sine    :float     '())


(tabular
  (fact ":float-sign returns the sine(x)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; up we go
    :float    '(-0.0)        :float-sign    :float     '(0.0)
    :float    '(111.11)      :float-sign    :float     '(1.0)

    :float    '(-3.0)        :float-sign    :float     '(-1.0)
    ;; missing args
    :float    '()            :float-sign    :float     '())


(tabular
  (fact ":float-subtract returns the difference"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; subtracting
    :float    '(11.0 -5.0)  :float-subtract   :float     '(-16.0)
    :float    '(-3.0 -5.0)  :float-subtract   :float     '(-2.0)
    ;; missing args
    :float    '(11.0)       :float-subtract   :float     '(11.0)
    :float    '()           :float-subtract   :float     '()
    ;; bigness
    :float    '(3.1e12 2.4e13)
                            :float-subtract   :float     '(2.09E13)
    ;; smallness
    :float    '(3.0e-88M 2.5e-88M)
                            :float-subtract   :float     '(-5E-89M))

(tabular
  (fact ":float-tangent returns the tan(x)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; wavy
    :float    '(0.0)        :float-tangent    :float     '(0.0)
    :float    (list 3.0)
                            :float-tangent    :float     (list (Math/tan 3.0))

    ;; missing args
    :float    '()           :float-tangent    :float     '())


;; visible


(tabular
  (fact ":float-stackdepth returns the number of items on the :float stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :float    '(1.1 2.2 3.3)   :float-stackdepth   :integer      '(3)
    :float    '(1.0)           :float-stackdepth   :integer      '(1)
    :float    '()              :float-stackdepth   :integer      '(0))


(tabular
  (fact ":float-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :float    '(0.2 1.3e7)    :float-empty?   :boolean     '(false)
    :float    '()             :float-empty?   :boolean     '(true))


;; equatable


(tabular
  (fact ":float-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :float    '(1.2 1.3)       :float-equal?      :boolean        '(false)
    :float    '(1.3 1.2)       :float-equal?      :boolean        '(false)
    :float    '(1.2 1.2)       :float-equal?      :boolean        '(true)
    ;; missing args    
    :float    '(1.3)           :float-equal?      :boolean        '()
    :float    '(1.3)           :float-equal?      :float         '(1.3)
    :float    '()              :float-equal?      :boolean        '()
    :float    '()              :float-equal?      :float         '())


(tabular
  (fact ":float-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction  ?get-stack     ?expected
    ;; different
    :float    '(1.2 -1.2)       :float-notequal?      :boolean        '(true)
    :float    '(-1.2 1.2)       :float-notequal?      :boolean        '(true)
    :float    '(1.2 1.2)        :float-notequal?      :boolean        '(false)
    ;; missing args    
    :float    '(-1.2)           :float-notequal?      :boolean        '()
    :float    '(-1.2)           :float-notequal?      :float         '(-1.2)
    :float    '()               :float-notequal?      :boolean        '()
    :float    '()               :float-notequal?      :float         '())


;; comparable


(tabular
  (fact ":float<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; float comparisons are pretty weird, actually
    :float    '(1.1 2.2)       :float<?      :boolean        '(false)
    :float    '(2.2 1.1)       :float<?      :boolean        '(true)
    :float    '(0.0 0.0)       :float<?      :boolean        '(false)
    ;; missing args    
    :float    '(1.1)           :float<?      :boolean        '()
    :float    '(1.1)           :float<?      :float         '(1.1)
    :float    '()              :float<?      :boolean        '()
    :float    '()              :float<?      :float         '())


(tabular
  (fact ":float≤? returns a :boolean indicating whether :first ≤ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; float comparisons are pretty weird, actually
    :float    '(1.1 2.2)       :float≤?      :boolean        '(false)
    :float    '(2.2 1.1)       :float≤?      :boolean        '(true)
    :float    '(0.0 0.0)       :float≤?      :boolean        '(true)
    ;; missing args    
    :float    '(1.1)           :float≤?      :boolean        '()
    :float    '(1.1)           :float≤?      :float         '(1.1)
    :float    '()              :float≤?      :boolean        '()
    :float    '()              :float≤?      :float         '())



(tabular
  (fact ":float≥? returns a :boolean indicating whether :first ≥ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; float comparisons are pretty weird, actually
    :float    '(1.1 2.2)       :float≥?      :boolean        '(true)
    :float    '(2.2 1.1)       :float≥?      :boolean        '(false)
    :float    '(0.0 0.0)       :float≥?      :boolean        '(true)
    ;; missing args    
    :float    '(1.1)           :float≥?      :boolean        '()
    :float    '(1.1)           :float≥?      :float         '(1.1)
    :float    '()              :float≥?      :boolean        '()
    :float    '()              :float≥?      :float         '())


(tabular
  (fact ":float>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-float-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; float comparisons are pretty weird, actually
    :float    '(1.1 2.2)       :float>?      :boolean        '(true)
    :float    '(2.2 1.1)       :float>?      :boolean        '(false)
    :float    '(0.0 0.0)       :float>?      :boolean        '(false)
    ;; missing args    
    :float    '(1.1)           :float>?      :boolean        '()
    :float    '(1.1)           :float>?      :float         '(1.1)
    :float    '()              :float>?      :boolean        '()
    :float    '()              :float>?      :float         '())


;; movable