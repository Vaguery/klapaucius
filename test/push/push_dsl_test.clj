(ns push.push-dsl-test
  (:use midje.sweet)
  (:use [push.interpreter])
  (:use [push.push-dsl]))

;; convenience functions for testing

(defn get-local
  [kwd dslblob]
  (kwd (second dslblob)))

;; count-of

(fact "`count-of` saves the number of items on the named stack in the specified local"
  (let [nada (make-interpreter)
        afew (make-interpreter :stacks {:integer '(1 2 3)})
        lots (make-interpreter :stacks {:code (range 1 20)})]
  (get-local :foo (count-of [nada {}] :integer :as :foo)) => 0
  (get-local :foo (count-of [afew {}] :integer :as :foo)) => 3
  (get-local :foo (count-of [lots {}] :code :as :foo)) => 19))


(fact "`count-of` throws an Exception when the named stack doesn't exist"
  (count-of [(make-interpreter) {}] :foo :as :badidea) =>
    (throws #"Push DSL argument error: no "))


(fact "`count-of` has no effect if the scratch variable isn't specified"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (second (count-of [afew {}] :integer)) => {}
    (first (count-of [afew {}] :integer)) => afew))

;; 