(ns push.instructions.aspects.storable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t])
  )


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
      `(push.instructions.dsl/bind-item :what :into :where)
      ))))



(defn store-instruction
  "returns a new x-stoer instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-store")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "`, which is pushed to a new `:ref` created in the `:bindings` registry")
      :tags #{:storable}
      `(push.instructions.dsl/consume-top-of ~typename :as :what)
      `(push.instructions.dsl/bind-item :what)
      ))))

