# Recent changes

## work in progress (expected in 0.1.11)

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

