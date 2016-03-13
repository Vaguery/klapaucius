(ns push.instructions.aspects.movable
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.types.core :as t]
            [push.util.code-wrangling :as util]
            ))


(defn againlater-instruction
  "returns a new x-againlater instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-againlater")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` places a copy of the top `" typename "` item at the tail of the `:exec` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :item)
      `(push.instructions.dsl/consume-stack :exec :as :oldstack)
      `(push.instructions.dsl/calculate [:oldstack :item]
          #(into '() (reverse (concat %1 (list %2)))) :as :newstack)
      `(push.instructions.dsl/replace-stack :exec :newstack)
      `(push.instructions.dsl/push-onto ~typename :item)))))



(defn cutflip-instruction
  "returns a new x-cutflip instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cutflip")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` takes an `:integer` argument, makes that into an index modulo the `" typename "` stack size, takes the first [idx] items and reverses the order of that segment on the stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of :integer :as :where)
      `(push.instructions.dsl/consume-stack ~typename :as :old)
      `(push.instructions.dsl/calculate [:old :where]
        #(if (empty? %1) 0 (util/safe-mod %2 (count %1))) :as :idx)
      `(push.instructions.dsl/calculate [:old :idx]
        #(into '() (reverse (concat (reverse (take %2 %1)) (drop %2 %1)))) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))



(defn cutstack-instruction
  "returns a new x-cutstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cutstack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` takes an `:integer` argument, makes that into an index modulo the `" typename "` stack size, takes the first [idx] items and moves that to the bottom of the stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of :integer :as :where)
      `(push.instructions.dsl/consume-stack ~typename :as :old)
      `(push.instructions.dsl/calculate [:old :where]
        #(if (empty? %1) 0 (util/safe-mod %2 (count %1))) :as :idx)
      `(push.instructions.dsl/calculate [:old :idx]
        #(into '() (reverse (concat (drop %2 %1) (take %2 %1)))) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))



(defn dup-instruction
  "returns a new x-dup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-dup")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` examines the top `" typename "` item and pushes a duplicate to the same stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/save-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg1)))))



(defn flipstack-instruction
  "returns a new x-flipstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flipstack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` reverses the entire `" typename "` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-stack ~typename :as :old)
      `(push.instructions.dsl/calculate [:old]
          #(into '() %1) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))



(defn flush-instruction
  "returns a new x-flush instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flush")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` discards all items from the `" typename "` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/delete-stack ~typename)))))



(defn later-instruction
  "returns a new x-later instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-later")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and places it at the tail of the `:exec` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :item)
      `(push.instructions.dsl/consume-stack :exec :as :oldstack)
      `(push.instructions.dsl/calculate [:oldstack :item]
          #(into '() (reverse (concat %1 (list %2)))) :as :newstack)
      `(push.instructions.dsl/replace-stack :exec :newstack)))))



(defn liftstack-instruction
  "returns a new x-liftstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-liftstack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` takes an `:integer` argument, makes that into an index modulo the `" typename "` stack size, 'cuts' the stack after the first [idx] items and copies everything below that point onto the top as a block. If the result would have more points (at any level) than `max-collection-size`, the change is undone.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of :integer :as :where)
      `(push.instructions.dsl/consume-stack ~typename :as :old)
      `(push.instructions.dsl/calculate [:old :where]
        #(if (empty? %1) 0 (util/safe-mod %2 (count %1))) :as :idx)
      `(push.instructions.dsl/calculate [:old :idx]
        #(into '() (reverse (concat (drop %2 %1) %1))) :as :new)
      '(push.instructions.dsl/save-max-collection-size :as :limit)
      `(push.instructions.dsl/calculate [:new :limit :old]
        #(if (> (util/count-collection-points %1) %2) %3 %1) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))



(defn pop-instruction
  "returns a new x-pop instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-pop")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` discards the top item from the `" typename "` stack.")
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
      (str "`:" instruction-name "` pops the top three items from the `" typename "` stack; call them `A`, `B` and `C`, respectively. It pushes them back so that top-to-bottom order is now `'(C A B ...)`")
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






