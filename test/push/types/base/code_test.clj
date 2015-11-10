(ns push.types.base.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.code])
  )


(fact "classic-code-type has :name ':code'"
  (:name classic-code-type) => :code)


(fact "classic-code-type has the correct :recognizer"
  (:recognizer classic-code-type) => (exactly push-code?))


(facts "about the `push-code?` recognizer for :code items"
  (fact "it returns false for anything unquoted"
    (push-code? 8) => false
    (push-code? false) => false
    (push-code? [1 2 3]) => false)
  (fact "it returns false for single-quoted items, including lists"
    (push-code? '8) => false
    (push-code? 'false) => false
    (push-code? '(1 2 3)) => false)
  (fact "it returns false for double-quoted items, including lists"
    (push-code? ''8) => true
    (push-code? ''false) => true
    (push-code? ''(1 2 3)) => true))


(fact "classic-code-type has the expected :attributes"
  (:attributes classic-code-type) =>
    (contains #{:equatable :movable :complex :visible}))


(fact "classic-code-type knows the :equatable instructions"
  (keys (:instructions classic-code-type)) =>
    (contains [:code-equal? :code-notequal?] :in-any-order :gaps-ok))


(fact "classic-code-type does NOT know any :comparable instructions"
  (keys (:instructions classic-code-type)) =not=> (contains [:code-max]))


(fact "classic-code-type knows the :visible instructions"
  (keys (:instructions classic-code-type)) =>
    (contains [:code-stackdepth :code-empty?] :in-any-order :gaps-ok))


(fact "classic-code-type knows the :movable instructions"
  (keys (:instructions classic-code-type)) =>
    (contains [:code-shove :code-pop :code-dup :code-rotate :code-yank :code-yankdup :code-flush :code-swap] :in-any-order :gaps-ok))


(future-fact "classic-code-type knows all the :code-specific stuff"
  (keys (:instructions classic-code-type)) =>
  (contains [] :in-any-order :gaps-ok))

