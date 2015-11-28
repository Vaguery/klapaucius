(ns push.instructions.extra.introspection
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.util.code-wrangling :as u])
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


(def push-inputset
  (core/build-instruction
    push-inputset
    "`:push-inputset` pushes a set containing all the registered :input keywords to the :set stack"
    :tags #{:set :introspection}
    (d/save-inputs :as :known)
    (d/push-onto :set :known)))


(def push-inputs
  (core/build-instruction
    push-inputs
    "`:push-inputs` pushes a list (in sorted order) containing all the registered :input keywords to the :code stack"
    :tags #{:introspection}
    (d/save-inputs :as :known)
    (d/calculate [:known] #(into '() (reverse (sort %1))) :as :listed)
    (d/push-onto :code :listed)))


;;;;;;;;;;;;;;;;;


(def standard-introspection-module
  ( ->  (t/make-module  :introspection
                        :attributes #{:introspection})

        (t/attach-instruction , push-counter)
        (t/attach-instruction , push-inputs)
        (t/attach-instruction , push-inputset)
        (t/attach-instruction , push-instructionset)
        ))
