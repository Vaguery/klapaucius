(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.modules.print :as print])
  (:use push.instructions.aspects.equatable)
  (:use push.instructions.aspects.movable)
  (:use push.instructions.aspects.returnable)
  (:use push.instructions.aspects.visible)
  (:use [push.util.type-checkers :only (boolean?)])
  )


(def boolean-frominteger
  (core/build-instruction
    boolean-frominteger
    "`:boolean-frominteger` pops the top `:integer` item, and pushes `false` if it is 0, or `true` if it is any other value"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromintsign
  (core/build-instruction
    boolean-fromintsign
    "`:boolean-fromintsign` pops the top `:integer` item, and pushes `true` if it positive, or `false` if it is zero or negative"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromfloat
  (core/build-instruction
    boolean-fromfloat
    "`:boolean-fromfloat` pops the top `:float` item, and pushes `false` if it is 0.0, or `true` if it is any other value"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromfloatsign
  (core/build-instruction
    boolean-fromfloatsign
    "`:boolean-fromfloatsign` pops the top `:float` item, and pushes `true` if it positive, or `false` if it is zero or negative"
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
    "`:boolean-or` pops the top two `:boolean` items, and pushes `true` if exactly one of them is `true`, or `false` otherwise"
    :boolean "xor" 'xor2))


(def bool-not 
  (t/simple-1-in-1-out-instruction
  "`:bool_not returns the logical negation of the top item on the `:boolean`
  stack"
  :boolean "not" 'not))


(def classic-boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        make-visible 
        make-equatable
        make-movable
        print/make-printable
        make-returnable
        (t/attach-instruction , bool-and)
        (t/attach-instruction , boolean-frominteger)
        (t/attach-instruction , boolean-fromintsign)
        (t/attach-instruction , boolean-fromfloat)
        (t/attach-instruction , boolean-fromfloatsign)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-xor)
        ))

