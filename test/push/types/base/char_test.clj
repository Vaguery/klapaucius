(ns push.types.base.char_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.type-checkers :only (boolean?)])
  (:use [push.types.type.char])
  )


(fact "char-type has :name ':char'"
  (:name char-type) => :char)


(fact "char-type has the correct :recognizer"
  (:recognizer (:router char-type)) => (exactly char?))


(fact "char-type has the expected :attributes"
  (:attributes char-type) =>
    (contains #{:equatable :comparable :movable :string :visible}))


(fact "char-type knows the :equatable instructions"
  (keys (:instructions char-type)) =>
    (contains [:char-equal? :char-notequal?] :in-any-order :gaps-ok))


(fact "char-type knows the :visible instructions"
  (keys (:instructions char-type)) =>
    (contains [:char-stackdepth :char-empty?] :in-any-order :gaps-ok))


(fact "char-type knows the :movable instructions"
  (keys (:instructions char-type)) =>
    (contains [:char-shove :char-pop :char-dup :char-rotate :char-yank :char-yankdup :char-flush :char-swap] :in-any-order :gaps-ok))


(fact "char-type knows the :printable instructions"
  (keys (:instructions char-type)) => (contains [:char-print]))


(fact "char-type knows the :returnable instructions"
  (keys (:instructions char-type)) => (contains [:char-return]))
