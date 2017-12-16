(ns push.instructions.base.code_test
  (:require [push.interpreter.core :as i]
            [push.type.definitions.quoted :as qc]
            [push.type.definitions.quoted :as qc]
            [push.type.definitions.tagspace :as ts]
            )
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.module.code])
  )


;; helpers

(defn qq
  "shorthand for 'list containing push-quoted item'"
  [item]
  (list (qc/push-quote item)))

;; a fixture

(def huge-list (repeat 131070 1))
(def teeny (push.core/interpreter :config {:max-collection-size 9}))


(tabular
  (fact ":code-append concats two :code items, wrapping them in lists first if they aren't already"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-append    :exec      (qq '(8 9 1.1))
    :code    '(2 3)                 :code-append    :exec      (qq '(3 2))
    :code    '(() 3)                :code-append    :exec      (qq '(3))
    :code    '(2 ())                :code-append    :exec      (qq '(2))
    :code    '(() ())               :code-append    :exec      (qq '())
    :code    '(2)                   :code-append    :exec      '()
    )

(tabular
  (fact ":code-append checks for oversized results and pushes an `:error` if one arises"
    (register-type-and-check-instruction-in-this-interpreter
      teeny
      ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :code       '((1 2 3) (4 5))
                          :code-append      :exec       (qq '(4 5 1 2 3))
    :code       '((1 2 3 4 5) (6 7 8 9 10))
                          :code-append      :exec       '()
    :code       '((1 2 3 4 5) (6 7 8 9 10))
                          :code-append      :error      '({:item ":code-append tried to push an oversized item to :exec", :step 0})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )

(tabular
  (fact ":code-atom? pushes true to :boolean if the top :code is not a list"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; are you alone?
    :code    '(1.1 '(8 9))         :code-atom?        :exec        '(true)
    :code    '(() 8)               :code-atom?        :exec        '(false)
    ;; …except in silence
    :code    '()                   :code-atom?        :exec        '())


(tabular
  (fact ":code-cons conj's the second :code item onto the first, coercing it to a list if necessary"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-cons    :exec     (qq '((8 9) 1.1))
    :code    '(2 3)                 :code-cons    :exec           (qq '(3 2))
    :code    '(() 3)                :code-cons    :exec             (qq '(3))
    :code    '(2 ())                :code-cons    :exec          (qq '(() 2))
    :code    '(() ())               :code-cons    :exec            (qq '(()))
    :code    '(2)                   :code-cons    :exec                   '()
    )


(tabular
  (fact ":code-cons checks for oversized results and pushes an `:error` if one arises"
    (register-type-and-check-instruction-in-this-interpreter
      teeny
      ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :code       '((1 2 3) (4))
                          :code-cons         :exec     (qq '((4) 1 2 3))
    :code       '((1 2 3 4 5) (6 7 8 9 10))
                          :code-cons         :exec     '()
    :code       '((1 2 3 4 5) (6 7 8 9 10))
                          :code-cons         :error    '({:item ":code-cons tried to push an oversized item to :exec", :step 0})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )

(tabular
  (fact ":code-container returns the smallest, first container of code/1 in code/2"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '(8 (8 9))          :code-container    :exec         (qq '(8 9))
    :code    '(2 2)              :code-container    :exec            (qq '())
    :code    '(2 (1 (2 (3))))    :code-container    :exec       (qq '(2 (3)))
    :code    '(() (()))          :code-container    :exec          (qq '(()))
    :code    '((3) (0 ((1 2) ((3) 4))))
                                 :code-container    :exec       (qq '((3) 4))
    :code    '((3) (0 ((1 (3)) ((3) 4))))
                                 :code-container    :exec       (qq '(1 (3)))
    :code    '((1 (2)) (1 (2) 3))
                                 :code-container    :exec            (qq '())
    :code    '(2)                :code-container        :exec             '()
    )



(tabular
  (fact ":code-contains? returns true if the second item contains (or is) the first anywhere"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '(8 (8 9))          :code-contains?    :exec        '(true)
    :code    '(2 2)              :code-contains?    :exec        '(true)
    :code    '(2 3)              :code-contains?    :exec        '(false)
    :code    '(() (()))          :code-contains?    :exec        '(true)
    :code    '(2 ((1 2) (3 4)))  :code-contains?    :exec        '(true)
    :code    '((1 (2)) (1 (2) 3))
                                 :code-contains?    :exec        '(false)
    :code    '(2)                :code-contains?    :exec        '())



(tabular
  (fact ":code-do executes the top :code item and :code-pop"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; do it
    :code    '((1.1) (8 9))         :code-do        :exec
                         (list (list (qc/push-quote '(1.1)) '(1.1) :code-pop))
    :code    '(2 3)                 :code-do        :exec
                                   (list (list (qc/push-quote 2) 2 :code-pop))
    :code    '(() 3)                :code-do        :exec
                               (list (list (qc/push-quote '()) '() :code-pop))
    :code    '()                    :code-do        :exec        '()
    )


(tabular
  (fact ":code-do* executes the top :code item and :code-pop"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; JUST do it
    :code    '((1.1) (8 9))         :code-do*        :exec        '((1.1))
    :code    '(2 3)                 :code-do*        :exec        '(2)
    :code    '(() 3)                :code-do*        :exec        '(())
    :code    '()                    :code-do*        :exec        '()
    )


(tabular
  (fact ":code-do*count does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(2 9)}         :code-do*count     {:exec '(
                                                      (2 0 :code-quote :foo :code-do*range))
                                                   :scalar '(9)
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(-2 -9)}         :code-do*count     {:exec '((-2 :code-quote :foo))
                                                   :scalar '(-9)
                                                   :code '(:bar)}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(0 -9)}          :code-do*count     {:exec '((0 :code-quote :foo))
                                                   :scalar '(-9)
                                                   :code '(:bar)}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(0.3 11/4)}      :code-do*count     {:exec '((0.3 0 :code-quote :foo :code-do*range))
                                                   :scalar '(11/4)
                                                   :code '(:bar)}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(-11/4)}      :code-do*count     {:exec '((-11/4 :code-quote :foo))
                                                   :scalar '()
                                                   :code '(:bar)}
    ; ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:code     '()
     :scalar  '(0 -9)}         :code-do*count     {:exec '()
                                                   :scalar '(0 -9)
                                                   :code '()}
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :scalar  '()}             :code-do*count     {:exec '()
                                                   :scalar '()
                                                   :code '(:foo)}
                                                   )


(tabular
  (fact ":code-do*range does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(2 9)}         :code-do*range     {:exec '((9 :foo
                                                    (8 2 :code-quote :foo :code-do*range)))
                                                   :scalar '()
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(-2 -9)}         :code-do*range     {:exec '((-9 :foo
                                                    (-8 -2 :code-quote :foo :code-do*range)))
                                                   :scalar '()
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(-2917.5M -9.3e32M)}  :code-do*range     {:exec '((-9.3E+32M :foo (-929999999999999999999999999999999M -2917.5M :code-quote :foo :code-do*range)))
                                                   :scalar '()
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; these are finished up
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(2 2)}         :code-do*range     {:exec '((2 :foo))
                                                   :scalar '()
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(0 1)}         :code-do*range     {:exec '((0 :foo))
                                                   :scalar '()
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(5/2 2.1)}     :code-do*range     {:exec '((3.1 :foo))
                                                   :scalar '()
                                                   :code '(:bar)}
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ;; missing arguments
    {:code     '()
     :scalar  '(-2 -9)}         :code-do*range     {:exec '()
                                                     :scalar '(-2 -9)
                                                     :code '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :scalar  '(-2)}         :code-do*range     {:exec '()
                                                     :scalar '(-2)
                                                     :code '(:foo)})



(tabular
  (fact ":code-do*range consumes all arguments and reports runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar  '(2M 1/3)}         :code-do*range    {:exec '()
                                                    :scalar '()
                                                    :code '(:bar)
                                                    :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0},
                                                      {:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":code-do*times does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(2 9)}         :code-do*times     {:exec '((:foo (1 :code-quote :foo :code-do*times)))
                                                   :scalar  '(9)
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(-2 -9)}       :code-do*times     {:exec '(:foo)
                                                   :scalar  '(-9)
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(1 -9)}        :code-do*times     {:exec '((:foo (0 :code-quote :foo :code-do*times)))
                                                   :scalar  '(-9)
                                                   :code '(:bar)}


    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(7.8 -9)}       :code-do*times     {:exec '((:foo (6.8 :code-quote :foo :code-do*times)))
                                                   :scalar  '(-9)
                                                   :code '(:bar)}

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(77631M -9)}       :code-do*times     {:exec '((:foo (77630M :code-quote :foo :code-do*times)))
                                                   :scalar  '(-9)
                                                   :code '(:bar)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(0.125 -9)}       :code-do*times     {:exec '((:foo (-0.875 :code-quote :foo :code-do*times)))
                                                   :scalar  '(-9)
                                                   :code '(:bar)}

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :scalar   '(0 2)}          :code-do*times     {:exec '(:foo)
                                                   :scalar  '(2)
                                                   :code '(:bar)}
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ;; missing arguments
    {:code     '()
     :scalar   '(-2 -9)}        :code-do*times      {:exec '()
                                                     :scalar  '(-2 -9)
                                                     :code '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :scalar   '()}           :code-do*times        {:exec '()
                                                     :scalar  '()
                                                     :code '(:foo)})


(tabular
  (fact ":code-drop drops the first n elements of a code item, using that modulo trick"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(1)}            :code-drop     {:exec   (qq '(2 3))
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(5)}            :code-drop     {:exec     (qq '(3))
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(1/3)}          :code-drop     {:exec   (qq '(2 3))
                                               :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(-1/3)}         :code-drop     {:exec (qq '(1 2 3))
                                               :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(-3)}           :code-drop     {:exec (qq '(1 2 3))
                                               :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '(() :bar)
     :scalar  '(-3)}           :code-drop     {:exec      (qq '())
                                               :scalar    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '(77)
     :scalar  '(1)}            :code-drop     {:exec     (qq '(77))
                                               :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '()
     :scalar  '(1)}            :code-drop     {:exec     '()
                                               :scalar  '(1)})


(tabular
  (fact ":code-extract picks an indexed item out of a :code item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(2)}            :code-extract     {:exec (qq 8) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(0)}            :code-extract     {:exec (qq '(9 8 7 6)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(1/3)}          :code-extract     {:exec (qq 9) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(-4/3)}         :code-extract     {:exec (qq 6) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(-2)}           :code-extract     {:exec (qq 7) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '((9 8 7 6))
     :scalar   '(221)}          :code-extract     {:exec (qq 9) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; nesting
    {:code     '(((9) (8 (7 6))))
     :scalar   '(1)}           :code-extract      {:exec (qq '(9)) }
    {:code     '(((9) (8 (7 6))))
     :scalar   '(2)}           :code-extract      {:exec (qq 9) }
    {:code     '(((9) (8 (7 6))))
     :scalar   '(3)}           :code-extract      {:exec (qq '(8 (7 6))) }
    {:code     '(((9) (8 (7 6))))
     :scalar   '(4)}           :code-extract      {:exec (qq 8) }
    {:code     '(((9) (8 (7 6))))
     :scalar   '(6)}           :code-extract      {:exec (qq 7) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; does not traverse vectors
    {:code     '(([9] (8 [7 6])))
     :scalar   '(1)}           :code-extract      {:exec (qq [9]) }

    {:code     '(([9] (8 [7 6])))
     :scalar   '(2)}           :code-extract      {:exec (qq '(8 [7 6])) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; does not traverse sets
    {:code     '((#{9} (8 [7 6])))
     :scalar   '(1)}           :code-extract      {:exec (qq #{9}) }
    {:code     '((#{9} (8 [7 6])))
    :scalar   '(2)}            :code-extract       {:exec (qq '(8 [7 6])) }
     )


(tabular
  (fact ":code-first pushes the first item of the top :code item, if it's a non-empty list"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction  ?get-stack     ?expected
    :code    '((1.1) (8 9))       :code-first   :exec            (qq 1.1)
    :code    '((2 3))             :code-first   :exec              (qq 2)
    :code    '(() 3)              :code-first   :exec                 '()
    :code    '(2)                 :code-first   :exec              (qq 2)
    :code    '(((3)))             :code-first   :exec           (qq '(3))
    :code    '()                  :code-first   :exec                 '()
    )


(tabular
  (fact ":code-first does not affect other collection types (like maps, records or sets)"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    :code    '([1 2 3])           :code-first        :exec      (qq [1 2 3])
    :code    '({:a 8 :b 7})       :code-first        :exec  (qq {:a 8 :b 7})
    )


(tabular
  (fact ":code-if throws one of the top two :code items away, based on a :boolean"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:code     '(:foo :bar)
     :boolean  '(true)}         :code-if        {:code '()
                                                 :boolean '()
                                                 :exec (qq :bar) }
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :boolean  '(false)}        :code-if        {:code '()
                                                 :boolean '()
                                                 :exec (qq :foo) }
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:code     '(:foo)
     :boolean  '(false)}         :code-if            {:code '(:foo)
                                                     :boolean '(false)
                                                     :exec '()}
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :boolean  '()}         :code-if                {:code '(:foo :bar)
                                                     :boolean '()
                                                     :exec '()} )



(tabular
  (fact ":code-insert pops an :scalar and two :code items, and replaces the node at the indicated position in the second code with the first"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(1)}            :code-insert   {:exec (qq '(99 2 3)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(1/3)}          :code-insert   {:exec (qq '(99 2 3)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(-4/3)}         :code-insert   {:exec (qq '(1 2 99)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(0)}            :code-insert   {:exec (qq 99) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(3.1)}          :code-insert   {:exec (qq 99) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(3)}            :code-insert   {:exec (qq '(1 2 99)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; wrapping index
    {:code     '(99 (1 2 3))
     :scalar   '(11)}            :code-insert  {:exec (qq '(1 2 99)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(12)}            :code-insert  {:exec (qq 99) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 (1 2 3))
     :scalar   '(-1)}            :code-insert  {:exec (qq '(1 2 99)) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; traverses trees
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(1)}            :code-insert   {:exec (qq '(99 (()) (3 4))) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(2)}            :code-insert   {:exec
                                                (qq '((99 (2)) (()) (3 4))) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(3)}            :code-insert   {:exec
                                                  (qq '((1 99) (()) (3 4))) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(4)}            :code-insert   {:exec
                                                (qq '((1 (99)) (()) (3 4))) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(5)}            :code-insert   {:exec
                                                   (qq '((1 (2)) 99 (3 4))) }
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(99 ((1 (2)) ( ()) (3 4)))
     :scalar   '(52/7)}         :code-insert   {:exec
                                                    (qq '((1 (2)) (()) 99)) }
     )

(tabular
  (fact "code-insert produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:code '(99 (4 5 6))
     :scalar '(2)}
                       :code-insert
                                          {:exec (qq '(4 99 6)) }
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code '((1 2 3 4 5) (6 7 8 9 10))
     :scalar '(2)}
                       :code-insert       {:exec '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code '((1 2 3 4 5) (6 7 8 9 10))
     :scalar '(2)}
                       :code-insert       {:error '({:item ":code-insert tried to push an oversized item to :exec", :step 0})}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
  (fact ":code-length pushes the count of the top :code item (including :set or :vector items) or 1 if it is not a collection (:tagspace is not a collection)"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; pick a number
    :code    '((1 2 3) (8 9))     :code-length        :exec        '(3)
    :code    '((2))               :code-length        :exec        '(1)
    :code    '(() 3)              :code-length        :exec        '(0)
    :code    '(2)                 :code-length        :exec        '(1)
    :code    '((2 (3)))           :code-length        :exec        '(2)
    :code    '([1 2 3])           :code-length        :exec        '(3)
    :code    '(#{1 2 3})          :code-length        :exec        '(3)
    :code    (list (ts/make-tagspace {1 2 3 4}))
                                  :code-length        :exec        '(1)
    :code    '()                  :code-length        :exec        '()
    )


(tabular
  (fact ":code-list puts the top 2 :code items into a list on the :code stack"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction   ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))       :code-list     :exec     (qq '((8 9) (1.1)))
    :code    '(2 3)               :code-list     :exec             (qq '(3 2))
    :code    '(() 3)              :code-list     :exec            (qq '(3 ()))
    :code    '(2 ())              :code-list     :exec            (qq '(() 2))
    :code    '(() ())             :code-list     :exec           (qq '(() ()))
    :code    '(2)                 :code-list     :exec                     '()
    )


(tabular
  (fact "code-list produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:code '((1) 2)}
                       :code-list       {:exec (qq  '(2 (1))) }
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code '((1 2 3 4 5) (6 7 8 9 10))}
                       :code-list       {:exec '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code '((1 2 3 4 5) (6 7 8 9 10))}
                       :code-list       {:error '({:item ":code-list tried to push an oversized item to :exec", :step 0})}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
  (fact ":code-map does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks              ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '((1 2 3) :bar)
     :exec  '(:foo)}          :code-map     {:exec '((:code-quote ()
                                                    (:code-quote 1 :foo)
                                                    :code-cons
                                                    (:code-quote (2 3)
                                                      :code-reduce :foo)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '((1) :bar)
     :exec  '(:foo)}          :code-map     {:exec '((:code-quote ()
                                                    (:code-quote 1 :foo)
                                                    :code-cons))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(() :bar)
     :exec  '(:foo)}          :code-map     {:exec '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(88 :bar)
     :exec  '(:foo)}          :code-map     {:exec '((:code-quote ()
                                                    (:code-quote 88 :foo)
                                                    :code-cons))}
    )

(tabular
  (fact "code-map produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:code  '(1 2)
     :exec  '(:foo)}
                       :code-map       {:code '(2)
                                        :exec '((:code-quote () (:code-quote 1 :foo) :code-cons))}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:code  '([1 2] 10)
      :exec  '(:foo)}
                        :code-map       {:exec '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:code  '([1 2] 10)
      :exec  '(:foo)}
                        :code-map       {:error '({:item ":code-map tried to push an oversized item to :exec", :step 0})}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
  (fact ":code-reduce does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '((1 2 3) :bar)
     :exec  '(:foo)}          :code-reduce     {:exec '(((:code-quote 1 :foo)
                                                  :code-cons
                                                  (:code-quote (2 3)
                                                    :code-reduce :foo)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '((1) :bar)
     :exec  '(:foo)}          :code-reduce     {:exec '(((:code-quote 1 :foo)
                                                  :code-cons))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(() :bar)
     :exec  '(:foo)}          :code-reduce     {:exec '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(88 :bar)
     :exec  '(:foo)}          :code-reduce     {:exec '(((:code-quote 88 :foo)
                                                  :code-cons))}
    )


(tabular
  (fact "code-reduce produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:code  '(1 2)
     :exec  '(:foo)}
                       :code-reduce       {:code '(2)
                                           :exec '(((:code-quote 1 :foo) :code-cons))}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:code  '([1 2 3 4 5] 6)
      :exec  '(:foo)}
                        :code-reduce       {:exec '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:code  '([1 2 3 4 5] 6)
      :exec  '(:foo)}
                        :code-reduce       {:error '({:item ":code-reduce tried to push an oversized item to :exec", :step 0})}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(tabular
  (fact ":code-member? pushes true if the second item is found in the root of the first"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; be vewwy quiet
    :code    '(1.1 '(8 9))         :code-noop        :code        '(1.1 '(8 9))
    :code    '()                   :code-noop        :code        '())


(tabular
  (fact ":code-nth takes the nth item of :code, using that modulo trick"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(1)}            :code-nth     {:code '(2 :bar)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(4/3)}          :code-nth     {:code '(3 :bar)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(-1/3)}         :code-nth     {:code '(1 :bar)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(10)}           :code-nth     {:code '(2 :bar)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :scalar  '(-4)}            :code-nth     {:code '(3 :bar)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '(77)
     :scalar  '(1)}            :code-nth     {:code '(77)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '(77)
     :scalar  '(1183/5)}       :code-nth     {:code '(77)
                                              :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '()
     :scalar  '(1)}            :code-nth     {:code '()
                                              :scalar  '(1)})


(tabular
  (fact ":code-null? pushes true to :boolean if the top :code item is an empty list, false otherwise"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; an echoing sound is heard
    :code    '(1.1 (8 9))         :code-null?        :exec         '(false)
    :code    '(() 8)              :code-null?        :exec         '(true)

;;;;; PROBLEM HERE
    :code    '(() 8)              :code-null?        :exec         '(true)
    ;; …except in silence
    :code    '()                  :code-null?        :exec         '()
    )


(tabular
  (fact ":code-position finds the top :code item index in the second item, if at all; coerces second item to list if needed"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; say where
    :code    '(3 (1 2 3 4))      :code-position        :scalar         '(2)
    :code    '(6 (1 2 3 4))      :code-position        :scalar         '(-1)
    :code    '(3 (1 2 3 1 2 3 4))
                                 :code-position        :scalar         '(2)
    :code    '((2) ((1) (2) (3) (4)))
                                 :code-position        :scalar         '(1)
    :code    '(2 2)              :code-position        :scalar         '(0)
    :code    '(2)                :code-position        :scalar         '()
    :code    '(2)                :code-position        :code           '(2))


(tabular
  (fact ":code-quote moves the top :exec item to :code"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks code-module ?instruction) => (contains ?expected))

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
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; what's left for me now?
    :code    '((1 2 3) (8 9))     :code-rest        :exec        '((2 3))
    :code    '((2))               :code-rest        :exec        '(())
    :code    '(() 3)              :code-rest        :exec        '(())
    :code    '(2)                 :code-rest        :exec        '(())
    :code    '((2 (3)))           :code-rest        :exec        '(((3)))
    :code    '()                  :code-rest        :exec        '())



(tabular
  (fact ":code-points counts the number of points in the top :code item"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '((1 2 3) (8 9))     :code-points        :scalar         '(4)
    :code    '((2))               :code-points        :scalar         '(2)
    :code    '(() 3)              :code-points        :scalar         '(1)
    :code    '(2)                 :code-points        :scalar         '(1)
    :code    '((1 (2 (3))))       :code-points        :scalar         '(6)
    :code    '([1 2 3])           :code-points        :scalar         '(1)
    :code    '(#{1 2 3})          :code-points        :scalar         '(1)
    :code    '((1 2 {1 2 3 4}))   :code-points        :scalar         '(4)
    :code    '()                  :code-points        :scalar         '())



(tabular
  (fact ":code-size counts the number of points in the top :code item, counting contents of every collection type"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '((1 2 3) (8 9))     :code-size        :scalar         '(4)
    :code    '((2))               :code-size        :scalar         '(2)
    :code    '(() 3)              :code-size        :scalar         '(1)
    :code    '(2)                 :code-size        :scalar         '(1)
    :code    '((1 (2 (3))))       :code-size        :scalar         '(6)
    :code    '([1 2 3])           :code-size        :scalar         '(4)
    :code    '(#{1 2 3})          :code-size        :scalar         '(4)
    :code    '({1 2 3 4})         :code-size        :scalar         '(7)
    :code    '()                  :code-size        :scalar         '())



(tabular
  (fact ":code-subst replaces all copies of arg2 in arg1 with arg3"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; changes…
    :code    '(99 2 (1 2 3 4))      :code-subst        :code        '((1 99 3 4))
    :code    '(99 2 2)              :code-subst        :code        '(99)
    :code    '(99 88 (1 2 3 4))     :code-subst        :code        '((1 2 3 4))
    :code    '(99 (2) (1 ((2) 3) 4))
                                    :code-subst        :code        '((1 (99 3) 4))
    :code    '((99 99) (2) (1 ((2) 3 (2)) (2) 4))
                                    :code-subst        :code        '((1 ((99 99) 3
                                                                      (99 99)) (99 99) 4))
    :code    '(99 (1 2 3 4))        :code-subst        :code        '(99 (1 2 3 4))
    :code    '(99)                  :code-subst        :code        '(99)
    )

(tabular
  (fact "code-reduce produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:code  '(99 2 (1 2 3 4))}
                       :code-subst       {:code   '((1 99 3 4))
                                          :error  '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:code  '([99 99 99 99 99 99] 2 (1 2 3 4))}
                        :code-subst      {:code   '()
                                          :error  '({:item ":code-subst tried to push an oversized item to :code", :step 0})}
     )


(tabular
  (fact ":code-subst DOES NOT change the contents of vectors, maps, records, sets etc"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    :code    '(99 2 [1 2 3 4])      :code-subst        :code        '([1 2 3 4])
    :code    '(99 2 #{1 2 3 4})      :code-subst        :code        '(#{1 2 3 4})
    :code    '(99 2 {2 1, 4 3})      :code-subst        :code        '({2 1, 4 3}))



(tabular
  (fact ":code-wrap returns a :the top :code item in an extra list layer"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; wrap
    :code    '(1)               :code-wrap      :exec        '((1))
    :code    '((3 4))           :code-wrap      :exec        '(((3 4)))
    :code    '(())              :code-wrap      :exec        '((()))
    ;; missing args
    :code    '()                :code-wrap      :code          '())

;; visible


(tabular
  (fact ":code-stackdepth returns the number of items on the :code stack (to :scalar)"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '(1.1 2.2 3.3)      :code-stackdepth   :exec         '(3)
    :code    '(1.0)              :code-stackdepth   :exec         '(1)
    :code    '()                 :code-stackdepth   :exec         '(0))


(tabular
  (fact ":code-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :code    '(0.2 1.3e7)        :code-empty?   :exec        '(false)
    :code    '()                 :code-empty?   :exec        '(true))


; ;; equatable


(tabular
  (fact ":code-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :code    '((1 2) (3 4))     :code-equal?      :exec         '(false)
    :code    '((3 4) (1 2))     :code-equal?      :exec         '(false)
    :code    '((1 2) (1 2))     :code-equal?      :exec         '(true)

    ;; note this depends on Clojure's 'eq function
    :code    '((1 2) (11/11 2.0))
                                :code-equal?      :exec         '(false)
    ;; missing args
    :code    '((3 4))           :code-equal?      :exec         '()
    :code    '((3 4))           :code-equal?      :code           '((3 4))
    :code    '()                :code-equal?      :exec         '()
    :code    '()                :code-equal?      :code           '())


(tabular
  (fact ":code-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :code    '((1) (88))       :code-notequal?      :exec       '(true)
    :code    '((88) (1))       :code-notequal?      :exec       '(true)
    :code    '((1) (1))        :code-notequal?      :exec       '(false)
    ;; missing args
    :code    '((88))           :code-notequal?      :exec       '()
    :code    '((88))           :code-notequal?      :code         '((88))
    :code    '()               :code-notequal?      :exec       '()
    :code    '()               :code-notequal?      :code         '())


; ;; movable
