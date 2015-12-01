(ns demo.examples.x-plus-6
  (:use midje.sweet)
  (:require [push.interpreter.templates.one-with-everything :as owe])
  (:require [push.interpreter.core :as core])
  (:require [push.util.stack-manipulation :as u])
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
;;
;; ## Guessing
;;
;; Finally, there's the question we should ask next: What's a "guess"?
;;
;; The `push-in-clojure` package includes a number of "template" interpreters, so let's use one of those as a basis. To make things interesting (and also to drive home the point that a Push program imposes almost no semantic meaning on your input:output choices), let's use the one called `one-with-everything`, which piles every defined Push type, module, aspect and instruction into one convenient jumble.
;;
;; We'll want an input value, call it `:x`, but otherwise it's as simple as this:


(def my-interpreter (owe/make-everything-interpreter :inputs {:x nil}))


(fact "my-interpreter knows some things"
  (keys (:instructions my-interpreter)) =>
    (contains [:integer-subtract :exec-while] :gaps-ok :in-any-order)

  (< 400 (count (:instructions my-interpreter))) => truthy

  (map :name (:types my-interpreter)) => '(:set :vector :strings :integers :floats :chars :booleans :string :float :char :boolean :integer)

  (keys (:inputs my-interpreter)) => '(:x))


;; That seems like it should more than suffice.
;;
;; A Push program is a vector of items: instruction keys, input keys, literal values and lists of those things. For example, both `[1 (false [4 3 1] :code-flush)]` and `[:x 6 "blueberry" :integer-add :integer->float]` are valid Push programs this interpreter would recognize and run. Any vector of these sorts of valid item, of any length, would be a valid "guess" for our algorithm to match the data. The first trick is arguably being able to pick random _valid_ programs that this interpreter will recognize and run.
;;
;; As the preceding tests imply, a new Push `Interpreter` instance already knows and can communicate a lot about the things it will do and recognize. Looking at those `midje` results, we know it already can deal with literal values of these types:
;;
;; - `:boolean`
;; - `:booleans`
;; - `:char`
;; - `:chars`
;; - `:integer`
;; - `:integers`
;; - `:float`
;; - `:floats`
;; - `:set`
;; - `:string`
;; - `:strings`
;; - `:vector`
;;
;; We know it can also recognize a bunch of instructions:


(fact "the number of instructions known ain't small"
  (count (:instructions my-interpreter)) => 587) ;; as of this writing


;; ... and what those are:


(fact "my-interpreter can tell us the instructions it knows"
  (sort (keys (:instructions my-interpreter))) =>
    '(:boolean->code :boolean->float :boolean->integer :boolean->signedfloat :boolean->signedint :boolean->string :boolean-and :boolean-cutflip :boolean-cutstack :boolean-dup :boolean-empty? :boolean-equal? :boolean-flipstack :boolean-flush :boolean-not :boolean-notequal? :boolean-or :boolean-pop :boolean-print :boolean-return :boolean-return-pop :boolean-rotate :boolean-shove :boolean-stackdepth :boolean-swap :boolean-xor :boolean-yank :boolean-yankdup :booleans->code :booleans-butlast :booleans-concat :booleans-conj :booleans-contains? :booleans-cutflip :booleans-cutstack :booleans-do*each :booleans-dup :booleans-empty? :booleans-emptyitem? :booleans-equal? :booleans-first :booleans-flipstack :booleans-flush :booleans-fromexample :booleans-generalize :booleans-generalizeall :booleans-indexof :booleans-last :booleans-length :booleans-new :booleans-notequal? :booleans-nth :booleans-occurrencesof :booleans-pop :booleans-portion :booleans-print :booleans-remove :booleans-replace :booleans-replacefirst :booleans-rest :booleans-return :booleans-return-pop :booleans-reverse :booleans-rotate :booleans-set :booleans-shatter :booleans-shove :booleans-stackdepth :booleans-swap :booleans-take :booleans-yank :booleans-yankdup :char->code :char->float :char->integer :char->string :char-cutflip :char-cutstack :char-digit? :char-dup :char-empty? :char-equal? :char-flipstack :char-flush :char-letter? :char-lowercase? :char-max :char-min :char-notequal? :char-pop :char-print :char-return :char-return-pop :char-rotate :char-shove :char-stackdepth :char-swap :char-uppercase? :char-whitespace? :char-yank :char-yankdup :char<? :char>? :chars->code :chars-butlast :chars-concat :chars-conj :chars-contains? :chars-cutflip :chars-cutstack :chars-do*each :chars-dup :chars-empty? :chars-emptyitem? :chars-equal? :chars-first :chars-flipstack :chars-flush :chars-fromexample :chars-generalize :chars-generalizeall :chars-indexof :chars-last :chars-length :chars-new :chars-notequal? :chars-nth :chars-occurrencesof :chars-pop :chars-portion :chars-print :chars-remove :chars-replace :chars-replacefirst :chars-rest :chars-return :chars-return-pop :chars-reverse :chars-rotate :chars-set :chars-shatter :chars-shove :chars-stackdepth :chars-swap :chars-take :chars-yank :chars-yankdup :char≤? :char≥? :code->set :code->string :code-append :code-atom? :code-cons :code-container :code-contains? :code-cutflip :code-cutstack :code-do :code-do* :code-do*count :code-do*range :code-do*times :code-drop :code-dup :code-empty? :code-equal? :code-extract :code-first :code-flipstack :code-flush :code-if :code-insert :code-length :code-list :code-map :code-member? :code-noop :code-notequal? :code-nth :code-null? :code-points :code-pop :code-position :code-print :code-quote :code-reduce :code-rest :code-return :code-return-pop :code-rotate :code-shove :code-size :code-stackdepth :code-subst :code-swap :code-wrap :code-yank :code-yankdup :environment-begin :environment-empty? :environment-end :environment-new :environment-stackdepth :error-empty? :error-stackdepth :exec->string :exec-cutflip :exec-cutstack :exec-do*count :exec-do*range :exec-do*times :exec-do*while :exec-dup :exec-empty? :exec-equal? :exec-flipstack :exec-flush :exec-if :exec-k :exec-noop :exec-notequal? :exec-pop :exec-print :exec-return :exec-return-pop :exec-rotate :exec-s :exec-shove :exec-stackdepth :exec-string-iterate :exec-swap :exec-when :exec-while :exec-y :exec-yank :exec-yankdup :float->asciichar :float->boolean :float->char :float->code :float->integer :float->string :float-add :float-cosine :float-cutflip :float-cutstack :float-dec :float-divide :float-dup :float-empty? :float-equal? :float-flipstack :float-flush :float-inc :float-max :float-min :float-mod :float-multiply :float-notequal? :float-pop :float-print :float-return :float-return-pop :float-rotate :float-shove :float-sign :float-sine :float-stackdepth :float-subtract :float-swap :float-tangent :float-yank :float-yankdup :float<? :float>? :floats->code :floats-butlast :floats-concat :floats-conj :floats-contains? :floats-cutflip :floats-cutstack :floats-do*each :floats-dup :floats-empty? :floats-emptyitem? :floats-equal? :floats-first :floats-flipstack :floats-flush :floats-fromexample :floats-generalize :floats-generalizeall :floats-indexof :floats-last :floats-length :floats-new :floats-notequal? :floats-nth :floats-occurrencesof :floats-pop :floats-portion :floats-print :floats-remove :floats-replace :floats-replacefirst :floats-rest :floats-return :floats-return-pop :floats-reverse :floats-rotate :floats-set :floats-shatter :floats-shove :floats-stackdepth :floats-swap :floats-take :floats-yank :floats-yankdup :floatsign->boolean :float≤? :float≥? :integer->asciichar :integer->boolean :integer->char :integer->code :integer->float :integer->string :integer-add :integer-cutflip :integer-cutstack :integer-dec :integer-divide :integer-dup :integer-empty? :integer-equal? :integer-few :integer-flipstack :integer-flush :integer-inc :integer-lots :integer-many :integer-max :integer-min :integer-mod :integer-multiply :integer-notequal? :integer-pop :integer-print :integer-return :integer-return-pop :integer-rotate :integer-shove :integer-sign :integer-some :integer-stackdepth :integer-subtract :integer-swap :integer-yank :integer-yankdup :integer<? :integer>? :integers->code :integers-butlast :integers-concat :integers-conj :integers-contains? :integers-cutflip :integers-cutstack :integers-do*each :integers-dup :integers-empty? :integers-emptyitem? :integers-equal? :integers-first :integers-flipstack :integers-flush :integers-fromexample :integers-generalize :integers-generalizeall :integers-indexof :integers-last :integers-length :integers-new :integers-notequal? :integers-nth :integers-occurrencesof :integers-pop :integers-portion :integers-print :integers-remove :integers-replace :integers-replacefirst :integers-rest :integers-return :integers-return-pop :integers-reverse :integers-rotate :integers-set :integers-shatter :integers-shove :integers-stackdepth :integers-swap :integers-take :integers-yank :integers-yankdup :integer≤? :integer≥? :intsign->boolean :log-empty? :log-stackdepth :print-empty? :print-newline :print-space :print-stackdepth :push-counter :push-inputs :push-inputset :push-instructionset :set->code :set-cutflip :set-cutstack :set-difference :set-dup :set-empty? :set-equal? :set-flipstack :set-flush :set-intersection :set-notequal? :set-pop :set-print :set-return :set-return-pop :set-rotate :set-shove :set-stackdepth :set-subset? :set-superset? :set-swap :set-union :set-yank :set-yankdup :string->chars :string->code :string->float :string->integer :string-butlast :string-concat :string-conjchar :string-contains? :string-containschar? :string-cutflip :string-cutstack :string-dup :string-empty? :string-emptystring? :string-equal? :string-first :string-flipstack :string-flush :string-indexofchar :string-last :string-length :string-max :string-min :string-notequal? :string-nth :string-occurrencesofchar :string-pop :string-print :string-removechar :string-replace :string-replacechar :string-replacefirst :string-replacefirstchar :string-rest :string-return :string-return-pop :string-reverse :string-rotate :string-setchar :string-shatter :string-shove :string-solid? :string-spacey? :string-splitonspaces :string-stackdepth :string-substring :string-swap :string-take :string-yank :string-yankdup :string<? :string>? :strings->code :strings-butlast :strings-concat :strings-conj :strings-contains? :strings-cutflip :strings-cutstack :strings-do*each :strings-dup :strings-empty? :strings-emptyitem? :strings-equal? :strings-first :strings-flipstack :strings-flush :strings-fromexample :strings-generalize :strings-generalizeall :strings-indexof :strings-last :strings-length :strings-new :strings-notequal? :strings-nth :strings-occurrencesof :strings-pop :strings-portion :strings-print :strings-remove :strings-replace :strings-replacefirst :strings-rest :strings-return :strings-return-pop :strings-reverse :strings-rotate :strings-set :strings-shatter :strings-shove :strings-stackdepth :strings-swap :strings-take :strings-yank :strings-yankdup :string≤? :string≥? :vector->code :vector->set :vector-butlast :vector-concat :vector-conj :vector-contains? :vector-cutflip :vector-cutstack :vector-do*each :vector-dup :vector-empty? :vector-emptyitem? :vector-equal? :vector-first :vector-flipstack :vector-flush :vector-fromexample :vector-indexof :vector-last :vector-length :vector-new :vector-notequal? :vector-nth :vector-occurrencesof :vector-pop :vector-portion :vector-print :vector-refilter :vector-refilterall :vector-remove :vector-replace :vector-replacefirst :vector-rest :vector-return :vector-return-pop :vector-reverse :vector-rotate :vector-set :vector-shatter :vector-shove :vector-stackdepth :vector-swap :vector-take :vector-yank :vector-yankdup)
    )

(defn random-instruction
  [interpreter]
  (rand-nth (keys (:instructions interpreter))))


(defn random-float
  [scale]
  (* scale (/ (rand-int scale) 32.0)))


(defn random-integer
  [scale]
  (rand-int scale))


(defn random-input
  [interpreter]
  (rand-nth (keys (:inputs interpreter))))


(defn random-boolean
  []
  (< 0.5 (rand)))


(declare random-push-item)

(defn random-code
  [interpreter length]
  (into '() (repeatedly length #(random-push-item interpreter))))


(defn random-push-item
  [interpreter]
  (let [diceroll (rand-int 20)]
    (condp #(<= %1 %2) diceroll
      15 (random-float 100)
      13 (random-integer 100)
      11 (random-boolean)
      9 (random-input my-interpreter)
      6 (random-code my-interpreter 5)
      (random-instruction interpreter)
      )))
    


(defn random-push-program
  [length]
  (into [] (repeatedly length #(random-push-item my-interpreter))))


(def sample-program (random-push-program 7))


(fact 
  (let [dude (core/recycle-interpreter
              my-interpreter (random-push-program 1000)
              :inputs {:x 17})]
    ; (println (:program dude))
    (println
      (sort 
        (reduce-kv 
          #(assoc %1 %2 (count %3)) 
          {} 
          (:stacks (core/run dude 5000)))))
    (println
      (u/get-stack (core/run dude 5000) :return))
    ))










