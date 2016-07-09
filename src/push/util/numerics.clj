(ns push.util.numerics
  (:require [clojure.math.numeric-tower :as nt])
  )


(defmacro pN
  [args]
  `(with-precision 100 ~args))



(defn scalar-to-index
  "Takes an arbitrary :scalar value and the size of a collection, and returns an index falling in the range of the collection's size; assumes the count is 1 or larger"
  [value howmany]
  (let [idx (long (Math/ceil (mod value howmany)))]
    (if (= idx howmany) 0 idx)))



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



(defn infinite?
  [number]
  (or (= number Double/NEGATIVE_INFINITY)
      (= number Double/POSITIVE_INFINITY)))



(defn downsample-bigdec
  "Takes a `bigdec` argument. If the number has no fractional part and is smaller than Long/MAX_VALUE, it returns `(long n)`; otherwise, it returns `(double n)`."
  [n]
  (if (bigdecimal? n)
    (if (and
          (<= (nt/abs n) Long/MAX_VALUE)
          (integerish? n))
      (long n)
      (double n))
    (throw (Exception. "cannot downsample a non-BigDecimal value"))))



(defn index-maker
  "takes a count, a start scalar and a step size, and produces a non-lazy collection of numerical values"
  [howmany start delta]
  (map
    #(+' start (*' %1 delta))
    (range 0 howmany)))

