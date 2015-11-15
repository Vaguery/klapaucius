(ns push.instructions.instructions-setup-test
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u])
  (:require [push.instructions.dsl :as d])
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.core])
  )


;; make-instruction (bare bones)


(fact "make-instruction creates a new Instruction record with default values"
  (:token (make-instruction :foo)) => :foo
  (:tags (make-instruction :foo)) => #{}
  (:needs (make-instruction :foo)) => {}
  (:transaction (make-instruction :foo)) => identity)


(fact "make-instruction accepts a :tags argument"
  (:tags (make-instruction :foo :tags #{:nasty :brutish})) => #{:brutish :nasty})


(fact "make-instruction accepts a :needs argument"
  (:needs (make-instruction :foo :needs {:integer 2})) => {:integer 2})


(fact "make-instruction accepts a :transaction argument"
  (let [fake_fn 88123]
     (:transaction (make-instruction :foo :transaction fake_fn)) => fake_fn))


(fact "`make-instruction` sets the docstring field to a default value"
  (:docstring (make-instruction :foo)) => "`:foo` needs a docstring!")


;; build-instruction


(fact "build-instruction creates a new Instruction with the right token"
  (:token
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => :foobar)


(fact "build-instruction can accept an optional #tags argument"
  (:tags 
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => #{}
  (:tags 
    (build-instruction foobar
      :tags #{:foo :bar :baz!}
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => #{:bar :baz! :foo})


(fact "build-instruction creates a new Instruction with the right needs"
  (:needs
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => {:bar 0 :foo 1})


(fact "build-instruction creates a new Instruction a transaction that's a function"
  (fn? (:transaction
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in)))) => true)


(fact "`build-instruction` captures a keyword-specified docstring"
  (:docstring 
    (build-instruction foobar
      :docstring "foobar really?"
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => "foobar really?")


(fact "`build-instruction` lacking a docstring will get default"
  (:docstring 
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => "`:foobar` needs a docstring!")



;; a bit of a test


(fact "registering and executing the instruction in an Interpreter works"
  (let [foobar (build-instruction foobar             ;;; moves top :foo to :bar
                  (d/consume-top-of :foo :as :in)
                  (d/push-onto :bar :in))
        context (i/register-instruction 
                  (i/basic-interpreter :stacks {:foo '(1 2 3) :bar '(4 5 6)})
                  foobar)]
  (u/get-stack (i/execute-instruction context :foobar) :bar ) => '(1 4 5 6)
  (u/get-stack (i/execute-instruction context :foobar) :foo ) => '(2 3)))
