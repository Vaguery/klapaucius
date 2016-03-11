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


(fact "make-tagspace"
  (:contents (make-tagspace)) => {}
  (:contents (make-tagspace {8 :a 2 :b})) => {2 :b 8 :a}
  )

(fact "store-in-tagspace"
  (:contents (-> (store-in-tagspace (make-tagspace) "foo" 77)
                   (store-in-tagspace , "bar" 22))) => {22 "bar", 77 "foo"})



(fact "find-in-tagspace"
  (let [xy (make-tagspace {7 :x 2 :y 1 :z})]
    (:contents xy) => {1 :z 2 :y, 7 :x}
    (find-in-tagspace xy 2) => :y
    (find-in-tagspace xy 3) => :x
    (find-in-tagspace xy 7) => :x
    (find-in-tagspace xy 8) => :z
    (find-in-tagspace xy -2123) => :z
    

    (find-in-tagspace xy 1.8) => :y
    (find-in-tagspace xy 3.9) => :x
    (find-in-tagspace xy 35/6) => :x
    (find-in-tagspace xy 81002102002M) => :z
    (find-in-tagspace xy -2123/99) => :z

    (find-in-tagspace (make-tagspace) 88) => nil
    ))


