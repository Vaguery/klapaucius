(ns push.instructions.base.integer_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.exotics])
  (:use [push.types.type.integer])
  )



;; integer-specific instructions


(tabular
  (fact ":integer-digits pushes a vector of digits"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(1182)     :integer-digits   :exec      '((1 1 8 2))
    :integer    '(-39812)   :integer-digits   :exec      '((3 9 8 1 2))
    :integer    '()         :integer-digits   :exec      '()
    )


(tabular
  (fact ":integer->numerals pushes a vector of numeric :chars"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(1182)     :integer->numerals   :chars      '([\1 \1 \8 \2])
    :integer    '(-39812M)  :integer->numerals   :chars      '([\3 \9 \8 \1 \2])
    :integer    '(1191N)    :integer->numerals   :chars      '([\1 \1 \9 \1])
    )


(tabular
  (fact ":integer->bits pushes a vector of numeric :booleans"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction     ?get-stack     ?expected
    :integer    '(0)       :integer->bits   :booleans      '([false false])
    :integer    '(1)       :integer->bits   :booleans      '([true false])
    :integer    '(3)       :integer->bits   :booleans      '([true true])
    :integer    '(127)     :integer->bits   :booleans      '([true true true true
                                                              true true true])
    :integer    '(128)     :integer->bits   :booleans      '([false false false false
                                                              false false false true])
    :integer    '(-128)    :integer->bits   :booleans      '([false false false false
                                                              false false false true])
    :integer    '(-1771277172712712728M)
                            :integer->bits  :booleans      '([false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true]))


(tabular
  (fact ":integer-bits pushes a list of true/false bits"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction       ?get-stack     ?expected
    :integer    '(0)        :integer-bits      :exec      '((false false))
    :integer    '(1)        :integer-bits      :exec      '((true false))
    :integer    '(3)        :integer-bits      :exec      '((true true))
    :integer    '(127)      :integer-bits      :exec      '((true true true true true true true))
    :integer    '(128)      :integer-bits      :exec      '((false false false false
                                                             false false false true))
    :integer    '(-128)     :integer-bits      :exec      '((false false false false
                                                             false false false true))
    :integer    '(1771277172712712728M)
                            :integer-bits      :exec      '((false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true))
    :integer    '(-1771277172712712728N)
                            :integer-bits      :exec      '((false false false true
                                                             true  false false false
                                                             false true false true
                                                             false true false true
                                                             false false true true
                                                             false false true false
                                                             false true true false
                                                             true true false false
                                                             false false true false
                                                             true true false false
                                                             true true true false
                                                             true false true true
                                                             false false true false
                                                             true false false true
                                                             false false false true
                                                             true)))







;; conversions



(tabular
  (fact ":string->integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected

    :string     '("123")       :string->integer        :integer       '(123)
    :string     '("-123")      :string->integer        :integer       '(-123)
    :string     '("  52")      :string->integer        :integer       '()
    :string     '("  52")      :string->integer        :error         '({:item ":string->integer failed", :step 0})
    :string     '("52  ")      :string->integer        :integer       '()
    :string     '("\t\n52")    :string->integer        :integer       '()
    :string     '("-52e3")     :string->integer        :integer       '()
    :string     '("2.3e-4")    :string->integer        :integer       '()

    :string     '("foo")       :string->integer        :integer       '()
    :string     '("foo")       :string->integer        :error         '({:item ":string->integer failed", :step 0})
    :string     '("1.2.3")     :string->integer        :integer       '()
    :string     '("1.2 8")     :string->integer        :integer       '()
    :string     '("1/17")      :string->integer        :integer       '()
    :string     '("")          :string->integer        :integer       '()
    :string     '()            :string->integer        :integer       '())


(tabular
  (fact ":char->integer takes a :char value, and converts it to an :integer"
    (register-type-and-check-instruction
        ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction      ?get-stack     ?expected
    ;; simple     
    :char    '(\0)           :char->integer      :integer       '(48)
    :char    '(\r)           :char->integer      :integer       '(114)
    :char    '(\newline)     :char->integer      :integer       '(10)
    :char    '(\uF021)       :char->integer      :integer       '(61473)
    ;; consumes arg
    :char    '(\0)           :char->integer      :char          '()
    ;; missing args 
    :char    '()             :char->integer      :integer       '()
    :char    '()             :char->integer      :char          '())




