(ns push.types.base.float_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.float])
  )


(fact "classic-float-type has :name ':float'"
  (:name classic-float-type) => :float)


(fact "classic-float-type has the correct :recognizer"
  (:recognizer classic-float-type) => (exactly float?))


(fact "classic-float-type has the expected :attributes"
  (:attributes classic-float-type) =>
    (contains #{:equatable :comparable :movable :numeric :visible}))


(fact "classic-float-type knows the :equatable instructions"
  (keys (:instructions classic-float-type)) =>
    (contains [:float-equal? :float-notequal?] :in-any-order :gaps-ok))


(fact "classic-float-type knows the :visible instructions"
  (keys (:instructions classic-float-type)) =>
    (contains [:float-stackdepth :float-empty?] :in-any-order :gaps-ok))


(fact "classic-float-type knows the :movable instructions"
  (keys (:instructions classic-float-type)) =>
    (contains [:float-shove :float-pop :float-dup :float-rotate :float-yank :float-yankdup :float-flush :float-swap] :in-any-order :gaps-ok))


(fact "classic-float-type knows the :printable instructions"
  (keys (:instructions classic-float-type)) => (contains [:float-print]))


(fact "classic-float-type knows the :returnable instructions"
  (keys (:instructions classic-float-type)) => (contains [:float-return]))
