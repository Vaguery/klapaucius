(ns push.type.item.span
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.router.core :as r]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))





(def span-type
  (-> (t/make-type  :span
                    :recognized-by push.type.definitions.complex/complex?
                    :attributes #{:numeric :set :continuous})
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
  ))