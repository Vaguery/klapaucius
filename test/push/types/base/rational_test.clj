(ns push.types.base.rational_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.rational])
  )


(fact "rational-type has :name ':rational'"
  (:name rational-type) => :rational)


(fact "rational-type has the correct :recognizer"
  (:recognizer (:router rational-type)) => (exactly rational?))


(fact "rational-type has at least some of the expected :attributes"
  (:attributes rational-type) =>
    (contains #{:equatable :comparable :movable :numeric :visible}))


(fact "rational-type knows the :equatable instructions"
  (keys (:instructions rational-type)) =>
    (contains [:rational-equal? :rational-notequal?] :in-any-order :gaps-ok))


(fact "rational-type knows the :visible instructions"
  (keys (:instructions rational-type)) =>
    (contains [:rational-stackdepth :rational-empty?] :in-any-order :gaps-ok))


(fact "rational-type knows the :movable instructions"
  (keys (:instructions rational-type)) =>
    (contains [:rational-shove :rational-pop :rational-dup :rational-rotate :rational-yank :rational-yankdup :rational-flush :rational-swap] :in-any-order :gaps-ok))


(fact "rational-type knows the :printable instructions"
  (keys (:instructions rational-type)) => (contains [:rational-print]))


(fact "rational-type knows the :returnable instructions"
  (keys (:instructions rational-type)) => (contains [:rational-return]))
