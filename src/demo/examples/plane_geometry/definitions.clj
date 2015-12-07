(ns demo.examples.plane-geometry.definitions
  "Completed library for a set of experiments in plane geometry and compass-and-straightedge constructions. Continues work started in the Gorilla REPL file demo.examples.plane-geometry.cljw"
  (:require [push.interpreter.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.core :as i])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  (:require [clojure.math.numeric-tower :as math])
  )


; - `:line-coincide?`
; - `:line-intersect?`
; - `:line-parallel?`
; - `:line-intersection`
; - `:circle-coincide?`
; - `:circle-intersect?`
; - `:circle-separate?`
; - `:circle-tangent?` (to another circle)
; - `:circle-intersections` (with another circle)
; - `:LC-intersect?` (line-circle)
; - `:LC-tangent?` (line-circle)
; - `:LC-miss?` (line-circle)
; - `:LC-intersections` zero, one or two `:point` items
; - `:point-inside?`
; - `:point-oncircle?`
; - `:point-online?`
; - `:circle-nested?` (two `:circle` items do not intersect, and one's center is inside the other)
; - `:circle-concentric?` (shared centers)


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


(defn make-line-from-xyxy
  "Builds an (oriented) Line record from four float values, interpreted as x1, y1, x2, y2"
  [x1 y1 x2 y2]
  (if (and (= x1 x2) (= y1 y2))
    (throw (Exception. "make-line argument error: points must differ"))
    (->Line (->Point x1 y1) (->Point x2 y2))))


;;; circles

(defrecord Circle [origin edgepoint])

(defn make-circle
  "Builds a Circle record from two numbers, one the origin and one lying on its circumference; throws an Exception if the point arguments are equal"
  [origin edge]
  (if (= origin edge)
    (throw (Exception. "make-circle argument error: points must differ"))
    (->Circle origin edge)))

(defn make-circle-from-xyxy
  "Builds an (oriented) circle record from four float values, interpreted as x1, y1, x2, y2"
  [x1 y1 x2 y2]
  (if (and (= x1 x2) (= y1 y2))
    (throw (Exception. "make-circle-from-xyxy argument error: points must differ"))
    (->Circle (->Point x1 y1) (->Point x2 y2))))


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
  

(defn lines-coincide?
  "returns `true` if the lines have the same canonical equation"
  [line1 line2]
  (if (= :infinity (slope line1) (slope line2))
    (= (double (:x (:p1 line1))) (double (:x (:p1 line2))))
    (and
      (= (slope line1) (slope line2))
      (= (intercept line1) (intercept line2)))))


(defn lines-are-parallel?
  "returns `true` if the lines are parallel"
  [line1 line2]
  (and
    (= (slope line1) (slope line2))
    (not (lines-coincide? line1 line2))))


(defn line-at-x
  "returns the y value at a given x for a line argument"
  [line x]
  (+ (intercept line) (* (slope line) x)))


(defn line-relation
  "given two lines, it returns one of :equal, :coincident, :parallel, :intersecting"
  [line1 line2]
  (cond
    (= line1 line2) :equal
    (lines-coincide? line1 line2) :coincident
    (lines-are-parallel? line1 line2) :parallel
    :else :intersecting
    ))


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
          (let [x (double (:x (:p1 line1)))]
            (make-point x (line-at-x line2 x)))
        (= s2 :infinity)
          (let [x (double (:x (:p1 line2)))]
            (make-point x (line-at-x line1 x)))
        :else
        (let [f (/ (- i2 i1) (- s1 s2))]
            (make-point f (+ i1 (* s1 f)))))))

;; circles


(defn distance-between
  "returns the radial distance between two points"
  [p1 p2]
    (let [x1 (double (:x p1))
          y1 (double (:y p1))
          x2 (double (:x p2))
          y2 (double (:y p2))]
    (math/sqrt (+ (* (- x2 x1) (- x2 x1)) (* (- y2 y1) (- y2 y1))))))


(defn radius
  "takes a center-edgepoint circle and returns its radius"
  [circle]
  (distance-between (:origin circle) (:edgepoint circle)))


(defn circles-coincide?
  "returns `true` if the circles have the same center and radius"
  [circle1 circle2]
  (and
    (= (:origin circle1) (:origin circle2))
    (= (radius circle1) (radius circle2))))


(defn circles-tangent?
  "returns `true` if the circles touch at an edge, whether one is inside the other or they are outside one another (but are not coincident or intersecting)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (+ r1 r2)]
    (cond
      (= circle1 circle2) false
      (circles-coincide? circle1 circle2) false
      (< sum-of-radii center-to-center) false
      (= center-to-center sum-of-radii) true
      (= r1 (+ center-to-center r2)) true
      (= r2 (+ center-to-center r1)) true
      :else false
      )))


(defn circles-separate?
  "returns `true` if the circles are not coincident, tangent, concentric, contained in one another, or intersecting"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (+ r1 r2)]
    (if (< sum-of-radii center-to-center)
      true
      false)))



(defn circles-intersect?
  "returns `true` if the circles intersect (and are not identical, tangent, concentric, coincident, or disconnected)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (+ r1 r2)]
    (cond
      (= circle1 circle2) false
      (circles-coincide? circle1 circle2) false
      (<= (+ r1 center-to-center) r2) false
      (<= (+ r2 center-to-center) r1) false
      (= sum-of-radii center-to-center) false
      :else true)))



;;; push instructions


(def circle-coincide?
  (i/build-instruction
    circle-coincide?
    "`:circle-coincide?` pops the top two `:circle` items, and pushes `true` if they have the same origin and radius (but not the same defining points)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circles-coincide? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def circle-intersect?
  (i/build-instruction
    circle-intersect?
    "`:circle-intersect?` pops the top two `:circle` items, and pushes `true` if they intersect (and are thus not coincident, identical, concentric, contained in one another or completely separate)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circles-intersect? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def circle-separate?
  (i/build-instruction
    circle-separate?
    "`:circle-separate?` pops the top two `:circle` items, and pushes `true` if they are not coincident, identical, concentric, contained in one another or intersecting"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circles-separate? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def line-coincide?
  (i/build-instruction
    line-coincide?
    "`:line-coincide?` pops the top two `:line` items, and pushes `true` if the two lines coincide (i.e., are neither intersecting nor parallel)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :line :as :arg2)
    (d/consume-top-of :line :as :arg1)
    (d/calculate [:arg1 :arg2] #(lines-coincide? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def line-parallel?
  (i/build-instruction
    line-parallel?
    "`:line-parallel?` pops the top two `:line` items, and pushes `true` if the two lines are parallel (i.e., are neither intersecting nor coincident)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :line :as :arg2)
    (d/consume-top-of :line :as :arg1)
    (d/calculate [:arg1 :arg2] #(lines-are-parallel? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def line-intersect?
  (i/build-instruction
    line-intersect?
    "`:line-intersect?` pops the top two `:line` items, and pushes `true` if the two lines intersect (i.e., are neither parallel nor coincident)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :line :as :arg2)
    (d/consume-top-of :line :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(not (or
              (lines-are-parallel? %1 %2)
              (lines-coincide? %1 %2))) :as :result)
    (d/push-onto :boolean :result)))


(def line-intersection
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
      (t/attach-instruction line-intersection)))


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