(ns push.interpreter.interpreter-running-test
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.types.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:use [push.interpreter.core])
  )


;;; the router and handle-item


;; unknown items


(fact "`handle-item` pushes an item to the :unknown stack if unrecognized when :config :lenient? is true"
  (let [junk :this-is-an-unknown-item]
    (u/get-stack
      (handle-item 
        (make-classic-interpreter :config {:lenient? true})
        junk)
      :unknown) => '(:this-is-an-unknown-item)))


(fact "`handle-item` pushes an item to the :unknown stack if 
  unrecognized when :config :lenient? is not true (or unset)"
  (handle-item (make-classic-interpreter) (basic-interpreter)) => 
    (throws #"Push Parsing Error: Cannot interpret '"))


;; the router order is: :input? :instruction? [router] :unknown


(fact "`router-sees?` checks the router predicates and returns true if one matches"
  (let [abbr #'push.interpreter.core/router-sees?]
    (abbr (make-classic-interpreter) :not-likely) => nil
    (abbr (basic-interpreter 
      :router [[(fn [item] (= item :not-likely)) :integer]]) :not-likely) => true))


(fact "`handle-item` checks the :router list"
  (let [he-knows-foo (basic-interpreter :router [[integer? :code]])]
        ;; this will route integers to the :code stack, and not to :integer
    (:stacks (handle-item he-knows-foo 99) :code) => (contains {:code '(99)})))


(def foo-type 
  (-> (types/make-type :foo :recognizer integer?)
      types/make-visible
      types/make-comparable
      types/make-equatable
      types/make-movable))


(fact "types added to the router with `register-type` are used by `handle-item`"
  (:router (register-type (basic-interpreter) foo-type)) =>
    [ [(:recognizer foo-type) :foo] ]
  (u/get-stack (handle-item (register-type (basic-interpreter) foo-type) 99) :integer) => '()
  (u/get-stack (handle-item (register-type (basic-interpreter) foo-type) 99) :foo) => '(99))


(fact "handle-item sends integers to :integer"
  (u/get-stack (handle-item (make-classic-interpreter) 8) :integer) => '(8)
  (u/get-stack (handle-item (make-classic-interpreter) -8) :integer) => '(-8)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:integer '(1)}) -8) :integer) =>
    '(-8 1))


; (fact "handle-item handles integer overflow")


(fact "handle-item sends floats to :float"
  (u/get-stack (handle-item (make-classic-interpreter) 8.0) :float) => '(8.0)
  (u/get-stack (handle-item (make-classic-interpreter) -8.0) :float) => '(-8.0)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:float '(1.0)}) -8.0) :float) =>
    '(-8.0 1.0))


; (fact "handle-item handles float overflow")
; (fact "handle-item handles float underflow")


(fact "handle-item sends booleans to :boolean"
  (u/get-stack (handle-item (make-classic-interpreter) false) :boolean) => '(false)
  (u/get-stack (handle-item (make-classic-interpreter) true) :boolean) => '(true)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:boolean '(false)}) true) :boolean) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (u/get-stack (handle-item (make-classic-interpreter) \J) :char) => '(\J)
  (u/get-stack (handle-item (make-classic-interpreter) \o) :char) => '(\o)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:char '(\Y)}) \e) :char) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (u/get-stack (handle-item (make-classic-interpreter) "foo") :string) => '("foo")
  (u/get-stack (handle-item (make-classic-interpreter) "") :string) => '("")
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:string '("bar")}) "baz") :string) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' lists onto :exec"
  (u/get-stack (handle-item (make-classic-interpreter) '(1 2 3)) :exec) => '(1 2 3)
  (u/get-stack (handle-item (make-classic-interpreter) '(1 (2) (3))) :exec) => '(1 (2) (3))
  (u/get-stack (handle-item (make-classic-interpreter) '(1 () ())) :exec) => '(1 () ())
  (u/get-stack (handle-item (make-classic-interpreter) '()) :exec) => '())


(fact "handle-item will execute a registered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (basic-interpreter :instructions registry)]
   (handle-item he-knows-foo :foo) => 761))
    ;; an intentionally surprising result


(fact "handle-item will not execute an unregistered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (basic-interpreter :instructions registry)]
   (handle-item he-knows-foo :bar) => (throws #"Push Parsing Error:")))


;; some fixtures:


(def intProductToFloat
  (instr/build-instruction intProductToFloat
    (dsl/consume-top-of :integer :as :arg1)
    (dsl/consume-top-of :integer :as :arg2)
    (dsl/calculate [:arg1 :arg2] #(float (* %1 %2)) :as :p)
    (dsl/push-onto :float :p)))


(def knows-some-things
  (register-instruction
    (basic-interpreter 
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
  (u/get-stack knows-some-things :exec) => '(:intProductToFloat)
  (u/get-stack (reset-interpreter knows-some-things) :exec) =>
    '(1.1 2.2 :intProductToFloat))


(fact "calling `reset-interpreter` clears the other stacks"
  (u/get-stack knows-some-things :integer) => '(1 2 3)
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
  (is-done? (basic-interpreter)) => true
  (is-done? knows-some-things) => false)


;; step


(fact "calling `step` consumes one item from the :exec stack (if any)"
  (u/get-stack knows-some-things :exec) => '(:intProductToFloat)
  (u/get-stack (step knows-some-things) :exec) => '())


(fact "calling `step` increments the counter if something happens"
  (:counter knows-some-things) => 22
  (:counter (step knows-some-things)) => 23)


(fact "calling `step` doesn't affect the counter if :exec is empty"
  (:counter (basic-interpreter)) => 0
  (:counter (step (basic-interpreter))) => 0
  (:counter (step (u/clear-stack knows-some-things :exec))) => 22)


(fact "calling `step` sets the :done? flag if a halting condition is encountered"
  (is-done? knows-some-things) => false
  (:done? knows-some-things) => false
  (:done? (step knows-some-things)) => true)
