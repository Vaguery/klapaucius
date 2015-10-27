(ns push.instructions.instructions-setup-test
  (:use midje.sweet)
  (:use [push.instructions.instructions-core])
  (:use [push.instructions.dsl])
  (:require [push.interpreter.interpreter-core :as i])
)


;; make-instruction (bare bones)


(fact "make-instruction creates a new Instruction record with default values"
  (:token (make-instruction :foo)) => :foo
  (:needs (make-instruction :foo)) => {}
  (:makes (make-instruction :foo)) => {}
  (:transaction (make-instruction :foo)) => identity)


(fact "make-instruction accepts a :needs argument"
  (:needs (make-instruction :foo :needs {:integer 2})) => {:integer 2})


(fact "make-instruction accepts a :makes argument"
  (:makes (make-instruction :foo :makes {:boolean 3})) => {:boolean 3})


(fact "make-instruction accepts a :transaction argument"
  (let [fake_fn 88123]
     (:transaction (make-instruction :foo :transaction fake_fn)) => fake_fn))


;; build-instruction


(fact "build-instruction creates a new Instruction with the right token"
  (:token
    (build-instruction foobar
      (consume-top-of :foo :as :in)
      (push-onto :bar :in))) => :foobar)


(fact "build-instruction creates a new Instruction with the right needs"
  (:needs
    (build-instruction foobar
      (consume-top-of :foo :as :in)
      (push-onto :bar :in))) => {:bar 0 :foo 1})


(fact "build-instruction creates a new Instruction a transaction that's a function"
  (fn? (:transaction
    (build-instruction foobar
      (consume-top-of :foo :as :in)
      (push-onto :bar :in)))) => true)


(fact "registering and executing the instruction in an Interpreter works"
  (let [foobar (build-instruction foobar             ;;; moves top :foo to :bar
                    (consume-top-of :foo :as :in)
                    (push-onto :bar :in))
        context (i/register-instruction 
                  (i/make-interpreter :stacks {:foo '(1 2 3) :bar '(4 5 6)})
                  foobar)]
  (i/get-stack (i/execute-instruction context :foobar) :bar ) => '(1 4 5 6)
  (i/get-stack (i/execute-instruction context :foobar) :foo ) => '(2 3)))
