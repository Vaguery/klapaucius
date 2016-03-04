(ns push.instructions.base.boolean_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.boolean])  ;; sets up boolean-type
  )

;; all the conversions

(tabular
  (fact ":integer->boolean is false if 0, true otherwise
         :intsign->boolean is false if neg, true otherwise
         ditto :float->boolean and :floatsign->boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected
    :integer    '(0)         :integer->boolean      :boolean       '(false)
    :integer    '(11)        :integer->boolean      :boolean       '(true)
    :integer    '(-4)        :integer->boolean      :boolean       '(true)
    :integer    '(0)         :intsign->boolean      :boolean       '(true)
    :integer    '(11)        :intsign->boolean      :boolean       '(true)
    :integer    '(-4)        :intsign->boolean      :boolean       '(false)

    :float    '(0.0)         :float->boolean        :boolean       '(false)
    :float    '(11.0)        :float->boolean        :boolean       '(true)
    :float    '(-4.0)        :float->boolean        :boolean       '(true)
    :float    '(0.0)         :floatsign->boolean    :boolean       '(true)
    :float    '(11.0)        :floatsign->boolean    :boolean       '(true)
    :float    '(-4.0)        :floatsign->boolean    :boolean       '(false))


;; quotable

(tabular
  (fact ":boolean->code move the top :boolean item to :code;"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; move it!
    :boolean    '(false)        :boolean->code      :code        '(false)
    :boolean    '()             :boolean->code      :code        '()
    )



;; specific boolean behavior

(tabular
  (fact ":boolean-and returns the binary AND over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-and   :boolean     '(false)
    :boolean    '(false true)   :boolean-and   :boolean     '(false)
    :boolean    '(true false)   :boolean-and   :boolean     '(false)
    :boolean    '(true true)    :boolean-and   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        :boolean-and   :boolean     '(false)
    :boolean    '()             :boolean-and   :boolean     '())


(tabular
  (fact ":boolean-not returns the binary NOT of the top :boolean value"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-not   :boolean     '(true false)
    :boolean    '(false true)   :boolean-not   :boolean     '(true true)
    ;; missing args
    :boolean    '()             :boolean-not   :boolean     '())


(tabular
  (fact ":boolean-or returns the binary OR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-or   :boolean     '(false)
    :boolean    '(false true)   :boolean-or   :boolean     '(true)
    :boolean    '(true false)   :boolean-or   :boolean     '(true)
    :boolean    '(true true)    :boolean-or   :boolean     '(true)
    ;; missing args
    :boolean    '(false)        :boolean-or   :boolean     '(false)
    :boolean    '()             :boolean-or   :boolean     '())


(tabular
  (fact ":boolean-xor returns the binary XOR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-xor   :boolean     '(false)
    :boolean    '(false true)   :boolean-xor   :boolean     '(true)
    :boolean    '(true false)   :boolean-xor   :boolean     '(true)
    :boolean    '(true true)    :boolean-xor   :boolean     '(false)
    ;; missing args
    :boolean    '(false)        :boolean-xor   :boolean     '(false)
    :boolean    '()             :boolean-xor   :boolean     '())


;; equatable


;; combinators


(tabular
  (fact ":boolean-dup duplicates the top item from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-dup      :boolean       '(false false true)
    :boolean    '(true)         :boolean-dup      :boolean       '(true true)
    ;; missing args 
    :boolean    '()             :boolean-dup      :boolean       '())


(tabular
  (fact ":boolean-pop removes the top item from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-pop      :boolean       '(true)
    :boolean    '(true)         :boolean-pop      :boolean       '()
    ;; missing args 
    :boolean    '()             :boolean-pop      :boolean       '())


(tabular
  (fact ":boolean-rotate shifts the top 3 items from :boolean, putting 3rd on top"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items             ?instruction        ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true true false)
                                   :boolean-rotate      :boolean       '(true false true false)
    ;; missing args 
    :boolean    '(false true)      :boolean-rotate      :boolean       '(false true)
    :boolean    '(true)            :boolean-rotate      :boolean       '(true)
    :boolean    '()                :boolean-rotate      :boolean       '())


(tabular
  (fact ":boolean-stackdepth saves (count :boolean) onto :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items               ?instruction            ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false false false) :boolean-stackdepth      :integer       '(3)
    :boolean    '()                  :boolean-stackdepth      :integer       '(0))


(tabular
  (fact ":boolean-swap swaps the top two items from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true true)   
                           :boolean-swap      :boolean       '(true false true)
    :boolean    '(false true)
                           :boolean-swap      :boolean       '(true false)
    ;; missing args 
    :boolean    '(false)   :boolean-swap      :boolean       '(false)
    :boolean    '()        :boolean-swap      :boolean       '())



(tabular
  (fact ":boolean-flush flushes the entire :boolean stack"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-flush      :boolean       '()
    ;; missing args 
    :boolean    '()             :boolean-flush      :boolean       '())

