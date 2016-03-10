(ns push.instructions.extra.stack-combinators
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as u])
  )


(defn flipstack-instruction
  "returns a new x-flipstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flipstack")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` reverses the entire `" typename
        "` stack.")
      :tags #{:combinator}
      `(push.instructions.dsl/consume-stack ~typename :as :old)
      `(push.instructions.dsl/calculate [:old]
          #(into '() %1) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))


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
        #(if (empty? %1) 0 (u/safe-mod %2 (count %1))) :as :idx)
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
        #(if (empty? %1) 0 (u/safe-mod %2 (count %1))) :as :idx)
      `(push.instructions.dsl/calculate [:old :idx]
        #(into '() (reverse (concat (drop %2 %1) (take %2 %1)))) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))


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
        #(if (empty? %1) 0 (u/safe-mod %2 (count %1))) :as :idx)
      `(push.instructions.dsl/calculate [:old :idx]
        #(into '() (reverse (concat (drop %2 %1) %1))) :as :new)
      '(push.instructions.dsl/save-max-collection-size :as :limit)
      `(push.instructions.dsl/calculate [:new :limit :old]
        #(if (> (u/count-collection-points %1) %2) %3 %1) :as :new)
      `(push.instructions.dsl/replace-stack ~typename :new)))))


;;;;;;;;;;;;;;;;;


(defn add-extra-stack-combinators
  "takes a PushType and adds the instructions defined in this file to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (flipstack-instruction pushtype))
      (t/attach-instruction (cutflip-instruction pushtype))
      (t/attach-instruction (cutstack-instruction pushtype))
      (t/attach-instruction (liftstack-instruction pushtype))
      ))


(defn extend-combinators
  "takes a PushType, and if it has the attribute :movable (already), it will add the instructions defined in this file; otherwise returns it unchanged"
  [pushtype]
  (if (some #{:movable} (:attributes pushtype))
    (add-extra-stack-combinators pushtype)
    pushtype))

