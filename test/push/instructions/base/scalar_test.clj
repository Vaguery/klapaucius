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


;; predicates


(tabular
  (fact ":scalar-bigdec? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-bigdec?     :boolean        '(true)
    :scalar     '(-2)       :scalar-bigdec?     :boolean        '(false)
    :scalar     '(22/11)    :scalar-bigdec?     :boolean        '(false)
    :scalar     '(3/7)      :scalar-bigdec?     :boolean        '(false)
    :scalar     '(3N)       :scalar-bigdec?     :boolean        '(false)
    :scalar     '(3.2)      :scalar-bigdec?     :boolean        '(false)
    :scalar     '(3.2M)     :scalar-bigdec?     :boolean        '(true)
    :scalar     '()         :scalar-bigdec?     :boolean        '()
    )


(tabular
  (fact ":scalar-float? returns true if the number is EITHER a Clojure float or bigdec"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-float?        :boolean        '(true)
    :scalar     '(-2)       :scalar-float?        :boolean        '(false)
    :scalar     '(22/11)    :scalar-float?        :boolean        '(false)
    :scalar     '(3/7)      :scalar-float?        :boolean        '(false)
    :scalar     '(3N)       :scalar-float?        :boolean        '(false)
    :scalar     '(3.2)      :scalar-float?        :boolean        '(true)
    :scalar     '(3.2M)     :scalar-float?        :boolean        '(true)
    :scalar     '()         :scalar-float?        :boolean        '()
    )



(tabular
  (fact ":scalar-integer? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-integer?     :boolean        '(false)
    :scalar     '(-2)       :scalar-integer?     :boolean        '(true)
    :scalar     '(22/11)    :scalar-integer?     :boolean        '(true)
    :scalar     '(3/7)      :scalar-integer?     :boolean        '(false)
    :scalar     '(3N)       :scalar-integer?     :boolean        '(true)
    :scalar     '(3.2)      :scalar-integer?     :boolean        '(false)
    :scalar     '(3.2M)     :scalar-integer?     :boolean        '(false)
    :scalar     '()         :scalar-integer?     :boolean        '()
    )



(tabular
  (fact ":scalar-ratio? is the obvious predicate"
    (register-type-and-check-instruction
        ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    :scalar     '(92M)      :scalar-ratio?     :boolean        '(false)
    :scalar     '(-2)       :scalar-ratio?     :boolean        '(false)
    :scalar     '(3/1)      :scalar-ratio?     :boolean        '(false)
    :scalar     '(22/11)    :scalar-ratio?     :boolean        '(false)
    :scalar     '(3/7)      :scalar-ratio?     :boolean        '(true)
    :scalar     '(3N)       :scalar-ratio?     :boolean        '(false)
    :scalar     '(3.2)      :scalar-ratio?     :boolean        '(false)
    :scalar     '(3.2M)     :scalar-ratio?     :boolean        '(false)
    :scalar     '()         :scalar-ratio?     :boolean        '()
    )


