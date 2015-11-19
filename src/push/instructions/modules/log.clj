(ns push.instructions.modules.log
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(def classic-log-module
  ( ->  (t/make-module  :log
                        :attributes #{:internal :base})
        t/make-visible
        ))

