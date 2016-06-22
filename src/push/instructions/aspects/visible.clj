(ns push.instructions.aspects.visible
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.type.core :as t]
            ))


(defn empty?-instruction
  "returns a new x-empty? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-empty?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pushes a `:boolean`, `true` if the `" typename "` stack is empty, `false` otherwise.")
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/calculate [:depth] #(zero? %1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))



(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pushes a `:scalar` which is the number of items in the `" typename "` stack.")
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/push-onto :scalar :depth)))))
