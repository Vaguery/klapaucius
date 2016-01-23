(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use [push.instructions.aspects :as aspects])
  (:use [push.util.type-checkers :only (boolean?)])
  )


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


(defn xor2 [p q] (or (and p (not q)) (and q (not p))))


(def bool-and
  (t/simple-2-in-1-out-instruction
    "`:boolean-and` pops the top two `:boolean` items, and pushes `true` if they're both `true`, `false` otherwise"
    :boolean "and" 'and))

  
(def bool-or
  (t/simple-2-in-1-out-instruction
    "`:boolean-or` pops the top two `:boolean` items, and pushes `true` if either one is `true, `false` otherwise"
    :boolean "or" 'or))


(def bool-xor
  (t/simple-2-in-1-out-instruction
    "`:boolean-xor` pops the top two `:boolean` items, and pushes `true` if exactly one of them is `true`, or `false` otherwise"
    :boolean "xor" 'xor2))


(def bool-not 
  (t/simple-1-in-1-out-instruction
  "`:boolean-not returns the logical negation of the top item on the `:boolean`
  stack"
  :boolean "not" 'not))


(def boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        aspects/make-visible 
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        (t/attach-instruction , bool-and)
        (t/attach-instruction , integer->boolean)
        (t/attach-instruction , intsign->boolean)
        (t/attach-instruction , float->boolean)
        (t/attach-instruction , floatsign->boolean)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-xor)
        ))

