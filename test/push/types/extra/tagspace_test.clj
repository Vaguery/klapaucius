(ns push.types.extra.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.extra.tagspace])
  )


(fact "tagspace-type knows some instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-dup :tagspace-print] :in-any-order :gaps-ok))


(fact ":tagspace type has the expected :attributes"
  (:attributes tagspace-type) =>
    (contains #{:collection :equatable :movable :printable :returnable :visible}))


(fact "tagspace-type knows the :equatable instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-equal? :tagspace-notequal?] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :visible instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-stackdepth :tagspace-empty?] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :movable instructions"
  (keys (:instructions tagspace-type)) =>
    (contains [:tagspace-shove :tagspace-pop :tagspace-dup :tagspace-rotate :tagspace-yank :tagspace-yankdup :tagspace-flush :tagspace-swap] :in-any-order :gaps-ok))


(fact "tagspace-type knows the :printable instructions"
  (keys (:instructions tagspace-type)) => (contains [:tagspace-print]))


(fact "tagspace-type knows the :returnable instructions"
  (keys (:instructions tagspace-type)) => (contains [:tagspace-return]))

