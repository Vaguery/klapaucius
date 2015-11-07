(ns push.interpreter.basic
  (:require [push.interpreter.core :as i]))


(defn make-basic-interpreter
  "Creates a new Interpreter record with the core Push types and
  instructions defined. The :router will be loaded in the order
  [:integer, :float, :boolean, :code, :exec]."
  []
  (i/make-interpreter))