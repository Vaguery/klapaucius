(ns push.types.standard.vector_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.standard.vector])
  )


(fact "The :vector type has the right name"
  (:name standard-vector-type) => :vector)


(fact "the :vector type has the correct :recognizer"
  (:recognizer standard-vector-type) => (exactly vector?))


(fact ":vector type has the expected :attributes"
  (:attributes standard-vector-type) =>
    (contains #{:collection :equatable :movable :printable :returnable :visible}))


(fact "standard-vector-type knows the :equatable instructions"
  (keys (:instructions standard-vector-type)) =>
    (contains [:vector-equal? :vector-notequal?] :in-any-order :gaps-ok))


(fact "standard-vector-type knows the :visible instructions"
  (keys (:instructions standard-vector-type)) =>
    (contains [:vector-stackdepth :vector-empty?] :in-any-order :gaps-ok))


(fact "standard-vector-type knows the :movable instructions"
  (keys (:instructions standard-vector-type)) =>
    (contains [:vector-shove :vector-pop :vector-dup :vector-rotate :vector-yank :vector-yankdup :vector-flush :vector-swap] :in-any-order :gaps-ok))


(fact "standard-vector-type knows the :printable instructions"
  (keys (:instructions standard-vector-type)) => (contains [:vector-print]))


(fact "standard-vector-type knows the :returnable instructions"
  (keys (:instructions standard-vector-type)) => (contains [:vector-return]))
