(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use [push.util.type-checkers :only (boolean?)])
  )


(defn xor2 [p q] (or (and p (not q)) (and q (not p))))


(def bool-and
  (t/simple-2-in-1-out-instruction :boolean "and" 'and))

  
(def bool-or
  (t/simple-2-in-1-out-instruction :boolean "or" 'or))


(def bool-xor
  (t/simple-2-in-1-out-instruction :boolean "xor" 'xor2))


(def bool-not 
  (t/simple-1-in-1-out-instruction :boolean "not" 'not))


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

