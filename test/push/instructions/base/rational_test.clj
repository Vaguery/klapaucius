(ns push.instructions.base.rational_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.rational])
  )


;; quotable

(tabular
  (fact ":rational->code move the top :rational item to :code"
    (register-type-and-check-instruction
        ?set-stack ?items rational-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; move it!
    :rational       '(3/17)      :rational->code         :code        '(3/17)
    :rational       '()          :rational->code         :code        '()
    )


;; rational-specific instructions

