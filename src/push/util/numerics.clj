(ns push.util.numerics
  (:require [clojure.math.numeric-tower :as nt])
  )


(def ∞ Double/POSITIVE_INFINITY)
(def -∞ Double/NEGATIVE_INFINITY)


(defn infinite?
  [number]
  (or (= number ∞)
      (= number -∞)))


(defn infty? [number] (= number ∞))
(defn ninfty? [number] (= number -∞))


(defmacro pN
  [args]
  `(with-precision 100 ~args))


(defn scalar-to-index
  "Takes an arbitrary :scalar value and the size of a collection, and returns an index falling in the range of the collection's size; assumes the count is 1 or larger. An index value of ∞ or -∞ always produces index 0. NOTE: due to a bug in Clojure's mod function, it's possible for very small scalar values to produce out-of-bounds results. This function explicitly checks those bounds while the bug is in place."
  [value howmany]
  (cond
    (zero? howmany)
      (throw (Exception. "scalar-to-index requires a strictly positive argument"))
    (infinite? value)
      0
    :else
      (let [idx (nt/floor (mod value howmany))]
        (if (>= idx howmany)
          0
          (max idx 0)))))



(defn within-1?
  "predicate returns true if the absolute difference between two numbers is 1.0 or smaller"
  [num1 num2]
  (<= (nt/abs (-' num1 num2)) 1))



(defn bigdecimal?
  "predicate returns true if the argument is a Clojure `bigdec` item"
  [n]
  (instance? java.math.BigDecimal n))



(defn integerish?
  "predicate returns true when the numerical argument has no fractional part, even if it's a `Double`, `BigInteger` or `BigDecimal`"
  [n]
  (let [r (mod n 1M)]
    (zero? r)))





(defn index-maker
  "takes a count, a start scalar and a step size, and produces a non-lazy collection of numerical values"
  [howmany start delta]
  (map
    #(+' start (*' %1 delta))
    (range 0 howmany)))

