(ns push.types.type.ref
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))



(def quote-refs
  (core/build-instruction
    push-quoterefs
    "`:push-quoterefs` toggles the interpreter state so that all known binding keywords are pushed automatically to the :ref stack without being resolved"
    :tags #{:binding}
    (d/quote-all-bindings)))



(def ref-clear
  (core/build-instruction
    ref-clear
    "`:ref-clear` pops the top `:ref` keyword and clears all items currently bound to that keyword in the Interpreter's binding table. The variable remains recognized, it simply has no bound values."
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/clear-binding :arg)))



(def ref-dump
  (core/build-instruction
    ref-dump
    "`:ref-dump` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:exec` stack as a single block"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/push-onto :exec :newblock)))



(def ref-exchange
  (core/build-instruction
    ref-exchange
    "`:ref-exchange` pops the top two `:ref` keywords; if they are references to the same binding, there is no effect; if either or both is an undefined `:ref`, it has an empty value stack created as needed"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg1)
    (d/consume-top-of :ref :as :arg2)
    (d/save-binding-stack :arg1 :as :values1)
    (d/save-binding-stack :arg2 :as :values2)
    (d/replace-binding :values1 :into :arg2)
    (d/replace-binding :values2 :into :arg1)
    ))



(def ref-forget
  (core/build-instruction
    ref-forget
    "`:ref-forget` pops the top `:ref` keyword and clears the entire binding currently associated with it, key and all. NOTE: this is permitted to erase an `:input` binding."
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/forget-binding :arg)))



(def ref-fullquote
  (core/build-instruction
    ref-fullquote
    "`:ref-fullquote` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:code` stack as a single block"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/push-onto :code :newblock)))



(def ref-known?
  (core/build-instruction
    ref-known?
    "`:ref-known?` pops the top `:ref` keyword and `true` if it is one of the defined `:binding` keys"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-bindings :as :known) ;; just the keys
    (d/calculate [:known :arg] #(boolean (some #{%2} %1)) :as :result)
    (d/push-onto :boolean :result)))



(def ref-lookup
  (core/build-instruction
    ref-lookup
    "`:ref-lookup` pops the top `:ref` keyword and pushes a copy of the top item on its stack onto the `:exec` stack"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-top-of-binding :arg :as :value)
    (d/push-onto :exec :value)))



(def ref-new
  (core/build-instruction
    ref-new
    "`:ref-new` creates a new (randomly-named) `:ref` keyword and pushes it to that stack"
    :tags #{:binding}
    (d/calculate [] #(keyword (gensym "ref!")) :as :newref)
    (d/push-onto :ref :newref)))



(def unquote-refs
  (core/build-instruction
    push-unquoterefs
    "`:push-unquoterefs` toggles the interpreter state so that all known binding keywords are resolved immediately, not pushed to the :ref stack"
    :tags #{:binding}
    (d/quote-no-bindings)))



(def ref-type
  ( ->  (t/make-type    :ref
                        :recognized-by keyword?
                        :attributes #{:base})

        (t/attach-instruction quote-refs)
        (t/attach-instruction unquote-refs)
        (t/attach-instruction ref-clear)
        (t/attach-instruction ref-dump)
        (t/attach-instruction ref-exchange)
        (t/attach-instruction ref-forget)
        (t/attach-instruction ref-fullquote)
        (t/attach-instruction ref-known?)
        (t/attach-instruction ref-lookup)
        (t/attach-instruction ref-new)

        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        ))

