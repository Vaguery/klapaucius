(ns push.instructions.aspects.movable-test
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:require [push.interpreter.core :as i])
  (:require [push.interpreter.templates.minimum :as m])
  (:use push.instructions.aspects)
  (:use push.types.core)
  (:use push.instructions.aspects.movable)
  )


;; movable instructions


(fact "dup-instruction returns an Instruction with the correct stuff"
  (let [foo-dup (dup-instruction (make-type :foo))]
    (class foo-dup) => push.instructions.core.Instruction
    (:tags foo-dup) => #{:combinator}
    (:needs foo-dup) => {:foo 1}
    (:token foo-dup) => :foo-dup
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(1 2)}) foo-dup)
        :foo-dup)
      :foo) => '(1 1 2)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11)}) foo-dup)
        :foo-dup)
      :foo) => '(11 11)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-dup)
        :foo-dup)
      :foo) => '()))


(fact "flush-instruction returns an Instruction with the correct stuff"
  (let [foo-flush (flush-instruction (make-type :foo))]
    (class foo-flush) => push.instructions.core.Instruction
    (:tags foo-flush) => #{:combinator}
    (:needs foo-flush) => {:foo 0}
    (:token foo-flush) => :foo-flush
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(false 11)}) foo-flush)
        :foo-flush)
      :foo) => '()
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-flush)
        :foo-flush)
      :foo) => '()))


(fact "pop-instruction returns an Instruction with the correct stuff"
  (let [foo-pop (pop-instruction (make-type :foo))]
    (class foo-pop) => push.instructions.core.Instruction
    (:tags foo-pop) => #{:combinator}
    (:needs foo-pop) => {:foo 1}
    (:token foo-pop) => :foo-pop
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22)}) foo-pop)
        :foo-pop)
      :foo) => '(22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-pop)
        :foo-pop)
      :foo) => '()))


(fact "rotate-instruction returns an Instruction with the correct stuff"
  (let [foo-rotate (rotate-instruction (make-type :foo))]
    (class foo-rotate) => push.instructions.core.Instruction
    (:tags foo-rotate) => #{:combinator}
    (:needs foo-rotate) => {:foo 3}
    (:token foo-rotate) => :foo-rotate
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22 33 44)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22 44)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22 33)}) foo-rotate)
        :foo-rotate)
      :foo) => '(33 11 22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22)}) foo-rotate)
        :foo-rotate)
      :foo) => '(11 22)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-rotate)
        :foo-rotate)
      :foo) => '()))



(fact "shove-instruction returns an Instruction with the correct stuff"
  (let [foo-shove (shove-instruction (make-type :foo))]
    (class foo-shove) => push.instructions.core.Instruction
    (:tags foo-shove) => #{:combinator}
    (:needs foo-shove) => {:foo 1, :integer 1}
    (:token foo-shove) => :foo-shove
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :integer '(1)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :a :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :integer '(-1)}) foo-shove)
        :foo-shove)
      :foo) => '(:b :c :a)))


(fact "swap-instruction returns an Instruction with the correct stuff"
  (let [foo-swap (swap-instruction (make-type :foo))]
    (class foo-swap) => push.instructions.core.Instruction
    (:tags foo-swap) => #{:combinator}
    (:needs foo-swap) => {:foo 2}
    (:token foo-swap) => :foo-swap
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a :c)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b)}) foo-swap)
        :foo-swap)
      :foo) => '(:b :a)))


(fact "yank-instruction returns an Instruction with the correct stuff"
  (let [foo-yank (yank-instruction (make-type :foo))]
    (class foo-yank) => push.instructions.core.Instruction
    (:tags foo-yank) => #{:combinator}
    (:needs foo-yank) => {:foo 1, :integer 1}
    (:token foo-yank) => :foo-yank
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(2)}) foo-yank)
        :foo-yank)
      :foo) => '(:c :a :b :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(-2)}) foo-yank)
        :foo-yank)
      :foo) => '(:d :a :b :c :e)))



(fact "yankdup-instruction returns an Instruction with the correct stuff"
  (let [foo-yankdup (yankdup-instruction (make-type :foo))]
    (class foo-yankdup) => push.instructions.core.Instruction
    (:tags foo-yankdup) => #{:combinator}
    (:needs foo-yankdup) => {:foo 1, :integer 1}
    (:token foo-yankdup) => :foo-yankdup
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(2)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:c :a :b :c :d :e)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :integer '(-2)}) foo-yankdup)
        :foo-yankdup)
      :foo) => '(:d :a :b :c :d :e)))


(fact "`make-movable` adds the :movable attribute to a PushType record"
  (:attributes (make-movable (make-type :foo))) => #{:movable})


(fact "`make-movable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-movable (make-type :foo)))) =>
      (contains [:foo-dup :foo-flush :foo-pop :foo-rotate :foo-shove :foo-swap :foo-yank :foo-yankdup :foo-againlater] :gaps-ok :in-any-order))
