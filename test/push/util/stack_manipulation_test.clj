(ns push.util.stack-manipulation-test
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  )

(fact "`to-code-item` converts a LazySeq into a list proper"
  (type (to-code-item (concat '(1 2 3) '(4 5 6)))) => (type '(1 2 3))
  )

(fact "`to-code-item` converts a LazySeq into a list proper"
  (type (to-code-item (conj '(1 2 3) '(4 5 6)))) => (type '(1 2 3))
  )
