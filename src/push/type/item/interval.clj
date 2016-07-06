(ns push.type.item.interval
  (:use     [push.instructions.dsl]
            [push.type.core]
            [push.instructions.core])
  (:require [push.instructions.aspects :as aspects]
            [push.type.definitions.interval :as interval]
            ))




(def interval-empty?
  (build-instruction
    interval-empty?
    "`:interval-empty?` pops the top `:interval` item and pushes `true` if it's empty: that is, if the ends are equal, AND at least one end is open"
    :tags #{:interval}
    (consume-top-of :interval :as :arg)
    (calculate [:arg] #(interval/interval-empty? %1) :as :result)
    (push-onto :boolean :result)
    ))





(def interval-include?
  (build-instruction
    interval-include?
    "`:interval-include?` pops the top `:interval` and top `:scalar`, and pushes `true` if the `:scalar` value falls (strictly!) within the `:interval`, taking openness of the ends into account"
    :tags #{:interval}
    (consume-top-of :interval :as :i)
    (consume-top-of :scalar :as :number)
    (calculate [:i :number] #(interval/interval-include? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))




(def interval-overlap?
  (build-instruction
    interval-overlap?
    "`:interval-overlap?` pops the top two `:interval` items and pushes `true` if they overlap, even in a single point: that is, if their union is non-empty, taking into account which ends are open"
    :tags #{:interval}
    (consume-top-of :interval :as :arg2)
    (consume-top-of :interval :as :arg1)
    (calculate [:arg1 :arg2] #(interval/interval-overlap? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))





(def interval-subset?
  (build-instruction
    interval-subset?
    "`:interval-subset?` pops the top two `:interval` items (call them B and A, respectively) and pushes `true` if B is a subset of A. That is, if both ends of B fall strictly within A, or they are identical."
    :tags #{:interval}
    (consume-top-of :interval :as :arg2)
    (consume-top-of :interval :as :arg1)
    (calculate [:arg1 :arg2] #(interval/interval-subset? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))





(def interval-type
  (-> (make-type  :interval
                  :recognized-by interval/interval?
                  :attributes #{:numeric :set})
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
        (attach-instruction , interval-empty?)
        (attach-instruction , interval-include?)
        (attach-instruction , interval-overlap?)
        (attach-instruction , interval-subset?)
  ))

