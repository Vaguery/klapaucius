(ns push.util.numerics
  (:require [clojure.math.numeric-tower :as math])
  )


(defn scalar-to-index
  "Takes an arbitrary :scalar value and the size of a collection, and returns an index falling in the range of the collection's size; assumes the count is 1 or larger"
  [value howmany]
  (let [idx (long (Math/ceil (mod value howmany)))]
    (if (= idx howmany) 0 idx)))



(defn within-1?
  "predicate returns true if the absolute difference between two numbers is 1.0 or smaller"
  [num1 num2]
  (<= (math/abs (-' num1 num2)) 1))