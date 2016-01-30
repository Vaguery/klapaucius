(ns push.instructions.extra.numeric-functions
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )




;;;;;;;;;;;;;;;;;


(def numeric-functions-module
  ( ->  (t/make-module  :numeric-functions
                        :attributes #{:numeric})

        ))
