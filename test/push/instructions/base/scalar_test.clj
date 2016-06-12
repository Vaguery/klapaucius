(ns push.instructions.base.scalar_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )


;; quotable

(tabular
  (fact ":scalar->code move the top :scalar item to :code"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; move it!
    :scalar       '(92M)      :scalar->code         :code        '(92M)
    :scalar       '()         :scalar->code         :code        '()
    )

