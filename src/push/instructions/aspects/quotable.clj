(ns push.instructions.aspects.quotable
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]))



(defn tocode-instruction
  "returns a new x->code instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->code")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` moves the top item of the `" typename "` stack to the `:code` stack.")
      :tags #{:visible}

      `(consume-top-of ~typename :as :arg)
      `(push-onto :code :arg)))))
