(ns push.type.item.interval
  (:use     [push.instructions.dsl]
            [push.type.core]
            [push.instructions.core])
  (:require [push.instructions.aspects :as aspects]
            [push.type.definitions.interval :as interval]
            ))



(def interval-add
  (build-instruction
    interval-add
    "`:interval-add` pops the top two `:interval` items and pushes a new `:interval` which is the sum of the two. If either `:min` (or `:max`) is open, the result `:min` (or `:max`) is also open."
    :tags #{:interval}
    (consume-top-of :interval :as :i2)
    (consume-top-of :interval :as :i1)
    (calculate [:i1 :i2]
        #(interval/make-interval 
            (+' (:min %1) (:min %2))
            (+' (:max %1) (:max %2))
            :min-open? (or (:min-open? %1) (:min-open? %2))
            :max-open? (or (:max-open? %1) (:max-open? %2))) :as :result)
    (push-onto :interval :result)))





(def interval-crossover
  (build-instruction
    interval-crossover
    "`:interval-crossover` pops the top two `:interval` items (call them `B` and `A`, respectively) and pushes a code block containing the four FOIL `:interval` items onto `:exec`. The results (in order) are `(make-interval (:min A) (:min B))`, `(make-interval (:min A) (:max B))`, `(make-interval (:max A) (:min B))` and `(make-interval (:max A) (:max B))`, and they preserve the openness of the points included the resulting intervals."
    :tags #{:interval}
    (consume-top-of :interval :as :B)
    (consume-top-of :interval :as :A)
    (calculate [:A :B]
      #(interval/make-interval
        (:min %1)
        (:min %2)
        :min-open? (:min-open? %1)
        :max-open? (:min-open? %2)) :as :first)
    (calculate [:A :B]
      #(interval/make-interval
        (:min %1)
        (:max %2)
        :min-open? (:min-open? %1)
        :max-open? (:max-open? %2)) :as :outer)
    (calculate [:A :B]
      #(interval/make-interval
        (:max %1)
        (:min %2)
        :min-open? (:max-open? %1)
        :max-open? (:min-open? %2)) :as :inner)
    (calculate [:A :B]
      #(interval/make-interval
        (:max %1)
        (:max %2)
        :min-open? (:max-open? %1)
        :max-open? (:max-open? %2)) :as :last)
    (calculate [:first :outer :inner :last] #(list %1 %2 %3 %4) :as :foil)
    (push-onto :exec :foil)
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




(def interval-hull
  (build-instruction
    interval-hull
    "`:interval-hull` pops the top two `:interval` items and pushes a new `:interval` whose extent is the hull of the arguments. That is, it reaches from the smallest `:min` to the largest `:max`, preserving inclusiveness of the ends."
    :tags #{:interval}
    (consume-top-of :interval :as :i2)
    (consume-top-of :interval :as :i1)
    (calculate [:i1 :i2]
        #(interval/make-interval 
            (min (:min %1) (:min %2))
            (max (:max %1) (:max %2))
            :min-open? (and (:min-open? %1) (:min-open? %2))
            :max-open? (and (:max-open? %1) (:max-open? %2))) :as :result)
    (push-onto :interval :result)
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




(def interval-intersection
  (build-instruction
    interval-intersection
    "`:interval-intersection` pops the top two `:interval` items and pushes a new `:interval` whose extent is the intersection of the arguments, if it is non-empty. If there is no overlap (including a trivial empty `:interval` like `(3,3)`), no result is pushed."
    :tags #{:interval}
    (consume-top-of :interval :as :i2)
    (consume-top-of :interval :as :i1)
    (calculate [:i1 :i2] #(interval/interval-intersection %1 %2) :as :result)
    (push-onto :interval :result)
    ))




(def interval-new
  (build-instruction
    interval-new
    "`:interval-new` pops the top two `:scalar` items (`B` and `A`, respectively) and creates a new `:interval` item [A,B]. Both ends are closed."
    :tags #{:interval}
    (consume-top-of :scalar :as :arg2)
    (consume-top-of :scalar :as :arg1)
    (calculate [:arg1 :arg2] #(interval/make-interval %1 %2) :as :result)
    (push-onto :interval :result)
    ))



(def interval-newopen
  (build-instruction
    interval-newopen
    "`:interval-newopen` pops the top two `:scalar` items (`B` and `A`, respectively) and creates a new `:interval` item `(A,B)`. Both ends are open."
    :tags #{:interval}
    (consume-top-of :scalar :as :arg2)
    (consume-top-of :scalar :as :arg1)
    (calculate [:arg1 :arg2] #(interval/make-open-interval %1 %2) :as :result)
    (push-onto :interval :result)
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




(def interval-rebracket
  (build-instruction
    interval-rebracket
    "`:interval-rebracket` pops the top `:scalar` item and two `:boolean` values (`B` and `A`, respectively). The `:min-open?` value is set to `A`, the `:max-open?` value is set to `B`, and the resulting `:interval` is pushed as a result."
    :tags #{:interval}
    (consume-top-of :boolean :as :max?)
    (consume-top-of :boolean :as :min?)
    (consume-top-of :interval :as :i)
    (calculate [:i :min? :max?]
        #(interval/make-interval 
            (:min %1) (:max %1) :min-open? %2 :max-open? %3) :as :result)
    (push-onto :interval :result)
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




(def interval-subtract
  (build-instruction
    interval-subtract
    "`:interval-subtract` pops the top two `:interval` items and pushes a new `:interval` which is the difference of the two. If either `:min` (or `:max`) is open, the result `:min` (or `:max`) is also open."
    :tags #{:interval}
    (consume-top-of :interval :as :i2)
    (consume-top-of :interval :as :i1)
    (calculate [:i1 :i2]
        #(interval/make-interval 
            (-' (:min %1) (:max %2))
            (-' (:max %1) (:min %2))
            :min-open? (or (:min-open? %1) (:max-open? %2))
            :max-open? (or (:max-open? %1) (:min-open? %2))) :as :result)
    (push-onto :interval :result)))






(def interval-union
  (build-instruction
    interval-union
    "`:interval-union` pops the top two `:interval` items and pushes a list that includes the one or two `:interval` items that are the union of the arguments. The resulting code block is pushed to `:exec`. If they overlap or are \"snug\", the result is one; if they do not overlap, the result contains both arguments."
    :tags #{:interval}
    (consume-top-of :interval :as :i2)
    (consume-top-of :interval :as :i1)
    (calculate [:i1 :i2] #(interval/interval-union %1 %2) :as :result)
    (push-onto :exec :result)
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
        (attach-instruction , interval-add)
        (attach-instruction , interval-crossover)
        (attach-instruction , interval-empty?)
        (attach-instruction , interval-hull)
        (attach-instruction , interval-include?)
        (attach-instruction , interval-intersection)
        (attach-instruction , interval-new)
        (attach-instruction , interval-newopen)
        (attach-instruction , interval-rebracket)
        (attach-instruction , interval-overlap?)
        (attach-instruction , interval-subset?)
        (attach-instruction , interval-subtract)
        (attach-instruction , interval-union)
  ))

