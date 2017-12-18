(ns push.type.module.print
  (:require [push.instructions.dsl :as d]
            [push.instructions.core :as i]
            [push.type.core :as t]
            [push.instructions.aspects :as aspects]
            ))


;; INSTRUCTIONS


(def print-newline
  (i/build-instruction
    print-newline
    "`:print-newline` pushes a single newline character to the `:print` stack"

    (d/calculate [] (fn [] "\n") :as :newline)
    (d/push-onto :print :newline)
    ))



(def print-space
  (i/build-instruction
    print-space
    "`:print-space` pushes a single space character to the `:print` stack"

    (d/calculate [] (fn [] " ") :as :space)
    (d/push-onto :print :space)
    ))



(def print-module
  ( ->  (t/make-module  :print
                        :attributes #{:io :base})
        aspects/make-visible
        (t/attach-instruction print-newline)
        (t/attach-instruction print-space)
        ))
