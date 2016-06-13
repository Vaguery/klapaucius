(ns push.instructions.base.exec_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.module.exec])
  )



(tabular
  (fact ":exec-do*count does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :scalar  '(0)}         :exec-do*count    {:exec ' ((-1 :foo) :bar)
                                                :scalar '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar  '(1/3)}       :exec-do*count    {:exec '((0 1/3 :exec-do*range :foo) :bar)
                                                :scalar '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar  '(2)}        :exec-do*count     {:exec '((0 2 :exec-do*range :foo) :bar)
                                                :scalar '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar  '(-10)}        :exec-do*count    {:exec '((-11 :foo) :bar)
                                                :scalar '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:exec     '()
     :scalar  '(-2 -10)}      :exec-do*count     {:exec '()
                                                   :scalar '(-2 -10)} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo)
     :scalar  '()}      :exec-do*count     {:exec '(:foo)
                                                   :scalar '()} )



(tabular
  (fact ":exec-do*times does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :scalar   '(0)}         :exec-do*times    {:exec '(:foo :bar)
                                                :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(2)}        :exec-do*times     {:exec '((:foo (1 :exec-do*times :foo)) :bar)
                                                :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(-10)}        :exec-do*times    {:exec '(:foo :bar)
                                                :scalar  '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:exec     '()
     :scalar   '(-2 -10)}      :exec-do*times     {:exec '()
                                                   :scalar  '(-2 -10)} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo)
     :scalar   '()}      :exec-do*times     {:exec '(:foo)
                                                   :scalar  '()} )

(tabular
  (fact ":exec-do*range does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :scalar   '(4 2)}        :exec-do*range     {:exec '((2 :foo (3 4 :exec-do*range :foo)) :bar)
                                                   :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(4.9 5/2)}    :exec-do*range     {:exec '((5/2 :foo (7/2 4.9 :exec-do*range :foo)) :bar)
                                                   :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(3 3)}         :exec-do*range     {:exec '((3 :foo) :bar)
                                                     :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(3.5 11/3)}    :exec-do*range     {:exec '((8/3 :foo) :bar)
                                                     :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(2 10)}        :exec-do*range     {:exec '((10 :foo
                                                      (9 2 :exec-do*range :foo)) :bar)
                                                   :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(10 10)}        :exec-do*range     {:exec '((10 :foo) :bar)
                                                     :scalar  '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :scalar   '(-2 -10)}      :exec-do*range     {:exec '((-10 :foo 
                                                      (-9 -2 :exec-do*range :foo)) :bar)
                                                   :scalar  '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ;; missing arguments
    {:exec     '()
     :scalar   '(-2 -10)}      :exec-do*range     {:exec '()
                                                   :scalar  '(-2 -10)} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo)
     :scalar   '(-2)}      :exec-do*range     {:exec '(:foo)
                                                   :scalar  '(-2)} )



(tabular
  (fact ":exec-if pops either the top or second :exec item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    {:exec '(1 2 3)
     :boolean '(false)}           :exec-if            {:exec '(2 3)
                                                    :boolean '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '(1 2 3)
     :boolean '(true)}            :exec-if            {:exec '(1 3)
                                                    :boolean '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:exec '(1)
     :boolean '(true)}            :exec-if            {:exec '(1)
                                                    :boolean '(true)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '(1 2 3)
     :boolean '()}                :exec-if            {:exec '(1 2 3)
                                                    :boolean '()})


(tabular
  (fact ":exec-k applies the K combinator"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; not the second one
    :exec    '(1.1 2.2 (3.3))    :exec-k          :exec         '(1.1 (3.3)) 
    ;; missing arguments
    :exec    '(1.0)              :exec-k          :exec         '(1.0)     
    :exec    '()                 :exec-k          :exec         '())


(tabular
  (fact ":exec-noop does nothing"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; nothing happens
    :exec    '(1.1 2.2)          :exec-noop          :exec         '(1.1 2.2) 
    :exec    '(1.0)              :exec-noop          :exec         '(1.0)     
    :exec    '()                 :exec-noop          :exec         '())


(tabular
  (fact ":exec-s applies the S combinator"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; forever
    :exec    '(1.1 2.2 3.3 4.4)  :exec-s          :exec         '(1.1 3.3 (2.2 3.3) 4.4) 
    :exec    '(1.1 2.2)          :exec-s          :exec         '(1.1 2.2)     
    :exec    '()                 :exec-s          :exec         '())


(tabular
  (fact ":exec-do*while does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :boolean  '(true)}         :exec-do*while    {:exec '((:foo :exec-while :foo) :bar)
                                                :boolean '(true)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :boolean  '(false)}        :exec-do*while    {:exec '((:foo :exec-while :foo) :bar)
                                                :boolean '(false)} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ; ;; missing arguments
    {:exec     '()
     :boolean  '(true)}         :exec-do*while    {:exec '()
                                                :boolean '(true)})


(tabular
  (fact ":exec-when does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :boolean  '(true)}         :exec-when    {:exec '(:foo :bar)
                                               :boolean '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :boolean  '(false)}        :exec-when    {:exec '(:bar)
                                               :boolean '()} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ; ; ;; missing arguments
    {:exec     '()
     :boolean  '(true)}         :exec-when    {:exec '()
                                               :boolean '(true)} 
    ; ; ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :boolean  '()}             :exec-when    {:exec '(:foo :bar)
                                               :boolean '()})


(tabular
  (fact ":exec-while does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :boolean  '(true)}         :exec-while    {:exec '((:foo :exec-while :foo) :bar)
                                                :boolean '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :boolean  '(false)}        :exec-while    {:exec '(() :bar)
                                                :boolean '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ; ;; missing arguments
    {:exec     '()
     :boolean  '(true)}         :exec-while    {:exec '()
                                                :boolean '(true)} 
    ; ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(:foo :bar)
     :boolean  '()}             :exec-while    {:exec '(:foo :bar)
                                                :boolean '()})


(tabular
  (fact ":exec-y applies the Y combinator"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; forever
    :exec    '(1.1 2.2)          :exec-y          :exec         '((1.1 :exec-y 1.1) 2.2) 
    :exec    '(1.0)              :exec-y          :exec         '((1.0 :exec-y 1.0))     
    :exec    '()                 :exec-y          :exec         '())



;; visible


(tabular
  (fact ":exec-stackdepth returns the number of items on the :exec stack (to :scalar)"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :exec    '(1.1 2.2 3.3)      :exec-stackdepth   :scalar      '(3)
    :exec    '(1.0)              :exec-stackdepth   :scalar      '(1)
    :exec    '()                 :exec-stackdepth   :scalar      '(0))
   

(tabular
  (fact ":exec-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :exec    '(0.2 1.3e7)        :exec-empty?   :boolean     '(false)
    :exec    '()                 :exec-empty?   :boolean     '(true))


;; equatable


(tabular
  (fact ":exec-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :exec    '((1 2) (3 4))     :exec-equal?      :boolean        '(false)
    :exec    '((3 4) (1 2))     :exec-equal?      :boolean        '(false)
    :exec    '((1 2) (1 2))     :exec-equal?      :boolean        '(true)
    ;; missing args     
    :exec    '((3 4))           :exec-equal?      :boolean        '()
    :exec    '((3 4))           :exec-equal?      :exec           '((3 4))
    :exec    '()                :exec-equal?      :boolean        '()
    :exec    '()                :exec-equal?      :exec           '())


(tabular
  (fact ":exec-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :exec    '((1) (88))       :exec-notequal?      :boolean      '(true)
    :exec    '((88) (1))       :exec-notequal?      :boolean      '(true)
    :exec    '((1) (1))        :exec-notequal?      :boolean      '(false)
    ;; missing args    
    :exec    '((88))           :exec-notequal?      :boolean      '()
    :exec    '((88))           :exec-notequal?      :exec         '((88))
    :exec    '()               :exec-notequal?      :boolean      '()
    :exec    '()               :exec-notequal?      :exec         '())

; ;; movable