(ns push.type.extra.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t]
            [push.util.numerics :as n])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.definitions.tagspace])
  (:use [push.type.item.tagspace])
  )


(fact "tagspace-type knows some instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-dup :tagspace-print] :in-any-order :gaps-ok))


(fact ":tagspace type has the expected :attributes"
  (:attributes tagspace-type) =>
    (contains #{:collection :equatable :movable :printable :returnable :visible}))


(fact "tagspace-type knows the :equatable instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-equal? :tagspace-notequal?] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :visible instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-stackdepth :tagspace-empty?] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :movable instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-shove :tagspace-pop :tagspace-dup :tagspace-rotate :tagspace-yank :tagspace-yankdup :tagspace-flush :tagspace-swap] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :printable instructions"
  (keys (:instructions tagspace-type)) => (contains [:tagspace-print]))


(fact "tagspace-type knows the :returnable instructions"
  (keys (:instructions tagspace-type)) => (contains [:tagspace-return]))


(fact "make-tagspace"
  (:contents (make-tagspace)) => {}
  (:contents (make-tagspace {8 :a 2 :b})) => {2 :b 8 :a}
  )

(fact "store-in-tagspace"
  (:contents (-> (store-in-tagspace (make-tagspace) "foo" 77)
                   (store-in-tagspace , "bar" 22))) => {22 "bar", 77 "foo"})



(fact "find-in-tagspace works for exact matches"
  (let [xy (make-tagspace {7 :a 2 :b 1 :c})]
    (:contents xy) => {1 :c 2 :b, 7 :a}
    (find-in-tagspace xy 2) => :b
    (find-in-tagspace xy 7) => :a
    (find-in-tagspace xy 1) => :c
    ))


(fact "find-in-tagspace rounds up for inexact matches within its range"
  (let [xy (make-tagspace {7 :c 2 :b 1 :a 13 :d})] ;; note order now
    (find-in-tagspace xy 1.1) => :b
    (find-in-tagspace xy 2.1) => :c
    (find-in-tagspace xy 7.1) => :d
    ))

(fact "avrage-gap produces the range divided by the count of gaps `(dec (count keys))`"
  (average-gap
    (make-tagspace {1 :a 2 :b 7 :c 13 :d})) => 4
  (average-gap
    (make-tagspace {1 :a 2 :b 111 :c})) => 55
  (average-gap
    (make-tagspace {1e-8M :a 2 :b 1e8M :c})) => (/ (- 1e8M 1e-8M) 2)
  )


(fact "average-gap produces 0 for empty tagspace"
  (average-gap (make-tagspace)) => 0
  )

(fact "average-gap produces 0 for one-key tagspace"
  (average-gap (make-tagspace {99 :a})) => 0)

(fact "average-gap produces 0 for tagspaces with infinite keys"
  (average-gap (make-tagspace {n/∞ :a 18 :b})) => 0
  (average-gap (make-tagspace {n/-∞ :a 18 :b})) => 0
  (average-gap (make-tagspace {n/-∞ :a n/∞ :b})) => 0
  )

(fact "average-gap works for cases with rational/bignum clashes"
    (average-gap (make-tagspace {-1M :a 3 :b  4 :c 9 :d})) =>
      (with-precision 100 (average-gap (make-tagspace {-1M :a 3 :b  4 :c 9 :d})))
  )

(fact "modded-index beings the index into the ts range plus average gapsize"
  (let [ts (make-tagspace {1 :a 2 :b 7 :c 13 :d})]
    (modded-index ts 12) => 12
    (modded-index ts 16) => 16
    (modded-index ts 17) => 1
    (modded-index ts 16.5) => 16.5
    (modded-index ts 18) => 2
    (modded-index ts 19) => 3

    (modded-index ts -1) => 15
    (modded-index ts -3) => 13
    (modded-index ts -8) => 8
    (modded-index ts 1e11) => 16.0
    (modded-index ts 111N) => 15N
    ))

(fact "modded-index works for oddly-shaped tagspaces"
  (modded-index (make-tagspace {1 :a}) 12) => 12
  (modded-index (make-tagspace {1 :a n/∞ :b}) 12) => 12
  (modded-index (make-tagspace {n/-∞ :a n/∞ :b}) 12) => 12
  )

(fact "modded-index works with empty tagspaces"
  (modded-index (make-tagspace) 12) => 12
  )

(fact "modded-index works for unusual index numbers"
  (modded-index (make-tagspace {1 :a 2 :b 3 :c}) n/-∞) => n/-∞
  (modded-index (make-tagspace {1 :a 2 :b 3 :c}) n/∞) => n/∞
  (modded-index (make-tagspace {1 :a n/∞ :b 3 :c}) n/∞) => n/∞
  (modded-index (make-tagspace {1 :a 2 :b 3 :c 5 :d}) 15M) =>
    (with-precision 100 (modded-index (make-tagspace {1 :a 2 :b 3 :c 5 :d}) 15M))
  )


(fact "find-in-tagspace uses its span plus the average gap size to right size keys outside its range"
  (let [xy (make-tagspace {1 :a
                           2 :b
                           7 :c
                          13 :d})] ;; average-gap = 4

    (find-in-tagspace xy 1/2) => :a
    (find-in-tagspace xy 13.1) => :a
    (find-in-tagspace xy 14.1) => :a
    (find-in-tagspace xy 16) => :a
    (find-in-tagspace xy 16.1) => :a
    (find-in-tagspace xy 17) => :a
    (find-in-tagspace xy 17.1) => :b

    (find-in-tagspace xy 1.8) => :b
    (find-in-tagspace xy 3.9) => :c
    (find-in-tagspace xy 35/6) => :c
    (find-in-tagspace xy 81002102002M) => :b
    (find-in-tagspace xy -2123/99) => :d

    (find-in-tagspace (make-tagspace) 88) => nil
    ))


(fact "find-in-tagspace works for single-item tagspaces"
  (let [xy (make-tagspace {8 :x})]
  (find-in-tagspace xy 8) => :x
  (find-in-tagspace xy 7) => :x
  (find-in-tagspace xy 9) => :x
  (find-in-tagspace xy -1e88M) => :x
  (find-in-tagspace xy 1e88M) => :x
  ))


(fact "find-in-tagspace works for empty tagspace items"
  (find-in-tagspace (make-tagspace) 88) => nil
  )


(fact "find-in-tagspace is actually protected against typeclash errors"
  (let [xy (make-tagspace {7M 11})
        yx (make-tagspace {1/3 11})]
    (find-in-tagspace xy 1/3) => 11
    (find-in-tagspace yx 7M) => 11
    ))



(fact "find-in-tagspace has no trouble being mapped as a result of this protection"
  (let [xy (make-tagspace {7M 11})]
    (map #(find-in-tagspace xy %) [1 9]) => [11 11]
    (map #(find-in-tagspace xy %) [1 9/7]) => '(11 11)
    ))




(fact "tagspace-dissoc removes an item by specific key"
  (tagspace-dissoc (make-tagspace {7 :x 2 :y 1 :z}) 2) =>
    (make-tagspace {7 :x 1 :z})
  (tagspace-dissoc (make-tagspace {7 :x 2 :y 1 :z}) 7) =>
    (make-tagspace {2 :y 1 :z})
  (tagspace-dissoc (make-tagspace {7 :x 2 :y 1 :z}) 5) =>
    (make-tagspace {7 :x 2 :y 1 :z})
  )
