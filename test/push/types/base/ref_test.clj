(ns push.types.base.ref_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.ref])
  )


(fact "ref-type has :name ':ref'"
  (:name ref-type) => :ref)


(future-fact "ref-type has the expected :attributes"
  (:attributes ref-type) =>
    (contains #{:equatable :movable :complex :visible}))


(future-fact "ref-type knows the :equatable instructions"
  (keys (:instructions ref-type)) =>
    (contains [:code-equal? :code-notequal?] :in-any-order :gaps-ok))


(future-fact "ref-type does NOT know any :comparable instructions"
  (keys (:instructions ref-type)) =not=> (contains [:code-max]))


(future-fact "ref-type knows the :visible instructions"
  (keys (:instructions ref-type)) =>
    (contains [:code-stackdepth :code-empty?] :in-any-order :gaps-ok))


(future-fact "ref-type knows the :movable instructions"
  (keys (:instructions ref-type)) =>
    (contains [:code-shove :code-pop :code-dup :code-rotate :code-yank :code-yankdup :code-flush :code-swap] :in-any-order :gaps-ok))


(future-fact "ref-type knows the :printable instructions"
  (keys (:instructions ref-type)) => (contains [:code-print]))


(future-fact "ref-type knows the :returnable instructions"
  (keys (:instructions ref-type)) => (contains [:code-return]))
