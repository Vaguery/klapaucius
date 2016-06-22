(ns push.core-test
  (:require [push.interpreter.core :as i]
            [push.core :as p])
  (:use midje.sweet))


(fact "I can produce a generic interpreter"
  (class (p/interpreter)) => push.interpreter.definitions.Interpreter )


(fact "I can set input values"
  (keys (:bindings (p/interpreter :bindings [7 2 3]))) => '(:input!1 :input!2 :input!3)
  (:bindings (p/interpreter :bindings {:a 8 :b false})) =>  {:a '(8), :b '(false)})


(fact "I can set a program"
  (:program (p/interpreter :program [1 2 3])) => [1 2 3])


(fact "when I invoke push.core/interpreter with a :stacks argument, the indicated items are present on the result"
  (:stacks (p/interpreter :stacks {:foo '(1 2 3)})) =>
    (contains {:foo '(1 2 3)}))


(fact "I can use merge-stacks to merge new values onto an interpreter"
  (:stacks (p/merge-stacks (p/interpreter) {:foo '(1 2 3)})) => (contains {:foo '(1 2 3)}))


(fact "I can produce a list of instructions from an interpreter"
  (p/known-instructions (p/interpreter)) =>
    (contains [:scalar-add :boolean-or :code-dup :exec-y]
              :in-any-order :gaps-ok))


(fact "I can produce a list of input names (keywords) from an interpreter"
  (p/binding-names (p/interpreter :bindings {:a 2 :b 7})) => [:a :b] )


(fact "I can produce a list of types and modules loaded into the interpreter"
  (p/types-and-modules (p/interpreter)) => (contains [:introspection :print :environment :code :error :log :exec :set :vector :strings :chars :booleans :string :char :boolean :scalar :scalars] :in-any-order :gaps-ok))


(fact "I can produce the router list for an interpreter"
  (p/routing-list (p/interpreter)) =>
    (contains [:ref :refs :boolean :char :complex :complexes :generator :quoted :string :booleans :chars :strings :tagspace :scalars :vector :set :scalar] :in-any-order))





(fact "I can run a Push program and get a named stack"
  (p/get-stack (p/run (p/interpreter) [88 99 :scalar-add] 100) :scalar) => '(187))




(fact "I can re-run an interpreter with bound inputs, replacing the input values"
  (let [a-99 (p/interpreter :bindings {:a 99})]
    (p/get-stack
        (p/run
          a-99
          [:a 99 :scalar-add]
          100) :scalar) => '(198)
    (p/get-stack
        (p/run
          a-99
          [:a 99 :scalar-add]
          100
          :bindings {:a -99}) :scalar) => '(0)
    (p/get-stack
        (p/run
          a-99
          [:a 99 :scalar-add :b]
          100
          :bindings {:b -99}) :scalar) => '(-99 198)))


(future-fact "I can turn off individual instructions with the :disable key in the :config hash"
  ;; but I don't know how just yet
  )

