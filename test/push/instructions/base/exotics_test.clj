(ns push.instructions.base.exotics_test
  (:require [push.interpreter.core :as i]
            [push.util.numerics :as num])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.exotics])
  )



(fact "rewrite-digits produces reasonable new integers for integer args"
  (rewrite-digits 12345 3) => 69208
  (rewrite-digits 63119988 3) => 5196527
  (rewrite-digits 8 1) => 8
  (rewrite-digits 8 2) => 6
  (rewrite-digits 8 3) => 4
  (rewrite-digits -63119988 3) => -5196527
  (rewrite-digits -50000 3) => -50055
  (rewrite-digits Long/MIN_VALUE 3) => -3783295979768903679N
  )
