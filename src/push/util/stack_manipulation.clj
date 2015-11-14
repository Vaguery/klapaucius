(ns push.util.stack-manipulation)


(defn get-stack
  "A convenience function which returns the named stack from the
  interpreter"
  [interpreter stack]
  (get-in interpreter [:stacks stack]))


(defn set-stack
  "A convenience function which replaces the named stack with the
  indicated list"
  [interpreter stack new-value]
  (assoc-in interpreter [:stacks stack] new-value))


(defn clear-stack
  "Empties the named stack."
  [interpreter stack]
  (assoc-in interpreter [:stacks stack] (list)))


(defn peek-at-stack
  "Returns the top item on the named stack."
  [interpreter stack]
  (peek (get-in interpreter [:stacks stack])))