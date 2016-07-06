(ns push.type.definitions.interval_test
  (:use midje.sweet)
  (:use [push.type.definitions.interval])
  )


(fact "interval? recognizes an Interval item"
  (interval? 99) => false
  (interval? (make-interval 1 1)) => true
  )



(fact "make-interval returns a interval record with closed and sorted bounds"
  (make-interval 9 6) => (->Interval 6 9 false false)
  (make-interval 9 -6) => (->Interval -6 9 false false)
  (make-interval 1 1) => (->Interval 1 1 false false)
  )



(fact "make-interval can take optional keyworded open bounds arguments"
  (make-interval 9 6 :min-open? true) => (->Interval 6 9 true false)
  (make-interval 1 1 :max-open? true) => (->Interval 1 1 false true)
  (make-interval 2 3 :min-open? true :max-open? true) => (->Interval 2 3 true true)
  )



(fact "Interval getters work as expected"
  (:min (make-interval 3 4)) => 3
  (:max (make-interval 3 4)) => 4
  (:min-open? (make-interval 3 4)) => false
  (:max-open? (make-interval 3 4)) => false
  )



(fact "make-open-interval works as expected"
  (:min (make-open-interval 3 4)) => 3
  (:max (make-open-interval 3 4)) => 4
  (:min-open? (make-open-interval 3 4)) => true
  (:max-open? (make-open-interval 3 4)) => true
  )


(fact "interval-empty? works as expected"
  (interval-empty? (make-open-interval 3 4)) => false
  (interval-empty? (make-interval 3 3)) => false
  (interval-empty? (make-interval 3 3 :min-open? true)) => true
  (interval-empty? (make-interval 7 7 :min-open? true)) => true
  )


(fact "interval-include? works for closed intervals"
  (interval-include? (make-interval 3 4) 3.5) => true
  (interval-include? (make-interval 3 4) 3) => true
  (interval-include? (make-interval 3 4) 4) => true
  (interval-include? (make-interval 3 4) 2) => false
  (interval-include? (make-interval 3 4) 7) => false
  )


(fact "interval-include? works for open intervals"
  (interval-include? (make-open-interval 3 4) 3.5) => true
  (interval-include? (make-open-interval 3 4) 3) => false
  (interval-include? (make-open-interval 3 4) 4) => false
  (interval-include? (make-open-interval 3 4) 2) => false
  (interval-include? (make-open-interval 3 4) 7) => false
  )


(fact "interval-include? works for empty intervals"
  (interval-include? (make-open-interval 3 3) 3) => false
  (interval-include? (make-open-interval 3 3) 4) => false
  (interval-include? (make-open-interval 3 3) 2) => false
  )


(fact "interval-include? works for partially open intervals"
  (interval-include? (make-interval 3 4 :max-open? true) 3) => true
  (interval-include? (make-interval 3 4 :max-open? true) 4) => false
  )


(fact "interval-include? works for point intervals"
  (interval-include? (make-interval 3 3) 3) => true
  (interval-include? (make-interval 3 3) 4) => false
  )



(fact "interval-overlap? works as expected for closed intervals"
  (interval-overlap? (make-interval 3 5) (make-interval 4 6)) => true
  (interval-overlap? (make-interval 3 5) (make-interval 2 4)) => true
  (interval-overlap? (make-interval 3 5) (make-interval 17 77)) => false
  )


(fact "interval-overlap? works as expected for nested intervals"
  (interval-overlap? (make-interval 3 8) (make-interval 4 6)) => true
  (interval-overlap? (make-interval 3 4) (make-interval 2 8)) => true
  )


(fact "interval-overlap? can notice touching-but-not-overlapping open intervals"
  (interval-overlap? (make-open-interval 3 4) (make-interval 4 6)) => false
  (interval-overlap? (make-interval 4 3) (make-open-interval 2 3)) => false
  (interval-overlap? (make-interval 4 3) (make-interval 2 3)) => true
  )


(fact "interval-overlap? works for open/closed differences"
  (interval-overlap?
    (make-open-interval 3 4) (make-interval 3 4)) => true
  (interval-overlap?
    (make-open-interval 2 5) (make-open-interval 3 4)) => true
  )


(fact "interval-overlap? works as expected for overlapping open intervals"
  (interval-overlap? (make-open-interval 3 8) (make-open-interval 4 6)) => true
  (interval-overlap? (make-open-interval 3 8) (make-interval 4 6)) => true
  (interval-overlap? (make-interval 3 8) (make-open-interval 4 6)) => true
  (interval-overlap? (make-open-interval 3 4) (make-open-interval 2 8)) => true
  (interval-overlap? (make-open-interval 3 4) (make-interval 2 8)) => true
  (interval-overlap? (make-interval 3 4) (make-open-interval 2 8)) => true

  (interval-overlap? (make-interval 3 4 :max-open true) (make-interval 3 4)) => true
  (interval-overlap? (make-interval 3 4) (make-interval 3 4 :max-open true) ) => true
  )


(fact "interval-overlap? works as expected for nested open intervals"
  (interval-overlap? (make-interval 3 8) (make-open-interval 4 6)) => true
  (interval-overlap? (make-open-interval 3 4) (make-interval 2 8)) => true
  )


(fact "interval-overlap? works for identical intervals"
  (interval-overlap? (make-interval 3 4) (make-interval 3 4)) => true
  (interval-overlap? (make-interval 3 4) (make-interval 4 3)) => true
  (interval-overlap? (make-open-interval 3 4) (make-open-interval 3 4)) => true
  )


(fact "interval-overlap? works when an open end overlaps another"
  (interval-overlap? (make-open-interval 3 5) (make-open-interval 4 6)) => true
  )


(fact "interval-subset? works as it should"
  (interval-subset?
    (make-interval 2 5) (make-interval 3 4)) => true
  (interval-subset?
    (make-interval 2 5) (make-open-interval 3 4)) => true
  (interval-subset?
    (make-interval 3 4) (make-interval 3 4)) => true
  (interval-subset?
    (make-open-interval 3 4) (make-interval 3 4)) => false
  (interval-subset?
    (make-open-interval 3 4) (make-open-interval 3 4)) => true
  (interval-subset?
    (make-interval 3 4 :min-open? true) (make-interval 3 4)) => false

  (interval-subset?
    (make-interval 3 4 :min-open? true) (make-open-interval 3 4)) => true
  (interval-subset?
    (make-interval 3 4 :max-open? true) (make-open-interval 4 3)) => true
  )
