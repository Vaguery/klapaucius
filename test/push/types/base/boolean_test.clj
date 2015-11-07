(ns push.types.base.boolean_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.boolean])
  (:use [push.util.type-checkers :only (boolean?)])
)


(fact "classic-boolean-type has :stackname ':boolean'"
  (:stackname classic-boolean-type) => :boolean)


(fact "classic-boolean-type has the correct :recognizer"
  (:recognizer classic-boolean-type) => (exactly boolean?))


(fact "classic-boolean-type has the expected :attributes"
  (:attributes classic-boolean-type) =>
    (contains #{:equatable :movable :logical :visible}))


(fact "classic-boolean-type does NOT know any :comparable instructions"
  (keys (:instructions classic-boolean-type)) =not=>
    (contains [:boolean-max :boolean>? :boolean≤? :boolean<? :boolean-min :boolean≥?] :in-any-order :gaps-ok))


(fact "classic-boolean-type knows the :equatable instructions"
  (keys (:instructions classic-boolean-type)) =>
    (contains [:boolean-equal? :boolean-notequal?] :in-any-order :gaps-ok))


(fact "classic-boolean-type knows the :visible instructions"
  (keys (:instructions classic-boolean-type)) =>
    (contains [:boolean-stackdepth :boolean-empty?] :in-any-order :gaps-ok))


(fact "classic-boolean-type knows the :movable instructions"
  (keys (:instructions classic-boolean-type)) =>
    (contains [:boolean-shove :boolean-pop :boolean-dup :boolean-rotate :boolean-yank :boolean-yankdup :boolean-flush :boolean-swap] :in-any-order :gaps-ok))


(fact "classic-boolean-type knows logic"
  (keys (:instructions classic-boolean-type)) =>
  (contains [:boolean-and :boolean-not :boolean-or :boolean-xor] :in-any-order :gaps-ok))
