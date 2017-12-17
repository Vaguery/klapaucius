(ns push.type.item.interval
  (:require [push.instructions.dsl          :as d]
            [push.instructions.core         :as i]
            [push.util.numerics             :as n]
            [push.type.core                 :as t]
            [push.instructions.aspects      :as aspects]
            [push.type.definitions.interval :as interval]
            [push.type.definitions.tagspace :as ts]
            ))

(def interval-add
  (i/build-instruction
    interval-add
    "`:interval-add` pops the top two `:interval` items and pushes a new `:interval` which is the sum of the two. The result's min is the smallest sum of any of the four pairs of extremes, and its max is the largest of any pair. An extreme in the sum is open if either addend is open. If both arguments are empty, there is no result. If one is empty, the other is returned unchanged."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i2)
    (d/consume-top-of :interval :as :i1)
    (d/calculate [:i1 :i2] interval/interval-add :as :result)
    (d/return-item :result)))


(def interval-crossover
  (i/build-instruction
    interval-crossover
    "`:interval-crossover` pops the top two `:interval` items (call them `B` and `A`, respectively) and pushes a code block containing the four FOIL `:interval` items onto `:exec`. The results (in order) are `(make-interval (:min A) (:min B))`, `(make-interval (:min A) (:max B))`, `(make-interval (:max A) (:min B))` and `(make-interval (:max A) (:max B))`, and they preserve the openness of the points included the resulting intervals."
    :tags #{:interval}
    (d/consume-top-of :interval :as :B)
    (d/consume-top-of :interval :as :A)
    (d/calculate [:A :B]
      #(interval/make-interval
        (:min %1)
        (:min %2)
        :min-open? (:min-open? %1)
        :max-open? (:min-open? %2)) :as :first)
    (d/calculate [:A :B]
      #(interval/make-interval
        (:min %1)
        (:max %2)
        :min-open? (:min-open? %1)
        :max-open? (:max-open? %2)) :as :outer)
    (d/calculate [:A :B]
      #(interval/make-interval
        (:max %1)
        (:min %2)
        :min-open? (:max-open? %1)
        :max-open? (:min-open? %2)) :as :inner)
    (d/calculate [:A :B]
      #(interval/make-interval
        (:max %1)
        (:max %2)
        :min-open? (:max-open? %1)
        :max-open? (:max-open? %2)) :as :last)
    (d/calculate [:first :outer :inner :last] list :as :foil)
    (d/return-item :foil)
    ))




(def interval-divide
  (i/build-instruction
    interval-divide
    "`:interval-divide` pops the top two `:interval` items (call them `B` and `A`, respectively) and pushes a continuation that will calculate their quotient(s) `A÷B` onto `:exec` (by taking the reciprocal of B). If `B` strictly covers zero, then two continuations are pushed: one for the positive and one for the negative regions."
    :tags #{:interval}
    (d/consume-top-of :interval :as :divisor)
    (d/consume-top-of :interval :as :dividend)
    (d/calculate [:divisor] #(interval/interval-reciprocal %1) :as :inverses)
    (d/calculate [:dividend :inverses]
      #(if (seq? %2)
        (list %1 (first %2) :interval-multiply
              %1 (second %2) :interval-multiply)
        (list %1 %2 :interval-multiply)) :as :results)
    (d/return-item :results)
    ))





(def interval-emptyset?
  (i/build-instruction
    interval-emptyset?
    "`:interval-emptyset?` pops the top `:interval` item and pushes `true` if it's empty: that is, if the ends are equal, AND at least one end is open"
    :tags #{:interval}
    (d/consume-top-of :interval :as :arg)
    (d/calculate [:arg] #(interval/interval-emptyset? %1) :as :result)
    (d/return-item :result)
    ))




(def interval-hull
  (i/build-instruction
    interval-hull
    "`:interval-hull` pops the top two `:interval` items and pushes a new `:interval` whose extent is the hull of the arguments. That is, it reaches from the smallest `:min` to the largest `:max`, preserving inclusiveness of the ends."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i2)
    (d/consume-top-of :interval :as :i1)
    (d/calculate [:i1 :i2]
        #(interval/make-interval
            (min (:min %1) (:min %2))
            (max (:max %1) (:max %2))
            :min-open? (and (:min-open? %1) (:min-open? %2))
            :max-open? (and (:max-open? %1) (:max-open? %2))) :as :result)
    (d/return-item :result)
    ))




(def interval-include?
  (i/build-instruction
    interval-include?
    "`:interval-include?` pops the top `:interval` and top `:scalar`, and pushes `true` if the `:scalar` value falls (strictly!) within the `:interval`, taking openness of the ends into account"
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/consume-top-of :scalar :as :number)
    (d/calculate [:i :number] #(interval/interval-include? %1 %2) :as :result)
    (d/return-item :result)
    ))




(def interval-intersection
  (i/build-instruction
    interval-intersection
    "`:interval-intersection` pops the top two `:interval` items and pushes a new `:interval` whose extent is the intersection of the arguments, if it is non-empty. If there is no overlap (including a trivial empty `:interval` like `(3,3)`), no result is pushed."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i2)
    (d/consume-top-of :interval :as :i1)
    (d/calculate [:i1 :i2] #(interval/interval-intersection %1 %2) :as :result)
    (d/return-item :result)
    ))


(def interval-min
  (i/build-instruction
    interval-min
    "`:interval-min` pops the top `:interval` item and pushes its `:min` value to `:scalar`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i] :min :as :result)
    (d/return-item :result)))


(def interval-max
  (i/build-instruction
    interval-max
    "`:interval-max` pops the top `:interval` item and pushes its `:max` value to `:scalar`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i] :max :as :result)
    (d/return-item :result)))


(def interval-multiply
  (i/build-instruction
    interval-multiply
    "`:interval-multiply` pops the top two `:interval` items and pushes a new `:interval` which is the product of the two. If either `:min` (or `:max`) is open, the result `:min` (or `:max`) is also open."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i2)
    (d/consume-top-of :interval :as :i1)
    (d/calculate [:i1 :i2] #(interval/interval-multiply %1 %2) :as :result)
    (d/return-item :result)
    ))


(def interval-reflect
  (i/build-instruction
    interval-reflect
    "`:interval-reflect` pops the top `:interval` item and pushes a new one with the signs of the `:min` and `:max` reversed, and also the boundedness of the ends."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i] #(
      interval/make-interval
        (- (:max %1))
        (- (:min %1))
        :min-open? (:max-open? %1)
        :max-open? (:min-open? %1)) :as :result)
    (d/return-item :result)
    ))


(def interval-new
  (i/build-instruction
    interval-new
    "`:interval-new` pops the top two `:scalar` items (`B` and `A`, respectively) and creates a new `:interval` item [A,B]. Both ends are closed."
    :tags #{:interval}
    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :scalar :as :arg1)
    (d/calculate [:arg1 :arg2] #(interval/make-interval %1 %2) :as :result)
    (d/return-item :result)
    ))


(def interval-newopen
  (i/build-instruction
    interval-newopen
    "`:interval-newopen` pops the top two `:scalar` items (`B` and `A`, respectively) and creates a new `:interval` item `(A,B)`. Both ends are open."
    :tags #{:interval}
    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :scalar :as :arg1)
    (d/calculate [:arg1 :arg2] #(interval/make-open-interval %1 %2) :as :result)
    (d/return-item :result)
    ))


(def interval-overlap?
  (i/build-instruction
    interval-overlap?
    "`:interval-overlap?` pops the top two `:interval` items and pushes `true` if they overlap, even in a single point: that is, if their intersection is non-empty, taking into account which ends are open"
    :tags #{:interval}
    (d/consume-top-of :interval :as :arg2)
    (d/consume-top-of :interval :as :arg1)
    (d/calculate [:arg1 :arg2] #(interval/interval-overlap? %1 %2) :as :result)
    (d/return-item :result)
    ))


(def interval-rebracket
  (i/build-instruction
    interval-rebracket
    "`:interval-rebracket` pops the top `:interval` item and two `:boolean` values (`B` and `A`, respectively). The `:min-open?` value is set to `A`, the `:max-open?` value is set to `B`, and the resulting `:interval` is pushed as a result."
    :tags #{:interval}
    (d/consume-top-of :boolean :as :max?)
    (d/consume-top-of :boolean :as :min?)
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i :min? :max?]
        #(interval/make-interval
            (:min %1) (:max %1) :min-open? %2 :max-open? %3) :as :result)
    (d/return-item :result)
    ))


(def interval-recenter
  (i/build-instruction
    interval-recenter
    "`:interval-recenter` pops the top `:interval` item and pushes a new `:interval` with center at 0. If either bound of the argument is infinite, the result will be infinite in both directions."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i]
        #(if (or (n/infinite? (:min %1)) (n/infinite? (:max %1)))
            (interval/make-interval n/-∞ n/∞)
            (let [c (/ (-' (:max %1) (:min %1)) 2)]
              (interval/make-interval
                (- c)
                c
                :min-open? (:min-open? %1)
                :max-open? (:max-open? %1)))) :as :result)
    (d/return-item :result)))


(def interval-reciprocal
  (i/build-instruction
    interval-reciprocal
    "`:interval-reciprocal` pops the top `:interval` item and pushes its reciprocal to `:exec`. If the span strictly covers zero, then a code block containing _two_ spans is pushed."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/calculate [:i] #(interval/interval-reciprocal %1) :as :results)
    (d/return-item :results)
    ))



(def interval-scale
  (i/build-instruction
    interval-scale
    "`:interval-scale` pops the top `:interval` item and top `:scalar` item, and pushes a new `:interval` with the original `:max` and `:min` multiplied by the `:scalar`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/consume-top-of :scalar :as :factor)
    (d/calculate [:i :factor] #(
      interval/make-interval
        (*' %2 (:min %1))
        (*' %2 (:max %1))
        :min-open? (if (neg? %2) (:max-open? %1) (:min-open? %1))
        :max-open? (if (neg? %2) (:min-open? %1) (:max-open? %1))) :as :result)
    (d/return-item :result)
    ))




(def interval-shift
  (i/build-instruction
    interval-shift
    "`:interval-shift` pops the top `:interval` item and top `:scalar` item, and pushes a new `:interval` with the original `:max` and `:min` added to the `:scalar`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/consume-top-of :scalar :as :factor)
    (d/calculate [:i :factor] #(
      interval/make-interval
        (+' %2 (:min %1))
        (+' %2 (:max %1))
        :min-open? (:min-open? %1)
        :max-open? (:max-open? %1)) :as :result)
    (d/return-item :result)
    ))




(def interval-subset?
  (i/build-instruction
    interval-subset?
    "`:interval-subset?` pops the top two `:interval` items (call them B and A, respectively) and pushes `true` if B is a subset of A. That is, if both ends of B fall strictly within A, or they are identical."
    :tags #{:interval}
    (d/consume-top-of :interval :as :arg2)
    (d/consume-top-of :interval :as :arg1)
    (d/calculate [:arg1 :arg2] #(interval/interval-subset? %1 %2) :as :result)
    (d/return-item :result)
    ))




(def interval-subtract
  (i/build-instruction
    interval-subtract
    "`:interval-subtract` pops the top two `:interval` items (call them `B` and `A` respectively) and pushes a continuation form onto `:exec` that will calculate the sum of `A` plus the negative of `B`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :b)
    (d/consume-top-of :interval :as :a)
    (d/calculate [:a :b]
      #(list %1 (interval/interval-negate %2) :interval-add) :as :result)
    (d/return-item :result)))




(def interval-union
  (i/build-instruction
    interval-union
    "`:interval-union` pops the top two `:interval` items and pushes a list that includes the one or two `:interval` items that are the union of the arguments. The resulting code block is pushed to `:exec`. If they overlap or are \"snug\", the result is one; if they do not overlap, the result contains both arguments."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i2)
    (d/consume-top-of :interval :as :i1)
    (d/calculate [:i1 :i2] #(interval/interval-union %1 %2) :as :result)
    (d/return-item :result)
    ))



;;; COLLECTION FILTERS


(def scalars-filter
  (i/build-instruction
    scalars-filter
    "`:scalars-filter` pops the top `:scalars` vector and top `:interval` item, and pushes a new `:scalars` item that only contains values falling (strictly) within the `:interval`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :allowed)
    (d/consume-top-of :scalars :as :vector)
    (d/calculate [:vector :allowed]
        #(filterv (fn [n] (interval/interval-include? %2 n)) %1) :as :result)
    (d/return-item :result)
    ))




(def scalars-remove
  (i/build-instruction
    scalars-remove
    "`:scalars-remove` pops the top `:scalars` vector and top `:interval` item, and pushes a new `:scalars` item that does not contain any values falling (strictly) within the `:interval`."
    :tags #{:interval}
    (d/consume-top-of :interval :as :allowed)
    (d/consume-top-of :scalars :as :vector)
    (d/calculate [:vector :allowed]
        #(vec
          (remove (fn [n] (interval/interval-include? %2 n)) %1)) :as :result)
    (d/return-item :result)
    ))



(def scalars-split
  (i/build-instruction
    scalars-split
    "`:scalars-split` pops the top `:scalars` vector and top `:interval` item, and pushes a code block containing two new `:scalars` items onto `:exec`. The first item will contain all elements falling (strictly) within the `:interval`, the second all other elements."
    :tags #{:interval}
    (d/consume-top-of :interval :as :i)
    (d/consume-top-of :scalars :as :vector)
    (d/calculate [:vector :i]
        #(list
          (filterv (fn [n] (interval/interval-include? %2 n)) %1)
          (vec
            (remove (fn [n] (interval/interval-include? %2 n)) %1))) :as :result)
    (d/return-item :result)
    ))





(def tagspace-filter
  (i/build-instruction
    tagspace-filter
    "`:tagspace-filter` pops the top `:tagspace` vector and top `:interval` item, and pushes a new `:tagspace` item that contains all keys falling (strictly) within the `:interval`; the removed keys are forgotten with their values."
    :tags #{:interval}
    (d/consume-top-of :interval :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(ts/make-tagspace
        (filter
          (fn [kv] (interval/interval-include? %2 (first kv)))
          (seq (:contents %1)))) :as :result)
    (d/return-item :result)
    ))




(def tagspace-remove
  (i/build-instruction
    tagspace-remove
    "`:tagspace-remove` pops the top `:tagspace` vector and top `:interval` item, and pushes a new `:tagspace` item that does not contain any keys falling (strictly) within the `:interval`; the removed keys are forgotten with their values."
    :tags #{:interval}
    (d/consume-top-of :interval :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(ts/make-tagspace
        (remove
          (fn [kv] (interval/interval-include? %2 (first kv)))
          (seq (:contents %1)))) :as :result)
    (d/return-item :result)
    ))




(def tagspace-split
  (i/build-instruction
    tagspace-split
    "`:tagspace-split` pops the top `:tagspace` vector and top `:interval` item, and pushes a new code block containing two `:tagspace` items. The first contains all items from the original with keys falling (strictly) within the `:interval`; the second contains the remaining key-value pairs."
    :tags #{:interval}
    (d/consume-top-of :interval :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(list
        (ts/make-tagspace
          (filter
            (fn [kv] (interval/interval-include? %2 (first kv)))
            (seq (:contents %1))))
        (ts/make-tagspace
          (remove
            (fn [kv] (interval/interval-include? %2 (first kv)))
            (seq (:contents %1))))) :as :result)
    (d/return-item :result)
    ))




(def interval-type
  (-> (t/make-type  :interval
                  :recognized-by interval/interval?
                  :attributes #{:numeric :set}
                  :manifest {:min       :scalar
                             :max       :scalar
                             :min-open? :boolean
                             :max-open? :boolean}
                  :builder #(interval/make-interval %1 %2 :min-open? %3 :max-open? %4)
                )
        aspects/make-buildable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-set-able
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , interval-add)
        (t/attach-instruction , interval-crossover)
        (t/attach-instruction , interval-divide)
        (t/attach-instruction , interval-emptyset?)
        (t/attach-instruction , interval-hull)
        (t/attach-instruction , interval-include?)
        (t/attach-instruction , interval-intersection)
        (t/attach-instruction , interval-max)
        (t/attach-instruction , interval-min)
        (t/attach-instruction , interval-multiply)
        (t/attach-instruction , interval-new)
        (t/attach-instruction , interval-newopen)
        (t/attach-instruction , interval-overlap?)
        (t/attach-instruction , interval-rebracket)
        (t/attach-instruction , interval-recenter)
        (t/attach-instruction , interval-reciprocal)
        (t/attach-instruction , interval-reflect)
        (t/attach-instruction , interval-scale)
        (t/attach-instruction , interval-shift)
        (t/attach-instruction , interval-subset?)
        (t/attach-instruction , interval-subtract)
        (t/attach-instruction , interval-union)
        (t/attach-instruction , scalars-filter)
        (t/attach-instruction , scalars-remove)
        (t/attach-instruction , scalars-split)
        (t/attach-instruction , tagspace-filter)
        (t/attach-instruction , tagspace-remove)
        (t/attach-instruction , tagspace-split)
  ))
