(ns push.type.module.log
  (:require [push.type.core            :as t]
            [push.instructions.aspects :as aspects]
            ))


(def log-module
  (aspects/make-visible
    (t/make-module :log
                   :attributes #{:internal :base})))
