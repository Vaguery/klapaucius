# Push in Clojure

This is a clean, fully tested, extensible and maintainable reimplementation of the Push interpreter, central to [Lee Spector's Clojush](https://github.com/lspector/Clojush) project. The intention in starting from scratch is to fully document the actual behavior of Push, separate concerns that are confounded in the original project, and to better serve the user base seeking to reuse and extend the language for their domain-specific projects.

The project uses [Midje](https://github.com/marick/Midje/) and Clojure 1.7.

## Plan

1. first iteration
  - implement an `Interpreter` as a Clojure record
  - implement `make-interpreter` constructor with core types
  - "router" structure for handling literals
  - `step` and `run` functions
  - new "register instruction" function(s)
  - basic `:integer` instructions
  - basic `:boolean` instructions
  - handle `:input` values
  - step counting
2. complete core instruction coverage (`:float`, `:code`, `:exec`, `:string`, `:char` etc)
3. tracing and reporting
3. performance restrictions
  - halting problem
  - numeric overflow and underflow
  - memory management
  - error handling
  - large value handling
3. extensibility for "new" types via "experiment" interface:
  - standard library: vectors, printing, etc
  - adding a type or instruction
4. interface(s) for experiments:
  - random code instructions
  - `:genome` "types", etc


## How to run the tests

`lein midje` will run all tests.

`lein midje namespace.*` will run only tests beginning with "namespace.".

`lein midje :autotest` will run all the tests indefinitely. It sets up a
watcher on the code files. If they change, only the relevant tests will be
run again.

## How to contribute

- Make sure any new features you add are _thoroughly tested_ before submitting pull requests. "Thoroughly tested" means there should be human-readable, understandable unit and acceptance tests for all functionality, and that every test in the entire project passes despite the changes you've made. I'd suggest you have a separate shell session running at all times with `lein midje :autotest` checking your changes constantly.
- Do not add features that aren't present in the original Clojush project
- Avoid duplicating code
- Do not add features involved in Genetic Programming, such as representations of "genomes" or "search operators"
- Hesitate and discuss any changes you're tempted to make to the public methods this library exposes: it is intended to be a self-contained interpreter _only_. If you're not writing private methods, be ready to explain why.
- If a function or other definition is long, consider how to make it shorter _and more readable_.