(ns push.instructions.dsl-test
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u])
  (:require [push.interpreter.core :as i])
  (:require [push.instructions.core :as inst])
  (:use push.instructions.dsl)
  )

;; convenience functions for testing


(defn get-local-from-dslblob
  [kwd dslblob]
  (kwd (second dslblob)))


;; fixtures


(def nada (i/basic-interpreter))
(def afew (i/basic-interpreter :stacks {:integer '(1 2 3)}))
(def lots (i/basic-interpreter :stacks {:code (range 1 20)}))


;; count-of


(facts "about `count-of`"
  (fact "`count-of` saves the number of items on the named stack in the specified local"
    (get-local-from-dslblob :foo
      (count-of [nada {}] :integer :as :foo)) => 0
    (get-local-from-dslblob :foo
      (count-of [afew {}] :integer :as :foo)) => 3
    (get-local-from-dslblob :foo
      (count-of [lots {}] :code :as :foo)) => 19)


  (fact "`count-of` throws an Exception when the named stack doesn't exist"
    (count-of [nada {}] :foo :as :badidea) =>
      (throws #"no :foo stack"))


  (fact "`count-of` throws an Exception when no local is specified"
    (count-of [afew {}] :integer) => (throws #"missing key: :as")))


;; `delete-top-of [stackname]`


(defn get-stack-from-dslblob
  [stackname dslblob]
  (u/get-stack (first dslblob) stackname))


(facts "about `delete-top-of`"

  (fact "`delete-top-of` deletes the top item of a stack"
    (get-stack-from-dslblob :integer
      (delete-top-of [afew {}] :integer)) => '(2 3))


  (fact "`delete-top-of` raises an Exception if the stack doesn't exist"
    (delete-top-of [afew {}] :stupid) => (throws #"no :stupid stack"))


  (fact "`delete-top-of` raises an Exception if the stack is empty"
    (delete-top-of [nada {}] :integer) => 
      (throws #"stack :integer is empty"))


  (fact "`delete-top-of` works on :boolean stacks containing false values"
    (get-stack-from-dslblob :boolean
      (delete-top-of
        [(i/basic-interpreter :stacks {:boolean '(false true)}) {}]
        :boolean)) => '(true)))


;; `consume-top-of [stackname :as local]`


(facts "about `consume-top-of`"

  (fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
    (get-stack-from-dslblob :integer
      (consume-top-of [afew {}] :integer :as :foo)) => '(2 3)
    (get-local-from-dslblob :foo
      (consume-top-of [afew {}] :integer :as :foo)) => 1 )


  (fact "`consume-top-of` overwrites locals that already exist"
    (get-local-from-dslblob :foo
      (consume-top-of [afew {:foo \f}] :integer :as :foo)) => 1)


  (fact "`consume-top-of` throws an exception when no local is given"
    (consume-top-of [afew {:foo \f}] :integer) => (throws #"missing key: :as"))



  (fact "`consume-top-of` works with a :boolean stack of falses"
    (consume-top-of [(i/basic-interpreter :stacks {:boolean '(false false)}) {:foo \f}]
      :boolean :as :foo) =not=> (throws)))


;; delete-nth

(facts "about `delete-nth`"

  (fact "`delete-nth` returns a collection with the nth item removed"
    (#'push.instructions.dsl/delete-nth '(0 1 2 3 4 5) 3) => '(0 1 2 4 5)
    (#'push.instructions.dsl/delete-nth '(0 1 2 3 4 5) 0) => '(1 2 3 4 5)
    (#'push.instructions.dsl/delete-nth '(0 1 2 3 4 5) 5) => '(0 1 2 3 4))


  (fact "`delete-nth` throws an Exception when the list is empty"
    (#'push.instructions.dsl/delete-nth '() 3) => (throws #"Assert"))


  (fact "`delete-nth` throws an Exception when the index is out of range"
    (#'push.instructions.dsl/delete-nth '(0 1 2 3 4 5) -99) => (throws #"Assert")
    (#'push.instructions.dsl/delete-nth '(0 1 2 3 4 5) 99) => (throws #"Assert")))


;; insert-as-nth


(facts "about `insert-as-nth`"

  (fact "`insert-as-nth` returns a collection with the item inserted at position n"
    (#'push.instructions.dsl/insert-as-nth '(0 1 2 3 4 5) \X 3) => '(0 1 2 \X 3 4 5)
    (#'push.instructions.dsl/insert-as-nth '(0 1 2 3 4 5) \X 0) => '(\X 0 1 2 3 4 5)
    (#'push.instructions.dsl/insert-as-nth '(0 1 2 3 4 5) \X 6) => '(0 1 2 3 4 5 \X))


  (fact "`insert-as-nth` returns a PersistentList"
    (class (#'push.instructions.dsl/insert-as-nth '(1 2 3 4) \X 3)) => 
    clojure.lang.PersistentList)


  (fact "`insert-as-nth` DOES NOT throw an Exception when the list is empty"
    (#'push.instructions.dsl/insert-as-nth '() \X 0) =not=> (throws)
    (#'push.instructions.dsl/insert-as-nth '() \X 0) =not=> (throws))


  (fact "`insert-as-nth` throws an Exception when the index is out of range"
    (#'push.instructions.dsl/insert-as-nth '(0 1 2 3 4 5) \X -99) => (throws #"Assert")
    (#'push.instructions.dsl/insert-as-nth '(0 1 2 3 4 5) \X 99) => (throws #"Assert")))


;; `consume-stack [stackname :as local]`


(facts "about `consume-stack`"

  (fact "`consume-stack` saves the entire stack into the named scratch variable"
    (get-stack-from-dslblob :integer
      (consume-stack [afew {}] :integer :as :foo)) => '()
    (get-local-from-dslblob :foo
      (consume-stack [afew {}] :integer :as :foo)) => '(1 2 3) )


  (fact "`consume-stack` works when the stack is empty"
    (get-local-from-dslblob :foo
      (consume-stack [afew {}] :boolean :as :foo)) => '() )


  (fact "`consume-stack` throws an exception when the stack isn't defined"
    (consume-stack [afew {}] :quux :as :foo) =>
      (throws #"no :quux stack"))


  (fact "`consume-stack` throws an Exception when no local is specified"
    (consume-stack [afew {}] :integer) =>
      (throws #"missing key: :as")))


;; `delete-stack [stackname]`


(facts "about `delete-stack`"

  (fact "`delete-stack` discards the named stack"
    (get-stack-from-dslblob :integer
      (delete-stack [afew {}] :integer)) => '()
    (second (delete-stack [afew {}] :integer)) => {})


  (fact "`delete-stack` raises an exception if the stack doesn't exist"
    (delete-stack [afew {}] :quux) =>
      (throws #"no :quux stack")))


;; `index-from-scratch-ref [key hashmap]`


(facts "about `index-from-scratch-ref`"

  (fact "index-from-scratch-ref returns an integer if one is stored"
    (#'push.instructions.dsl/index-from-scratch-ref :foo {:foo 8}) => 8)


  (fact "index-from-scratch-ref throws up if the stored value isn't an integer"
    (#'push.instructions.dsl/index-from-scratch-ref :foo {:foo false}) => 
      (throws #":foo is not an integer"))


  (fact "index-from-scratch-ref throws up if the key is not present"
    (#'push.instructions.dsl/index-from-scratch-ref :bar {:foo 2}) => 
      (throws #":bar is not an integer")))


;; `delete-nth-of [stackname :at where]`


(facts "about `delete-nth-of`"

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
      (throws #":foo is not an integer")
    (delete-nth-of [afew {:foo 1}] :integer :at :bar) => 
      (throws #":bar is not an integer"))


  (fact "`delete-nth-of` throws up if no index is given"
    (delete-nth-of [afew {:foo false}] :integer) => 
      (throws #"missing key: :at"))


  (fact "`delete-nth-of` throws up if the stack is empty"
    (delete-nth-of [afew {}] :boolean :at 7) => 
      (throws #"stack :boolean is empty")))


;; `replace-stack [stackname local]`


(facts "about `replace-stack`"

  (fact "`replace-stack` sets the named stack to the value of the local if it is a list"
    (get-stack-from-dslblob :integer
      (replace-stack [afew {:foo '(4 5 6)}] :integer :foo)) => '(4 5 6)
    )


  (fact "`replace-stack` empties a stack if the local is not defined"
    (get-stack-from-dslblob :integer
      (replace-stack [afew {}] :integer :foo)) => '())


  (fact "`replace-stack` replaces the stack with just the item in a list otherwise"
    (get-stack-from-dslblob :integer
      (replace-stack [afew {:foo false}] :integer :foo)) => '(false))


  (fact "`replace-stack` throws an Exception when the named stack doesn't exist"
    (replace-stack [nada {:bar 1}] :foo :bar) => (throws #"no :foo stack")))


;; `push-onto [stackname local]`


(facts "about `push-onto`"

  (fact "`push-onto` places the indicated scratch item onto the named stack"
    (get-stack-from-dslblob :integer
      (push-onto [afew {:foo 99}] :integer :foo)) => '(99 1 2 3))


  (fact "`push-onto` throws up if the stack doesn't exist"
    (push-onto [afew {:foo 99}] :grault :foo) =>
      (throws #"no :grault stack"))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable isn't set"
    (get-stack-from-dslblob :integer
      (push-onto [afew {}] :integer :foo)) => '(1 2 3))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable is a list"
    (get-stack-from-dslblob :integer
      (push-onto [afew {:foo '(4 5 6)}] :integer :foo)) => '((4 5 6) 1 2 3)))


;; `save-stack [stackname :as local]`


(facts "about `save-stack`"

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
      (throws #"no :grault"))


  (fact "`save-stack` throws up if you leave out the :as argument"
    (save-stack [afew {}] :integer ) =>
      (throws #"missing key: :as")))


;; `save-top-of [stackname :as local]`


(facts "about `save-top-of`"

  (fact "`save-top-of` puts the top item on the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (save-top-of [afew {}] :integer :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-top-of [afew {}] :integer :as :bar)) => 1)


  (fact "`save-top-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (save-top-of [afew {:foo false}] :integer :as :foo)) => 1)


  (fact "`save-top-of` throws up if you ask for an undefined stack"
    (save-top-of [afew {}] :grault :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`save-top-of` throws up if you try to pop an empty stack"
    (save-top-of [afew {}] :boolean :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-top-of` throws up if you forget the :as argument"
    (save-top-of [afew {}] :integer) => (throws #"missing key: :as"))


  (fact "`save-top-of` works on :boolean stacks containing false"
    (save-top-of [(i/basic-interpreter :stacks {:boolean '(false)}) {}] :boolean :as :foo)
      =not=> (throws)))


;; `save-nth-of [stackname :at where :as local]`


(facts "about `save-nth-of"

  (fact "given an integer index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (save-nth-of [afew {}] :integer :at 1 :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-nth-of [afew {}] :integer :at 1 :as :bar)) => 2)


  (fact "given an keyword index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :integer
      (save-nth-of [afew {:foo 2}] :integer :at :foo :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-nth-of [afew {:foo 2}] :integer :at :foo :as :bar)) => 3)


  (fact "`save-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :integer :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :integer :at -1 :as :foo)) => 3)


  (fact "`save-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :integer :at 1 :as :foo)) => 2)


  (fact "`save-nth-of` throws up if you ask for an undefined stack"
    (save-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`save-nth-of` throws up if the keyword index doesn't point to an integer"
    (save-nth-of [afew {:foo false}] :integer :at :foo :as :bar) =>
      (throws #":foo is not an integer"))


  (fact "`save-nth-of` throws up if you try to pop an empty stack"
    (save-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-nth-of` throws up if you forget the :as argument"
    (save-nth-of [afew {}] :integer :at 8) =>
      (throws #"missing key: :as"))


  (fact "`save-nth-of` throws up if you forget the :at argument"
    (save-nth-of [afew {}] :integer :as :foo) =>
      (throws #"missing key: :at")))


;; `consume-nth-of [stackname :at where :as local]`


(facts "about `consume-nth-of`"

  (fact "given an integer index, `consume-nth-of` puts the indicated item from the named stack into a scratch variable, deleting it"
    (get-stack-from-dslblob :integer
      (consume-nth-of [afew {}] :integer :at 1 :as :bar)) => '(1 3)
    (get-local-from-dslblob :bar
      (consume-nth-of [afew {}] :integer :at 1 :as :bar)) => 2)


  (fact "`consume-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :integer :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :integer :at -1 :as :foo)) => 3)


  (fact "`consume-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :integer :at 1 :as :foo)) => 2)


  (fact "`consume-nth-of` throws up if you ask for an undefined stack"
    (consume-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"no :grault stack"))


  (fact "`consume-nth-of` throws up if the keyword index doesn't point to an integer"
    (consume-nth-of [afew {:foo false}] :integer :at :foo :as :bar) =>
      (throws #":foo is not an integer"))


  (fact "`consume-nth-of` throws up if you try to pop an empty stack"
    (consume-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`consume-nth-of` throws up if you forget the :as argument"
    (consume-nth-of [afew {}] :integer :at 8) =>
      (throws #"missing key: :as"))


  (fact "`consume-nth-of` throws up if you forget the :at argument"
    (consume-nth-of [afew {}] :integer :as :foo) =>
      (throws #"missing key: :at")))


;; `get-nth-of [stackname :at where]` (shared functionality)


(facts "about `get-nth-of`"

  (fact "given an integer index, `get-nth-of` returns the index and the item in the named stack"
    (#'push.instructions.dsl/get-nth-of [afew {}] :integer :at 1) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds numeric indices"
    (#'push.instructions.dsl/get-nth-of [afew {}] :integer :at -1) =>
      [2 '(1 2 3)]
    (#'push.instructions.dsl/get-nth-of [afew {}] :integer :at 10) =>
      [1 '(1 2 3)])


  (fact "given a keyword index, `get-nth-of` returns the index and the item in the named stack"
    (#'push.instructions.dsl/get-nth-of [afew {:foo 1}] :integer :at :foo) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds keyword indices"
    (#'push.instructions.dsl/get-nth-of [afew {:foo -1}] :integer :at :foo) =>
      [2 '(1 2 3)]
    (#'push.instructions.dsl/get-nth-of [afew {:foo 10}] :integer :at :foo) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of` throws up if you ask for an undefined stack"
    (#'push.instructions.dsl/get-nth-of [afew {}] :grault :at 2) =>
      (throws #"no :grault stack"))


  (fact "`get-nth-of` throws up if the keyword index doesn't point to an integer"
    (#'push.instructions.dsl/get-nth-of [afew {:foo false}] :integer :at :foo) =>
      (throws #":foo is not an integer")
    (#'push.instructions.dsl/get-nth-of [afew {}] :integer :at :foo) =>
      (throws #":foo is not an integer"))


  (fact "`get-nth-of` throws up if you refer to an empty stack"
    (#'push.instructions.dsl/get-nth-of [afew {}] :boolean :at 6) =>
      (throws #"stack :boolean is empty"))


  (fact "`get-nth-of` works with a stack full of false values"
    (#'push.instructions.dsl/get-nth-of
      [(i/basic-interpreter :stacks {:boolean '(false false)}) {}] :boolean :at 6) =not=>
        (throws)))


;; `calculate [[args] fn :as local]`


(facts "about `calculate`"

  (fact "calculate maps the function onto the indicated scratch items and stores the result in the named local"
    (get-local-from-dslblob :sum
      (calculate [afew {:a 8 :b 2}] [:a :b] #(+ %1 %2) :as :sum)) => 10
    (get-local-from-dslblob :min
      (calculate [afew {:a 8 :b 2}] [:a :b] #(min %1 %2) :as :min)) => 2
    (get-local-from-dslblob :choice
      (calculate [afew {:a 8 :b 2 :c true}] 
                       [:c :a :b]
                       #(if %1 %2 %3) 
                       :as :choice)) => 8)


  (fact "`calculate` throws up if you forget the :as argument"
    (calculate [afew {:a 8 :b 2}] [:a :b] #(min %1 %2)) =>
      (throws #"missing key: :as"))


  (fact "`calculate` throws up if the args are not a vector"
    (calculate [afew {:a 8 :b 2}] :a #(%1)) =>
      (throws #"error: ':a' can't be parsed"))


  (fact "`calculate` throws up if the args are not a vector"
    (calculate [afew {:a 8 :b 2}] [:a] #(+ %1 %2) :as :foo) =>
      (throws #"Wrong number of args"))


  (fact "`calculate` is fine with nil"
    (calculate [afew {:a nil}] [:a] #(if %1 2 3) :as :foo) =not=>
      (throws Exception)))


;;;; working with DSL transactions


;; inst/needs-of-dsl-step


(fact "`needs-of-dsl-step` returns a hashmap containing the needs for every DSL instruction"
  (inst/needs-of-dsl-step 
    '(calculate [:a :b] #(+ %1 %2) :as :sum)) => {}
  (inst/needs-of-dsl-step 
    '(consume-nth-of :integer :at 1 :as :bar)) => {:integer 1}
  (inst/needs-of-dsl-step
    '(save-nth-of :integer :at 11 :as :foo))  => {:integer 1}
  (inst/needs-of-dsl-step
    '(save-top-of :integer :as :bar))  => {:integer 1}
  (inst/needs-of-dsl-step
    '(save-stack :integer :as :bar))  => {:integer 0}
  (inst/needs-of-dsl-step
    '(push-onto :integer :foo))  => {:integer 0}
  (inst/needs-of-dsl-step
    '(replace-stack :integer :foo))  => {:integer 0}
  (inst/needs-of-dsl-step
    '(delete-nth-of :integer :at 1))  => {:integer 1}
  (inst/needs-of-dsl-step
    '(delete-stack :integer))  => {:integer 0}
  (inst/needs-of-dsl-step
    '(consume-stack :integer :as :foo))  => {:integer 0}
  (inst/needs-of-dsl-step
    '(consume-top-of :integer :as :foo)) => {:integer 1}
  (inst/needs-of-dsl-step
    '(delete-top-of :integer)) => {:integer 1}
  (inst/needs-of-dsl-step
    '(count-of :integer :as :foo)) => {:integer 0})


(fact "`inst/needs-of-dsl-step` throws an exception for unknown DSL instructions"
  (inst/needs-of-dsl-step '(bad-idea-instruction :foo 8 :bar)) =>
    (throws #"parse error: 'bad-idea-instruction' is not"))


;; inst/total-needs


(fact "`inst/total-needs` takes a whole transaction and sums up all the needs of each item"
  (inst/total-needs 
    ['(consume-nth-of :integer :at 1 :as :bar)]) => {:integer 1}

  (inst/total-needs 
    ['(consume-top-of :integer :as :bar)
     '(consume-nth-of :integer :at 1 :as :bar)]) => {:integer 2}

  (inst/total-needs 
    ['(delete-nth-of :integer :at 1)
     '(consume-top-of :integer :as :arg1)
     '(consume-top-of :integer :as :arg2)
     '(consume-top-of :boolean :as :b1)
     '(consume-top-of :foo :as :foo1)]) => {:boolean 1, :foo 1, :integer 3}

  (inst/total-needs 
    ['(calculate [] #(33) :as :tt)]) => {}


  (inst/total-needs 
    ['(consume-top-of :integer :as :arg1)
     '(consume-top-of :integer :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push-onto :integer :m)]) => {:integer 2} )


(fact "`inst/total-needs` throws up when it sees bad DSL code"
  (inst/total-needs 
    ['(consume-top-of :integer :as :arg1)
     '(consume-top-of :integer :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push :integer :m)]) =>
    (throws "Push DSL parse error: 'push' is not a known instruction."))


;; inst/def-function-from-dsl


(fact "`inst/def-function-from-dsl` produces a function from zero or more DSL commands"
  (fn? (macroexpand-1 (inst/def-function-from-dsl 
    (consume-top-of :integer :as :bar) ))) => true
  (fn? (macroexpand-1 (inst/def-function-from-dsl ))) => true
  (fn? (macroexpand-1 (inst/def-function-from-dsl 
    (consume-top-of :integer :as :bar)
    (consume-top-of :integer :as :bar) ))) => true)


(fact "applying that function to an Interpreter produces an Interpreter result"
  (let [int-add (inst/def-function-from-dsl 
                  (consume-top-of :integer :as :arg1)
                  (consume-top-of :integer :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :integer :sum))]
  (class (int-add afew)) => push.interpreter.core.Interpreter))


(fact "applying the function does the things it's supposed to"
  (let [int-add (inst/def-function-from-dsl 
                  (consume-top-of :integer :as :arg1)
                  (consume-top-of :integer :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :integer :sum))]
  (u/get-stack (int-add afew) :integer) => '(3 3)))


;; `push-these-onto [stackname [locals]]`


(facts "about `push-these-onto`"

  (fact "`push-these-onto` places all indicated scratch items onto the named stack"
    (get-stack-from-dslblob :integer
      (push-these-onto
        [afew {:foo 99 :bar 111}]
        :integer
        [:foo :bar])) => '(111 99 1 2 3))


  (fact "`push-these-onto` throws up if the stack doesn't exist"
    (push-these-onto [afew {:foo 99}] :grault [:foo]) =>
      (throws #"no :grault stack"))


  (fact "`push-these-onto` doesn't raise a fuss if a scratch variable isn't set"
    (get-stack-from-dslblob :integer
      (push-these-onto [afew {}] :integer [:foo])) => '(1 2 3)
        (get-stack-from-dslblob :integer
      (push-these-onto [afew {:foo 99 :bar 111}] :integer [:foo :qux])) => 
        '(99 1 2 3))


  (fact "`push-these-onto` doesn't raise a fuss if the vector's empty"
    (get-stack-from-dslblob :integer
      (push-these-onto [afew {:foo 99 :bar 111}] :integer [])) => '(1 2 3))


  (fact "`push-onto` doesn't care if a scratch variable is a list"
    (get-stack-from-dslblob :integer
      (push-these-onto [afew {:foo '(4 5 6)}] :integer [:foo])) =>
        '((4 5 6) 1 2 3)))



;; `insert-as-nth-of [stackname local :at where]`


(facts "about `insert-as-nth-of`"

  (fact "`insert-as-nth-of` puts the named scratch item in position n of the named stack"
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 0)) => '(99 1 2 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 1)) => '(1 99 2 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 2)) => '(1 2 99 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 3)) => '(1 2 3 99))


  (fact "`insert-as-nth-of` picks the index as `(mod where stacklength)`"
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at -1)) => '(1 2 3 99)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 4)) => '(99 1 2 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at 6)) => '(1 2 99 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at -5)) => '(1 2 3 99))


  (fact "`insert-as-nth-of` will use a scratch variable as index if it's an integer"
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99 :bar 1}] :integer :foo :at :bar)) =>
        '(1 99 2 3)
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99 :bar -1}] :integer :foo :at :bar)) =>
        '(1 2 3 99))


  (fact "`insert-as-nth-of` throws up if the scratch variable isn't an integer"
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99 :bar false}] :integer :foo :at :bar)) =>
        (throws #"Push DSL argument error: :bar is not an integer")
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo :at :bar)) =>
        (throws #"Push DSL argument error: :bar is not an integer"))


  (fact "`insert-as-nth-of` throws up if no index is given"
    (get-stack-from-dslblob :integer
      (insert-as-nth-of [afew {:foo 99}] :integer :foo)) =>
        (throws #"Push DSL argument error: missing key: :at"))

  (fact "`insert-as-nth-of` is OK if the stack is empty"
    (get-stack-from-dslblob :boolean
      (insert-as-nth-of [afew {:foo 99}] :boolean :foo :at 8182)) =>
        '(99)))
