(ns push.instructions.aspects.taggable
  (:require [push.util.code-wrangling :as fix])
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]
        [push.type.definitions.tagspace]
        ))



(defn tag-instruction
  "returns a new x-tag instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tag")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:scalar`, the top `:tagspace` and the top `" typename "` items. The item is stored in the `:tagspace` under the index specified by the `:scalar`. If the item is too large (bigger than `max-collection-size` in the current interpreter state) then an `:error` is produced instead.")
      :tags #{:tagspace :collection}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :tagspace :as :ts)
      `(consume-top-of :scalar :as :index)
      `(save-max-collection-size :as :max)
      `(calculate [:max :ts :arg]
        #(< %1
            (+' (fix/count-collection-points %2)
                (fix/count-collection-points %3))) :as :fail?)
      `(calculate [:fail? :ts :arg :index]
        #(if %1
            %2
            (store-in-tagspace %2 %3 %4)) :as :stored)
      `(calculate [:fail?]
        #(if %1 (str ~instruction-name " failed: oversized result") nil) :as :warning)
      `(push-onto :tagspace :stored)
      `(record-an-error :from :warning)
      ))))




(defn tagstack-instruction
  "returns a new x-tagstack instruction for the given type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-tagstack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` saves a _copy_ of the entire `" typename "` stack as a new `:tagspace`, using the stack order (0-based) as keys.")
      :tags #{:tagspace :collection}

      `(save-stack ~typename :as :items)
      `(calculate [:items] #(make-tagspace (zipmap (range) %1)) :as :new-ts)
      `(push-onto :tagspace :new-ts)
      ))))
