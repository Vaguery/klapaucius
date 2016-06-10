(ns push.types.type.quoted
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            ))


;; QuotedCode items


(defrecord QuotedCode [value])


(defn push-quote
  "takes any Push item (single literal, keyword, or code block) and returns a QuotedCode Push item with that value"
  [item]
  (->QuotedCode item))


(defn quoted-code?
  "a type checker that returns true if the argument is a QuotedCode record"
  [item]
  (= push.types.type.quoted.QuotedCode (class item)))


(def quoted-type
  ( ->  (t/make-type  :quoted
                      :recognized-by push.types.type.quoted/quoted-code?
                      :attributes #{})

        ))

