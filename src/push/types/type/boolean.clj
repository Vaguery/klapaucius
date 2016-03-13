(ns push.types.type.boolean
  (:use push.types.type.generator)
  (:use [push.util.type-checkers :only (boolean?)])
  (:require [push.instructions.aspects :as aspects]
            [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.exotics :as exotics]
            ))


;; SUPPORT


(defn xor2 [p q] (or (and p (not q)) (and q (not p))))


;; INSTRUCTIONS


(def boolean-2bittable
  (core/build-instruction
    boolean-2bittable
    "`:boolean-2bittable` pops the top `:integer` item, converts it into a 2-bit truth table by taking its value (modulo 16) in radix 2, pops 2 `:boolean` items (`q` and `p` respectively in stack order) and returns the indicated result"
    :tags #{:boolean :conversion}
    (d/consume-top-of :integer :as :which)
    (d/consume-top-of :boolean :as :q)
    (d/consume-top-of :boolean :as :p)
    (d/calculate [:which] #(exotics/integer-to-truth-table (mod %1 16) 2) :as :table)
    (d/calculate [:p :q] #(+ (* 2 (exotics/bit-to-int %1)) (exotics/bit-to-int %2)) :as :idx)
    (d/calculate [:table :idx] #(nth %1 %2) :as :result) 
    (d/push-onto :boolean :result)))



(def boolean-3bittable
  (core/build-instruction
    boolean-3bittable
    "`:boolean-3bittable` pops the top `:integer` item, converts it into a 3-bit truth table by taking its value (modulo 32) in radix 2, pops 3 `:boolean` items (`r`, `q` and `p` respectively in stack order) and returns the indicated result"
    :tags #{:boolean :conversion}
    (d/consume-top-of :integer :as :which)
    (d/consume-top-of :boolean :as :r)
    (d/consume-top-of :boolean :as :q)
    (d/consume-top-of :boolean :as :p)
    (d/calculate [:which] #(exotics/integer-to-truth-table (mod %1 32) 3) :as :table)
    (d/calculate [:p :q :r]
      #(+ 
        (* 4 (exotics/bit-to-int %1))
        (* 2 (exotics/bit-to-int %2))
        (exotics/bit-to-int %3)) :as :idx)
    (d/calculate [:table :idx] #(nth %1 %2) :as :result) 
    (d/push-onto :boolean :result)))



(def bool-and
  (t/simple-2-in-1-out-instruction
    "`:boolean-and` pops the top two `:boolean` items, and pushes `true` if they're both `true`, `false` otherwise"
    :boolean "and" 'and))



(def bool-not 
  (t/simple-1-in-1-out-instruction
  "`:boolean-not returns the logical negation of the top item on the `:boolean`
  stack"
  :boolean "not" 'not))



(def bool-or
  (t/simple-2-in-1-out-instruction
    "`:boolean-or` pops the top two `:boolean` items, and pushes `true` if either one is `true, `false` otherwise"
    :boolean "or" 'or))



(def bool-xor
  (t/simple-2-in-1-out-instruction
    "`:boolean-xor` pops the top two `:boolean` items, and pushes `true` if exactly one of them is `true`, or `false` otherwise"
    :boolean "xor" 'xor2))



(def float->boolean
  (core/build-instruction
    float->boolean
    "`:float->boolean` pops the top `:float` item, and pushes `false` if it is 0.0, or `true` if it is any other value"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))



(def floatsign->boolean
  (core/build-instruction
    floatsign->boolean
    "`:floatsign->boolean` pops the top `:float` item, and pushes `true` if it positive, or `false` if it is zero or negative"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))



(def integer->boolean
  (core/build-instruction
    integer->boolean
    "`:integer->boolean` pops the top `:integer` item, and pushes `false` if it is 0, or `true` if it is any other value"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))



(def intsign->boolean
  (core/build-instruction
    intsign->boolean
    "`:intsign->boolean` pops the top `:integer` item, and pushes `true` if it positive, or `false` if it is zero or negative"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))



(def boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
        (t/attach-instruction , boolean-2bittable)
        (t/attach-instruction , boolean-3bittable)
        (t/attach-instruction , bool-and)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-xor)
        (t/attach-instruction , float->boolean)
        (t/attach-instruction , floatsign->boolean)
        (t/attach-instruction , integer->boolean)
        (t/attach-instruction , intsign->boolean)
        ))

