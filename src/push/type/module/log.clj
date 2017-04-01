(ns push.type.module.log
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))


(def log-module
  (aspects/make-visible
    (t/make-module :log
                   :attributes #{:internal :base})))
