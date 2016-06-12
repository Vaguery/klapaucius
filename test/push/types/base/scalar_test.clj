(ns push.types.base.scalar_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )


(fact "scalar-type has :name ':scalar'"
  (:name scalar-type) => :scalar)


(fact "scalar-type has the correct :recognizer"
  (:recognizer (:router scalar-type)) => (exactly number?))


(fact "scalar-type has at least some of the expected :attributes"
  (:attributes scalar-type) =>
    (contains #{:equatable :comparable :movable :numeric :visible}))


(fact "scalar-type knows the :equatable instructions"
  (keys (:instructions scalar-type)) =>
    (contains [:scalar-equal? :scalar-notequal?] :in-any-order :gaps-ok))


(fact "scalar-type knows the :visible instructions"
  (keys (:instructions scalar-type)) =>
    (contains [:scalar-stackdepth :scalar-empty?] :in-any-order :gaps-ok))


(fact "scalar-type knows the :movable instructions"
  (keys (:instructions scalar-type)) =>
    (contains [:scalar-shove :scalar-pop :scalar-dup :scalar-rotate :scalar-yank :scalar-yankdup :scalar-flush :scalar-swap] :in-any-order :gaps-ok))


(fact "scalar-type knows the :printable instructions"
  (keys (:instructions scalar-type)) => (contains [:scalar-print]))


(fact "scalar-type knows the :returnable instructions"
  (keys (:instructions scalar-type)) => (contains [:scalar-return]))
