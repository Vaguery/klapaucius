(ns demo.examples.plane-geometry.instruction-test
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  (:require [push.interpreter.core :as core])
  (:require [push.util.stack-manipulation :as u])
  (:use demo.examples.plane-geometry.definitions)
  (:import  [org.apfloat Apfloat ApfloatMath])
  (:use push.util.test-helpers)
  )


; [X] `:circle-intersections` (with another circle)
; [X] `:LC-intersections` zero, one or two `:point` items


;; setup interpreter for testing

(def geo-interpreter
  (core/register-types
    (owe/make-everything-interpreter)
    [push-point push-line push-circle]))


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