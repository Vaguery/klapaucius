(ns demo.examples.x-plus-6
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  )


;; # An introductory example

;; Suppose we have a simple task: to discover Push expressions (that is, algorithms) which _fit_ the following data:
;;
;;           x    y
;;         12.3   18.3
;;         -2.0    4.0
;;         33.8   39.8
;;         10.2   16.2
;;         -1.2    4.8
;;  187267311.9   187267317.9
;;  (and so on)
;;
;; Regardless of how we decide to go about _searching_ for these expressions, we'll use the `push-in-clojure` library to build `Interpreter` instances to run our prospective solutions so we can score them against this training data. In this example, let me take what might be the simplest approach that could possibly work: repeated guessing, keeping the best-scoring example seen so far.
;;
;; But before we start, we should ask: In what way is a Push program a "function" of the sort we're used to? Push carefully and thoroughly defines a wide variety of types and instructions, but those instructions technically transform one `Interpreter` state into another, not "inputs" into "outputs". Push has a surprisingly light hand when imposing _meaning_ on the dynamics of a running program, as a result. We can specify `input` values and manage which `instructions` will be recognized by the `Interpreter`, but there is _no aspect of the Push language_ corresponding to our common idea of "return value". Even the items on the `:print` and `:return` stacks are left uninterpreted beyond their stack names.
;;
;; The most common approach for "forcing" a Push program return a particular typed result has been to take the _top item from a particular stack at a particular time_ as "the answer". We can do that here as well; the data seems to be floating-point values, so we can use the top `:float` item at (say) 3000 interpreter steps to signify the "answer".
;;
;; Two things to say about this decision:
;;
;; First, notice we could as easily have said the second `:float` item, or the _best_ `:float` item, or the next `:float` item pushed to that stack _after_ 3000 interpreter steps have been taken. Or we could have taken the average of all the `:integer` values on that stack, or take the entire contents of the `:print` stack, convert it to a string, and then parse that string as a floating-point value. These approaches may seem arbitrary and convoluted for this example, but keep in mind that we bring those judgments with us—they are not hard-coded into the Push language's syntax or semantics.
;;
;; Second, realize that there may not _be_ a `:float` value on that stack when we decide to check. There may be no `:float` literals in the program we run, or it might not even refer to the input value `x`, or we might in the course of running arbitrary code consume or delete all the `:float` values. The arbitrariness of mapping the entire Push interpreter state (all its stacks and history if we like) does not affect the deterministic but essentially arbitrary dynamics of running a program.
;;
;; This leaves us with a question of how to model "results" for programs that don't even give us the input to our defined "return function" (top thing on the `:float` stack after 3000 steps). Should we use a default value? Should we permit "no answer given" as the _result_, and somehow penalize the _score_ of a program that gives that result? Or should we immediately discard any program that provides no `:float` value (which is a sort of Extremely Large Immediate Penalty function)?
;;
;; Here I'll stick with "top `:float` at 3000 steps, if any", and immediately discard any program that gives no answer.
;;
;; ## Guessing
;;
;; Finally, there's the question we should ask next: What's a "guess"?
;;
;; The `push-in-clojure` package includes a number of "template" interpreters, so let's use one of those as a basis. To make things interesting (and also to drive home the point that a Push program imposes almost no semantic meaning on your input:output choices), let's use the one called `one-with-everything`, which piles every defined Push type, module, aspect and instruction into one convenient jumble.
;;
;; We'll want an input value, call it `:x`, but otherwise it's as simple as this:


(def my-interpreter (owe/make-everything-interpreter :inputs {:x 12.3}))


(fact "my-interpreter knows some things"
  (keys (:instructions my-interpreter)) =>
    (contains [:integer-subtract :exec-while] :gaps-ok :in-any-order)

  (< 400 (count (:instructions my-interpreter))) => truthy

  (map :name (:types my-interpreter)) => '(:set :vector :strings :integers :floats :chars :booleans :string :float :char :boolean :integer)

  (keys (:inputs my-interpreter)) => '(:x))

