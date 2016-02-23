(ns push.instructions.extra.introspection
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as u])
  )


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


(def push-bindingset
  (core/build-instruction
    push-bindingset
    "`:push-bindingset` pushes a set containing all the registered :bindings keywords to the :set stack"
    :tags #{:set :introspection}
    (d/save-bindings :as :known)
    (d/push-onto :set :known)))


(def push-bindings
  (core/build-instruction
    push-bindings
    "`:push-bindings` pushes a list (in sorted order) containing all the registered :bindings keywords to the :code stack"
    :tags #{:introspection}
    (d/save-bindings :as :known)
    (d/calculate [:known] #(into '() (reverse (sort %1))) :as :listed)
    (d/push-onto :code :listed)))


;;;;;;;;;;;;;;;;;


(def standard-introspection-module
  ( ->  (t/make-module  :introspection
                        :attributes #{:introspection})

        (t/attach-instruction , push-counter)
        (t/attach-instruction , push-bindings)
        (t/attach-instruction , push-bindingset)
        (t/attach-instruction , push-instructionset)
        ))
