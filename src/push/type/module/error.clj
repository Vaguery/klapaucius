(ns push.type.module.error
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            ))


(def error-module
  (aspects/make-visible
    (t/make-module :error
                   :attributes #{:internal :base})))
