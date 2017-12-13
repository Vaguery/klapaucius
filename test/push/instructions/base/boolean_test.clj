(ns push.instructions.base.boolean_test
  (:require [push.interpreter.core :as i]
            [push.util.numerics :as num])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use push.util.exotics)
  (:use push.type.item.boolean)  ;; sets up boolean-type
  )


;; support items (now located in push.util.exotics)


(fact "scalar-to-truth-table puts bits in correct order and pads with false as needed"
  (scalar-to-truth-table 19 3) =>
    [true true false false true false false false]
 ;;    1    2    4     8    16    32    64   128
 ;;   FFF  FFT  FTF   FTT  TFF   TFT   TTF   TTT

  (scalar-to-truth-table 1 3) =>
    [true false false false false false false false]

  (scalar-to-truth-table 1 1) =>
    [true false]

  (scalar-to-truth-table 1 2) =>
    [true false false false]
    )


(fact "scalar-to-truth-table trims unasked-for bits"
  (scalar-to-truth-table 7777777 3) =>
    [true false false false true true true true] ;;... true false true true false true false true false true true false true true true

  (scalar-to-truth-table 1024 1) =>
    [false false] ;; false false false false false false false false true
    )


(fact "scalar-to-truth-table accepts scalars"
  (scalar-to-truth-table 8.3 3) =>
    [false false false true false false false false]
  (scalar-to-truth-table -8.3 3) =>
    [false false false true false false false false]
  (scalar-to-truth-table 1e82 3) =>
    [false false false false false false false false]
  (scalar-to-truth-table 771251672M 3) =>
    [false false false true true false true true]
  (scalar-to-truth-table 1623761723/41 3) =>
    [false false false true false true true true]
    )


(fact "scalar-to-truth-table with too few bits"
  (scalar-to-truth-table 0 1) => [false false]
  (scalar-to-truth-table 1 1) => [true false]
  (scalar-to-truth-table 2 1) => [false true]
  (scalar-to-truth-table 3 1) => [true true]
  (scalar-to-truth-table 6 1) => [false true]
  (scalar-to-truth-table 11 1) => [true true]
  (scalar-to-truth-table 310 1) => [false true]
  )


(fact "scalar-to-truth-table with really bad args"
  (scalar-to-truth-table 11 -1) => []
  (scalar-to-truth-table 11 0) => []
  (scalar-to-truth-table num/∞ 0) =>[]
  (scalar-to-truth-table num/-∞ 0) => []
  (scalar-to-truth-table -12345678912344567899.123567899M 3) =not=> []
  )


;; all the conversions


(tabular
  (fact ":scalar->boolean is false if 0, true otherwise
         :scalarsign->boolean is false if neg, true otherwise"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected
    :scalar     '(0)         :scalar->boolean           :exec       '(false)
    :scalar     '(11)        :scalar->boolean           :exec       '(true)
    :scalar     '(-4)        :scalar->boolean           :exec       '(true)
    :scalar     '(0)         :scalarsign->boolean       :exec       '(true)
    :scalar     '(11)        :scalarsign->boolean       :exec       '(true)
    :scalar     '(-4)        :scalarsign->boolean       :exec       '(false)

    :scalar     '(0.0)       :scalar->boolean           :exec       '(false)
    :scalar     '(11.0)      :scalar->boolean           :exec       '(true)
    :scalar     '(-4.0)      :scalar->boolean           :exec       '(true)
    :scalar     '(0.0)       :scalarsign->boolean       :exec       '(true)
    :scalar     '(11.0)      :scalarsign->boolean       :exec       '(true)
    :scalar     '(-4.0)      :scalarsign->boolean       :exec       '(false)

    :scalar     '(0/7)       :scalar->boolean           :exec       '(false)
    :scalar     '(11/7)      :scalar->boolean           :exec       '(true)
    :scalar     '(-4/7)      :scalar->boolean           :exec       '(true)
    :scalar     '(0/7)       :scalarsign->boolean       :exec       '(true)
    :scalar     '(11/7)      :scalarsign->boolean       :exec       '(true)
    :scalar     '(-4/7)      :scalarsign->boolean       :exec       '(false)
    )


;; quotable

(tabular
  (fact ":boolean->code move the top :boolean item to :code"
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
    :boolean    '(false false)  :boolean-and   :exec        '(false)
    :boolean    '(false true)   :boolean-and   :exec        '(false)
    :boolean    '(true false)   :boolean-and   :exec        '(false)
    :boolean    '(true true)    :boolean-and   :exec        '(true)
    ;; missing args
    :boolean    '(false)        :boolean-and   :boolean     '(false)
    :boolean    '()             :boolean-and   :boolean     '())


(tabular
  (fact ":boolean-not returns the binary NOT of the top :boolean value"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-not   :exec        '(true)
    :boolean    '(false true)   :boolean-not   :exec        '(true)
    ;; missing args
    :boolean    '()             :boolean-not   :boolean     '())


(tabular
  (fact ":boolean-or returns the binary OR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-or   :exec     '(false)
    :boolean    '(false true)   :boolean-or   :exec     '(true)
    :boolean    '(true false)   :boolean-or   :exec     '(true)
    :boolean    '(true true)    :boolean-or   :exec     '(true)
    ;; missing args
    :boolean    '(false)        :boolean-or   :boolean     '(false)
    :boolean    '()             :boolean-or   :boolean     '())


(tabular
  (fact ":boolean-xor returns the binary XOR over the top two :boolean values"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :boolean    '(false false)  :boolean-xor   :exec     '(false)
    :boolean    '(false true)   :boolean-xor   :exec     '(true)
    :boolean    '(true false)   :boolean-xor   :exec     '(true)
    :boolean    '(true true)    :boolean-xor   :exec     '(false)
    ;; missing args
    :boolean    '(false)        :boolean-xor   :boolean  '(false)
    :boolean    '()             :boolean-xor   :boolean  '())



(tabular
  (fact "`:boolean-2bittable` pops a `:scalar`, and pushes the 4-entry `:booleans` truth table"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction              ?expected
    {:scalar  '(3)
     :booleans '()}    :boolean-2bittable     {:scalar  '()
                                               :exec '([true true false false])}
    )



(tabular
  (fact "`:boolean-3bittable` takes a :scalar to make a 3-bit :booleans truth table"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

    ?new-stacks           ?instruction           ?expected
    {:scalar '(23)
     :booleans '()}
                          :boolean-3bittable     {:scalar '()
                                                  :exec '([true true true false true false false false])}
    )



(tabular
  (fact "`:boolean-arity2` pops a `:scalar`, makes a lookup table, and pops two `:boolean` values as inputs, pushes the result from the table"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

  ;; see the table above for expected values for truth table 3
  ;; [true true false false]
  ;;   FF   FT    TF    TT
  ;;   pq   pq    pq    pq
    ?new-stacks                ?instruction      ?expected
    {:scalar  '(3)
     :boolean '(true true)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  '(3)
     :boolean '(true false)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  '(3)
     :boolean '(false true)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(true)}
    {:scalar  '(3)
     :boolean '(false false)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(true)}
    )


(tabular
  (fact "`:boolean-arity2` returns `false` for infinite arguments"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar  (list num/∞)
     :boolean '(true true)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/∞)
     :boolean '(true false)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/-∞)
     :boolean '(false true)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/-∞)
     :boolean '(false false)}
               ;  q    p
                              :boolean-arity2   {:scalar  '()
                                                  :exec '(false)}
    )


(tabular
  (fact "`:boolean-arity3` pops a `:scalar`, makes a lookup table, and pops 3 `:boolean` values as inputs, pushes the result from the table"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

  ;; see the table above for expected values for truth table 23
  ;; [true true true false true false false false]
  ;;   FFF  FFT  FTF  FTT   TFF   TFT   TTF   TTT
  ;;   pqr  pqr  pqr  pqr   pqr   pqr   pqr   pqr

    ?new-stacks                ?instruction      ?expected
    {:scalar  '(23)
     :boolean '(true true true)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  '(23)
     :boolean '(true false false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(true)}
    {:scalar  '(23)
     :boolean '(false true false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(true)}
    {:scalar  '(23)
     :boolean '(false false false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(true)}
    )

(tabular
  (fact "`:boolean-arity3` returns false for all bad scalar arguments"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks boolean-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar  (list num/∞)
     :boolean '(true true true)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/∞)
     :boolean '(true false false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/-∞)
     :boolean '(false true false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(false)}
    {:scalar  (list num/∞)
     :boolean '(false false false)}
               ;  r    q    p
                               :boolean-arity3   {:scalar  '()
                                                  :exec '(false)}
    )


;; combinators


(tabular
  (fact ":boolean-dup duplicates the top item from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true)   :boolean-dup    :exec      '((false false))
    :boolean    '(true)         :boolean-dup    :exec      '((true true))
    ;; missing args
    :boolean    '()             :boolean-dup    :exec      '())


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
  (fact ":boolean-stackdepth saves (count :boolean) onto :scalar"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items               ?instruction            ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false false false) :boolean-stackdepth      :scalar       '(3)
    :boolean    '()                  :boolean-stackdepth      :scalar       '(0))


(tabular
  (fact ":boolean-swap swaps the top two items from :boolean"
    (register-type-and-check-instruction
        ?set-stack ?items boolean-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction    ?get-stack     ?expected
    ;; just shifting things
    :boolean    '(false true true)
                           :boolean-swap      :exec       '((false true))
    :boolean    '(false true)
                           :boolean-swap      :exec       '((false true))
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
    :boolean    '()             :boolean-flush      :boolean       '()
    )
