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


(fact "indices-of-item-in-vector"
  (indices-of-item-in-vector [1 3 2 3 1] 2) => [2]
  (indices-of-item-in-vector [1 3 2 3 1] 1) => [0 4]
  (indices-of-item-in-vector [1 3 2 3 1] 3) => [1 3]
  (indices-of-item-in-vector [1 3 2 3 1] 99) => []
  )


(fact "vector->order"
  (vector->order [1 2 3 4 5]) => [0 1 2 3 4]
  (vector->order [5 2 1 3 4]) => [4 1 0 2 3]
  (vector->order [5 2 1 3 1]) => [3 1 0 2 0]
  (vector->order [9 3 -2.1 -2.1 9 0]) => [3 2 0 0 3 1]
  (vector->order [\e \w \E \W \a \>]) => [4 5 1 2 3 0]
  (vector->order ["bob" "" "alice" "charlie" "aardman" ""]) => [3 0 2 4 1 0]
  (vector->order [false true false]) => [0 1 0]
  (vector->order [ [1 2] [3 4] [2 4]]) => [0 2 1]
  (vector->order [:foo :bar :baz]) => [2 0 1]
  (vector->order (seq "188462712887162487634823487")) => [0 6 6 3 4 1 5 0 1 6 6 5 0 4 1 3 6 5 4 2 3 6 1 2 3 6 5]
  (vector->order [#{1 2} #{2 3} #{}]) => (throws)
  (vector->order []) => []
  )
