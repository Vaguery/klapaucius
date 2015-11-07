(ns push.util.type-checkers)


(defn boolean?
  "a checker that returns true if the argument is the literal `true`
  or the literal `false`"
  [item]
  (or (false? item) (true? item)))
