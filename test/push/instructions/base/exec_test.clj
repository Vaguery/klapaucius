(ns push.instructions.base.exec_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.module.exec])
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
  (fact ":exec-doabunch*count does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks exec-module ?instruction) => (contains ?expected))

     ?new-stacks                ?instruction             ?expected

     {:exec     '(:foo :bar)
      :scalar  '(0)}         :exec-doabunch*count    {:exec ' ((-1 :foo) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec     '(:foo :bar)
      :scalar  '(1/3)}       :exec-doabunch*count    {:exec '((0 1/3 :exec-doabunch*range :foo) :bar)
                                                 :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec     '(:foo :bar)
      :scalar  '(12312.5M)}  :exec-doabunch*count     {:exec '((0 12.5M :exec-doabunch*range :foo) :bar)
                                                 :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
  (fact ":exec-doafew*count does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks exec-module ?instruction) => (contains ?expected))

     ?new-stacks                ?instruction             ?expected

     {:exec     '(:foo :bar)
      :scalar  '(0)}          :exec-doafew*count    {:exec ' ((-1 :foo) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec     '(:foo :bar)
      :scalar  '(-111/7)}     :exec-doafew*count    {:exec '((-48/7 :foo) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec     '(:foo :bar)
      :scalar  '(12312.5M)}   :exec-doafew*count     {:exec '((0 2.5M :exec-doafew*range :foo) :bar)
                                                 :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


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
 (fact ":exec-doafew*times does complicated things involving continuations (see tests)"
   (check-instruction-with-all-kinds-of-stack-stuff
     ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec    '(:foo :bar)
     :scalar  '(4.9M)}       :exec-doafew*times     {:exec '((:foo (3.9M :exec-doafew*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(124.9M)}     :exec-doafew*times     {:exec '((:foo (3.9M :exec-doafew*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(111N)}       :exec-doafew*times     {:exec '((:foo (0N :exec-doafew*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
 (fact ":exec-doabunch*times does complicated things involving continuations (see tests)"
   (check-instruction-with-all-kinds-of-stack-stuff
     ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec    '(:foo :bar)
     :scalar  '(121014.9M)}  :exec-doabunch*times   {:exec '((:foo (13.9M :exec-doabunch*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(124.9M)}     :exec-doabunch*times   {:exec '((:foo (23.9M :exec-doabunch*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(111N)}       :exec-doabunch*times   {:exec '((:foo (10N :exec-doabunch*times :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


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
  (fact ":exec-doafew*range does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks exec-module ?instruction) => (contains ?expected))

     ?new-stacks                ?instruction             ?expected

     {:exec    '(:foo :bar)
      :scalar  '(4.9M 5/2)}       :exec-doafew*range  {:exec '((5/2 :foo (7/2 4.9M :exec-doafew*range :foo)) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec    '(:foo :bar)
      :scalar  '(124.9M -125/2)}  :exec-doafew*range  {:exec '((-5/2 :foo (-3/2 4.9M :exec-doafew*range :foo)) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:exec    '(:foo :bar)
      :scalar  '(-124.9M 111N)}  :exec-doafew*range  {:exec '((1N :foo (0N -4.9M :exec-doafew*range :foo)) :bar)
                                                      :scalar '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
 (fact ":exec-doabunch*range does complicated things involving continuations (see tests)"
   (check-instruction-with-all-kinds-of-stack-stuff
     ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec    '(:foo :bar)
     :scalar  '(4.9M 117215/2)}       :exec-doabunch*range  {:exec '((15/2 :foo (13/2 4.9M :exec-doabunch*range :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(124.9M -125/2)}  :exec-doabunch*range  {:exec '((-125/2 :foo (-123/2 24.9M :exec-doabunch*range :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec    '(:foo :bar)
     :scalar  '(-124.9M 111N)}  :exec-doabunch*range  {:exec '((11N :foo (10N -24.9M :exec-doabunch*range :foo)) :bar)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":exec-do*range consumes arguments but reports runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks exec-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:exec     '(:foo :bar)
     :scalar   '(4M 2/3)}        :exec-do*range   {:exec '(:bar)
                                                   :error  '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0} {:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


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



;; visible


(tabular
  (fact ":exec-stackdepth returns the number of items on the :exec stack (to :scalar)"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :exec    '(1.1 2.2 3.3)      :exec-stackdepth   :exec    '(3 1.1 2.2 3.3)
    :exec    '(1.0)              :exec-stackdepth   :exec         '(1 1.0)
    :exec    '()                 :exec-stackdepth   :exec         '(0)
    )


(tabular
  (fact ":exec-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :exec    '(0.2 1.3e7)        :exec-empty?   :exec       '(false 0.2 1.3e7)
    :exec    '()                 :exec-empty?   :exec       '(true))


;; equatable


(tabular
  (fact ":exec-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :exec    '((1 2) (3 4))     :exec-equal?      :exec           '(false)
    :exec    '((3 4) (1 2))     :exec-equal?      :exec           '(false)
    :exec    '((1 2) (1 2))     :exec-equal?      :exec           '(true)
    ;; missing args
    :exec    '((3 4))           :exec-equal?      :exec           '((3 4))
    :exec    '()                :exec-equal?      :exec           '())


(tabular
  (fact ":exec-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :exec    '((1) (88))       :exec-notequal?      :exec        '(true)
    :exec    '((88) (1))       :exec-notequal?      :exec        '(true)
    :exec    '((1) (1))        :exec-notequal?      :exec        '(false)
    ;; missing args
    :exec    '((88))           :exec-notequal?      :exec         '((88))
    :exec    '()               :exec-notequal?      :exec         '())


(tabular
  (fact ":exec-laterloop returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction    ?get-stack     ?expected
    :exec    '(1 2 3)      :exec-laterloop   :exec     '(2 3 (1 :exec-laterloop 1))
    :exec    '(1)          :exec-laterloop   :exec      '(1))
