# Klapaucius

> "Certainly not! I didn't build a machine to solve ridiculous crossword puzzles! That's hack work, not Great Art! Just give it a topic, any topic, as difficult as you like..."
> 
> Klapaucius thought, and thought some more. Finally he nodded and said:
"Very well. Let's have a love poem, lyrical, pastoral, and expressed in the language of pure mathematics. Tensor algebra mainly, with a little topology and higher calculus, if need be. But with feeling, you understand, and in the cybernetic spirit."
> 
> "Love and tensor algebra?" Have you taken leave of your senses?" Trurl began, but stopped, for his electronic bard was already declaiming....

(previously `push-in-clojure`)

This library includes a clean, fully tested, extensible and maintainable Push language interpreter. Push is a simple and robust programming language designed to be _evolved_ rather than hand-composed by human programmers, which originated in the [Hampshire College Computational Intelligence Lab](http://sites.hampshire.edu/ci-lab/). You may have run across it by way of [Lee Spector's Clojush](https://github.com/lspector/Clojush) project.

## Requirements

The project is written in Clojure 1.8, and depends heavily on [Midje](https://github.com/marick/Midje/) for testing.

## Using the library

### Project status

`klapaucius "0.1.5-SNAPSHOT"` includes a fully working interpreter, but is undergoing rapid expansion (thus the `SNAPSHOT` designation). While the current version is rigorously tested,  **substantial deep architectural changes** will be made leading up the 0.2 release. If you're going to work on it, please contact me during this great leap forward, and submit pull requests for small amounts of work in numerous git branches!

### Project dependencies

Using `leiningen`, add the following dependency to your `project.clj`

```clojure
(defproject my-new-project "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [klapaucius "0.1.5-SNAPSHOT"]
                 ;; ... your other dependencies here ...
                 ] 
  :profiles {:dev
              {:dependencies [[midje "1.8.3"]]}})
                            ;; ^^^^^ you should run the tests
```

## Usage

```clojure
(ns my.fancy.namespace
  (:require [push.core :as push])
  (:require [push.interpreter.core :as interpreter])

;; ...

(def runner
  (push/interpreter
    :bindings {:speed 88.2 :burden 2 :african? false}))

(def my-push-program [1 :burden :integer-add])

(def final-integer-stack
  (push/get-stack
    (push/run runner my-push-program 1000)
    :integer))
```

### In the REPL

(assuming you've run `lein repl` from within your project directory, and you have the dependency mentioned above in `project.clj`)

```text
user=> (require '[push.core :as push])
;; nil

user=> (def runner (push/interpreter :bindings {:speed 8.1 :burden 2 :african? false}))
#'user/runner

;; don't do this except to learn a lesson:
user=> (push/run
  #_=>   runner
  #_=>   [1 :burden :integer-add]
  #_=>   1000)
#push.interpreter.core.Interpreter{:program [1 :burden :integer-add], :types ({:name :numeric-scaling, :attributes #{:numeric}, :instructions {:integer-few #push.instructions.core.Instruction{:token :integer-few, :docstring "`:integer-few` pops the top `:integer` value, and calculates `(mod 10 x)`.", :tags #{:numeric}, :needs {:integer 1}, :products {:integer 1}...

;; (push/run INTERPRETER) returns the ENTIRE interpreter state after running the program, including all the instruction definitions, stack contents, logs and more!


;; better to capture the state of the interpreter in a `var`
user=> (def ran-it (push/run
  #_=>   runner
  #_=>   [1 :burden :integer-add]
  #_=>   1000))
#'user/ran-it


user=> (push/get-stack ran-it :integer)
(3)


;; push/run requires 3 arguments: an interpreter, a program, and a step limit
;; but it permits an optional :bindings argument (a hashmap)
;; plus several other optional arguments (see docs)
user=> (push/get-stack (push/run runner [1 :burden :integer-add] 300 :bindings {:burden 87}) :integer)
(88)


user=> (push/known-instructions runner)
(:strings-cutflip :integers-yankdup :integer-max :line-circle-miss? :floats-length :string-cutstack :print-space :integer-multiply :strings-shatter :integers-contains? :char-lowercase? :booleans-rotate :float->boolean :string-butlast :code-return-pop :string-min :strings-stackdepth :set-return :integers-print :string-occurrencesofchar :push-inputset :integer-sign :circle-yank :char-max :exec-do*count :string-stackdepth :booleans-last :circle-swap :integers-set :integers-byexample :vector-replace :code-flipstack :exec-pop :boolean-dup :integers-take :line-print :integer-mod :set-flipstack :integers-replacefirst :string>? :environment-stackdepth :string->float :vector-return-pop :set-pop :string->integer :floats-contains? :strings-equal?
;;... a HUGE list of known instructions will follow
)


user=> (push/input-names runner)
(:speed :burden :african?)


user=> (:bindings runner)
{:speed 8.1, :burden 2, :african? false}


user=> (:program ran-it)
[1 :burden :integer-add]


user=> (:stacks ran-it)
{:booleans (), :integers (), :unknown (), :exec (), :return (), :float (), :strings (), :circle (), :string (), :vector (), :print (), :integer (3), :chars (), :line (), :code (), :point (), :error (), :environment (), :set (), :log ({:step 4, :item :integer-add} {:step 3, :item 2} {:step 2, :item :burden} {:step 1, :item 1}), :boolean (), :char (), :floats ()}

;; NOTICE THE :log STACK ^^^


;; also not we saved the interpreter after "1000 steps" in 'ran-it but:
user=> (:counter ran-it)
4
```


## What's it for?

[Push](https://github.com/lspector/Clojush) is a small, expressive language for genetic programming. That is, it's a language specifically designed for _machines to write_, not for people: it's almost illegible in practice, but that lack of clarity is due to the extraordinarily flexible syntax that lets almost any program run without "errors".

A Push interpreter has very few moving parts. There are a number of `stacks`, which are traditional LIFO stacks with (theoretically) unlimited capacity. The most important of these is the `:exec` stack, which holds the running code itself.

A Push program is an arbitrary ordered list composed of _inputs_, _instructions_, _literals_ and sub-lists of those. In each step of executing a program that's been pushed onto the `:exec` stack, the interpreter pops off the top item, and

- if an `input`, then the bound value is looked up and pushed to the `:exec` stack
- if an `instruction`, the indicated changes are made to the interpreter state, usually by popping arguments from the various stacks
- if a `literal` (of a recognized type), the item is pushed to a specified stack, which in the "basic model" includes
  - `:boolean` 
  - `:char` (single Clojure `char` items)
  - `:code` (any items; not a "type", but a place certain instructions send things)
  - `:float` 
  - `:integer` 
  - `:string` 
- if a list of items, the list is "unwrapped" and pushed back onto the `:exec` stack so the items inside it will be executed in turn

And that's about it. There are a few other special-purpose stacks, mainly used for IO and logging. It's possible to extend the language easily by defining new types (which in Push means little more than "literals that are recognized and sent to a stack"), the stacks that go with them, and instructions to manipulate them usefully.

The interpreter (generally) will run until the `:exec` stack has been emptied. This can happen if all the items have been popped and pushed onto other stacks, or when instructions consume arguments or make more dramatic changes to the type and number of items on the stacks. That said, some of the instructions _add_ items to the stacks, including adding new items to the `:exec` stack, and thus the program can end up running quite a while... or forever. (That's common enough, frankly, that it's pretty unwise to run a Push program without some additional halting condition—for example, a maximum number of steps to take.)

By convention, Push programs have no "return value" as such. They are simply ambiguous dynamical processes manipulating the values on the stacks, and whatever meaning a particular program has for a user depends entirely on how they intend to interrogate that dynamical state. For example, when Push programs are used in [symbolic regression](https://en.wikipedia.org/wiki/Symbolic_regression) projects, the common convention is to let the program "finish" (see above), and then look at the top number on one of the number stacks as "the answer". That said, a lot is happening in most interesting Push programs, and "the right answer" may not be where you're looking.

The design of evolutionary fitness functions is for another day. In this library, the only thing we're doing is running programs with specified `input` bindings until specified termination conditions are met, and then providing access to the whole pile of everything that happened in the meantime.

But the _reason_ we want an interpreter for a language people can't typically read is that we can _evolve the programs to do what we want_. Because Push (and languages like it) are able to run almost any sequence of defined tokens, and behave in so many diverse ways as they do so, it's possible to do [some amazing things](http://faculty.hampshire.edu/lspector/push.html) with artificial selection under random variation.

## Features

- A Push `type` is a suite of related instructions, associated with a `stack name` and `recognizer`. Whenever a literal item of that type appears in a running program, the `router` will use the `recognizer` function to send it to the indicated stack. For example, the `:boolean` type definition includes a `recognizer` which will send the literal values `true` and `false` (when they appear in a running Push program) to the `:boolean` stack.
- A push `module` is a suite of related instructions and a `stack name`, which do _not_ have a `recognizer` and which therefore have no "objects" which can appear as literals in a Push program. For example, the `:exec` module contains many instructions which manipulate items on the `:exec` stack, but there is no such thing as an "`:exec` item" as such.
- A Push `aspect` is a collection of instructions of shared functionality, which can be quickly associated with a `type` or `module`. For example, `(make-comparable mytype)` will automatically create and append the instructions `:mytype≤?`, `:mytype<?`, `:mytype>?`, `:mytype≥?`, `mytype-max` and `mytype-min`. Note that this may cause problems if your underlying (Clojure) `mytype` objects do not adhere to the `Comparable` protocol!
- The `:log` stack automatically records each item that the interpreter pops from the `:exec` stack
- the `:error` stack accumulates records of incidents (which normally occur in all Push interpreters) such as instructions encountered when some arguments are missing, attempted division by 0, and so forth

## Use cases and examples

**TBD**

### Using an interpreter template

### Creating a custom interpreter template

### Adding a new instruction

### Adding a new type or module

### Running an interpreter several times with different inputs

### Accessing interpreter state for "fitness scores"

### Working with halting problems

### Running a halted interpreter for more steps

### "Lenient mode"

## How to run the tests

Typing `lein midje` at the command line (from inside this directory) will run all tests that aren't tagged `:slow` or `:acceptance`. The reporting level isn't very verbose.

`lein midje :autotest` will run all the (non-slow) tests indefinitely. It sets up a watcher on the code files. If they change, only the relevant tests will be
run again. Be sure to run this in its own shell, since it's not going to let you work with other processes.

`lein midje :filter acceptance` will run the acceptance tests. These tests will really push the system they're run on, and will probably peg your CPU (no matter how big it is) for most of an hour.

## How to contribute

- If you are interested in adding new types or instruction sets for general-purpose or domain-specific projects, poke me and I will make sure the guide for adding those is up to date. In general, you should be able to look over the definitions in any of the type or module definitions in `push.types`.
- If you notice bugs, please open an Issue
- Feel free to point out where the code or documentation could be made more readable!
- Make sure any new features you add are _thoroughly tested_ before submitting pull requests. "Thoroughly tested" means there should be human-readable, understandable unit and acceptance tests for the new functionality, and that every test in the entire project passes despite the changes you've made. I'd suggest you have a separate shell session running at all times with `lein midje :autotest` checking your changes constantly.
- Avoid duplicating code
- Please **do not** add features involved in Genetic Programming, such as representations of "genomes" or "search operators", unless you intend Push programs _themselves_ to manipulate those objects
- If a function or other definition is long, consider how to make it shorter _and more readable_. If a function or definition does more than one thing, extract those things into separate functions that each do _one_ thing.