(ns push.types.type.complex
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.router.core :as r]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.types.definitions.complex :as cpx]
            ))










(def complex-type
  (-> (t/make-type  :complex
                    :recognized-by cpx/complex?
                    :attributes #{:numeric})
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

