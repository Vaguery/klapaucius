(ns push.instructions.aspects.taggable
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]
        [push.type.definitions.tagspace]))



(defn tag-instruction
  "returns a new x-tag instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tag")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:scalar`, the top `:tagspace` and the top `" typename "` items. The item is stored in the `:tagspace` under the index specified by the `:scalar`.")
      :tags #{:tagspace :collection}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :tagspace :as :ts)
      `(consume-top-of :scalar :as :index)
      `(calculate [:ts :arg :index] #(store-in-tagspace %1 %2 %3) :as :stored)
      `(push-onto :tagspace :stored)))))


