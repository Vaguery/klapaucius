(ns push.instructions.base.boolean_test
  (:require [push.interpreter.core :as i])
  (:use [push.util.test-helpers])
  (:use [push.types.base.boolean])  ;; sets up classic-boolean-type
  (:use midje.sweet))


;; these are tests of an Interpreter with the classic-boolean-type registered
;; the instructions under test are those stored IN THAT TYPE


;; specific boolean behavior

(tabular
  (fact ":boolean-and returns the binary AND over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-and   :boolean     '(false)
    :boolean    '(false true)   :boolean-and   :boolean     '(false)
    :boolean    '(true false)   :boolean-and   :boolean     '(false)
    :boolean    '(true true)    :boolean-and   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        :boolean-and   :boolean     '(false)
    :boolean    '()             :boolean-and   :boolean     '())


(tabular
  (fact ":boolean-not returns the binary NOT of the top :boolean value"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-not   :boolean     '(true false)
    :boolean    '(false true)   :boolean-not   :boolean     '(true true)
    ;; missing args
    :boolean    '()             :boolean-not   :boolean     '())


(tabular
  (fact ":boolean-or returns the binary OR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-or   :boolean     '(false)
    :boolean    '(false true)   :boolean-or   :boolean     '(true)
    :boolean    '(true false)   :boolean-or   :boolean     '(true)
    :boolean    '(true true)    :boolean-or   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        :boolean-or   :boolean     '(false)
    :boolean    '()             :boolean-or   :boolean     '())


(tabular
  (fact ":boolean-xor returns the binary XOR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-xor   :boolean     '(false)
    :boolean    '(false true)   :boolean-xor   :boolean     '(true)
    :boolean    '(true false)   :boolean-xor   :boolean     '(true)
    :boolean    '(true true)    :boolean-xor   :boolean     '(false)
    ;; missing args
    :boolean    '(false)        :boolean-xor   :boolean     '(false)
    :boolean    '()             :boolean-xor   :boolean     '())


;; equatable


;; combinators


(tabular
  (future-fact ":boolean-dup duplicates the top item from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-dup      :boolean       '(false false true)
    :boolean    '(true)         :boolean-dup      :boolean       '(true true)
    ;; missing args 
    :boolean    '()             :boolean-dup      :boolean       '())


(tabular
  (fact ":boolean-pop removes the top item from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-pop      :boolean       '(true)
    :boolean    '(true)         :boolean-pop      :boolean       '()
    ;; missing args 
    :boolean    '()             :boolean-pop      :boolean       '())


(tabular
  (fact ":boolean-rotate shifts the top 3 items from :boolean, putting 3rd on top"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items             ?instruction        ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true true false)
                                   :boolean-rotate      :boolean       '(true false true false)
    ;; missing args 
    :boolean    '(false true)      :boolean-rotate      :boolean       '(false true)
    :boolean    '(true)            :boolean-rotate      :boolean       '(true)
    :boolean    '()                :boolean-rotate      :boolean       '())


; (tabular
;   (fact ":boolean-shove pops an index from :boolean, then MOVES
;     the next item to a new position specified by the index; uses
;     `(mod arg (inc (count :boolean)))` to place it in range [0,(count stack)]"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items          ?instruction      ?get-stack     ?expected
;     ;; just shifting things     
;     :boolean    '(3 4 3 2 1)    :boolean-shove      :boolean       '(3 2 1 4)
;     :boolean    '(1 1 2 3)      :boolean-shove      :boolean       '(2 1 3)
;     ;; 0 index
;     :boolean    '(0 1 2 3)      :boolean-shove      :boolean       '(1 2 3)
;     ;; range handling
;     :boolean    '(-1 1 2 3)     :boolean-shove      :boolean       '(2 3 1)
;     :boolean    '(10 1 2 3)     :boolean-shove      :boolean       '(2 1 3)
;     ;; missing args 
;     :boolean    '(2)            :boolean-shove      :boolean       '(2)
;     :boolean    '()             :boolean-shove      :boolean       '())


(tabular
  (fact ":boolean-stackdepth saves (count :boolean) onto :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items               ?instruction            ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false false false) :boolean-stackdepth      :integer       '(3)
    :boolean    '()                  :boolean-stackdepth      :integer       '(0))


(tabular
  (fact ":boolean-swap swaps the top two items from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true true)   
                           :boolean-swap      :boolean       '(true false true)
    :boolean    '(false true)
                           :boolean-swap      :boolean       '(true false)
    ;; missing args 
    :boolean    '(false)   :boolean-swap      :boolean       '(false)
    :boolean    '()        :boolean-swap      :boolean       '())



(tabular
  (fact ":boolean-flush flushes the entire :boolean stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-flush      :boolean       '()
    ;; missing args 
    :boolean    '()             :boolean-flush      :boolean       '())



; (tabular
;   (fact ":boolean-yank takes its index from :boolean, then MOVES the (current)
;     nth item up to the top of the stack; uses (mod arg (count :boolean))"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items          ?instruction      ?get-stack     ?expected
;     ;; just shifting things     
;     :boolean    '(3 4 3 2 1)    :boolean-yank      :boolean       '(1 4 3 2)
;     :boolean    '(1 1 2 3)      :boolean-yank      :boolean       '(2 1 3)
;     ;; 0 index
;     :boolean    '(0 1 2 3)      :boolean-yank      :boolean       '(1 2 3)
;     ;; range handling
;     :boolean    '(-2 1 2 3)     :boolean-yank      :boolean       '(2 1 3)
;     :boolean    '(11 1 2 3)     :boolean-yank      :boolean       '(3 1 2)
;     ;; missing args 
;     :boolean    '(2)            :boolean-yank      :boolean       '(2)
;     :boolean    '()             :boolean-yank      :boolean       '())



; (tabular
;   (fact ":boolean-yankdup takes its index from :boolean, then COPIES the (current)
;     nth item up to the top of the stack; uses (mod arg (count :boolean))"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-boolean-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items          ?instruction      ?get-stack     ?expected
;     ;; just shifting things     
;     :boolean    '(3 4 3 2 1)    :boolean-yankdup      :boolean       '(1 4 3 2 1)
;     :boolean    '(1 1 2 3)      :boolean-yankdup      :boolean       '(2 1 2 3)
;     ;; 0 index
;     :boolean    '(0 1 2 3)      :boolean-yankdup      :boolean       '(1 1 2 3)
;     ;; range handling
;     :boolean    '(-2 1 2 3)     :boolean-yankdup      :boolean       '(2 1 2 3)
;     :boolean    '(11 1 2 3)     :boolean-yankdup      :boolean       '(3 1 2 3)
;     ;; missing args 
;     :boolean    '(2)            :boolean-yankdup      :boolean       '(2)
;     :boolean    '()             :boolean-yankdup      :boolean       '())
