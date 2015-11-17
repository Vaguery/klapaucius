(ns push.util.type-checkers)


(defn boolean?
  "a checker that returns true if the argument is the literal `true`
  or the literal `false`"
  [item]
  (or (false? item) (true? item)))


(defn pushcode?
  "a checker that returns true if the argument is a Clojure list or of type LazySeq (but not a vector)"
  [item]
  (or (instance? clojure.lang.LazySeq item) (list? item)))
