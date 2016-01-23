(ns push.types.modules.error
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.aspects :as aspects])
  )


(def error-module
  ( ->  (t/make-module  :error
                        :attributes #{:internal :base})
        aspects/make-visible
        ))

