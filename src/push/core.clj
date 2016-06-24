(ns push.core
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.one-with-everything :as owe]))


;; functions that need to be exposed

;; interpreters:
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



(defn merge-stacks
  "takes an interpreter and a hash-map of stacks, and merges the hash-map with those of the interpreter"
  [interpreter stacks]
  (assoc interpreter :stacks (merge (:stacks interpreter) stacks)))


(defn interpreter
  "Creates a new Push interpreter and returns it. Keyword arguments permit setting the :program, :stacks, :bindings, :instructions, :config, :counter, or :done? flag."
  [& {:keys [program stacks bindings config counter done?]
      :or {program []
           stacks {}
           bindings {}
           config {}
           counter 0
           done? false}}]
    (merge-stacks 
      (i/reset-interpreter
        (owe/make-everything-interpreter
          :program program
          :stacks stacks
          :bindings bindings
          :config config
          :counter counter
          :done? done?))
      stacks))


(defn known-instructions
  "Given an interpreter, returns a list of the keywords linked to defined instructions in that particular instance"
  [interpreter]
  (keys (:instructions interpreter)))


(defn binding-names
  "Given an interpreter, returns a list of the keywords linked to defined bindings in that particular instance"
  [interpreter]
  (keys (:bindings interpreter)))


(defn types-and-modules
  "Given an interpreter, returns a list of the types and modules known to that instance"
  [interpreter]
  (map :name (:types interpreter)))


(defn routing-list
  "Given an interpreter, returns the list of items recognized by that particular instance's router"
  [interpreter]
  (map :name (:routers interpreter)))


(defn run
  "Creates a new Push interpreter, using that to run the specified program for the specified number of steps. Uses :one-with-everything as a default template; :bindings can be specified (only in map format) using the optional :binding keyword argument."
  [interpreter program steps & {:keys [bindings] :or {bindings {}}}]
  (i/run-n
    (-> interpreter
      (assoc :program program)
      (assoc :config (merge (:config interpreter) {:step-limit steps}))
      (i/bind-inputs bindings))
    steps))


(defn get-stack
  "returns a named stack from a given interpreter"
  [interpreter stackname]
  (get-in interpreter [:stacks stackname] '()))



