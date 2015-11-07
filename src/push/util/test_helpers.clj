(ns push.util.test-helpers
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  )


;; convenience functions for testing


(defn step-and-check-it
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named `instruction`, executes that instruction to produce
  the next step after, and returns the indicated `get-stack`"
  [setup-stack items instruction read-stack]
  (let [setup (i/register-instruction
                (i/set-stack (i/make-interpreter) setup-stack items)
                instruction)
        after (i/execute-instruction setup (:token instruction))]
    (i/get-stack after read-stack)
    ))


(defn register-type-and-check-instruction
  "helper sets up an interpreter with `items` on `setup-stack`,
  registers the named type (with all instructions loaded as a matter of course),
  executes the named instruction to produce the next step after, and returns 
  the indicated `get-stack`"
  [setup-stack items type-under-test instruction-token read-stack]
  (let [setup (i/set-stack 
                (i/register-type
                  (i/make-interpreter)
                  type-under-test)
                setup-stack items)
        after (i/execute-instruction setup instruction-token)]
    (i/get-stack after read-stack)
    ))
