(ns push.instructions.base.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.code])            ;; sets up classic-code-type
  )


;; these are tests of an Interpreter with the classic-code-type registered
;; the instructions under test are those stored IN THAT TYPE

;; work in progress
;; these instructions from Clojush are yet to be implemented:

; assemblers and disassemblers

; code_fromboolean
; code_fromfloat
; code_frominteger
; code_quote
; code_wrap
; code_fromzipnode
; code_fromziproot
; code_fromzipchildren
; code_fromziplefts
; code_fromziprights


; getters and setters


; code_length
; code_nth
; code_nthcdr
; code_size
; code_extract
; code_insert
; code_container


; code methods qua methods


; code_discrepancy
; code_overlap
; code_do
; code_do*
; code_do*range
; code_do*count
; code_do*times
; code_map
; code_if
; code_member
; code_subst
; code_contains
; code_position


(tabular
  (fact ":code-append concats two :code items, wrapping them in lists first if they aren't already"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-append        :code        '((8 9 1.1))
    :code    '(2 3)                 :code-append        :code        '((3 2))
    :code    '(() 3)                :code-append        :code        '((3))
    :code    '(2 ())                :code-append        :code        '((2))
    :code    '(() ())               :code-append        :code        '(())
    :code    '(2)                   :code-append        :code        '(2))


(tabular
  (future-fact ":code-atom? pushes true to :boolean if the top :code is not a list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; are you alone?
    :code    '(1.1 '(8 9))         :code-atom?        :boolean        '(true)
    :code    '(() 8)               :code-atom?        :boolean        '(false)
    
;;;;; PROBLEM HERE
    :code    '('() 8)              :code-atom?        :boolean        '(true)
    ;; …except in silence
    :code    '()                   :code-atom?        :boolean        '())


(tabular
  (fact ":code-cons conj's the second :code item onto the first, coercing it to a list if necessary"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-cons        :code        '(((8 9) 1.1))
    :code    '(2 3)                 :code-cons        :code        '((3 2))
    :code    '(() 3)                :code-cons        :code        '((3))
    :code    '(2 ())                :code-cons        :code        '((() 2))
    :code    '(() ())               :code-cons        :code        '((()))
    :code    '(2)                   :code-cons        :code        '(2))


(tabular
  (fact ":code-first pushes the first item of the top :code item, if it's a list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; pick a card
    :code    '((1.1) (8 9))       :code-first        :code        '(1.1 (8 9))
    :code    '((2 3))             :code-first        :code        '(2)
    :code    '(() 3)              :code-first        :code        '(3)
    :code    '(2)                 :code-first        :code        '(2)
    :code    '(((3)))             :code-first        :code        '((3))
    :code    '()                  :code-first        :code        '())


(tabular
  (fact ":code-length pushes the count of the top :code item (1 if a literal) onto :integer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; pick a number
    :code    '((1 2 3) (8 9))     :code-length        :integer        '(3)
    :code    '((2))               :code-length        :integer        '(1)
    :code    '(() 3)              :code-length        :integer        '(0)
    :code    '(2)                 :code-length        :integer        '(1)
    :code    '((2 (3)))           :code-length        :integer        '(2)
    :code    '()                  :code-length        :integer        '())


(tabular
  (fact ":code-list puts the top 2 :code items into a list on the :code stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-list        :code        '(((8 9) (1.1)))
    :code    '(2 3)                 :code-list        :code        '((3 2))
    :code    '(() 3)                :code-list        :code        '((3 ()))
    :code    '(2 ())                :code-list        :code        '((() 2))
    :code    '(() ())               :code-list        :code        '((() ()))
    :code    '(2)                   :code-list        :code        '(2))


(tabular
  (fact ":code-member? pushes true if the second item is found in the root of the first"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; 
    :code    '((1 2) 2)           :code-member?        :boolean        '(true)
    :code    '((2 3) 4)           :code-member?        :boolean        '(false)
    :code    '(() 3)              :code-member?        :boolean        '(false)
    :code    '(((3) 3) (3))       :code-member?        :boolean        '(true)
    :code    '(3 (3 4))           :code-member?        :boolean        '(false)
    :code    '(() ())             :code-member?        :boolean        '(false)
    :code    '()                  :code-member?        :boolean        '())


(tabular
  (fact ":code-noop don't do shit"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; be vewwy quiet
    :code    '(1.1 '(8 9))         :code-noop        :code        '(1.1 '(8 9))
    :code    '()                   :code-noop        :code        '())
     



(tabular
  (future-fact ":code-null? pushes true to :boolean if the top :code item is an empty list, false otherwise"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; an echoing sound is heard
    :code    '(1.1 '(8 9))         :code-null?        :boolean        '(false)
    :code    '(() 8)               :code-null?        :boolean        '(true)
    
;;;;; PROBLEM HERE
    :code    '('() 8)              :code-null?        :boolean        '(true)
    ;; …except in silence
    :code    '()                   :code-null?        :boolean        '())


(tabular
  (fact ":code-quote moves the top :exec item to :code"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    {:exec '(1 2 3)
     :code '(false)}           :code-quote            {:exec '(2 3)
                                                       :code '(1 false)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '((1 2) 3)
     :code '(true)}            :code-quote            {:exec '(3)
                                                       :code '((1 2) true)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:exec '()
     :code '(true)}            :code-quote            {:exec '()
                                                       :code '(true)})

(tabular
  (fact ":code-rest pushes all but the first item of a :code list; if the item is not a list, pushes an empty list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; what's left for me now?
    :code    '((1 2 3) (8 9))     :code-rest        :code        '((2 3) (8 9))
    :code    '((2))               :code-rest        :code        '(())
    :code    '(() 3)              :code-rest        :code        '(() 3)
    :code    '(2)                 :code-rest        :code        '(())
    :code    '((2 (3)))           :code-rest        :code        '(((3)))
    :code    '()                  :code-rest        :code        '())


;; visible


(tabular
  (fact ":code-stackdepth returns the number of items on the :code stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '(1.1 2.2 3.3)      :code-stackdepth   :integer      '(3)
    :code    '(1.0)              :code-stackdepth   :integer      '(1)
    :code    '()                 :code-stackdepth   :integer      '(0))
   

(tabular
  (fact ":code-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :code    '(0.2 1.3e7)        :code-empty?   :boolean     '(false)
    :code    '()                 :code-empty?   :boolean     '(true))


; ;; equatable


(tabular
  (fact ":code-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :code    '((1 2) (3 4))     :code-equal?      :boolean        '(false)
    :code    '((3 4) (1 2))     :code-equal?      :boolean        '(false)
    :code    '((1 2) (1 2))     :code-equal?      :boolean        '(true)
    ;; missing args     
    :code    '((3 4))           :code-equal?      :boolean        '()
    :code    '((3 4))           :code-equal?      :code           '((3 4))
    :code    '()                :code-equal?      :boolean        '()
    :code    '()                :code-equal?      :code           '())


(tabular
  (fact ":code-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :code    '((1) (88))       :code-notequal?      :boolean      '(true)
    :code    '((88) (1))       :code-notequal?      :boolean      '(true)
    :code    '((1) (1))        :code-notequal?      :boolean      '(false)
    ;; missing args    
    :code    '((88))           :code-notequal?      :boolean      '()
    :code    '((88))           :code-notequal?      :code         '((88))
    :code    '()               :code-notequal?      :boolean      '()
    :code    '()               :code-notequal?      :code         '())

; ;; movable