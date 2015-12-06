(ns push.instructions.aspects.printable
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:require [push.types.core :as t])
  )


(defn print-instruction
  "returns a new x-print instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-print")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes it to the `:print` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto :print :arg1)))))

