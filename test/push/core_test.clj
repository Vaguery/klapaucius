(ns push.core-test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:require [push.core :as p]))


(fact "I can produce a generic interpreter"
  (class (p/interpreter)) => push.interpreter.core.Interpreter )


(fact "I can set input values"
  (keys (:inputs (p/interpreter :inputs [7 2 3]))) => '(:input!1 :input!2 :input!3)
  (:inputs (p/interpreter :inputs {:a 8 :b false})) =>  {:a 8, :b false})


(fact "I can set a program"
  (:program (p/interpreter :program [1 2 3])) => [1 2 3])


(fact "I can produce a list of instructions from an interpreter"
  (p/known-instructions (p/interpreter)) =>
    (contains [:integer-add :boolean-or :code-dup :exec-y]
              :in-any-order :gaps-ok))


(fact "I can produce a list of input names (keywords) from an interpreter"
  (p/input-names (p/interpreter :inputs {:a 2 :b 7})) => [:a :b] )


(fact "I can produce a list of types and modules loaded into the interpreter"
  (p/types-and-modules (p/interpreter)) => (contains [:numeric-scaling :introspection :print :environment :code :error :log :exec :point :line :circle :set :vector :strings :integers :floats :chars :booleans :string :float :char :boolean :integer] :in-any-order :gaps-ok))


(fact "I can produce the router list for an interpreter"
  (p/routing-list (p/interpreter)) =>
    '(:integer :boolean :char :float :string :booleans :chars :floats :integers :strings :vector :set :circle :line :point))


(fact "I can run a Push program and get a named stack"
  (p/get-stack (p/run [1 2 :integer-add] 100) :integer) => '(3))
