(ns push.interpreter-test
  (:use midje.sweet)
  (:use [push.interpreter]))

;; initialization with make-interpreter

;; program


(fact "a new Interpreter will have a program"
  (:program (make-interpreter)) => [])


(fact "a program can be passed into make-interpreter"
  (:program (make-interpreter [1 2 3])) => [1 2 3])


;; stacks


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
  (get-stack :integer (make-interpreter [] {})) => '()
  (get-stack :integer (make-interpreter [] {:integer '(7 6 5)})) => '(7 6 5)
  (get-stack :foo (make-interpreter [] {:foo '(7 6 5)})) => '(7 6 5))


;; utilities and helpers


(fact "get-stack is a convenience function for reading the named stack"
  (get-stack :boolean (make-interpreter [] {:boolean '(false true)})) =>
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


;; dealing with stack items


(fact "push-item pushes the specified item to the stack, returning the updated Interpreter"
  (get-stack :integer (push-item (make-interpreter) :integer 9)) => '(9)
  (get-stack :integer (push-item (make-interpreter [] {:integer '(1 2 3)}) :integer 9)) =>
    '(9 1 2 3))


(fact "push-item does not do type-checking"
  (get-stack :integer (
    push-item (make-interpreter [] {:integer '(1 2 3)}) :integer false)) =>
    '(false 1 2 3))


;; route-item


(fact "route-item throws an error if the item isn't recognized"
  ;; yes this is trying to process another interpreter, which is (for now) not permitted
  (route-item (make-interpreter) (make-interpreter)) => 
    (throws Exception #"Push Parsing Error:"))


(fact "route-item sends integers to :integer"
  (get-stack :integer (route-item (make-interpreter) 8)) => '(8)
  (get-stack :integer (route-item (make-interpreter) -8)) => '(-8)
  (get-stack :integer (route-item (make-interpreter [] {:integer '(1)}) -8)) => '(-8 1))


; (fact "route-item handles integer overflow")


(fact "route-item sends floats to :float"
  (get-stack :float (route-item (make-interpreter) 8.0)) => '(8.0)
  (get-stack :float (route-item (make-interpreter) -8.0)) => '(-8.0)
  (get-stack :float (route-item (make-interpreter [] {:float '(1.0)}) -8.0)) => '(-8.0 1.0))


; (fact "route-item handles float overflow")
; (fact "route-item handles float underflow")


(fact "route-item sends booleans to :boolean"
  (get-stack :boolean (route-item (make-interpreter) false)) => '(false)
  (get-stack :boolean (route-item (make-interpreter) true)) => '(true)
  (get-stack :boolean (route-item (make-interpreter [] {:boolean '(false)}) true)) => '(true false))


(fact "route-item sends characters to :char"
  (get-stack :char (route-item (make-interpreter) \J)) => '(\J)
  (get-stack :char (route-item (make-interpreter) \o)) => '(\o)
  (get-stack :char (route-item (make-interpreter [] {:char '(\Y)}) \e)) => '(\e \Y))


(fact "route-item sends strings to :string"
  (get-stack :string (route-item (make-interpreter) "foo")) => '("foo")
  (get-stack :string (route-item (make-interpreter) "")) => '("")
  (get-stack :string (route-item (make-interpreter [] {:string '("bar")}) "baz")) =>
    '("baz" "bar"))


;; process-expression


(fact "process-expression 'interprets' a specified Clojure expression in the interpreter"
  (let [dumb-interpreter (make-interpreter)]
    (get-stack :integer (process-expression dumb-interpreter 8)) => '(8)
    ))


;; step-interpreter


; (fact "stepping an interpreter with an integer on :exec moves it to :integer"
;   (:integer (:stacks (make-interpreter))) => '()
;   )