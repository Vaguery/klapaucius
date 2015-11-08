(ns push.types.base.exec_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.exec])
  )


(fact "classic-exec-type has :stackname ':exec'"
  (:stackname classic-exec-type) => :exec)


(future-fact "classic-exec-type has the correct :recognizer"
  (:recognizer classic-exec-type) => (constantly false))


(fact "classic-exec-type has the expected :attributes"
  (:attributes classic-exec-type) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "classic-exec-type knows the :equatable instructions"
  (keys (:instructions classic-exec-type)) =>
    (contains [:exec-equal? :exec-notequal?] :in-any-order :gaps-ok))


(fact "classic-exec-type does NOT know any :comparable instructions"
  (keys (:instructions classic-exec-type)) =not=> (contains [:exec<?]))


(fact "classic-exec-type knows the :visible instructions"
  (keys (:instructions classic-exec-type)) =>
    (contains [:exec-stackdepth :exec-empty?] :in-any-order :gaps-ok))


(fact "classic-exec-type knows the :movable instructions"
  (keys (:instructions classic-exec-type)) =>
    (contains [:exec-shove :exec-pop :exec-dup :exec-rotate :exec-yank :exec-yankdup :exec-flush :exec-swap] :in-any-order :gaps-ok))


(future-fact "classic-exec-type knows all the :exec-specific stuff"
  (keys (:instructions classic-exec-type)) =>
  (contains [] :in-any-order :gaps-ok))
