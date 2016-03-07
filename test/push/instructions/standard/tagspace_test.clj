(ns push.instructions.standard.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.extra.tagspace])
  )



(tabular
  (fact ":tagspace-new pushes a new tagspace"
    (register-type-and-check-instruction
        ?set-stack ?items tagspace-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction    ?get-stack     ?expected
    :tagspace   '()         :tagspace-new    :tagspace    (list (make-tagspace)))

