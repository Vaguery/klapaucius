(ns push.interpreter.inputs-test
  (:use midje.sweet)
  (:use [push.interpreter.core]))


(fact "a bare naked Interpreter has an empty :inputs hashmap"
    (:inputs (make-interpreter)) => {})


(fact "`register-input` saves a key-value pair in the :inputs field"
  (:inputs (register-input (make-interpreter) :foo 99)) => {:foo 99})


(fact "the `(input? interpreter item)` recognizer returns true for registered inputs"
  (input? (make-interpreter) :kung) => false
  (input? (register-input (make-interpreter) :kung 77) :kung) => true)


(fact "an Interpreter will recognize a registered input keyword in a
  running program and replace it with the stored value"
  (let [neo (register-input (make-interpreter) :kung "foo")]
    (get-stack (handle-item neo :kung) :exec) => '("foo")))


(fact "`register-input` saves a new key-value pair with a generated key if none is given"
  (:inputs (register-input (make-interpreter) 9912)) =>
    {:input!1 9912})


;; register-inputs


(fact "`register-inputs` can take a hashmap and will register all the items as :input pairs"
  (:inputs (register-inputs (make-interpreter) {:a 1 :b 2 :c 3}))
    => {:a 1, :b 2, :c 3})


(fact "`register-inputs` can take a vector of items and will register
  each of them under generated `input!` keys"
  (:inputs (register-inputs (make-interpreter) [1 2 false 99]))
    => {:input!1 1, :input!2 2, :input!3 false, :input!4 99})