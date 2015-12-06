(ns demo.examples.plane-geometry.definitions
  "Completed library for a set of experiments in plane geometry and compass-and-straightedge constructions. Continues work started in the Gorilla REPL file demo.examples.plane-geometry.cljw"
  (:require [push.interpreter.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.core :as i])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )


; - `:line-coincide?` which pushes `true` if the top two `:line` items are algebraically identical
; - `:line-intersect?` which pushes `true` if the top two `:line` items cross exactly once (and are not coincident or parallel)
; - `:line-parallel?` which pushes `true` if the top two `:line` items do not cross and are not coincident
; - `:line-intersection` which pushes a new `:point` only when its arguments are not parallel or coincident
; - `:circle-coincide?`
; - `:circle-intersect?`
; - `:circle-nonintersecting?`
; - `:circle-tangent?`
; - `:circle-intersections` which pushes zero, one or two `:point` items
; - `:line-circleintersect?`
; - `:line-circletangent?`
; - `:line-circlemiss?`
; - `:line-circleintersections` zero, one or two `:point` items

; We should also talk about (and let Push "think" about) the things we can see when we look at a drawing:

; - `:circle-containpoint?`
; - `:circle-contain-circle?` if two `:circle` items do not intersect, but one's center is inside the other
; - `:circle-concentric?`


;;; points


(defrecord Point [x y])

(defn make-point
  "Builds a Point record from two numbers"
  [x y]
  (->Point x y))


;;; lines

(defrecord Line [p1 p2])

(defn make-line
  "Builds an (oriented) Line record from two Points that lie on that line; throws an Exception if the point arguments are equal"
  [a b]
  (if (= a b)
    (throw (Exception. "make-line argument error: points must differ"))
    (->Line a b)))


;;; circles

(defrecord Circle [origin edgepoint])

(defn make-circle
  "Builds a Circle record from two numbers, one the origin and one lying on its circumference; throws an Exception if the point arguments are equal"
  [origin edge]
  (if (= origin edge)
    (throw (Exception. "make-circle argument error: points must differ"))
    (->Circle origin edge)))


;;; helpers

(defn slope
  "returns the slope of a Push `:line` item, or the keyword `:infinity` if the line is vertical"
  [line]
  (let [x1 (double (:x (:p1 line)))
        y1 (double (:y (:p1 line)))
        x2 (double (:x (:p2 line)))
        y2 (double (:y (:p2 line)))]
    (if (= x1 x2)
      :infinity
      (/ (- y2 y1) (- x2 x1)))))


(defn intercept
  "returns the y-intercept of a Push `:line` item, or nil if the line is vertical"
  [line]
  (let [x1 (double (:x (:p1 line)))
        y1 (double (:y (:p1 line)))
        x2 (double (:x (:p2 line)))
        y2 (double (:y (:p2 line)))]
    (if (= x1 x2)
      nil
      (- y1 (* x1 (slope line))) ;; y - mx = b
      )))
  

(defn line-coincide?
  "returns `true` if the lines have the same canonical equation"
  [line1 line2]
  (and
    (= (slope line1) (slope line2))
    (= (intercept line1) (intercept line2))))


(defn line-at-x
  "returns the y value at a given x for a line argument"
  [line x]
  (+ (intercept line) (* (slope line) x)))


(defn crossing-point
  "returns a `point` where its two `line` arguments intersect, if they intersect at all (even if one is vertical)"
  [line1 line2]
  (let [s1 (slope line1)
        i1 (intercept line1)
        s2 (slope line2)
        i2 (intercept line2)]
  (cond (= s1 s2)
        nil
        (= s1 :infinity)
          (let [x (:x (:p1 line1))]
            (make-point x (line-at-x line2 x)))
        (= s2 :infinity)
          (let [x (:x (:p1 line2))]
            (make-point x (line-at-x line1 x)))
        :else
        (let [f (/ (- i2 i1) (- s1 s2))]
            (make-point f (+ i1 (* s1 f)))))))


;;; push instructions


(def crosspoint
  (i/build-instruction
    line-intersection
    "`:line-intersection` pops the top two `:line` items, and pushes the `:point` at which they intersect, if they are not the same or parallel"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :line :as :arg2)
    (d/consume-top-of :line :as :arg1)
    (d/calculate [:arg1 :arg2] #(crossing-point %1 %2) :as :pt)
    (d/push-onto :point :pt)))



;;; push types

  (def push-point
    (-> (t/make-type 
          :point 
          :recognizer #(instance? Point %))
        aspects/make-visible
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable))

    
(def push-line
  (-> (t/make-type 
        :line 
        :recognizer #(instance? Line %))
      aspects/make-visible
      aspects/make-equatable
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-returnable
      (t/attach-instruction crosspoint)))


(def push-circle
  (-> (t/make-type 
        :circle 
        :recognizer #(instance? Circle %))
      aspects/make-visible
      aspects/make-equatable
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-returnable))