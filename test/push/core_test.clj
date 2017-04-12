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
    (contains [:scalar-add :boolean-or :code-dup :exec-flipstack]
              :in-any-order :gaps-ok))


(fact "I can produce a list of input names (keywords) from an interpreter"
  (p/binding-names (p/interpreter :bindings {:a 2 :b 7})) => [:a :b] )


(fact "I can produce a list of types and modules loaded into the interpreter"
  (p/types-and-modules (p/interpreter)) => (contains [:introspection :print :snapshot :code :error :log :exec :set :vector :strings :chars :booleans :string :char :boolean :scalar :scalars] :in-any-order :gaps-ok))


(fact "I can produce the router list for an interpreter"
  (p/routing-list (p/interpreter)) =>
    (contains [:ref :refs :boolean :char :complex :complexes :snapshot :generator :quoted :string :booleans :chars :strings :tagspace :scalars :vector :set :scalar] :in-any-order :gaps-ok))




(fact "I can run a Push program and get a named stack"
  (p/get-stack
    (p/run (p/interpreter) [88 99 :scalar-add] 100) 
    :scalar) => '(187))




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


(fact "I can turn off individual instructions with the forget-instructions function"
  (keys
    (:instructions
      (p/forget-instructions (p/interpreter) [:scalar-add]))) =not=>
        (contains :scalar-add)
  (keys (:instructions (p/interpreter))) => (contains :scalar-add))



(fact "I can turn off multiple instructions with the forget-instructions function"
  (count
    (keys
      (:instructions
        (p/forget-instructions (p/interpreter) [:scalar-add :scalar-subtract])))) =>
     (- (count
      (keys
        (:instructions (p/interpreter)))) 2))



(fact "I can turn off unknown instructions with no trouble"
  (keys
    (:instructions
      (p/forget-instructions (p/interpreter) [:foo-bar :baz-quux]))) =>
  (keys
    (:instructions (p/interpreter))))


;; a fixture

(def foo-pop
  (push.instructions.core/build-instruction
    foo-pop
    (push.instructions.dsl/consume-top-of :foo :as :gone)))



(fact "about :foo-pop"
  (:token foo-pop) => :foo-pop
  (:needs foo-pop) => {:foo 1})



(fact "I can add a new instruction with register-instructions"
  (keys
    (:instructions
      (p/register-instructions (p/interpreter) [foo-pop]))) => (contains :foo-pop))



(fact "I can overwrite an old instruction with register-instructions"
  (:docstring
    (:scalar-add
      (:instructions
        (p/register-instructions
          (p/interpreter)
          [(assoc foo-pop :token :scalar-add)])))) => (:docstring foo-pop))
