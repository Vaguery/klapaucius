(ns push.type.item.ref
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.util.numerics :as num]
            ))


;; interpreter state toggles

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



(def push-storeARGS
  (core/build-instruction
    push-storeARGS
    "`:push-storeARGS` sets the `:store-args?` value of the interpreter's `:config`. While it is `true`, the interpreter will save arguments consumed by instructions into the `binding` named `:ARGS`."
    :tags #{:binding}
    (d/start-storing-arguments)))



(def push-discardARGS
  (core/build-instruction
    push-discardARGS
    "`:push-discardARGS` unsets the `:store-args?` value of the interpreter's `:config`. While it is `false`, the interpreter will not save the arguments consumed by instructions into the `:ARGS` binding."
    :tags #{:binding}
    (d/stop-storing-arguments)))




;; storage and retrieval


(def ref-ARGS
  (core/build-instruction
    ref-ARGS
    "`:ref-ARGS` (1) pushes the top item currently stored in the special `:ARGS` binding onto `:exec`, and (2) pushes the keyword `:ARGS` onto the `:ref` stack."
    (d/calculate [] (fn [] :ARGS) :as :dummy)  ;; awful syntax needs to be fixed
    (d/save-top-of-binding :dummy :as :value)
    (d/push-onto :exec :value)
    (d/push-onto :ref :dummy)
    ))



(def ref-clear
  (core/build-instruction
    ref-clear
    "`:ref-clear` pops the top `:ref` keyword and clears all items currently bound to that keyword in the Interpreter's binding table. The variable remains recognized, it simply has no bound values."
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/clear-binding :arg)))




(def ref-cyclevector
  (core/build-instruction
    ref-cyclevector
    "`:ref-cyclevector` pops the top `:ref` item and the top two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of elements from the `:ref`'s current stack are made into a single vector (by cycling) and pushed to `:exec`. If the `:ref` has no bound values, an empty vector is pushed."
    :tags #{:binding}
    (d/consume-top-of :ref :as :which)
    (d/save-binding-stack :which :as :contents)
    (d/consume-top-of :scalar :as :scale)
    (d/consume-top-of :scalar :as :raw-count)
    (d/calculate [:scale]
      #(nth [10 100 1000 10000] (num/scalar-to-index %1 4)) :as :relative)
    (d/calculate [:raw-count :relative]
      #(num/scalar-to-index %1 %2) :as :size)
    (d/calculate [:contents :size]
      #(into [] (take %2 (cycle %1))) :as :result)
    (d/push-onto :exec :result)
    ))




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



(def ref-fillvector
  (core/build-instruction
    ref-fillvector
    "`:ref-fillvector` pops the top `:ref` item and the top two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of copies of the `:ref`'s current value are made into a single vector and pushed to `:exec`. If the `:ref` has no bound values, an empty vector is pushed."
    :tags #{:binding}
    (d/consume-top-of :ref :as :which)
    (d/save-top-of-binding :which :as :item)
    (d/consume-top-of :scalar :as :scale)
    (d/consume-top-of :scalar :as :raw-count)
    (d/calculate [:scale]
      #(nth [10 100 1000 10000] (num/scalar-to-index %1 4)) :as :relative)
    (d/calculate [:raw-count :relative]
      #(num/scalar-to-index %1 %2) :as :size)
    (d/calculate [:item :size]
      #(if (nil? %1)
        []
        (into [] (take %2 (repeat %1)))) :as :result)
    (d/push-onto :exec :result)
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



(def ref-peek
  (core/build-instruction
    ref-peek
    "`:ref-peek` pops the top `:ref` keyword and pushes a copy of the top item on its stack onto the `:exec` stack; it then returns the `:ref` to that stack"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-top-of-binding :arg :as :value)
    (d/push-onto :exec :value)
    (d/push-onto :ref :arg)))



(def ref->vector
  (core/build-instruction
    ref->vector
    "`:ref->vector` pops the top `:ref` keyword and copies its entire stack of contents into a new `:vector` item, which is pushed to `:exec`"
    :tags #{:binding}
    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :contents)
    (d/calculate [:contents] #(into [] %1) :as :result)
    (d/push-onto :exec :result)
    ))






(def ref-type
  ( ->  (t/make-type    :ref
                        :recognized-by keyword?
                        :attributes #{:base})

        (t/attach-instruction quote-refs)
        (t/attach-instruction unquote-refs)
        (t/attach-instruction push-discardARGS)
        (t/attach-instruction push-storeARGS)

        (t/attach-instruction ref-ARGS)
        (t/attach-instruction ref-clear)
        (t/attach-instruction ref-cyclevector)
        (t/attach-instruction ref-dump)
        (t/attach-instruction ref-exchange)
        (t/attach-instruction ref-fillvector)
        (t/attach-instruction ref-forget)
        (t/attach-instruction ref-fullquote)
        (t/attach-instruction ref-known?)
        (t/attach-instruction ref-lookup)
        (t/attach-instruction ref-new)
        (t/attach-instruction ref-peek)
        (t/attach-instruction ref->vector)

        aspects/make-collectible
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

