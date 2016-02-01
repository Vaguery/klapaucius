(ns push.interpreter.inputs-test
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u])
  (:use [push.interpreter.core])
  (:require [push.interpreter.templates.minimum :as m])
  )


(fact "a bare naked Interpreter has an empty :bindings hashmap"
    (:bindings (m/basic-interpreter)) => {})


(fact "`bind-input` saves a key-value pair in the :bindings field, using a stack"
  (:bindings (bind-input (m/basic-interpreter) :foo 99)) => {:foo '(99)})


(fact "the `(bound-keyword? interpreter item)` recognizer returns true for registered inputs"
  (bound-keyword? (m/basic-interpreter) :kung) => false
  (bound-keyword? (bind-input (m/basic-interpreter) :kung 77) :kung) => true)


(fact "an Interpreter will recognize a registered input keyword in a
  running program and replace it with the stored value"
  (let [neo (bind-input (m/basic-interpreter) :kung "foo")]
    (u/get-stack (handle-item neo :kung) :exec) => '("foo")))


(fact "`bind-input` saves a new key-value pair with a generated key if none is given"
  (:bindings (bind-input (m/basic-interpreter) 9912)) =>
    {:input!1 '(9912)})



(fact "bind-input pushes multiple items if called repeatedly"
  (let [knows-foo (assoc-in (m/basic-interpreter) [:bindings :foo] '(1 2 3))]
    (:bindings (bind-input knows-foo :foo 99)) => {:foo '(99 1 2 3)}

    (:bindings
      (bind-input 
        (bind-input knows-foo :foo 99)
        :foo 88)) =>  {:foo '(88 99 1 2 3)}))


;; bind-value


(fact "bind-value pushes the item onto the indicated stack, if it exists"
  (let [knows-foo (assoc-in (m/basic-interpreter) [:bindings :foo] '(1 2 3))]
    (:bindings (bind-value knows-foo :foo 99)) => {:foo '(99 1 2 3)}))




(fact "bind-value pushes the item onto a new stack, if it doesn't already exist"
  (let [knows-foo (assoc-in (m/basic-interpreter) [:bindings :foo] '(1 2 3))]
    (:bindings (bind-value knows-foo :bar 99)) => {:bar '(99), :foo '(1 2 3)}))


;; peek-at-binding


(fact "peek-at-binding returns the top item on the indicated :bindings item (by key)" 
  (let [knows-foo (assoc-in (m/basic-interpreter) [:bindings :foo] '(1 2 3))]
    (peek-at-binding knows-foo :foo) => 1 
    (peek-at-binding knows-foo :bar) => nil
    (peek-at-binding (assoc-in (m/basic-interpreter) [:bindings :foo] '()) :foo) => nil))


;; bind-inputs


(fact "`bind-inputs` can take a hashmap and will register all the items as :input pairs"
  (:bindings (bind-inputs (m/basic-interpreter) {:a 1 :b 2 :c 3}))
    => {:a '(1), :b '(2), :c '(3)})


(fact "`bind-inputs` can take a vector of items and will register
  each of them under generated `input!` keys"
  (:bindings (bind-inputs (m/basic-interpreter) [1 2 false 99]))
    => {:input!1 '(1), :input!2 '(2), :input!3 '(false), :input!4 '(99)})

