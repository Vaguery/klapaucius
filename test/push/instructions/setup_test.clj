(ns push.instructions.setup-test
  (:use midje.sweet)
  (:use [push.instructions.instructions-core])
)


;; make-instruction


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

