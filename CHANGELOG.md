# Recent changes

## work in progress (expected in 0.1.9)

- `binding` introspection and manipulation instructions will be added shortly
- rewrite `require` blocks for ClojureScript compatibility


## 0.1.8

- debugging bump; some obsolete documentation updated

## 0.1.7

- `inputs` renamed `bindings` throughout
- each `binding` is now a _stack_ rather than a simple `var` now; assignment pushes new values onto the stack without deleting earlier values

## 0.1.6

- various numeric functions

## 0.1.5

- `core.clj` exposes basic `interpreter` creation functions for use as a library

