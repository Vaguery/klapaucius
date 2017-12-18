(ns push.instructions.aspects.set-able
  (:use [push.instructions.core
          :only (build-instruction)]
        [push.instructions.dsl]))




(defn as-set-instruction
  "returns a new x-as-set instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-as-set")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and creates a new `:set`. If the argument is a code block, set or vector, the set contains its elements; otherwise, the set contains the entire item.")

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg]
        #(if (or (set? %1) (seq? %1) (vector? %1))
          (set %1)
          (conj #{} %1)) :as :s)
      `(return-item :s)
      ))))




(defn conj-set-instruction
  "returns a new x-conj-set instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-conj-set")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and the top `:set` item, and conjoins the item to the `:set`. If the argument is a code block, set or vector, the items added will be its elements, otherwise the entire item.")

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :set :as :s)
      `(calculate [:arg :s]
        #(if (or (set? %1) (seq? %1) (vector? %1))
          (into %2 %1)
          (conj %2 %1)) :as :result)
      `(return-item :result)
      ))))




(defn in-set?-instruction
  "returns a new x-in-set? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-in-set?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and the top `:set` item, and pushes `true` if the item is an element of the `:set`.")

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :set :as :s)
      `(calculate [:s :arg] #(boolean (%1 %2)) :as :result)
      `(return-item :result)
      ))))





(defn intoset-instruction
  "returns a new x-intoset instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-intoset")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and the top `:set` item, and adds the item to the `:set`. If a collection, the argument remains a single set item.")

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :set :as :s)
      `(calculate [:s :arg] #(conj %1 %2) :as :result)
      `(return-item :result)
      ))))




(defn toset-instruction
  "returns a new x->set instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->set")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and creates a new `:set` containing just that item. If a collection, the argument remains a single set item.")

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg] #(conj #{} %1) :as :s)
      `(return-item :s)
      ))))
