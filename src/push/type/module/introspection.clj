(ns push.type.module.introspection
  (:require [push.instructions.dsl    :as d]
            [push.instructions.core   :as i]
            [push.util.numerics       :as n]
            [push.type.core           :as t]
            [push.util.code-wrangling :as u]
            [push.type.definitions.quoted :as qc]
            ))



(def push-bindingcount
  (i/build-instruction
    push-bindingcount
    "`:push-bindingcount` pushes the number of registered bindings to `:scalar`"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(count (keys %)) :as :count)
    (d/return-item :count)
    ))



(def push-bindings
  (i/build-instruction
    push-bindings
    "`:push-bindings` returns a quoted list (in sorted order) containing all the registered :bindings keywords"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(qc/push-quote (u/list! (sort %1))) :as :listed)
    (d/return-item :listed)
    ))



(def push-bindingset
  (i/build-instruction
    push-bindingset
    "`:push-bindingset` pushes a set containing all the registered :bindings keywords to the :set stack"
    :tags #{:set :binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] set :as :bindingset)
    (d/return-item :bindingset)
    ))



(def push-counter
  (i/build-instruction
    push-counter
    "`:push-counter` pushes the current interpreter counter value to the `:scalar` stack"
    :tags #{:introspection}
    (d/save-counter :as :count)
    (d/return-item :count)
    ))



(def push-instructionset
  (i/build-instruction
    push-instructionset
    "`:push-instructionset` pushes a set containing all the registered :instruction keywords to the :set stack"
    :tags #{:set :introspection}
    (d/save-instructions :as :dictionary)
    (d/return-item :dictionary)
    ))



(def push-nthref
  (i/build-instruction
    push-nthref
    "`:push-nthref` takes an `:scalar`, maps that value onto the number of `:bindings` it knows, and pushes the indexed key to `:ref` (after sorting the list of keywords)"
    :tags #{:binding :introspection}
    (d/consume-top-of :scalar :as :i)
    (d/save-bindings :as :known) ;; just the keys
    (d/calculate [:known :i]
        #(if (empty? %1) 0 (n/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:known :idx] #(when (seq %1) (nth (sort %1) %2)) :as :result)
    (d/return-item :result)
    ))



(def push-refcycler
  (i/build-instruction
    push-refcycler
    "`:push-refcycler` pushes a code block to `:exec` that contains `(:push-bindings :code-cycler)`"
    :tags #{:binding :generator :introspection}
    (d/calculate [] #(list :push-bindings :code-cycler) :as :result)
    (d/return-item :result)
    ))

;;;;;;;;;;;;;;;;;


(def standard-introspection-module
  ( ->  (t/make-module  :introspection
                        :attributes #{:introspection})

        (t/attach-instruction , push-bindingcount)
        (t/attach-instruction , push-bindings)
        (t/attach-instruction , push-bindingset)
        (t/attach-instruction , push-counter)
        (t/attach-instruction , push-instructionset)
        (t/attach-instruction , push-nthref)
        (t/attach-instruction , push-refcycler)
        ))
