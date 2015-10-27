(ns push.instructions.instructions-core
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.dsl]))


(defrecord Instruction [token needs makes transaction])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [needs makes transaction] 
    :or { needs {}
          makes {}
          transaction identity }}]
  (->Instruction token needs makes transaction))


(defmacro build-instruction
  "Takes a token and zero or more transaction steps, and
  creates the named instruction from those steps."
  [new-name & transactions]
  `(make-instruction (keyword ~(name new-name))
      :needs ~(total-needs transactions)
      :transaction (def-function-from-dsl ~@transactions))
  )

