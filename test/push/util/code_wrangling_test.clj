(ns push.util.code-wrangling-test
  (:use midje.sweet)
  (:use push.util.code-wrangling)
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
  (count-collection-points :yup) => 1)


(fact "`count-collection-points` counts map keys and values"
  (count-collection-points {:a 7}) => 4 ;; map, tuple, key, value
  (count-collection-points {:a 7 :b 11}) => 7 
  (count-collection-points {[1 2] {:a 7 :b 11} [3 [5 [7]]] {[1 2] :c}}) => 25)


(fact "`count-collection-points` counts sets"
  (count-collection-points #{}) => 1
  (count-collection-points #{1 2 3}) => 4
  (count-collection-points #{1 2 #{3 4 5}}) => 7)


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
  (replace-in-code '(1 (2 3) (4 (1 2) 3) 4) 3 '(3 3 3)) => '(1 (2 (3 3 3)) (4 (1 2) (3 3 3)) 4))


(future-fact "`replace-in-code` doesn't get mixed up about vectors"
  (replace-in-code '(1 (2 [1 2] 3) (4 (1 2) 3) 4) '(1 2) 99) => '(1 (2 [1 2] 3) (4 99 3) 4)
)