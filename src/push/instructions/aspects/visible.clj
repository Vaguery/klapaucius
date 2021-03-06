(ns push.instructions.aspects.visible
  (:use [push.instructions.core
          :only (build-instruction)]
        [push.instructions.dsl]))



(defn empty?-instruction
  "returns a new x-empty? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-empty?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pushes a `:boolean`, `true` if the `" typename "` stack is empty, `false` otherwise.")

      `(count-of ~typename :as :depth)
      `(calculate [:depth] #(zero? %1) :as :check)
      `(return-item :check)
      ))))



(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pushes a `:scalar` which is the number of items in the `" typename "` stack.")

      `(count-of ~typename :as :depth)
      `(return-item :depth)
      ))))
