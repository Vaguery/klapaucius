(ns push.type.definitions.tagspace
  (:require [push.util.numerics :as n])
  )


(defrecord TagSpace [contents])


(defn make-tagspace
  "Creates a new empty tagspace"
  ([] (->TagSpace (sorted-map)))
  ([starting-items] (->TagSpace (into (sorted-map) starting-items))))


(defn tagspace?
  "Returns `true` if the item is a `:tagspace`, and `false` otherwise."
  [item]
  (instance? push.type.definitions.tagspace.TagSpace item))


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (assoc-in ts [:contents idx] item))


(defn find-in-tagspace
  "Takes a tagspace and a numeric key, and returns the last first item at or after the index in the tagspace. If the index is larger than the largest key, it 'wraps around' and returns the first item. This lookup is safe against `bigdec` vs `rational` arithmetic clashes."
  [ts idx]
  (let [contents (:contents ts)
        keepers (filter (fn [[k v]] (n/pN (<= idx k))) contents)]
    (if (empty? keepers)
      (second (first contents))
      (second (first keepers)))))


(defn tagspace-dissoc
  "Takes a `TagSpace` and a numeric key. Returns the `TagSpace` with _that exact_ key forgotten, if it is present. NOTE this does not use inexact matching; no change will occur if the key is not present!"
  [ts n]
  (update-in ts [:contents] dissoc n))

