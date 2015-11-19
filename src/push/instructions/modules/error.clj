(ns push.instructions.modules.error
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(def classic-error-module
  ( ->  (t/make-module  :error
                        :attributes #{:internal :base})
        t/make-visible
        ))

