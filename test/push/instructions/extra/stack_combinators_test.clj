(ns push.instructions.extra.stack-combinators-test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.type.core :as t])
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

;;;;;;;;;;;;;;;


(tabular
  (fact ":foo-againlater copies the top item from :foo to the tail of :exec"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack         ?expected
    :foo       '(1 2 3 4 5)    :foo-againlater   :foo              '(1 2 3 4 5)
    :foo       '(1 2 3 4 5)    :foo-againlater   :exec             '(1)
    ;; missing args
    :foo       '()             :foo-againlater   :exec              '())



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
  (fact ":foo-flipstack reverses the whole :foo stack"
    (register-type-and-check-instruction
        ?set-stack ?items foo-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack         ?expected
    :foo       '(1 2 3 4 5)    :foo-flipstack   :foo              '(5 4 3 2 1)
    :foo       '(21)           :foo-flipstack   :foo              '(21)
    ;; missing args
    :foo       '()             :foo-flipstack   :foo              '())


(tabular
  (fact "`foo-cutstack` takes an :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(2)}             :foo-cutstack      {:foo      '(7 6 5 4 9 8)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2.77)}         :foo-cutstack      {:foo      '(5 4 9 8 7 6)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(0)}             :foo-cutstack      {:foo      '(9 8 7 6 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(11)}            :foo-cutstack      {:foo      '(4 9 8 7 6 5)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-97/9)}         :foo-cutstack      {:foo      '(7 6 5 4 9 8)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}            :foo-cutstack      {:foo      '()
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :scalar  '()}              :foo-cutstack      {:foo      '(2 3 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foo-cutflip` takes an :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(3)}            :foo-cutflip          {:foo      '(7 8 9 6 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2)}           :foo-cutflip          {:foo      '(6 7 8 9 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(0)}            :foo-cutflip          {:foo      '(9 8 7 6 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(9/2)}         :foo-cutflip          {:foo      '(5 6 7 8 9 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(111.2)}        :foo-cutflip          {:foo      '(6 7 8 9 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(11)}           :foo-cutflip          {:foo      '(5 6 7 8 9 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-1.0e191M)}    :foo-cutflip          {:foo      '(8 9 7 6 5 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}           :foo-cutflip          {:foo      '()
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :scalar  '()}             :foo-cutflip          {:foo      '(2 3 4)
                                                     :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(def huge-list (repeat 131070 1))


(tabular
  (fact "`foo-liftstack` takes a :scalar, divides the stack into two parts at that index (measured from the top), and puts the top segment at the bottom"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:foo      '(9 8 7 6 5 4)
     :scalar  '(3)}             :foo-liftstack      {:foo    '(6 5 4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(-2)}            :foo-liftstack      {:foo    '(5 4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(0)}             :foo-liftstack      {:foo  '(9 8 7 6 5 4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(5)}             :foo-liftstack      {:foo    '(4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(11)}            :foo-liftstack      {:foo    '(4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(9 8 7 6 5 4)
     :scalar  '(1e181)}            :foo-liftstack      {:foo    '(9 8 7 6 5 4 9 8 7 6 5 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '()
     :scalar  '(11)}            :foo-liftstack      {:foo    '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo      '(2 3 4)
     :scalar  '()}              :foo-liftstack      {:foo    '(2 3 4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; oversized produces an :error
    {:foo      huge-list
     :scalar  '(12000)}         :foo-liftstack      {:foo    huge-list
                                                     :scalar '()
                                                     :error '({:item "foo-liftstack produced stack overflow", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
