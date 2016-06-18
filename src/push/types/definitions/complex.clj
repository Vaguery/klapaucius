(ns push.types.definitions.complex)


;; Complex records


(defrecord Complex [re im])


(defn complexify
  "Takes a number and returns a Complex record {:re n :im 0}"
  [n]
  (->Complex n 0))



(defn complex?
  "a type checker that returns true if the argument is a Complex record; note that this does not recognize 'trivially' complex numbers (reals or pure imaginary ones) "
  [item]
  (= push.types.definitions.complex.Complex (class item)))



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


