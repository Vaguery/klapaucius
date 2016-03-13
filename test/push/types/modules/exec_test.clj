(ns push.types.modules.exec_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.module.exec])
  )



(fact "exec-module has :name ':exec'"
  (:name exec-module) => :exec)


(fact "exec-module has the expected :attributes"
  (:attributes exec-module) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "exec-module knows the :equatable instructions"
  (keys (:instructions exec-module)) =>
    (contains [:exec-equal? :exec-notequal?] :in-any-order :gaps-ok))


(fact "exec-module does NOT know any :comparable instructions"
  (keys (:instructions exec-module)) =not=> (contains [:exec<?]))


(fact "exec-module knows the :visible instructions"
  (keys (:instructions exec-module)) =>
    (contains [:exec-stackdepth :exec-empty?] :in-any-order :gaps-ok))


(fact "exec-module knows the :movable instructions"
  (keys (:instructions exec-module)) =>
    (contains [:exec-shove :exec-pop :exec-dup :exec-rotate :exec-yank :exec-yankdup :exec-flush :exec-swap] :in-any-order :gaps-ok))


(fact "exec-module knows the :printable instructions"
  (keys (:instructions exec-module)) => (contains [:exec-print]))


(fact "exec-module knows the :returnable instructions"
  (keys (:instructions exec-module)) => (contains [:exec-return]))
