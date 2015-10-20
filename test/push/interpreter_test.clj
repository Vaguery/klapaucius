(ns push.interpreter-test
  (:use midje.sweet)
  (:use [push.interpreter]))

;; initialization

;; default behavior

(fact "a new Interpreter will have the core stack types"
  (keys (:stacks (make-interpreter))) => (contains :boolean)
  (keys (:stacks (make-interpreter))) => (contains :char)
  (keys (:stacks (make-interpreter))) => (contains :code)
  (keys (:stacks (make-interpreter))) => (contains :exec)
  (keys (:stacks (make-interpreter))) => (contains :float)
  (keys (:stacks (make-interpreter))) => (contains :input)
  (keys (:stacks (make-interpreter))) => (contains :integer)
  (keys (:stacks (make-interpreter))) => (contains :string)
  )

;; not core: :tag, :genome, :return, :print, :puck