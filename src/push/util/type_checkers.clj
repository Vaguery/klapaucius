(ns push.util.type-checkers)


(defn boolean?
  "a checker that returns true if the argument is the literal `true`
  or the literal `false`"
  [item]
  (or (false? item) (true? item)))


(defn pushcode?
  "a checker that returns true if the argument is a seq of any sort (LazySeq, PersistentList, Cons, etc). NOTE this will also 'unwrap' vectors and other seq types, if they make it this far through the router!"
  [item]
  (seq? item))
