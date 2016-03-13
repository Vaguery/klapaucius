(ns push.types.module.log
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))


(def log-module
  ( ->  (t/make-module  :log
                        :attributes #{:internal :base})
        aspects/make-visible
        ))

