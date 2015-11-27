(ns push.instructions.modules.print
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.visible)
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


(def classic-print-module
  ( ->  (t/make-module  :print
                        :attributes #{:io :base})
        make-visible
        (t/attach-instruction print-newline)
        (t/attach-instruction print-space)
        ))

