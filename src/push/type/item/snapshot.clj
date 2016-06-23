(ns push.type.item.snapshot
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.util.stack-manipulation :as u]
            [push.instructions.aspects :as aspects])
  (:use push.type.definitions.snapshot))


;; INSTRUCTIONS


(def snapshot-begin
  (core/build-instruction
    snapshot-begin
    "`snapshot-begin` saves a snapshot of the interpreter with only its `:exec` stack emptied, and clears the current `:return` stack. As a result, the archived stacks have the old `:return` stack and an empty `:exec` stack, and the running stacks have the old `:exec` stack and an empty `:return` stack."
    :tags #{:complex :base}
    (d/save-stack :exec :as :old-exec)
    (d/delete-stack :exec)
    (d/save-snapshot)
    (d/delete-stack :return)
    (d/replace-stack :exec :old-exec)))



(def snapshot-end
  (core/build-instruction
    snapshot-end
    "`snapshot-end` pops the top `:snapshot` item. The current stacks are all replaced with the archived ones, EXCEPT for `:print`, `:log`, `:unknown`, and `:error`. The current `:exec` stack's contents are pushed onto the retrieved `:exec` stack as a code block. The current `:return` stack's contents are then pushed onto `:exec` as a single code block. The `:bindings` and `:config` are also returned to their stored states."
    :tags #{:complex :base}

    (d/save-stack :exec :as :new-exec)
    (d/consume-stack :return :as :results)
    (d/consume-top-of :snapshot :as :archive)
    (d/retrieve-snapshot-state :using :archive)
    ;; replaces :exec with stored version
    (d/push-onto :exec :new-exec)
    (d/push-onto :exec :results)
    ))



(def snapshot-new
  (core/build-instruction
    snapshot-new
    "`snapshot-new` pops the top `:exec` item, then archives all the remaining current stacks (including the rest of the `:exec` stack) to a new :snapshot item, then empties the `:return` stack. In other words, it 'runs' only top item from the `:exec` stack, and the current `:bindings`."
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :run-this)
    (d/save-snapshot)
    (d/delete-stack :return)
    (d/delete-stack :exec)
    (d/push-onto :exec :run-this)))



(def snapshot-type
  ( ->  (t/make-type    :snapshot
                        :recognized-by snapshot?
                        :attributes #{:complex :base})
        aspects/make-equatable
        aspects/make-movable
        aspects/make-quotable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , snapshot-begin)
        (t/attach-instruction , snapshot-end)
        (t/attach-instruction , snapshot-new)
))

