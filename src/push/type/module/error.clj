(ns push.type.module.error
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))


(def error-module
  ( ->  (t/make-module  :error
                        :attributes #{:internal :base})
        aspects/make-visible
        ))

