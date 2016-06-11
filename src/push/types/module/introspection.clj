(ns push.types.module.introspection
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as u]
            ))



(def push-bindingcount
  (core/build-instruction
    push-bindingcount
    "`:push-bindingcount` pushes the number of registered bindings to `:integer`"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(count (keys %)) :as :count)
    (d/push-onto :integer :count)))



(def push-bindings
  (core/build-instruction
    push-bindings
    "`:push-bindings` pushes a list (in sorted order) containing all the registered :bindings keywords to the :code stack"
    :tags #{:binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(sort %1) :as :listed)
    (d/push-onto :code :listed)))



(def push-bindingset
  (core/build-instruction
    push-bindingset
    "`:push-bindingset` pushes a set containing all the registered :bindings keywords to the :set stack"
    :tags #{:set :binding :introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(into #{} %1) :as :bindingset)
    (d/push-onto :set :bindingset)))



(def push-counter
  (core/build-instruction
    push-counter
    "`:push-counter` pushes the current interpreter counter value to the `:integer` stack"
    :tags #{:introspection}
    (d/save-counter :as :count)
    (d/push-onto :integer :count)))



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
    "`:push-nthref` takes an `:integer`, maps that value onto the number of `:bindings` it knows, and pushes the indexed key to `:ref` (after sorting the list of keywords)"
    :tags #{:binding :introspection}
    (d/consume-top-of :integer :as :i)
    (d/save-bindings :as :known) ;; just the keys
    (d/calculate [:known :i] #(u/safe-mod %2 (count %1)) :as :idx)
    (d/calculate [:known :idx] #(if (empty? %1) nil (nth (sort %1) %2)) :as :result)
    (d/push-onto :ref :result)
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
        ))
