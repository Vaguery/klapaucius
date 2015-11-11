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


;; contains-anywhere?


(fact "`contains-anywhere?` traverses a collection and checks for presence of an item"
  (contains-anywhere? '(1 2 3) 3) => true
  (contains-anywhere? '(1 2 3) 4) => false
  (contains-anywhere? '(1 2 3 (4)) '(4)) => true
  (contains-anywhere? '(1 2 3 (4)) 4) => true
  (contains-anywhere? '(1 2 3 (4)) nil) => false
  (contains-anywhere? '(1 2 3 (4)) '4) => true)


(fact "`contains-anywhere?` checks in map keys"
  (contains-anywhere? {:a 1 :b 2 :c 3} :a) => true
  (contains-anywhere? {:a 1 :b 22 :c 3} 22) => true
  (contains-anywhere? {:a 1 :b 22 :c 3} 99) => false)


(fact "`contains-anywhere?` does fancy nesting"
  (let [monster {:a [1,2,3] , [4,5,6] :b , :c {:d [7,8,9]} }]
  (contains-anywhere? monster 8) => true
  (contains-anywhere? monster 11) => false
  (contains-anywhere? monster 5) => true
  (contains-anywhere? monster :d) => true
  (contains-anywhere? monster [8,9]) => false))


;; container-in


(future-fact "`container-in` traverses a collection and returns the collection (if any) containing that item"
  (container-in '(1 2 3) 3) => '( (1 2 3) )
  (container-in '(1 2 3) 4) => '())
