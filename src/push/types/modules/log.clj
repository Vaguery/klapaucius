(ns push.types.modules.log
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )


(def log-module
  ( ->  (t/make-module  :log
                        :attributes #{:internal :base})
        aspects/make-visible
        ))

