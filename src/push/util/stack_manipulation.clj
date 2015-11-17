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


(defn to-code-item
  "Takes a LazySeq item (typically the result of a cons or concat)
  and puts it into an actual list structure. In the right order. Does
  not affect other collections."
  [collection]
  (cond (= (type collection) clojure.lang.LazySeq)
          (into '() (reverse collection))
        :else collection))



