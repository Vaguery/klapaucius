(ns demo.examples.plane-geometry.test
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  (:require [push.interpreter.core :as core])
  (:require [push.util.stack-manipulation :as u])
  (:require [clojure.math.numeric-tower :as math])
  (:use demo.examples.plane-geometry.definitions)
  (:import  [org.apfloat Apfloat ApfloatMath])

  )


;;; Apfloat details

(fact "I can compare and manipulate Apfloat items meaningfully, so I can compare geometric items"
  (.equals (apf 2.000002) (apf 2.000003)) => false
  (= 2.000002 2.000003) => false
  ;; NOTE: (= (apf 2.000002) (apf 2.000003)) => true
  (.subtract (apf 2.000002) (apf 2.000003)) => (apf 0.000001)
  (- (apf 2.000002) (apf 2.000003)) => (apf 0.000001)
  (.multiply (apf 2.000002) (apf 2.000003)) => (apf 4.000010000006)
  ;; NOTE: (* (apf 2.000002) (apf 2.000003)) => 4
  )


(fact "apf? checks for Apfloat type"
  (apf? 3) => false
  (apf? (apf 3)) => true
)

;; pretty-much-equal?

(fact "pretty-much-equal? uses rounding to the specified precision to compare apf numbers"
  (.subtract
    (intercept (make-line (make-point 3.3 9.9) (make-point 0 0)))
    (intercept (make-line (make-point 0 0) (make-point 3.3 9.9)))) => (apf 4e-99)

  (pretty-much-equal?
    (intercept (make-line (make-point 3.3 9.9) (make-point 0 0)))
    (intercept (make-line (make-point 0 0) (make-point 3.3 9.9)))) => true)



;; constructors

(fact "I can make and recognize instances of :points, :lines and :circles"
  ((:recognizer push-point) (make-point 3 9))         => true
  ((:recognizer push-point) 9912)                     => false

  ((:recognizer push-line)
     (make-line (make-point 2 9) (make-point 2 1)))   => true
  ((:recognizer push-line)
     (make-point 2 9))                                => false

  ((:recognizer push-circle)
     (make-circle (make-point 2 9) (make-point 2 1))) => true
  ((:recognizer push-circle)
     (make-point 2 9))                                => false
  ((:recognizer push-circle)
     (make-line (make-point 2 9) (make-point 2 1)))   => false)


(fact "make-line-from-xyxy"
  (make-line-from-xyxy 0 1 2 3) => (make-line (make-point 0 1) (make-point 2 3))
  (make-line-from-xyxy 0 1 0 1) => (throws #"points must differ")
  )


(fact "`make-line` throws an Exception if both point arguments are the same"
  (make-line (make-point 1 2) (make-point 1 2)) => (throws #"points must differ"))



(fact "`make-circle` throws an Exception if both point arguments are the same"
  (make-circle (make-point 1 2) (make-point 1 2)) => (throws #"points must differ")
  (make-circle (make-point 1 2.000002) (make-point 1 2.000003)) =not=> (throws)
  )



; ;;; helpers

; ;; line slopes


(fact "slope works as expected"
  (slope (make-line (make-point 2.0 2.0) (make-point 1.0 1.0))) => (apf 1.0)
  (slope (make-line (make-point 1.0 0.0) (make-point 0.0 1.0))) => (apf -1.0)
  (slope (make-line (make-point 1.0 3.0) (make-point 2.0 1.0))) => (apf -2.0)
  (slope (make-line (make-point 2.0 1.0) (make-point 1.0 3.0))) => (apf -2.0)
  (slope (make-line (make-point 1.0 1.0) (make-point 0.0 1.0))) => (apf 0.0))


(fact "slope returns the keyword :infinity if the line is vertical"
  (slope (make-line (make-point 0.0 0.0) (make-point 0.0 1.0))) => :infinity)


(fact "slope can detect subtle differences"
  (< 0.0 (.subtract
    (slope (make-line (make-point 3.111 2.9128) (make-point 0 0))) 
    (slope (make-line (make-point 0 0) (make-point 3.11100000001 2.9128))))) => true)


; ;; line intercepts

(fact "intercept works as expected"
  (intercept (make-line (make-point 2.0 2.0) (make-point 1.0 1.0))) => (apf 0.0)
  (intercept (make-line (make-point 1.0 0.0) (make-point 0.0 1.0))) => (apf 1.0)
  (intercept (make-line (make-point 1.0 0.0) (make-point -1.0 -1.0))) => (apf -0.5)
  (intercept (make-line (make-point 1.0 3.0) (make-point 2.0 1.0))) => (apf 5.0)
  (intercept (make-line (make-point 2.0 1.0) (make-point 1.0 3.0))) => (apf 5.0)
  (intercept (make-line (make-point 1.0 1.0) (make-point 0.0 1.0))) => (apf 1.0))

(fact "intercept works for horizontal lines"
  (intercept (make-line-from-xyxy 0  2 5  2)) => 2)

(fact "intercept returns nil if the line is vertical"
  (intercept (make-line (make-point 0.0 0.0) (make-point 0.0 1.0))) => nil)


(fact "intercept can detect subtle differences"
  (< 0.0
    (.subtract
      (intercept (make-line (make-point 129 333) (make-point 88 0.00001)))
      (intercept (make-line (make-point 88 0) (make-point 129 333))))) => true)



; ;; lines-coincide?


(fact "`lines-coincide?` should detect 'reversed' lines"
  (lines-coincide?
    (make-line (make-point 0 2) (make-point 0 11))
    (make-line (make-point 0 11) (make-point 0 2))) => true

  (pretty-much-equal?
    (slope (make-line (make-point 3.3 9.9) (make-point 0 0)))
    (slope (make-line (make-point 0 0) (make-point 3.3 9.9)))) => true

  (pretty-much-equal?
    (intercept (make-line (make-point 3.3 9.9) (make-point 0 0)))
    (intercept (make-line (make-point 0 0) (make-point 3.3 9.9)))) => true

  (and
    (pretty-much-equal?
      (slope (make-line (make-point 3.3 9.9) (make-point 0 0)))
      (slope (make-line (make-point 0 0) (make-point 3.3 9.9))))
    (pretty-much-equal?
      (intercept (make-line (make-point 3.3 9.9) (make-point 0 0)))
      (intercept (make-line (make-point 0 0) (make-point 3.3 9.9))))) => true
    

  (lines-coincide?
    (make-line (make-point 3.3 9.9) (make-point 0 0))
    (make-line (make-point 0 0) (make-point 3.3 9.9))) => true)


(fact "`lines-coincide?` should be able to detect subtle differences"
  (lines-coincide?
    (make-line (make-point 0 2) (make-point 7 11))
    (make-line (make-point 7 11) (make-point 0 2.0000001))) => false)


(fact "`lines-coincide?` works for vertical lines"
  (lines-coincide?
    (make-line (make-point 0 2) (make-point 0 11))
    (make-line (make-point 0 11) (make-point 0 3))) => true
  (lines-coincide?
    (make-line (make-point 0 2) (make-point 0 11))
    (make-line (make-point 1 11) (make-point 1 3))) => false)


(fact "`lines-coincide?` isn't thrown off by type rounding errors"
  ;; this came up along the way

  (pretty-much-equal? (apf 0.0) (apf 5.0)) => false

  (vertical? (make-line (make-point 0.0 5.0) (make-point 1.0 10.0))) => false

  (lines-coincide?
    (make-line (make-point 0.0 5.0) (make-point 1.0 10.0))
    (make-line (make-point 1.0 10.0) (make-point 2.0 15.0))) => true
  (lines-coincide?
    (make-line (make-point 0 2) (make-point 10 7))
    (make-line (make-point 10 7) (make-point 20 12))) => true



  )


(facts "lines-coincide? works for horizontal lines"
  (lines-coincide?
    (make-line-from-xyxy 0  2 5  2)
    (make-line-from-xyxy 0 11 5 11)) => false
  (pretty-much-equal?
    (slope (make-line-from-xyxy 0  2 5  2))
    (slope (make-line-from-xyxy 0 11 5 11))) => true
  (intercept (make-line-from-xyxy 0  2 5  2)) => 2
  (intercept (make-line-from-xyxy 0 11 5 11)) => 11
  (pretty-much-equal? (apf 2) (apf 11))

  )

; ;; lines-are-parallel?


(fact "`lines-are-parallel?` works for vertical, horizontal and angled lines"
  (lines-are-parallel?
    (make-line-from-xyxy 0  2 0 11)
    (make-line-from-xyxy 5 11 5  2)) => true
  (pretty-much-equal?
    (slope (make-line-from-xyxy 0  2 5  2))
    (slope (make-line-from-xyxy 0 11 5 11))) => true
  (pretty-much-equal?
    (intercept (make-line-from-xyxy 0  2 5  2))
    (intercept (make-line-from-xyxy 0 11 5 11))) => false
  (lines-are-parallel?
    (make-line-from-xyxy 0  2 5  2)
    (make-line-from-xyxy 0 11 5 11)) => true
  (lines-are-parallel?
    (make-line-from-xyxy 0 2 8 22)
    (make-line-from-xyxy 1 3 9 23)) => true
)


(fact "`lines-are-parallel?` doesn't detect identical, coincident or skew lines"
  (lines-are-parallel?
    (make-line (make-point 0 2) (make-point 0 11))
    (make-line (make-point 0 2) (make-point 0 11))) => false
  (lines-are-parallel?
    (make-line (make-point 0 2) (make-point 5 2))
    (make-line (make-point 0 11) (make-point 5 2))) => false
  (lines-are-parallel?
    (make-line (make-point 0 2) (make-point 8 22.0001))
    (make-line (make-point 1 3) (make-point 9 23))) => false)


; ;; crossing-point (of lines)


(def line1 (make-line-from-xyxy 0.0  1.0  1.0 2.0))
(def line2 (make-line-from-xyxy 0.0 10.0  5.0 0.0))
(def line3 (make-line-from-xyxy 6.0 10.0  6.0 0.0))
(def line4 (make-line-from-xyxy 0.0  5.0 10.0 5.0))


(facts "about line-relation"
  (line-relation
    (make-line-from-xyxy 0 1 2 3)
    (make-line-from-xyxy 0 1 2 3)) => :equal
  (line-relation
    (make-line-from-xyxy 0 1 2 3)
    (make-line-from-xyxy 2 3 0 1)) => :coincident
  (line-relation
    (make-line-from-xyxy 0 1 2 3)
    (make-line-from-xyxy 1 1 3 3)) => :parallel
  (line-relation
    (make-line-from-xyxy 0 1 2 3)
    (make-line-from-xyxy 9 2 7 3)) => :intersecting
  (line-relation
    (make-line-from-xyxy 0 1 0 12)
    (make-line-from-xyxy 2 1 2 12)) => :parallel
  (line-relation
    (make-line-from-xyxy 0 1 6 1)
    (make-line-from-xyxy 2 2 8 2)) => :parallel
  (line-relation
    (make-line-from-xyxy 0 1 0 12)
    (make-line-from-xyxy 0 12 0 1)) => :coincident
  (line-relation
    (make-line-from-xyxy 0 1 6 1)
    (make-line-from-xyxy 6 1 0 1)) => :coincident
  (line-relation
    (make-line-from-xyxy 0 1 6 1)
    (make-line-from-xyxy 3 7 3 1)) => :intersecting
  (line-relation
    (make-line-from-xyxy 3 7 3 1)
    (make-line-from-xyxy 0 1 6 1)) => :intersecting
)


(fact "crossing-point returns nil if they don't intersect"
  (crossing-point line1 line1) => nil
  (crossing-point line3 (make-line (make-point 3.0 0.0) (make-point 3.0 12.0))) => nil
  (crossing-point line1 (make-line (make-point -1.0 0.0) (make-point 0.0 1.0))) => nil
  (crossing-point line4 (make-line (make-point 0.0 0.0) (make-point 3.0 0.0))) => nil
  )


(fact "crossing-point returns the point where lines cross, if they do"
  (crossing-point line1 line2) => (make-point 3.0 4.0)
  (crossing-point line1 line3) => (make-point 6.0 7.0)
  (crossing-point line1 line4) => (make-point 4.0 5.0)
  (crossing-point line2 line3) => (make-point 6.0 -2.0)
  (crossing-point line2 line4) => (make-point 2.5 5.0)
  (crossing-point line3 line4) => (make-point 6.0 5.0)
)


(fact "crossing-point works with horizontal and vertical lines"
  (crossing-point
    (make-line-from-xyxy 3 8 3 1)
    (make-line-from-xyxy 6 8 6 1)) => nil
  (crossing-point
    (make-line-from-xyxy 3 8 3 1)
    (make-line-from-xyxy 7 2 3 2)) => (make-point 3.0 2.0)
  (crossing-point
    (make-line-from-xyxy 7 2 3 2)
    (make-line-from-xyxy 3 8 3 1)) => (make-point 3.0 2.0)
)

(fact "crossing-point is pretty accurate even for small divergences"
  (crossing-point line1 (make-line (make-point 0.0 1.0) (make-point 112230.0 112231.0001))) => (make-point 0.0 1.0))



; ;; circles

(fact "make-circle-from-xyxy"
  (:origin (make-circle-from-xyxy 0 1 2 3)) => (make-point 0 1)
  (:edgepoint (make-circle-from-xyxy 0 1 2 3)) => (make-point 2 3))

(fact "make-circle-from-xyxy"
  (make-circle-from-xyxy 0 1 0 1) => (throws))


; ;; distance


(fact "about distance"
  (distance-between (make-point 0 0) (make-point 0 12)) => (roughly 12.0)
  (distance-between (make-point 0 0) (make-point 0 0)) => (roughly 0.0)
  (distance-between (make-point 0 0) (make-point 3 4)) => (roughly 5.0)
  (distance-between (make-point 0 0) (make-point 5 12)) => (roughly 13.0)
  (distance-between (make-point 1 1) (make-point 6 13)) => (roughly 13.0)
  (distance-between (make-point -1 -2) (make-point 4 10)) => (roughly 13.0)
  (distance-between (make-point 0 0) (make-point -12 -5)) => (roughly 13.0)
  )


; ;; radius


(fact "radius works"
  (pretty-much-equal?
    (apf 2.0)
    (radius (make-circle-from-xyxy 0 0 2 0))) => true
  (pretty-much-equal?
    (apf 2.0)
    (radius (make-circle-from-xyxy 2 0 4 0))) => true
  (radius (make-circle-from-xyxy 0 0 9 9)) => (roughly (* 9 (math/sqrt 2))))


; ;; helpers


(fact "circles-coincide?"
  (circles-coincide?
    (make-circle-from-xyxy 0 0 7 0)
    (make-circle-from-xyxy 0 0 0 7)) => true
  (circles-coincide?
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 4 3)) => true
  (circles-coincide?
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 4 3.001)) => false
  (circles-coincide?
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 4 3.00001)) => false
  (circles-coincide?
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 4 3.0000001)) => false
  (circles-coincide?
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 4 3.000000001)) => false)


(fact "circles-tangent?"
  (circles-tangent?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 0 3 0)) => true
  (circles-tangent?
    (make-circle-from-xyxy 0.0 0.0 5.0 5.0)
    (make-circle-from-xyxy 195.0 195.0 5.0 5.0)) => true
  (circles-tangent?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 6 0 3 0)) => true
  (circles-tangent?
    (make-circle-from-xyxy 0 0 3 3)
    (make-circle-from-xyxy 6 6 3 3)) => true
  (circles-tangent?
    (make-circle-from-xyxy 0 0 1 1)
    (make-circle-from-xyxy 0 0 2 2)) => false
  (circles-tangent?
    (make-circle-from-xyxy 0 0 1 1)
    (make-circle-from-xyxy 1 1 2 2)) => false
  (circles-tangent?
    (make-circle-from-xyxy 0 0 1 1)
    (make-circle-from-xyxy 12 12 11 11)) => false)



(fact "these should work (edge cases found in testing)"
  (circles-tangent?
    (make-circle-from-xyxy 0.0 0.0 13.0 13.0)
    (make-circle-from-xyxy 195.0 195.0 13.0 13.0)) => true
  (circles-tangent?
    (make-circle-from-xyxy -301.27 -301.27 3.0 3.0)
    (make-circle-from-xyxy 195.0 195.0 3.0 3.0)) => true)


(fact "circles-separate?"
  (circles-separate?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 9 9 8 8)) => true
  (circles-separate?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 8 8)) => false
  (circles-separate?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 6 0 3 0)) => false
  (circles-separate?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 3 0)) => false
  (circles-separate?
    (make-circle-from-xyxy 0 0 5 5)
    (make-circle-from-xyxy 1 1 7 7)) => false)




(fact "circles-inside?"
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 2 0)) => true
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 4 0)) => true
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 1 4 0)) => false
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 0 3 0)) => false
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 9 9 3 6)) => false
  (circles-inside?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 2 0)) => true)


(fact "circle-A-contains-B?"
  (circle-A-contains-B?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 2 0)) => true
  (circle-A-contains-B?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 4 0)) => false
  (circle-A-contains-B?
    (make-circle-from-xyxy 0 0 4 0)
    (make-circle-from-xyxy 0 0 3 0)) => true
  (circle-A-contains-B?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 0 3 0)) => false
  (circle-A-contains-B?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 0 2.9999999999999M 0)) => true)


(fact "circles-concentric?"
  (circles-concentric?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 2 0)) => true
  (circles-concentric?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 1 1 0)) => false
  (circles-concentric?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 3 0)) => false)



(fact "circles-intersect?"
  (circles-intersect?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 1 0 3 3)) => true
  (circles-intersect?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 0 0 0 3)) => false
  (circles-intersect?
    (make-circle-from-xyxy 0 0 3 0)
    (make-circle-from-xyxy 6 0 3 0)) => false
  (circles-intersect?
    (make-circle-from-xyxy 0 0 1 1)
    (make-circle-from-xyxy 0 0 2 2)) => false
  (circles-intersect?
    (make-circle-from-xyxy 0.5 0.5 1 1)
    (make-circle-from-xyxy 0 0 2 2)) => false
  (circles-intersect?
    (make-circle-from-xyxy 0 0 1 1)
    (make-circle-from-xyxy 2 2 1 1)) => false)


(fact "circle-intersection-points returns an empty list if they don't touch"
  (circle-intersection-points
    (make-circle-from-xyxy 0 0 10 10)
    (make-circle-from-xyxy 1 1 3  3)) => '()
  (circle-intersection-points
    (make-circle-from-xyxy 0 0 10 10)
    (make-circle-from-xyxy 0 0 33 33)) => '()
  (circle-intersection-points
    (make-circle-from-xyxy 0  0  10 10)
    (make-circle-from-xyxy 22 22 33 33)) => '())



(fact "circle-intersection-points returns a list containing the first circle if they are equal or coincide"
  (first (circle-intersection-points
    (make-circle-from-xyxy 0 0 10 10)
    (make-circle-from-xyxy 0 0 10 10))) => (make-circle-from-xyxy 0 0 10 10)
  (first (circle-intersection-points
    (make-circle-from-xyxy 0 0 3 4)
    (make-circle-from-xyxy 0 0 5 0))) => (make-circle-from-xyxy 0 0 3 4))



(fact "tangent-point returns the tangent point, or nil"
  (pt-equal?
    (tangent-point
      (make-circle-from-xyxy 0  0  10 10)
      (make-circle-from-xyxy 20 20 10 10))
    (make-point 10 10)) => true
  (pt-equal?
    (tangent-point
      (make-circle-from-xyxy 1 1 2 0)
      (make-circle-from-xyxy 17 17 2 32))
    (make-point 2 2)) => true)



(fact "circle-intersection-points returns a list with the one point if they are tangent"
  (pt-equal?
    (first (circle-intersection-points
      (make-circle-from-xyxy 0 0 10 10)
      (make-circle-from-xyxy 20 20 10 10)))
    (make-point 10 10)) => true

  (pt-equal?
    (first (circle-intersection-points
      (make-circle-from-xyxy 1 1 2 2)
      (make-circle-from-xyxy 22 22 2 2)))
      (make-point 2 2)) => true)


(fact "circle-intersection-points returns both points if there are two"
  (circles-intersect?
    (make-circle-from-xyxy 0 0 5 0)
    (make-circle-from-xyxy 6 0 1 0)) => true

  (let [pts (both-circle-intersection-points
              (make-circle-from-xyxy 0 0 5 0)
              (make-circle-from-xyxy 6 0 1 0))]
    (pt-equal? (first pts) (make-point 3 -4)) => true
    (pt-equal? (second pts) (make-point 3 4)) => true)


  (let [pts (both-circle-intersection-points
              (make-circle-from-xyxy 0 0 0.5 0)
              (make-circle-from-xyxy 0.6 0 0.1 0))]
    (pt-equal? (first pts) (make-point 0.3 -0.4)) => true
    (pt-equal? (second pts) (make-point 0.3 0.4)) => true)


  (let [pts (both-circle-intersection-points
              (make-circle-from-xyxy 0 0 10 0)
              (make-circle-from-xyxy 10 0 0 0))
        sqrt3over2 (.divide (ApfloatMath/sqrt (apf 3)) (apf 2))
        y-val (.multiply (apf 10) sqrt3over2)]
    (pt-equal? (first pts) (make-point 5 (.multiply (apf -1) y-val))) => true
    (pt-equal? (second pts) (make-point 5 y-val)) => true))


;; points


(fact "point-in-circle? returns true when the point is inside the circle"
  (point-in-circle? (make-point 1 2) (make-circle-from-xyxy 1 2 3 4)) => true
  (point-in-circle? (make-point 11 12) (make-circle-from-xyxy 1 2 3 4)) => false
  (point-in-circle? (make-point 3 4) (make-circle-from-xyxy 1 2 3 4)) => false
  (point-in-circle? (make-point 0 5) (make-circle-from-xyxy 0 0 3 4)) => false
  (radius (make-circle-from-xyxy 0 0 3 4)) => (apf 5)
  (point-in-circle? (make-point 0 4.9999999999999M) (make-circle-from-xyxy 0 0 3 4)) => true
  (point-in-circle? (make-point 0 5M) (make-circle-from-xyxy 0 0 3 4)) => false)


(fact "point-on-circle? returns true when the point is inside the circle"
  (point-on-circle? (make-point 3 4) (make-circle-from-xyxy 1 2 3 4)) => true
  (point-on-circle? (make-point 0 5M) (make-circle-from-xyxy 0 0 3 4)) => true
  (point-on-circle?
    (make-point 0 4.9999999999999999M)
    (make-circle-from-xyxy 0 0 3 4)) => false
  (point-on-circle?
    (make-point 0 5.000000000000000001M)
    (make-circle-from-xyxy 0 0 3 4)) => false)



(fact "point-on-line? returns true when the point is on the line"
  (point-on-line? (make-point 3 4) (make-line-from-xyxy 1 2 3 4)) => true
  (point-on-line? (make-point 5 6) (make-line-from-xyxy 1 2 3 4)) => true
  (point-on-line? (make-point 5 7) (make-line-from-xyxy 1 2 3 4)) => false
  (point-on-line? (make-point 3.00000000000000000001M 4)
    (make-line-from-xyxy 1 2 3 4)) => false)



;;;; Push types and instructions


;; recognizers work for routing

(def test-interpreter
  (core/register-types 
    (owe/make-everything-interpreter)
    [push-point push-line push-circle]))


(def point-class (class (make-point 0.0 0.0)))


(def line-class (class (make-line (make-point 0.0 0.0) (make-point 1 2))))


(def circle-class (class (make-circle (make-point 0.0 0.0) (make-point 1 2))))

(fact "the stacks are already there (though it doesn't really matter)"
  (keys (:stacks test-interpreter)) => (contains [:circle :line :point] :gaps-ok :any-order))


(fact "items can be routed to those stacks correctly"
  (u/get-stack
    (core/handle-item test-interpreter (make-point 3 9))
    :point) => (list (map->Point {:x 3, :y 9}))

  (u/get-stack
    (core/handle-item test-interpreter (make-line (make-point 2 9) (make-point 2 1)))
    :line) => (list (map->Line { :p1 (map->Point {:x 2, :y 9})
                                 :p2 (map->Point {:x 2, :y 1}) }))

  (u/get-stack
    (core/handle-item test-interpreter (make-circle (make-point 2 9) (make-point 2 1)))
    :circle) => (list (map->Circle {
                    :origin
                      (map->Point {:x 2, :y 9})
                    :edgepoint
                      (map->Point {:x 2, :y 1})})))


;; interpreter executes instructions


(fact "test-interpreter knows :line-intersection"
  (keys (:instructions test-interpreter)) => (contains :line-intersection))


(fact "the intersection ends up on the :point stack"
  (let [prepped (core/recycle-interpreter
                test-interpreter
                [line1 line2 :line-intersection])]
    (:stacks prepped) =>
    `{:boolean (), :booleans (), :char (), :chars (), :circle (), :code (), :environment (), :error (), :exec (~line1 ~line2 :line-intersection), :float (), :floats (), :integer (), :integers (), :line (), :log (), :point (), :print (), :return (), :set (), :string (), :strings (), :unknown (), :vector ()}
    
    (u/get-stack (core/run prepped 1000) :point) => (list (make-point 3.0 4.0))))


