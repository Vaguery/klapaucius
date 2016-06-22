(ns push.type.definitions.complex
  (:require [push.util.numerics :as math]))


;; Complex records


(defrecord Complex [re im])


(defn complexify
  "Takes a number and returns a Complex record {:re n :im 0}"
  [n]
  (->Complex n 0))



(defn complex?
  "a type checker that returns true if the argument is a Complex record; note that this does not recognize 'trivially' complex numbers (reals or pure imaginary ones) "
  [item]
  (= push.type.definitions.complex.Complex (class item)))



(defn complex-zero?
  "predicate returns true if both real and imaginary parts of a Complex are zero"
  [c]
  (and (zero? (:re c)) (zero? (:im c))))



(defn complex-NaN?
  "predicate returns true if either part of a Complex is NaN"
  [c]
  (or (Double/isNaN (:re c)) (Double/isNaN (:im c))))



(defn conjugate
  "takes a Complex record and returns its complex conjugate"
  [c]
  (->Complex
    (:re c) (-' 0 (:im c))))



(defn complex-sum
  "takes two Complex records and returns a new one that is their sum"
  [c1 c2]
  (->Complex
    (+' (:re c1) (:re c2))
    (+' (:im c1) (:im c2))))


(defn complex-diff
  "takes two Complex records and returns a new one that is their difference"
  [c1 c2]
  (->Complex
    (-' (:re c1) (:re c2))
    (-' (:im c1) (:im c2))))


(defn complex-product
  "takes two Complex records and returns a new one that is their product"
  [c1 c2]
  (let [r1 (:re c1)
        r2 (:re c2)
        i1 (:im c1)
        i2 (:im c2)]
  (->Complex
    (-' (*' r1 r2) (*' i1 i2))
    (+' (*' r1 i2) (*' r2 i1)))))



(defn complex-quotient
  "takes two Complex records and returns a new one that is their quotient"
  [c1 c2]
  (let [r1 (:re c1)
        r2 (:re c2)
        i1 (:im c1)
        i2 (:im c2)
        d  (+' (*' r2 r2) (*' i2 i2))
        n1 (+' (*' r1 r2) (*' i1 i2))
        n2 (-' (*' i1 r2) (*' i2 r1))]
    (->Complex
      (/ n1 d)
      (/ n2 d))))


