(ns push.util.type-checkers)


(defn pushcode?
  "a checker that returns true if the argument is a seq of any sort (LazySeq, PersistentList, Cons, etc). NOTE this will also 'unwrap' vectors and other seq types, if they make it this far through the router!"
  [item]
  (seq? item))
