(ns push.push-dsl-test
  (:use midje.sweet)
  (:use [push.interpreter]))


(comment
"
The Push DSL is a highly-constrained domain-specific language for defining
Push instructions.

An Instruction operates on an Interpreter by sending it (thread-first) through
the steps of the DSL script, producing a changed Interpreter at each step, and
(optionally) recording and manipulating ephemeral scratch variables as it does
so. These scratch variables are always local to the DSL interpreter environment,
and are discarded as soon as the Instruction code ends.

DSL instructions include:
- `count-of [stackname :as local]`
"
)