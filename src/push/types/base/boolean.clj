(ns push.types.base.boolean
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:use [push.instructions.dsl])
  (:use [push.util.type-checkers :only (boolean?)])
  )

(def bool-and
  (core/build-instruction
    boolean-and
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(and %1 %2) :as :both)
    (push-onto :boolean :both)))


(def bool-not
  (core/build-instruction
    boolean-not
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (calculate [:arg1] #(not %1) :as :nope)
    (push-onto :boolean :nope)))


(def bool-or
  (core/build-instruction
    boolean-or
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(or %1 %2) :as :either)
    (push-onto :boolean :either)))


(def bool-xor
  (core/build-instruction
    boolean-xor
    :tags #{:logic :base}
    (consume-top-of :boolean :as :arg1)
    (consume-top-of :boolean :as :arg2)
    (calculate [:arg1 :arg2] #(or (and %1 (not %2)) (and %2 (not %1))) :as :one)
    (push-onto :boolean :one)))




(def classic-boolean-type
  ( ->  (t/make-type  :boolean
                      :recognizer boolean?
                      :attributes #{:logical})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , bool-and)
        (t/attach-instruction , bool-or)
        (t/attach-instruction , bool-not)
        (t/attach-instruction , bool-xor)
        ))

