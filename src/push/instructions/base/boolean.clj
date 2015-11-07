(ns push.instructions.base.boolean
  (:require [push.instructions.core :as core])
  (:use [push.instructions.dsl]))


(def boolean-and
  (core/build-instruction
    boolean-and
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(and %1 %2) :as :both)
    (push-onto :boolean :both)))


(def boolean-not
  (core/build-instruction
    boolean-not
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (calculate [:arg1] #(not %1) :as :nope)
    (push-onto :boolean :nope)))


(def boolean-or
  (core/build-instruction
    boolean-or
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(or %1 %2) :as :either)
    (push-onto :boolean :either)))


(def boolean-xor
  (core/build-instruction
    boolean-or
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(or (and %1 (not %2)) (and %2 (not %1))) :as :one)
    (push-onto :boolean :one)))

