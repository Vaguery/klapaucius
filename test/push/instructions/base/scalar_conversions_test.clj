(ns push.instructions.base.scalar_conversions_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.scalar])
  )


(tabular
  (fact ":boolean->float returns 1.0 if the `:boolean` is `true`, 0.0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    :boolean    '(true)    :boolean->float    :exec          '(1.0)
    :boolean    '(false)   :boolean->float    :exec          '(0.0)
    :boolean    '()        :boolean->float    :exec          '()
    )



(tabular
  (fact ":boolean->signedfloat returns 1.0 if the `:boolean` is `true`, -1.0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction          ?get-stack     ?expected
    :boolean    '(true)    :boolean->signedfloat    :exec          '(1.0)
    :boolean    '(false)   :boolean->signedfloat    :exec          '(-1.0)
    :boolean    '()        :boolean->signedfloat    :exec          '()
    )



(tabular
  (fact ":boolean->integer returns 1 if the `:boolean` is `true`, 0 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    :boolean    '(true)    :boolean->integer    :exec          '(1)
    :boolean    '(false)   :boolean->integer    :exec          '(0)
    :boolean    '()        :boolean->integer    :exec          '()
    )



(tabular
  (fact ":boolean->signedint returns 1 if the `:boolean` is `true`, -1 if `false`"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction          ?get-stack     ?expected
    :boolean    '(true)    :boolean->signedint    :exec          '(1)
    :boolean    '(false)   :boolean->signedint    :exec          '(-1)
    :boolean    '()        :boolean->signedint    :exec          '()
    )
