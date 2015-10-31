(ns push.instructions.base.integer_test
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.base.integer])
  (:use midje.sweet)
  (:use [push.util.test-helpers]))


(tabular
  (fact ":integer-add returns the sum, auto-promoting overflows"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack   ?expected
    ;; adding
    :integer    '(11 -5)  integer-add   :integer     '(6)
    :integer    '(-3 -5)  integer-add   :integer     '(-8)
    ;; missing args
    :integer    '(11)     integer-add   :integer     '(11)
    :integer    '()       integer-add   :integer     '()
    ;; overflow
    :integer    '(3333333333333333333 7777777777777777777)
                          integer-add   :integer     '(11111111111111111110N))


(tabular
  (fact ":integer-subtract returns (- :second :first)"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(11 -5)  integer-subtract   :integer      '(-16)
    :integer    '(-3 -5)  integer-subtract   :integer      '(-2)
    ;; missing args
    :integer    '(11)     integer-subtract   :integer      '(11)
    :integer    '()       integer-subtract   :integer      '()
    ;; overflow
    :integer    '(33333333333333333333 77777777777777777777)
                          integer-subtract   :integer      '(44444444444444444444N))


(tabular
  (fact ":integer-multiply returns the product, auto-promoting overflows"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(11 -5)  integer-multiply   :integer       '(-55)
    :integer    '(-3 -5)  integer-multiply   :integer       '(15)
    ;; missing args
    :integer    '(11)     integer-multiply   :integer       '(11)
    :integer    '()       integer-multiply   :integer       '()
    ;; overflow
    :integer    '(333333333333 777777777777)
                          integer-multiply   :integer       '(259259259258740740740741N))



(tabular
  (fact ":integer-divide returns the quotient :second/:first, unless :first is zero"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)   integer-divide      :integer       '(5)
    :integer    '(-3 -15) integer-divide      :integer       '(5)
    ;; missing args
    :integer    '(11)     integer-divide      :integer       '(11)
    :integer    '()       integer-divide      :integer       '()
    ;; divide-by-zero
    :integer    '(0 11)   integer-divide      :integer       '(0 11))


(tabular
  (fact ":integer-mod returns (mod :second :first)"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)   integer-mod      :integer      '(0)
    :integer    '(4 21)   integer-mod      :integer      '(1)
    :integer    '(4 -21)  integer-mod      :integer      '(3)
    :integer    '(-4 21)  integer-mod      :integer      '(-3)
    :integer    '(-3 -15) integer-mod      :integer      '(0)
    :integer    '(-3 -16) integer-mod      :integer      '(-1)
    ;; missing args
    :integer    '(11)     integer-mod      :integer      '(11)
    :integer    '()       integer-mod      :integer      '()
    ;; divide-by-zero
    :integer    '(0 11)   integer-mod      :integer      '(0 11))



(tabular
  (fact ":integer-lt returns a :boolean indicating whether :first < :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-lt      :boolean       '(false)
    :integer    '(20 4)    integer-lt      :boolean       '(true)
    :integer    '(4 4)     integer-lt      :boolean       '(false)
    ;; missing args 
    :integer    '(11)      integer-lt      :boolean       '()
    :integer    '(11)      integer-lt      :integer       '(11)
    :integer    '()        integer-lt      :boolean       '()
    :integer    '()        integer-lt      :integer       '())


(tabular
  (fact ":integer-lte returns a :boolean indicating whether :first <= :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-lte      :boolean      '(false)
    :integer    '(20 4)    integer-lte      :boolean      '(true)
    :integer    '(4 4)     integer-lte      :boolean      '(true)
    ;; missing args 
    :integer    '(11)      integer-lte      :boolean      '()
    :integer    '(11)      integer-lte      :integer      '(11)
    :integer    '()        integer-lte      :boolean      '()
    :integer    '()        integer-lte      :integer      '())


(tabular
  (fact ":integer-gt returns a :boolean indicating whether :first > :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-gt      :boolean       '(true)
    :integer    '(20 4)    integer-gt      :boolean       '(false)
    :integer    '(4 4)     integer-gt      :boolean       '(false)
    ;; missing args 
    :integer    '(11)      integer-gt      :boolean       '()
    :integer    '(11)      integer-gt      :integer       '(11)
    :integer    '()        integer-gt      :boolean       '()
    :integer    '()        integer-gt      :integer       '())


(tabular
  (fact ":integer-gte returns a :boolean indicating whether :first > :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-gte      :boolean     '(true)
    :integer    '(20 4)    integer-gte      :boolean     '(false)
    :integer    '(4 4)     integer-gte      :boolean     '(true)
    ;; missing args 
    :integer    '(11)      integer-gte      :boolean     '()
    :integer    '(11)      integer-gte      :integer     '(11)
    :integer    '()        integer-gte      :boolean     '()
    :integer    '()        integer-gte      :integer     '())


(tabular
  (fact ":integer-empty? returns a :boolean indicating whether :integer is empty"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-empty?      :boolean       '(false)
    :integer    '()        integer-empty?      :boolean       '(true))


(tabular
  (fact ":integer-eq returns a :boolean indicating whether :first = :second"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just the math
    :integer    '(4 20)    integer-eq      :boolean       '(false)
    :integer    '(20 4)    integer-eq      :boolean       '(false)
    :integer    '(4 4)     integer-eq      :boolean       '(true)
    ;; missing args 
    :integer    '(11)      integer-eq      :boolean       '()
    :integer    '(11)      integer-eq      :integer       '(11)
    :integer    '()        integer-eq      :boolean       '()
    :integer    '()        integer-eq      :integer       '())


(tabular
  (fact ":integer-inc takes one :integer and adds 1 to it"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just one more     
    :integer    '(99)          integer-inc      :integer         '(100)
    :integer    '(-99)         integer-inc      :integer         '(-98)
    ;; overflow 
    :integer    '(22222222222222222222222222222222222N)
                               integer-inc      :integer       '(22222222222222222222222222222222223N)
    :integer    '(-22222222222222222222222222222222222N)
                               integer-inc      :integer       '(-22222222222222222222222222222222221N)
    ;; missing args 
    :integer    '()            integer-inc      :integer       '())



(tabular
  (fact ":integer-dec takes one :integer and subtracts 1 from it"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just one more     
    :integer    '(99)          integer-dec      :integer         '(98)
    :integer    '(-99)         integer-dec      :integer         '(-100)
    ;; overflow 
    :integer    '(22222222222222222222222222222222222N)
                               integer-dec      :integer       '(22222222222222222222222222222222221N)
    :integer    '(-22222222222222222222222222222222222N)
                               integer-dec      :integer       '(-22222222222222222222222222222222223N)
    ;; missing args 
    :integer    '()            integer-dec      :integer       '())


;; combinators (will be built automatically in future)


(tabular
  (fact ":integer-flush flushes the entire :integer stack"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   integer-flush      :integer       '()
    :integer    '(2 3)     integer-flush      :integer       '()
    :integer    '(2)       integer-flush      :integer       '()
    ;; missing args 
    :integer    '()        integer-flush      :integer       '())



(tabular
  (fact ":integer-dup duplicates the top item from :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   integer-dup      :integer       '(1 1 2 3)
    :integer    '(2 3)     integer-dup      :integer       '(2 2 3)
    :integer    '(2)       integer-dup      :integer       '(2 2)
    ;; missing args 
    :integer    '()        integer-dup      :integer       '())


(tabular
  (fact ":integer-max takes two items from :integer and replaces the larger one;
    if they are the same, it still returns one of them only"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just the bigger one please     
    :integer    '(4 3 2 1)     integer-max      :integer         '(4 2 1)
    :integer    '(-1 2 3 4)    integer-max      :integer         '(2 3 4)
    :integer    '(8 8 3 4)     integer-max      :integer         '(8 3 4)
    ;; missing args 
    :integer    '(2)            integer-max      :integer       '(2)
    :integer    '()             integer-max      :integer       '())



(tabular
  (fact ":integer-min takes two items from :integer and replaces the smaller one;
    if they are the same, it still returns one of them only"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just the smaller one please     
    :integer    '(4 3 2 1)     integer-min      :integer         '(3 2 1)
    :integer    '(-1 2 3 4)    integer-min      :integer         '(-1 3 4)
    :integer    '(8 8 3 4)     integer-min      :integer         '(8 3 4)
    ;; missing args 
    :integer    '(2)            integer-min      :integer       '(2)
    :integer    '()             integer-min      :integer       '())



(tabular
  (fact ":integer-pop removes the top item from :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   integer-pop      :integer       '(2 3)
    :integer    '(2 3)     integer-pop      :integer       '(3)
    :integer    '(2)       integer-pop      :integer       '()
    ;; missing args 
    :integer    '()        integer-pop      :integer       '())


(tabular
  (fact ":integer-rotate shifts the top 3 items from :integer, putting 3rd on top"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3 4) integer-rotate      :integer       '(3 1 2 4)
    :integer    '(1 2 3)   integer-rotate      :integer       '(3 1 2)
    ;; missing args 
    :integer    '(2 3)     integer-rotate      :integer       '(2 3)
    :integer    '(2)       integer-rotate      :integer       '(2)
    :integer    '()        integer-rotate      :integer       '())


(tabular
  (fact ":integer-shove takes an item from :integer, then MOVES it to an index specified
    by the second :integer value; uses (mod arg (inc (count :integer)))"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(4 3 3 2 1)    integer-shove      :integer       '(3 2 1 4)
    :integer    '(1 1 2 3)      integer-shove      :integer       '(2 1 3)
    ;; 0 index
    :integer    '(1 0 2 3)      integer-shove      :integer       '(1 2 3)
    ;; range handling
    :integer    '(1 -1 2 3)     integer-shove      :integer       '(2 3 1)
    :integer    '(1 10 2 3)     integer-shove      :integer       '(2 1 3)
    ;; missing args 
    :integer    '(2)            integer-shove      :integer       '(2)
    :integer    '()             integer-shove      :integer       '())


(tabular
  (fact ":integer-shovedup pops an item from :integer, then MOVES it to an index specified
    by the second :integer value AFTER replacing it on the top of the stack;
    uses (mod arg (inc (count :integer)))"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(4 3 3 2 1)    integer-shovedup      :integer       '(4 3 2 4 1)
    :integer    '(1 1 2 3)      integer-shovedup      :integer       '(1 1 2 3)
    ;; 0 index
    :integer    '(1 0 2 3)      integer-shovedup      :integer       '(1 1 2 3)
    ;; range handling
    :integer    '(1 -1 2 3)     integer-shovedup      :integer       '(1 2 3 1)
    :integer    '(1 10 2 3)     integer-shovedup      :integer       '(1 2 1 3)
    ;; missing args 
    :integer    '(2)            integer-shovedup      :integer       '(2)
    :integer    '()             integer-shovedup      :integer       '())


(tabular
  (fact ":integer-stackdepth saves (count :integer) onto :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction            ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)     integer-stackdepth      :integer       '(3 1 2 3)
    :integer    '(1 1 1 1 1)    
                             integer-stackdepth      :integer       '(5 1 1 1 1 1)
    :integer    '()          integer-stackdepth      :integer       '(0))


(tabular
  (fact ":integer-swap swaps the top two items from :integer"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :integer    '(1 2 3)   integer-swap      :integer       '(2 1 3)
    :integer    '(2 3)     integer-swap      :integer       '(3 2)
    ;; missing args 
    :integer    '(2)       integer-swap      :integer       '(2)
    :integer    '()        integer-swap      :integer       '())


(tabular
  (fact ":integer-yank takes its index from :integer, then MOVES the (current)
    nth item up to the top of the stack; uses (mod arg (count :integer))"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(3 4 3 2 1)    integer-yank      :integer       '(1 4 3 2)
    :integer    '(1 1 2 3)      integer-yank      :integer       '(2 1 3)
    ;; 0 index
    :integer    '(0 1 2 3)      integer-yank      :integer       '(1 2 3)
    ;; range handling
    :integer    '(-2 1 2 3)     integer-yank      :integer       '(2 1 3)
    :integer    '(11 1 2 3)     integer-yank      :integer       '(3 1 2)
    ;; missing args 
    :integer    '(2)            integer-yank      :integer       '(2)
    :integer    '()             integer-yank      :integer       '())



(tabular
  (fact ":integer-yankdup takes its index from :integer, then COPIES the (current)
    nth item up to the top of the stack; uses (mod arg (count :integer))"
    (step-and-check-it ?set-stack ?items ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; just shifting things     
    :integer    '(3 4 3 2 1)    integer-yankdup      :integer       '(1 4 3 2 1)
    :integer    '(1 1 2 3)      integer-yankdup      :integer       '(2 1 2 3)
    ;; 0 index
    :integer    '(0 1 2 3)      integer-yankdup      :integer       '(1 1 2 3)
    ;; range handling
    :integer    '(-2 1 2 3)     integer-yankdup      :integer       '(2 1 2 3)
    :integer    '(11 1 2 3)     integer-yankdup      :integer       '(3 1 2 3)
    ;; missing args 
    :integer    '(2)            integer-yankdup      :integer       '(2)
    :integer    '()             integer-yankdup      :integer       '())

