(ns push.types.base.ref
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )

; - `:push-flushrefs` (drops all :ref bindings)
; - `:ref-forget` (takes :ref, eliminates that binding)
; - `:ref-lookup` (takes :ref, pushes that value)

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


(def ref-dump
  (core/build-instruction
    ref-dump
    "`:ref-dump` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:exec` stack as a single block"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/push-onto :exec :newblock)))


(def ref-fullquote
  (core/build-instruction
    ref-fullquote
    "`:ref-fullquote` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:code` stack as a single block"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/push-onto :code :newblock)))


(def quote-refs
  (core/build-instruction
    push-quoterefs
    "`:push-quoterefs` toggles the interpreter state so that all known binding keywords are pushed automatically to the :ref stack without being resolved"
    :tags #{:binding}
    (d/quote-all-bindings)))


(def unquote-refs
  (core/build-instruction
    push-unquoterefs
    "`:push-unquoterefs` toggles the interpreter state so that all known binding keywords are resolved immediately, not pushed to the :ref stack"
    :tags #{:binding}
    (d/quote-no-bindings)))


(def ref-type
  ( ->  (t/make-type    :ref
                        :recognizer keyword?
                        :attributes #{:base})


        (t/attach-instruction quote-refs)
        (t/attach-instruction unquote-refs)
        (t/attach-instruction ref-dump)
        (t/attach-instruction ref-fullquote)
        (t/attach-instruction ref-new)

        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-visible
        ))

