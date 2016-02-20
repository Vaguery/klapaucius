(ns demo.examples.plane-geometry.instruction-test
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  (:require [push.interpreter.core :as core])
  (:require [push.util.stack-manipulation :as u])
  (:use demo.examples.plane-geometry.definitions)
  (:import  [org.apfloat Apfloat ApfloatMath])
  (:use push.util.test-helpers)
  )


;; setup interpreter for testing

(def geo-interpreter
  (core/register-types
    (owe/make-everything-interpreter)
    [precise-point precise-line precise-circle]))



;;;;;;;;;;;;;;;;;;;


(tabular
  (fact "`circle-coincide?` takes two :circle items and pushes `true` if they have the same center and radius"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 -3 -3))
     :boolean '()}             :circle-coincide?      {:circle '()
                                                       :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-coincide?      {:circle '()
                                                       :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 3 3))
     :boolean '()}             :circle-coincide?      {:circle '()
                                                       :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`circle-concentric?` takes two :circle items and pushes `true` if they have the same center and different radii"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 -3 -3))
     :boolean '()}             :circle-concentric?      {:circle '()
                                                         :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-concentric?      {:circle '()
                                                         :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 1 1 1.1 1.1))
     :boolean '()}             :circle-concentric?      {:circle '()
                                                         :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 3 3))
     :boolean '()}             :circle-concentric?      {:circle '()
                                                         :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`circle-inside?` takes two :circle items and pushes `true` if the second is entirely inside the first"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-inside?      {:circle '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 4 4))
     :boolean '()}             :circle-inside?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 1 1 4 4))
     :boolean '()}             :circle-inside?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 1 0 3 0))
     :boolean '()}             :circle-inside?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 1 0 3 0)
                (make-circle-from-xyxy 0 0 3 0))
     :boolean '()}             :circle-inside?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`circle-intersect?` takes two :circle items and pushes `true` if they intersect (and are not tangent or coincident)"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-intersect?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 1 1 4 4))
     :boolean '()}             :circle-intersect?      {:circle '()
                                                        :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 3 3))
     :boolean '()}             :circle-intersect?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 0 6 3 6))
     :boolean '()}             :circle-intersect?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 0 6 2.999999999M 6))
     :boolean '()}             :circle-intersect?      {:circle '()
                                                        :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(fact "`circle-intersections` takes two :circle items and pushes a list containing their intersection points (if any) to :exec"
  (let [c1 (make-circle-from-xyxy 0 0 3 0)
        c2 (make-circle-from-xyxy 6 0 3 0)
        tangent-at-3-0 (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list c1 c2)}
                          :circle-intersections)]

  (circles-tangent? c1 c2) => true
  (first (first (:exec tangent-at-3-0))) => #(pt-equal? (make-point 3 0) %)
  (count (first (:exec tangent-at-3-0))) => 1)


  (let [c1 (make-circle-from-xyxy 0 0 5 0)
        c2 (make-circle-from-xyxy 6 0 1 0)
        cross-at-3-4-5  (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list c1 c2)}
                          :circle-intersections)]

  (circles-intersect? c1 c2) => true
  (count (first (:exec cross-at-3-4-5))) => 2
  (first (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point 3 4) %)
  (second (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point 3 -4) %))

  (let [c1 (make-circle-from-xyxy 0 0 5 0)
        c2 (make-circle-from-xyxy 0 0 0 5)
        same-all-over   (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list c1 c2)}
                          :circle-intersections)]

  (circles-coincide? c1 c2) => true
  (count (first (:exec same-all-over))) => 1
  (first (first (:exec same-all-over))) =>
    #(circle-equal? (make-circle-from-xyxy 0 0 0 5) %))


  (let [c1 (make-circle-from-xyxy 0 0 5 0)
        c2 (make-circle-from-xyxy 9 9 8 8)
        do-not-touch    (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list c1 c2)}
                          :circle-intersections)]

  (circles-separate? c1 c2) => true
  (count (first (:exec do-not-touch))) => 0))



(tabular
  (fact "`circle-nested?` takes two :circle items and pushes `true` if either is inside the other (but not identical or coincident)"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-nested?      {:circle '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 2 2)
                (make-circle-from-xyxy 0 0 3 3))
     :boolean '()}             :circle-nested?      {:circle '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 1 1 4 4))
     :boolean '()}             :circle-nested?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 6 0 3 0))
     :boolean '()}             :circle-nested?      {:circle '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact "`circle-separate?` takes two :circle items and pushes `true` if they don't touch or overlap in any way"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-separate?      {:circle '()
                                                       :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 14 14 15 15))
     :boolean '()}             :circle-separate?      {:circle '()
                                                       :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 4 0 3 0))
     :boolean '()}             :circle-separate?      {:circle '()
                                                       :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 0 0 3 0))
     :boolean '()}             :circle-separate?      {:circle '()
                                                       :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`circle-surrounds?` takes two :circle items and pushes `true` if the second entirely surrounds the first"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-surrounds?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 4 4))
     :boolean '()}             :circle-surrounds?      {:circle '()
                                                        :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 1 1 4 4))
     :boolean '()}             :circle-surrounds?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0)
                (make-circle-from-xyxy 1 0 3 0))
     :boolean '()}             :circle-surrounds?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 1 0 3 0)
                (make-circle-from-xyxy 0 0 3 0))
     :boolean '()}             :circle-surrounds?      {:circle '()
                                                        :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`circle-tangent?` takes two :circle items and pushes `true` if they are tangent (in any arrangement of nesting)"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3)
                (make-circle-from-xyxy 0 0 2 2))
     :boolean '()}             :circle-tangent?      {:circle '()
                                                      :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 3 0 0 0)
                (make-circle-from-xyxy 8 0 6 0))
     :boolean '()}             :circle-tangent?      {:circle '()
                                                      :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 3 0 0 0)
                (make-circle-from-xyxy 8 0 5 0))
     :boolean '()}             :circle-tangent?      {:circle '()
                                                      :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`lc-intersect?` takes a :circle and a :line and pushes `true` if the :line intersects the circle"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
              (make-line-from-xyxy 0 0 3 3))
     :circle (list
              (make-circle-from-xyxy 5 5 7 7))}
                               :line-circle-intersect? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 8 0 8 22))
     :circle (list
              (make-circle-from-xyxy 1 1 2 2))}
                               :line-circle-intersect? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 0 0 10 0))
     :circle (list
              (make-circle-from-xyxy 0 2 0 0))}
                               :line-circle-intersect? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(fact "`line-circle-intersections` takes one :circle and one :line, and pushes a list containing their intersection points (if any) to :exec"
  (let [circle (make-circle-from-xyxy 0 0 5 0)
        line   (make-line-from-xyxy 0 0 6 8)
        cross-at-3-4-5  (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list circle)
                           :line (list line)}
                          :line-circle-intersections)]

  (line-enters-circle? line circle) => true
  (count (first (:exec cross-at-3-4-5))) => 2
  (first (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point 3 4) %)
  (second (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point -3 -4) %)
  )

  (let [circle (make-circle-from-xyxy 0 0 5 0)
        line   (make-line-from-xyxy 5 22 5 -22)
        cross-at-3-4-5  (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list circle)
                           :line (list line)}
                          :line-circle-intersections)]

  (line-tangent-to-circle? line circle) => true
  (count (first (:exec cross-at-3-4-5))) => 1
  (first (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point 5 0) %)
  )

  (let [circle (make-circle-from-xyxy 0 0 5 0)
        line   (make-line-from-xyxy -5 22 -5 -22)
        cross-at-3-4-5  (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list circle)
                           :line (list line)}
                          :line-circle-intersections)]

  (line-tangent-to-circle? line circle) => true
  (count (first (:exec cross-at-3-4-5))) => 1
  (first (first (:exec cross-at-3-4-5))) => #(pt-equal? (make-point -5 0) %)
  )

  (let [circle (make-circle-from-xyxy 0 0 5 0)
        line   (make-line-from-xyxy 9 0 22 1000)
        cross-at-3-4-5  (check-instruction-here-using-this
                          geo-interpreter
                          {:circle (list circle)
                           :line (list line)}
                          :line-circle-intersections)]

  (line-misses-circle? line circle) => true
  (count (first (:exec cross-at-3-4-5))) => 0
  )
)



(tabular
  (fact "`lc-miss?` takes a :circle and a :line and pushes `true` if the :line intersects the circle"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
              (make-line-from-xyxy 0 0 3 3))
     :circle (list
              (make-circle-from-xyxy 5 5 7 7))}
                               :line-circle-miss? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 8 0 8 22))
     :circle (list
              (make-circle-from-xyxy 1 1 2 2))}
                               :line-circle-miss? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 0 0 10 0))
     :circle (list
              (make-circle-from-xyxy 0 2 0 0))}
                               :line-circle-miss? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`lc-tangent?` takes a :circle and a :line and pushes `true` if the :line intersects the circle"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
              (make-line-from-xyxy 0 0 3 3))
     :circle (list
              (make-circle-from-xyxy 5 5 7 7))}
                               :line-circle-tangent? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 8 0 8 22))
     :circle (list
              (make-circle-from-xyxy 1 1 2 2))}
                               :line-circle-tangent? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 0 0 10 0))
     :circle (list
              (make-circle-from-xyxy 0 2 0 0))}
                               :line-circle-tangent? 
                                                 {:line     '()
                                                  :circle   '()
                                                  :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact "`line-coincide?` takes two :line items and pushes `true` if they have the same slope and intercept"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 0 -3 -3))
     :boolean '()}             :line-coincide?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 0 3 3))
     :boolean '()}             :line-coincide?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 1 3 3))
     :boolean '()}             :line-coincide?      {:line '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 1 1 4 4))
     :boolean '()}             :line-coincide?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
        {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 3 3 0 0))
     :boolean '()}             :line-coincide?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`line-intersect?` takes two :line items and pushes `true` if they have one point in common "
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 1 3 4))
     :boolean '()}             :line-intersect?      {:line '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 3 3 0 0))
     :boolean '()}             :line-intersect?      {:line '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 0 3 6))
     :boolean '()}             :line-intersect?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`line-intersection` takes two :line items and pushes the `:point` they have in common, if any"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 2 0 2 11))
     :boolean '()}             :line-intersection      {:line '()
                                                        :point (list
                                                            (make-point 2 2))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 1 0 4 3))
     :boolean '()}             :line-intersection      {:line '()
                                                        :point '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 1 1 4 4))
     :boolean '()}             :line-intersection      {:line '()
                                                        :point '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`line-parallel?` takes two :line items and pushes `true` if they have no points in common"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 0 1 3 4))
     :boolean '()}             :line-parallel?      {:line '()
                                                     :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 1 1 4 4))
     :boolean '()}             :line-parallel?      {:line '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
                (make-line-from-xyxy 0 0 3 3)
                (make-line-from-xyxy 2 -1 7 8))
     :boolean '()}             :line-parallel?      {:line '()
                                                     :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`point-inside?` takes a :point and a :circle and pushes `true` if the :point is inside"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3))
     :point (list (make-point 1 1))}
                               :point-inside?      {:line     '()
                                                    :circle   '()
                                                    :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3))
     :point (list (make-point 9 9))}
                               :point-inside?      {:line     '()
                                                    :circle   '()
                                                    :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0))
     :point (list (make-point 0 3))}
                               :point-inside?      {:line     '()
                                                    :circle   '()
                                                    :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`point-oncircle?` takes a :point and a :circle and pushes `true` if the :point is strictly on the circumference of the circle"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3))
     :point (list (make-point 1 1))}
                               :point-oncircle?    {:line     '()
                                                    :circle   '()
                                                    :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 3))
     :point (list (make-point 9 9))}
                               :point-oncircle?    {:line     '()
                                                    :circle   '()
                                                    :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  (list
                (make-circle-from-xyxy 0 0 3 0))
     :point (list (make-point 0 3))}
                               :point-oncircle?    {:line     '()
                                                    :circle   '()
                                                    :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`point-online?` takes a :point and a :line and pushes `true` if the :point is on the line"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
              (make-line-from-xyxy 0 0 3 3))
     :point (list (make-point 1 1))}
                               :point-online?    {:line     '()
                                                  :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 0 0 3 3))
     :point (list (make-point 3 3))}
                               :point-online?    {:line     '()
                                                  :boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  (list
              (make-line-from-xyxy 0 0 3 0))
     :point (list (make-point 0 3))}
                               :point-online?    {:line     '()
                                                  :boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`line->points` takes a :line pushes a list of (pt1, pt2) (in that order) to :exec"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  (list
              (make-line-from-xyxy 0 1 2 3))
     :point '()}
                               :line->points       {:line    '()
                                                    :exec    (list (list
                                                        (make-point 0 1)
                                                        (make-point 2 3)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`line<-points` takes two points and makes a :line"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line  '()
     :point (list
              (make-point 2 3)
              (make-point 0 1))}
                               :line<-points       {:line
                                                    (list
                                                      (make-line-from-xyxy 0 1 2 3))
                                                    :point  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line  '()
     :point (list
              (make-point 2 3)
              (make-point 2 3))}
                               :line<-points       {:line '()
                                                    :point  '()
                                                    :error
                                                    '({:item "can't make line from identical points", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`circle->points` takes a :circle pushes a list of (origin, edgepoint) (in that order) to :exec"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  (list
              (make-circle-from-xyxy 0 1 2 3))
     :point '()}
                               :circle->points     {:circle    '()
                                                    :exec    (list (list
                                                        (make-point 0 1)
                                                        (make-point 2 3)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`circle<-points` takes two points and makes a :circle"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle  '()
     :point (list
              (make-point 2 3)
              (make-point 0 1))}
                               :circle<-points      {:circle
                                                    (list
                                                      (make-circle-from-xyxy 0 1 2 3))
                                                    :point  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle  '()
     :point (list
              (make-point 2 3)
              (make-point 2 3))}
                               :circle<-points       {:circle '()
                                                    :point  '()
                                                    :error
                                                    '({:item "can't make circle from identical points", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


;;;; equality


(tabular
  (fact "`point-equal?` takes two :point objects and returns true if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:point
      (list
        (make-point 1 1)
        (make-point 1 1))}
                                 :point-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:point
      (list
        (make-point 1 1.00000000000001M)
        (make-point 1 1))}
                                 :point-equal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:point
      (list
        (make-point 1 (.add (apf 1) (apf 1e-77)))
        (make-point 1 1))}
                                 :point-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`point-notequal?` takes two :point objects and returns false if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:point
      (list
        (make-point 1 1)
        (make-point 1 1))}
                                 :point-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:point
      (list
        (make-point 1 1.00000000000001M)
        (make-point 1 1))}
                                 :point-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:point
      (list
        (make-point 0 (apf 1e-47))
        (make-point 0 0))}
                                 :point-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:point
      (list
        (make-point 0 (apf 1e-77))
        (make-point 0 0))}
                                 :point-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`line-equal?` takes two :line objects and returns true if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line
      (list
        (make-line-from-xyxy 1 1 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 1.00000000000001M 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-equal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 (.add (apf 1) (apf 1e-77)) 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 (.add (apf 1) (apf 1e-37)) 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-equal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`line-notequal?` takes two :line objects and returns true if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:line
      (list
        (make-line-from-xyxy 1 1 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 1.00000000000001M 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 (.add (apf 1) (apf 1e-77)) 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:line
      (list
        (make-line-from-xyxy 1 (.add (apf 1) (apf 1e-37)) 2 2)
        (make-line-from-xyxy 1 1 2 2))}
                                 :line-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`circle-equal?` takes two :circle objects and returns true if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle
      (list
        (make-circle-from-xyxy 1 1 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 1.00000000000001M 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-equal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 (.add (apf 1) (apf 1e-77)) 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-equal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 (.add (apf 1) (apf 1e-37)) 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-equal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`circle-notequal?` takes two :circle objects and returns true if :x and :y are pretty-much-equal"
    (check-instruction-here-using-this
      geo-interpreter
      ?new-stacks
      ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction           ?expected

    {:circle
      (list
        (make-circle-from-xyxy 1 1 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 1.00000000000001M 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 (.add (apf 1) (apf 1e-77)) 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-notequal?      {:boolean  '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:circle
      (list
        (make-circle-from-xyxy 1 (.add (apf 1) (apf 1e-37)) 2 2)
        (make-circle-from-xyxy 1 1 2 2))}
                                 :circle-notequal?      {:boolean  '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
