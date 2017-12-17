(ns push.type.item.quoted
  (:require [push.type.definitions.quoted :as q :refer [quoted-code?]]
            [push.router.core :as r]
            [push.type.core :as t]
            ))



(def quoted-type
  (t/make-type  :quoted
                :router (r/make-router :quoted
                                       :recognizer q/quoted-code?
                                       :preprocessor :value
                                       :target-stack :code)
                :attributes #{}
                :instructions {}
                ))
