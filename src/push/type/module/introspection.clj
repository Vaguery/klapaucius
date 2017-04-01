(ns push.type.module.introspection
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.util.numerics :as num]
            [push.util.code-wrangling :as u]
            ))



(def push-bindingcount
  (core/build-instruction
    push-bindingcount
    "`:push-bindingcount` pushes the number of registered bindings to `:scalar`"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(count (keys %)) :as :count)
    (d/push-onto :scalar :count)))



(def push-bindings
  (core/build-instruction
    push-bindings
    "`:push-bindings` pushes a list (in sorted order) containing all the registered :bindings keywords to the :code stack"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(u/list! (sort %1)) :as :listed)
    (d/push-onto :code :listed)))



(def push-bindingset
  (core/build-instruction
    push-bindingset
    "`:push-bindingset` pushes a set containing all the registered :bindings keywords to the :set stack"
    :tags #{:set :binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] set :as :bindingset)
    (d/push-onto :set :bindingset)))



(def push-counter
  (core/build-instruction
    push-counter
    "`:push-counter` pushes the current interpreter counter value to the `:scalar` stack"
    :tags #{:introspection}
    (d/save-counter :as :count)
    (d/push-onto :scalar :count)))



(def push-instructionset
  (core/build-instruction
    push-instructionset
    "`:push-instructionset` pushes a set containing all the registered :instruction keywords to the :set stack"
    :tags #{:set :introspection}
    (d/save-instructions :as :dictionary)
    (d/push-onto :set :dictionary)))



(def push-nthref
  (core/build-instruction
    push-nthref
    "`:push-nthref` takes an `:scalar`, maps that value onto the number of `:bindings` it knows, and pushes the indexed key to `:ref` (after sorting the list of keywords)"
    :tags #{:binding :introspection}
    (d/consume-top-of :scalar :as :i)
    (d/save-bindings :as :known) ;; just the keys
    (d/calculate [:known :i]
        #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:known :idx] #(when (seq %1) (nth (sort %1) %2)) :as :result)
    (d/push-onto :ref :result)
    ))



(def push-refcycler
  (core/build-instruction
    push-refcycler
    "`:push-refcycler` pushes a code block to `:exec` that contains `(:push-bindings :code-cycler)`"
    :tags #{:binding :generator :introspection}
    (d/calculate [] #(list :push-bindings :code-cycler) :as :result)
    (d/push-onto :exec :result)))

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
