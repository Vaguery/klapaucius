(ns push.interpreter.interpreter-running-test
  (:use midje.sweet)
  (:require [push.instructions.instructions-core :as i])
  (:use [push.instructions.dsl])
  (:use [push.interpreter.interpreter-core])
  (:require [push.types.core :as types]))


;; handle-item


(fact "handle-item throws an error if the item isn't recognized"
  ;; yes this is trying to process another interpreter, which is (for now) not permitted
  (handle-item (make-interpreter) (make-interpreter)) => 
    (throws Exception #"Push Parsing Error:"))


(fact "handle-item sends integers to :integer"
  (get-stack (handle-item (make-interpreter) 8) :integer) => '(8)
  (get-stack (handle-item (make-interpreter) -8) :integer) => '(-8)
  (get-stack (handle-item (make-interpreter :stacks {:integer '(1)}) -8) :integer) =>
    '(-8 1))


; (fact "handle-item handles integer overflow")


(fact "handle-item sends floats to :float"
  (get-stack (handle-item (make-interpreter) 8.0) :float) => '(8.0)
  (get-stack (handle-item (make-interpreter) -8.0) :float) => '(-8.0)
  (get-stack (handle-item (make-interpreter :stacks {:float '(1.0)}) -8.0) :float) =>
    '(-8.0 1.0))


; (fact "handle-item handles float overflow")
; (fact "handle-item handles float underflow")


(fact "handle-item sends booleans to :boolean"
  (get-stack (handle-item (make-interpreter) false) :boolean) => '(false)
  (get-stack (handle-item (make-interpreter) true) :boolean) => '(true)
  (get-stack (handle-item (make-interpreter :stacks {:boolean '(false)}) true) :boolean) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (get-stack (handle-item (make-interpreter) \J) :char) => '(\J)
  (get-stack (handle-item (make-interpreter) \o) :char) => '(\o)
  (get-stack (handle-item (make-interpreter :stacks {:char '(\Y)}) \e) :char) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (get-stack (handle-item (make-interpreter) "foo") :string) => '("foo")
  (get-stack (handle-item (make-interpreter) "") :string) => '("")
  (get-stack (handle-item (make-interpreter :stacks {:string '("bar")}) "baz") :string) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' quoted lists onto :exec"
  (get-stack (handle-item (make-interpreter) '(1 2 3)) :exec) => '(1 2 3)
  (get-stack (handle-item (make-interpreter) '(1 (2) (3))) :exec) => '(1 (2) (3))
  (get-stack (handle-item (make-interpreter) '(1 () ())) :exec) => '(1 () ())
  (get-stack (handle-item (make-interpreter) '()) :exec) => '())


(fact "handle-item will execute a registered instruction"
 (let [foo (i/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (handle-item he-knows-foo :foo) => 761 ;; an intentionally surprising result
   ))


(fact "handle-item will not execute an unregistered instruction"
 (let [foo (i/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (handle-item he-knows-foo :bar) => (throws #"Push Parsing Error:")))


;; some fixtures:


(def intProductToFloat
  (i/build-instruction intProductToFloat
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(float (* %1 %2)) :as :p)
    (push-onto :float :p)))


(def knows-some-things
  (register-instruction
    (make-interpreter 
      :program [1.1 2.2 :intProductToFloat]
      :counter 22
      :stacks {:integer '(1 2 3)
               :exec '(:intProductToFloat)})
    intProductToFloat))


;; clear-all-stacks


(fact "`clear-all-stacks` empties every stack"
  (:stacks (clear-all-stacks knows-some-things)) => core-stacks)


;; reset-interpreter


(fact "calling `reset-interpreter` loads the program onto :exec"
  (get-stack knows-some-things :exec) => '(:intProductToFloat)
  (get-stack (reset-interpreter knows-some-things) :exec) =>
    '(1.1 2.2 :intProductToFloat))


(fact "calling `reset-interpreter` clears the other stacks"
  (get-stack knows-some-things :integer) => '(1 2 3)
  (:stacks (reset-interpreter knows-some-things)) => 
    (merge core-stacks {:exec '(1.1 2.2 :intProductToFloat)}))


(fact "`reset-interpreter` sets the counter to 0"
  (let [counted (assoc knows-some-things :counter 9912)]
    (:counter counted) => 9912
    (:counter (reset-interpreter counted)) => 0))


;; increment-counter


(fact "`increment-counter` increments the counter"
  (:counter knows-some-things) => 22
  (:counter (increment-counter knows-some-things)) => 23)


;; is-done?


(fact "`is-done?` checks the Interpreter for various halting states"
  (is-done? (make-interpreter)) => true
  (is-done? knows-some-things) => false)


;; step


(fact "calling `step` consumes one item from the :exec stack (if any)"
  (get-stack knows-some-things :exec) => '(:intProductToFloat)
  (get-stack (step knows-some-things) :exec) => '())


(fact "calling `step` increments the counter if something happens"
  (:counter knows-some-things) => 22
  (:counter (step knows-some-things)) => 23)


(fact "calling `step` doesn't affect the counter if :exec is empty"
  (:counter (make-interpreter)) => 0
  (:counter (step (make-interpreter))) => 0
  (:counter (step (clear-stack knows-some-things :exec))) => 22)


(fact "calling `step` sets the :done? flag if a halting condition is encountered"
  (is-done? knows-some-things) => false
  (:done? knows-some-things) => false
  (:done? (step knows-some-things)) => true)
