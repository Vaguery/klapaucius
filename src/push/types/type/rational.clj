(ns push.types.type.rational
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math]
            ))



;; INSTRUCTIONS



; ;; CONVERSIONS



(def rational-type
  ( ->  (t/make-type  :rational
                      :recognized-by rational?
                      :attributes #{:numeric})
        aspects/make-comparable
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

