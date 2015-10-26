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


(fact "`count-of` throws an Exception when no local is specified"
  (count-of [afew {}] :integer) => (throws #"Push DSL argument error: missing key"))


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
  (delete-stack [afew {}] :quux) =>
    (throws #"Push DSL argument error: no :quux stackname registered"))


;; `index-from-scratch-ref [key hashmap]`

(fact "index-from-scratch-ref returns an integer if one is stored"
  (index-from-scratch-ref :foo {:foo 8}) => 8)


(fact "index-from-scratch-ref throws up if the stored value isn't an integer"
  (index-from-scratch-ref :foo {:foo false}) => 
    (throws #"Push DSL argument error: :foo is not an integer"))


(fact "index-from-scratch-ref throws up if the key is not present"
  (index-from-scratch-ref :bar {:foo 2}) => 
    (throws #"Push DSL argument error: :bar is not an integer"))

;; `delete-nth-of [stackname :at where]`


(fact "`delete-nth-of` discards the indicated item given an integer location"
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {}] :integer :at 1)) => '(1 3))


(fact "`delete-nth-of` picks the index as `(mod where stacklength)`"
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {}] :integer :at -2)) => '(1 3)
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {}] :integer :at -3)) => '(2 3))


(fact "`delete-nth-of` discards the indicated item given scratch ref to integer"
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {:foo 1}] :integer :at :foo)) => '(1 3)
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {:foo -1}] :integer :at :foo)) => '(1 2)
  (get-stack-from-dslblob :integer
    (delete-nth-of [afew {:foo 3}] :integer :at :foo)) => '(2 3))


(fact "`delete-nth-of` throws up given a scratch ref to non-integer"
  (delete-nth-of [afew {:foo false}] :integer :at :foo) => 
    (throws #"Push DSL argument error: :foo is not an integer")
  (delete-nth-of [afew {:foo 1}] :integer :at :bar) => 
    (throws #"Push DSL argument error: :bar is not an integer"))


(fact "`delete-nth-of` throws up if no index is given"
  (delete-nth-of [afew {:foo false}] :integer) => 
    (throws #"Push DSL argument error: missing key"))


;; `replace-stack [stackname local]`


(fact "`replace-stack` sets the named stack to the value of the local if it is a list"
  (get-stack-from-dslblob :integer
    (replace-stack [afew {:foo '(4 5 6)}] :integer :foo)) => '(4 5 6))


(fact "`replace-stack` empties a stack if the local is not defined"
  (get-stack-from-dslblob :integer
    (replace-stack [afew {}] :integer :foo)) => '())


(fact "`replace-stack` replaces the stack with just the item in a list otherwise"
  (get-stack-from-dslblob :integer
    (replace-stack [afew {:foo false}] :integer :foo)) => '(false))


(fact "`replace-stack` throws an Exception when the named stack doesn't exist"
  (replace-stack [nada {:bar 1}] :foo :bar) => (throws #"Push DSL argument error: no "))


;; `push-onto [stackname local]`


(fact "`push-onto` places the indicated scratch item onto the named stack"
  (get-stack-from-dslblob :integer
    (push-onto [afew {:foo 99}] :integer :foo)) => '(99 1 2 3))


(fact "`push-onto` throws up if the stack doesn't exist"
  (push-onto [afew {:foo 99}] :grault :foo) =>
    (throws #"Push DSL argument error: no :grault"))


(fact "`push-onto` doesn't raise a fuss if the scratch variable isn't set"
  (get-stack-from-dslblob :integer
    (push-onto [afew {}] :integer :foo)) => '(1 2 3))


(fact "`push-onto` doesn't raise a fuss if the scratch variable is a list"
  (get-stack-from-dslblob :integer
    (push-onto [afew {:foo '(4 5 6)}] :integer :foo)) => '((4 5 6) 1 2 3))


;; `save-stack [stackname :as local]`

(fact "`save-stack` puts the entire named stack into a scratch variable (without deleting it)"
  (get-stack-from-dslblob :integer
    (save-stack [afew {}] :integer :as :bar)) => '(1 2 3)
  (get-local-from-dslblob :bar
    (save-stack [afew {}] :integer :as :bar)) => '(1 2 3))


(fact "`save-stack` overwrites the scratch variable if asked to"
  (get-local-from-dslblob :foo
    (save-stack [afew {:foo false}] :integer :as :foo)) => '(1 2 3))


(fact "`save-stack` throws up if you ask for an undefined stack"
  (save-stack [afew {:foo 99}] :grault :as :foo) =>
    (throws #"Push DSL argument error: no :grault"))


(fact "`save-stack` throws up if you ask forget the :as argument"
  (save-stack [afew {}] :integer ) =>
    (throws #"Push DSL argument error: missing key: :as"))
