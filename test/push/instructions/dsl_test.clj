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
  (:use push.util.scratch)
  (:use push.util.test-helpers)
  )

;; fixtures

(def nada (m/basic-interpreter))

(def afew
  (m/basic-interpreter
    :stacks {:scalar '(1 2 3)}
    :config {:max-collection-size 8888}))

(def lots
  (m/basic-interpreter
    :stacks {:code (range 1 20)}))


;; helper functions

(fact "delete-nth-item-of-stack"
  (push/get-stack
    (delete-nth-item-of-stack afew :scalar 1)
    :scalar) => '(1 3)

  (push/get-stack (delete-nth-item-of-stack afew :boolean 1)
    :boolean) => '()

  (push/get-stack (delete-nth-item-of-stack afew :scalar -13/4)
    :scalar) => '(2 3)

  (push/get-stack (delete-nth-item-of-stack afew :scalar "not valid")
    :scalar) => (throws #"is not a valid index")
    )


(fact "nth-item-of-stack"
  (nth-item-of-stack afew :scalar 1) => 2
  (nth-item-of-stack afew :scalar -1.9) => 3
  (nth-item-of-stack afew :scalar 11/7) => 3
  )


(fact "insert-as-nth"
  (push/get-stack (insert-as-nth afew :scalar 99 1) :scalar) => [1 99 2 3]
  (push/get-stack (insert-as-nth afew :scalar 99 -1) :scalar) => [1 2 3 99]
  (push/get-stack (insert-as-nth afew :scalar 99 0) :scalar) => [99 1 2 3]
  (push/get-stack (insert-as-nth afew :scalar 99 3) :scalar) => [1 2 3 99]
  (push/get-stack (insert-as-nth afew :scalar 99 11/7) :scalar) => [1 2 99 3]
  (push/get-stack (insert-as-nth afew :blargh 99 2) :blargh) => [99]
  )

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

(fact "forgetting things in Interpreter scratch map"
  (:scratch
    (scratch-forget
      (scratch-replace nada {:foo 99 :bar 88})
      :foo)) => {:bar 88}
      )

(fact "replacing the Interpreter scratch map"
  (:scratch
    (scratch-replace nada {:foo 99 :bar 88})) => {:foo 99 :bar 88}
    )

(fact "storing items in ARGS"
  (:scratch
    (scratch-save-arg nada 99)) => {:ARGS '(99)}
  (:scratch
    (scratch-save-arg nada [1 2 3])) => {:ARGS '([1 2 3])}
  (:scratch
    (scratch-save-arg
      (scratch-save-arg nada 99)
      88)) => {:ARGS '(88 99)}
      )

;; max-collection-size

(fact "I can read the max-collection-size"
  (get-max-collection-size nada) =>
    (:max-collection-size m/interpreter-default-config)
    )

;; count-of

(fact "`count-of` saves the number of items on the named stack in the specified local"
  (scratch-read (count-of nada :scalar :as :foo) :foo) => 0
  (scratch-read (count-of afew :scalar :as :foo) :foo) => 3
  (scratch-read (count-of lots :code :as :foo) :foo) => 19
  )

(fact "`count-of` returns 0 when the stack doesn't exist"
  (scratch-read (count-of nada :baz :as :foo) :foo) => 0
  )

(fact "`count-of` throws an Exception when no local is specified"
  (count-of afew :scalar) => (throws #"missing key: :as")
  )

;; `delete-top-of [stackname]`

(fact "`delete-top-of` deletes the top item of a stack"
  (push/get-stack (delete-top-of afew :scalar) :scalar) => '(2 3)
  )

(fact "`delete-top-of` is fine if the stack doesn't exist"
  (delete-top-of afew :stupid) =not=> (throws)
  )

(fact "`delete-top-of` works on :boolean stacks containing false values"
  (push/get-stack
    (delete-top-of
      (m/basic-interpreter :stacks {:boolean '(false true)})
      :boolean)
     :boolean) => '(true)
     )

;; `consume-top-of [stackname :as local]`

(fact "`consume-top-of` saves the top item of a stack in the indicated scratch variable"
  (push/get-stack (consume-top-of afew :scalar :as :foo) :scalar) => '(2 3)
  (scratch-read (consume-top-of afew :scalar :as :foo) :foo) => 1
  )

(fact "`consume-top-of` overwrites locals that already exist"
  (scratch-read
    (consume-top-of
      (scratch-replace afew {:foo 1 :bar 2})
      :scalar :as :foo) :foo) => 1
      )

(fact "`consume-top-of` throws an exception when no local is given"
  (consume-top-of afew :scalar) => (throws #"missing key: :as")
  )

(fact "`consume-top-of` works with a :boolean stack of falses"
  (consume-top-of
    (m/basic-interpreter :stacks {:boolean '(false false)})
    :boolean :as :foo) =not=> (throws)
    )

(fact "`consume-top-of` also saves anything it eats by appending it to scratch variable `:ARGS`"
  (:scratch
    (consume-top-of afew :scalar :as :foo)) => {:foo 1 :ARGS [1]}
    )

;; `consume-stack [stackname :as local]`

(fact "`consume-stack` saves the entire stack into the named scratch variable"
  (push/get-stack
    (consume-stack afew :scalar :as :foo)
    :scalar) => '()
  (scratch-read
    (consume-stack afew :scalar :as :foo)
    :foo) => '(1 2 3)
    )

(fact "`consume-stack` works when the stack is empty"
  (scratch-read
    (consume-stack afew :boolean :as :foo)
    :foo) => '()
    )

(fact "`consume-stack` returns an empty list (and creates a stack) when the stack isn't defined"
  (:stacks
    (consume-stack afew :blargh :as :foo)) => (merge (:stacks afew) {:blargh '()})
    )

(fact "`consume-stack` throws an Exception when no local is specified"
  (consume-stack afew :scalar) => (throws #"missing key: :as")
  )

;; `delete-stack [stackname]`

(fact "`delete-stack` discards the named stack"
  (push/get-stack
    (delete-stack afew :scalar)
    :scalar) => '()
    )

(fact "`delete-stack` is fine if the stack doesn't exist"
  (push/get-stack
    (delete-stack afew :quux)
    :quux) => '()
    )

;; `save-max-collection-size [as kwd]`

(fact "save-max-collection-size stores the max-collection-size"
  (scratch-read
    (save-max-collection-size afew :as :max)
    :max) => (config-read afew :max-collection-size)
    )

;; `delete-nth-of [stackname :at where]`

(fact "`delete-nth-of` discards the indicated item given an integer location"
  (push/get-stack
    (delete-nth-of afew :scalar :at 1)
    :scalar) => '(1 3)
    )

(fact "`delete-nth-of` picks the index as `(mod where stacklength)`"
  (push/get-stack
    (delete-nth-of afew :scalar :at -2)
    :scalar) => '(1 3)
  (push/get-stack
    (delete-nth-of afew :scalar :at -3)
    :scalar) => '(2 3)
    )

(fact "`delete-nth-of` discards the indicated item given scratch ref to integer"
  (push/get-stack
    (delete-nth-of
      (scratch-replace afew {:foo 1})
      :scalar :at :foo)
    :scalar) => '(1 3)
  (push/get-stack
    (delete-nth-of
      (scratch-replace afew {:foo -1})
      :scalar :at :foo)
    :scalar) => '(1 2)
  (push/get-stack
    (delete-nth-of
      (scratch-replace afew {:foo 3})
      :scalar :at :foo)
    :scalar) => '(2 3)
    )

(fact "`delete-nth-of` throws up given a scratch ref to non-integer"
  (delete-nth-of
    (scratch-replace afew {:foo false}) :scalar :at :foo) =>
      (throws #"false is not a valid index")
  (delete-nth-of
    (scratch-replace afew {:foo 1}) :scalar :at :bar) =>
      (throws #"nil is not a valid index")
      )

(fact "`delete-nth-of` throws up if no index is given"
  (delete-nth-of
    (scratch-replace afew {:foo false}) :scalar) =>
      (throws #"missing key: :at")
      )

;; `replace-stack [stackname local]`

(fact "`replace-stack` sets the named stack to the value of the local if it is a list"
  (push/get-stack
    (replace-stack
      (scratch-replace afew {:foo '(4 5 6)})
      :scalar :foo)
    :scalar) => '(4 5 6)
    )

(fact "`replace-stack` empties a stack if the local is not defined"
  (push/get-stack
    (replace-stack afew :scalar :foo)
    :scalar) => '()
    )

(fact "`replace-stack` replaces the stack with just the item in a list otherwise"
  (push/get-stack
    (replace-stack
      (scratch-replace afew {:foo false})
      :scalar :foo)
    :scalar) => '(false)
    )

(fact "`replace-stack` creates an empty one when the stack doesn't exist"
  (push/get-stack
    (replace-stack
      (scratch-replace nada {:bar 1})
      :foo :bar)
    :foo) => '(1)
    )

;; `push-onto [stackname local]`

(fact "`push-onto` places the indicated scratch item onto the named stack"
  (push/get-stack
    (push-onto
      (scratch-replace afew {:foo 99})
      :scalar :foo)
    :scalar) => '(99 1 2 3)
    )

(fact "`push-onto` creates a new empty stack if needed"
  (push/get-stack
    (push-onto
      (scratch-replace afew {:foo 99})
      :grault :foo)
    :grault) => '(99)
    )


(fact "`push-onto` doesn't raise a fuss if the scratch variable isn't set"
  (push/get-stack
    (push-onto afew :scalar :foo)
    :scalar) => '(1 2 3)
    )


(fact "`push-onto` doesn't raise a fuss if the scratch variable is a list"
  (push/get-stack
    (push-onto
      (scratch-replace afew {:foo '(4 5 6)})
      :scalar :foo)
    :scalar) => '((4 5 6) 1 2 3)
    )

;; `save-stack [stackname :as local]`

(fact "`save-stack` puts the entire named stack into a scratch variable (without deleting it)"
  (push/get-stack
    (save-stack afew :scalar :as :bar)
    :scalar) => '(1 2 3)
  (scratch-read
    (save-stack afew :scalar :as :bar)
     :bar) => '(1 2 3)
     )

(fact "`save-stack` overwrites the scratch variable if asked to"
  (scratch-read
    (save-stack
      (scratch-replace afew {:foo false})
      :scalar :as :foo)
    :foo) => '(1 2 3)
    )

(fact "`save-stack` throws up if you leave out the :as argument"
  (save-stack afew :scalar) => (throws #"missing key: :as")
  )

;; `save-top-of [stackname :as local]`

(fact "`save-top-of` puts the top item on the named stack into a scratch variable (without deleting it)"
  (push/get-stack
    (save-top-of afew :scalar :as :bar)
     :scalar) => '(1 2 3)
  (scratch-read
    (save-top-of afew :scalar :as :bar)
    :bar) => 1
    )

(fact "`save-top-of` overwrites the scratch variable if asked to"
  (scratch-read
    (save-top-of
      (scratch-replace afew {:foo false})
      :scalar :as :foo)
    :foo) => 1
    )

(fact "`save-top-of` throws up if you ask for the top of a nonexistent stack"
  (save-top-of afew :grault :as :foo) => (throws #"stack :grault is empty")
  )

(fact "`save-top-of` throws up if you try to pop an empty stack"
  (save-top-of afew :boolean :as :foo) => (throws #"stack :boolean is empty")
  )

(fact "`save-top-of` throws up if you forget the :as argument"
  (save-top-of afew :scalar) => (throws #"missing key: :as")
  )

(fact "`save-top-of` works on :boolean stacks containing false"
  (save-top-of
    (m/basic-interpreter :stacks {:boolean '(false)})
    :boolean :as :foo) =not=> (throws)
    )

;; `save-nth-of [stackname :at where :as local]`

(fact "given an scalar index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
  (push/get-stack
    (save-nth-of afew :scalar :at 1 :as :bar)
    :scalar) => '(1 2 3)

  (scratch-read
    (save-nth-of afew :scalar :at 1 :as :bar)
    :bar) => 2
    )

(fact "given an keyword index, `save-nth-of` puts the indicated item from the named stack into a scratch variable (without deleting it)"
  (push/get-stack
    (save-nth-of
      (scratch-replace afew {:foo 2}) :scalar :at :foo :as :bar)
    :scalar) => '(1 2 3)

  (scratch-read
    (save-nth-of
      (scratch-replace afew {:foo 2}) :scalar :at :foo :as :bar) :bar) => 3
      )

(fact "`save-nth-of` works with an out-of-bounds index"
  (scratch-read
    (save-nth-of
      (scratch-replace afew {:foo false}) :scalar :at 11 :as :foo)
    :foo) => 3
  (scratch-read
    (save-nth-of
      (scratch-replace afew {:foo false}) :scalar :at -1 :as :foo)
    :foo) => 3
    )

(fact "`save-nth-of` overwrites the scratch variable if asked to"
  (scratch-read
    (save-nth-of
      (scratch-replace afew {:foo false}) :scalar :at 1 :as :foo)
    :foo) => 2
    )

(fact "`save-nth-of` throws up if the keyword index doesn't point to an integer"
  (save-nth-of
    (scratch-replace afew {:foo false}) :scalar :at :foo :as :bar) =>
  (throws #"false is not a valid index")
  )

(fact "`save-nth-of` throws up if you forget the :as argument"
  (save-nth-of afew :scalar :at 8) => (throws #"missing key: :as")
  )

(fact "`save-nth-of` throws up if you forget the :at argument"
  (save-nth-of afew :scalar :as :foo) => (throws #"missing key: :at")
  )

;; `consume-nth-of [stackname :at where :as local]`

(fact "given an scalar index, `consume-nth-of` puts the indicated item from the named stack into a scratch variable, deleting it"
  (push/get-stack
    (consume-nth-of afew :scalar :at 1 :as :bar)
    :scalar) => '(1 3)
  (scratch-read
    (consume-nth-of afew :scalar :at 1 :as :bar)
    :bar) => 2
    )

(fact "`consume-nth-of` works with an out-of-bounds index"
  (scratch-read
    (consume-nth-of
      (scratch-replace afew {:foo false}) :scalar :at 11 :as :foo)
    :foo) => 3
  (scratch-read
    (consume-nth-of
      (scratch-replace afew {:foo false}) :scalar :at -1 :as :foo)
    :foo) => 3
    )

(fact "`consume-nth-of` overwrites the scratch variable if asked to"
  (scratch-read
    (consume-nth-of
      (scratch-replace afew {:foo false}) :scalar :at 1 :as :foo)
      :foo) => 2
      )

(fact "`consume-nth-of` throws up if the keyword index doesn't point to an integer"
  (consume-nth-of
    (scratch-replace afew {:foo false}) :scalar :at :foo :as :bar) =>
      (throws #"false is not a valid index")
      )

(fact "`consume-nth-of` throws up if you forget the :as argument"
  (consume-nth-of afew :scalar :at 8) =>
    (throws #"missing key: :as")
    )

(fact "`consume-nth-of` throws up if you forget the :at argument"
  (consume-nth-of afew :scalar :as :foo) =>
    (throws #"missing key: :at")
    )

(fact "`consume-nth-of` also saves anything it eats by appending it to scratch variable `:ARGS`"
  (:scratch
    (consume-nth-of
      (scratch-replace afew {:foo false}) :scalar :at 11 :as :foo)) =>
      {:foo 3 :ARGS [3]}
      )

;; `save-bindings [:as where]`

(fact "all the bindings are saved as a set in the named scratch variable"
  (scratch-read
    (save-bindings nada :as :inp)
    :inp) => '()
  (scratch-read
    (save-bindings
      (m/basic-interpreter :bindings {:a 8 :b 6}) :as :inp)
      :inp) => '(:a :b)
      )

(fact "raises an exception when the :as arg is missing"
    (save-bindings
      (m/basic-interpreter :bindings {:a 8 :b 6})) =>
        (throws #"Push DSL argument error: missing key")
        )

;; `save-instructions [:as where]`

(fact "all the instructions are saved as a set in the named scratch variable"
  (scratch-read
    (save-instructions nada :as :inst)
    :inst) => #{}
  (scratch-read
    (save-instructions
      (push/interpreter) :as :inst)
      :inst) => (contains [:code-pop :boolean-print :string-swap])
      )

(fact "raises an exception when the :as arg is missing"
  (save-instructions (m/basic-interpreter)) =>
    (throws #"Push DSL argument error: missing key")
    )

;; `save-counter [:as where]`

(fact "the counter value is saved into a scratch variable"
  (scratch-read
    (save-counter nada :as :count)
    :count) => 0
  (scratch-read
    (save-counter
      (m/basic-interpreter :counter 8812)
      :as :count)
    :count) => 8812
    )

(fact "raises an exception when the :as arg is missing"
  (save-counter (m/basic-interpreter)) =>
    (throws #"Push DSL argument error: missing key")
    )

;; `calculate [[args] fn :as local]`

(fact "calculate maps the function onto the indicated scratch items and stores the result in the named local"
  (scratch-read
    (calculate
      (scratch-replace afew {:a 8 :b 2}) [:a :b] #(+ %1 %2) :as :sum)
    :sum) => 10
  (scratch-read
    (calculate
      (scratch-replace afew {:a 8 :b 2}) [:a :b] #(min %1 %2) :as :min)
    :min) => 2
  (scratch-read
    (calculate
      (scratch-replace afew {:a 8 :b 2 :c true}) [:c :a :b] #(if %1 %2 %3) :as :choice)
     :choice) => 8
     )

(fact "`calculate` throws up if you forget the :as argument"
  (calculate
    (scratch-replace afew {:a 8 :b 2}) [:a :b] #(min %1 %2)) =>
    (throws #"missing key: :as")
    )

(fact "`calculate` throws up if the args are not a vector"
  (calculate
    (scratch-replace afew {:a 8 :b 2}) :a #(%1)) =>
    (throws #"error: ':a' can't be parsed")
    )

(fact "`calculate` throws up if the args are not a vector"
  (calculate
    (scratch-replace afew {:a 8 :b 2}) [:a] #(+ %1 %2) :as :foo) =>
    (throws #"Wrong number of args")
    )

(fact "`calculate` is fine with nil"
  (calculate
    (scratch-replace afew {:a nil}) [:a] #(if %1 2 3) :as :foo) =not=>
    (throws Exception)
    )

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
    (throws "Push DSL parse error: 'push' is not a known instruction.")
    )

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
    '(count-of :scalar :as :foo)) => {}
      )

(fact "`inst/products-of-dsl-step` throws an exception for unknown DSL instructions"
  (inst/products-of-dsl-step '(bad-idea-instruction :foo 8 :bar)) =>
    (throws #"parse error: 'bad-idea-instruction' is not")
    )

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
     '(count-of :exec :as :foo)]) => {:boolean 1, :float 4, :scalar 2}
     )

(fact "`inst/total-products` throws up when it sees bad DSL code"
  (inst/total-products
    ['(consume-top-of :scalar :as :arg1)
     '(consume-top-of :scalar :as :arg2)
     '(calculate [:arg1 :arg2] #(mod %1 %2) :as :m)
     '(push :scalar :m)]) =>
    (throws "Push DSL parse error: 'push' is not a known instruction.")
    )


;; inst/def-function-from-dsl

(fact "`inst/def-function-from-dsl` produces a function from zero or more DSL commands"
  (fn? (macroexpand-1 (inst/def-function-from-dsl
    (consume-top-of :scalar :as :bar) ))) => true

  (fn? (macroexpand-1 (inst/def-function-from-dsl ))) => true

  (fn? (macroexpand-1 (inst/def-function-from-dsl
    (consume-top-of :scalar :as :bar)
    (consume-top-of :scalar :as :bar) ))) => true
    )


(fact "applying that function to an Interpreter produces an Interpreter result"
  (let [int-add (inst/def-function-from-dsl
                  (consume-top-of :scalar :as :arg1)
                  (consume-top-of :scalar :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :scalar :sum))]
  (class (int-add afew)) => push.interpreter.definitions.Interpreter
  ))


(fact "applying the function does the things it's supposed to"
  (let [int-add (inst/def-function-from-dsl
                  (consume-top-of :scalar :as :arg1)
                  (consume-top-of :scalar :as :arg2)
                  (calculate [:arg1 :arg2] #(+ %1 %2) :as :sum)
                  (push-onto :scalar :sum))]
  (push/get-stack (int-add afew) :scalar) => '(3 3)
  ))

; `push-these-onto [stackname [locals]]`

(fact "`push-these-onto` places all indicated scratch items onto the named stack"
  (push/get-stack
    (push-these-onto
      (scratch-replace afew {:foo 99 :bar 111}) :scalar [:foo :bar])
    :scalar) => '(111 99 1 2 3)
    )

(fact "`push-these-onto` will create a stack if the named one is missing"
  (push/get-stack
    (push-these-onto
      (scratch-replace afew {:foo 99}) :grault [:foo])
    :grault) => '(99)
    )

(fact "`push-these-onto` doesn't raise a fuss if a scratch variable isn't set"
  (push/get-stack
    (push-these-onto afew :scalar [:foo])
     :scalar) => '(1 2 3)
  (push/get-stack
    (push-these-onto
      (scratch-replace afew {:foo 99 :bar 111})
      :scalar [:foo :qux])
    :scalar) => '(99 1 2 3)
    )

(fact "`push-these-onto` doesn't raise a fuss if the vector's empty"
  (push/get-stack
    (push-these-onto
      (scratch-replace afew {:foo 99 :bar 111})
      :scalar [])
    :scalar) => '(1 2 3)
    )

(fact "`push-these-onto` doesn't care if a scratch variable is a list"
  (push/get-stack
    (push-these-onto
      (scratch-replace afew {:foo '(4 5 6)})
      :scalar [:foo])
    :scalar) => '((4 5 6) 1 2 3)
    )

(fact "push-these-onto balks when the items (taken together) oversized"
  (let [skimpy (push/interpreter :config {:max-collection-size 9}
                                 :stacks {:foo '(1 2 3 4 5)} )]
    (push/get-stack
      (push-these-onto
        (scratch-replace skimpy {:bar [1 2]})
        :foo [:bar])
      :error) => '()

    (push/get-stack
      (push-these-onto
        (scratch-replace skimpy {:bar [1 2]})
        :foo [:bar])
      :foo) => '([1 2] 1 2 3 4 5)

    (push/get-stack
      (push-these-onto
        (scratch-replace skimpy {:bar [1 2]})
        :foo [:bar :bar :bar])
      :error) => '({:item " tried to push an oversized item to :foo", :step 0})

    (push/get-stack
      (push-these-onto
        (scratch-replace skimpy {:bar [1 2]})
        :foo [:bar :bar :bar])
      :foo) => '(1 2 3 4 5)
      ))

;; `insert-as-nth-of [stackname local :at where]`

(fact "`insert-as-nth-of` puts the named scratch item in position n of the named stack"
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at 0)
    :scalar) => '(99 1 2 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at 1)
    :scalar) => '(1 99 2 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at 2)
    :scalar) => '(1 2 99 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at 3)
    :scalar) => '(1 2 3 99)
    )

(fact "`insert-as-nth-of` picks the index as `(mod where stacklength)`"
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at -1)
    :scalar) => '(1 2 3 99)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace  afew {:foo 99}) :scalar :foo :at 4)
    :scalar) => '(99 1 2 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at 6)
    :scalar) => '(1 2 99 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :scalar :foo :at -5)
     :scalar) => '(1 2 3 99)
     )

(fact "`insert-as-nth-of` will use a scratch variable as index if it's an scalar"
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99 :bar 1}) :scalar :foo :at :bar)
    :scalar) => '(1 99 2 3)
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99 :bar -1}) :scalar :foo :at :bar)
    :scalar) => '(1 2 3 99)
    )

(fact "`insert-as-nth-of` throws up if the scratch variable isn't an integer"
  (insert-as-nth-of
    (scratch-replace afew {:foo 99 :bar false}) :scalar :foo :at :bar) =>
      (throws #"Push DSL argument error: false is not a valid index")
  (insert-as-nth-of
    (scratch-replace afew {:foo 99}) :scalar :foo :at :bar) =>
      (throws #"Push DSL argument error: nil is not a valid index")
      )

(fact "`insert-as-nth-of` throws up if no index is given"
  (insert-as-nth-of [afew {:foo 99}] :scalar :foo) =>
    (throws #"Push DSL argument error: missing key: :at")
    )

(fact "`insert-as-nth-of` is OK if the stack is empty"
  (push/get-stack
    (insert-as-nth-of
      (scratch-replace afew {:foo 99}) :boolean :foo :at 8182)
    :boolean) => '(99)
    )

(fact "`insert-as-nth-of` creates an error if the stack item pushes us over the size limit"
  (let [wee (m/basic-interpreter :stacks {:scalar '(1 2 3)} :config {:max-collection-size 5})]
    (push/get-stack
      (insert-as-nth-of
        (scratch-replace wee {:foo [1 2 3 4 5 6]}) :scalar :foo :at 1)
      :scalar) => '(1 2 3)
    (push/get-stack
      (insert-as-nth-of
        (scratch-replace wee {:foo [1 2 3 4 5 6]}) :scalar :foo :at 1)
      :error) =>
      '({:item " tried to push an oversized item to :scalar", :step 0})
    (push/get-stack
      (insert-as-nth-of
        (scratch-replace wee {:foo [1]}) :scalar :foo :at 1)
      :scalar) => '(1 [1] 2 3)
    (push/get-stack
      (insert-as-nth-of
        (scratch-replace wee {:foo [1]}) :scalar :foo :at 1)
       :error) => '()
       ))


;; `save-snapshot`


(fact "`save-snapshot` creates a single new :snapshot item from the current interpreter's :stacks, :bindings and :config"
  (first
    (push/get-stack
      (save-snapshot afew)
      :snapshot)) =>
    (snap/->Snapshot (:stacks afew)
                     (:bindings afew)
                     (:config afew)
                     ))


(fact "`save-snapshot` works regardless of stack contents"
  (push/get-stack
    (save-snapshot (m/basic-interpreter))
    :snapshot) =>
  (list (snap/->Snapshot (:stacks (m/basic-interpreter))
        {}
        (:config (m/basic-interpreter))))
        )


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
      (retrieve-snapshot-state
        (scratch-replace r {:foo s}) :using :foo))) =>
      (contains {:exec    '(1 2 3)
                 :print   '(:NEW)
                 :log     '(:NEW)
                 :unknown '(:NEW)
                 :error   '(:NEW)
                 :foo     '(:FOO)})
                 )


(fact "`retrieve-snapshot-state` throws an exception if it lacks the hash"
  (:stacks (retrieve-snapshot-state [afew {}])) => (throws #"Push DSL argument error")
  )



;; `record-an-error [:from scratch item]`

(fact "`record-an-error` puts a time-stamped entry on the Interpreter's :error stack, with the argument as its :item field"
  (push/get-stack
    (record-an-error
      (scratch-replace afew {:foo 99}) :from :foo)
    :error) => '({:item 99, :step 0})

  (push/get-stack
    (record-an-error
      (scratch-replace afew {:foo "a message"}) :from :foo)
    :error) => '({:item "a message", :step 0})
    )

(fact "`record-an-error` logs nothing if the :from item is nil"
  (push/get-stack
    (record-an-error
      (scratch-replace afew {:foo nil}) :from :foo)
  :error) => '()
  )

(fact "`record-an-error` throws an exception if it lacks the :from argument"
  (record-an-error afew) => (throws #"Push DSL argument error")
  )

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
    (bind-item
      (scratch-replace afew {:foo 9 :bar :baz}) :foo :into :bar)) => {:baz '(9)}
      )

(fact "`bind-item` saves the item into a new ref if none is named in :into"
  (let [b (:bindings (bind-item (scratch-replace afew {:foo 9 :bar :baz}) :foo))]
    (first (vals b)) => '(9)
    (str (first (keys b))) => #":ref!\d+")
    )

(fact "`bind-item` throws an exception if the :into arg is not bound to a keyword"
  (bind-item (scratch-replace afew {:foo 9 :bar "oops!"}) :foo :into :bar) =>
    (throws #"as a :bindings key")
    )

(fact "`bind-item` has no effect if the item argument is nil (and does not push nil!)"
  (:bindings
    (bind-item
      (scratch-replace afew {:foo nil :bar :baz}) :foo :into :bar)) =>
      {:baz '()}
      )


(fact "bind-item balks when the item is oversized or would be if added to the count if items in that binding"
  (let [skimpy (push/interpreter :config {:max-collection-size 4}
                                 :bindings {:foo 999})]
    (:bindings
      (bind-item
        (scratch-replace skimpy {:bar 99 :where :foo})
        :bar
        :into :where)) => {:foo '(99 999)}

    (:bindings
      (bind-item
        (scratch-replace skimpy {:bar [1 2] :where :foo})
        :bar
        :into :where)) => {:foo '([1 2] 999)}

    (:bindings
      (bind-item
        (scratch-replace skimpy {:bar [1 2 3 4] :where :foo})
        :bar
        :into :where)) => {:foo '(999)}

    (push/get-stack
      (bind-item
        (scratch-replace skimpy {:bar [1 2 3 4 5] :where :foo})
        :bar
        :into :where)
      :error) => '({:item "Push runtime error: binding is over size limit", :step 0})

    (:bindings
      (bind-item
        (scratch-replace skimpy {:bar [1 2 3 4 5]})
        :bar ;; no named target stack
        )) => {:foo '(999)}

    (push/get-stack
      (bind-item
        (scratch-replace skimpy {:bar [1 2 3 4 5]})
        :bar  ;; no named target stack
        )
      :error) => '({:item "Push runtime error: binding is over size limit", :step 0})
    ))


(fact "`replace-binding` creates a new binding if no `:into` is given"
  (:bindings
    (replace-binding
      (scratch-replace afew {:foo 9}) :foo)) => {:xxx '(9)}
    (provided (gensym anything) => (symbol "xxx"))
    )

(fact "`replace-binding` uses a binding if `:into` is given"
  (:bindings
    (replace-binding
      (scratch-replace afew {:foo 9 :bar :where}) :foo :into :bar)) =>
        {:where '(9)}
        )

(fact "`replace-binding` does not push `nil` if the item is `nil`"
  (:bindings
    (replace-binding
      (scratch-replace afew {:foo nil :bar :where}) :foo :into :bar)) =>
        {:where '()}
        )

(fact "`replace-binding` throws an exception if `:into` is not a kw"
  (replace-binding
    (scratch-replace afew {:foo 9 :bar 8.2}) :foo :into :bar) =>
      (throws #"Cannot use '8.2' as a :bindings key")
      )

(fact "`replace-binding` constructs an entire stack if a seq is passed in"
  (:bindings
    (replace-binding
      (scratch-replace afew {:foo '(1 2 3 4)}) :foo)) =>
      {:xxx '(1 2 3 4)}
    (provided (gensym anything) => (symbol "xxx"))

  (:bindings
    (replace-binding
      (scratch-replace afew {:foo '(1 2 3) :bar :where}) :foo :into :bar)) =>
      {:where '(1 2 3)}
      )

(fact "`replace-binding` can be tricked into making a stack containing a list"
  (:bindings
    (replace-binding
      (scratch-replace afew {:foo '((1 2 3 4))}) :foo)) =>
      {:xxx '((1 2 3 4))}
    (provided (gensym anything) => (symbol "xxx"))
    )

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
        foostack (push/get-stack skimpy :foo)]

    (push/get-stack
      (push-onto
        (scratch-replace skimpy {:bar skimpy}) :foo :bar)
      :error) => '({:item " tried to push an oversized item to :foo", :step 0})

    (push/get-stack
      (push-onto
        (scratch-replace skimpy {:bar skimpy}) :foo :bar)
      :foo) => '(1 [2 (3 4) {5 6} 7] 8)
      ))

;; save-snapshot


(fact "`save-snapshot` saves a snapshot onto the intepreter's :snapshot stack"
  (push/get-stack
    (save-snapshot afew)
    :snapshot) => (list (snap/snapshot afew))
    )


(def skimpy (m/basic-interpreter :config {:max-collection-size 7}))


(fact "`save-snapshot` will not save a snapshot if the snapshot is oversized"
  (push/get-stack
    (save-snapshot skimpy)
    :snapshot) => '()
  (push/get-stack
    (save-snapshot skimpy)
    :error) =>
      '({:step 0, :item "Push runtime error: snapshot is over size limit"})
      )

;; argument retention

(fact "`start-storing-arguments` sets the :store-args? flag to true"
  (let [turned-off (m/basic-interpreter :config {:store-args? false})]
    (config-read turned-off :store-args?) => false
    (config-read (start-storing-arguments turned-off) :store-args?) => true
    ))


(fact "`stop-storing-arguments` sets the :store-args? flag to false"
  (let [turned-on (m/basic-interpreter :config {:store-args? true})]
    (config-read turned-on :store-args?) => true
    (config-read (stop-storing-arguments turned-on) :store-args?) => false
    ))


(fact "`start-cycling-arguments` sets the :cycle-args? flag to true"
  (let [turned-off (m/basic-interpreter :config {:cycle-args? false})]
    (config-read turned-off :cycle-args?) => false
    (config-read (start-cycling-arguments turned-off) :cycle-args?) => true
    ))


(fact "`stop-cycling-arguments` sets the :cycle-args? flag to false"
  (let [turned-on (m/basic-interpreter :config {:cycle-args? true})]
    (config-read turned-on :cycle-args?) =>  true
    (config-read (stop-cycling-arguments turned-on) :cycle-args?) => false
    ))


;; return-item
(fact "`return-item` pushes the indicated scratch item onto :exec"
  (push/get-stack
    (return-item
      (scratch-replace afew {:foo 99})
      :foo)
    :exec) => '(99)
    )

(fact "`return-item` doesn't raise a fuss if the scratch variable isn't set"
  (push/get-stack
    (return-item afew :foo)
    :exec) => '()
    )

(fact "`return-item` doesn't raise a fuss if the scratch variable is a list"
  (push/get-stack
    (return-item
      (scratch-replace afew {:foo '(4 5 6)})
      :foo)
    :exec) => '((4 5 6))
    )


(fact "return-item balks when the items are oversized"
  (let [skimpy (push/interpreter :config {:max-collection-size 12}
                                 :stacks {:exec '(1 [2 (3 4) {5 6} 7] 8)} )]

    (push/get-stack
      (return-item
        (scratch-replace skimpy {:bar '(9 9 9 9 9 9 9 9 9)})
        :bar)
      :error) => '({:item " tried to push an oversized item to :exec" :step 0})

      (push/get-stack
        (return-item
          (scratch-replace skimpy {:bar '(9 9 9 9 9 9 9 9 9)})
          :bar)
        :exec) => '(1 [2 (3 4) {5 6} 7] 8)
      ))



;; return-codeblock
(fact "`return-codeblock` pushes the indicated scratch items onto :exec as a block"
  (push/get-stack
    (return-codeblock
      (scratch-replace afew {:foo 99 :bar 88})
      :foo :bar)
    :exec) => '((99 88))
    )

(fact "`return-codeblock` doesn't raise a fuss if a scratch variable isn't set"
  (push/get-stack
    (return-codeblock
      (scratch-replace afew {:foo 99 :bar 88})
      :baz :foo :bar :baz)
    :exec) => '((99 88))
    )

(fact "`return-codeblock` doesn't raise a fuss if a scratch variable is a list"
  (push/get-stack
    (return-codeblock
      (scratch-replace afew {:foo '(4 5 6) :bar [1 2 3]})
      :bar :foo)
    :exec) => '(([1 2 3] (4 5 6)))
    )

(fact "`return-codeblock` doesn't raise a fuss if all the scratch variables are nil"
  (push/get-stack
    (return-codeblock
      (scratch-replace afew {})
      :bar :foo)
    :exec) => '(())
    )

(fact "`return-codeblock` has no problems if no keywords are given"
  (push/get-stack
    (return-codeblock
      (scratch-replace afew {}))
    :exec) => '(( ))
    )

(fact "return-codeblock balks when the items are oversized"
  (let [skimpy (push/interpreter :config {:max-collection-size 12}
                                 :stacks {:exec '(1 [2 (3 4) {5 6} 7] 8)} )]

    (push/get-stack
      (return-codeblock
        (scratch-replace skimpy {:foo 9 :bar [99 99 99]})
        :bar :bar :bar)
      :error) => '({:item " tried to push an oversized item to :exec" :step 0})

    (push/get-stack
      (return-codeblock
        (scratch-replace skimpy {:foo 9 :bar [99 99 99]})
        :bar)
      :error) => '()

    (push/get-stack
      (return-codeblock
        (scratch-replace skimpy {:foo 9 :bar [99 99 99]})
        :bar :bar :bar)
      :exec) => '(1 [2 (3 4) {5 6} 7] 8)
      ))

;; print-item
(fact "`print-item` pushes a string to :print"
  (push/get-stack
    (print-item
      (scratch-replace afew {:foo 99})
      :foo)
    :print) => '("99")
  (push/get-stack
    (print-item
      (scratch-replace afew {:foo "bar"})
      :foo)
    :print) => '("\"bar\"")
  (push/get-stack
    (print-item
      (scratch-replace afew {:foo #{9 8 2} })
      :foo)
    :print) => (list (pr-str #{9 8 2}))
    )
