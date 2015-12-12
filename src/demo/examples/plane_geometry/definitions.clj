(ns demo.examples.plane-geometry.definitions
  "Completed library for a set of experiments in plane geometry and compass-and-straightedge constructions. Continues work started in the Gorilla REPL file demo.examples.plane-geometry.cljw"
  (:require [push.interpreter.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.core :as i])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  (:require [clojure.math.numeric-tower :as math])
  (:import  [org.apfloat Apfloat ApfloatMath])
  )




; This extension uses the APfloat Java class for high-accuracy numerical calculations of geometric points. Note that these values are entirely self-contained in the plane geometry objects; no Push items (including primitives) are affected or created from these type or instruction definitions, except for `:line`, `:point` and `:circle`.
; 
; The precision argument used throughout is set here:

(def precision 100)


(defn apf
  "creates an ApFloat value from a Clojure number, via bigdec"
  [n]
  (Apfloat. (bigdec n) precision))


;;; helpers


(defn pretty-much-equal?
  "takes two apfloat items, subtracts one from the other, and returns `true` if the absolute difference is less than 1e-cutoff"
  [num1 num2]
  (cond
    (or (keyword? num1) (keyword? num2))
      (= num1 num2)
    (or (nil? num1) (nil? num2))
      (and (nil? num1) (nil? num2))
    :else
      (= -1 (.compareTo
              (ApfloatMath/abs (.subtract num1 num2))
              (Apfloat. (str "1e-" (/ precision 2)) precision)))))


;;; points


(defrecord Point [x y])

(defn make-point
  "Builds a Point record from two numbers"
  [x y]
  (->Point (apf x) (apf y)))


(defn pt-equal?
  "explicitly checks whether the Apfloat values of two points are identical"
  [pt1 pt2]
  (and
    (pretty-much-equal? (:x pt1) (:x pt2))
    (pretty-much-equal? (:y pt1) (:y pt2))))


;;; lines


(defrecord Line [p1 p2])


(defn make-line
  "Builds an (oriented) Line record from two Points that lie on that line; throws an Exception if the point arguments are equal"
  [a b]
  (if (pt-equal? a b)
    (throw (Exception. "make-line argument error: points must differ"))
    (->Line a b)))


(defn make-line-from-xyxy
  "Builds an (oriented) Line record from four float values, interpreted as x1, y1, x2, y2"
  [x1 y1 x2 y2]
  (if (and (pretty-much-equal? (apf x1) (apf x2))
           (pretty-much-equal? (apf y1) (apf y2)))
    (throw (Exception. "make-line argument error: points must differ"))
    (->Line
      (->Point (apf x1) (apf y1))
      (->Point (apf x2) (apf y2)))))


(defn line-equal?
  "explicitly checks whether the Apfloat values of two lines are identical"
  [line1 line2]
  (and
    (pt-equal? (:p1 line1) (:p1 line2))
    (pt-equal? (:p2 line1) (:p2 line2))))


;;; circles


(defrecord Circle [origin edgepoint])


(defn make-circle
  "Builds a Circle record from two numbers, one the origin and one lying on its circumference; throws an Exception if the point arguments are equal"
  [origin edge]
  (if (pt-equal? origin edge)
    (throw (Exception. "make-circle argument error: points must differ"))
    (->Circle origin edge)))


(defn make-circle-from-xyxy
  "Builds an (oriented) circle record from four float values, interpreted as x1, y1, x2, y2"
  [x1 y1 x2 y2]
  (if (and (pretty-much-equal? (apf x1) (apf x2))
           (pretty-much-equal? (apf y1) (apf y2)))
    (throw (Exception. "make-circle-from-xyxy argument error: points must differ"))
    (->Circle
      (->Point (apf x1) (apf y1))
      (->Point (apf x2) (apf y2)))))


(defn circle-equal?
  "explicitly checks whether the Apfloat values of two circles are identical"
  [circle1 circle2]
  (and
    (pt-equal? (:origin circle1) (:origin circle2))
    (pt-equal? (:edgepoint circle1) (:edgepoint circle2))))



(defn slope
  "returns the slope of a Push `:line` item, or the keyword `:infinity` if the line is vertical"
  [line]
  (let [x1 (:x (:p1 line))
        y1 (:y (:p1 line))
        x2 (:x (:p2 line))
        y2 (:y (:p2 line))]
    (if (pretty-much-equal? x1 x2)
      :infinity
      (.divide (.subtract y2 y1) (.subtract x2 x1)))))


(defn vertical?
  [line]
  (pretty-much-equal? (:x (:p1 line)) (:x (:p2 line))))


(defn horizontal?
  [line]
  (pretty-much-equal? (:y (:p1 line)) (:y (:p2 line))))


(defn intercept
  "returns the y-intercept of a Push `:line` item, or nil if the line is vertical"
  [line]
  (let [x1 (:x (:p1 line))
        y1 (:y (:p1 line))
        x2 (:x (:p2 line))
        y2 (:y (:p2 line))]
    (if (pretty-much-equal? x1 x2)
      nil
      (.subtract y1 (.multiply x1 (slope line))) ;; y - mx = b
      )))
  

(defn lines-coincide?
  "returns `true` if the lines have the same canonical equation"
  [line1 line2]
  (cond
    (and (vertical? line1) (vertical? line2))
      (pretty-much-equal? (:x (:p1 line1)) (:x (:p1 line2)))
    (and (horizontal? line1) (horizontal? line2))
      (pretty-much-equal? (:y (:p1 line1)) (:y (:p1 line2)))
    :else
      (and
        (pretty-much-equal? (slope line1) (slope line2))
        (pretty-much-equal? (intercept line1) (intercept line2)))))


(defn lines-are-parallel?
  "returns `true` if the lines are parallel"
  [line1 line2]
  (if (and (vertical? line1) (vertical? line2))
    (not (pretty-much-equal? (:x (:p1 line1)) (:x (:p1 line2))))
    (and
      (pretty-much-equal? (slope line1) (slope line2))
      (not (lines-coincide? line1 line2)))))


(defn line-at-x
  "returns the y value at a given x for a line argument"
  [line x]
  (.add (intercept line) (.multiply (slope line) x)))


(defn line-relation
  "given two lines, it returns one of :equal, :coincident, :parallel, :intersecting"
  [line1 line2]
  (cond
    (line-equal? line1 line2) :equal
    (lines-coincide? line1 line2) :coincident
    (lines-are-parallel? line1 line2) :parallel
    :else :intersecting))


(defn crossing-point
  "returns a `point` where its two `line` arguments intersect, if they intersect at all (even if one is vertical)"
  [line1 line2]
  (let [s1 (slope line1)
        i1 (intercept line1)
        s2 (slope line2)
        i2 (intercept line2)]
  (cond (lines-are-parallel? line1 line2)
          nil
        (line-equal? line1 line2)
          nil
        (lines-coincide? line1 line2)
          nil
        (vertical? line1)
          (let [x (:x (:p1 line1))]
            (make-point x (line-at-x line2 x)))
        (vertical? line2)
          (let [x (:x (:p1 line2))]
            (make-point x (line-at-x line1 x)))
        :else
          (let [f (.divide (.subtract i2 i1) (.subtract s1 s2))]
            (make-point f (.add i1 (.multiply s1 f)))))))

;; circles


(defn distance-between
  "returns the radial distance between two points"
  [p1 p2]
    (let [x1 (:x p1)
          y1 (:y p1)
          x2 (:x p2)
          y2 (:y p2)]
    (ApfloatMath/sqrt
      (.add (.multiply (.subtract x2 x1) (.subtract x2 x1))
            (.multiply (.subtract y2 y1) (.subtract y2 y1))))))


(defn radius
  "takes a center-edgepoint circle and returns its radius"
  [circle]
  (distance-between (:origin circle) (:edgepoint circle)))


(defn circles-coincide?
  "returns `true` if the circles have the same center and radius"
  [circle1 circle2]
  (and
    (pt-equal? (:origin circle1) (:origin circle2))
    (pretty-much-equal? (radius circle1) (radius circle2))))


(defn circles-tangent?
  "returns `true` if the circles touch at an edge, whether one is inside the other or they are outside one another (but are not coincident or intersecting)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (.add r1 r2)]
    (cond
      (circle-equal? circle1 circle2) false
      (circles-coincide? circle1 circle2) false
      (pretty-much-equal? center-to-center sum-of-radii) true
      (pretty-much-equal? r1 (.add center-to-center r2)) true
      (pretty-much-equal? r2 (.add center-to-center r1)) true
      :else false
      )))


(defn circles-separate?
  "returns `true` if the circles are not coincident, tangent, concentric, contained in one another, or intersecting"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (.add r1 r2)]

    (and
      (not (neg? (.compareTo center-to-center r1)))
      (not (neg? (.compareTo center-to-center r2)))
      (not (circles-tangent? circle1 circle2)))))


(defn circle-A-contains-B?
  "returns `true` if the second circle is nested inside the first (not tangent, entirely within)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (.add r1 r2)]
      (neg? (.compareTo (.add r2 center-to-center) r1))))


(defn circles-inside?
  "returns `true` if one circle is entirely inside the other, or they are concentric with different radii (but they are not tangent)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (.add r1 r2)]
    (or
      (neg? (.compareTo (.add r1 center-to-center) r2))
      (neg? (.compareTo (.add r2 center-to-center) r1)))))


(defn circles-concentric?
  "returns `true` if one circle is entirely inside the other, and they have the same origin points"
  [circle1 circle2]
  (let [r1 (radius circle1)
        r2 (radius circle2)]
    (and
      (pt-equal? (:origin circle1) (:origin circle2))
      (not (circles-coincide? circle1 circle2)))))



(defn circles-intersect?
  "returns `true` if the circles intersect (and are not identical, tangent, concentric, coincident, or disconnected)"
  [circle1 circle2]
  (let [center-to-center
          (distance-between (:origin circle1) (:origin circle2))
        r1 (radius circle1)
        r2 (radius circle2)
        sum-of-radii (.add r1 r2)]
    (cond
      (circle-equal? circle1 circle2) false
      (circles-coincide? circle1 circle2) false
      (not (pos? (.compareTo (.add r1 center-to-center) r2))) false
      (not (pos? (.compareTo (.add r2 center-to-center) r1))) false
      (pretty-much-equal? sum-of-radii center-to-center) false
      :else true)))


(defn point-in-circle?
  "returns `true` if the point is strictly inside (not on the circumference) of the circle"
  [point circle]
  (let [pt-to-center (distance-between (:origin circle) point)
        r (radius circle)]
        (neg? (.compareTo pt-to-center r))))


(defn point-on-circle?
  "returns `true` if the point is on the circumference of the circle"
  [point circle]
  (let [pt-to-center (distance-between (:origin circle) point)
        r (radius circle)]
        (pretty-much-equal? pt-to-center r)))


(defn point-on-line?
  "returns `true` if the point is on the given line"
  [point line]
  (let [m (slope line)
        b (intercept line)]
    (pretty-much-equal? (:y point) (.add b (.multiply m (:x point))))))


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


(def circle-inside?
  (i/build-instruction
    circle-inside?
    "`:circle-inside?` pops the top two `:circle` items (call them `B` and `A` respectively), and pushes `true` if `A` lies entirely inside `B`, and is not tangent (they may be concentric)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circle-A-contains-B? %1 %2) :as :result)
    (d/push-onto :boolean :result)))


(def circle-surrounds?
  (i/build-instruction
    circle-surrounds?
    "`:circle-surrounds?` pops the top two `:circle` items (call them `B` and `A` respectively), and pushes `true` if `B` lies entirely inside `A`, and is not tangent (they may be concentric)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circle-A-contains-B? %2 %1) :as :result)
    (d/push-onto :boolean :result)))


(def circle-nested?
  (i/build-instruction
    circle-nested?
    "`:circle-nested?` pops the top two `:circle` items, and pushes `true` if one of them lies entirely inside the other (in either order), but is not tangent (they may be concentric)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circles-inside? %1 %2) :as :result)
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


(def circle-tangent?
  (i/build-instruction
    circle-tangent?
    "`:circle-tangent?` pops the top two `:circle` items, and pushes `true` if they are tangent (either one external to the other, or one inside the other) but not coincident"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :circle :as :arg2)
    (d/consume-top-of :circle :as :arg1)
    (d/calculate [:arg1 :arg2] #(circles-tangent? %1 %2) :as :result)
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


(def point-inside?
  (i/build-instruction
    point-inside?
    "`:point-inside?` pops the top `:point` item and the top `:circle` item, and pushes `true` to `:boolean` if the point is _strictly_ inside the circle (not on its circumference)"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :point :as :p)
    (d/consume-top-of :circle :as :c)
    (d/calculate [:p :c] #(point-in-circle? %1 %2) :as :within?)
    (d/push-onto :boolean :within?)))


(def point-oncircle?
  (i/build-instruction
    point-oncircle?
    "`:point-oncircle?` pops the top `:point` item and the top `:circle` item, and pushes `true` to `:boolean` if the point is _strictly_ on the circumference of the circle"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :point :as :p)
    (d/consume-top-of :circle :as :c)
    (d/calculate [:p :c] #(point-on-circle? %1 %2) :as :on?)
    (d/push-onto :boolean :on?)))


(def point-online?
  (i/build-instruction
    point-online?
    "`:point-online?` pops the top `:point` item and the top `:line` item, and pushes `true` to `:boolean` if the point lies on the line"
    :tags #{:plane-geometry :construction}
    (d/consume-top-of :point :as :p)
    (d/consume-top-of :line :as :l)
    (d/calculate [:p :l] #(point-on-line? %1 %2) :as :on?)
    (d/push-onto :boolean :on?)))


; [X] `:line-coincide?`
; [X] `:line-intersect?`
; [X] `:line-parallel?`
; [X] `:line-intersection`
; [X] `:circle-coincide?`
; [X] `:circle-concentric?` (shared centers)
; [X] `:circle-inside?`
; [X] `:circle-intersect?`
; [X] `:circle-separate?`
; [X] `:circle-nested?`
; [X] `:circle-surrounds?`
; [X] `:circle-tangent?` (to another circle)
; [X] `:point-inside?`
; [X] `:point-oncircle?`
; [X] `:point-online?`
; [ ] `:circle-intersections` (with another circle)
; [ ] `:LC-intersect?` (line-circle)
; [ ] `:LC-tangent?` (line-circle)
; [ ] `:LC-miss?` (line-circle)
; [ ] `:LC-intersections` zero, one or two `:point` items


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
        aspects/make-returnable
        (t/attach-instruction point-inside?)
        (t/attach-instruction point-oncircle?)
        (t/attach-instruction point-online?)
        ))

    
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
      (t/attach-instruction line-coincide?)
      (t/attach-instruction line-intersect?)
      (t/attach-instruction line-intersection)
      (t/attach-instruction line-parallel?)
      ))

(def push-circle
  (-> (t/make-type 
        :circle 
        :recognizer #(instance? Circle %))
      aspects/make-visible
      aspects/make-equatable
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-returnable
      (t/attach-instruction circle-coincide?)
      (t/attach-instruction circle-inside?)
      (t/attach-instruction circle-intersect?)
      (t/attach-instruction circle-nested?)
      (t/attach-instruction circle-separate?)
      (t/attach-instruction circle-surrounds?)
      (t/attach-instruction circle-tangent?)
      ))