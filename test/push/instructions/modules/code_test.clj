(ns push.instructions.modules.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.modules.code])
  )


(fact "classic-code-module has :name ':code'"
  (:name classic-code-module) => :code)


(fact "classic-code-module has the expected :attributes"
  (:attributes classic-code-module) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "classic-code-module knows the :equatable instructions"
  (keys (:instructions classic-code-module)) =>
    (contains [:code-equal? :code-notequal?] :in-any-order :gaps-ok))


(fact "classic-code-module does NOT know any :comparable instructions"
  (keys (:instructions classic-code-module)) =not=> (contains [:code-max]))


(fact "classic-code-module knows the :visible instructions"
  (keys (:instructions classic-code-module)) =>
    (contains [:code-stackdepth :code-empty?] :in-any-order :gaps-ok))


(fact "classic-code-module knows the :movable instructions"
  (keys (:instructions classic-code-module)) =>
    (contains [:code-shove :code-pop :code-dup :code-rotate :code-yank :code-yankdup :code-flush :code-swap] :in-any-order :gaps-ok))


(fact "classic-code-module knows the :printable instructions"
  (keys (:instructions classic-code-module)) => (contains [:code-print]))


(fact "classic-code-module knows the :returnable instructions"
  (keys (:instructions classic-code-module)) => (contains [:code-return]))
