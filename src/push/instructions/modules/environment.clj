(ns push.instructions.modules.environment
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(defn return-instruction
  "returns a new x-return instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes it to the `:return` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto :return :arg1)))))


(defn return-pop-instruction
  "returns a new x-return-pop instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return-pop")
        token (keyword (str (name typename) "-pop"))]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` creates a new `" typename
        "-pop` token shoves it to the _bottom_ of the `:return` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-stack :return :as :old-stack)
      `(push.instructions.dsl/calculate [:old-stack] 
          #(concat %1 (list ~token)) :as :new-stack)
      `(push.instructions.dsl/replace-stack :return :new-stack)))))


(def environment-new
  (core/build-instruction
    environment-new
    "`environment-new` pops the top `:exec` item, archives all the remaining current stacks to a new :environment item, and then continues with only the popped item in place of the archived `:exec` stack, and an empty `:return` stack."
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :run-this)
    (d/archive-all-stacks)
    (d/delete-stack :return)
    (d/delete-stack :exec)
    (d/push-onto :exec :run-this)))


(def environment-begin
  (core/build-instruction
    environment-begin
    "`environment-begin` takes a snapshot of the current `:exec` stack, clears it, saves an archive of the stacks, clears the `:return` stack, and finally replaces the `:exec` stack. As a result, the archived stacks have the old `:return` stack and an empty `:exec` stack, and the running stacks have the old `:exec` stack and an empty `:return` stack."
    :tags #{:complex :base}
    (d/save-stack :exec :as :old-exec)
    (d/delete-stack :exec)
    (d/archive-all-stacks)
    (d/delete-stack :return)
    (d/replace-stack :exec :old-exec)))


(def environment-end
  (core/build-instruction
    environment-end
    "`environment-end` pops the top `:environment` item, and records the current `:return` stack, then replaces all stacks _except_ the `:print`, `:log` and `:error` stacks with their archived counterparts. Then the items on the `:return` stack are pushed to `:exec` as a single list."
    :tags #{:complex :base}
    (d/save-stack :return :as :results)
    (d/consume-top-of :environment :as :archive)
    (d/retrieve-all-stacks :using :archive)
    (d/push-onto :exec :results)
    ))


(defn make-returnable
  "takes a PushType and adds the :returnable attribute and the `:X-return` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (return-instruction pushtype))
      (t/attach-instruction (return-pop-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :returnable))))



(def classic-environment-module
  ( ->  (t/make-module  :environment
                        :attributes #{:complex :base})
        (t/attach-instruction , environment-new)
        (t/attach-instruction , environment-begin)
        (t/attach-instruction , environment-end)
))

