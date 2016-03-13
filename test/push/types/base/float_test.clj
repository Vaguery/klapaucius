(ns push.types.base.float_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.float])
  )


(fact "float-type has :name ':float'"
  (:name float-type) => :float)


(fact "float-type has the correct :recognizer"
  (:recognizer float-type) => (exactly float?))


(fact "float-type has the expected :attributes"
  (:attributes float-type) =>
    (contains #{:equatable :comparable :movable :numeric :visible}))


(fact "float-type knows the :equatable instructions"
  (keys (:instructions float-type)) =>
    (contains [:float-equal? :float-notequal?] :in-any-order :gaps-ok))


(fact "float-type knows the :visible instructions"
  (keys (:instructions float-type)) =>
    (contains [:float-stackdepth :float-empty?] :in-any-order :gaps-ok))


(fact "float-type knows the :movable instructions"
  (keys (:instructions float-type)) =>
    (contains [:float-shove :float-pop :float-dup :float-rotate :float-yank :float-yankdup :float-flush :float-swap] :in-any-order :gaps-ok))


(fact "float-type knows the :printable instructions"
  (keys (:instructions float-type)) => (contains [:float-print]))


(fact "float-type knows the :returnable instructions"
  (keys (:instructions float-type)) => (contains [:float-return]))
