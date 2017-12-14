(ns push.instructions.aspects.set-able-test
  (:require [push.interpreter.core :as i])
  (:require [push.core :as push])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.set-able)
  )


;; set-able instructions




(fact "conj-set-instruction returns an Instruction with the correct stuff"
  (let [foo-conj-set (conj-set-instruction (make-type :foo))]
    (class foo-conj-set) => push.instructions.core.Instruction
    (:needs foo-conj-set) => {:foo 1, :set 1}
    (:token foo-conj-set) => :foo-conj-set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :exec) => '(#{99 11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3]) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :exec) => '(#{99 1 2 3})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '({1 2 3 4}) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :exec) => '(#{99 {1 2 3 4}})
    ))





(fact "intoset-instruction returns an Instruction with the correct stuff"
  (let [foo-intoset (intoset-instruction (make-type :foo))]
    (class foo-intoset) => push.instructions.core.Instruction
    (:needs foo-intoset) => {:foo 1, :set 1}
    (:token foo-intoset) => :foo-intoset
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{99})}) foo-intoset)
        :foo-intoset)
      :exec) => '(#{99 11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3]) :set '(#{99})}) foo-intoset)
        :foo-intoset)
      :exec) => '(#{99 [1 2 3]})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '({1 2 3 4}) :set '(#{99})}) foo-intoset)
        :foo-intoset)
      :exec) => '(#{99 {1 2 3 4}})
    ))




(fact "as-set-instruction returns an Instruction with the correct stuff"
  (let [foo-as-set (as-set-instruction (make-type :foo))]
    (class foo-as-set) => push.instructions.core.Instruction
    (:needs foo-as-set) => {:foo 1}
    (:token foo-as-set) => :foo-as-set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11)}) foo-as-set)
        :foo-as-set)
      :exec) => '(#{11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3])}) foo-as-set)
        :foo-as-set)
      :exec) => '(#{1 2 3})
    ))


(fact "toset-instruction returns an Instruction with the correct stuff"
  (let [foo-toset (toset-instruction (make-type :foo))]
    (class foo-toset) => push.instructions.core.Instruction
    (:needs foo-toset) => {:foo 1}
    (:token foo-toset) => :foo->set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11)}) foo-toset)
        :foo->set)
      :exec) => '(#{11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3])}) foo-toset)
        :foo->set)
      :exec) => '(#{[1 2 3]})
    ))



(fact "in-set?-instruction returns an Instruction with the correct stuff"
  (let [foo-in-set? (in-set?-instruction (make-type :foo))]
    (class foo-in-set?) => push.instructions.core.Instruction
    (:needs foo-in-set?) => {:foo 1, :set 1}
    (:token foo-in-set?) => :foo-in-set?
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{22})}) foo-in-set?)
        :foo-in-set?)
      :exec) => '(false)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{11 22 33})}) foo-in-set?)
        :foo-in-set?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([11]) :set '(#{[11] 22 33})}) foo-in-set?)
        :foo-in-set?)
      :exec) => '(true)
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{[11] 22 33})}) foo-in-set?)
        :foo-in-set?)
      :exec) => '(false)
    ))




(fact "`make-set-able` takes adds the :set-able attribute to a PushType record"
  (:attributes (make-set-able (make-type :foo))) => #{:set-able})


(fact "`make-set-able` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-set-able (make-type :foo)))) => (contains [:foo->set :foo-as-set] :in-any-order :gaps-ok))
