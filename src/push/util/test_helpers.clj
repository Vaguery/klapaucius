(ns push.util.test-helpers
  (:use midje.sweet)
  (:require [push.interpreter.core              :as i]
            [push.interpreter.templates.minimum :as m]
            [push.util.stack-manipulation       :as u]
            [push.core                          :as push]
            ))


;; convenience functions for testing

(def starting-interpreter
  (push/interpreter))


(def basic-only
  (m/basic-interpreter))

(defn step-and-check-it
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named `instruction`, executes that instruction to produce
  the next step after, and returns the indicated `get-stack`"
  [setup-stack items instruction read-stack]
  (let [setup (-> starting-interpreter
                  (u/set-stack , setup-stack items)
                  (i/register-instruction , instruction)
                  (assoc , :current-item (:token instruction)))
        after (i/execute-instruction setup (:token instruction))]
    (u/get-stack after read-stack)
    ))


(defn register-type-and-check-instruction
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns
  the indicated `get-stack`"
  [setup-stack items type-under-test instruction-token read-stack]
  (let [setup (-> starting-interpreter
                  (u/set-stack , setup-stack items)
                  (i/register-type , type-under-test)
                  (assoc , :current-item instruction-token))
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
  (let [setup (-> base-interpreter
                  (i/register-type , type-under-test)
                  (u/set-stack , setup-stack items)
                  (assoc , :current-item instruction-token))
        after (i/execute-instruction setup instruction-token)]
    (u/get-stack after read-stack)
    ))



(defn check-instruction-with-all-kinds-of-stack-stuff
  "helper sets up an interpreter with a hash-map of stacks and items,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns all
  the stacks of the resulting Interpreter state"
  [new-stacks type-under-test instruction-token]
  (let [setup (-> basic-only
                  (i/register-type , type-under-test)
                  (assoc , :current-item instruction-token))
        old-stacks (:stacks setup)
        with-stacks (assoc setup :stacks (merge old-stacks new-stacks))
        after (i/execute-instruction with-stacks instruction-token)]
    (:stacks after)))


(defn check-instruction-here-using-this
  "takes an interpreter, a map of stacks to impose on that, and a token to execute"
  [interpreter
    new-stacks instruction-token]
  (let [setup (assoc interpreter :current-item instruction-token)
        old-stacks (:stacks setup)
        with-stacks (assoc setup :stacks (merge old-stacks new-stacks))
        after (i/execute-instruction with-stacks instruction-token)]
    (:stacks after)))



(defn config-read
  "returns the value in the Interpreter's `:config` map under the indicated keyword"
  [interpreter keyword]
  (get-in interpreter [:config keyword]))
