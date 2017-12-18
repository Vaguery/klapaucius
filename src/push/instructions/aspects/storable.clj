(ns push.instructions.aspects.storable
  (:use [push.instructions.core
          :only (build-instruction)]
        [push.instructions.dsl]))




(defn save-instruction
  "returns a new x-save instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-save")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:ref` and the top `" typename "`, which is pushed to the indicated `:ref` in the `:bindings` registry")

      `(consume-top-of :ref :as :where)
      `(consume-top-of ~typename :as :what)
      `(bind-item :what :into :where)
      ))))



(defn store-instruction
  "returns a new x-store instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-store")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "`, which is pushed to a new `:ref` created in the `:bindings` registry")

      `(consume-top-of ~typename :as :what)
      `(replace-binding :what)
      ))))



(defn savestack-instruction
  "returns a new x-savestack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-savestack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:ref` and copies the entire `" typename "` stack into the indicated `:ref` in the `:bindings` registry, replacing the binding's current contents.")

      `(consume-top-of :ref :as :where)
      `(save-stack ~typename :as :all)
      `(replace-binding :all :into :where)
      ))))



(defn storestack-instruction
  "returns a new x-storestack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-storestack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` copies the entire `" typename "` stack into a new `:ref` created in the `:bindings` registry. The stack contents are not consumed.")

      `(save-stack ~typename :as :all)
      `(replace-binding :all)
      ))))
