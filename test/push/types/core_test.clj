(ns push.types.core-test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:require [push.instructions.core :as instr])
  (:require [push.instructions.dsl :as d])
  (:use [push.types.core])
  )


;;;; type information


;; PushType records


(fact "`make-type` takes a keyword and recognizer"
  (make-type :integer :recognizer integer?) =>
    {:name :integer, :recognizer integer?, :attributes #{}, :instructions {}})


(fact "`make-type` defaults the :recognizer to #(false)"
  ((:recognizer (make-type :foo)) 99) => false)


(fact "`make-type` takes an optional :attributes set"
  (:attributes (make-type 
                  :integer 
                  :recognizer integer? 
                  :attributes #{:comparable :numeric})) => #{:comparable :numeric})


(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :exec 
                                    :float 
                                    :integer 
                                    :string] :in-any-order))


;;;; Modules (like types, just not about items)


;; modules


(fact "`make-module` creates a simple map with :attributes and :instructions fields"
  (:name (make-module :foo)) => :foo
  (:attributes (make-module :foo)) => #{}
  (:instructions (make-module :foo)) => {})


(fact "modules can have whole attributes assigned as with PushTypes"
  (keys (:instructions (make-visible (make-module :foo)))) => '(:foo-stackdepth :foo-empty?)
  (:attributes (make-visible (make-module :foo))) => #{:visible})


;; a fixture


(def foo-barbaz
  (instr/build-instruction
    foo-barbaz
    :tags #{:foo :double-ba*}
    (d/consume-top-of :foo :as :arg1)))


(fact "modules can have individual instructions assigned"
  (keys (:instructions (attach-instruction (make-module :foo) foo-barbaz))) =>
    '(:foo-barbaz))


;; generic functions

(fact "`predicate-docstring` produces the appropriate string when asked"
  (predicate-docstring :foo-pos? "pos?" :foo) => 
  "`:foo-pos?` pushes true to the `:boolean` stack if the predicate `pos?` returns true when applied to the top `:foo` item, false otherwise.")


(fact "`simple-1-in-predicate` has the right docstring"
  (:docstring (push.types.core/simple-1-in-predicate :foo "pos?" pos?)) => 
    "`:foo-pos?` pushes true to the `:boolean` stack if the predicate `pos?` returns true when applied to the top `:foo` item, false otherwise."
  (:docstring (push.types.core/simple-1-in-predicate :baz "qux?" #(+ % 7))) => 
    "`:baz-qux?` pushes true to the `:boolean` stack if the predicate `qux?` returns true when applied to the top `:baz` item, false otherwise."


  )

