(ns push.instructions.base.scalar_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )


;; conversions

(tabular
  (fact ":scalar->code move the top :scalar item to :code"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; move it!
    :scalar       '(92M)      :scalar->code         :code        '(92M)
    :scalar       '()         :scalar->code         :code        '()
    )



(tabular
  (fact ":char->integer takes a :char value, and converts it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :char    '(\0)           :char->integer      :scalar        '(48)
    :char    '(\r)           :char->integer      :scalar        '(114)
    :char    '(\newline)     :char->integer      :scalar        '(10)
    :char    '(\uF021)       :char->integer      :scalar        '(61473)
    ;; consumes arg
    :char    '(\0)           :char->integer      :char          '()
    ;; missing args 
    :char    '()             :char->integer      :scalar        '()
    :char    '()             :char->integer      :char          '())


