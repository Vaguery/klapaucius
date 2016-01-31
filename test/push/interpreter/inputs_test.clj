(ns push.interpreter.inputs-test
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u])
  (:use [push.interpreter.core])
  (:require [push.interpreter.templates.minimum :as m])
  )


(fact "a bare naked Interpreter has an empty :bindings hashmap"
    (:bindings (m/basic-interpreter)) => {})


(fact "`register-input` saves a key-value pair in the :bindings field"
  (:bindings (register-input (m/basic-interpreter) :foo 99)) => {:foo 99})


(fact "the `(bound-keyword? interpreter item)` recognizer returns true for registered inputs"
  (bound-keyword? (m/basic-interpreter) :kung) => false
  (bound-keyword? (register-input (m/basic-interpreter) :kung 77) :kung) => true)


(fact "an Interpreter will recognize a registered input keyword in a
  running program and replace it with the stored value"
  (let [neo (register-input (m/basic-interpreter) :kung "foo")]
    (u/get-stack (handle-item neo :kung) :exec) => '("foo")))


(fact "`register-input` saves a new key-value pair with a generated key if none is given"
  (:bindings (register-input (m/basic-interpreter) 9912)) =>
    {:input!1 9912})


;; register-inputs


(fact "`register-inputs` can take a hashmap and will register all the items as :input pairs"
  (:bindings (register-inputs (m/basic-interpreter) {:a 1 :b 2 :c 3}))
    => {:a 1, :b 2, :c 3})


(fact "`register-inputs` can take a vector of items and will register
  each of them under generated `input!` keys"
  (:bindings (register-inputs (m/basic-interpreter) [1 2 false 99]))
    => {:input!1 1, :input!2 2, :input!3 false, :input!4 99})