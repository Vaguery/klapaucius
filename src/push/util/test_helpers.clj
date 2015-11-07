(ns push.util.test-helpers
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.base.integer])
  (:use [push.instructions.base.conversion])
  (:use [push.instructions.base.boolean]))


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
