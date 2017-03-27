(ns push.util.code-wrangling-test
  (:require [push.util.stack-manipulation :as fix])
  (:use midje.sweet)
  (:use push.util.code-wrangling)
  )

;; utilities for counting nested collection items

(defrecord Foo [a b])

(fact "`branch?` recognizes anything that can contain 'code points'"
  (branch? '(1 2 3)) => true
  (branch? [1 2 3]) => true
  (branch? {:a 7 :b 8}) => true
  (branch? #{1 2 3}) => true
  (branch? (->Foo 1 2)) => true
  (branch? []) => true
  (branch? (sort [1 2 3])) => true
  (branch? (lazy-seq)) => true
  )

(fact "`branch?` returns `false` for non-traversable items"
  (branch? "foo") => false
  (branch? \w) => false
  (branch? 123.4) => false
  (branch? false) => false
  (branch? nil) => false
  )

(fact "`children` returns the children of containers we want to search"
  (children '(1 2 3)) => (seq [1 2 3])
  (children [1 2 3]) => (seq [1 2 3])
  (children {:a 1 :b 2}) => [[:a 1] [:b 2]]
  (children #{1 2}) => (seq [1 2])
  (children (->Foo [1 2] "bar")) => [[:a [1 2]] [:b "bar"]]
  (children "bar") => [\b \a \r]
  )


;; count-collection-points


(fact "`count-collection-points` counts every item in every list and sub-list"
  (count-collection-points '(1 2 3)) => 4
  (count-collection-points '()) => 1
  (count-collection-points '(1 (2 3))) => 5
  (count-collection-points '((1) (2) (3))) => 7
  (count-collection-points '()) => 1
  (count-collection-points '(())) => 2
  (count-collection-points '((((((()))))))) => 7)


(fact "`count-collection-points` counts every item in vectors and sub-vectors"
  (count-collection-points [1 2 3]) => 4
  (count-collection-points []) => 1)


(fact "`count-collection-points` counts items as 1"
  (count-collection-points '()) => 1
  (count-collection-points nil) => 1
  (count-collection-points false) => 1
  (count-collection-points 'integer?) => 1
  (count-collection-points '+) => 1
  (count-collection-points []) => 1
  (count-collection-points {}) => 1
  (count-collection-points #{}) => 1
  (count-collection-points :yup) => 1
  (count-collection-points "foo") => 1
  )


(fact "`count-collection-points` counts map keys and values"
  (count-collection-points {:a 7}) => 4 ;; map, tuple, key, value
  (count-collection-points {:a 7 :b 11}) => 7
  (count-collection-points {[1 2] {:a 7 :b 11} [3 [5 [7]]] {[1 2] :c}}) => 25)


(fact "`count-collection-points` counts sets"
  (count-collection-points #{}) => 1
  (count-collection-points #{1 2 3}) => 4
  (count-collection-points #{1 2 #{3 4 5}}) => 7)


(defrecord Foo [a b c d])


(fact "`count-collection-points` counts records and things in them"
  (vec (->Foo 1 2 3 4)) => [[:a 1] [:b 2] [:c 3] [:d 4]]
  (count-collection-points (->Foo 1 2 3 4)) => 13
  (count-collection-points (->Foo '(1 2 3) 2 3 4)) => 16
  (count-collection-points (->Foo
                             (->Foo 1 2 3 4)
                             (->Foo 1 2 3 4)
                             (->Foo 1 2 3 4)
                             (->Foo 1 2 3 4))) => 61)

;; count-code-points


(fact "`count-code-points` counts every item in every list and sub-list"
  (count-code-points '(1 2 3)) => 4
  (count-code-points '()) => 1
  (count-code-points '(1 (2 3))) => 5
  (count-code-points '((1) (2) (3))) => 7
  (count-code-points '()) => 1
  (count-code-points '(())) => 2
  (count-code-points '((((((()))))))) => 7)


(fact "`count-code-points` ignores vectors and sub-vectors"
  (count-code-points [1 2 3]) => 1
  (count-code-points []) => 1)


(fact "`count-code-points` counts items as 1"
  (count-code-points '()) => 1
  (count-code-points nil) => 0
  (count-code-points false) => 1
  (count-code-points 'integer?) => 1
  (count-code-points '+) => 1
  (count-code-points []) => 1
  (count-code-points {}) => 1
  (count-code-points #{}) => 1
  (count-code-points :yup) => 1)


(fact "`count-code-points` skips over map contents"
  (count-code-points {:a 7}) => 1
  (count-code-points {:a 7 :b 11}) => 1
  (count-code-points {[1 2] {:a 7 :b 11} [3 [5 [7]]] {[1 2] :c}}) => 1

  (count-code-points '(1 2 {:a 7})) => 4)


(fact "`count-code-points` ignores contents of sets"
  (count-code-points #{}) => 1
  (count-code-points #{1 2 3}) => 1
  (count-code-points #{1 2 #{3 4 5}}) => 1
  (count-code-points '( 1 2 #{1 2 #{3 4 5}})) => 4)


(fact "`count-code-points` is OK with nil (but doesn't count it)"
  (count-code-points nil) => 0)

;; nth-code-point


(fact "`nth-code-point` returns the n+1th item of a flat list (because the list is a point!)"
  (nth-code-point '(9 8 7 6 5 4) 2) => 8
  (nth-code-point '(9 8 7 6 5 4) 0) => '(9 8 7 6 5 4)
  (nth-code-point '(9 8 7 6 5 4) 6) => 4)


(fact "`nth-code-point` is OK with non-list arguments, as long as you ask for 0th position"
  (nth-code-point 77 0) => 77
  (nth-code-point [1 2 3] 0) => [1 2 3]
  (nth-code-point #{1 2 3} 0) => #{1 2 3}
  (nth-code-point "[1 2 3]" 0) => "[1 2 3]")


(fact "`nth-code-point` will return the item itself if you exceed its bouns"
  (nth-code-point '(1 2) 112) => '(1 2))


(fact "`nth-code-point` will count vectors as single items"
  (nth-code-point '([1 2] [3 4]) 1) => [1 2]
  (nth-code-point '([1 2] [3 4]) 2) => [3 4])


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


;; containers-in


(fact "`containers-in` traverses a collection and returns the collection (if any) containing that item"
  (containers-in '(1 2 3) 6) => ['()]
  (containers-in '(1 2 3) 2) => ['(1 2 3)]
  (containers-in '(1 2 3) '(1 2 3)) => ['()]

  (containers-in '(1 (2 3)) 2) => ['(2 3)]
  (containers-in '(1 (2 3)) 2) => ['(2 3)]
  (containers-in '(1 (2 3) (2 3 4)) 2) => ['(2 3) '(2 3 4)]
  (containers-in '(1 (2 3) (2 3 4)) 3) => ['(2 3) '(2 3 4)]

  (containers-in '(1 (2 3) ((2) 3 (4 2))) 2) => ['(2 3) '(2) '(4 2)]

  (containers-in '(1 (2 2) ((2) 3 (4 (3 3)))) '(3 3)) => ['(4 (3 3))]
  (containers-in '(1 (2 2) ((2) 3 (4 (3 3)))) '(2)) =>  ['((2) 3 (4 (3 3)))]
  (containers-in '(1 (2 (2)) ((2) 3 (4 (3 3)))) '(2)) =>  ['(2 (2)) '((2) 3 (4 (3 3)))]

  (containers-in '(1 [2 3 4]) [2 3 4]) => ['(1 [2 3 4])]
  ; (containers-in '(1 [2 3 4]) 2) => [[2 3 4]]
  )

;; replace-in-code

(fact "`replace-in-code` does simple stuff"
  (replace-in-code '(1 2 3 4 1 2 3 4) 2 99) => '(1 99 3 4 1 99 3 4)
  (replace-in-code '(1 2 3 4 1 2 3 4) 11 99) => '(1 2 3 4 1 2 3 4))


(fact "`replace-in-code` works on trees"
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) 2 99) => '(1 (99 3) (4 (1 99) 3) 4)
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) 11 99) => '(1 (2 3) (4 (1 2) 3) 4))


(fact "`replace-in-code` works with trees as arguments"
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) '(1 2) 99) => '(1 (2 3) (4 99 3) 4)
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) 4 '(99 99 99)) =>
    '(1 (2 3) ((99 99 99) (1 2) 3) (99 99 99)))


(fact "`replace-in-code` doesn't stumble over recursions"
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) 3 '(3 3 3)) =>
    '(1 (2 (3 3 3)) (4 (1 2) (3 3 3)) 4))


(fact "`replace-in-code` doesn't get mixed up and replace content of vectors"
  (replace-in-code '(1 [1] [1 (1)]) 1 99) =>
    '(99 [1] [1 (1)])
  (replace-in-code '(1 [1] [1 (1)]) '(1) 99) =>
    '(1 [1] [1 (1)])
  (replace-in-code '(1 (2 [1 2] 3) (4 (1 2) 3) 4) '(1 2) 99) =>
    '(1 (2 [1 2] 3) (4 99 3) 4)
    )


;; replace-nth-in-code


(fact "`replace-nth-in-code` does simple stuff"
  (replace-nth-in-code '(1 2 3 4) 99 3) => '(1 2 99 4)
  (replace-nth-in-code '(1 2 3 4) 99 0) => 99
  (replace-nth-in-code '(1 2 3 4) '(9 9 9) 2) => '(1 (9 9 9) 3 4))


(fact "`replace-nth-in-code` gets up in yer tree"
  (let [tree '((1 (2)) (3 () (4)))
        pts (count-code-points tree)]
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 4) => '((1 (99)) (3 () (4)))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 5) => '((1 (2)) 99)
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 6) => '((1 (2)) (99 () (4)))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 7) => '((1 (2)) (3 99 (4)))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 8) => '((1 (2)) (3 (99) (4)))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 9) => '((1 (2)) (3 () 99))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 9) => '((1 (2)) (3 () 99))
  (replace-nth-in-code '((1 (2)) (3 () (4))) 99 pts) => '((1 (2)) (3 () (99)))))



(fact "`replace-nth-in-code` can handle non-lists?"
  (replace-nth-in-code 99 '(77) 0) => '(77)
  (replace-nth-in-code 99 88 0) => 88
  )
