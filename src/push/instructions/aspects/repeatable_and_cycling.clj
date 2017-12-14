(ns push.instructions.aspects.repeatable-and-cycling
  (:require [push.util.code-wrangling
              :as util
              :refer [list! count-collection-points]]
            [push.type.definitions.generator
              :as g
              :refer [make-generator]])
  (:use     [push.instructions.core
              :only (build-instruction)]
            [push.instructions.dsl]))


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
  (when (splittable? item)
    (list (first item) (rest item))))


(defn dissect-step
  "Takes the tuple from a :comprehension :state and returns the next step, if possible. Returns `nil` if the contents are exhausted."
  [item-and-items]
  (let [item  (first item-and-items)
        items (second item-and-items)]
    (when (splittable? items)
      (list (first items) (drop 1 items)))))


(defn cycle-collection
  "Takes an item and attempts to produce an initial :cycler item's :state tuple. Returns `nil` if it's not possible."
  [item]
  (when (splittable? item)
    (list (first item) (util/list! (concat (drop 1 item) (take 1 item))))))


(defn cycle-step
  "Takes the tuple from a :cycler :state and returns the next step, if possible. Returns `nil` if the contents are inappropriate."
  [item-and-items]
  (let [item  (first item-and-items)
        items (second item-and-items)]
    (when (splittable? items)
      (list (first items) (util/list! (concat (drop 1 items) (take 1 items))))
      )))


(defn rand-nth-seq-function
  [items]
  (when (splittable? items)
    (fn [_] (rand-nth (seq items)))
    ))


;; CYCLER INSTRUCTIONS


(defn comprehension-instruction
  "returns a new x-comprehension instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-comprehension")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item. If it is a non-empty collection, it pushes a new `:generator` that will return a list containing the first item and the remaining ones until there are none.")
      :tags #{:generator :cycling}

      `(consume-top-of ~typename :as :arg)
      `(save-max-collection-size :as :limit)
      `(calculate [:arg :limit]
          #(g/make-generator
              (if (< (util/count-collection-points %1) %2)
                (dissect-collection %)
                nil)
              (partial dissect-step)) :as :g)
      `(push-onto :exec :g)))))



(defn cycler-instruction
  "returns a new x-cycler instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cycler")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return a list containing an item and the contents rotated head->tail.")
      :tags #{:generator :cycling}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
          #(g/make-generator
              (cycle-collection %)
              (partial cycle-step)) :as :g)
      `(push-onto :exec :g)))))



(defn sampler-instruction
  "returns a new x-sampler instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-sampler")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return a random element (sampled uniformly) from the collection.")
      :tags #{:generator :random}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
          #(g/make-generator
              (if (splittable? %1) (rand-nth (seq %1)) nil)
              (rand-nth-seq-function %1)
              (if (splittable? %1) (rand-nth (seq %1)) nil)) :as :g)
      `(push-onto :exec :g)))))



;; ECHO GENERATORS



(defn echo-instruction
  "returns a new x-echo instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-echo")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes a new `:generator` that will return that item every time it's called.")
      :tags #{:generator :repeatable}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
          #(g/make-generator %1 (partial (constantly %1))) :as :g)
      `(push-onto :exec :g)))))



(defn echoall-instruction
  "returns a new x-echoall instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-echoall")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the entire `" typename "` stack into a new `:generator` that will return the entire stack (as a list pushed to the `:exec` stack) every time it's called.")
      :tags #{:generator :repeatable}

      `(save-stack ~typename :as :all)
      `(save-max-collection-size :as :limit)
      `(calculate [:all :limit]
          #(g/make-generator
            (if (< (util/count-collection-points %1) %2) %1 nil)
              (partial (constantly %1))) :as :g)
      `(push-onto :exec :g)))))



(defn rerunall-instruction
  "returns a new x-rerunall instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-rerunall")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the `" typename "` stack into a new _cycler_ `:generator` instance, if the stack is not empty.")
      :tags #{:generator :cycling}

      `(save-stack ~typename :as :all)
      `(calculate [:all]
          #(g/make-generator
              (dissect-collection %)
              (partial dissect-step)) :as :g)
      `(push-onto :exec :g)))))
