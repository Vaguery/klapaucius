(ns push.instructions.aspects.collectible-test
  (:require [push.interpreter.core :as i])
  (:require [push.core :as push])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.collectible)
  )


;; collectible instructions


(fact "toset-instruction returns an Instruction with the correct stuff"
  (let [foo-toset (toset-instruction (make-type :foo))]
    (class foo-toset) => push.instructions.core.Instruction
    (:needs foo-toset) => {:foo 1, :set 0}
    (:token foo-toset) => :foo->set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11)}) foo-toset)
        :foo->set)
      :set) => '(#{11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3])}) foo-toset)
        :foo->set)
      :set) => '(#{[1 2 3]})
    ))



(fact "conj-set-instruction returns an Instruction with the correct stuff"
  (let [foo-conj-set (conj-set-instruction (make-type :foo))]
    (class foo-conj-set) => push.instructions.core.Instruction
    (:needs foo-conj-set) => {:foo 1, :set 1}
    (:token foo-conj-set) => :foo-conj-set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :set) => '(#{99 11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3]) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :set) => '(#{99 1 2 3})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '({1 2 3 4}) :set '(#{99})}) foo-conj-set)
        :foo-conj-set)
      :set) => '(#{99 {1 2 3 4}})
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
      :set) => '(#{99 11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3]) :set '(#{99})}) foo-intoset)
        :foo-intoset)
      :set) => '(#{99 [1 2 3]})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '({1 2 3 4}) :set '(#{99})}) foo-intoset)
        :foo-intoset)
      :set) => '(#{99 {1 2 3 4}})
    ))




(fact "as-set-instruction returns an Instruction with the correct stuff"
  (let [foo-as-set (as-set-instruction (make-type :foo))]
    (class foo-as-set) => push.instructions.core.Instruction
    (:needs foo-as-set) => {:foo 1, :set 0}
    (:token foo-as-set) => :foo-as-set
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '(11)}) foo-as-set)
        :foo-as-set)
      :set) => '(#{11})
    (get-stack
      (i/execute-instruction
        (i/register-instruction (push/interpreter :stacks {:foo '([1 2 3])}) foo-as-set)
        :foo-as-set)
      :set) => '(#{1 2 3})
    ))




(fact "`make-collectible` takes adds the :collectible attribute to a PushType record"
  (:attributes (make-collectible (make-type :foo))) => #{:collectible})


(fact "`make-collectible` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-collectible (make-type :foo)))) => (contains [:foo->set :foo-as-set] :in-any-order :gaps-ok))

