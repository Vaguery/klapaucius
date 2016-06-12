(ns push.instructions.aspects.to-tagspace
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            ))





;; INSTRUCTIONS


(defn to-tagspaceint
  "returns a new x->tagspaceint instruction for the given collection type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->tagspaceint")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and two `:integer` items (call them `end` and `start`, respectively). The contents of the collection are stored in a new `:tagspace` with the first item at index `start`, the last at index `end`, and the rest as evenly distributed as possible between the two. Recall that tagspace keys are permitted to be any scalar numbers, so calculated values are used for intermediate indices. If `end` is smaller than `start`, then that's the way things will work.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :integer :as :end)
      `(push.instructions.dsl/consume-top-of :integer :as :start)
      `(push.instructions.dsl/calculate [:arg] #(count %1) :as :howmany)
      `(push.instructions.dsl/calculate [:start :end :howmany]
        #(if (zero? %3) 0 (/ (- %2 %1) (dec %3))) :as :delta)
      `(push.instructions.dsl/calculate [:howmany :start :delta]
          #(take %1 (iterate (partial +' %3) %2)) :as :indices)
      `(push.instructions.dsl/calculate [:indices :arg]
          #(push.types.type.tagspace/make-tagspace (zipmap %1 %2)) :as :result)
      `(push.instructions.dsl/push-onto :tagspace :result)
      ))))



(defn to-tagspacefloat
  "returns a new x->tagspacefloat instruction for the given collection type or module."
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->tagspacefloat")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and two `:float` items (call them `end` and `start`, respectively). The contents of the collection are stored in a new `:tagspace` with the first item at index `start`, the last at index `end`, and the rest as evenly distributed as possible between the two. Recall that tagspace keys are permitted to be any scalar numbers, so calculated values are used for intermediate indices. If `end` is smaller than `start`, then that's the way things will work.")
      :tags #{:tagspace :collection}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/consume-top-of :float :as :end)
      `(push.instructions.dsl/consume-top-of :float :as :start)
      `(push.instructions.dsl/calculate [:arg] #(count %1) :as :howmany)
      `(push.instructions.dsl/calculate [:start :end :howmany]
        #(if (zero? %3) 0 (/ (-' %2 %1) (dec %3))) :as :delta)
      `(push.instructions.dsl/calculate [:howmany :start :delta]
          #(take %1 (iterate (partial +' %3) %2)) :as :indices)
      `(push.instructions.dsl/calculate [:indices :arg]
          #(push.types.type.tagspace/make-tagspace (zipmap %1 %2)) :as :result)
      `(push.instructions.dsl/push-onto :tagspace :result)
      ))))
