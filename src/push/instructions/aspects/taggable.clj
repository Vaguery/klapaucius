(ns push.instructions.aspects.taggable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            [push.util.numerics :as n]
            ))


;; SUPPORT


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (n/pN (assoc-in ts [:contents idx] item)))


;; INSTRUCTIONS


(defn tag-instruction
  "returns a new x-tag instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tag")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:scalar`, the top `:tagspace` and the top `" typename "` items. The item is stored in the `:tagspace` under the index specified by the `:scalar`.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :tagspace :as :ts)
      `(push.instructions.dsl/consume-top-of :scalar :as :index)
      `(push.instructions.dsl/calculate [:ts :arg :index]
        #(store-in-tagspace  %1 %2 %3) :as :stored)
      `(push.instructions.dsl/push-onto :tagspace :stored)))))


