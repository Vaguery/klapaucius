(ns push.instructions.base.behavior_test
  (:require [push.interpreter.core :as i]
            [push.util.stack-manipulation :as s]
            [push.type.core :as t]
            [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.module.behavior])
  )



(fact ":push-quoterefs turns on the interpreter's :quote-refs? flag"
  (let [no (push/interpreter)]
    (get-in no [:config :quote-refs?]) => nil
    (get-in
      (i/execute-instruction no :push-quoterefs)
      [:config :quote-refs?]) => true))



(fact ":push-unquoterefs turns off the interpreter's :quote-refs? flag"
  (let [yes (assoc-in
              (push/interpreter)
              [:config :quote-refs?]
              true)]
    (get-in yes [:config :quote-refs?]) => true
    (get-in
      (i/execute-instruction yes :push-unquoterefs)
      [:config :quote-refs?]) => false
    ))



(fact ":push-storeARGS turns on the interpreter's :store-args? flag"
  (let [no (push/interpreter :config {:store-args? false})]
    (get-in no [:config :store-args?]) => false
    (get-in
      (i/execute-instruction no :push-storeARGS)
      [:config :store-args?]) => true))



(fact ":push-nostoreARGS turns off the interpreter's :store-args? flag"
  (let [yes (assoc-in
      (push/interpreter)
      [:config :store-args?]
      true)]
    (get-in yes [:config :store-args?]) => true
    (get-in
      (i/execute-instruction yes :push-nostoreARGS)
      [:config :store-args?]) => false
    ))



(fact ":push-cycleARGS turns on the interpreter's :cycle-args? flag"
  (let [no (push/interpreter :config {:cycle-args? false})]
    (get-in no [:config :cycle-args?]) => false
    (get-in
      (i/execute-instruction no :push-cycleARGS)
      [:config :cycle-args?]) => true))
