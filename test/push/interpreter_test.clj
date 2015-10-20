(ns push.interpreter-test
  (:use midje.sweet)
  (:use [push.interpreter]))

;; initialization with make-interpreter

;; program

(fact "a new Interpreter will have a program"
  (:program (make-interpreter)) => []
  )

(fact "a program can be passed into make-interpreter"
  (:program (make-interpreter [1 2 3])) => [1 2 3]
  )

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
  (keys (:stacks (make-interpreter))) => (keys core-stacks)
  )

(fact "make-interpreter can be passed a hashmap of populated stacks to merge into the core"
  (:integer (:stacks (make-interpreter [] {}))) => '()
  (:integer (:stacks (make-interpreter [] {:integer '(7 6 5)}))) => '(7 6 5)
  (:foo (:stacks (make-interpreter [] {:foo '(7 6 5)}))) => '(7 6 5)
  )