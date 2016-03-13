(ns push.types.base.boolean_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.type-checkers :only (boolean?)])
  (:use [push.types.type.boolean])
  )


(fact "boolean-type has :name ':boolean'"
  (:name boolean-type) => :boolean)


(fact "boolean-type has the correct :recognizer"
  (:recognizer boolean-type) => (exactly boolean?))


(fact "boolean-type has the expected :attributes"
  (:attributes boolean-type) =>
    (contains #{:equatable :movable :logical :visible}))


(fact "boolean-type does NOT know any :comparable instructions"
  (keys (:instructions boolean-type)) =not=>
    (contains [:boolean-max :boolean>? :boolean≤? :boolean<? :boolean-min :boolean≥?] :in-any-order :gaps-ok))


(fact "boolean-type knows the :equatable instructions"
  (keys (:instructions boolean-type)) =>
    (contains [:boolean-equal? :boolean-notequal?] :in-any-order :gaps-ok))


(fact "boolean-type knows the :visible instructions"
  (keys (:instructions boolean-type)) =>
    (contains [:boolean-stackdepth :boolean-empty?] :in-any-order :gaps-ok))


(fact "boolean-type knows the :movable instructions"
  (keys (:instructions boolean-type)) =>
    (contains [:boolean-shove :boolean-pop :boolean-dup :boolean-rotate :boolean-yank :boolean-yankdup :boolean-flush :boolean-swap] :in-any-order :gaps-ok))


(fact "boolean-type knows logic"
  (keys (:instructions boolean-type)) =>
  (contains [:boolean-and :boolean-not :boolean-or :boolean-xor] :in-any-order :gaps-ok))


(fact "boolean-type knows the :printable instructions"
  (keys (:instructions boolean-type)) => (contains [:boolean-print]))


(fact "boolean-type knows the :returnable instructions"
  (keys (:instructions boolean-type)) => (contains [:boolean-return]))
