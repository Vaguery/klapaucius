(ns push.types.base.string_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.string])
  )


(fact "classic-string-type has :stackname ':string'"
  (:stackname classic-string-type) => :string)


(fact "classic-string-type has the correct :recognizer"
  (:recognizer classic-string-type) => (exactly string?))


(fact "classic-string-type has the expected :attributes"
  (:attributes classic-string-type) =>
    (contains #{:equatable :comparable :movable :string :visible}))


(fact "classic-string-type knows the :equatable instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-equal? :string-notequal?] :in-any-order :gaps-ok))


(fact "classic-string-type knows the :visible instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-stackdepth :string-empty?] :in-any-order :gaps-ok))


(fact "classic-string-type knows the :movable instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-shove :string-pop :string-dup :string-rotate :string-yank :string-yankdup :string-flush :string-swap] :in-any-order :gaps-ok))


(future-fact "classic-string-type knows a few things about string"
  (keys (:instructions classic-string-type)) =>
    (contains [] :in-any-order :gaps-ok))
