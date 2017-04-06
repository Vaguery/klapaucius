(ns push.type.item.boolean
  ; (:use push.type.item.generator)
  (:require [push.instructions.dsl     :as d]
            [push.instructions.core    :as i]
            [push.instructions.aspects :as aspects]
            [push.util.type-checkers   :as checkers :refer [boolean?]]
            [push.util.exotics         :as exotics]
            [push.type.core            :as types]
            ))

(defn xor2 [p q] (or (and p (not q)) (and q (not p))))

(defn bit [bool] (if bool 1 0))

(def boolean-2bittable
  (i/build-instruction
    boolean-2bittable
    "`:boolean-2bittable` pops the top `:scalar` item and pushes the result of applying `(exotics/scalar-to-truth-table i 2)` to :booleans. If the `:scalar` is not a finite positive number, an empty vector is pushed instead."
    :tags #{:boolean :conversion}
    (d/consume-top-of :scalar :as :value)
    (d/calculate [:value] #(exotics/scalar-to-truth-table %1 2) :as :table)
    (d/push-onto :booleans :table)))


(def boolean-3bittable
  (i/build-instruction
    boolean-3bittable
    "`:boolean-3bittable` pops the top `:scalar` item and pushes the result of applying `(exotics/scalar-to-truth-table i 3)` to :booleans. If the `:scalar` is not a finite positive number, an empty vector is pushed instead."
    :tags #{:boolean :conversion}
    (d/consume-top-of :scalar :as :value)
    (d/calculate [:value] #(exotics/scalar-to-truth-table %1 3) :as :table)
    (d/push-onto :booleans :table)))


(def boolean-arity2
  (i/build-instruction
    boolean-arity2
    "`:boolean-arity2` pops the top `:scalar` item, creates a truth table on 2 inputs using `(exotics/scalar-to-truth-table i 2)`, pops the top two `:boolean` values and uses them to look up a `:boolean` result in the table. If the `:scalar` is infinite, `false` will be returned for all input bits."
    :tags #{:boolean :conversion}
    (d/consume-top-of :scalar :as :value)
    (d/calculate [:value] #(exotics/scalar-to-truth-table %1 2) :as :table)
    (d/consume-top-of :boolean :as :q)
    (d/consume-top-of :boolean :as :p)
    (d/calculate [:p :q] #(+ (bit %1) (* 2 (bit %2))) :as :index)
    (d/calculate [:table :index]
      #(if (empty? %1) false (nth %1 %2)) :as :result)
    (d/push-onto :boolean :result)))



(def boolean-arity3
  (i/build-instruction
    boolean-arity3
    "`:boolean-arity3` pops the top `:scalar` item, creates a truth table on 3 inputs using `(exotics/scalar-to-truth-table i 3)`, pops the top 3 `:boolean` values and uses them to look up a `:boolean` result in the table. If the `:scalar` is infinite, `false` will be returned for all input bits."
    :tags #{:boolean :conversion}
    (d/consume-top-of :scalar :as :value)
    (d/calculate [:value] #(exotics/scalar-to-truth-table %1 3) :as :table)
    (d/consume-top-of :boolean :as :r)
    (d/consume-top-of :boolean :as :q)
    (d/consume-top-of :boolean :as :p)
    (d/calculate [:p :q :r] #(+ (bit %1) (* 2 (bit %2)) (* 4 (bit %3))) :as :index)
    (d/calculate [:table :index]
      #(if (empty? %1) false (nth %1 %2)) :as :result)
    (d/push-onto :boolean :result)))



(def bool-and
  (i/simple-2-in-1-out-instruction
    "`:boolean-and` pops the top two `:boolean` items, and pushes `true` if they're both `true`, `false` otherwise"
    :boolean "and" 'and))



(def bool-not
  (i/simple-1-in-1-out-instruction
  "`:boolean-not returns the logical negation of the top item on the `:boolean`
  stack"
  :boolean "not" 'not))



(def bool-or
  (i/simple-2-in-1-out-instruction
    "`:boolean-or` pops the top two `:boolean` items, and pushes `true` if either one is `true, `false` otherwise"
    :boolean "or" 'or))



(def bool-xor
  (i/simple-2-in-1-out-instruction
    "`:boolean-xor` pops the top two `:boolean` items, and pushes `true` if exactly one of them is `true`, or `false` otherwise"
    :boolean "xor" 'xor2))



(def scalar->boolean
  (i/build-instruction
    scalar->boolean
    "`:scalar->boolean` pops the top `:scalar` item, and pushes `false` if it is zero.0, or `true` if it is any other value"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))



(def scalarsign->boolean
  (i/build-instruction
    scalarsign->boolean
    "`:scalarsign->boolean` pops the top `:scalar` item, and pushes `true` if it positive, or `false` if it is zero or negative"
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-type
  ( ->  (types/make-type  :boolean
                      :recognized-by checkers/boolean?
                      :attributes #{:logical})
        aspects/make-set-able
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (types/attach-instruction , boolean-2bittable)
        (types/attach-instruction , boolean-3bittable)
        (types/attach-instruction , bool-and)
        (types/attach-instruction , boolean-arity2)
        (types/attach-instruction , boolean-arity3)
        (types/attach-instruction , bool-not)
        (types/attach-instruction , bool-or)
        (types/attach-instruction , bool-xor)
        (types/attach-instruction , scalar->boolean)
        (types/attach-instruction , scalarsign->boolean)
        ))
