(ns push.core
  (:require [push.interpreter.core :as i])
  (:require [push.interpreter.templates.one-with-everything :as owe]))


;; functions that need to be exposed

;; interpreters:
;; new interpreter from template (classic, minimum, o.w.e.)
;; step interpreter
;; run interpreter
;; then-run function (load new code, run that)
;; interrogate interpreter state
;; define new interpreter template

;; instructions:
;; write new instruction
;; register instruction in interpreter instance
;; remove instructions from interpreter instance

;; types:
;; define new type
;; register new type in interpreter instance
;; remove type from interpreter instance


(defn interpreter
  "Creates a new Push interpreter and returns it."
  []
  (i/reset-interpreter (owe/make-everything-interpreter)))


(defn known-instructions
  "Given an interpreter, returns a list of the keywords linked to defined instructions in that particular instance"
  [interpreter]
  (keys (:instructions interpreter)))


(defn types-and-modules
  "Given an interpreter, returns a list of the types and modules known to that instance"
  [interpreter]
  (map :name (:types interpreter)))


(defn routing-list
  "Given an interpreter, returns the list of items recognized by that particular instance's router"
  [interpreter]
  (map second (:router interpreter)))


(defn run
  "Creates a new Push interpreter, using that to run the specified program for the specified number of steps. Uses :one-with-everything as a default template; other templates can be specified with the optional :template keyword argument; :input bindings can be specified (either in vector or map format) using the optional :input keyword argument."
  [program steps]
  (i/run-n
    (owe/make-everything-interpreter
      :program program
      :config {:step-limit steps})
    steps))


(defn get-stack
  "returns a named stack from a given interpreter"
  [interpreter stackname]
  (stackname (:stacks interpreter)))



