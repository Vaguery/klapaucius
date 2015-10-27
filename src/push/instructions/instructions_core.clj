(ns push.instructions.instructions-core
  (:require [push.interpreter.interpreter-core :as i]))


(defrecord Instruction [token needs makes transaction])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [needs makes transaction] 
    :or { needs {}
          makes {}
          transaction identity }}]
  (->Instruction token needs makes transaction))

