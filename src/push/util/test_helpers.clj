(ns push.util.test-helpers
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u]
            [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m]
            [push.core :as push]
            )
  )


;; convenience functions for testing


(defn step-and-check-it
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named `instruction`, executes that instruction to produce
  the next step after, and returns the indicated `get-stack`"
  [setup-stack items instruction read-stack]
  (let [setup (i/register-instruction
                (u/set-stack (push/interpreter) setup-stack items)
                instruction)
        after (i/execute-instruction setup (:token instruction))]
    (u/get-stack after read-stack)
    ))


(defn register-type-and-check-instruction
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns 
  the indicated `get-stack`"
  [setup-stack items type-under-test instruction-token read-stack]
  (let [setup (u/set-stack 
                (i/register-type
                  (push/interpreter)
                  type-under-test)
                setup-stack items)
        after (i/execute-instruction setup instruction-token)]
    (u/get-stack after read-stack)
    ))


(defn register-type-and-check-instruction-in-this-interpreter
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns 
  the indicated `get-stack`"
  [base-interpreter
    setup-stack items type-under-test instruction-token read-stack]
  (let [setup (u/set-stack 
                (i/register-type
                  base-interpreter
                  type-under-test)
                setup-stack items)
        after (i/execute-instruction setup instruction-token)]
    (u/get-stack after read-stack)
    ))



(defn check-instruction-with-all-kinds-of-stack-stuff
  "helper sets up an interpreter with a hash-map of stacks and items,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns all
  the stacks of the resulting Interpreter state"
  [new-stacks type-under-test instruction-token]
  (let [setup (i/register-type (m/basic-interpreter) type-under-test)
        old-stacks (:stacks setup)
        with-stacks (assoc setup :stacks (merge old-stacks new-stacks))
        after (i/execute-instruction with-stacks instruction-token)]
    (:stacks after)))


(defn check-instruction-here-using-this
  "takes an interpreter, a map of stacks to impose on that, and a token to execute"
  [interpreter
    new-stacks instruction-token]
  (let [setup interpreter
        old-stacks (:stacks setup)
        with-stacks (assoc setup :stacks (merge old-stacks new-stacks))
        after (i/execute-instruction with-stacks instruction-token)]
    (:stacks after)))
