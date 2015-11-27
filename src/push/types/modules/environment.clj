(ns push.types.modules.environment
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.util.stack-manipulation :as u])
  (:use push.instructions.aspects.returnable)
  (:use push.instructions.aspects.visible)
  )


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
    "`environment-end` pops the top `:environment` item, and records the current `:return` stack, then replaces all stacks _except_ the `:print`, `:log` , `:unknown` and `:error` stacks with their archived counterparts. If there are any `:exec` items in the current state, these are pushed onto the retrieved archived state (as a single continuation, for simplicity). The items on the `:return` stack are then pushed onto `:exec` as a single list."
    :tags #{:complex :base}
    (d/save-stack :exec :as :remainder)
    (d/save-stack :return :as :results)
    (d/consume-top-of :environment :as :archive)
    (d/retrieve-all-stacks :using :archive)
    (d/consume-stack :exec :as :new-exec)
    (d/calculate [:results :remainder :new-exec]
      #(into '() (reverse (concat (reverse %1) %2 %3))) :as :compiled)
    (d/replace-stack :exec :compiled)))



(def classic-environment-module
  ( ->  (t/make-module  :environment
                        :attributes #{:complex :base})
        make-visible
        (t/attach-instruction , environment-new)
        (t/attach-instruction , environment-begin)
        (t/attach-instruction , environment-end)
))

