(ns push.instructions.aspects.taggable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t])
  )


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (assoc-in ts [:contents idx] item))



(defn tagwithint-instruction
  "returns a new x-tagwithinteger instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tagwithinteger")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:integer`, the top `:tagspace` and the top `" typename
        "` items. The item is stored in the `:tagspace` under the index specified by the `:integer`.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :tagspace :as :ts)
      `(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/calculate [:ts :arg :index]
        #(store-in-tagspace  %1 %2 %3) :as :stored)
      `(push.instructions.dsl/push-onto :tagspace :stored)))))



(defn tagwithfloat-instruction
  "returns a new x-tagwithinteger instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tagwithfloat")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:float`, the top `:tagspace` and the top `" typename
        "` items. The item is stored in the `:tagspace` under the index specified by the `:float`.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :tagspace :as :ts)
      `(push.instructions.dsl/consume-top-of :float :as :index)
      `(push.instructions.dsl/calculate [:ts :arg :index]
        #(store-in-tagspace  %1 %2 %3) :as :stored)
      `(push.instructions.dsl/push-onto :tagspace :stored)))))
