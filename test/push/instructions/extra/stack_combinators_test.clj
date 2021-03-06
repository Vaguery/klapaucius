(ns push.instructions.extra.stack-combinators-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.core :as push])
  (:require [push.type.core :as t])
  (:require [push.type.definitions.quoted :as qc])
  (:require [push.util.code-wrangling :as u])
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.movable)
  )


;;;;;;;;;;;;;

;; fixtures

(def foo-type (-> (t/make-type  :foo
                                :recognized-by number?
                                :attributes #{:foo})
                   make-movable))

(def teeny (push/interpreter :config {:max-collection-size 9}))
;;;;;;;;;;;;;;;

;; helper

(defn q!
  [item]
  (qc/push-quote item))
;;;;;;;;;;;;;;;


(tabular
  (fact ":foo-againlater copies the top item from :foo to the tail of :exec"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack         ?expected
    :foo       '(1 2 3 4 5)    :foo-againlater   :foo              '(2 3 4 5)
    :foo       '(1 2 3 4 5)    :foo-againlater   :exec             '(1 () 1)
    ;; missing args
    :foo       '()             :foo-againlater   :exec              '())


(tabular
  (fact ":code-againlater quotes its items"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack         ?expected
    :code       '(1 2 3 4 5)    :code-againlater   :code           '(2 3 4 5)
    :code       '(1 2 3 4 5)    :code-againlater   :exec   (list (q! 1) () (q! 1))
    ;; missing args
    :code       '()             :code-againlater   :exec              '()
    )

(fact "code-againlater clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-againlater)) =>
  #"All items taken from :code are returned"
  )



(tabular
  (fact ":foo-againlater produces an error when the result is oversized"
    (check-instruction-here-using-this
      (i/register-type teeny foo-type)
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction        ?expected
    {:foo '([1 1] 2 3 4)
     :exec '(99)}       :foo-againlater      {:foo '(2 3 4)
                                              :exec '([1 1] (99) [1 1])
                                              :error '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:foo '([1 1 1 1 1 1 1 1 1 1 1] 2 3 4)
      :exec '(99)}       :foo-againlater      {:foo   '(2 3 4)
                                               :exec  '(99)
                                               :error '({:item "foo-againlater produced an oversized result", :step 0})}
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )



(tabular
  (fact "`foo-later` pops the top :foo and puts it at the end of :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo   '(9 8 7 6 5 4)
     :exec  '(2)}             :foo-later      {:foo   '(8 7 6 5 4)
                                               :exec  '(2 9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo   '(9)
     :exec  '()}              :foo-later      {:foo   '()
                                               :exec  '(9)}
    )


(tabular
  (fact ":foo-later produces an error when the result is oversized"
    (check-instruction-here-using-this
      (i/register-type teeny foo-type)
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction        ?expected
    {:foo '([1 1] 2 3 4)
     :exec '(99)}         :foo-later         {:foo '(2 3 4)
                                              :exec '(99 [1 1])
                                              :error '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:foo '([1 1 1 1 1 1 1 1 1 1 1] 2 3 4)
      :exec '(99)}         :foo-later         {:foo  '(2 3 4)
                                               :exec '(99)
                                               :error '({:item "foo-later produced an oversized result", :step 0})}
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     )


(fact "later-instruction quotes :code items"
 (push/get-stack
   (i/execute-instruction
     (push/interpreter :stacks {:exec '(1 2 3) :code '(99)}) :code-later)
   :exec) => (list 1 2 3 (q! 99))
   )


(fact "code-later clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-later)) =>
  #"All items taken from :code are returned"
  )



(tabular
  (fact ":foo-flipstack reverses the whole :foo stack, pushing it to :exec"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack    ?expected
    :foo       '(1 2 3 4 5)    :foo-flipstack   :exec         '((5 4 3 2 1))
    :foo       '(21)           :foo-flipstack   :exec         '((21))
    ;; missing args
    :foo       '()             :foo-flipstack   :exec         '(())
    )


(fact "flipstack-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :code '(1 2 3 4)}) :code-flipstack)
    :exec) => (list (list
                (q! 4) (q! 3) (q! 2) (q! 1))
                ))

(fact "code-flipstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-flipstack)) =>
  #"All items taken from :code are returned"
  )


(fact "flipstack-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-flipstack)
    :exec) => '((:push-quoterefs (:d :c :b :a) :push-unquoterefs))
    )


(fact "ref-flipstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-flipstack)) =>
  #"The returned block of items is wrapped in"
  )



(tabular
  (fact "`foo-cutstack` takes an :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(2)}             :foo-cutstack     {:foo    '()
                                                  :exec   '(((8 9) (4 5 6 7)))
                                                  :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2.77)}         :foo-cutstack     {:foo    '()
                                                  :exec   '(((6 7 8 9) (4 5)))
                                                  :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(0)}             :foo-cutstack     {:foo    '()
                                                  :exec   '((() (4 5 6 7 8 9)))
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(11)}            :foo-cutstack     {:foo   '()
                                                  :exec   '(((5 6 7 8 9) (4)))
                                                  :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-97/9)}         :foo-cutstack     {:foo   '()
                                                  :exec   '(((8 9) (4 5 6 7)))
                                                  :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}            :foo-cutstack     {:foo   '()
                                                  :exec   '((() ()))
                                                  :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :scalar  '()}              :foo-cutstack     {:foo   '(2 3 4)
                                                  :exec   '()
                                                  :scalar '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(fact "cutstack-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :code '(1 2 3 4 5 6)}) :code-cutstack)
    :exec) => (list (list
                (list (q! 3) (q! 2) (q! 1))
                (list (q! 6) (q! 5) (q! 4))
                )))


(fact "code-cutstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-cutstack)) =>
  #"All items taken from :code are returned"
  )


(fact "cutstack-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-cutstack)
    :exec) => '((:push-quoterefs ((:c :b :a) (:d)) :push-unquoterefs))
    )


(fact "ref-cutstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-cutstack)) =>
  #"The returned block of items is wrapped in"
  )



(tabular
  (fact "`foo-cutflip` takes an :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(3)}            :foo-cutflip    {:foo     '()
                                                :exec    '(((4 5 6) (9 8 7)))
                                                :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2)}           :foo-cutflip    {:foo     '()
                                                :exec    '(((4 5) (9 8 7 6)))
                                                :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(0)}            :foo-cutflip     {:foo     '()
                                                 :exec    '(((4 5 6 7 8 9) ()))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(9/2)}         :foo-cutflip      {:foo     '()
                                                 :exec    '(((4) (9 8 7 6 5)))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(111.2)}        :foo-cutflip     {:foo     '()
                                                 :exec    '(((4 5) (9 8 7 6)))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(11)}           :foo-cutflip     {:foo     '()
                                                 :exec    '(((4) (9 8 7 6 5)))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-1.0e191M)}    :foo-cutflip     {:foo     '()
                                                 :exec    '(((4 5 6 7) (9 8)))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}           :foo-cutflip     {:foo     '()
                                                 :exec    '((() ()))
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo     '(2 3 4)
     :scalar  '()}             :foo-cutflip     {:foo     '(2 3 4)
                                                 :exec    '()
                                                 :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(fact "cutflip-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :code '(1 2 3 4 5 6)}) :code-cutflip)
    :exec) => (list (list
                (list (q! 6) (q! 5) (q! 4))
                (list (q! 1) (q! 2) (q! 3))
                )))


(fact "code-cutflip clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-cutflip)) =>
  #"All items taken from :code are returned"
  )


(fact "cutflip-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-cutflip)
    :exec) => '((:push-quoterefs ((:d) (:a :b :c)) :push-unquoterefs))
    )


(fact "ref-cutflip clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-cutflip)) =>
  #"The returned block of items is wrapped in"
  )



(tabular
  (fact "`foo-liftstack` takes a :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom; discards if there's an error!"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(3)}           :foo-liftstack   {:foo  '()
                                                :exec '(((4 5 6 7 8 9) (4 5 6)))
                                                :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2)}          :foo-liftstack   {:foo  '()
                                                :exec '(((4 5 6 7 8 9) (4 5)))
                                                :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo     '(9 8 7 6 5 4)
     :scalar  '(0)}           :foo-liftstack   {:foo  '()
                                                :exec '(((4 5 6 7 8 9)
                                                  (4 5 6 7 8 9)))
                                                :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo     '(9 8 7 6 5 4)
     :scalar  '(5)}           :foo-liftstack    {:foo    '()
                                                :exec '(((4 5 6 7 8 9) (4)))
                                                :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo     '(9 8 7 6 5 4)
     :scalar  '(11)}          :foo-liftstack    {:foo '()
                                                 :exec '(((4 5 6 7 8 9) (4)))
                                                 :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo     '(9 8 7 6 5 4)
     :scalar  '(1e181)}       :foo-liftstack    {:foo  '()
                                               :exec '(((4 5 6 7 8 9) (4 5 6 7 8 9)))
                                               :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}          :foo-liftstack   {:foo    '()
                                                :exec '((() ()))
                                               :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :scalar  '()}            :foo-liftstack    {:foo    '(2 3 4)
                                                 :exec '()
                                                 :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )

(tabular
  (fact ":foo-liftstack produces an error when the result is oversized"
    (check-instruction-here-using-this
      (i/register-type teeny foo-type)
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction        ?expected
    {:foo    '(1 2 3 4 5)
     :scalar '(1)}       :foo-liftstack      {:foo    '()
                                              :scalar '()
                                              :exec '()
                                              :error  '({:item ":foo-liftstack tried to push an oversized item to :exec" :step 0})}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo    '(1 2 3 4)
     :scalar '(3)}       :foo-liftstack      {:foo    '()
                                              :scalar '()
                                              :exec   '(((4 3 2 1) (4)))
                                              :error  '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo    '(1 2 3 4)
     :scalar '(2)}       :foo-liftstack      {:foo    '()
                                              :scalar '()
                                              :exec   '(((4 3 2 1) (4 3)))
                                              :error  '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

     )


(fact "liftstack-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(2) :code '(1 2 3 4)}) :code-liftstack)
    :exec) => (list (list
                (list (q! 4) (q! 3) (q! 2) (q! 1))
                (list (q! 4) (q! 3))
                )))


(fact "code-liftstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-liftstack)) =>
  #"All items taken from :code are returned"
  )


(fact "liftstack-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-liftstack)
    :exec) => '((:push-quoterefs ((:d :c :b :a) (:d)) :push-unquoterefs))
    )


(fact "ref-liftstack clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-liftstack)) =>
  #"The returned block of items is wrapped in"
  )
