(ns push.instructions.base.scalar_conversions_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )


(tabular
  (fact ":boolean->float returns 1.0 if the `:boolean` is `true`, 0.0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    :boolean    '(true)    :boolean->float    :scalar        '(1.0)
    :boolean    '(false)   :boolean->float    :scalar        '(0.0)
    :boolean    '()        :boolean->float    :scalar        '()
    )



(tabular
  (fact ":boolean->signedfloat returns 1.0 if the `:boolean` is `true`, -1.0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction          ?get-stack     ?expected
    :boolean    '(true)    :boolean->signedfloat    :scalar        '(1.0)
    :boolean    '(false)   :boolean->signedfloat    :scalar        '(-1.0)
    :boolean    '()        :boolean->signedfloat    :scalar        '()
    )



(tabular
  (fact ":boolean->integer returns 1 if the `:boolean` is `true`, 0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    :boolean    '(true)    :boolean->integer    :scalar        '(1)
    :boolean    '(false)   :boolean->integer    :scalar        '(0)
    :boolean    '()        :boolean->integer    :scalar        '()
    )



(tabular
  (fact ":boolean->signedint returns 1 if the `:boolean` is `true`, -1 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction          ?get-stack     ?expected
    :boolean    '(true)    :boolean->signedint    :scalar        '(1)
    :boolean    '(false)   :boolean->signedint    :scalar        '(-1)
    :boolean    '()        :boolean->signedint    :scalar        '()
    )