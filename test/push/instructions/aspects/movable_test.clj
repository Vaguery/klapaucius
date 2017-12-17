(ns push.instructions.aspects.movable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m]
            [push.core :as push]
            [push.type.definitions.quoted :as qc])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.instructions.aspects)
  (:use push.type.core)
  (:use push.util.numerics)
  (:use push.instructions.aspects.movable)
  )


;; support functions

(fact "scalar-to-index returns a valid index (rounding up!)"
  (scalar-to-index 10 7) => 3
  (scalar-to-index -10 7) => 4
  (scalar-to-index -10.5 7) => 4.0
  (scalar-to-index 87/7 6) => 1N
  (scalar-to-index 94/7 6) => 2N
  (scalar-to-index 87/7 1) => 0N
  (scalar-to-index 77777777777777777777777777777777777777777M 8) => 1N
  (scalar-to-index 77777777777777777777777777777777777777777N 8) => 1N
  (scalar-to-index 76652.3333e871M 17) => 9N
  (scalar-to-index -76652.3333e871M 17) => 8N
  (scalar-to-index ∞ 17) => 0
  (scalar-to-index -∞ 17) => 0
  (scalar-to-index -1.2246467991473532E-16 4) => 0
  )

;; helper

(defn q!
  [item]
  (qc/push-quote item))


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
      :exec) => '((1 1))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11)}) foo-dup)
        :foo-dup)
      :exec) => '((11 11))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '()}) foo-dup)
        :foo-dup)
      :exec) => '()
      ))


(fact "dup-instruction quotes :code items"
  (get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:code '(1 2 3)}) :code-dup)
    :exec) => (list (list (q! 1) (q! 1)))
    )

(fact "code-dup clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-dup)) =>
  #"All items taken from :code are returned"
  )


(fact "dup-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-dup)
    :exec) => '((:push-quoterefs (:a :a) :push-unquoterefs))
    )


(fact "ref-dup clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-dup)) =>
  #"The returned block of items is wrapped in"
  )



(fact "flush-instruction returns an Instruction with the correct stuff"
  (let [foo-flush (flush-instruction (make-type :foo))]
    (class foo-flush) => push.instructions.core.Instruction
    (:tags foo-flush) => #{:combinator}
    (:needs foo-flush) => {}
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
    (:needs foo-pop) => {}
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
      :exec) => '((22 11 33))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(11 22 33)}) foo-rotate)
        :foo-rotate)
      :exec) => '((22 11 33))
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


(fact "rotate-instruction quotes :code items"
  (get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:code '(1 2 3)}) :code-rotate)
    :exec) => (list (list (q! 2) (q! 1) (q! 3)))
    )

(fact "code-rotate clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-rotate)) =>
  #"All items taken from :code are returned"
  )


(fact "rotate-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d)}) :ref-rotate)
    :exec) => '((:push-quoterefs (:b :a :c) :push-unquoterefs))
    )


(fact "ref-rotate clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-rotate)) =>
  #"The returned block of items is wrapped in"
  )



(fact "shove-instruction returns an Instruction with the correct stuff"
  (let [foo-shove (shove-instruction (make-type :foo))]
    (class foo-shove) => push.instructions.core.Instruction
    (:tags foo-shove) => #{:combinator}
    (:needs foo-shove) => {:foo 1, :scalar 1}
    (:token foo-shove) => :foo-shove
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(1)}) foo-shove)
        :foo-shove)
      :exec) => '(((:c) :a (:b)))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(0)}) foo-shove)
        :foo-shove)
      :exec) => '(((:c :b) :a ()))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(2)}) foo-shove)
        :foo-shove)
      :exec) => '((() :a (:c :b)))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(-1)}) foo-shove)
        :foo-shove)
      :exec) => '(((:c :b) :a ()))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c) :scalar '(-1182)}) foo-shove)
        :foo-shove)
      :exec) => '(((:c :b) :a ()))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(387)}) foo-shove)
        :foo-shove)
      :exec) => '((() :a (:e :d :c :b)))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(38702777777777777744444444444444444444444N)}) foo-shove)
        :foo-shove)
      :exec) => '((() :a (:e :d :c :b)))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(-38702777777777777744444444444444444444444N)}) foo-shove)
        :foo-shove)
      :exec) => '(((:e :d :c :b) :a ()))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(387/13)}) foo-shove)
        :foo-shove)
      :exec) => '((() :a (:e :d :c :b)))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b :c :d :e) :scalar '(7.71753612845e612M)}) foo-shove)
        :foo-shove)
      :exec) => '((() :a (:e :d :c :b)))
    ))


(fact "shove-instruction quotes :code items"
  (get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(1) :code '(1 2 3 4)}) :code-shove)
    :exec) => (list (list (list (q! 4) (q! 3))
                                (q! 1)
                          (list (q! 2))))
                          )

(fact "code-shove clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-shove)) =>
  #"All items taken from :code are returned"
  )


(fact "shove-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d :e)}) :ref-shove)
    :exec) => '((:push-quoterefs ((:e) :a (:d :c :b)) :push-unquoterefs))
    )


(fact "ref-shove clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-shove)) =>
  #"The returned block of items is wrapped in"
  )



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
      :exec) => '((:a :b))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter :stacks {:foo '(:a :b)}) foo-swap)
        :foo-swap)
      :exec) => '((:a :b))
      ))


(fact "swap-instruction quotes :code items"
  (get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:code '(1 2 3)}) :code-swap)
    :exec) => (list (list (q! 1) (q! 2)))
    )


(fact "code-swap clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-swap)) =>
  #"All items taken from :code are returned"
  )


(fact "swap-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d :e)}) :ref-swap)
    :exec) => '((:push-quoterefs (:a :b) :push-unquoterefs))
    )


(fact "ref-swap clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-swap)) =>
  #"The returned block of items is wrapped in"
  )


(fact "yank-instruction returns an Instruction with the correct stuff"
  (let [foo-yank (yank-instruction (make-type :foo))]
    (class foo-yank) => push.instructions.core.Instruction
    (:tags foo-yank) => #{:combinator}
    (:needs foo-yank) => {:scalar 1}
    (:token foo-yank) => :foo-yank
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(2)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e :d) (:b :a) :c))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(0)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e :d :c :b) () :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-1)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e :d :c :b) () :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(4)}) foo-yank)
        :foo-yank)
      :exec) => '((() (:d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(19122)}) foo-yank)
        :foo-yank)
      :exec) => '((() (:d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-8.555)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e :d :c :b) () :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(19122/17)}) foo-yank)
        :foo-yank)
      :exec) => '((() (:d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(5/2)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e) (:c :b :a) :d))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-666666666666.55555M)}) foo-yank)
        :foo-yank)
      :exec) => '(((:e :d :c :b) () :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '() :scalar '(2)}) foo-yank)
        :foo-yank)
      :exec) => '((() ()))
      ))


(fact "yank-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(2) :code '(1 2 3 4)}) :code-yank)
    :exec) => (list (list
                (list (q! 4))
                (list (q! 2) (q! 1))
                (q! 3)
                )))

(fact "code-yank clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-yank)) =>
  #"All items taken from :code are returned"
  )


(fact "yank-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d :e)}) :ref-yank)
    :exec) => '((:push-quoterefs ((:e) (:c :b :a) :d) :push-unquoterefs))
    )


(fact "ref-yank clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-yank)) =>
  #"The returned block of items is wrapped in"
  )



(fact "yankdup-instruction returns an Instruction with the correct stuff"
  (let [foo-yankdup (yankdup-instruction (make-type :foo))]
    (class foo-yankdup) => push.instructions.core.Instruction
    (:tags foo-yankdup) => #{:combinator}
    (:needs foo-yankdup) => {:scalar 1}
    (:token foo-yankdup) => :foo-yankdup
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(2)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :c))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(0)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-1)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(4)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(912)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(9.7e82)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(912/7)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :e))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '(:a :b :c :d :e) :scalar '(-912876.5512M)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(((:e :d :c :b :a) :a))
    (get-stack
      (i/execute-instruction
        (i/register-instruction (m/basic-interpreter
          :stacks {:foo '() :scalar '(8)}) foo-yankdup)
        :foo-yankdup)
      :exec) => '(())
    ))


(fact "yankdup-instruction quotes :code items"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(2) :code '(1 2 3 4)}) :code-yankdup)
    :exec) => (list (list
                (list (q! 4) (q! 3) (q! 2) (q! 1))
                (q! 3)
                )))

(fact "code-yankdup clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :code-yankdup)) =>
  #"All items taken from :code are returned"
  )


(fact "yankdup-instruction handles :ref items differently"
  (push/get-stack
    (i/execute-instruction
      (push/interpreter :stacks {:scalar '(3) :ref '(:a :b :c :d :e)}) :ref-yankdup)
    :exec) => '((:push-quoterefs ((:e :d :c :b :a) :d) :push-unquoterefs))
    )


(fact "ref-yankdup clarifies this in its docstring"
  (:docstring (i/get-instruction (push/interpreter) :ref-yankdup)) =>
  #"The returned block of items is wrapped in"
  )


(fact "`make-movable` adds the :movable attribute to a PushType record"
  (:attributes (make-movable (make-type :foo))) => #{:movable})


(fact "`make-movable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-movable (make-type :foo)))) =>
      (contains [:foo-dup :foo-flush :foo-pop :foo-rotate :foo-shove :foo-swap :foo-yank :foo-yankdup :foo-againlater] :gaps-ok :in-any-order))
