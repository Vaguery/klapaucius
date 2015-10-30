(ns push.interpreter.interpreter-acceptance-test
  (:use midje.sweet)
  (:require [push.instructions.instructions-core :as i])
  (:use [push.instructions.dsl])
  (:use [push.interpreter.interpreter-core]))
