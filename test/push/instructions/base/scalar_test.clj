(ns push.instructions.base.scalar_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.definitions.quoted])
  (:use [push.type.item.scalar])
  )


;; conversions

(tabular
  (fact ":scalar->code code-quotes the top :scalar item to :exec"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar       '(92M)      :scalar->code         :exec        (list (push-quote 92M))
    :scalar       '()         :scalar->code         :code        '()
    )



(tabular
  (fact ":char->integer takes a :char value, and converts it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple
    :char    '(\0)           :char->integer      :exec          '(48)
    :char    '(\r)           :char->integer      :exec          '(114)
    :char    '(\newline)     :char->integer      :exec          '(10)
    :char    '(\uF021)       :char->integer      :exec          '(61473)
    ;; consumes arg
    :char    '(\0)           :char->integer      :char          '()
    ;; missing args
    :char    '()             :char->integer      :exec          '()
    :char    '()             :char->integer      :char          '())


;; predicates


(tabular
  (fact ":scalar-bigdec? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-bigdec?     :exec           '(true)
    :scalar     '(-2)       :scalar-bigdec?     :exec           '(false)
    :scalar     '(22/11)    :scalar-bigdec?     :exec           '(false)
    :scalar     '(3/7)      :scalar-bigdec?     :exec           '(false)
    :scalar     '(3N)       :scalar-bigdec?     :exec           '(false)
    :scalar     '(3.2)      :scalar-bigdec?     :exec           '(false)
    :scalar     '(3.2M)     :scalar-bigdec?     :exec           '(true)
    :scalar     '()         :scalar-bigdec?     :exec           '()
    )


(tabular
  (fact ":scalar-float? returns true if the number is EITHER a Clojure float or bigdec"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-float?        :exec           '(true)
    :scalar     '(-2)       :scalar-float?        :exec           '(false)
    :scalar     '(22/11)    :scalar-float?        :exec           '(false)
    :scalar     '(3/7)      :scalar-float?        :exec           '(false)
    :scalar     '(3N)       :scalar-float?        :exec           '(false)
    :scalar     '(3.2)      :scalar-float?        :exec           '(true)
    :scalar     '(3.2M)     :scalar-float?        :exec           '(true)
    :scalar     '()         :scalar-float?        :exec           '()
    )



(tabular
  (fact ":scalar-integer? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-integer?      :exec          '(false)
    :scalar     '(-2)       :scalar-integer?      :exec          '(true)
    :scalar     '(22/11)    :scalar-integer?      :exec          '(true)
    :scalar     '(3/7)      :scalar-integer?      :exec          '(false)
    :scalar     '(3N)       :scalar-integer?      :exec          '(true)
    :scalar     '(3.2)      :scalar-integer?      :exec          '(false)
    :scalar     '(3.2M)     :scalar-integer?      :exec          '(false)
    :scalar     '()         :scalar-integer?      :exec          '()
    )



(tabular
  (fact ":scalar-ratio? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-ratio?        :exec          '(false)
    :scalar     '(-2)       :scalar-ratio?        :exec          '(false)
    :scalar     '(3/1)      :scalar-ratio?        :exec          '(false)
    :scalar     '(22/11)    :scalar-ratio?        :exec          '(false)
    :scalar     '(3/7)      :scalar-ratio?        :exec          '(true)
    :scalar     '(3N)       :scalar-ratio?        :exec          '(false)
    :scalar     '(3.2)      :scalar-ratio?        :exec          '(false)
    :scalar     '(3.2M)     :scalar-ratio?        :exec          '(false)
    :scalar     '()         :scalar-ratio?        :exec          '()
    )
