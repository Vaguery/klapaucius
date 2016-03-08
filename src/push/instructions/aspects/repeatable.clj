(ns push.instructions.aspects.repeatable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            ))


(defn echo-instruction
  "returns a new x-echo instruction for the given type or module"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-echo")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes a new `:generator` that will return that item every time it's called.")
      :tags #{:generator :repeatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      `(push.instructions.dsl/calculate [:arg]
          #(push.types.extra.generator/make-generator %1 (partial (constantly %1))) :as :g)
      `(push.instructions.dsl/push-onto :generator :g)))))



