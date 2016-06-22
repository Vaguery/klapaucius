(ns push.instructions.aspects.printable
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]))



(defn print-instruction
  "returns a new x-print instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-print")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes it to the `:print` stack.")
      :tags #{:io}
      
      `(consume-top-of ~typename :as :arg1)
      `(push-onto :print :arg1)))))

