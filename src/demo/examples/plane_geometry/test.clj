(ns demo.examples.plane-geometry.test
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  (:require [push.interpreter.core :as core])
  (:require [push.util.stack-manipulation :as u])
  (:use demo.examples.plane-geometry.definitions)
  )

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

(fact "`make-line` throws an Exception if both point arguments are the same"
  (make-line (make-point 1 2) (make-point 1 2)) => (throws #"points must differ"))

(fact "`make-circle` throws an Exception if both point arguments are the same"
  (make-circle (make-point 1 2) (make-point 1 2)) => (throws #"points must differ")
  (make-circle (make-point 1 2.000002) (make-point 1 2.000001)) =not=> (throws))


;;; helpers

;; line slopes


(fact "slope works as expected"
  (slope (make-line (make-point 2.0 2.0) (make-point 1.0 1.0))) => 1.0
  (slope (make-line (make-point 1.0 0.0) (make-point 0.0 1.0))) => -1.0
  (slope (make-line (make-point 1.0 3.0) (make-point 2.0 1.0))) => -2.0
  (slope (make-line (make-point 2.0 1.0) (make-point 1.0 3.0))) => -2.0
  (slope (make-line (make-point 1.0 1.0) (make-point 0.0 1.0))) => 0.0)


(fact "slope returns the keyword :infinity if the line is vertical"
  (slope (make-line (make-point 0.0 0.0) (make-point 0.0 1.0))) => :infinity)


(fact "slope can detect subtle differences"
  (slope (make-line (make-point 3.111 2.9128) (make-point 0 0))) =not=>
    (slope (make-line (make-point 0 0) (make-point 3.112 2.9128))))


;; line intercepts


(fact "intercept works as expected"
  (intercept (make-line (make-point 2.0 2.0) (make-point 1.0 1.0))) => 0.0
  (intercept (make-line (make-point 1.0 0.0) (make-point 0.0 1.0))) => 1.0
  (intercept (make-line (make-point 1.0 0.0) (make-point -1.0 -1.0))) => -0.5
  (intercept (make-line (make-point 1.0 3.0) (make-point 2.0 1.0))) => 5.0
  (intercept (make-line (make-point 2.0 1.0) (make-point 1.0 3.0))) => 5.0
  (intercept (make-line (make-point 1.0 1.0) (make-point 0.0 1.0))) => 1.0)


(fact "intercept returns nil if the line is vertical"
  (intercept (make-line (make-point 0.0 0.0) (make-point 0.0 1.0))) => nil)


(fact "intercept can detect subtle differences"
  (intercept (make-line (make-point 3.111 2.9128) (make-point 0 0))) =not=>
    (intercept (make-line (make-point 0 0.0001) (make-point 3.111 2.9128))))


;; line-coincide?


(fact "`line-coincide` should detect 'reversed' lines"
  (line-coincide?
    (make-line (make-point 0 2) (make-point 0 11))
    (make-line (make-point 0 11) (make-point 0 2))) => true
  (line-coincide?
    (make-line (make-point 3.111 2.9128) (make-point 0 0))
    (make-line (make-point 0 0) (make-point 3.111 2.9128))) => true)


(fact "`line-coincide` should be able to detect subtle differences"
  (line-coincide?
    (make-line (make-point 0 2.0) (make-point 1 11))
    (make-line (make-point 1 11) (make-point 0 2.00001))) => false)


(fact "`line-coincide` isn't thrown off by type rounding errors"
  ;; this came up along the way
  (line-coincide?
    (make-line (make-point 0 2.0) (make-point 1 11))
    (make-line (make-point 1 11.0) (make-point 2 20))) => true
  (line-coincide?
    (make-line (make-point 0 2.0) (make-point 1 11))
    (make-line (make-point 1 11) (make-point 2 20))) => true)


;; crossing-point (of lines)


(def line1 (make-line (make-point 0.0 1.0) (make-point 1.0 2.0)))
(def line2 (make-line (make-point 0.0 10.0) (make-point 5.0 0.0)))
(def line3 (make-line (make-point 6.0 10.0) (make-point 6.0 0.0)))
(def line4 (make-line (make-point 0.0 5.0) (make-point 10.0 5.0)))


(fact "crossing-point returns the point where lines cross, if they do"
  (crossing-point line1 line2) => (make-point 3.0 4.0)
  (crossing-point line1 line3) => (make-point 6.0 7.0)
  (crossing-point line1 line4) => (make-point 4.0 5.0)
  (crossing-point line2 line3) => (make-point 6.0 -2.0)
  (crossing-point line2 line4) => (make-point 2.5 5.0)
  (crossing-point line3 line4) => (make-point 6.0 5.0))


(fact "crossing-point returns nil if they don't intersect"
  (crossing-point line1 line1) => nil
  (crossing-point line3 (make-line (make-point 3.0 0.0) (make-point 3.0 12.0))) => nil
  (crossing-point line1 (make-line (make-point -1.0 0.0) (make-point 0.0 1.0))) => nil
  (crossing-point line4 (make-line (make-point 0.0 0.0) (make-point 3.0 0.0))) => nil)


(fact "crossing-point is pretty accurate even for small divergences"
  (crossing-point line1 (make-line (make-point 0.0 1.0) (make-point 112230.0 112231.0001))) => (make-point 0.0 1.0))



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


(def test-interpreter
  (core/register-types 
    (owe/make-everything-interpreter)
    [push-point push-line push-circle]))


(fact "test-interpreter knows :line-intersection"
  (keys (:instructions test-interpreter)) => (contains :line-intersection))


(fact "the intersection ends up on the :point stack"
  (let [prepped (core/recycle-interpreter
                test-interpreter
                [line1 line2 :line-intersection])]
    (:stacks prepped) =>
    `{:boolean (), :booleans (), :char (), :chars (), :circle (), :code (), :environment (), :error (), :exec (~line1 ~line2 :line-intersection), :float (), :floats (), :integer (), :integers (), :line (), :log (), :point (), :print (), :return (), :set (), :string (), :strings (), :unknown (), :vector ()}
    
    (u/get-stack (core/run prepped 1000) :point) => (list (make-point 3.0 4.0))))


