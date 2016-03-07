(ns push.types.extra.generator
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]))


(defrecord Generator [state step-function origin])


(defn make-generator
  "Takes an initial state and a generator function that transforms the state into its next value. Stores the initial state in the :origin field so it can reset and detect loops. The function should have arity 1 and return a new state value."
  ([state step-function origin]
    (->Generator state step-function origin))
  ([state step-function]
    (->Generator state step-function state)))


(defn generator?
  "Returns `true` if the item is a `:generator`, and `false` otherwise."
  [item]
  (= (type item) push.types.extra.generator.Generator))


(defn step-generator
  "Takes a generator, and applies its step-function to its state. Returns a list containing the new result, and the updated generator."
  [g]
  (let [gen       (:step-function g) 
        new-value (apply gen (list (:state g)))]
    (make-generator new-value gen (:origin g))))


;; instructions
;; - generator-range
;; - generator-reset
;; etc


(def generator-counter
  (core/build-instruction
    generator-counter
    "`:generator-counter` pops an `:integer` and uses that to create a `:generator` that will count by 1 from the starting point."
    :tags #{:generator}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(make-generator %1 inc) :as :g)
    (d/push-onto :generator :g)))



(def generator-next
  (core/build-instruction
    generator-next
    "`:generator-next` pops the top `:generator` and calls calls its `step` function to create a new item and update its state. The result is a code block containing the result and the updated generator, which is pushed to the `:exec` stack."
    :tags #{:generator}
    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg]
      #(let [n (step-generator %1)] (list (:state n) n)) :as :results)
    (d/push-onto :exec :results)))



(def generator-reset
  (core/build-instruction
    generator-reset
    "`:generator-reset` pops the top `:generator` and resets its state to its :origin, then returns it to the `:generator` stack."
    :tags #{:generator}
    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg] #(make-generator (:origin %1) (:step-function %1)) :as :result)
    (d/push-onto :generator :result)))



(def generator-stepper
  (core/build-instruction
    generator-stepper
    "`:generator-stepper` pops 2 `:integer` values (A and B) and uses them to create a `:generator` that will start from B and change by A (positive or negative) at every step."
    :tags #{:generator}
    (d/consume-top-of :integer :as :arg1)
    (d/consume-top-of :integer :as :arg2)
    (d/calculate [:arg1 :arg2] #(make-generator %2 (partial + %1)) :as :g)
    (d/push-onto :generator :g)))



(def generator-type
  "builds the `:generator` type, which can emit items when asked"
  (let [typename :generator]
  (-> (t/make-type  :generator
                    :recognizer generator?
                    :attributes #{:generator})
      (t/attach-instruction , generator-counter)
      (t/attach-instruction , generator-next)
      (t/attach-instruction , generator-reset)
      (t/attach-instruction , generator-stepper)
      aspects/make-visible 
      aspects/make-movable
      aspects/make-quotable
      aspects/make-returnable
      aspects/make-storable
      )))

