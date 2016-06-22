(ns push.instructions.aspects.storable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.type.core :as t]
            ))



(defn save-instruction
  "returns a new x-save instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-save")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:ref` and the top `" typename "`, which is pushed to the indicated `:ref` in the `:bindings` registry")
      :tags #{:storable}
      '(push.instructions.dsl/consume-top-of :ref :as :where)
      `(push.instructions.dsl/consume-top-of ~typename :as :what)
      `(push.instructions.dsl/bind-item :what :into :where)))))



(defn store-instruction
  "returns a new x-store instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-store")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "`, which is pushed to a new `:ref` created in the `:bindings` registry")
      :tags #{:storable}
      `(push.instructions.dsl/consume-top-of ~typename :as :what)
      `(push.instructions.dsl/replace-binding :what)))))



(defn savestack-instruction
  "returns a new x-savestack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-savestack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:ref` and copies the entire `" typename "` stack into the indicated `:ref` in the `:bindings` registry, replacing the binding's current contents.")
      :tags #{:storable}
      '(push.instructions.dsl/consume-top-of :ref :as :where)
      `(push.instructions.dsl/save-stack ~typename :as :all)
      `(push.instructions.dsl/replace-binding :all :into :where)))))



(defn storestack-instruction
  "returns a new x-storestack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-storestack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the entire `" typename "` stack into a new `:ref` created in the `:bindings` registry. The stack contents are not consumed.")
      :tags #{:storable}
      `(push.instructions.dsl/save-stack ~typename :as :all)
      `(push.instructions.dsl/replace-binding :all)))))
