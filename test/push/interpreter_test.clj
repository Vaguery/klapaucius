(ns push.interpreter-test
  (:use midje.sweet)
  (:use [push.interpreter]))

;; initialization with make-interpreter

;; program


(fact "a new Interpreter will have a program"
  (:program (make-interpreter)) => [])


(fact "a program can be passed into make-interpreter"
  (:program (make-interpreter :program [1 2 3])) => [1 2 3])


(fact "a new Interpreter will have an :instructions map"
  (:instructions (make-interpreter)) => {})


(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :exec 
                                    :float 
                                    :input 
                                    :integer 
                                    :string] :in-any-order))


;; non-core but standard library core types: :tag, :genome, :return, :print, :puck, etc


(fact "a new Interpreter will have all the core stacks"
  (keys (:stacks (make-interpreter))) => (keys core-stacks))


(fact "make-interpreter can be passed a hashmap of populated stacks to merge into the core"
  (get-stack :integer (make-interpreter)) => '()
  (get-stack :integer (make-interpreter :stacks {:integer '(7 6 5)})) => '(7 6 5)
  (get-stack :foo (make-interpreter :stacks {:foo '(7 6 5)})) => '(7 6 5)
  (get-stack :integer (make-interpreter :stacks {:foo '(7 6 5)})) => '()
  (get-stack :foo (make-interpreter)) => nil)


;; instructions

;; make-instruction


(fact "make-instruction creates a new Instruction record with default values"
  (:token (make-instruction :foo)) => :foo
  (:needs (make-instruction :foo)) => {}
  (:makes (make-instruction :foo)) => {}
  (:function (make-instruction :foo)) => identity)


(fact "make-instruction accepts a :needs argument"
  (:needs (make-instruction :foo :needs {:integer 2})) => {:integer 2})


(fact "make-instruction accepts a :makes argument"
  (:makes (make-instruction :foo :makes {:boolean 3})) => {:boolean 3})


(fact "make-instruction accepts a :function argument"
  (let [fake_fn 88123]
     (:function (make-instruction :foo :function fake_fn)) => fake_fn))


;; register-instruction


(fact "register-instruction adds an Instruction to the registry in a specified Interpreter"
  (let [foo (make-instruction :foo)]
    (keys (:instructions 
      (register-instruction (make-interpreter) foo))) => '(:foo)
    (:foo (:instructions (register-instruction (make-interpreter) foo))) => foo))


(fact "register-instruction throws an exception if a token is reassigned (because that's what Clojush does)"
  (let [foo (make-instruction :foo)]
    (register-instruction (register-instruction (make-interpreter) foo) foo) =>
      (throws Exception "Push Instruction Redefined:':foo'")))


(fact "execute-instruction applies the named instruction to the Interpreter itself"
  (let [foo (make-instruction :foo :function (fn [a] 99))
        bar (make-instruction :bar)
        he-knows-foo (register-instruction
          (register-instruction (make-interpreter) foo) bar)]
    (keys (:instructions he-knows-foo)) => (just :foo :bar)
    (execute-instruction he-knows-foo :foo) => 99
    (execute-instruction he-knows-foo :bar) => he-knows-foo))


;; utilities and helpers


(fact "get-stack is a convenience function for reading the named stack"
  (get-stack :boolean (make-interpreter :stacks {:boolean '(false true)})) =>
    '(false true))


(fact "get-stack will happily look up and return any named stack"
  (get-stack :foo (make-interpreter)) => nil)


(fact "get-stack will return an empty list for an existing (but empty) stack"
  (get-stack :integer (make-interpreter)) => '())


(fact "boolean? checks that an item is specifically one of the LITERALS true or false"
  (boolean? 8) => false
  (boolean? true) => true
  (boolean? false) => true
  (boolean? nil) => false
  (boolean? '()) => false)


(fact "instruction? checks that an item is a registered Instruction in a given Interpreter"
 (let [foo (make-instruction :foo)
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (instruction? he-knows-foo :foo) => true
   (instruction? he-knows-foo :bar) => false))


;; dealing with stack items


(fact "push-item pushes the specified item to the stack, returning the updated Interpreter"
  (get-stack :integer (push-item (make-interpreter) :integer 9)) => '(9)
  (get-stack :integer (push-item 
    (make-interpreter :stacks {:integer '(1 2 3)}) :integer 9)) => '(9 1 2 3))


(fact "push-item does not do type-checking"
  (get-stack :integer (
    push-item (make-interpreter :stacks {:integer '(1 2 3)}) :integer false)) =>
    '(false 1 2 3))


(fact "push-item will create a stack if told to"
  (get-stack :foo (
    push-item (make-interpreter) :foo [1 :weird :thing])) => '([1 :weird :thing]))


;; handle-item


(fact "handle-item throws an error if the item isn't recognized"
  ;; yes this is trying to process another interpreter, which is (for now) not permitted
  (handle-item (make-interpreter) (make-interpreter)) => 
    (throws Exception #"Push Parsing Error:"))


(fact "handle-item sends integers to :integer"
  (get-stack :integer (handle-item (make-interpreter) 8)) => '(8)
  (get-stack :integer (handle-item (make-interpreter) -8)) => '(-8)
  (get-stack :integer (handle-item (make-interpreter :stacks {:integer '(1)}) -8)) =>
    '(-8 1))


; (fact "handle-item handles integer overflow")


(fact "handle-item sends floats to :float"
  (get-stack :float (handle-item (make-interpreter) 8.0)) => '(8.0)
  (get-stack :float (handle-item (make-interpreter) -8.0)) => '(-8.0)
  (get-stack :float (handle-item (make-interpreter :stacks {:float '(1.0)}) -8.0)) =>
    '(-8.0 1.0))


; (fact "handle-item handles float overflow")
; (fact "handle-item handles float underflow")


(fact "handle-item sends booleans to :boolean"
  (get-stack :boolean (handle-item (make-interpreter) false)) => '(false)
  (get-stack :boolean (handle-item (make-interpreter) true)) => '(true)
  (get-stack :boolean (handle-item (make-interpreter :stacks {:boolean '(false)}) true)) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (get-stack :char (handle-item (make-interpreter) \J)) => '(\J)
  (get-stack :char (handle-item (make-interpreter) \o)) => '(\o)
  (get-stack :char (handle-item (make-interpreter :stacks {:char '(\Y)}) \e)) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (get-stack :string (handle-item (make-interpreter) "foo")) => '("foo")
  (get-stack :string (handle-item (make-interpreter) "")) => '("")
  (get-stack :string (handle-item (make-interpreter :stacks {:string '("bar")}) "baz")) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' quoted lists onto :exec"
  (get-stack :exec (handle-item (make-interpreter) '(1 2 3))) => '(1 2 3)
  (get-stack :exec (handle-item (make-interpreter) '(1 (2) (3)))) => '(1 (2) (3))
  (get-stack :exec (handle-item (make-interpreter) '(1 () ()))) => '(1 () ())
  (get-stack :exec (handle-item (make-interpreter) '())) => '())


(fact "handle-item will execute a registered instruction"
 (let [foo (make-instruction :foo :function (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (handle-item he-knows-foo :foo) => 761 ;; an intentionally surprising result
   ))


(fact "handle-item will not execute an unregistered instruction"
 (let [foo (make-instruction :foo :function (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (make-interpreter :instructions registry)]
   (handle-item he-knows-foo :bar) => (throws #"Push Parsing Error:")))


;; step-interpreter


; (fact "stepping an interpreter with an integer on :exec moves it to :integer"
;   (:integer (:stacks (make-interpreter))) => '()
;   )