(ns push.interpreter.interpreter-setup-test
  (:use midje.sweet)
  (:require [push.instructions.instructions-core :as i])
  (:use [push.instructions.dsl])
  (:use [push.interpreter.interpreter-core]))

;; initialization with make-interpreter

;; program


(fact "a new Interpreter will have a program"
  (:program (make-interpreter)) => [])


(fact "a program can be passed into make-interpreter"
  (:program (make-interpreter :program [1 2 3])) => [1 2 3])


(fact "a new Interpreter will have an :instructions map"
  (:instructions (make-interpreter)) => {})


(fact "a new Interpreter will have a counter = 0"
  (:counter (make-interpreter)) => 0)


(fact "a counter value can be passed into make-interpreter"
  (:counter (make-interpreter :counter 771)) => 771 )


(fact "a new interpreter will have a default :done? setting of false"
  (:done? (make-interpreter)) => false )


(fact "a new interpreter can have :done? set as an option"
  (:done? (make-interpreter :done? true)) => true )


(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :exec 
                                    :float 
                                    :integer 
                                    :string] :in-any-order))


;; non-core but standard library core types: :tag, :genome, :return, :print, :puck, etc


(fact "a new Interpreter will have all the core stacks"
  (keys (:stacks (make-interpreter))) => (keys core-stacks))


(fact "make-interpreter can be passed a hashmap of populated stacks to merge into the core"
  (get-stack (make-interpreter) :integer ) => '()
  (get-stack (make-interpreter :stacks {:integer '(7 6 5)}) :integer ) => '(7 6 5)
  (get-stack (make-interpreter :stacks {:foo '(7 6 5)}) :foo ) => '(7 6 5)
  (get-stack (make-interpreter :stacks {:foo '(7 6 5)}) :integer ) => '()
  (get-stack (make-interpreter) :foo ) => nil)



;; register-instruction


(fact "register-instruction adds an Instruction to the registry in a specified Interpreter"
  (let [foo (i/make-instruction :foo)]
    (keys (:instructions 
      (register-instruction (make-interpreter) foo))) => '(:foo)
    (:foo (:instructions (register-instruction (make-interpreter) foo))) => foo))


(fact "register-instruction throws an exception if a token is reassigned (because that's what Clojush does)"
  (let [foo (i/make-instruction :foo)]
    (register-instruction (register-instruction (make-interpreter) foo) foo) =>
      (throws Exception "Push Instruction Redefined:':foo'")))


;; contains-at-least?


(fact "contains-at-least? returns true if the count of the specified stack is >= the number"
  (let [abbr #'push.interpreter.interpreter-core/contains-at-least?]
  (abbr (make-interpreter) :integer 0) => true
  (abbr (make-interpreter) :integer 3) => false
  (abbr (make-interpreter :stacks {:integer '(1 2 3)}) :integer 3) => true
  (abbr (make-interpreter :stacks {:integer '(1 2 3)}) :integer 2) => true))


(fact "contains-at-least? returns false if the named stack isn't present"
  (let [abbr #'push.interpreter.interpreter-core/contains-at-least?]

  (abbr (make-interpreter) :foo 0) => false
  (abbr (make-interpreter) :boolean 0) => true))


;; ready-for-instruction?


(fact "ready-for-instruction? returns false if the :needs of the specified instruction aren't met"
  (let [foo
          (i/make-instruction :foo :needs {:integer 2})
        an-int
          (register-instruction (make-interpreter :stacks {:integer '(1)}) foo)
        many-ints
          (register-instruction (make-interpreter :stacks {:integer '(1 2 3 4)}) foo)]
    (count (get-stack an-int :integer)) => 1
    (ready-for-instruction? an-int :foo) => false
    (count (get-stack many-ints :integer )) => 4
    (#'push.interpreter.interpreter-core/contains-at-least?
        many-ints :integer 2) => true
    (ready-for-instruction? many-ints :foo) => true))


(fact "ready-for-instruction? returns false if the named instruction is not registered"
  (let [foo
          (i/make-instruction :foo :needs {:integer 2})
        an-int
          (register-instruction (make-interpreter :stacks {:integer '(1)}) foo)]
    (ready-for-instruction? an-int :bar) => false))


;; execute-instruction


(fact "execute-instruction applies the named instruction to the Interpreter itself"
  (let [foo (i/make-instruction :foo :transaction (fn [a] 99))
        bar (i/make-instruction :bar)
        he-knows-foo (register-instruction
          (register-instruction (make-interpreter) foo) bar)]
    (keys (:instructions he-knows-foo)) => (just :foo :bar)
    (execute-instruction he-knows-foo :foo) => 99
    (execute-instruction he-knows-foo :bar) => he-knows-foo))


(fact "execute-instruction will not change the Interpreter if the needs aren't met"
  (let [foo (i/make-instruction :foo :needs {:integer 3} :transaction (fn [a] 99))
      he-knows-foo (register-instruction (make-interpreter) foo)]
    (execute-instruction he-knows-foo :foo) => he-knows-foo ;; not enough integers
    (execute-instruction
      (assoc-in he-knows-foo [:stacks :integer] '(1 2 3 4)) :foo) => 99))


(fact "execute-instruction will throw an Exception if the token is not registered"
    (execute-instruction (make-interpreter) :foo) => (throws #"Unknown Push instruction:"))


;; utilities and helpers


(fact "get-stack is a convenience function for reading the named stack"
  (get-stack (make-interpreter :stacks {:boolean '(false true)}) :boolean) =>
    '(false true))


(fact "get-stack will happily look up and return any named stack"
  (get-stack (make-interpreter) :foo) => nil)


(fact "get-stack will return an empty list for an existing (but empty) stack"
  (get-stack (make-interpreter) :integer) => '())


(fact "set-stack replaces a stack completely"
  (get-stack (set-stack (make-interpreter) :integer '(1 2 3)) :integer) => '(1 2 3)
  )


(fact "boolean? checks that an item is specifically one of the LITERALS true or false"
  (boolean? 8) => false
  (boolean? true) => true
  (boolean? false) => true
  (boolean? nil) => false
  (boolean? '()) => false)


(fact "instruction? checks that an item is a registered Instruction in a given Interpreter"
 (let [foo (i/make-instruction :foo)
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (instruction? he-knows-foo :foo) => true
   (instruction? he-knows-foo :bar) => false))


;; dealing with stack items


(fact "push-item pushes the specified item to the stack, returning the updated Interpreter"
  (get-stack (push-item (make-interpreter) :integer 9) :integer) => '(9)
  (get-stack (push-item 
    (make-interpreter :stacks {:integer '(1 2 3)}) :integer 9) :integer) => '(9 1 2 3))


(fact "push-item does not do type-checking"
  (get-stack (
    push-item (make-interpreter :stacks {:integer '(1 2 3)}) :integer false)  :integer) =>
    '(false 1 2 3))


(fact "push-item will create a stack if told to"
  (get-stack (push-item (make-interpreter) :foo [1 :weird :thing]) :foo) =>
    '([1 :weird :thing]))


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

