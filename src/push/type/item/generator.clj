(ns push.type.item.generator
  (:require [push.instructions.dsl           :as d]
            [push.instructions.core          :as i]
            [push.type.definitions.generator :as g]
            [push.util.numerics              :as n        :refer [infinite?]]
            [clojure.math.numeric-tower      :as nt       :refer [floor]]
            [push.type.core                  :as t]
            [push.util.code-wrangling        :as u        :refer [safe-mod]]
            [push.instructions.aspects       :as aspects]
            [push.util.exotics               :as exotics]
            ))


(def generator-again
  (i/build-instruction
    generator-again
    "`:generator-again` pops the top `:generator` and produces a code block containing the current `:state` and the unchanged generator, which is pushed to the `:exec` stack. If the number of points exceeds the "

    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg] #(list (:state %1) %1) :as :results)
    (d/return-item :results)
    ))



(def generator-counter
  (i/build-instruction
    generator-counter
    "`:generator-counter` pops a `:scalar` and uses that to create a `:generator` that will count by 1 from the starting point."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(g/make-generator %1 inc') :as :g)
    (d/return-item :g)
    ))



(def generator-jumpsome
  (i/build-instruction
    generator-jumpsome
    "`:generator-jumpsome` pops the top `:generator` and the top `:scalar`, and calls the `:generator`'s  `step` function as many (integer) times as the rounded `:scalar` indicates. The number of steps is calculated modulo 100 (with a max of 99 for infinite :scalar argument). The advanced `:generator` is pushed to the `:generator` stack. If the `:scalar` is negative or zero, there is no change to the state. If the `:generator` is exhausted in the process, it is discarded."

    (d/consume-top-of :generator :as :arg)
    (d/consume-top-of :scalar :as :steps)
    (d/calculate [:arg :steps]
      #(first
        (drop
          (nt/floor (max 0 (min (u/safe-mod %2 100) 99)))
          (iterate
            (fn [g] (when-not (nil? g) (g/step-generator g)))
            %1))) :as :result)
    (d/return-item :result)
    ))



(def generator-next
  (i/build-instruction
    generator-next
    "`:generator-next` pops the top `:generator` and calls calls its `step` function to create a new item and update its state. The result is a code block containing the result and the updated generator, which is pushed to the `:exec` stack."

    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg]
      #(let [n (g/step-generator %1)]
        (when-not (nil? n) (list (:state n) n))) :as :results)
    (d/return-item :results)
    ))



(def generator-reset
  (i/build-instruction
    generator-reset
    "`:generator-reset` pops the top `:generator` and resets its state to its :origin, then returns it to the `:generator` stack."

    (d/consume-top-of :generator :as :arg)
    (d/calculate [:arg] #(g/make-generator (:origin %1) (:step-function %1)) :as :result)
    (d/return-item :result)
    ))



(def generator-stepper
  (i/build-instruction
    generator-stepper
    "`:generator-stepper` pops 2 `:scalar` values (A and B) and uses them to create a `:generator` that will start from B and change by A (positive or negative) at every step."

    (d/consume-top-of :scalar :as :arg1)
    (d/consume-top-of :scalar :as :arg2)
    (d/calculate [:arg1 :arg2] #(g/make-generator %2 (partial +' %1)) :as :g)
    (d/return-item :g)
    ))



(def generator-totalistic3
  (i/build-instruction
    generator-totalistic3
    "`:generator-totalistic3` pops a `:scalar` and uses that to create a `:generator` that will cycle through a digitwise totalistic rewrite rule of width 3. The scalar is converted to a `bigint` when consumed. If the `:scalar` is infinite, 0 is used as the starting value."

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(if (n/infinite? %1) 0 %1) :as :arg)
    (d/calculate [:arg]
      #(g/make-generator
        (bigint %1)
        (fn [x] (exotics/rewrite-digits x 3))) :as :g)
    (d/return-item :g)
    ))



(def generator-type
  "builds the `:generator` type, which can emit items when asked"
  (let [typename :generator]
  (-> (t/make-type  :generator
                    :recognized-by g/generator?
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
