(ns push.push-dsl-test
  (:use midje.sweet)
  (:use [push.interpreter])
  (:use [push.push-dsl]))

;; convenience functions for testing

(defn get-local-from-dslblob
  [kwd dslblob]
  (kwd (second dslblob)))

(def nada (make-interpreter))
(def afew (make-interpreter :stacks {:integer '(1 2 3)}))
(def lots (make-interpreter :stacks {:code (range 1 20)}))


;; count-of


(fact "`count-of` saves the number of items on the named stack in the specified local"
  (get-local-from-dslblob :foo (count-of [nada {}] :integer :as :foo)) => 0
  (get-local-from-dslblob :foo (count-of [afew {}] :integer :as :foo)) => 3
  (get-local-from-dslblob :foo (count-of [lots {}] :code :as :foo)) => 19)


(fact "`count-of` throws an Exception when the named stack doesn't exist"
  (count-of [nada {}] :foo :as :badidea) =>
    (throws #"Push DSL argument error: no "))


(fact "`count-of` has no effect if the scratch variable isn't specified"
  (second (count-of [afew {}] :integer)) => {}
  (first (count-of [afew {}] :integer)) => afew)


;; `delete-top-of [stackname]`


(defn get-stack-from-dslblob
  [stackname dslblob]
  (get-stack (first dslblob) stackname))


(fact "`delete-top-of` deletes the top item of a stack"
  (get-stack-from-dslblob :integer (delete-top-of [afew {}] :integer)) => '(2 3))


(fact "`delete-top-of` raises an Exception if the stack doesn't exist"
  (delete-top-of [afew {}] :stupid) => (throws #"Push DSL argument error: no "))


(fact "`delete-top-of` raises an Exception if the stack is empty"
  (delete-top-of [nada {}] :integer) => 
    (throws #"Push DSL runtime error: stack "))


;; `consume-top-of [stackname :as local]`


(fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
  (get-stack-from-dslblob :integer (consume-top-of [afew {}] :integer :as :foo)) =>
    '(2 3)
  (get-local-from-dslblob :foo (consume-top-of [afew {}] :integer :as :foo)) => 1 )


(fact "`consume-top-of` overwrites locals that already exist"
  (get-local-from-dslblob :foo (consume-top-of [afew {:foo \f}] :integer :as :foo)) => 1)


(fact "`consume-top-of` throws an exception when no local is given"
  (consume-top-of [afew {:foo \f}] :integer) =>
    (throws #"Push DSL argument error: missing key"))


;; delete-nth


(fact "`delete-nth` returns a collection with the nth item removed"
  (delete-nth '(0 1 2 3 4 5) 3) => '(0 1 2 4 5)
  (delete-nth '(0 1 2 3 4 5) 0) => '(1 2 3 4 5)
  (delete-nth '(0 1 2 3 4 5) 5) => '(0 1 2 3 4))


(fact "`delete-nth` throws an Exception when the list is empty"
  (delete-nth '() 3) => (throws #"Assert"))


(fact "`delete-nth` throws an Exception when the index is out of range"
  (delete-nth '(0 1 2 3 4 5) -99) => (throws #"Assert")
  (delete-nth '(0 1 2 3 4 5) 99) => (throws #"Assert"))


;; `consume-stack [stackname :as local]`


(fact "`consume-stack` saves the entire stack into the named scratch variable"
  (get-stack-from-dslblob :integer (consume-stack [afew {}] :integer :as :foo)) =>
    '()
  (get-local-from-dslblob :foo (consume-stack [afew {}] :integer :as :foo)) => '(1 2 3) )


(fact "`consume-stack` works when the stack is empty"
  (get-local-from-dslblob :foo (consume-stack [afew {}] :boolean :as :foo)) => '() )


(fact "`consume-stack` throws an exception when the stack isn't defined"
  (consume-stack [afew {}] :quux :as :foo) =>
    (throws #"Push DSL argument error: no :quux stackname registered"))


(fact "`consume-stack` throws an Exception when no local is specified"
  (consume-stack [afew {}] :integer) =>
    (throws #"Push DSL argument error: missing key"))


;; `delete-stack [stackname]`


(fact "`delete-stack` discards the named stack"
  (get-stack-from-dslblob :integer
    (delete-stack [afew {}] :integer)) => '()
  (second (delete-stack [afew {}] :integer)) => {})


(fact "`delete-stack` raises an exception if the stack doesn't exist"
  (delete-stack [afew {}] :quux :as :foo) =>
    (throws #"Push DSL argument error: no :quux stackname registered"))
