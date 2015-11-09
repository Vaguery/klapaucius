(ns push.instructions.base.integer_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.integer])  ;; sets up classic-integer-type
  )

;; these are tests of an Interpreter with the classic-integer-type registered
;; the instructions under test are those stored IN THAT TYPE


;; integer-specific instructions


(tabular
  (fact ":integer-add returns the sum, auto-promoting overflows"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)   :integer-divide      :integer       '(5)
    :integer    '(-3 -15) :integer-divide      :integer       '(5)
    ;; missing args
    :integer    '(11)     :integer-divide      :integer       '(11)
    :integer    '()       :integer-divide      :integer       '()
    ;; divide-by-zero
    :integer    '(0 11)   :integer-divide      :integer       '(0 11))


(tabular
  (fact ":integer-mod returns (mod :second :first)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
    :integer    '(0 11)   :integer-mod      :integer      '(0 11))


(tabular
  (fact ":integer-inc takes one :integer and adds 1 to it"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
  (fact ":integer-fromboolean takes a :boolean value, and returns 1 if true, 0 if false"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :boolean    '(false true)    :integer-fromboolean      :integer       '(0)
    :boolean    '(false true)    :integer-fromboolean      :boolean       '(true)
    :boolean    '(true false)    :integer-fromboolean      :integer       '(1)
    :boolean    '(true false)    :integer-fromboolean      :boolean       '(false)
    ;; missing args 
    :boolean    '()              :integer-fromboolean      :integer       '()
    :boolean    '()              :integer-fromboolean      :boolean       '())


(tabular
  (fact ":integer-fromfloat takes a :float value, and truncates it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :float    '(0.0)          :integer-fromfloat      :integer       '(0)
    :float    '(0.1)          :integer-fromfloat      :integer       '(0)
    :float    '(0.9)          :integer-fromfloat      :integer       '(0)
    :float    '(22.22)        :integer-fromfloat      :integer       '(22)
    ;; consumes arg
    :float    '(22.22)        :integer-fromfloat      :float         '()
    ;; edge cases 
    :float    '(-0.0)         :integer-fromfloat      :integer       '(0)
    :float    '(-0.1)         :integer-fromfloat      :integer       '(0)
    :float    '(-22.22)       :integer-fromfloat      :integer       '(-22)
    ;; range
    :float    '(-22222222222222222222222222222.3333333333M)
                              :integer-fromfloat      :integer       '(-22222222222222222222222222222N)
    ;; missing args 
    :float    '()             :integer-fromfloat      :integer       '()
    :float    '()             :integer-fromfloat      :float         '())




(tabular
  (fact ":integer-fromchar takes a :char value, and converts it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :char    '(\0)           :integer-fromchar      :integer       '(48)
    :char    '(\r)           :integer-fromchar      :integer       '(114)
    :char    '(\newline)     :integer-fromchar      :integer       '(10)
    :char    '(\uF021)       :integer-fromchar      :integer       '(61473)
    ;; consumes arg
    :char    '(\0)           :integer-fromchar      :char          '()
    ;; missing args 
    :char    '()             :integer-fromchar      :integer       '()
    :char    '()             :integer-fromchar      :char          '())


(tabular
  (fact ":integer-sign returns the sine(x)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    :integer-empty?      :boolean       '(false)
    :integer    '()        :integer-empty?      :boolean       '(true))


(tabular
  (fact ":integer-stackdepth saves (count :integer) onto :integer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-integer-type ?instruction ?get-stack) => ?expected)

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

