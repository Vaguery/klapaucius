(ns push.types.modules.print
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects])
  )


(def print-newline
  (core/build-instruction
    print-newline
    "`:print-newline` pushes a single newline character to the `:print` stack"
    :tags #{:print :io :base}
    (d/calculate [] (fn [] \newline) :as :newline)
    (d/push-onto :print :newline)))


(def print-space
  (core/build-instruction
    print-space
    "`:print-space` pushes a single space character to the `:print` stack"
    :tags #{:print :io :base}
    (d/calculate [] (fn [] \space) :as :space)
    (d/push-onto :print :space)))


(def print-module
  ( ->  (t/make-module  :print
                        :attributes #{:io :base})
        aspects/make-visible
        (t/attach-instruction print-newline)
        (t/attach-instruction print-space)
        ))

