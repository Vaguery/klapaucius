(ns push.type.item.span
  (:use     [push.instructions.dsl]
            [push.type.core]
            [push.instructions.core])
  (:require [push.instructions.aspects :as aspects]
            [push.type.definitions.span :as span]
            ))


(def span-coincide?
  (build-instruction
    span-coincide?
    "`:span-coincide?` pops the top two `:span` items and pushes `true` if they coincide: that is, if they are equal or one is the reverse of the other (including open ends)"
    :tags #{:span}
    (consume-top-of :span :as :arg2)
    (consume-top-of :span :as :arg1)
    (calculate [:arg1 :arg2] #(span/span-coincide? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))




(def span-direction
  (build-instruction
    span-direction
    "`:span-direction` pops the top `:span` item and pushes -1 if the :start is greater than the :end, 0 if they're identical, or 1 if the :start is less than the :end"
    :tags #{:span}
    (consume-top-of :span :as :arg)
    (calculate [:arg] #(compare (:end %1) (:start %1)) :as :result)
    (push-onto :scalar :result)
    ))




(def span-empty?
  (build-instruction
    span-empty?
    "`:span-empty?` pops the top `:span` item and pushes `true` if it's empty: that is, if the ends are equal, AND at least one end is open"
    :tags #{:span}
    (consume-top-of :span :as :arg)
    (calculate [:arg] #(span/span-empty? %1) :as :result)
    (push-onto :boolean :result)
    ))





(def span-include?
  (build-instruction
    span-include?
    "`:span-include?` pops the top `:span` and top `:scalar`, and pushes `true` if the `:scalar` value falls (strictly!) within the `:span`, taking openness of the ends into account"
    :tags #{:span}
    (consume-top-of :span :as :span)
    (consume-top-of :scalar :as :number)
    (calculate [:span :number] #(span/span-include? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))




(def span-reverse
  (build-instruction
    span-reverse
    "`:span-reverse` pops the top `:span` item and pushes its reverse, including the openness states of its endpoints"
    :tags #{:span}
    (consume-top-of :span :as :arg)
    (calculate [:arg] #(span/span-reverse %1) :as :result)
    (push-onto :span :result)
    ))




(def span-surrounds?
  (build-instruction
    span-surrounds?
    "`:span-surrounds?` pops the top two `:span` items and pushes `true` if the first one surrounds the second one: that is, if both ends of the second one fall strictly within the first span"
    :tags #{:span}
    (consume-top-of :span :as :arg2)
    (consume-top-of :span :as :arg1)
    (calculate [:arg1 :arg2] #(span/span-surrounds? %1 %2) :as :result)
    (push-onto :boolean :result)
    ))





(def span-type
  (-> (make-type  :span
                  :recognized-by push.type.definitions.span/span?
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
        (attach-instruction , span-coincide?)
        (attach-instruction , span-direction)
        (attach-instruction , span-empty?)
        (attach-instruction , span-include?)
        (attach-instruction , span-reverse)
        (attach-instruction , span-surrounds?)
  ))

