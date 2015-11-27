(ns push.types.modules.error
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.visible)
  )


(def classic-error-module
  ( ->  (t/make-module  :error
                        :attributes #{:internal :base})
        make-visible
        ))

