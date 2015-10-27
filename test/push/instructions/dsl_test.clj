(ns push.instructions.dsl-test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:require [push.instructions.dsl :as d]))

;; convenience functions for testing

(defn get-local-from-dslblob
  [kwd dslblob]
  (kwd (second dslblob)))


(def nada (i/make-interpreter))
(def afew (i/make-interpreter :stacks {:integer '(1 2 3)}))
(def lots (i/make-interpreter :stacks {:code (range 1 20)}))


;; count-of

(facts "about `count-of`"
  (fact "`count-of` saves the number of items on the named stack in the specified local"
    (get-local-from-dslblob :foo
      (d/count-of [nada {}] :integer :as :foo)) => 0
    (get-local-from-dslblob :foo
      (d/count-of [afew {}] :integer :as :foo)) => 3
    (get-local-from-dslblob :foo
      (d/count-of [lots {}] :code :as :foo)) => 19)


  (fact "`count-of` throws an Exception when the named stack doesn't exist"
    (d/count-of [nada {}] :foo :as :badidea) =>
      (throws #"no :foo stack"))


  (fact "`count-of` throws an Exception when no local is specified"
    (d/count-of [afew {}] :integer) => (throws #"missing key: :as")))


;; `delete-top-of [stackname]`


(defn get-stack-from-dslblob
  [stackname dslblob]
  (i/get-stack (first dslblob) stackname))


(facts "about `delete-top-of`"

  (fact "`delete-top-of` deletes the top item of a stack"
    (get-stack-from-dslblob :integer
      (d/delete-top-of [afew {}] :integer)) => '(2 3))


  (fact "`delete-top-of` raises an Exception if the stack doesn't exist"
    (d/delete-top-of [afew {}] :stupid) => (throws #"no :stupid stack"))


  (fact "`delete-top-of` raises an Exception if the stack is empty"
    (d/delete-top-of [nada {}] :integer) => 
      (throws #"stack :integer is empty")))


;; `consume-top-of [stackname :as local]`


(facts "about `consume-top-of`"

  (fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
    (get-stack-from-dslblob :integer
      (d/consume-top-of [afew {}] :integer :as :foo)) => '(2 3)
    (get-local-from-dslblob :foo
      (d/consume-top-of [afew {}] :integer :as :foo)) => 1 )


  (fact "`consume-top-of` overwrites locals that already exist"
    (get-local-from-dslblob :foo
      (d/consume-top-of [afew {:foo \f}] :integer :as :foo)) => 1)


  (fact "`consume-top-of` throws an exception when no local is given"
    (d/consume-top-of [afew {:foo \f}] :integer) => (throws #"missing key: :as")))


;; delete-nth

(facts "about `delete-nth`"

  (fact "`delete-nth` returns a collection with the nth item removed"
    (#'d/delete-nth '(0 1 2 3 4 5) 3) => '(0 1 2 4 5)
    (#'d/delete-nth '(0 1 2 3 4 5) 0) => '(1 2 3 4 5)
    (#'d/delete-nth '(0 1 2 3 4 5) 5) => '(0 1 2 3 4))


  (fact "`delete-nth` throws an Exception when the list is empty"
    (#'d/delete-nth '() 3) => (throws #"Assert"))


  (fact "`delete-nth` throws an Exception when the index is out of range"
    (#'d/delete-nth '(0 1 2 3 4 5) -99) => (throws #"Assert")
    (#'d/delete-nth '(0 1 2 3 4 5) 99) => (throws #"Assert")))


;; `consume-stack [stackname :as local]`

(facts "about `consume-stack`"

  (fact "`consume-stack` saves the entire stack into the named scratch variable"
    (get-stack-from-dslblob :integer
      (d/consume-stack [afew {}] :integer :as :foo)) => '()
    (get-local-from-dslblob :foo
      (d/consume-stack [afew {}] :integer :as :foo)) => '(1 2 3) )


  (fact "`consume-stack` works when the stack is empty"
    (get-local-from-dslblob :foo
      (d/consume-stack [afew {}] :boolean :as :foo)) => '() )


  (fact "`consume-stack` throws an exception when the stack isn't defined"
    (d/consume-stack [afew {}] :quux :as :foo) =>
      (throws #"no :quux stack"))


  (fact "`consume-stack` throws an Exception when no local is specified"
    (d/consume-stack [afew {}] :integer) =>
      (throws #"missing key: :as")))


;; `delete-stack [stackname]`

(facts "about `delete-stack`"

  (fact "`delete-stack` discards the named stack"
    (get-stack-from-dslblob :integer
      (d/delete-stack [afew {}] :integer)) => '()
    (second (d/delete-stack [afew {}] :integer)) => {})


  (fact "`delete-stack` raises an exception if the stack doesn't exist"
    (d/delete-stack [afew {}] :quux) =>
      (throws #"no :quux stack")))


;; `index-from-scratch-ref [key hashmap]`

(facts "about `index-from-scratch-ref`"

  (fact "index-from-scratch-ref returns an integer if one is stored"
    (#'d/index-from-scratch-ref :foo {:foo 8}) => 8)


  (fact "index-from-scratch-ref throws up if the stored value isn't an integer"
    (#'d/index-from-scratch-ref :foo {:foo false}) => 
      (throws #":foo is not an integer"))


  (fact "index-from-scratch-ref throws up if the key is not present"
    (#'d/index-from-scratch-ref :bar {:foo 2}) => 
      (throws #":bar is not an integer")))


;; `delete-nth-of [stackname :at where]`

(facts "about `delete-nth-of`"

  (fact "`delete-nth-of` discards the indicated item given an integer location"
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {}] :integer :at 1)) => '(1 3))


  (fact "`delete-nth-of` picks the index as `(mod where stacklength)`"
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {}] :integer :at -2)) => '(1 3)
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {}] :integer :at -3)) => '(2 3))


  (fact "`delete-nth-of` discards the indicated item given scratch ref to integer"
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {:foo 1}] :integer :at :foo)) => '(1 3)
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {:foo -1}] :integer :at :foo)) => '(1 2)
    (get-stack-from-dslblob :integer
      (d/delete-nth-of [afew {:foo 3}] :integer :at :foo)) => '(2 3))


  (fact "`delete-nth-of` throws up given a scratch ref to non-integer"
    (d/delete-nth-of [afew {:foo false}] :integer :at :foo) => 
      (throws #":foo is not an integer")
    (d/delete-nth-of [afew {:foo 1}] :integer :at :bar) => 
      (throws #":bar is not an integer"))


  (fact "`delete-nth-of` throws up if no index is given"
    (d/delete-nth-of [afew {:foo false}] :integer) => 
      (throws #"missing key: :at"))


  (fact "`delete-nth-of` throws up if the stack is empty"
    (d/delete-nth-of [afew {}] :boolean :at 7) => 
      (throws #"stack :boolean is empty")))


;; `replace-stack [stackname local]`

(facts "about `replace-stack`"

  (fact "`replace-stack` sets the named stack to the value of the local if it is a list"
    (get-stack-from-dslblob :integer
      (d/replace-stack [afew {:foo '(4 5 6)}] :integer :foo)) => '(4 5 6))


  (fact "`replace-stack` empties a stack if the local is not defined"
    (get-stack-from-dslblob :integer
      (d/replace-stack [afew {}] :integer :foo)) => '())


  (fact "`replace-stack` replaces the stack with just the item in a list otherwise"
    (get-stack-from-dslblob :integer
      (d/replace-stack [afew {:foo false}] :integer :foo)) => '(false))


  (fact "`replace-stack` throws an Exception when the named stack doesn't exist"
    (d/replace-stack [nada {:bar 1}] :foo :bar) => (throws #"no :foo stack")))


;; `push-onto [stackname local]`

(facts "about `push-onto`"

  (fact "`push-onto` places the indicated scratch item onto the named stack"
    (get-stack-from-dslblob :integer
      (d/push-onto [afew {:foo 99}] :integer :foo)) => '(99 1 2 3))


  (fact "`push-onto` throws up if the stack doesn't exist"
    (d/push-onto [afew {:foo 99}] :grault :foo) =>
      (throws #"no :grault stack"))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable isn't set"
    (get-stack-from-dslblob :integer
      (d/push-onto [afew {}] :integer :foo)) => '(1 2 3))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable is a list"
    (get-stack-from-dslblob :integer
      (d/push-onto [afew {:foo '(4 5 6)}] :integer :foo)) => '((4 5 6) 1 2 3)))


;; `save-stack [stackname :as local]`

(facts "about `save-stack`"

  (fact "`save-stack` puts the entire named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (d/save-stack [afew {}] :integer :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (d/save-stack [afew {}] :integer :as :bar)) => '(1 2 3))


  (fact "`save-stack` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (d/save-stack [afew {:foo false}] :integer :as :foo)) => '(1 2 3))


  (fact "`save-stack` throws up if you ask for an undefined stack"
    (d/save-stack [afew {:foo 99}] :grault :as :foo) =>
      (throws #"no :grault"))


  (fact "`save-stack` throws up if you leave out the :as argument"
    (d/save-stack [afew {}] :integer ) =>
      (throws #"missing key: :as")))


;; `save-top-of [stackname :as local]`

(facts "about `save-top-of`"

  (fact "`save-top-of` puts the top item on the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (d/save-top-of [afew {}] :integer :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (d/save-top-of [afew {}] :integer :as :bar)) => 1)


  (fact "`save-top-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (d/save-top-of [afew {:foo false}] :integer :as :foo)) => 1)


  (fact "`save-top-of` throws up if you ask for an undefined stack"
    (d/save-top-of [afew {}] :grault :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`save-top-of` throws up if you try to pop an empty stack"
    (d/save-top-of [afew {}] :boolean :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-top-of` throws up if you forget the :as argument"
    (d/save-top-of [afew {}] :integer) =>
      (throws #"missing key: :as")))


;; `save-nth-of [stackname :at where :as local]`

(facts "about `save-nth-of"

  (fact "given an integer index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (d/save-nth-of [afew {}] :integer :at 1 :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (d/save-nth-of [afew {}] :integer :at 1 :as :bar)) => 2)


  (fact "given an keyword index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (d/save-nth-of [afew {:foo 2}] :integer :at :foo :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (d/save-nth-of [afew {:foo 2}] :integer :at :foo :as :bar)) => 3)


  (fact "`save-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (d/save-nth-of [afew {:foo false}] :integer :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (d/save-nth-of [afew {:foo false}] :integer :at -1 :as :foo)) => 3)


  (fact "`save-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (d/save-nth-of [afew {:foo false}] :integer :at 1 :as :foo)) => 2)


  (fact "`save-nth-of` throws up if you ask for an undefined stack"
    (d/save-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`save-nth-of` throws up if the keyword index doesn't point to an integer"
    (d/save-nth-of [afew {:foo false}] :integer :at :foo :as :bar) =>
      (throws #":foo is not an integer"))


  (fact "`save-nth-of` throws up if you try to pop an empty stack"
    (d/save-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-nth-of` throws up if you forget the :as argument"
    (d/save-nth-of [afew {}] :integer :at 8) =>
      (throws #"missing key: :as"))


  (fact "`save-nth-of` throws up if you forget the :at argument"
    (d/save-nth-of [afew {}] :integer :as :foo) =>
      (throws #"missing key: :at")))


;; `consume-nth-of [stackname :at where :as local]`

(facts "about `consume-nth-of`"

  (fact "given an integer index, `consume-nth-of` puts the indicated item from the named stack into a scratch variable, deleting it"
    (get-stack-from-dslblob :integer
      (d/consume-nth-of [afew {}] :integer :at 1 :as :bar)) => '(1 3)
    (get-local-from-dslblob :bar
      (d/consume-nth-of [afew {}] :integer :at 1 :as :bar)) => 2)


  (fact "`consume-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (d/consume-nth-of [afew {:foo false}] :integer :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (d/consume-nth-of [afew {:foo false}] :integer :at -1 :as :foo)) => 3)


  (fact "`consume-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (d/consume-nth-of [afew {:foo false}] :integer :at 1 :as :foo)) => 2)


  (fact "`consume-nth-of` throws up if you ask for an undefined stack"
    (d/consume-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`consume-nth-of` throws up if the keyword index doesn't point to an integer"
    (d/consume-nth-of [afew {:foo false}] :integer :at :foo :as :bar) =>
      (throws #":foo is not an integer"))


  (fact "`consume-nth-of` throws up if you try to pop an empty stack"
    (d/consume-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`consume-nth-of` throws up if you forget the :as argument"
    (d/consume-nth-of [afew {}] :integer :at 8) =>
      (throws #"missing key: :as"))


  (fact "`consume-nth-of` throws up if you forget the :at argument"
    (d/consume-nth-of [afew {}] :integer :as :foo) =>
      (throws #"missing key: :at")))


;; `get-nth-of [stackname :at where]` (shared functionality)


(facts "about `get-nth-of`"

  (fact "given an integer index, `get-nth-of` returns the index and the item in the named stack"
    (#'d/get-nth-of [afew {}] :integer :at 1) => [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds numeric indices"
    (#'d/get-nth-of [afew {}] :integer :at -1) => [2 '(1 2 3)]
    (#'d/get-nth-of [afew {}] :integer :at 10) => [1 '(1 2 3)])


  (fact "given a keyword index, `get-nth-of` returns the index and the item in the named stack"
    (#'d/get-nth-of [afew {:foo 1}] :integer :at :foo) => [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds keyword indices"
    (#'d/get-nth-of [afew {:foo -1}] :integer :at :foo) => [2 '(1 2 3)]
    (#'d/get-nth-of [afew {:foo 10}] :integer :at :foo) => [1 '(1 2 3)])


  (fact "`get-nth-of` throws up if you ask for an undefined stack"
    (#'d/get-nth-of [afew {}] :grault :at 2) =>
      (throws #"no :grault stack"))


  (fact "`get-nth-of` throws up if the keyword index doesn't point to an integer"
    (#'d/get-nth-of [afew {:foo false}] :integer :at :foo) =>
      (throws #":foo is not an integer")
    (#'d/get-nth-of [afew {}] :integer :at :foo) =>
      (throws #":foo is not an integer"))


  (fact "`get-nth-of` throws up if you refer to an empty stack"
    (#'d/get-nth-of [afew {}] :boolean :at 6) =>
      (throws #"stack :boolean is empty")))



;; `calculate [[args] fn :as local]`

(facts "about `calculate`"

  (fact "calculate maps the function onto the indicated scratch items and stores the result in the named local"
    (get-local-from-dslblob :sum
      (d/calculate [afew {:a 8 :b 2}] [:a :b] #(+ %1 %2) :as :sum)) => 10
    (get-local-from-dslblob :min
      (d/calculate [afew {:a 8 :b 2}] [:a :b] #(min %1 %2) :as :min)) => 2
    (get-local-from-dslblob :choice
      (d/calculate [afew {:a 8 :b 2 :c true}] 
                       [:c :a :b]
                       #(if %1 %2 %3) 
                       :as :choice)) => 8)


  (fact "`calculate` throws up if you forget the :as argument"
    (d/calculate [afew {:a 8 :b 2}] [:a :b] #(min %1 %2)) =>
      (throws #"missing key: :as"))


  (fact "`calculate` throws up if the args are not a vector"
    (d/calculate [afew {:a 8 :b 2}] :a #(%1)) =>
      (throws #"error: ':a' can't be parsed"))


  (fact "`calculate` throws up if the args are not a vector"
    (d/calculate [afew {:a 8 :b 2}] [:a] #(+ %1 %2) :as :foo) =>
      (throws #"Wrong number of args"))


  (fact "`calculate` is fine with nil"
    (d/calculate [afew {:a nil}] [:a] #(if %1 2 3) :as :foo) =not=>
      (throws Exception)))
