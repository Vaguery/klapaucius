(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use [push.util.type-checkers :only (boolean?)])
  )


(defn xor2 [p q] (or (and p (not q)) (and q (not p))))


(defn basic-boolean-instruction
  "returns a standard :boolean arity-2 function"
  [operation]
  (let [instruction-name (str "boolean-" operation)]
    (eval (list
      'core/build-instruction
      instruction-name
      :tags #{:logic :base}
      '(d/consume-top-of :boolean :as :arg1)
      '(d/consume-top-of :boolean :as :arg2)
      `(d/calculate [:arg1 :arg2] #(~operation %1 %2) :as :result)
      '(d/push-onto :boolean :result)))))


(def bool-and (basic-boolean-instruction 'and))

  
(def bool-or (basic-boolean-instruction 'or))


(def bool-xor (basic-boolean-instruction 'xor2))


(def bool-not
  (core/build-instruction
    boolean-not
    :tags #{:logic :base}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(not %1) :as :nope)
    (d/push-onto :boolean :nope)))


(def classic-boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , bool-and)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-xor)
        ))

