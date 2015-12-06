(ns push.instructions.aspects.movable
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:require [push.types.core :as t])
  )


(defn dup-instruction
  "returns a new x-dup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-dup")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` examines the top `" typename
        "` item and pushes a duplicate to the same stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/save-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg1)))))


(defn flush-instruction
  "returns a new x-flush instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flush")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` discards all items from the `" typename
        "` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/delete-stack ~typename)))))


(defn pop-instruction
  "returns a new x-pop instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-pop")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` discards the top item from the `" typename
        "` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/delete-top-of ~typename)))))


(defn rotate-instruction
  "returns a new x-rotate instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-rotate")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top three items from the `" typename
        "` stack; call them `A`, `B` and `C`, respectively. It pushes them back so that top-to-bottom order is now `'(C A B ...)`")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg3)
      `(push.instructions.dsl/push-onto ~typename :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg3)))))


(defn shove-instruction
  "returns a new x-shove instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-shove")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top item from the `" typename
        "` stack and the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the top item is _moved_ so that it is in that position in the resulting stack.")
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/consume-top-of ~typename :as :shoved-item)
      `(push.instructions.dsl/insert-as-nth-of ~typename :shoved-item :at :index)))))



(defn swap-instruction
  "returns a new x-swap instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-swap")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` swaps the positions of the top two `" typename
        "` items.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg2)))))


(defn yank-instruction
  "returns a new x-yank instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yank")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the item _currently_ found in the indexed position in the `" typename "` stack is _moved_ so that it is on top.")
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/consume-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))


(defn yankdup-instruction
  "returns a new x-yankdup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yankdup")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:integer`. The `:integer` is brought into range as an index by applying `(mod integer (count stack))`, and then the item _currently_ found in the indexed position in the `" typename "` stack is _copied_ so that a duplicate of it is on top.")
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/save-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))

