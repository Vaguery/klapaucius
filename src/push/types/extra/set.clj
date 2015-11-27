(ns push.types.extra.set
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.equatable)
  (:use push.instructions.aspects.movable)
  (:use push.instructions.aspects.printable)
  (:use push.instructions.aspects.returnable)
  (:use push.instructions.aspects.visible)
  (:require [clojure.set :as sets])
  )


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
    "`:set-subset?` pops the top two `:float` values (call them `A` and `B`, respectively). Pushes `true` if `B` is a subset of `A`, `false` otherwise."
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
                    :recognizer set?
                    :attributes #{:collection :set})
      make-visible 
      make-equatable
      make-movable
      make-printable
      make-returnable
      (t/attach-instruction , set-difference)
      (t/attach-instruction , set-intersection)
      (t/attach-instruction , set-subset?)
      (t/attach-instruction , set-superset?)
      (t/attach-instruction , set-union)
      )))

