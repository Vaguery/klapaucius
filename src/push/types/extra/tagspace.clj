(ns push.types.extra.tagspace
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects])
  (:use push.types.extra.generator)
)


(defrecord TagSpace [contents])


(defn make-tagspace
  ""
  []
  (->TagSpace (sorted-map)))


(defn tagspace?
  "Returns `true` if the item is a `:tagspace`, and `false` otherwise."
  [item]
  (= (type item) push.types.extra.tagspace.TagSpace))


;; instructions

; - :tagspace-scale
; - :tagspace-shift
; - :tagspace-invertrange
; - :tagspace-link


(def tagspace-new
  (core/build-instruction
    tagspace-new
    "`:tagspace-new` creates a new, empty `:tagspace` item and pushes it to the stack."
    :tags #{:tagspace}
    (d/calculate [] #(make-tagspace) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-type
  "builds the `:tagspace` collection type, which can hold arbitrary and mixed contents and uses numeric indices"
  (let [typename :tagspace]
  (-> (t/make-type  :tagspace
                    :recognizer tagspace?
                    :attributes #{:collection :tagspace})
      (t/attach-instruction , tagspace-new)
      aspects/make-cycling
      aspects/make-equatable
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-repeatable
      aspects/make-returnable
      aspects/make-storable
      aspects/make-visible 
      )))

