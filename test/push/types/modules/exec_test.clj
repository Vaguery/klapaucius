(ns push.types.modules.exec_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.modules.exec])
  )



(fact "classic-exec-module has :name ':exec'"
  (:name classic-exec-module) => :exec)


(fact "classic-exec-module has the expected :attributes"
  (:attributes classic-exec-module) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "classic-exec-module knows the :equatable instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-equal? :exec-notequal?] :in-any-order :gaps-ok))


(fact "classic-exec-module does NOT know any :comparable instructions"
  (keys (:instructions classic-exec-module)) =not=> (contains [:exec<?]))


(fact "classic-exec-module knows the :visible instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-stackdepth :exec-empty?] :in-any-order :gaps-ok))


(fact "classic-exec-module knows the :movable instructions"
  (keys (:instructions classic-exec-module)) =>
    (contains [:exec-shove :exec-pop :exec-dup :exec-rotate :exec-yank :exec-yankdup :exec-flush :exec-swap] :in-any-order :gaps-ok))


(fact "classic-exec-module knows the :printable instructions"
  (keys (:instructions classic-exec-module)) => (contains [:exec-print]))


(fact "classic-exec-module knows the :returnable instructions"
  (keys (:instructions classic-exec-module)) => (contains [:exec-return]))
