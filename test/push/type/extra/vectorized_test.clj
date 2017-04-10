(ns push.type.extra.vectorized_test
  (:require [push.type.item.scalar :as s]
            [push.type.item.complex :as cplx])
  (:use midje.sweet)
  (:require [push.type.core :as core])
  (:use [push.util.test-helpers])
  (:use [push.type.item.vectorized])
  )


(def vector-of-scalars (build-vectorized-type s/scalar-type))
(def vector-of-complexes (build-vectorized-type cplx/complex-type))



(fact "I can make a vector type out of :scalar"
  (:name vector-of-scalars) => :scalars)



(fact "the :scalars type has the correct :recognizer"
  (core/recognize? vector-of-scalars [1 2 3]) => true
  (core/recognize? vector-of-scalars 99) => false
  (core/recognize? vector-of-scalars [1 2.2 3]) => true
  (core/recognize? vector-of-scalars [1/2 3e-4 5.6M 7]) => true
  (core/recognize? vector-of-scalars '(1 2 3)) => false
  (core/recognize? vector-of-scalars []) => false
  )

(fact ":scalars type has the expected :attributes"
  (:attributes vector-of-scalars) =>
    (contains #{:equatable :movable :vector :visible}))


(fact "vector-of-scalars knows the :equatable instructions"
  (keys (:instructions vector-of-scalars)) =>
    (contains [:scalars-equal? :scalars-notequal?] :in-any-order :gaps-ok))


(fact "vector-of-scalars knows the :visible instructions"
  (keys (:instructions vector-of-scalars)) =>
    (contains [:scalars-stackdepth :scalars-empty?] :in-any-order :gaps-ok))


(fact "vector-of-scalars knows the :movable instructions"
  (keys (:instructions vector-of-scalars)) =>
    (contains [:scalars-shove :scalars-pop :scalars-dup :scalars-rotate :scalars-yank :scalars-yankdup :scalars-flush :scalars-swap] :in-any-order :gaps-ok))


(fact "vector-of-scalars knows the :printable instructions"
  (keys (:instructions vector-of-scalars)) => (contains [:scalars-print]))


(fact "vector-of-scalars knows the :returnable instructions"
  (keys (:instructions vector-of-scalars)) => (contains [:scalars-return]))


(fact "replacefirst helper substitutes an item at the first position it occurs (from a bug fix)"
  (replacefirst [1 2 3 4 3 2 1] 2 99) => [1 99 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 1] 88 99) => [1 2 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 1] 1 99) => [99 2 3 4 3 2 1]
  (replacefirst [1 2 3 4 3 2 7] 7 99) => [1 2 3 4 3 2 99]
  (replacefirst [1 2 3 4 3 2 7] 7 [6 6]) => [1 2 3 4 3 2 [6 6]] ;; it's generic
  )


(fact "x-sort-instruction only gets added to sortable root types"
  (keys (:instructions vector-of-scalars)) => (contains :scalars-sort)
  (keys (:instructions vector-of-complexes)) =not=> (contains :complexes-sort)
  )

(fact "x-order-instruction only gets added to sortable root types"
  (keys (:instructions vector-of-scalars)) => (contains :scalars-order)
  (keys (:instructions vector-of-complexes)) =not=> (contains :complexes-order)
  )
  
