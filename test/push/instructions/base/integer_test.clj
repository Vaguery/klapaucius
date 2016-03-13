(ns push.instructions.base.integer_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.exotics])
  (:use [push.types.type.integer])
  )


;; quotable

(tabular
  (fact ":integer->code move the top :integer item to :code"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; move it!
    :integer       '(92)      :integer->code         :code        '(92)
    :integer       '()        :integer->code         :code       '()
    )


;; integer-specific instructions


(tabular
  (fact ":integer-abs returns the absolute value"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(4 20)   :integer-abs       :integer        '(4 20)
    :integer    '(-4 20)   :integer-abs      :integer        '(4 20)
    :integer    '(0 20)   :integer-abs       :integer        '(0 20))


(tabular
  (fact ":integer-add returns the sum, auto-promoting overflows"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack   ?expected
    ;; adding
    :integer    '(11 -5)  :integer-add   :integer     '(6)
    :integer    '(-3 -5)  :integer-add   :integer     '(-8)
    ;; missing args
    :integer    '(11)     :integer-add   :integer     '(11)
    :integer    '()       :integer-add   :integer     '()
    ;; overflow
    :integer    '(3333333333333333333 7777777777777777777)
                          :integer-add   :integer     '(11111111111111111110N))


(tabular
  (fact ":integer-subtract returns (- :second :first)"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(11 -5)  :integer-subtract   :integer      '(-16)
    :integer    '(-3 -5)  :integer-subtract   :integer      '(-2)
    ;; missing args
    :integer    '(11)     :integer-subtract   :integer      '(11)
    :integer    '()       :integer-subtract   :integer      '()
    ;; overflow
    :integer    '(33333333333333333333 77777777777777777777)
                          :integer-subtract   :integer      '(44444444444444444444N))


(tabular
  (fact ":integer-multiply returns the product, auto-promoting overflows"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(11 -5)  :integer-multiply   :integer       '(-55)
    :integer    '(-3 -5)  :integer-multiply   :integer       '(15)
    ;; missing args
    :integer    '(11)     :integer-multiply   :integer       '(11)
    :integer    '()       :integer-multiply   :integer       '()
    ;; overflow
    :integer    '(333333333333 777777777777)
                          :integer-multiply   :integer       '(259259259258740740740741N))



(tabular
  (fact ":integer-divide returns the quotient :second/:first, unless :first is zero"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)   :integer-divide      :integer       '(5)
    :integer    '(-3 -15) :integer-divide      :integer       '(5)
    ;; missing args
    :integer    '(11)     :integer-divide      :integer       '(11)
    :integer    '()       :integer-divide      :integer       '()
    ;; divide-by-zero
    :integer    '(0 11)   :integer-divide      :integer       '(0 11)
    :integer    '(0 11)   :integer-divide      :error         '({:step 0 :item ":integer-divide 0 denominator"})

    )


(tabular
  (fact ":integer-digits pushes a vector of digits"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(1182)     :integer-digits   :exec      '((1 1 8 2))
    :integer    '(-39812M)  :integer-digits   :exec      '((3 9 8 1 2))
    :integer    '(1191N)    :integer-digits   :exec      '((1 1 9 1))
    )


(tabular
  (fact ":integer->numerals pushes a vector of numeric :chars"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(1182)     :integer->numerals   :chars      '([\1 \1 \8 \2])
    :integer    '(-39812M)  :integer->numerals   :chars      '([\3 \9 \8 \1 \2])
    :integer    '(1191N)    :integer->numerals   :chars      '([\1 \1 \9 \1])
    )


(tabular
  (fact ":integer->bits pushes a vector of numeric :booleans"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction     ?get-stack     ?expected
    :integer    '(0)       :integer->bits   :booleans      '([false false])
    :integer    '(1)       :integer->bits   :booleans      '([true false])
    :integer    '(3)       :integer->bits   :booleans      '([true true])
    :integer    '(127)     :integer->bits   :booleans      '([true true true true
                                                              true true true])
    :integer    '(128)     :integer->bits   :booleans      '([false false false false
                                                              false false false true])
    :integer    '(-128)    :integer->bits   :booleans      '([false false false false
                                                              false false false true])
    :integer    '(-1771277172712712728M)
                            :integer->bits  :booleans      '([false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true]))


(tabular
  (fact ":integer-bits pushes a list of true/false bits"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(0)        :integer-bits      :exec      '((false false))
    :integer    '(1)        :integer-bits      :exec      '((true false))
    :integer    '(3)        :integer-bits      :exec      '((true true))
    :integer    '(127)      :integer-bits      :exec      '((true true true true true true true))
    :integer    '(128)      :integer-bits      :exec      '((false false false false
                                                             false false false true))
    :integer    '(-128)     :integer-bits      :exec      '((false false false false
                                                             false false false true))
    :integer    '(1771277172712712728M)
                            :integer-bits      :exec      '((false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true))
    :integer    '(-1771277172712712728N)
                            :integer-bits      :exec      '((false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true)))



(fact "rewrite-digits"
  (rewrite-digits 12345 3) => 69208
  (rewrite-digits 63119988 3) => 5196527
  (rewrite-digits 8 1) => 8
  (rewrite-digits 8 2) => 6
  (rewrite-digits 8 3) => 4
  (rewrite-digits -63119988 3) => -5196527
  (rewrite-digits -50000 3) => -50055)


(tabular
  (fact ":integer-totalistic3 does some weird stuff"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction             ?get-stack     ?expected
    :integer    '(1182)     :integer-totalistic3   :integer      '(114)
    :integer    '(-39812M)  :integer-totalistic3   :integer      '(-8164)
    :integer    '(235235235)       
                            :integer-totalistic3   :integer      '(0)
    :integer    '(16112002112012012191N)
                            :integer-totalistic3   :integer      '(88432234433333342118N)
    )




(tabular
  (fact ":integer-mod returns (mod :second :first)"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)   :integer-mod      :integer      '(0)
    :integer    '(4 21)   :integer-mod      :integer      '(1)
    :integer    '(4 -21)  :integer-mod      :integer      '(3)
    :integer    '(-4 21)  :integer-mod      :integer      '(-3)
    :integer    '(-3 -15) :integer-mod      :integer      '(0)
    :integer    '(-3 -16) :integer-mod      :integer      '(-1)
    ;; missing args
    :integer    '(11)     :integer-mod      :integer      '(11)
    :integer    '()       :integer-mod      :integer      '()
    ;; divide-by-zero
    :integer    '(0 11)   :integer-mod      :integer      '(0 11)
    :integer    '(0 11)   :integer-mod      :error        '({:step 0 :item ":integer-mod 0 denominator"})
    )


(tabular
  (fact ":integer-inc takes one :integer and adds 1 to it"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just one more     
    :integer    '(99)          :integer-inc      :integer         '(100)
    :integer    '(-99)         :integer-inc      :integer         '(-98)
    ;; overflow 
    :integer    '(22222222222222222222222222222222222N)
                               :integer-inc      :integer       '(22222222222222222222222222222222223N)
    :integer    '(-22222222222222222222222222222222222N)
                               :integer-inc      :integer       '(-22222222222222222222222222222222221N)
    ;; missing args 
    :integer    '()            :integer-inc      :integer       '())



(tabular
  (fact ":integer-dec takes one :integer and subtracts 1 from it"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just one more     
    :integer    '(99)          :integer-dec      :integer         '(98)
    :integer    '(-99)         :integer-dec      :integer         '(-100)
    ;; overflow 
    :integer    '(22222222222222222222222222222222222N)
                               :integer-dec      :integer       '(22222222222222222222222222222222221N)
    :integer    '(-22222222222222222222222222222222222N)
                               :integer-dec      :integer       '(-22222222222222222222222222222222223N)
    ;; missing args 
    :integer    '()            :integer-dec      :integer       '())


;; conversions


(tabular
  (fact ":boolean->integer takes a :boolean value, and returns 1 if true, 0 if false"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :boolean    '(false true)    :boolean->integer      :integer       '(0)
    :boolean    '(false true)    :boolean->integer      :boolean       '(true)
    :boolean    '(true false)    :boolean->integer      :integer       '(1)
    :boolean    '(true false)    :boolean->integer      :boolean       '(false)
    ;; missing args 
    :boolean    '()              :boolean->integer      :integer       '()
    :boolean    '()              :boolean->integer      :boolean       '())



(tabular
  (fact ":boolean->signedint takes a :boolean value, and returns 1 if true, -1 if false"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :boolean    '(false true)    :boolean->signedint      :integer       '(-1)
    :boolean    '(true false)    :boolean->signedint      :integer       '(1)
    ;; missing args 
    :boolean    '()              :boolean->signedint      :integer       '()
    :boolean    '()              :boolean->signedint      :boolean       '())


(tabular
  (fact ":float->integer takes a :float value, and truncates it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :float    '(0.0)          :float->integer      :integer       '(0)
    :float    '(0.1)          :float->integer      :integer       '(0)
    :float    '(0.9)          :float->integer      :integer       '(0)
    :float    '(22.22)        :float->integer      :integer       '(22)
    ;; consumes arg
    :float    '(22.22)        :float->integer      :float         '()
    ;; edge cases 
    :float    '(-0.0)         :float->integer      :integer       '(0)
    :float    '(-0.1)         :float->integer      :integer       '(0)
    :float    '(-22.22)       :float->integer      :integer       '(-22)
    ;; range
    :float    '(-22222222222222222222222222222.3333333333M)
                              :float->integer      :integer       '(-22222222222222222222222222222N)
    ;; missing args 
    :float    '()             :float->integer      :integer       '()
    :float    '()             :float->integer      :float         '())


(tabular
  (fact ":string->integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected

    :string     '("123")       :string->integer        :integer       '(123)
    :string     '("-123")      :string->integer        :integer       '(-123)
    :string     '("  52")      :string->integer        :integer       '()
    :string     '("52  ")      :string->integer        :integer       '()
    :string     '("\t\n52")    :string->integer        :integer       '()
    :string     '("-52e3")     :string->integer        :integer       '()
    :string     '("2.3e-4")    :string->integer        :integer       '()

    :string     '("foo")       :string->integer        :integer       '()
    :string     '("1.2.3")     :string->integer        :integer       '()
    :string     '("1.2 8")     :string->integer        :integer       '()
    :string     '("1/17")      :string->integer        :integer       '()
    :string     '("")          :string->integer        :integer       '()
    :string     '()            :string->integer        :integer       '())


(tabular
  (fact ":char->integer takes a :char value, and converts it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :char    '(\0)           :char->integer      :integer       '(48)
    :char    '(\r)           :char->integer      :integer       '(114)
    :char    '(\newline)     :char->integer      :integer       '(10)
    :char    '(\uF021)       :char->integer      :integer       '(61473)
    ;; consumes arg
    :char    '(\0)           :char->integer      :char          '()
    ;; missing args 
    :char    '()             :char->integer      :integer       '()
    :char    '()             :char->integer      :char          '())


(tabular
  (fact ":integer-sign returns the sine(x)"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    ;; up we go
    :integer    '(-0)        :integer-sign    :integer     '(0)
    :integer    '(111)       :integer-sign    :integer     '(1)

    :integer    '(-3)        :integer-sign    :integer     '(-1)
    ;; missing args
    :integer    '()          :integer-sign    :integer     '())


;; comparable (generated) instructions


(tabular
  (fact ":integer<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer<?      :boolean       '(false)
    :integer    '(20 4)    :integer<?      :boolean       '(true)
    :integer    '(4 4)     :integer<?      :boolean       '(false)
    ;; missing args 
    :integer    '(11)      :integer<?      :boolean       '()
    :integer    '(11)      :integer<?      :integer       '(11)
    :integer    '()        :integer<?      :boolean       '()
    :integer    '()        :integer<?      :integer       '())


(tabular
  (fact ":integer≤? returns a :boolean indicating whether :first <= :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer≤?      :boolean      '(false)
    :integer    '(20 4)    :integer≤?      :boolean      '(true)
    :integer    '(4 4)     :integer≤?      :boolean      '(true)
    ;; missing args 
    :integer    '(11)      :integer≤?      :boolean      '()
    :integer    '(11)      :integer≤?      :integer      '(11)
    :integer    '()        :integer≤?      :boolean      '()
    :integer    '()        :integer≤?      :integer      '())


(tabular
  (fact ":integer>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer>?      :boolean       '(true)
    :integer    '(20 4)    :integer>?      :boolean       '(false)
    :integer    '(4 4)     :integer>?      :boolean       '(false)
    ;; missing args 
    :integer    '(11)      :integer>?      :boolean       '()
    :integer    '(11)      :integer>?      :integer       '(11)
    :integer    '()        :integer>?      :boolean       '()
    :integer    '()        :integer>?      :integer       '())


(tabular
  (fact ":integer≥? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer≥?      :boolean     '(true)
    :integer    '(20 4)    :integer≥?      :boolean     '(false)
    :integer    '(4 4)     :integer≥?      :boolean     '(true)
    ;; missing args 
    :integer    '(11)      :integer≥?      :boolean     '()
    :integer    '(11)      :integer≥?      :integer     '(11)
    :integer    '()        :integer≥?      :boolean     '()
    :integer    '()        :integer≥?      :integer     '())


(tabular
  (fact ":integer-max takes two items from :integer and replaces the larger one;
    if they are the same, it still returns one of them only"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just the bigger one please     
    :integer    '(4 3 2 1)      :integer-max      :integer         '(4 2 1)
    :integer    '(-1 2 3 4)     :integer-max      :integer         '(2 3 4)
    :integer    '(8 8 3 4)      :integer-max      :integer         '(8 3 4)
    ;; missing args 
    :integer    '(2)            :integer-max      :integer       '(2)
    :integer    '()             :integer-max      :integer       '())



(tabular
  (fact ":integer-min takes two items from :integer and replaces the smaller one;
    if they are the same, it still returns one of them only"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just the smaller one please     
    :integer    '(4 3 2 1)      :integer-min      :integer         '(3 2 1)
    :integer    '(-1 2 3 4)     :integer-min      :integer         '(-1 3 4)
    :integer    '(8 8 3 4)      :integer-min      :integer         '(8 3 4)
    ;; missing args 
    :integer    '(2)            :integer-min      :integer       '(2)
    :integer    '()             :integer-min      :integer       '())


;; visible (generated) instructions


(tabular
  (fact ":integer-empty? returns a :boolean indicating whether :integer is empty"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer-empty?      :boolean       '(false)
    :integer    '()        :integer-empty?      :boolean       '(true))


(tabular
  (fact ":integer-stackdepth saves (count :integer) onto :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction            ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)     :integer-stackdepth      :integer       '(3 1 2 3)
    :integer    '(1 1 1 1 1)    
                             :integer-stackdepth      :integer       '(5 1 1 1 1 1)
    :integer    '()          :integer-stackdepth      :integer       '(0))


;; equatable (generated) instructions


(tabular
  (fact ":integer-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer-equal?      :boolean       '(false)
    :integer    '(20 4)    :integer-equal?      :boolean       '(false)
    :integer    '(4 4)     :integer-equal?      :boolean       '(true)
    ;; missing args 
    :integer    '(11)      :integer-equal?      :boolean       '()
    :integer    '(11)      :integer-equal?      :integer       '(11)
    :integer    '()        :integer-equal?      :boolean       '()
    :integer    '()        :integer-equal?      :integer       '())


(tabular
  (fact ":integer-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer-notequal?      :boolean       '(true)
    :integer    '(20 4)    :integer-notequal?      :boolean       '(true)
    :integer    '(4 4)     :integer-notequal?      :boolean       '(false)
    ;; missing args 
    :integer    '(11)      :integer-notequal?      :boolean       '()
    :integer    '(11)      :integer-notequal?      :integer       '(11)
    :integer    '()        :integer-notequal?      :boolean       '()
    :integer    '()        :integer-notequal?      :integer       '())


;; movable (generated) instructions


(tabular
  (fact ":integer-flush flushes the entire :integer stack"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   :integer-flush      :integer       '()
    :integer    '(2 3)     :integer-flush      :integer       '()
    :integer    '(2)       :integer-flush      :integer       '()
    ;; missing args 
    :integer    '()        :integer-flush      :integer       '())


(tabular
  (fact ":integer-dup duplicates the top item from :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   :integer-dup      :integer       '(1 1 2 3)
    :integer    '(2 3)     :integer-dup      :integer       '(2 2 3)
    :integer    '(2)       :integer-dup      :integer       '(2 2)
    ;; missing args 
    :integer    '()        :integer-dup      :integer       '())


(tabular
  (fact ":integer-pop removes the top item from :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   :integer-pop      :integer       '(2 3)
    :integer    '(2 3)     :integer-pop      :integer       '(3)
    :integer    '(2)       :integer-pop      :integer       '()
    ;; missing args 
    :integer    '()        :integer-pop      :integer       '())


(tabular
  (fact ":integer-rotate shifts the top 3 items from :integer, putting 3rd on top"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3 4) :integer-rotate      :integer       '(3 1 2 4)
    :integer    '(1 2 3)   :integer-rotate      :integer       '(3 1 2)
    ;; missing args 
    :integer    '(2 3)     :integer-rotate      :integer       '(2 3)
    :integer    '(2)       :integer-rotate      :integer       '(2)
    :integer    '()        :integer-rotate      :integer       '())


(tabular
  (fact ":integer-shove pops an index from :integer, then MOVES
    the next item to a new position specified by the index; uses
    `(mod arg (inc (count :integer)))` to place it in range [0,(count stack)]"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(3 4 3 2 1)    :integer-shove      :integer       '(3 2 1 4)
    :integer    '(1 1 2 3)      :integer-shove      :integer       '(2 1 3)
    ;; 0 index
    :integer    '(0 1 2 3)      :integer-shove      :integer       '(1 2 3)
    ;; range handling
    :integer    '(-1 1 2 3)     :integer-shove      :integer       '(2 3 1)
    :integer    '(10 1 2 3)     :integer-shove      :integer       '(2 1 3)
    ;; missing args 
    :integer    '(2)            :integer-shove      :integer       '(2)
    :integer    '()             :integer-shove      :integer       '())


(tabular
  (fact ":integer-swap swaps the top two items from :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   :integer-swap      :integer       '(2 1 3)
    :integer    '(2 3)     :integer-swap      :integer       '(3 2)
    ;; missing args 
    :integer    '(2)       :integer-swap      :integer       '(2)
    :integer    '()        :integer-swap      :integer       '())


(tabular
  (fact ":integer-yank takes its index from :integer, then MOVES the (current)
    nth item up to the top of the stack; uses (mod arg (count :integer))"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(3 4 3 2 1)    :integer-yank      :integer       '(1 4 3 2)
    :integer    '(1 1 2 3)      :integer-yank      :integer       '(2 1 3)
    ;; 0 index
    :integer    '(0 1 2 3)      :integer-yank      :integer       '(1 2 3)
    ;; range handling
    :integer    '(-2 1 2 3)     :integer-yank      :integer       '(2 1 3)
    :integer    '(11 1 2 3)     :integer-yank      :integer       '(3 1 2)
    ;; missing args 
    :integer    '(2)            :integer-yank      :integer       '(2)
    :integer    '()             :integer-yank      :integer       '())


(tabular
  (fact ":integer-yankdup takes its index from :integer, then COPIES the (current)
    nth item up to the top of the stack; uses (mod arg (count :integer))"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(3 4 3 2 1)    :integer-yankdup      :integer       '(1 4 3 2 1)
    :integer    '(1 1 2 3)      :integer-yankdup      :integer       '(2 1 2 3)
    ;; 0 index
    :integer    '(0 1 2 3)      :integer-yankdup      :integer       '(1 1 2 3)
    ;; range handling
    :integer    '(-2 1 2 3)     :integer-yankdup      :integer       '(2 1 2 3)
    :integer    '(11 1 2 3)     :integer-yankdup      :integer       '(3 1 2 3)
    ;; missing args 
    :integer    '(2)            :integer-yankdup      :integer       '(2)
    :integer    '()             :integer-yankdup      :integer       '())



(tabular
  (fact ":integer-few reduces the top :integer mod 10"
    (register-type-and-check-instruction
      ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(77)     :integer-few     :integer       '(7)
    :integer     '(-2)     :integer-few     :integer       '(8)
    :integer     '(8)      :integer-few     :integer       '(8)
    :integer     '(0)      :integer-few     :integer       '(0))


(tabular
  (fact ":integer-some reduces the top :integer mod 100"
    (register-type-and-check-instruction
      ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(677)    :integer-some    :integer       '(77)
    :integer     '(-2912)  :integer-some    :integer       '(88)
    :integer     '(79)     :integer-some    :integer       '(79)
    :integer     '(0)      :integer-some    :integer       '(0))


(tabular
  (fact ":integer-many reduces the top :integer mod 1000"
    (register-type-and-check-instruction
      ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(2677)    :integer-many    :integer       '(677)
    :integer     '(-22212)  :integer-many    :integer       '(788)
    :integer     '(79)      :integer-many    :integer       '(79)
    :integer     '(0)       :integer-many    :integer       '(0))


(tabular
  (fact ":integer-lots reduces the top :integer mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :integer     '(32677)   :integer-lots    :integer       '(2677)
    :integer     '(-22212)  :integer-lots    :integer       '(7788)
    :integer     '(79)      :integer-lots    :integer       '(79)
    :integer     '(0)       :integer-lots    :integer       '(0))
