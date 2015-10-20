(ns push.integration-test
  (:use midje.sweet)
  (:use [push.interpreter]))

; (future-fact "simple scripts containing literals sort them as expected" :integration
;   (all-stacks (run-script [1 2.3 false \T "foo" '(1 2 3)])) => 
;     { :integer '(1)
;       :float '(2.3)
;       :boolean '(false)
;       :char '(\T)
;       :string '("foo")
;       :code '( '(1 2 3) )}
;   )