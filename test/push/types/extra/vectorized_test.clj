(ns push.types.extra.vectorized_test
  (:require [push.types.type.integer :as int])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.vectorized])
  )


(def vector-of-integers (build-vectorized-type int/integer-type))

(fact "I can make a vector type out of :integer"
  (:name vector-of-integers) => :integers)


(fact "the :integers type has the correct :recognizer"
  (recognize? vector-of-integers [1 2 3]) => true
  (recognize? vector-of-integers 99) => false
  (recognize? vector-of-integers [1 2.2 3]) => false
  (recognize? vector-of-integers '(1 2 3)) => false
  (recognize? vector-of-integers []) => false
  )

(fact ":integers type has the expected :attributes"
  (:attributes vector-of-integers) =>
    (contains #{:equatable :movable :vector :visible}))


(fact "vector-of-integers knows the :equatable instructions"
  (keys (:instructions vector-of-integers)) =>
    (contains [:integers-equal? :integers-notequal?] :in-any-order :gaps-ok))


(fact "vector-of-integers knows the :visible instructions"
  (keys (:instructions vector-of-integers)) =>
    (contains [:integers-stackdepth :integers-empty?] :in-any-order :gaps-ok))


(fact "vector-of-integers knows the :movable instructions"
  (keys (:instructions vector-of-integers)) =>
    (contains [:integers-shove :integers-pop :integers-dup :integers-rotate :integers-yank :integers-yankdup :integers-flush :integers-swap] :in-any-order :gaps-ok))


(fact "vector-of-integers knows the :printable instructions"
  (keys (:instructions vector-of-integers)) => (contains [:integers-print]))


(fact "vector-of-integers knows the :returnable instructions"
  (keys (:instructions vector-of-integers)) => (contains [:integers-return]))


(fact "replacefirst helper substitutes an item at the first position it occurs"
  (replacefirst [1 2 3 4 3 2 1] 2 99) => [1 99 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 1] 88 99) => [1 2 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 1] 1 99) => [99 2 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 7] 7 99) => [1 2 3 4 3 2 99]
  (replacefirst [1 2 3 4 3 2 7] 7 [6 6]) => [1 2 3 4 3 2 [6 6]] ;; it's generic
  )

