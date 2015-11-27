(ns push.types.modules.log
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.visible)
  )


(def classic-log-module
  ( ->  (t/make-module  :log
                        :attributes #{:internal :base})
        make-visible
        ))

