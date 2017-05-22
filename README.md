# Klapaucius

> "Certainly not! I didn't build a machine to solve ridiculous crossword puzzles! That's hack work, not Great Art! Just give it a topic, any topic, as difficult as you like..."
>
> Klapaucius thought, and thought some more. Finally he nodded and said:
"Very well. Let's have a love poem, lyrical, pastoral, and expressed in the language of pure mathematics. Tensor algebra mainly, with a little topology and higher calculus, if need be. But with feeling, you understand, and in the cybernetic spirit."
>
> "Love and tensor algebra?" Have you taken leave of your senses?" Trurl began, but stopped, for his electronic bard was already declaiming....

(From [Lem's _The Cyberiad_](https://en.wikipedia.org/wiki/The_Cyberiad). [Non-Polish speakers may appreciate this recording of correct pronunciation of the character's name](./klapaucius.m4a), kindly provided by [Krzysztof Krawiec](http://www.cs.put.poznan.pl/kkrawiec/) and colleagues.)

## Project website

Docs are getting finished up now at [the project website](http://vaguery.github.io/klapaucius).

## About

This library includes a clean, fully tested, extensible and maintainable Push language interpreter. Push is a simple and robust programming language designed to be _evolved_ rather than hand-composed by human programmers, which originated in the [Hampshire College Computational Intelligence Lab](http://sites.hampshire.edu/ci-lab/). You may have run across it by way of [Lee Spector's Clojush](https://github.com/lspector/Clojush) project.

This is however _only a Push interpreter_. It does not "do genetic programming"; you still have to do that part yourself. But it does give you stable, extensible access to a _very large_ vocabulary of Push types and instructions.

## Requirements

The project is written in Clojure 1.8, and depends heavily on [Midje](https://github.com/marick/Midje/) for testing.

## Using the library

### Project status

Initial feature implementation is almost done. The interpreter handles 100% of the Clojush Push dialect, plus nearly a dozen additional types and hundreds of additional instructions.

Versioning is currently arbitrary and very low-valued, but _will become_ semantic after the initial features set is done. At the moment, basic functionality and usability are still my main concern, and I am adding types and large-scale features that are almost always "breaking" with every incremental release. As a result, the version will remain `0.1.X` for the near future, with `SNAPSHOT` releases capturing bug fixes, refactorings, documentation updates and general prep for "real" initial release.

Thus: `klapaucius "0.1.25"` includes a fully working interpreter, but is undergoing rapid expansion. While the current version is rigorously tested,  **substantial deep architectural changes** will be made leading up the 0.2 release. If you're going to work on it, please contact me during this great leap forward, and submit pull requests for small amounts of work in numerous git branches!

## Support

This project is open source and extremely complex, and I would like to be able to expand and support it. Please consider [donating via PayPal.me](https://www.paypal.me/BillTozier), or [supporting my work via Patreon](https://www.patreon.com/vaguery) if you find it useful or interesting!

### Project dependencies

Using `leiningen`, add the following dependency to your `project.clj`

```clojure
(defproject my-new-project "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [klapaucius "0.1.25"]
                 ;; ... your other dependencies here ...
                 ]
  :profiles {:dev
              {:dependencies [[midje "1.8.3"]]}})
                            ;; ^^^^^ you should run the tests
```

## Usage

```clojure
(ns my.fancy.namespace
  (:require [push.core :as push]
            [push.interpreter.core :as interpreter])

;; ...

(def runner
  (push/interpreter
    :bindings {:speed 88.2 :burden 2 :african? false}))

(def my-push-program [1 :burden :scalar-add])

(def final-scalar-stack
  (push/get-stack
    (push/run runner my-push-program 1000)
    :scalar))
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
  #_=>   [1 :burden :scalar-add]
  #_=>   1000)
#push.interpreter.core.Interpreter{:program [1 :burden :scalar-add], :types ({:name :numeric-scaling, :attributes #{:numeric}, :instructions {:scalar-few #push.instructions.core.Instruction{:token :scalar-few, :docstring "`:scalar-few` pops the top `:scalar` value, and calculates `(mod 10 x)`.", :tags #{:numeric}, :needs {:scalar 1}, :products {:scalar 1}...

;; (push/run INTERPRETER) returns the ENTIRE interpreter state after running the program, including all the instruction definitions, stack contents, logs and more!


;; better to capture the state of the interpreter in a `var`
user=> (def ran-it (push/run
  #_=>   runner
  #_=>   [1 :burden :scalar-add]
  #_=>   1000))
#'user/ran-it


user=> (push/get-stack ran-it :scalar)
(3)


;; push/run requires 3 arguments: an interpreter, a program, and a step limit
;; but it permits an optional :bindings argument (a hashmap)
;; plus several other optional arguments (see docs)
user=> (push/get-stack (push/run runner [1 :burden :scalar-add] 300 :bindings {:burden 87}) :scalar)
(88)


user=> (push/known-instructions runner)
(:strings-cutflip :scalars-yankdup :scalar-max :line-circle-miss?  :string-cutstack :print-space :scalar-multiply :strings-shatter :scalars-contains? :char-lowercase? :booleans-rotate :string-butlast :code-return-pop :string-min :strings-stackdepth :set-return :scalars-print :string-occurrencesofchar :push-bindingset :scalar-sign :circle-yank :char-max :exec-do*count :string-stackdepth :booleans-last :circle-swap :scalars-set :scalars-byexample :vector-replace :code-flipstack :exec-pop :boolean-dup :scalars-take :line-print :scalar-mod :set-flipstack :scalars-replacefirst :string>? :environment-stackdepth :vector-return-pop :set-pop :string->scalar :strings-equal?
;;... a HUGE list of known instructions will follow
)


user=> (push/binding-names runner)
(:speed :burden :african?)


user=> (:bindings runner)
{:speed '(8.1), :burden '(2), :african? '(false)}


user=> (:program ran-it)
[1 :burden :scalar-add]


user=> (:stacks ran-it)
{:booleans (), :scalars (), :unknown (), :exec (), :return (), :strings (), :circle (), :string (), :vector (), :print (), :scalar (3), :chars (), :line (), :code (), :point (), :error (), :environment (), :set (), :log ({:step 4, :item :scalar-add} {:step 3, :item 2} {:step 2, :item :burden} {:step 1, :item 1}), :boolean (), :char ()}

;; NOTICE THE :log STACK ^^^


;; also not we saved the interpreter after "1000 steps" in 'ran-it but:
user=> (:counter ran-it)
4
```


## How to contribute

- If you are interested in adding new types or instruction sets for general-purpose or domain-specific projects, poke me and I will make sure the guide for adding those is up to date. In general, you should be able to look over the definitions in any of the type or module definitions in `push.types`.
- If you notice bugs, please open an Issue
- Feel free to point out where the code or documentation could be made more readable!
- Make sure any new features you add are _thoroughly tested_ before submitting pull requests. "Thoroughly tested" means there should be human-readable, understandable unit and acceptance tests for the new functionality, and that every test in the entire project passes despite the changes you've made. I'd suggest you have a separate shell session running at all times with `lein midje :autotest` checking your changes constantly.
- Avoid duplicating code
- Please **do not** add features involved in Genetic Programming, such as representations of "genomes" or "search operators", unless you intend Push programs _themselves_ to manipulate those objects
- If a function or other definition is long, consider how to make it shorter _and more readable_. If a function or definition does more than one thing, extract those things into separate functions that each do _one_ thing.
