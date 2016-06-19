(ns push.instructions.aspects.repeatable-and-cycling
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            [push.util.numerics :as n]
            [push.util.code-wrangling :as wrangling]
            ))



;; SUPPORT FOR CYCLERS


(defn splittable?
  "Returns true if the item is a collection (but not a record), or a string, and is not empty."
  [item]
  (and (cond
          (record? item) false
          (seq? item) true
          (string? item) true
          (coll? item) true
          :else false)
    (seq item)))


(defn dissect-collection
  "Takes an item that is a collection and produces a 'dissected list' composed of `(first item)` and `(rest item)`, or returns nil if that is imposeible."
  [item]
  (if (splittable? item)
    (list (first item) (rest item))
    nil))


(defn dissect-step
  "Takes the tuple from a :comprehension :state and returns the next step, if possible. Returns `nil` if the contents are exhausted."
  [item-and-items]
  (let [item  (first item-and-items)
        items (second item-and-items)]
    (if (splittable? items)
      (list (first items) (drop 1 items))
      nil)))


(defn cycle-collection
  "Takes an item and attempts to produce an initial :cycler item's :state tuple. Returns `nil` if it's not possible."
  [item]
  (if (splittable? item)
    (list (first item) (concat (drop 1 item) (take 1 item)))
    nil))


(defn cycle-step
  "Takes the tuple from a :cycler :state and returns the next step, if possible. Returns `nil` if the contents are inappropriate."
  [item-and-items]
  (let [item  (first item-and-items)
        items (second item-and-items)]
    (if (splittable? items)
      (list (first items) (concat (drop 1 items) (take 1 items)))
      nil)))


(defn rand-nth-seq-function
  [items]
  (if (splittable? items)
    (fn [_] (rand-nth (seq items)))
    nil))


;; CYCLER INSTRUCTIONS


(defn comprehension-instruction
  "returns a new x-comprehension instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-comprehension")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item. If it is a non-empty collection, it pushes a new `:generator` that will return a list containing the first item and the remaining ones until there are none.")
      :tags #{:generator :cycling}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/save-max-collection-size :as :limit)
      `(push.instructions.dsl/calculate [:arg :limit]
          #(push.types.type.generator/make-generator
              (if (< (wrangling/count-collection-points %1) %2)
                (dissect-collection %)
                nil)
              (partial dissect-step)) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))



(defn cycler-instruction
  "returns a new x-cycler instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cycler")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return a list containing an item and the contents rotated head->tail.")
      :tags #{:generator :cycling}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/calculate [:arg]
          #(push.types.type.generator/make-generator
              (cycle-collection %)
              (partial cycle-step)) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))



(defn sampler-instruction
  "returns a new x-sampler instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-sampler")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return a random element (sampled uniformly) from the collection.")
      :tags #{:generator :random}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/calculate [:arg]
          #(push.types.type.generator/make-generator
              (if (splittable? %1) (rand-nth (seq %1)) nil)
              (rand-nth-seq-function %1)
              (if (splittable? %1) (rand-nth (seq %1)) nil)) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))


;; ECHO GENERATORS


(defn echo-instruction
  "returns a new x-echo instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-echo")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return that item every time it's called.")
      :tags #{:generator :repeatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/calculate [:arg]
          #(push.types.type.generator/make-generator %1 (partial (constantly %1))) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))



(defn echoall-instruction
  "returns a new x-echoall instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-echoall")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the entire `" typename "` stack into a new `:generator` that will return the entire stack (as a list pushed to the `:exec` stack) every time it's called.")
      :tags #{:generator :repeatable}
      `(push.instructions.dsl/save-stack ~typename :as :all)
      `(push.instructions.dsl/save-max-collection-size :as :limit)
      `(push.instructions.dsl/calculate [:all :limit]
          #(push.types.type.generator/make-generator
            (if (< (wrangling/count-collection-points %1) %2) %1 nil)
              (partial (constantly %1))) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))



(defn rerunall-instruction
  "returns a new x-rerunall instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-rerunall")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the `" typename "` stack into a new _cycler_ `:generator` instance, if the stack is not empty.")
      :tags #{:generator :cycling}
      `(push.instructions.dsl/save-stack ~typename :as :all)
      `(push.instructions.dsl/calculate [:all]
          #(push.types.type.generator/make-generator
              (dissect-collection %)
              (partial dissect-step)) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))
