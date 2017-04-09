(ns push.type.definitions.tagspace
  (:require [push.util.numerics :as n]
            [dire.core          :as dire :refer [with-handler!]]
            ))


(defrecord TagSpace [contents])


(defn make-tagspace
  "Creates a new empty tagspace"
  ([] (->TagSpace (sorted-map)))
  ([starting-items] (->TagSpace (into (sorted-map) starting-items))))



(dire/with-handler! #'make-tagspace
  "Handles bigdec vs rational unterminated expansion errors in `make-tagspace` by applying a `with-precision` wrapper and trying again."
  java.lang.ArithmeticException
  (fn [e & args]
    (with-precision 100 (make-tagspace args))))


(defn average-gap
  "Given a tagspace, this returns the distance between the largest and smallest keys, divided by the number of gaps between keys. If either max or min value is infinite, or there are one or fewer keys, it returns 0"
  [ts]
  (let [k (keys (:contents ts))]
    (if (< (count k) 2)
      0
      (let [biggest  (apply max k)
            smallest (apply min k)]
        (if (some n/infinite? k)
          0
          (/  (-' biggest smallest) (dec (count k))
          ))))))


(dire/with-handler! #'average-gap
  "Handles bigdec vs rational unterminated expansion errors in `average-gap` by applying a `with-precision` wrapper and trying again."
  java.lang.ArithmeticException
  (fn [e ts]
    (with-precision 100 (average-gap ts))))


(defn tagspace?
  "Returns `true` if the item is a `:tagspace`, and `false` otherwise."
  [item]
  (instance? push.type.definitions.tagspace.TagSpace item))


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (assoc-in ts [:contents idx] item))


(defn modded-index
  [ts idx]
  "Given a tagspace and a numeric index, this transforms the index into a new value suitable for 'wrapped lookup' by reducing it modulo (span + average gap). The new index will fall between the minimum key in the tagspace, and the max key plus the average of all gap-sizes."
  [ts idx]
  (let [k (keys (:contents ts))]
    (if (empty? k)
      idx
      (let [low   (apply min k)
            high  (apply max k)
            r     (-' high low)
            extra (average-gap ts)]
        (cond
          (n/infinite? idx) idx
          (zero? extra) idx
          :else (+' low (mod (-' idx low) (+' r extra)))
          )))))


(dire/with-handler! #'modded-index
  "Handles bigdec vs rational unterminated expansion errors in `modded-index` by applying a `with-precision` wrapper and trying again."
  java.lang.ArithmeticException
  (fn [e ts idx]
    (with-precision 100 (modded-index ts idx))))


(defn find-in-tagspace
  "Takes a tagspace and a numeric key, and returns the last first item at or after the index in the tagspace. If the index is larger than the largest key, it 'wraps around' and returns the first item. This lookup is safe against `bigdec` vs `rational` arithmetic clashes."
  [ts idx]
  (let [contents (:contents ts)
        m-idx (modded-index ts idx)
        keepers (filter (fn [[k v]] (n/pN (<= m-idx k))) contents)]
    (if (empty? keepers)
      (second (first contents))
      (second (first keepers)))))


(defn tagspace-dissoc
  "Takes a `TagSpace` and a numeric key. Returns the `TagSpace` with _that exact_ key forgotten, if it is present. NOTE this does not use inexact matching; no change will occur if the key is not present!"
  [ts n]
  (update-in ts [:contents] dissoc n))
