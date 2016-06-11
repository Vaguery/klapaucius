(ns push.types.base.integer_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.integer])
  )


(fact "integer-type has :name ':integer'"
  (:name integer-type) => :integer)


(fact "integer-type has the correct :recognizer"
  ((:recognizer (:router integer-type)) 9) => true
  ((:recognizer (:router integer-type)) (+ Long/MIN_VALUE 1)) => true
  ((:recognizer (:router integer-type)) Long/MAX_VALUE) => true
  ((:recognizer (:router integer-type)) (inc' Long/MAX_VALUE)) => false
  ((:recognizer (:router integer-type)) 9999999999999999999999999979976126726) => false
  ((:recognizer (:router integer-type)) false) => false
  ((:recognizer (:router integer-type)) 3.0) => false
  ((:recognizer (:router integer-type)) 3.1) => false
  ((:recognizer (:router integer-type)) [3]) => false
  )


(fact "integer-type has the expected :attributes"
  (:attributes integer-type) =>
    (contains #{:comparable :equatable :movable :numeric :visible}))


(fact "integer-type knows the :comparable instructions"
  (keys (:instructions integer-type)) =>
  (contains [:integer-max :integer>? :integer≤? :integer<? :integer-min :integer≥?] :in-any-order :gaps-ok))


(fact "integer-type knows the :equatable instructions"
  (keys (:instructions integer-type)) =>
  (contains [:integer-equal? :integer-notequal?] :in-any-order :gaps-ok))


(fact "integer-type knows the :visible instructions"
  (keys (:instructions integer-type)) =>
  (contains [:integer-stackdepth :integer-empty?] :in-any-order :gaps-ok))


(fact "integer-type knows the :movable instructions"
  (keys (:instructions integer-type)) =>
  (contains [:integer-shove :integer-pop :integer-dup :integer-rotate :integer-yank :integer-yankdup :integer-flush :integer-swap] :in-any-order :gaps-ok))


(fact "integer-type knows arithmetic"
  (keys (:instructions integer-type)) =>
  (contains [:integer-add :integer-subtract :integer-multiply :integer-divide] :in-any-order :gaps-ok))


(fact "integer-type knows the :printable instructions"
  (keys (:instructions integer-type)) => (contains [:integer-print]))


(fact "integer-type knows the :returnable instructions"
  (keys (:instructions integer-type)) => (contains [:integer-return]))
