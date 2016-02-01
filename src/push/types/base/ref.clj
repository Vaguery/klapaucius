(ns push.types.base.ref
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )

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
    :tags #{:binding}
    (d/calculate [] #(keyword (gensym "ref!")) :as :newref)
    (d/push-onto :ref :newref)))



(def ref-type
  ( ->  (t/make-type    :ref
                        :recognizer keyword?
                        :attributes #{:base})

        (t/attach-instruction ref-new)

        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        aspects/make-visible

        ))

