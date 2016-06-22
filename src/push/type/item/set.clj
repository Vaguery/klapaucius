(ns push.type.item.set
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [clojure.set :as sets])
  (:use push.type.item.generator)
  (:use push.type.item.tagspace)
  )



(def code->set
  (core/build-instruction
    code->set
    "`:code->set` pops the top `:code` item and (if it is a list) converts it to a :set; if it's not a list, it becomes a one-element :set."
    :tags #{:set :predicate}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg]
      #(into #{} (if (seq? %1) %1 (list %1))) :as :result)
    (d/push-onto :set :result)))



(def vector->set
  (core/build-instruction
    vector->set
    "`:vector->set` pops the top `:vector` item and converts it to a :set"
    :tags #{:set :predicate}
    (d/consume-top-of :vector :as :arg)
    (d/calculate [:arg] #(into #{} %1) :as :result)
    (d/push-onto :set :result)))



(def set-difference
  (t/simple-2-in-1-out-instruction
    "`:set-difference` pops the top two `:set` items, and pushes their difference"
    :set "difference" 'sets/difference))



(def set-intersection
  (t/simple-2-in-1-out-instruction
    "`:set-intersection` pops the top two `:set` items, and pushes their intersection"
    :set "intersection" 'sets/intersection))



(def set-subset?
  (core/build-instruction
    set-subset?
    "`:set-subset?` pops the top two `:set` values (call them `A` and `B`, respectively). Pushes `true` if `B` is a subset of `A`, `false` otherwise."
    :tags #{:set :predicate}
    (d/consume-top-of :set :as :b)
    (d/consume-top-of :set :as :a)
    (d/calculate [:a :b]
      #(sets/subset? %2 %1) :as :well?)
    (d/push-onto :boolean :well?)))



(def set-superset?
  (core/build-instruction
    set-superset?
    "`:set-superset?` pops the top two `:float` values (call them `A` and `B`, respectively). Pushes `true` if `B` is a superset of `A`, `false` otherwise."
    :tags #{:set :predicate}
    (d/consume-top-of :set :as :b)
    (d/consume-top-of :set :as :a)
    (d/calculate [:a :b]
      #(sets/superset? %2 %1) :as :well?)
    (d/push-onto :boolean :well?)))



(def set-union
  (t/simple-2-in-1-out-instruction
    "`:set-union` pops the top two `:set` items, and pushes their union"
    :set "union" 'sets/union))



(def standard-set-type
  "builds the basic `:set` type, which can hold arbitrary and mixed contents"
  (let [typename :set]
  (-> (t/make-type  :set
                    :recognized-by set?
                    :attributes #{:collection :set})
      aspects/make-cycling
      aspects/make-equatable
      aspects/make-into-tagspaces
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-repeatable
      aspects/make-returnable
      aspects/make-storable
      aspects/make-taggable
      aspects/make-visible 
      (t/attach-instruction , code->set)
      (t/attach-instruction , vector->set)
      (t/attach-instruction , set-difference)
      (t/attach-instruction , set-intersection)
      (t/attach-instruction , set-subset?)
      (t/attach-instruction , set-superset?)
      (t/attach-instruction , set-union)
      )))

