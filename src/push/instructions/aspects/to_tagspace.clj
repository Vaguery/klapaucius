(ns push.instructions.aspects.to-tagspace
  (:require [push.util.numerics
              :as num
              :refer [index-maker]])
  (:use     [push.instructions.core
              :only (build-instruction)]
            [push.instructions.dsl]))



(defn to-tagspace
  "returns a new x->tagspace instruction for the given collection type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->tagspace")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and two `:scalar` items (call them `end` and `start`, respectively). The contents of the collection are stored in a new `:tagspace` with the first item at index `start`, the last at index `end`, and the rest as evenly distributed as possible between the two. The indices are all coerced to be `:scalar` values, so some may overlap. If `end` is smaller than `start`, then that's the way things will work. The elements of the original `" typename "` are stored in the `:tagspace` in whatever order they appear, which may have unexpected consequences when converting unordered collections like `:set` items.")
      :tags #{:tagspace :collection}

      `(consume-top-of ~typename :as :arg)
      `(consume-top-of :scalar :as :end)
      `(consume-top-of :scalar :as :start)
      `(calculate [:arg] #(count %1) :as :howmany)
      `(calculate [:start :end :howmany]
        #(if (< %3 2)
          0
          (/ (-' %2 %1) (dec %3))) :as :delta)
      `(calculate [:howmany :start :delta]
          #(if %3 (num/index-maker %1 %2 %3) nil) :as :indices)
      `(calculate [:indices :arg]
          #(if %2
            (push.type.definitions.tagspace/make-tagspace (zipmap %1 %2))
            nil) :as :result)
      `(push-onto :exec :result)
      ))))
