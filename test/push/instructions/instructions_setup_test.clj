(ns push.instructions.instructions-setup-test
  (:use midje.sweet)
  (:require [push.util.stack-manipulation :as u])
  (:require [push.instructions.dsl :as d])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.instructions.core])
  )


;; make-instruction (bare bones)


(fact "make-instruction creates a new Instruction record with default values"
  (:token (make-instruction :foo)) => :foo
  (:tags (make-instruction :foo)) => #{}
  (:needs (make-instruction :foo)) => {}
  (:transaction (make-instruction :foo)) => identity)


(fact "make-instruction accepts a :tags argument"
  (:tags (make-instruction :foo :tags #{:nasty :brutish})) => #{:brutish :nasty})


(fact "make-instruction accepts a :needs argument"
  (:needs (make-instruction :foo :needs {:integer 2})) => {:integer 2})


(fact "make-instruction accepts a :transaction argument"
  (let [fake_fn 88123]
     (:transaction (make-instruction :foo :transaction fake_fn)) => fake_fn))


(fact "`make-instruction` sets the docstring field to a default value"
  (:docstring (make-instruction :foo)) => "`:foo` needs a docstring!")


;; build-instruction


(fact "build-instruction creates a new Instruction with the right token"
  (:token
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => :foobar)


(fact "build-instruction can accept an optional #tags argument"
  (:tags 
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => #{}
  (:tags 
    (build-instruction foobar
      :tags #{:foo :bar :baz!}
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => #{:bar :baz! :foo})


(fact "build-instruction creates a new Instruction with the right needs"
  (:needs
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => {:bar 0 :foo 1})


(fact "build-instruction creates a new Instruction a transaction that's a function"
  (fn? (:transaction
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in)))) => true)


(fact "`build-instruction` captures an un-keyworded docstring if it's after the token"
  (:docstring 
    (build-instruction
      foobar
      "foobar really?"
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => "foobar really?")


(fact "`build-instruction` lacking a docstring will get default"
  (:docstring 
    (build-instruction foobar
      (d/consume-top-of :foo :as :in)
      (d/push-onto :bar :in))) => "`:foobar` needs a docstring!")



;; a bit of a test


(fact "registering and executing the instruction in an Interpreter works"
  (let [foobar (build-instruction foobar             ;;; moves top :foo to :bar
                  (d/consume-top-of :foo :as :in)
                  (d/push-onto :bar :in))
        context (i/register-instruction 
                  (i/basic-interpreter :stacks {:foo '(1 2 3) :bar '(4 5 6)})
                  foobar)]
  (u/get-stack (i/execute-instruction context :foobar) :bar ) => '(1 4 5 6)
  (u/get-stack (i/execute-instruction context :foobar) :foo ) => '(2 3)))


;; automatic docstrings

(fact "a generated code-fromX instruction has a reasonable docstring"
  (:docstring (t/simple-item-to-code-instruction :char)) =>
    "`:code-fromchar` pops the top `:char` item and pushes it to `:code`")


(fact "a generated string-fromX instruction has a reasonable docstring"
  (:docstring (push.types.base.string/simple-item-to-string-instruction :foo)) =>
    "`:string-fromfoo` pops the top item from the `:foo` stack and converts it to a `:string` (using Clojure's `str` function)")


(def i-know-foo (t/make-type :foo :recognizer integer?))


(fact "a generated stackdepth-instruction has a reasonable docstring"
  (:docstring (t/stackdepth-instruction i-know-foo)) =>
    "`:foo-stackdepth` pushes an `:integer` which is the number of items in the `:foo` stack.")


(fact "a generated empty?-instruction has a reasonable docstring"
  (:docstring (t/empty?-instruction i-know-foo)) =>
    "`:foo-empty?` pushes a `:boolean`, `true` if the `:foo` stack is empty, `false` otherwise.")


(fact "a generated equal?-instruction has a reasonable docstring"
  (:docstring (t/equal?-instruction i-know-foo)) =>
    "`:foo-equal?` pops the top two `:foo` items and pushes `true` if they are equal, `false` otherwise.")


(fact "a generated notequal?-instruction has a reasonable docstring"
  (:docstring (t/notequal?-instruction i-know-foo)) =>
    "`:foo-notequal?` pops the top two `:foo` items and pushes `false` if they are equal, `true` otherwise.")


(fact "a generated lessthan?-instruction has a reasonable docstring"
  (:docstring (t/lessthan?-instruction i-know-foo)) =>
    "`:foo<?` pops the top two `:foo` items and pushes `true` if the top item is less than the second, `false` otherwise.")


(fact "a generated lessthanorequal?-instruction has a reasonable docstring"
  (:docstring (t/lessthanorequal?-instruction i-know-foo)) =>
    "`:foo≤?` pops the top two `:foo` items and pushes `true` if the top item is less than or equal to the second, `false` otherwise.")


(fact "a generated greaterthanorequal?-instruction has a reasonable docstring"
  (:docstring (t/greaterthanorequal?-instruction i-know-foo)) =>
    "`:foo≥?` pops the top two `:foo` items and pushes `true` if the top item is greater than or equal to the second, `false` otherwise.")


(fact "a generated greaterthan?-instruction has a reasonable docstring"
  (:docstring (t/greaterthan?-instruction i-know-foo)) =>
    "`:foo>?` pops the top two `:foo` items and pushes `true` if the top item is greater than the second, `false` otherwise.")


(fact "a generated min-instruction has a reasonable docstring"
  (:docstring (t/min-instruction i-know-foo)) =>
    "`:foo-min` pops the top two `:foo` items and pushes the _smaller_ of the two.")


(fact "a generated max-instruction has a reasonable docstring"
  (:docstring (t/max-instruction i-know-foo)) =>
    "`:foo-max` pops the top two `:foo` items and pushes the _larger_ of the two.")



(fact "a generated dup-instruction has a reasonable docstring"
  (:docstring (t/dup-instruction i-know-foo)) =>
    "`:foo-dup` examines the top `:foo` item and pushes a duplicate to the same stack.")


(fact "a generated flush-instruction has a reasonable docstring"
  (:docstring (t/flush-instruction i-know-foo)) =>
    "`:foo-flush` discards all items from the `:foo` stack.")


(fact "a generated pop-instruction has a reasonable docstring"
  (:docstring (t/pop-instruction i-know-foo)) =>
    "`:foo-pop` discards the top item from the `:foo` stack.")


(fact "a generated rotate-instruction has a reasonable docstring"
  (:docstring (t/rotate-instruction i-know-foo)) =>
    "`:foo-rotate` pops the top three items from the `:foo` stack; call them `A`, `B` and `C`, respectively. It pushes them back so that top-to-bottom order is now `'(C A B ...)`")


(fact "a generated shove-instruction has a reasonable docstring"
  (:docstring (t/shove-instruction i-know-foo)) =>
    "`:foo-shove` pops the top item from the `:foo` stack and the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the top item is _moved_ so that it is in that position in the resulting stack.")


(fact "a generated swap-instruction has a reasonable docstring"
  (:docstring (t/swap-instruction i-know-foo)) =>
    "`:foo-swap` swaps the positions of the top two `:foo` items.")


(fact "a generated yank-instruction has a reasonable docstring"
  (:docstring (t/yank-instruction i-know-foo)) =>
    "`:foo-yank` pops the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the item _currently_ found in the indexed position in the `:foo` stack is _moved_ so that it is on top.")


(fact "a generated yankdup-instruction has a reasonable docstring"
  (:docstring (t/yankdup-instruction i-know-foo)) =>
    "`:foo-yankdup` pops the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the item _currently_ found in the indexed position in the `:foo` stack is _copied_ so that a duplicate of it is on top.")
