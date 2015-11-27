(ns push.instructions.extra.stack-combinators-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:require [push.util.code-wrangling :as u])
  (:use push.instructions.aspects.movable)
  (:use push.instructions.extra.stack-combinators)
  )


;;;;;;;;;;;;;

(def foo-type (t/make-type  :foo
                              :recognizer number?
                              :attributes #{:foo}))


(fact "extend-combinators will not add these instructions unless the type is already :movable"
  (extend-combinators foo-type) => foo-type
  (keys (:instructions (extend-combinators foo-type))) =not=>
    (contains :foo-flipstack))


(def foo-type (make-movable (t/make-type  :foo
                              :recognizer number?
                              :attributes #{:foo})))


(fact "extend-combinators will add these instructions if the type is already :movable"
  (extend-combinators foo-type) =not=> foo-type
  (keys (:instructions (extend-combinators foo-type))) =>
    (contains :foo-flipstack))


(def foo-type (-> (t/make-type  :foo
                                :recognizer number?
                                :attributes #{:foo})
                   make-movable
                   extend-combinators))


;;;;;;;;;;;;;;;

(tabular
  (fact ":foo-flipstack reverses the whole :foo stack"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack         ?expected
    :foo       '(1 2 3 4 5)    :foo-flipstack   :foo              '(5 4 3 2 1)
    :foo       '(21)           :foo-flipstack   :foo              '(21)
    ;; missing args
    :foo       '()             :foo-flipstack   :foo              '())


(tabular
  (fact "`foo-cutstack` takes an :integer, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :integer  '(2)}             :foo-cutstack      {:foo      '(7 6 5 4 9 8)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(-2)}            :foo-cutstack      {:foo      '(5 4 9 8 7 6)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(0)}             :foo-cutstack      {:foo      '(9 8 7 6 5 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(11)}            :foo-cutstack      {:foo      '(4 9 8 7 6 5)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :integer  '(11)}            :foo-cutstack      {:foo      '()
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :integer  '()}              :foo-cutstack      {:foo      '(2 3 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foo-cutflip` takes an :integer, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :integer  '(3)}          :foo-cutflip          {:foo      '(7 8 9 6 5 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(-2)}         :foo-cutflip          {:foo      '(6 7 8 9 5 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(0)}          :foo-cutflip          {:foo      '(9 8 7 6 5 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(11)}         :foo-cutflip          {:foo      '(5 6 7 8 9 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :integer  '(11)}         :foo-cutflip          {:foo      '()
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :integer  '()}           :foo-cutflip          {:foo      '(2 3 4)
                                                     :integer  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(def huge-list (repeat 131070 1))


(tabular
  (fact "`foo-liftstack` takes an :integer, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :integer  '(2)}             :foo-liftstack      {:foo    '(7 6 5 4 9 8 7 6 5 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(-2)}            :foo-liftstack      {:foo    '(5 4 9 8 7 6 5 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(0)}             :foo-liftstack      {:foo  '(9 8 7 6 5 4 9 8 7 6 5 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(5)}             :foo-liftstack      {:foo    '(4 9 8 7 6 5 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :integer  '(11)}            :foo-liftstack      {:foo    '(4 9 8 7 6 5 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :integer  '(11)}            :foo-liftstack      {:foo    '()
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :integer  '()}              :foo-liftstack      {:foo    '(2 3 4)
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; oversized does nothing
    {:foo      huge-list
     :integer  '(12000)}         :foo-liftstack      {:foo    huge-list
                                                     :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
