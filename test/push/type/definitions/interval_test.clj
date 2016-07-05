(ns push.type.definitions.interval_test
  (:use midje.sweet)
  (:use [push.type.definitions.interval])
  )


(fact "span? recognizes a Span item"
  (span? 99) => false
  (span? (make-span 1 1)) => true
  )



(fact "make-span returns a Span record with closed bounds"
  (make-span 9 6) => (->Span 9 6 false false)
  (make-span 1 1) => (->Span 1 1 false false)
  )



(fact "make-span can take optional keyworded open bounds arguments"
  (make-span 9 6 :start-open? true) => (->Span 9 6 true false)
  (make-span 1 1 :end-open? true) => (->Span 1 1 false true)
  (make-span 2 3 :start-open? true :end-open? true) => (->Span 2 3 true true)
  )



(fact "Span getters work as expected"
  (:start (make-span 3 4)) => 3
  (:end (make-span 3 4)) => 4
  (:start-open? (make-span 3 4)) => false
  )



(fact "make-open-span works as expected"
  (:start (make-open-span 3 4)) => 3
  (:end (make-open-span 3 4)) => 4
  (:start-open? (make-open-span 3 4)) => true
  (:end-open? (make-open-span 3 4)) => true
  )


(fact "span-empty? works as expected"
  (span-empty? (make-open-span 3 4)) => false
  (span-empty? (make-span 3 3)) => false
  (span-empty? (make-span 3 3 :start-open? true)) => true
  )


(fact "span-orientation works as expected"
  (span-orientation (make-span 3 4)) => 1
  (span-orientation (make-span 4 3)) => -1
  (span-orientation (make-span 4 4)) => 0
  (span-orientation (make-span 4 4 :start-open? true)) => 0
  )


(fact "span-reverse works as expected"
  (span-reverse (make-span 4 3)) => (make-span 3 4)
  (span-reverse (make-span 4 3 :end-open? true)) => (make-span 3 4 :start-open? true)
  (span-reverse (make-span 4 3 :start-open? true)) => (make-span 3 4 :end-open? true)
  (span-reverse (make-open-span 4 3)) => (make-open-span 3 4)
  )


(fact "span-coincide? detects equality in either direction"
  (span-coincide? (make-span 3 4) (make-span 4 3)) => true
  (span-coincide? (make-span 3 4) (make-span 4 3 :start-open? true)) => false
  (span-coincide? (make-span 3 4) (make-span 3 4 :start-open? true)) => false
  (span-coincide? (make-span 3 4 :end-open? true)
                  (make-span 4 3 :start-open? true)) => true)


(fact "span-include? works for closed spans"
  (span-include? (make-span 3 4) 3.5) => true
  (span-include? (make-span 3 4) 3) => true
  (span-include? (make-span 3 4) 4) => true
  (span-include? (make-span 3 4) 2) => false
  (span-include? (make-span 3 4) 7) => false

  (span-include? (make-span 4 3) 3.5) => true
  (span-include? (make-span 4 3) 3) => true
  (span-include? (make-span 4 3) 4) => true
  (span-include? (make-span 4 3) 2) => false
  (span-include? (make-span 4 3) 7) => false
  )


(fact "span-include? works for open spans"
  (span-include? (make-open-span 3 4) 3.5) => true
  (span-include? (make-open-span 3 4) 3) => false
  (span-include? (make-open-span 3 4) 4) => false
  (span-include? (make-open-span 3 4) 2) => false
  (span-include? (make-open-span 3 4) 7) => false

  (span-include? (make-open-span 4 3) 3.5) => true
  (span-include? (make-open-span 4 3) 3) => false
  (span-include? (make-open-span 4 3) 4) => false
  (span-include? (make-open-span 4 3) 2) => false
  (span-include? (make-open-span 4 3) 7) => false
  )


(fact "span-include? works for empty spans"
  (span-include? (make-open-span 3 3) 3) => false
  (span-include? (make-open-span 3 3) 4) => false
  (span-include? (make-open-span 3 3) 2) => false
  )


(fact "span-include? works for partially open spans"
  (span-include? (make-span 3 4 :end-open? true) 3) => true
  (span-include? (make-span 3 4 :end-open? true) 4) => false
  )


(fact "span-include? works for point spans"
  (span-include? (make-span 3 3) 3) => true
  (span-include? (make-span 3 3) 4) => false
  )



(fact "span-overlap? works as expected for closed spans"
  (span-overlap? (make-span 3 5) (make-span 4 6)) => true
  (span-overlap? (make-span 3 5) (make-span 2 4)) => true
  (span-overlap? (make-span 3 5) (make-span 17 77)) => false
  )


(fact "span-overlap? works as expected for nested spans"
  (span-overlap? (make-span 3 8) (make-span 4 6)) => true
  (span-overlap? (make-span 3 4) (make-span 2 8)) => true
  )


(fact "span-overlap? can notice touching-but-not-overlapping open spans"
  (span-overlap? (make-open-span 3 4) (make-span 4 6)) => false
  (span-overlap? (make-span 4 3) (make-open-span 2 3)) => false
  (span-overlap? (make-span 4 3) (make-span 2 3)) => true
  )


(fact "span-overlap? works as expected for overlapping open spans"
  (span-overlap? (make-open-span 3 8) (make-open-span 4 6)) => true
  (span-overlap? (make-open-span 3 8) (make-span 4 6)) => true
  (span-overlap? (make-span 3 8) (make-open-span 4 6)) => true
  (span-overlap? (make-open-span 3 4) (make-open-span 2 8)) => true
  (span-overlap? (make-open-span 3 4) (make-span 2 8)) => true
  (span-overlap? (make-span 3 4) (make-open-span 2 8)) => true
  )


(fact "span-overlap? works as expected for nested open spans"
  (span-overlap? (make-span 3 8) (make-open-span 4 6)) => true
  (span-overlap? (make-open-span 3 4) (make-span 2 8)) => true
  )


(fact "span-overlap? works for identical spans"
  (span-overlap? (make-span 3 4) (make-span 3 4)) => true
  (span-overlap? (make-span 3 4) (make-span 4 3)) => true
  (span-overlap? (make-open-span 3 4) (make-open-span 3 4)) => true
  )


(fact "span-overlap? works when an open end overlaps another"
  (span-overlap? (make-open-span 3 5) (make-open-span 4 6)) => true
  )


(fact "span-surrounds? works as it should"
  (span-surrounds? (make-span 2 5) (make-span 3 4)) => true
  (span-surrounds? (make-span 2 5) (make-open-span 3 4)) => true
  (span-surrounds? (make-span 3 4) (make-span 3 4)) => true
  (span-surrounds? (make-open-span 3 4) (make-span 3 4)) => false
  (span-surrounds? (make-open-span 3 4) (make-open-span 3 4)) => true
  (span-surrounds? (make-span 3 4 :start-open? true) (make-span 3 4)) => false

  (span-surrounds? (make-span 3 4 :start-open? true) (make-open-span 3 4)) => true
  (span-surrounds? (make-span 3 4 :end-open? true) (make-open-span 4 3)) => true
  )
