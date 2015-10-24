(ns push.push-dsl-test
  (:use midje.sweet)
  (:use [push.interpreter])
  (:use [push.push-dsl]))

;; convenience functions for testing

(defn get-local-from-dslblob
  [kwd dslblob]
  (kwd (second dslblob)))

;; count-of

(fact "`count-of` saves the number of items on the named stack in the specified local"
  (let [nada (make-interpreter)
        afew (make-interpreter :stacks {:integer '(1 2 3)})
        lots (make-interpreter :stacks {:code (range 1 20)})]
  (get-local-from-dslblob :foo (count-of [nada {}] :integer :as :foo)) => 0
  (get-local-from-dslblob :foo (count-of [afew {}] :integer :as :foo)) => 3
  (get-local-from-dslblob :foo (count-of [lots {}] :code :as :foo)) => 19))


(fact "`count-of` throws an Exception when the named stack doesn't exist"
  (count-of [(make-interpreter) {}] :foo :as :badidea) =>
    (throws #"Push DSL argument error: no "))


(fact "`count-of` has no effect if the scratch variable isn't specified"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (second (count-of [afew {}] :integer)) => {}
    (first (count-of [afew {}] :integer)) => afew))


;; `consume-top-of [stackname]`


(defn get-stack-from-dslblob
  [stackname dslblob]
  (get-stack (first dslblob) stackname))


(fact "`consume-top-of` deletes the top item of a stack with you don't tell it to save it"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (get-stack-from-dslblob :integer (consume-top-of [afew {}] :integer)) => '(2 3) 
    ))


(fact "`consume-top-of` raises an Exception if the stack doesn't exist"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (consume-top-of [afew {}] :stupid) =>
      (throws #"Push DSL argument error: no ")))


(fact "`consume-top-of` raises an Exception if the stack is empty"
  (consume-top-of [(make-interpreter) {}] :integer) =>
    (throws #"Push DSL runtime error: stack "))


;; `consume-top-of [stackname :as local]`


(fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (get-stack-from-dslblob :integer (consume-top-of [afew {}] :integer :as :foo)) =>
      '(2 3)
    (get-local-from-dslblob :foo (consume-top-of [afew {}] :integer :as :foo)) => 1 ))

(fact "`consume-top-of` overwrites locals that already exist"
  (let [afew (make-interpreter :stacks {:integer '(1 2 3)})]
    (get-local-from-dslblob :foo (consume-top-of [afew {:foo \f}] :integer :as :foo)) => 1
    ))
