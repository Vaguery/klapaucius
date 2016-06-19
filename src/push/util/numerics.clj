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
  (pN (<= (nt/abs (-' num1 num2)) 1)))



(defn bigdecimal?
  "predicate returns true if the argument is a Clojure `bigdec` item"
  [n]
  (instance? java.math.BigDecimal n))



(defn integerish?
  "predicate returns true when the numerical argument has no fractional part, even if it's a `Double`, `BigInteger` or `BigDecimal`"
  [n]
  (let [r (mod n 1M)]
    (zero? r)))



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



(defn safe-add
  "Takes two scalar arguments and adds them. If the attempt at adding raises an exception (such as adding a `bigdec` and a non-terminating `rational`), the `bigdec` is typecast to a `double` or `long` implicitly."
  [arg1 arg2]
  (try
     (+' arg1 arg2)
     (catch java.lang.ArithmeticException e
        (cond
          (bigdecimal? arg1) (safe-add (downsample-bigdec arg1) arg2)
          (bigdecimal? arg2) (safe-add arg1 (downsample-bigdec arg2))
          :else (throw e) ))))




(defn safe-diff 
  "Takes two scalar arguments and subtracts them. If the attempt at subtraction raises an exception (such as when using a `bigdec` and a non-terminating `rational`), the `bigdec` is typecast to a `double` or `long` implicitly if that will help."
  [arg1 arg2]
  (try
     (-' arg1 arg2)
     (catch java.lang.ArithmeticException e
        (cond
          (bigdecimal? arg1) (safe-diff (downsample-bigdec arg1) arg2)
          (bigdecimal? arg2) (safe-diff arg1 (downsample-bigdec arg2))
          :else (throw e) ))))



(defn safe-times 
  "Takes two scalar arguments and multiplies them. If the attempt at multiplication raises an exception (such as when using a `bigdec` and a non-terminating `rational`), the `bigdec` is typecast to a `double` or `long` implicitly if that will help."
  [arg1 arg2]
  (try
     (*' arg1 arg2)
     (catch java.lang.ArithmeticException e
        (cond
          (bigdecimal? arg1) (safe-times (downsample-bigdec arg1) arg2)
          (bigdecimal? arg2) (safe-times arg1 (downsample-bigdec arg2))
          :else (throw e) ))))



(defn safe-quotient
  "Takes two scalar arguments and divides them. If the attempt at division raises an exception (such as when using a `bigdec` and a non-terminating `rational`), the `bigdec` is typecast to a `double` or `long` implicitly if that will help."
  [arg1 arg2]
  (try
     (/ arg1 arg2)
     (catch java.lang.ArithmeticException e
        (if (re-find #"Divide by zero" (.getMessage e))
          (throw e)
          (cond
            (bigdecimal? arg1) (safe-quotient (downsample-bigdec arg1) arg2)
            (bigdecimal? arg2) (safe-quotient arg1 (downsample-bigdec arg2))
            :else (throw e) )))))



(defn safe-modulo
  "Takes two scalar arguments and finds the remainder when divided. If the attempt at division raises an exception (such as when using a `bigdec` and a non-terminating `rational`), the `bigdec` is typecast to a `double` or `long` implicitly if that will help. All other "
  [arg1 arg2]
  (try
     (mod arg1 arg2)
     (catch java.lang.NumberFormatException e Double/NaN)
     (catch java.lang.ArithmeticException e
        (if (re-find #"Divide by zero" (.getMessage e))
          (throw e)
          (cond
            (bigdecimal? arg1) (safe-modulo (downsample-bigdec arg1) arg2)
            (bigdecimal? arg2) (safe-modulo arg1 (downsample-bigdec arg2))
            :else (throw e) )))))
