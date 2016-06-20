(ns push.instructions.aspects.to-tagspace
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            [push.util.numerics :as n]
            ))





;; INSTRUCTIONS



(defn to-tagspace
  "returns a new x->tagspace instruction for the given collection type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->tagspace")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and two `:scalar` items (call them `end` and `start`, respectively). The contents of the collection are stored in a new `:tagspace` with the first item at index `start`, the last at index `end`, and the rest as evenly distributed as possible between the two. The indices are all coerced to be `:scalar` values, so some may overlap. If `end` is smaller than `start`, then that's the way things will work.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :scalar :as :end)
      `(push.instructions.dsl/consume-top-of :scalar :as :start)
      `(push.instructions.dsl/calculate [:arg] #(count %1) :as :howmany)
      `(push.instructions.dsl/calculate [:start :end :howmany]
        #(if (< %3 2)
          0 
          (/ (-' %2 %1) (dec %3))) :as :delta)
      `(push.instructions.dsl/calculate [:howmany :start :delta]
          #(if %3 (n/index-maker %1 %2 %3) nil) :as :indices)
      `(push.instructions.dsl/calculate [:indices :arg]
          #(if %2
            (push.types.type.tagspace/make-tagspace (zipmap %1 %2))
            nil) :as :result)
      `(push.instructions.dsl/push-onto :tagspace :result)
      ))))


