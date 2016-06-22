(ns push.type.item.quoted
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.router.core :as r]
            [push.instructions.dsl :as d]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            )
  (:use push.type.definitions.quoted))
  


(def quoted-type
  (t/make-type  :quoted
                    :router (r/make-router :quoted
                                           :recognizer quoted-code?
                                           :preprocessor :value
                                           :target-stack :code)
                    :attributes #{}
                    :instructions {}
  ))

