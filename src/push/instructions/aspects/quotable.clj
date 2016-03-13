(ns push.instructions.aspects.quotable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            ))



(defn tocode-instruction
  "returns a new x->code instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->code")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` moves the top item of the `" typename "` stack to the `:code` stack.")
      :tags #{:visible}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      '(push.instructions.dsl/push-onto :code :arg)))))
