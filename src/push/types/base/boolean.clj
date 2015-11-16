(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use [push.util.type-checkers :only (boolean?)])
  )


(def boolean-frominteger
  (core/build-instruction
    boolean-frominteger
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromintsign
  (core/build-instruction
    boolean-fromintsign
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromfloat
  (core/build-instruction
    boolean-fromfloat
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(not (zero? %1)) :as :result)
    (d/push-onto :boolean :result)))


(def boolean-fromfloatsign
  (core/build-instruction
    boolean-fromfloatsign
    :tags #{:boolean :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(not (neg? %1)) :as :result)
    (d/push-onto :boolean :result)))


(defn xor2 [p q] (or (and p (not q)) (and q (not p))))


(def bool-and
  (t/simple-2-in-1-out-instruction :boolean "and" 'and))

  
(def bool-or
  (t/simple-2-in-1-out-instruction :boolean "or" 'or))


(def bool-xor
  (t/simple-2-in-1-out-instruction :boolean "xor" 'xor2))


(def bool-not 
  (t/simple-1-in-1-out-instruction
  "`:bool_not returns the logical negation of the top item on the `:boolean`
  stack"
  :boolean "not" 'not))


(def classic-boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , bool-and)
        (t/attach-instruction , boolean-frominteger)
        (t/attach-instruction , boolean-fromintsign)
        (t/attach-instruction , boolean-fromfloat)
        (t/attach-instruction , boolean-fromfloatsign)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-xor)
        ))

