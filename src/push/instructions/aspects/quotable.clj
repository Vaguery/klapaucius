(ns push.instructions.aspects.quotable
  (:require [push.type.definitions.quoted :as quotable])
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]
        ))



(defn tocode-instruction
  "returns a new x->code instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->code")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top item of the `" typename "` stack, makes it into a QuotedCode item, and pushes to `:exec`.")
      :tags #{:visible}

      `(consume-top-of ~typename :as :arg)
      `(calculate [:arg] #(quotable/push-quote %1) :as :quoted)
      `(push-onto :exec :quoted)
      ))))
