(ns push.type.module.error
  (:require [push.type.core            :as t]
            [push.instructions.aspects :as aspects]
            ))


(def error-module
  (aspects/make-visible
    (t/make-module :error
                   :attributes #{:internal :base})))
