(ns push.util.code-wrangling-test
  (:use midje.sweet)
  (:use push.util.code-wrangling)
  )


;; count-points

(fact "`count-points` counts every item in every list and sub-list"
  (count-points '(1 2 3)) => 4
  (count-points '()) => 1
  (count-points '(1 (2 3))) => 5
  (count-points '((1) (2) (3))) => 7
  (count-points '()) => 1
  (count-points '(())) => 2
  (count-points '((((((()))))))) => 7)


(fact "`count-points` counts every item in vectors and sub-vectors"
  (count-points [1 2 3]) => 4
  (count-points []) => 1)



(fact "`count-points` counts items as 1"
  (count-points '()) => 1
  (count-points nil) => 1
  (count-points false) => 1
  (count-points 'integer?) => 1
  (count-points '+) => 1
  (count-points []) => 1
  (count-points {}) => 1
  (count-points #{}) => 1
  (count-points :yup) => 1)


(fact "`count-points` counts map keys and values"
  (count-points {:a 7}) => 4 ;; map, tuple, key, value
  (count-points {:a 7 :b 11}) => 7 
  (count-points {[1 2] {:a 7 :b 11} [3 [5 [7]]] {[1 2] :c}}) => 25)


(fact "`count-points` counts sets"
  (count-points #{}) => 1
  (count-points #{1 2 3}) => 4
  (count-points #{1 2 #{3 4 5}}) => 7)
