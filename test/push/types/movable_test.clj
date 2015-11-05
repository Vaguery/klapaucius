(ns push.types.movable-test
  (:use midje.sweet)
  (:use [push.types.core])
  (:require [push.interpreter.interpreter-core :as i]))


;; movable instructions


(fact "dup-instruction returns an Instruction with the correct stuff"
  (let [foo-dup (dup-instruction (make-type :foo))]
    (class foo-dup) => push.instructions.instructions_core.Instruction
    (:tags foo-dup) => #{:combinator}
    (:needs foo-dup) => {:foo 1}
    (:token foo-dup) => :foo-dup
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(1 2)}) foo-dup)
        :foo-dup)
      :foo) => '(1 1 2)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(11)}) foo-dup)
        :foo-dup)
      :foo) => '(11 11)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '()}) foo-dup)
        :foo-dup)
      :foo) => '()))


(fact "flush-instruction returns an Instruction with the correct stuff"
  (let [foo-flush (flush-instruction (make-type :foo))]
    (class foo-flush) => push.instructions.instructions_core.Instruction
    (:tags foo-flush) => #{:combinator}
    (:needs foo-flush) => {:foo 0}
    (:token foo-flush) => :foo-flush
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(false 11)}) foo-flush)
        :foo-flush)
      :foo) => '()
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '()}) foo-flush)
        :foo-flush)
      :foo) => '()))


(fact "pop-instruction returns an Instruction with the correct stuff"
  (let [foo-pop (pop-instruction (make-type :foo))]
    (class foo-pop) => push.instructions.instructions_core.Instruction
    (:tags foo-pop) => #{:combinator}
    (:needs foo-pop) => {:foo 1}
    (:token foo-pop) => :foo-pop
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(11 22)}) foo-pop)
        :foo-pop)
      :foo) => '(22)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '()}) foo-pop)
        :foo-pop)
      :foo) => '()))


(fact "rotate-instruction returns an Instruction with the correct stuff"
  (let [foo-rotate (rotate-instruction (make-type :foo))]
    (class foo-rotate) => push.instructions.instructions_core.Instruction
    (:tags foo-rotate) => #{:combinator}
    (:needs foo-rotate) => {:foo 3}
    (:token foo-rotate) => :foo-rotate
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(11 22 33 44)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22 44)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(11 22 33)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(11 22)}) foo-rotate)
        :foo-rotate)
      :foo) => '(11 22)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '()}) foo-rotate)
        :foo-rotate)
      :foo) => '()))



(fact "shove-instruction returns an Instruction with the correct stuff"
  (let [foo-shove (shove-instruction (make-type :foo))]
    (class foo-shove) => push.instructions.instructions_core.Instruction
    (:tags foo-shove) => #{:combinator}
    (:needs foo-shove) => {:foo 1, :integer 1}
    (:token foo-shove) => :foo-shove
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(:a :b :c) :integer '(1)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :a :c)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(:a :b :c) :integer '(-1)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :a)))


(fact "swap-instruction returns an Instruction with the correct stuff"
  (let [foo-swap (swap-instruction (make-type :foo))]
    (class foo-swap) => push.instructions.instructions_core.Instruction
    (:tags foo-swap) => #{:combinator}
    (:needs foo-swap) => {:foo 2}
    (:token foo-swap) => :foo-swap
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(:a :b :c)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a :c)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter :stacks {:foo '(:a :b)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a)))


(fact "yank-instruction returns an Instruction with the correct stuff"
  (let [foo-yank (yank-instruction (make-type :foo))]
    (class foo-yank) => push.instructions.instructions_core.Instruction
    (:tags foo-yank) => #{:combinator}
    (:needs foo-yank) => {:foo 1, :integer 1}
    (:token foo-yank) => :foo-yank
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(2)}) foo-yank)
        :foo-yank)
      :foo) => '(:c :a :b :d :e)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(-2)}) foo-yank)
        :foo-yank)
      :foo) => '(:d :a :b :c :e)))



(fact "yankdup-instruction returns an Instruction with the correct stuff"
  (let [foo-yankdup (yankdup-instruction (make-type :foo))]
    (class foo-yankdup) => push.instructions.instructions_core.Instruction
    (:tags foo-yankdup) => #{:combinator}
    (:needs foo-yankdup) => {:foo 1, :integer 1}
    (:token foo-yankdup) => :foo-yankdup
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(2)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:c :a :b :c :d :e)
    (i/get-stack
      (i/execute-instruction
        (i/register-instruction (i/make-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(-2)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:d :a :b :c :d :e)))


(fact "`make-movable` adds the :movable attribute to a PushType record"
  (:attributes (make-movable (make-type :foo))) => #{:movable})


(fact "`make-movable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-movable (make-type :foo)))) =>
      '(:foo-dup :foo-flush :foo-pop :foo-rotate :foo-shove :foo-swap :foo-yank :foo-yankdup))
