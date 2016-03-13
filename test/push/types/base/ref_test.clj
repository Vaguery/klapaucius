(ns push.types.base.ref_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.ref])
  )


(fact "ref-type has :name ':ref'"
  (:name ref-type) => :ref)


(fact "ref-type has the expected :attributes"
  (:attributes ref-type) =>
    (contains #{:equatable :movable :visible} :in-any-order :gaps-ok))


(fact "ref-type knows the :equatable instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-equal? :ref-notequal?] :in-any-order :gaps-ok))


(fact "ref-type does NOT know any :comparable instructions"
  (keys (:instructions ref-type)) =not=> (contains [:ref-max]))


(fact "ref-type knows the :visible instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-stackdepth :ref-empty?] :in-any-order :gaps-ok))


(fact "ref-type knows the :storable instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-save :ref-store] :in-any-order :gaps-ok))


(fact "ref-type knows the :movable instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-shove :ref-pop :ref-dup :ref-rotate :ref-yank :ref-yankdup :ref-flush :ref-swap] :in-any-order :gaps-ok))


(fact "ref-type knows the :printable instructions"
  (keys (:instructions ref-type)) => (contains [:ref-print]))


(fact "ref-type knows the :returnable instructions"
  (keys (:instructions ref-type)) => (contains [:ref-return]))
