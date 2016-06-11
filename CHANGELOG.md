# Recent changes

## 0.1.16, 0.1.15, 0.1.14

- interpreter routing now uses `PushRouter` records, which are stored in each type definition
- when invoking `make-type`, an optional `:router` keyword argument can be assigned a `PushRouter` instance, which in turn can define a `:preprocessor` (which can transform the item before it is pushed), and a `:target-stack`, which names its destination stack; default `:preprocessor` is `identity`, and default `:target-stack` is the type name
- for backwards-compatibility, `make-type` still accepts a `:recognizer` argument; if a `:recognizer` is given (but no `:router`) then a `router` with the default values is constructed implicitly
- various introspection instructions, mainly focused on `:ref` and `:binding` manipulation


## 0.1.13

- changed behavior of `:x-yank`, `:x-yankdup` and `:x-shove` instructions to work like Clojush's; that is, rather than using modulo indices, it uses constrained ranges (negative index -> 0, overlarge index -> max item)

## 0.1.12, 0.1.11

- various bugfixes and refactoring

## 0.1.10

- added `:tagspace` type
- added `:generator` type
- restructured `push.types` tree
- various small changes to instruction sets
- removed `plane_geometry` references from main codebase
- cleaned up most type and module definition files

- rewrite `require` blocks for ClojureScript compatibility

## 0.1.9

- `:x-againlater` and `:x-later` instructions added to `:movable` aspect
- `:ref-lookup`, `:ref-dump`, `:ref-fullquote`, `:push-quoterefs` and `:push-unquoterefs` instructions for working with `:ref` bindings
- several cleanups of midje tests
- several DSL instructions added in support of new instructions

## 0.1.8

- debugging bump; some obsolete documentation updated

## 0.1.7

- `inputs` renamed `bindings` throughout
- each `binding` is now a _stack_ rather than a simple `var` now; assignment pushes new values onto the stack without deleting earlier values

## 0.1.6

- various numeric functions

## 0.1.5

- `core.clj` exposes basic `interpreter` creation functions for use as a library

