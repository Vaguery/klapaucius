(ns push.type.core-test
  (:require [push.interpreter.core :as i]
            [push.instructions.core :as instr]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.router.core :as router])
  (:use midje.sweet)
  (:use [push.type.core])
  )


;;;; type information


;; PushType records


(fact "`make-type` takes a type name at a minimum"
  (let [i (make-type :numbery-thing)]
    (:name i) => :numbery-thing
    (:attributes i) => #{}
    (:instructions i) => {}
    (router/router-recognize? (:router i) 99) => false  ;; NOTE! default recognizer
    ))


(fact "`make-type` can be called with a type name and a :recognizer"
  (let [i (make-type :numbery-thing :recognized-by integer?)]
    (:name i) => :numbery-thing
    (:attributes i) => #{}
    (:instructions i) => {}
    (router/router-recognize? (:router i) 99) => true
    (router/router-recognize? (:router i) 9.9) => false
    ))




(fact "`make-type` takes an optional :attributes set"
  (:attributes (make-type
                  :numbery-thing
                  :recognized-by integer?
                  :attributes #{:comparable :numeric})) => #{:comparable :numeric})


(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :exec
                                    :scalar
                                    :string] :in-any-order))



;;;; Modules (like types, just not about items)


;; modules


(fact "`make-module` creates a simple map with :attributes and :instructions fields"
  (:name (make-module :foo)) => :foo
  (:attributes (make-module :foo)) => #{}
  (:instructions (make-module :foo)) => {})


(fact "modules can have whole attributes assigned as with PushTypes"
  (keys (:instructions (aspects/make-visible (make-module :foo)))) => '(:foo-stackdepth :foo-empty?)
  (:attributes (aspects/make-visible (make-module :foo))) => #{:visible})


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


(fact "`simple-1-in-predicate` requires a docstring"
  (:docstring (instr/simple-1-in-predicate "foo bar baz" :foo "pos?" pos?)) =>
    "foo bar baz"
  )
