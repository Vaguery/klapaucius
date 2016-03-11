(ns push.types.extra.tagspace
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects])
  (:use push.types.extra.generator)
)


(defrecord TagSpace [contents])


(defn make-tagspace
  "Creates a new empty tagspace"
  ([] (->TagSpace (sorted-map)))
  ([starting-items] (->TagSpace (into (sorted-map) starting-items)))
  )


(defn tagspace?
  "Returns `true` if the item is a `:tagspace`, and `false` otherwise."
  [item]
  (= (type item) push.types.extra.tagspace.TagSpace))


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (assoc-in ts [:contents idx] item))


(defn find-in-tagspace
  "Takes a tagspace and a numeric key, and returns the last first item at or after the index in the tagspace. If the index is larger than the largest key, it 'wraps around' and returns the first item."
  [ts idx]
  (let [contents (:contents ts)
        keepers (filter (fn [[k v]] (<= idx k)) contents)]
    (if (empty? keepers)
      (second (first contents))
      (second (first keepers)))))


;; instructions

; - :tagspace-scale
; - :tagspace-shift
; - :tagspace-invertrange
; - :tagspace-link
; - :tagspace-merge
; - :tagspace-splitatinteger
; - :tagspace-splitatfloat


; (def tagspace-lookupint
;   (core/build-instruction
;     tagspace-lookupint
;     "`:tagspace-lookupint` pops the top `:integer` and the top `:tagspace`, and pushes a list containing the item stored at or after the index, and the tagspace."
;     :tags #{:tagspace :collection}
;     (d/consume-top-of :integer :as :idx)
;     (d/consume-top-of :tagspace :as :ts)
;     (d/calculate [:idx :ts] #(lookup-in-tagspace %2 %1) :as :result)
;     (d/push-onto :tagspace :result)))


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
      aspects/make-taggable
      aspects/make-visible 
      )))

