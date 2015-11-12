(ns push.instructions.modules.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.modules.code])
  )


(fact "classic-code-module has :name ':code'"
  (:name classic-code-module) => :code)


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
