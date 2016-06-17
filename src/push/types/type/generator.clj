(ns push.types.type.generator
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.util.exotics :as exotics]
            ))


;; SUPPORT


(defrecord Generator [state step-function origin])


(defn make-generator
  "Takes an initial state and a generator function that transforms the state into its next value. Stores the initial state in the :origin field so it can reset and detect loops. The function should have arity 1 and return a new state value. If the state turns out to be nil, no record is built and nil is returned instead."
  ([state step-function origin]
    (if (nil? state)
      nil
      (->Generator state step-function origin)))
  ([state step-function]
    (make-generator state step-function state)))



(defn generator?
  "Returns `true` if the item is a `:generator`, and `false` otherwise."
  [item]
  (= (type item) push.types.type.generator.Generator))



(defn step-generator
  "Takes a generator, and applies its step-function to its state. Returns a list containing the new result, and the updated generator."
  [g]
  (let [gen       (:step-function g) 
        new-value (apply gen (list (:state g)))]
    (if (nil? new-value) nil (make-generator new-value gen (:origin g)))))



;; INSTRUCTIONS


(def generator-again
  (core/build-instruction
    generator-again
    "`:generator-again` pops the top `:generator` and produces a code block containing the current `:state` and the unchanged generator, which is pushed to the `:exec` stack. If the number of points exceeds the "
    :tags #{:generator}
    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg] #(list (:state %) %) :as :results)
    (d/push-onto :exec :results)))



(def generator-counter
  (core/build-instruction
    generator-counter
    "`:generator-counter` pops a `:scalar` and uses that to create a `:generator` that will count by 1 from the starting point."
    :tags #{:generator}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(make-generator %1 inc') :as :g)
    (d/push-onto :generator :g)))



(def generator-jumpsome
  (core/build-instruction
    generator-jumpsome
    "`:generator-jumpsome` pops the top `:generator` and the top `:scalar`, and calls the `:generator`'s  `step` function as many (integer) times as the rounded `:scalar` indicates. The number of steps is calculated modulo 100. The advanced `:generator` is pushed to the `:generator` stack. If the `:scalar` is negative or zero, there is no change to the state. If the `:generator` is exhausted in the process, it is discarded."
    :tags #{:generator}
    (d/consume-top-of :generator :as :arg)
    (d/consume-top-of :scalar :as :steps)
    (d/calculate [:arg :steps]
      #(first 
        (drop (mod (bigint %2) 100)
          (iterate (fn [g] (if (nil? g) nil (step-generator g))) %1))) :as :result)
    (d/push-onto :generator :result)))



(def generator-next
  (core/build-instruction
    generator-next
    "`:generator-next` pops the top `:generator` and calls calls its `step` function to create a new item and update its state. The result is a code block containing the result and the updated generator, which is pushed to the `:exec` stack."
    :tags #{:generator}
    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg]
      #(let [n (step-generator %1)]
        (if (nil? n) nil (list (:state n) n))) :as :results)
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
    "`:generator-stepper` pops 2 `:scalar` values (A and B) and uses them to create a `:generator` that will start from B and change by A (positive or negative) at every step."
    :tags #{:generator}
    (d/consume-top-of :scalar :as :arg1)
    (d/consume-top-of :scalar :as :arg2)
    (d/calculate [:arg1 :arg2] #(make-generator %2 (partial +' %1)) :as :g)
    (d/push-onto :generator :g)))



(def generator-totalistic3
  (core/build-instruction
    generator-totalistic3
    "`:generator-totalistic3` pops a `:scalar` and uses that to create a `:generator` that will cycle through a digitwise totalistic rewrite rule of width 3. The scalar is converted to a `bigint` when consumed."
    :tags #{:generator}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(make-generator (bigint %1) (fn [x] (exotics/rewrite-digits x 3))) :as :g)
    (d/push-onto :generator :g)))



(def generator-type
  "builds the `:generator` type, which can emit items when asked"
  (let [typename :generator]
  (-> (t/make-type  :generator
                    :recognized-by generator?
                    :attributes #{:generator})
      (t/attach-instruction , generator-again)
      (t/attach-instruction , generator-counter)
      (t/attach-instruction , generator-jumpsome)
      (t/attach-instruction , generator-next)
      (t/attach-instruction , generator-reset)
      (t/attach-instruction , generator-stepper)
      (t/attach-instruction , generator-totalistic3)
      aspects/make-visible 
      aspects/make-movable
      aspects/make-quotable
      aspects/make-repeatable
      aspects/make-returnable
      aspects/make-storable
      aspects/make-taggable
      )))

