(ns push.types.base.ref
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )


; Today I'm adding what I suppose is the _successor_ to the `:name` functionality from Push 3, based on the work we did in Nudge and Duck interpreters in the meantime.

; There's a lot more introspection possible (based on the old Duck experiments several years back), and the responsibility for managing bindings is a bit better organized than what I understood from the Push 3 language specifications.

; First, these will be called `:ref` items, and they include `:input` and runtime-generated "local" bindings of a Clojure keyword to a Push item of arbitrary complexity.

; At the moment (before adding the `:ref` type), when  the interpreter encounters a Clojure keyword on the `:exec` stack, the order of routing is:

; 1. is it a registered `instruction`? If so, do that thing.
; 1. is it a registered `input`? If so, push the bound item to the `:exec` stack.
; 1. send it to `:unknown`

; After adding the `:ref` type, the order of routing will become:

; 1. `instruction`? do the thing
; 1. is it a bound `input` or `ref`?
;   - is the interpreter in "quote mode"? if so, push the keyword itself to the `:ref` stack
;   - the interpreter is not in "quote mode", so look up the bound value and push that to `:exec`
; 1. the keyword is not found in the registered bindings, so send it to `:ref`

; Notes:

; - The interpreter's "quote mode" is persistent, not a quick one-off toggle. An instruction explicitly handles that switch. Note that it does _not_ affect the interpretation of keywords _as instructions_, just as `input` or `ref` bindings
; - There can be circular references (which I learned to my surprise in some Duck interpreter experiments). Oh well, another thing not to do, evolution.
; - when the interpreter is in  "quote mode", `input` values are not looked up

; A quick (and preliminary) sketch of the instruction set associated with this functionality:

; - `:push-quoterefs` (flag to turn on :ref quoting)
; - `:push-unquoterefs` (flag to turn off :ref quoting)
; - `:push-allrefs` (return ordered list of all bound :ref keys)
; - `:push-refset` (return :set of bound :ref keys)
; - `:push-flushrefs` (drops all :ref bindings)
; - `:ref-forget` (takes :ref, eliminates that binding)
; - `:ref-lookup` (takes :ref, pushes that value)

; Also, there will be a `storable` module which adds instructions for items that can be stored in `:ref` bindings (basically everything):

; - `:x-bind` (pop top :ref, store item in that variable name)
; - `:x-store` (create new :ref, store item in that)
; - `:x-bound` (return `:set` of :`ref` bindings that hold items of this type only)
; - `:x-bound?` pops `:x` stack, checks to see if that exact value is held in any of the current bindings
; - `:x-reverselookup` pops top of `:x` stack, checks the current bindings (including `inputs`) and returns the `:ref` key if a match is found


(def ref-new
  (core/build-instruction
    ref-new
    "`:ref-new` creates a new (randomly-named) `:ref` keyword and pushes it to that stack"
    :tags #{:ref }
    (d/calculate [] #(keyword (gensym "ref!")) :as :newref)
    (d/push-onto :ref :newref)))



(def ref-type
  ( ->  (t/make-type    :ref
                        :recognizer keyword?
                        :attributes #{:internal :base})

        (t/attach-instruction ref-new)

        aspects/make-equatable
        aspects/make-movable
        aspects/make-quotable
        aspects/make-visible

        ))

