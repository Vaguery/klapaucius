 (ns push.instructions.dsl-test
  (:require [push.util.stack-manipulation :as u]
            [push.interpreter.core :as i]
            [push.instructions.core :as inst]
            [push.core :as push]
            [push.type.definitions.snapshot :as snap]
            [push.util.code-wrangling :as fix]
            [push.interpreter.templates.minimum :as m])
  (:use midje.sweet)
  (:use push.instructions.dsl)
  )

;; convenience functions for testing


(defn get-local-from-dslblob
  [kwd dslblob]
  (kwd (second dslblob)))


;; fixtures


(def nada (m/basic-interpreter))
(def afew
  (m/basic-interpreter
    :stacks {:scalar '(1 2 3)}))
(def lots
  (m/basic-interpreter
    :stacks {:code (range 1 20)}))


;; scratch map

(fact "reading from Interpreter scratch map"
  (scratch-read nada :foo) => nil
  (scratch-read (assoc-in nada [:scratch :foo] 99) :foo) => 99
  )


(fact "writing to Interpreter scratch map"
  (:scratch (scratch-write nada :foo 99)) => {:foo 99}
  (:scratch
    (scratch-write
      (scratch-write nada :foo 99)
      :bar 88)) => {:foo 99 :bar 88}
  )
;; max-collection-size

(fact "I can read the max-collection-size"
  (get-max-collection-size nada) =>
    (:max-collection-size m/interpreter-default-config))

;; count-of


(facts "about `count-of`"
  (fact "`count-of` saves the number of items on the named stack in the specified local"
    (get-local-from-dslblob :foo
      (count-of [nada {}] :scalar :as :foo)) => 0
    (get-local-from-dslblob :foo
      (count-of [afew {}] :scalar :as :foo)) => 3
    (get-local-from-dslblob :foo
      (count-of [lots {}] :code :as :foo)) => 19)


  (future-fact "`count-of` returns 0 when the stack doesn't exist")


  (fact "`count-of` throws an Exception when no local is specified"
    (count-of [afew {}] :scalar) => (throws #"missing key: :as")))


;; `delete-top-of [stackname]`


(defn get-stack-from-dslblob
  [stackname dslblob]
  (u/get-stack (first dslblob) stackname))


(facts "about `delete-top-of`"

  (fact "`delete-top-of` deletes the top item of a stack"
    (get-stack-from-dslblob :scalar
      (delete-top-of [afew {}] :scalar)) => '(2 3))


  (fact "`delete-top-of` is fine if the stack doesn't exist"
    (delete-top-of [afew {}] :stupid) =not=> (throws))



  (fact "`delete-top-of` works on :boolean stacks containing false values"
    (get-stack-from-dslblob :boolean
      (delete-top-of
        [(m/basic-interpreter :stacks {:boolean '(false true)}) {}]
        :boolean)) => '(true)))


;; `consume-top-of [stackname :as local]`


(facts "about `consume-top-of`"

  (fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
    (get-stack-from-dslblob :scalar
      (consume-top-of [afew {}] :scalar :as :foo)) => '(2 3)
    (get-local-from-dslblob :foo
      (consume-top-of [afew {}] :scalar :as :foo)) => 1 )


  (fact "`consume-top-of` overwrites locals that already exist"
    (get-local-from-dslblob :foo
      (consume-top-of [afew {:foo \f}] :scalar :as :foo)) => 1)


  (fact "`consume-top-of` throws an exception when no local is given"
    (consume-top-of [afew {:foo \f}] :scalar) => (throws #"missing key: :as"))



  (fact "`consume-top-of` works with a :boolean stack of falses"
    (consume-top-of [(m/basic-interpreter :stacks {:boolean '(false false)}) {:foo \f}]
      :boolean :as :foo) =not=> (throws)))


  (fact "`consume-top-of` also saves anything it eats by appending it to scratch variable `:ARGS`"
    (second (consume-top-of [afew {:foo \f}] :scalar :as :foo)) => {:foo 1 :ARGS [1]}
    )

;; `consume-stack [stackname :as local]`


(facts "about `consume-stack`"

  (fact "`consume-stack` saves the entire stack into the named scratch variable"
    (get-stack-from-dslblob :scalar
      (consume-stack [afew {}] :scalar :as :foo)) => '()
    (get-local-from-dslblob :foo
      (consume-stack [afew {}] :scalar :as :foo)) => '(1 2 3) )


  (fact "`consume-stack` works when the stack is empty"
    (get-local-from-dslblob :foo
      (consume-stack [afew {}] :boolean :as :foo)) => '() )


  (fact "`consume-stack` returns an empty list (and creates a stack) when the stack isn't defined"
    (consume-stack [afew {}] :quux :as :foo) =>
      [(assoc-in afew [:stacks :quux] '())
        {:foo '()}])


  (fact "`consume-stack` throws an Exception when no local is specified"
    (consume-stack [afew {}] :scalar) => (throws #"missing key: :as")))


;; `delete-stack [stackname]`


(facts "about `delete-stack`"

  (fact "`delete-stack` discards the named stack"
    (get-stack-from-dslblob :scalar
      (delete-stack [afew {}] :scalar)) => '()
    (second (delete-stack [afew {}] :scalar)) => {})


  (fact "`delete-stack` is fine if the stack doesn't exist"
    (get-stack-from-dslblob :quux
      (delete-stack [afew {}] :quux)) => '()))


;; `index-from-scratch-ref [key hashmap]`


(facts "about `index-from-scratch-ref`"

  (fact "index-from-scratch-ref returns a number if one is stored"
    (#'push.instructions.dsl/index-from-scratch-ref :foo {:foo 8}) => 8)


  (fact "index-from-scratch-ref throws up if the stored value isn't an integer"
    (#'push.instructions.dsl/index-from-scratch-ref :foo {:foo false}) =>
      (throws #"false is not a valid index"))


  (fact "index-from-scratch-ref throws up if the key is not present"
    (#'push.instructions.dsl/index-from-scratch-ref :bar {:foo 2}) =>
      (throws #"nil is not a valid index")))


;; `save-max-collection-size [as kwd]`


(facts "about `save-max-collection-size`"

  (fact "save-max-collection-size stores the max-collection-size"
    (get-local-from-dslblob :max
      (#'push.instructions.dsl/save-max-collection-size
        [afew {}] :as :max)) =>
    (:max-collection-size m/interpreter-default-config))
)


;; `delete-nth-of [stackname :at where]`


(facts "about `delete-nth-of`"

  (fact "`delete-nth-of` discards the indicated item given an integer location"
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {}] :scalar :at 1)) => '(1 3))


  (fact "`delete-nth-of` picks the index as `(mod where stacklength)`"
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {}] :scalar :at -2)) => '(1 3)
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {}] :scalar :at -3)) => '(2 3))


  (fact "`delete-nth-of` discards the indicated item given scratch ref to integer"
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {:foo 1}] :scalar :at :foo)) => '(1 3)
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {:foo -1}] :scalar :at :foo)) => '(1 2)
    (get-stack-from-dslblob :scalar
      (delete-nth-of [afew {:foo 3}] :scalar :at :foo)) => '(2 3))


  (fact "`delete-nth-of` throws up given a scratch ref to non-integer"
    (delete-nth-of [afew {:foo false}] :scalar :at :foo) =>
      (throws #"false is not a valid index")
    (delete-nth-of [afew {:foo 1}] :scalar :at :bar) =>
      (throws #"nil is not a valid index"))


  (fact "`delete-nth-of` throws up if no index is given"
    (delete-nth-of [afew {:foo false}] :scalar) =>
      (throws #"missing key: :at"))


  (fact "`delete-nth-of` throws up if the stack is empty"
    (delete-nth-of [afew {}] :boolean :at 7) =>
      (throws #"stack :boolean is empty")))


;; `replace-stack [stackname local]`


(facts "about `replace-stack`"

  (fact "`replace-stack` sets the named stack to the value of the local if it is a list"
    (get-stack-from-dslblob :scalar
      (replace-stack [afew {:foo '(4 5 6)}] :scalar :foo)) => '(4 5 6)
    )


  (fact "`replace-stack` empties a stack if the local is not defined"
    (get-stack-from-dslblob :scalar
      (replace-stack [afew {}] :scalar :foo)) => '())


  (fact "`replace-stack` replaces the stack with just the item in a list otherwise"
    (get-stack-from-dslblob :scalar
      (replace-stack [afew {:foo false}] :scalar :foo)) => '(false))


  (fact "`replace-stack` creates an empty one when the stack doesn't exist"
    (get-stack-from-dslblob :foo
      (replace-stack [nada {:bar 1}] :foo :bar)) => '(1))
  )


;; `push-onto [stackname local]`


(facts "about `push-onto`"

  (fact "`push-onto` places the indicated scratch item onto the named stack"
    (get-stack-from-dslblob :scalar
      (push-onto [afew {:foo 99}] :scalar :foo)) => '(99 1 2 3))


  (fact "`push-onto` creates a new empty stack if needed"
    (get-stack-from-dslblob :grault
      (push-onto [afew {:foo 99}] :grault :foo)) => '(99))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable isn't set"
    (get-stack-from-dslblob :scalar
      (push-onto [afew {}] :scalar :foo)) => '(1 2 3))


  (fact "`push-onto` doesn't raise a fuss if the scratch variable is a list"
    (get-stack-from-dslblob :scalar
      (push-onto [afew {:foo '(4 5 6)}] :scalar :foo)) => '((4 5 6) 1 2 3)))


;; `save-stack [stackname :as local]`


(facts "about `save-stack`"

  (fact "`save-stack` puts the entire named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :scalar
      (save-stack [afew {}] :scalar :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-stack [afew {}] :scalar :as :bar)) => '(1 2 3))


  (fact "`save-stack` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (save-stack [afew {:foo false}] :scalar :as :foo)) => '(1 2 3))



  (fact "`save-stack` throws up if you leave out the :as argument"
    (save-stack [afew {}] :scalar ) =>
      (throws #"missing key: :as")))


;; `save-top-of [stackname :as local]`


(facts "about `save-top-of`"

  (fact "`save-top-of` puts the top item on the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :scalar
      (save-top-of [afew {}] :scalar :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-top-of [afew {}] :scalar :as :bar)) => 1)


  (fact "`save-top-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (save-top-of [afew {:foo false}] :scalar :as :foo)) => 1)


  (fact "`save-top-of` throws up if you ask for the top of an empty stack"
    (save-top-of [afew {}] :grault :as :foo) => (throws #"stack :grault is empty"))


  (fact "`save-top-of` throws up if you try to pop an empty stack"
    (save-top-of [afew {}] :boolean :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-top-of` throws up if you forget the :as argument"
    (save-top-of [afew {}] :scalar) => (throws #"missing key: :as"))


  (fact "`save-top-of` works on :boolean stacks containing false"
    (save-top-of [(m/basic-interpreter :stacks {:boolean '(false)}) {}] :boolean :as :foo)
      =not=> (throws)))


;; `save-nth-of [stackname :at where :as local]`


(facts "about `save-nth-of"

  (fact "given an scalar index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :scalar
      (save-nth-of [afew {}] :scalar :at 1 :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-nth-of [afew {}] :scalar :at 1 :as :bar)) => 2)


  (fact "given an keyword index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
    (get-stack-from-dslblob :scalar
      (save-nth-of [afew {:foo 2}] :scalar :at :foo :as :bar)) => '(1 2 3)
    (get-local-from-dslblob :bar
      (save-nth-of [afew {:foo 2}] :scalar :at :foo :as :bar)) => 3)


  (fact "`save-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :scalar :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :scalar :at -1 :as :foo)) => 3)


  (fact "`save-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (save-nth-of [afew {:foo false}] :scalar :at 1 :as :foo)) => 2)


  (fact "`save-nth-of` throws up if you try save from an empty stack"
    (save-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"stack :grault is empty"))


  (fact "`save-nth-of` throws up if the keyword index doesn't point to an integer"
    (save-nth-of [afew {:foo false}] :scalar :at :foo :as :bar) =>
      (throws #"false is not a valid index"))


  (fact "`save-nth-of` throws up if you try to pop an empty stack"
    (save-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`save-nth-of` throws up if you forget the :as argument"
    (save-nth-of [afew {}] :scalar :at 8) =>
      (throws #"missing key: :as"))


  (fact "`save-nth-of` throws up if you forget the :at argument"
    (save-nth-of [afew {}] :scalar :as :foo) =>
      (throws #"missing key: :at")))


;; `consume-nth-of [stackname :at where :as local]`


(facts "about `consume-nth-of`"

  (fact "given an scalar index, `consume-nth-of` puts the indicated item from the named stack into a scratch variable, deleting it"
    (get-stack-from-dslblob :scalar
      (consume-nth-of [afew {}] :scalar :at 1 :as :bar)) => '(1 3)
    (get-local-from-dslblob :bar
      (consume-nth-of [afew {}] :scalar :at 1 :as :bar)) => 2)


  (fact "`consume-nth-of` works with an out-of-bounds index"
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :scalar :at 11 :as :foo)) => 3
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :scalar :at -1 :as :foo)) => 3)


  (fact "`consume-nth-of` overwrites the scratch variable if asked to"
    (get-local-from-dslblob :foo
      (consume-nth-of [afew {:foo false}] :scalar :at 1 :as :foo)) => 2)


  (fact "`consume-nth-of` throws up if you ask for an item from an empty stack"
    (consume-nth-of [afew {}] :grault :at 2 :as :foo) =>
      (throws #"stack :grault is empty"))


  (fact "`consume-nth-of` throws up if the keyword index doesn't point to an integer"
    (consume-nth-of [afew {:foo false}] :scalar :at :foo :as :bar) =>
      (throws #"false is not a valid index"))


  (fact "`consume-nth-of` throws up if you try to pop an empty stack"
    (consume-nth-of [afew {}] :boolean :at 6 :as :foo) =>
      (throws #"stack :boolean is empty"))


  (fact "`consume-nth-of` throws up if you forget the :as argument"
    (consume-nth-of [afew {}] :scalar :at 8) =>
      (throws #"missing key: :as"))


  (fact "`consume-nth-of` throws up if you forget the :at argument"
    (consume-nth-of [afew {}] :scalar :as :foo) =>
      (throws #"missing key: :at")))


  (fact "`consume-nth-of` also saves anything it eats by appending it to scratch variable `:ARGS`"
    (second (consume-nth-of [afew {:foo false}] :scalar :at 11 :as :foo)) =>
      {:foo 3 :ARGS [3]}
    )


;; `get-nth-of [stackname :at where]` (shared functionality)


(facts "about `get-nth-of`"

  (fact "given an scalar index, `get-nth-of` returns the index and the item in the named stack"
    (#'push.instructions.dsl/get-nth-of [afew {}] :scalar :at 1) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds numeric indices"
    (#'push.instructions.dsl/get-nth-of [afew {}] :scalar :at -1) =>
      [2 '(1 2 3)]
    (#'push.instructions.dsl/get-nth-of [afew {}] :scalar :at 10) =>
      [1 '(1 2 3)])


  (fact "given a keyword index, `get-nth-of` returns the index and the item in the named stack"
    (#'push.instructions.dsl/get-nth-of [afew {:foo 1}] :scalar :at :foo) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of works for out of bounds keyword indices"
    (#'push.instructions.dsl/get-nth-of [afew {:foo -1}] :scalar :at :foo) =>
      [2 '(1 2 3)]
    (#'push.instructions.dsl/get-nth-of [afew {:foo 10}] :scalar :at :foo) =>
      [1 '(1 2 3)])


  (fact "`get-nth-of` throws an index error if the stack is empty"
    (#'push.instructions.dsl/get-nth-of [afew {}] :grault :at 2) =>
      (throws #"stack :grault is empty"))


  (fact "`get-nth-of` throws up if the keyword index doesn't point to an integer"
    (#'push.instructions.dsl/get-nth-of [afew {:foo false}] :scalar :at :foo) =>
      (throws #"false is not a valid index")
    (#'push.instructions.dsl/get-nth-of [afew {}] :scalar :at :foo) =>
      (throws #"nil is not a valid index"))


  (fact "`get-nth-of` throws up if you refer to an empty stack"
    (#'push.instructions.dsl/get-nth-of [afew {}] :boolean :at 6) =>
      (throws #"stack :boolean is empty"))


  (fact "`get-nth-of` works with a stack full of false values"
    (#'push.instructions.dsl/get-nth-of
      [(m/basic-interpreter :stacks {:boolean '(false false)}) {}] :boolean :at 6) =not=>
        (throws)))


;; `save-bindings [:as where]`


(facts "about `save-bindings`"

  (fact "all the bindings are saved as a set in the named scratch variable"
    (get-local-from-dslblob :inp
      (#'push.instructions.dsl/save-bindings [nada {}] :as :inp)) =>
        '()


    (get-local-from-dslblob :inp
      (#'push.instructions.dsl/save-bindings
        [(m/basic-interpreter :bindings {:a 8 :b 6}) {}] :as :inp)) =>
          '(:a :b))


  (fact "raises an exception when the :as arg is missing"
      (#'push.instructions.dsl/save-bindings
        [(m/basic-interpreter :bindings {:a 8 :b 6}) {}]) =>
          (throws #"Push DSL argument error: missing key")))



;; `save-instructions [:as where]`


(facts "about `save-instructions`"

  (fact "all the instructions are saved as a set in the named scratch variable"
    (get-local-from-dslblob :inst
      (#'push.instructions.dsl/save-instructions [nada {}] :as :inst)) =>
        #{}


    (get-local-from-dslblob :inst
      (#'push.instructions.dsl/save-instructions
        [(push/interpreter) {}] :as :inst)) =>
          (contains [:code-pop :boolean-print :string-swap]))


  (fact "raises an exception when the :as arg is missing"
      (#'push.instructions.dsl/save-instructions
        [(m/basic-interpreter) {}]) =>
          (throws #"Push DSL argument error: missing key")))


;; `save-counter [:as where]`


(facts "about `save-counter`"

  (fact "the counter value is saved into a scratch variable"
    (get-local-from-dslblob :count
      (#'push.instructions.dsl/save-counter [nada {}] :as :count)) => 0


    (get-local-from-dslblob :count
      (#'push.instructions.dsl/save-counter
        [(m/basic-interpreter :counter 8812) {}] :as :count)) =>
          8812)


  (fact "raises an exception when the :as arg is missing"
      (#'push.instructions.dsl/save-counter
        [(m/basic-interpreter) {}]) =>
          (throws #"Push DSL argument error: missing key")))



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
    '(consume-nth-of :scalar :at 1 :as :bar)) => {:scalar 1}
  (inst/needs-of-dsl-step
    '(save-nth-of :scalar :at 11 :as :foo))  => {:scalar 1}
  (inst/needs-of-dsl-step
    '(save-top-of :scalar :as :bar))  => {:scalar 1}
  (inst/needs-of-dsl-step
    '(save-stack :scalar :as :bar))  => {}
  (inst/needs-of-dsl-step
    '(push-onto :scalar :foo))  => {}
  (inst/needs-of-dsl-step
    '(replace-stack :scalar :foo))  => {}
  (inst/needs-of-dsl-step
    '(delete-nth-of :scalar :at 1))  => {:scalar 1}
  (inst/needs-of-dsl-step
    '(delete-stack :scalar))  => {}
  (inst/needs-of-dsl-step
    '(consume-stack :scalar :as :foo))  => {}
  (inst/needs-of-dsl-step
    '(consume-top-of :scalar :as :foo)) => {:scalar 1}
  (inst/needs-of-dsl-step
    '(delete-top-of :scalar)) => {}
  (inst/needs-of-dsl-step
    '(count-of :scalar :as :foo)) => {})


(fact "`inst/needs-of-dsl-step` throws an exception for unknown DSL instructions"
  (inst/needs-of-dsl-step '(bad-idea-instruction :foo 8 :bar)) =>
    (throws #"parse error: 'bad-idea-instruction' is not"))


;; inst/total-needs


(fact "`inst/total-needs` takes a whole transaction and sums up all the needs of each item"
  (inst/total-needs
    ['(consume-nth-of :scalar :at 1 :as :bar)]) => {:scalar 1}

  (inst/total-needs
    ['(consume-top-of :scalar :as :bar)
     '(consume-nth-of :scalar :at 1 :as :bar)]) => {:scalar 2}

  (inst/total-needs
    ['(delete-nth-of :scalar :at 1)
     '(consume-top-of :scalar :as :arg1)
     '(consume-top-of :scalar :as :arg2)
     '(consume-top-of :boolean :as :b1)
     '(consume-top-of :foo :as :foo1)]) => {:boolean 1, :foo 1, :scalar 3}

  (inst/total-needs
    ['(calculate [] #(33) :as :tt)]) => {}


  (inst/total-needs
    ['(consume-top-of :scalar :as :arg1)
     '(consume-top-of :scalar :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push-onto :scalar :m)]) => {:scalar 2} )


(fact "`inst/total-needs` throws up when it sees bad DSL code"
  (inst/total-needs
    ['(consume-top-of :scalar :as :arg1)
     '(consume-top-of :scalar :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push :scalar :m)]) =>
    (throws "Push DSL parse error: 'push' is not a known instruction."))


;; inst/products-of-dsl-step


(fact "`products-of-dsl-step` returns a hashmap containing the products for every DSL instruction"
  (inst/products-of-dsl-step
    '(push-onto :scalar :foo))  => {:scalar 1}
  (inst/products-of-dsl-step
    '(push-these-onto :scalar [:foo :bar :baz]))  => {:scalar 3}
  (inst/products-of-dsl-step
    '(replace-stack :scalar :foo))  => {}
  (inst/products-of-dsl-step
    '(insert-as-nth-of :scalar :foo :at 0))  => {:scalar 1}
  (inst/products-of-dsl-step
    '(count-of :scalar :as :foo)) => {})


(fact "`inst/products-of-dsl-step` throws an exception for unknown DSL instructions"
  (inst/products-of-dsl-step '(bad-idea-instruction :foo 8 :bar)) =>
    (throws #"parse error: 'bad-idea-instruction' is not"))


;; inst/total-products


(fact "`inst/total-products` takes a whole transaction and sums up all the products of each item"
  (inst/total-products
    ['(push-onto :scalar :bar)]) => {:scalar 1}


  (inst/total-products
    ['(push-onto :scalar :foo)
     '(replace-stack :scalar :foo)
     '(insert-as-nth-of :scalar :foo :at 0)]) => {:scalar 2}


  (inst/total-products
    ['(push-onto :scalar :foo)
     '(push-onto :scalar :foo2)
     '(push-these-onto :float [:foo :bar :baz :qux])
     '(insert-as-nth-of :boolean :foo :at :place)
     '(count-of :exec :as :foo)]) => {:boolean 1, :float 4, :scalar 2})


(fact "`inst/total-products` throws up when it sees bad DSL code"
  (inst/total-products
    ['(consume-top-of :scalar :as :arg1)
     '(consume-top-of :scalar :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push :scalar :m)]) =>
    (throws "Push DSL parse error: 'push' is not a known instruction."))


;; inst/def-function-from-dsl


(fact "`inst/def-function-from-dsl` produces a function from zero or more DSL commands"
  (fn? (macroexpand-1 (inst/def-function-from-dsl
    (consume-top-of :scalar :as :bar) ))) => true
  (fn? (macroexpand-1 (inst/def-function-from-dsl ))) => true
  (fn? (macroexpand-1 (inst/def-function-from-dsl
    (consume-top-of :scalar :as :bar)
    (consume-top-of :scalar :as :bar) ))) => true)


(fact "applying that function to an Interpreter produces an Interpreter result"
  (let [int-add (inst/def-function-from-dsl
                  (consume-top-of :scalar :as :arg1)
                  (consume-top-of :scalar :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :scalar :sum))]
  (class (first (int-add afew))) => push.interpreter.definitions.Interpreter))


(fact "applying the function does the things it's supposed to"
  (let [int-add (inst/def-function-from-dsl
                  (consume-top-of :scalar :as :arg1)
                  (consume-top-of :scalar :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :scalar :sum))]
  (u/get-stack (first (int-add afew)) :scalar) => '(3 3)))


;; `push-these-onto [stackname [locals]]`


(facts "about `push-these-onto`"

  (fact "`push-these-onto` places all indicated scratch items onto the named stack"
    (get-stack-from-dslblob :scalar
      (push-these-onto
        [afew {:foo 99 :bar 111}]
        :scalar
        [:foo :bar])) => '(111 99 1 2 3))


  (fact "`push-these-onto` will create a stack if the named one is missing"
    (get-stack-from-dslblob :grault
      (push-these-onto [afew {:foo 99}] :grault [:foo])) => '(99))



  (fact "`push-these-onto` doesn't raise a fuss if a scratch variable isn't set"
    (get-stack-from-dslblob :scalar
      (push-these-onto [afew {}] :scalar [:foo])) => '(1 2 3)
        (get-stack-from-dslblob :scalar
      (push-these-onto [afew {:foo 99 :bar 111}] :scalar [:foo :qux])) =>
        '(99 1 2 3))


  (fact "`push-these-onto` doesn't raise a fuss if the vector's empty"
    (get-stack-from-dslblob :scalar
      (push-these-onto [afew {:foo 99 :bar 111}] :scalar [])) => '(1 2 3))


  (fact "`push-these-onto` doesn't care if a scratch variable is a list"
    (get-stack-from-dslblob :scalar
      (push-these-onto [afew {:foo '(4 5 6)}] :scalar [:foo])) =>
        '((4 5 6) 1 2 3)))

(fact "push-these-onto balks when the items (taken together) oversized"
  (let [skimpy (push/interpreter :config {:max-collection-size 9}
                                 :stacks {:foo '(1 2 3 4 5)} )]
    (get-stack-from-dslblob
      :error
      (push-these-onto [skimpy {:bar [1 2]}] :foo [:bar])) =>
        '()

    (get-stack-from-dslblob
      :foo
      (push-these-onto [skimpy {:bar [1 2]}] :foo [:bar])) =>
        '([1 2] 1 2 3 4 5)

    (get-stack-from-dslblob
      :error
      (push-these-onto [skimpy {:bar [1 2]}] :foo [:bar :bar :bar])) =>
        '({:item " tried to push an overized item to :foo", :step 0})

    (get-stack-from-dslblob
      :foo
      (push-these-onto [skimpy {:bar [1 2]}] :foo [:bar :bar :bar])) =>
        '(1 2 3 4 5)
    )
  )




;; `insert-as-nth-of [stackname local :at where]`


(facts "about `insert-as-nth-of`"

  (fact "`insert-as-nth-of` puts the named scratch item in position n of the named stack"
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 0)) => '(99 1 2 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 1)) => '(1 99 2 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 2)) => '(1 2 99 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 3)) => '(1 2 3 99))


  (fact "`insert-as-nth-of` picks the index as `(mod where stacklength)`"
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at -1)) => '(1 2 3 99)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 4)) => '(99 1 2 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at 6)) => '(1 2 99 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at -5)) => '(1 2 3 99))


  (fact "`insert-as-nth-of` will use a scratch variable as index if it's an scalar"
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99 :bar 1}] :scalar :foo :at :bar)) =>
        '(1 99 2 3)
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99 :bar -1}] :scalar :foo :at :bar)) =>
        '(1 2 3 99))


  (fact "`insert-as-nth-of` throws up if the scratch variable isn't an integer"
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99 :bar false}] :scalar :foo :at :bar)) =>
        (throws #"Push DSL argument error: false is not a valid index")
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo :at :bar)) =>
        (throws #"Push DSL argument error: nil is not a valid index"))


  (fact "`insert-as-nth-of` throws up if no index is given"
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [afew {:foo 99}] :scalar :foo)) =>
        (throws #"Push DSL argument error: missing key: :at"))

  (fact "`insert-as-nth-of` is OK if the stack is empty"
    (get-stack-from-dslblob :boolean
      (insert-as-nth-of [afew {:foo 99}] :boolean :foo :at 8182)) =>
        '(99)))


(fact "`insert-as-nth-of` creates an error if the stack item pushes us over the size limit"
  (let [wee (m/basic-interpreter :stacks {:scalar '(1 2 3)} :config {:max-collection-size 5})]
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [wee {:foo [1 2 3 4 5 6]}] :scalar :foo :at 1)) => '(1 2 3)
    (get-stack-from-dslblob :error
      (insert-as-nth-of [wee {:foo [1 2 3 4 5 6]}] :scalar :foo :at 1)) =>
      '({:item " tried to push an overized item to :scalar", :step 0})
    (get-stack-from-dslblob :scalar
      (insert-as-nth-of [wee {:foo [1]}] :scalar :foo :at 1)) => '(1 [1] 2 3)
    (get-stack-from-dslblob :error
      (insert-as-nth-of [wee {:foo [1]}] :scalar :foo :at 1)) =>
      '()
      ))


;; `save-snapshot`


(fact "`save-snapshot` creates a single new :snapshot item from the current interpreter's :stacks, :bindings and :config"
  (first (get-stack-from-dslblob :snapshot
      (save-snapshot [afew {}]))) =>
      (snap/->Snapshot (:stacks afew)
                       (:bindings afew)
                       (:config afew)
                       ))


(fact "`save-snapshot` works regardless of stack contents"
  (get-stack-from-dslblob :snapshot
      (save-snapshot [(m/basic-interpreter) {}])) =>
      (list (snap/->Snapshot (:stacks (m/basic-interpreter))
                              {}
                              (:config (m/basic-interpreter)))))


;; `retrieve-snapshot-state`





(fact "`retrieve-snapshot-state` replaces all the stacks except :print, :log and :error with the argument's ones"

  (let [s (snap/snapshot
            (push/interpreter :stacks {:exec    '(1 2 3)
                                       :print   '(:OLD)
                                       :log     '(:OLD)
                                       :error   '(:OLD)
                                       :unknown '(:OLD)}))
        r (push/interpreter   :stacks {:exec    '(9 9 9)
                                       :print   '(:NEW)
                                       :log     '(:NEW)
                                       :error   '(:NEW)
                                       :unknown '(:NEW)
                                       :foo     '(:FOO)})  ]

    (:stacks
      (first (retrieve-snapshot-state [r {:foo s}] :using :foo))) =>
        (contains {:exec    '(1 2 3)
                   :print   '(:NEW)
                   :log     '(:NEW)
                   :unknown '(:NEW)
                   :error   '(:NEW)
                   :foo     '(:FOO)})
        ))


(fact "`retrieve-snapshot-state` throws an exception if it lacks the hash"
  (:stacks (first (retrieve-snapshot-state [afew {}]))) => (throws #"Push DSL argument error"))



;; `record-an-error [:from scratch item]`


(facts "about `record-an-error`"

  (fact "`record-an-error` puts a time-stamped entry on the Interpreter's :error stack, with the argument as its :item field"
    (get-stack-from-dslblob :error
      (record-an-error [afew {:foo 99}] :from :foo)) =>
    '({:item 99, :step 0})

    (get-stack-from-dslblob :error
      (record-an-error [afew {:foo "a message"}] :from :foo)) =>
    '({:item "a message", :step 0}))


  (fact "`record-an-error` logs nothing if the :from item is nil"
    (get-stack-from-dslblob :error
      (record-an-error [afew {:foo nil}] :from :foo)) => '())


  (fact "`record-an-error` throws an exception if it lacks the :from argument"
    (:stacks (first (record-an-error [afew {}]))) =>
      (throws #"Push DSL argument error")))


;; `bind-item item :into kwd`

;;; oversized-binding?

(fact "oversized-binding? returns true if the combined size of the item and the indicated binding stack is larger than the interpreter's max-collection-size"
  (let [skimpy (push/interpreter :config {:max-collection-size 3})
        small-thing 99.99
        big-thing [1 2 3 4 5]]
    (fix/count-collection-points small-thing) => 1
    (fix/count-collection-points big-thing) => 6
    (oversized-binding? skimpy :foo nil) => false
    (oversized-binding? skimpy :foo small-thing) => false
    (oversized-binding? skimpy :foo big-thing) => true
    ))


(fact "`bind-item` saves the item stored in the first arg into the ref stored in the :into arg"
  (:bindings
    (first
      (bind-item [afew {:foo 9 :bar :baz}] :foo :into :bar))) => {:baz '(9)})


(fact "`bind-item` saves the item into a new ref if none is named in :into"
  (let [b (:bindings (first
            (bind-item [afew {:foo 9 :bar :baz}] :foo)))]
    (first (vals b)) => '(9)
    (str (first (keys b))) => #":ref!\d+"))


(fact "`bind-item` throws an exception if the :into arg is not bound to a keyword"
  (:bindings
    (first
      (bind-item [afew {:foo 9 :bar "oops!"}] :foo :into :bar))) =>
        (throws #"as a :bindings key"))


(fact "`bind-item` has no effect if the item argument is nil (and does not push nil!)"
  (:bindings
    (first
      (bind-item [afew {:foo nil :bar :baz}] :foo :into :bar))) =>
      {:baz '()})


(fact "bind-item balks when the item is oversized or would be if added to the count if items in that binding"
  (let [skimpy (push/interpreter :config {:max-collection-size 4}
                                 :bindings {:foo 999})]
    (:bindings (first
      (bind-item
        [skimpy {:bar 99 :where :foo}]
        :bar
        :into :where))) => {:foo '(99 999)}
    (:bindings (first
      (bind-item
        [skimpy {:bar [1 2] :where :foo}]
        :bar
        :into :where))) => {:foo '([1 2] 999)}
    (:bindings (first
      (bind-item
        [skimpy {:bar [1 2 3 4] :where :foo}]
        :bar
        :into :where))) => {:foo '(999)}
    (get-stack-from-dslblob
      :error
      (bind-item [skimpy {:bar [1 2 3 4 5] :where :foo}]
        :bar
        :into :where)) =>
          '({:item "Push runtime error: binding is over size limit", :step 0})
    (:bindings (first
      (bind-item
        [skimpy {:bar [1 2 3 4 5]}]
        :bar ;; no named target stack
        ))) => {:foo '(999)}
    (get-stack-from-dslblob
      :error
      (bind-item [skimpy {:bar [1 2 3 4 5]}]
        :bar  ;; no named target stack
        )) =>
          '({:item "Push runtime error: binding is over size limit", :step 0})
    ))


(facts "about `replace-binding`"
  (fact "`replace-binding` creates a new binding if no `:into` is given"
    (:bindings (first (replace-binding
                        [afew {:foo 9}] :foo))) => {:xxx '(9)}
      (provided (gensym anything) => (symbol "xxx")))


  (fact "`replace-binding` uses a binding if `:into` is given"
    (:bindings (first (replace-binding
                        [afew {:foo 9 :bar :where}] :foo :into :bar))) => {:where '(9)})


  (fact "`replace-binding` does not push `nil` if the item is `nil`"
    (:bindings (first (replace-binding
                        [afew {:foo nil :bar :where}] :foo :into :bar))) => {:where '()})



  (fact "`replace-binding` throws an exception if `:into` is not a kw"
    (:bindings (first (replace-binding
                        [afew {:foo 9 :bar 8.2}] :foo :into :bar))) =>
      (throws #"Cannot use '8.2' as a :bindings key"))


  (fact "`replace-binding` constructs an entire stack if a seq is passed in"
    (:bindings (first (replace-binding
                        [afew {:foo '(1 2 3 4)}] :foo))) => {:xxx '(1 2 3 4)}
      (provided (gensym anything) => (symbol "xxx"))

    (:bindings (first (replace-binding
                        [afew {:foo '(1 2 3) :bar :where}] :foo :into :bar))) =>
      {:where '(1 2 3)})


  (fact "`replace-binding` can be tricked into making a stack containing a list"
    (:bindings (first (replace-binding
                        [afew {:foo '((1 2 3 4))}] :foo))) => {:xxx '((1 2 3 4))}
      (provided (gensym anything) => (symbol "xxx"))))



;;; oversized-stack?

(fact "oversized-stack? returns true if the combined size of the item and the indicated stack is larger than the interpreter's max-collection-size"
  (let [skimpy (push/interpreter :config {:max-collection-size 15}
                                 :stacks {:foo '(1 [2 (3 4) {5 6} 7] 8)} )
        foostack (get-in skimpy [:stacks :foo])]
    (fix/count-collection-points (get-in skimpy [:stacks :foo])) => 13
    (fix/count-collection-points (:stacks skimpy)) => #(> % 91)
    (oversized-stack? skimpy foostack skimpy) => true
    (oversized-stack? skimpy foostack 1) => false
    )
  )



(fact "push-onto balks when the items are oversized"
  (let [skimpy (push/interpreter :config {:max-collection-size 15}
                                 :stacks {:foo '(1 [2 (3 4) {5 6} 7] 8)} )
        foostack (get-in skimpy [:stacks :foo])]

    (get-stack-from-dslblob
      :error
      (push-onto [skimpy {:bar skimpy}] :foo :bar)) =>
        '({:item " tried to push an overized item to :foo", :step 0})

    (get-stack-from-dslblob
      :foo
      (push-onto [skimpy {:bar skimpy}] :foo :bar)) =>
        '(1 [2 (3 4) {5 6} 7] 8)
    )
  )




;; save-snapshot




(fact "`save-snapshot` saves a snapshot onto the intepreter's :snapshot stack"
  (get-stack-from-dslblob
    :snapshot
    (save-snapshot [afew {}])) => (list (snap/snapshot afew))
  )


(def skimpy (m/basic-interpreter :config {:max-collection-size 7}))


(fact "`save-snapshot` will not save a snapshot if the snapshot is oversized"
  (get-stack-from-dslblob
    :snapshot
    (save-snapshot [skimpy {}])) => '()
  (get-stack-from-dslblob
    :error
    (save-snapshot [skimpy {}])) => '({:step 0, :item "Push runtime error: snapshot is over size limit"})
  )



;; argument retention


(fact "`start-storing-arguments` sets the :store-args? flag to true"
  (let [turned-off (m/basic-interpreter :config {:store-args? false})]
    (get-in turned-off [:config :store-args?]) =>
      false
    (get-in (start-storing-arguments [turned-off {}]) [0 :config :store-args?]) =>
      true
      ))


(fact "`stop-storing-arguments` sets the :store-args? flag to false"
  (let [turned-on (m/basic-interpreter :config {:store-args? true})]
    (get-in turned-on [:config :store-args?]) =>
      true
    (get-in (stop-storing-arguments [turned-on {}]) [0 :config :store-args?]) =>
      false
    ))


(fact "`start-cycling-arguments` sets the :cycle-args? flag to true"
  (let [turned-off (m/basic-interpreter :config {:cycle-args? false})]
    (get-in turned-off [:config :cycle-args?]) =>
      false
    (get-in (start-cycling-arguments [turned-off {}]) [0 :config :cycle-args?]) =>
      true
      ))


(fact "`stop-cycling-arguments` sets the :cycle-args? flag to false"
  (let [turned-on (m/basic-interpreter :config {:cycle-args? true})]
    (get-in turned-on [:config :cycle-args?]) =>
      true
    (get-in (stop-cycling-arguments [turned-on {}]) [0 :config :cycle-args?]) =>
      false
      ))
