(ns push.types.base.integer_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.integer])
  )


(fact "classic-integer-type has :name ':integer'"
  (:name classic-integer-type) => :integer)


(fact "classic-integer-type has the correct :recognizer"
  (:recognizer classic-integer-type) => (exactly integer?))


(fact "classic-integer-type has the expected :attributes"
  (:attributes classic-integer-type) =>
    (contains #{:comparable :equatable :movable :numeric :visible}))


(fact "classic-integer-type knows the :comparable instructions"
  (keys (:instructions classic-integer-type)) =>
  (contains [:integer-max :integer>? :integer≤? :integer<? :integer-min :integer≥?] :in-any-order :gaps-ok))


(fact "classic-integer-type knows the :equatable instructions"
  (keys (:instructions classic-integer-type)) =>
  (contains [:integer-equal? :integer-notequal?] :in-any-order :gaps-ok))


(fact "classic-integer-type knows the :visible instructions"
  (keys (:instructions classic-integer-type)) =>
  (contains [:integer-stackdepth :integer-empty?] :in-any-order :gaps-ok))


(fact "classic-integer-type knows the :movable instructions"
  (keys (:instructions classic-integer-type)) =>
  (contains [:integer-shove :integer-pop :integer-dup :integer-rotate :integer-yank :integer-yankdup :integer-flush :integer-swap] :in-any-order :gaps-ok))


(fact "classic-integer-type knows arithmetic"
  (keys (:instructions classic-integer-type)) =>
  (contains [:integer-add :integer-subtract :integer-multiply :integer-divide] :in-any-order :gaps-ok))


(fact "classic-integer-type knows the :printable instructions"
  (keys (:instructions classic-integer-type)) => (contains [:integer-print]))


(fact "classic-integer-type knows the :returnable instructions"
  (keys (:instructions classic-integer-type)) => (contains [:integer-return]))
