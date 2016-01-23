(ns push.types.modules.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.modules.code])
  )


(fact "code-module has :name ':code'"
  (:name code-module) => :code)


(fact "code-module has the expected :attributes"
  (:attributes code-module) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "code-module knows the :equatable instructions"
  (keys (:instructions code-module)) =>
    (contains [:code-equal? :code-notequal?] :in-any-order :gaps-ok))


(fact "code-module does NOT know any :comparable instructions"
  (keys (:instructions code-module)) =not=> (contains [:code-max]))


(fact "code-module knows the :visible instructions"
  (keys (:instructions code-module)) =>
    (contains [:code-stackdepth :code-empty?] :in-any-order :gaps-ok))


(fact "code-module knows the :movable instructions"
  (keys (:instructions code-module)) =>
    (contains [:code-shove :code-pop :code-dup :code-rotate :code-yank :code-yankdup :code-flush :code-swap] :in-any-order :gaps-ok))


(fact "code-module knows the :printable instructions"
  (keys (:instructions code-module)) => (contains [:code-print]))


(fact "code-module knows the :returnable instructions"
  (keys (:instructions code-module)) => (contains [:code-return]))
