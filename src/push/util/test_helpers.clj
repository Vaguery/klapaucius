(ns push.util.test-helpers
  (:use midje.sweet)
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.base.integer]))


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
