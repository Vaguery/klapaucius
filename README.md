# Push in Clojure

This is a clean, (almost) fully tested, extensible and maintainable reimplementation of the Push interpreter, central to [Lee Spector's Clojush](https://github.com/lspector/Clojush) project. The intention in starting from scratch is to fully document the actual behavior of Push, to separate concerns that are confounded in the original project, and to better serve the user base seeking to reuse and extend the language for their domain-specific projects.

The project depends on [Midje](https://github.com/marick/Midje/) for testing, and requires Clojure 1.7.

## What It's For?

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

## Usage

**TBD**

The codebase is still shambling towards a minimal viable state. In the meantime, take a look at the tests, and (if you want to heat up your CPU) have a look and try running the acceptance tests, which create and run many thousands of random Push programs.

As it moves through successive stages of "done", you'll be able to

- load it as a dependency in your project, send it Push programs to run, and interrogate their states when they're done
- run it from the command line
- launch a worker daemon that accepts API requests of programs to run, and which returns specified details about the final state and performance
- ?

## Examples

**TBD**

## Plan

1. first iteration [done]
  - implement an `Interpreter` as a Clojure record
  - implement `make-interpreter` constructor with core types
  - "router" structure for handling literals
  - `step` and `run` functions
  - new "register instruction" function(s)
  - basic `:integer` instructions
  - basic `:boolean` instructions
  - handle `:input` values
  - step counting
2. complete core instruction coverage
  - `:char`
  - `:code`
  - `:environment` and `:return`
  - `:exec`
  - `:float`
  - `:print`
  - `:string`
3. tracing and reporting
  - `:log` stack
  - `:error` stack
  - `produce-gazetteer`
3. performance restrictions
  - halting problem
  - numeric overflow and underflow
  - memory management
  - error handling
  - large value handling
3. extensibility for "new" types and instructions:
  - standard library: vectors, printing, etc
  - adding a type or instruction
4. interface(s) for experiments:
  - random code instructions
  - `:genome` "types", etc


## How to run the tests

Typing `lein midje` at the command line (from inside this directory) will run all tests that aren't tagged `:slow` or `:acceptance`. The reporting level isn't very verbose.

`lein midje :autotest` will run all the (non-slow) tests indefinitely. It sets up a watcher on the code files. If they change, only the relevant tests will be
run again. Be sure to run this in its own shell, since it's not going to let you work with other processes.

`lein midje :filter acceptance` will run the acceptance tests. These tests will really push the system they're run on, and will probably peg your CPU (no matter how big it is) for most of an hour.

## How to contribute

- Wait for the first minimal release. I'm getting there.
- Issues are always welcome.
- Feel free to point out where the code or documentation could be made more readable!
- Make sure any new features you add are _thoroughly tested_ before submitting pull requests. "Thoroughly tested" means there should be human-readable, understandable unit and acceptance tests for the new functionality, and that every test in the entire project passes despite the changes you've made. I'd suggest you have a separate shell session running at all times with `lein midje :autotest` checking your changes constantly.
- Avoid duplicating code
- Don't add features involved in Genetic Programming, such as representations of "genomes" or "search operators"
- If a function or other definition is long, consider how to make it shorter _and more readable_.