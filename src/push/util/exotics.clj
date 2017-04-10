(ns push.util.exotics
  (:require [clojure.math.numeric-tower :as nt]
            [push.util.numerics         :as num]
            [push.util.exceptions       :as oops]
            [push.util.code-wrangling   :as util]
            ))


(defn char-to-digits
  [c]
  (- (long c) 48))



(defn extend-short-list
  [items target]
  (loop [extended items
         n 0]
    (if (>= (count extended) target)
      (util/list! extended)
      (recur (concat extended (list (nth extended n)))
             (inc n)))))



(defn rewrite-digits
  "Applies a totalistic rewrite rule to the digits of an integer value, using a given window size and preserving its sign. If the number is infinite, returns 0"
  [number window]
  (let [digits    (seq (str (max number (-' number))))
        extended  (extend-short-list digits (+ (count digits) (dec window)))
        windows   (partition window 1 extended)
        sums      (map #(apply + (map char-to-digits %)) windows)
        rewrote   (clojure.string/join (map #(mod % 10) sums))
        chomped   (clojure.string/replace-first rewrote #"(^0+)" "")
        as-number (eval (read-string (if (empty? chomped) "0" chomped)))]
    (if (num/infinite? number)
      0
      (if (neg? number)
        (- as-number)
        as-number))))


;; boolean


(defn bit-to-int [b] (if b 1 0))


(defn scalar-to-truth-table
  "Takes an scalar value and a (positive) number of bits to use, and returns the n-bit truth table derived from the scalar's binary value read in canonical order (lowest bit at the left). The scalar's absolute value is used. NOTE: THE NUMBER OF BITS MUST BE A POSITIVE INTEGER. If the number of bits is lower than that needed, only the required number are returned, taken from the left end. If the number of bits in the scalar is more than what's asked for, only the least-significant 2^n bits are returned. No fewer than two bits will ever be returned. If the scalar is infinite or the number of bits is not positive, an empty vector is returned."
  [i bits]
  (cond
    (num/infinite? i) []
    (pos? bits)
      (let [len       (nth (iterate (partial * 2) 1) bits)
            seed      (.toString (biginteger (nt/abs i)) 2)
            shortfall (max 0 (- len (count seed)))]
        (vec
          (take len
            (reverse
              (map
                {\0 false \1 true}
                  (concat
                    (repeat shortfall \0)
                    (seq seed))
                    )))))
    :else []
    ))


(defn indices-of-item-in-vector
  "inspired by Stack Overflow http://stackoverflow.com/questions/4830900/how-do-i-find-the-index-of-an-item-in-a-vector"
  [v item]
  (map first
    (filter
      #(= (second %) item)
      (map-indexed vector v))
      ))


(defn vector->order
  "takes a vector of sortable items, and returns a vector of the same length, where the items are the sort-order of the original vector's items"
  [v]
  (let [sorted (sort (distinct v))]
    (reduce
      #(conj %1 (first (indices-of-item-in-vector sorted %2)))
      []
      v)))


(defn resample-vector
  "Takes a collection and a vector of numbers. Resamples the collection into a vector, using the numbers as indices (in Push robust modulo style)"
  [stuff indices]
  (let [s (count stuff)]
    (if (zero? s)
      []
      (reduce
        #(conj %1 (nth stuff (num/scalar-to-index %2 s)))
        []
        indices)
        )))


(defn permute-with-scalars
  "Takes an ordered collection and a vector of scalars. Using the vector of scalars as a (cycling) series of indices, it removes the nth items from the collection and appends them one at a time to a new vector. If the `:scalars` vector is empty, the original collection is returned unchanged."
  [stuff indices]
  (if (empty? indices)
    stuff
    (let [which (take (count stuff) (cycle indices)) ]
      (loop [result []
             remains stuff
             numbers which]
        (if (empty? remains)
          result
          (let [idx (num/scalar-to-index (first numbers) (count remains))]
          (recur  (conj result (nth remains idx))
                  (concat (take idx remains) (drop (inc idx) remains))
                  (drop 1 numbers))
                  ))))))
