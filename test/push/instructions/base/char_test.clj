(ns push.instructions.base.char_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.definitions.quoted])
  (:use [push.type.item.char])  ;; sets up char-type
  )

;; quotable

(tabular
  (fact ":char->code code-quotes the top :char item to :exec"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; move it!
    :char       '(\y)           :char->code         :exec        (list (push-quote \y))
    :char       '()             :char->code         :code        '()
    )


;; specific char behavior


(tabular
  (fact ":string->chars puts every char in the top :string onto the :exec stack"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected
    ;; all the letters
    :string    '("foo")         :string->chars   :exec     '((\f \o \o))
    :string    '("4\n5")        :string->chars   :exec     '((\4 \newline \5))
    :string    '("")            :string->chars   :exec     '(())
    ;; missing args
    :string    '()              :string->chars   :exec     '()
    )



(tabular
  (fact ":char-digit? returns true when the :char item is an numeric digit"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; exploring Java's recognizers
    :char    '(\R)           :char-digit?   :exec      '(false)
    :char    '(\8)           :char-digit?   :exec      '(true)
    :char    '(\e)           :char-digit?   :exec      '(false)
    :char    '(\space)       :char-digit?   :exec      '(false)
    :char    '(\2)           :char-digit?   :exec      '(true)
    :char    '(\٧)           :char-digit?   :exec      '(true)   ;; Arabic
    :char    '(\൬)           :char-digit?   :exec      '(true)  ;; Malayalam
    :char    '(\④)           :char-digit?   :exec      '(false)
    :char    '(\⒋)           :char-digit?   :exec      '(false)
    :char    '(\⓽)           :char-digit?   :exec      '(false)
    :char    '(\➏)           :char-digit?   :exec      '(false)
    :char    '(\８)           :char-digit?   :exec      '(true)   ;; fullwidth (Asian)
    :char    '(\Ⅷ)           :char-digit?   :exec    '(false)
   ;; missing args
    :char    '()             :char-digit?   :exec      '())


;; fixture


(tabular
  (fact ":scalar->asciichar mods the top :scalar into [0..128] and pushes that ASCII character"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected
    ;; all the letters
    :scalar    '(88)         :scalar->asciichar   :exec        '(\X)
    :scalar    '(37)         :scalar->asciichar   :exec        '(\%)
    :scalar    '(-37)        :scalar->asciichar   :exec        '(\[)
    :scalar    '(200)        :scalar->asciichar   :exec        '(\H)
    ;; edge cases
    :scalar    '(0)          :scalar->asciichar   :exec        (list (char 0))
    :scalar    '(128)        :scalar->asciichar   :exec        (list (char 0))
    :scalar    '(256)        :scalar->asciichar   :exec        (list (char 0))
    :scalar    '(-128)       :scalar->asciichar   :exec        (list (char 0))

    :scalar    '(88.9)       :scalar->asciichar   :exec        (list (char 89))
    :scalar    '(37.2)       :scalar->asciichar   :exec        (list (char 38))
    :scalar    '(-37.9)      :scalar->asciichar   :exec        (list (char 91))
    :scalar    '(200.2)      :scalar->asciichar   :exec        (list (char 73))

    :scalar    '(828/5)      :scalar->asciichar   :exec        (list (char 38))
    :scalar    '(373/2)      :scalar->asciichar   :exec        (list (char 59))
    :scalar    '(-37/2)      :scalar->asciichar   :exec        (list (char 110))
    :scalar    '(200/6)      :scalar->asciichar   :exec        (list (char 34))
    ;; edge cases
    :scalar    '(0.2)        :scalar->asciichar   :exec         (list (char 1))
    :scalar    '(128.2)      :scalar->asciichar   :exec         (list (char 1))
    :scalar    '(256.2)      :scalar->asciichar   :exec         (list (char 1))
    :scalar    '(-128.2)     :scalar->asciichar   :exec         (list (char 0))

    ;; missing args
    :scalar    '()           :scalar->asciichar   :exec         '())


(tabular
  (fact ":scalar->char drops the top :scalar into [0..65535] and pushes that unicode character"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected

    :scalar    '(88)         :scalar->char   :exec         '(\X)
    :scalar    '(37)         :scalar->char   :exec         '(\%)
    :scalar    '(382)        :scalar->char   :exec         '(\ž)
    :scalar    '(-17212)     :scalar->char   :exec         '(\볃)
    :scalar    '(2764)       :scalar->char   :exec         '(\ૌ)
    ;; edge cases
    :scalar    '(0)          :scalar->char   :exec         (list (char 0))
    :scalar    '(65535)      :scalar->char   :exec         (list (char 0))
    :scalar    '(256)        :scalar->char   :exec         (list (char 256))
    :scalar    '(-128)       :scalar->char   :exec         (list (char 65407))
    ;; missing args
    :scalar    '()           :scalar->char   :exec         '()

    :scalar    '(88.9)         :scalar->char   :exec         (list (char 89))
    :scalar    '(37.2)         :scalar->char   :exec         (list (char 38))
    :scalar    '(-22771.9)     :scalar->char   :exec         (list (char 42764))
    :scalar    '(200.2)        :scalar->char   :exec         (list (char 201))

    :scalar    '(0.2)          :scalar->char   :exec         (list (char 1))
    :scalar    '(65535.3)      :scalar->char   :exec         (list (char 1))
    :scalar    '(256.9)        :scalar->char   :exec         (list (char 257))
    :scalar    '(-128.2)       :scalar->char   :exec         (list (char 65407))
    ;; bounds for internal typecast (huge bigint mod 65535 -> 0)
    :scalar    '(1.1e88M)      :scalar->char   :exec         '(\뗖)
    :scalar    '(111111111111111111111111111111111111111.0M)
                               :scalar->char   :exec         '(\㓂)
    ;; missing args
    :scalar    '()             :scalar->char   :exec         '()

    )



(tabular
  (fact ":char-letter? returns true when the :char item is an alphabetic letter (LC or UC)"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; exploring Java's recognizers
    :char    '(\R)           :char-letter?   :exec       '(true)
    :char    '(\8)           :char-letter?   :exec       '(false)
    :char    '(\e)           :char-letter?   :exec       '(true)
    :char    '(\space)       :char-letter?   :exec       '(false)
    :char    '(\2)           :char-letter?   :exec       '(false)
    :char    '(\ø)           :char-letter?   :exec       '(true)
    :char    '(\á)           :char-letter?   :exec       '(true)
    :char    '(\Ñ)           :char-letter?   :exec       '(true)
    :char    '(\Ω)           :char-letter?   :exec       '(true)
    :char    '(\£)           :char-letter?   :exec       '(false)
    :char    '(\ℜ)           :char-letter?   :exec       '(true)
    :char    '(\♫)           :char-letter?   :exec       '(false)
   ;; missing args
    :char    '()             :char-letter?   :exec       '())


(tabular
  (fact ":char-lowercase? returns true when the :char item is lowercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\r)         :char-lowercase?   :exec      '(true)
    :char    '(\R)         :char-lowercase?   :exec      '(false)
    :char    '(\1)         :char-lowercase?   :exec      '(false)
    :char    '(\space)     :char-lowercase?   :exec      '(false)
    :char    '(\æ)         :char-lowercase?   :exec      '(true)
    :char    '(\ƛ)         :char-lowercase?   :exec      '(true)
    :char    '(\ǯ)         :char-lowercase?   :exec      '(true)
    :char    '(\ɷ)         :char-lowercase?   :exec      '(true)
    :char    '(\ʯ)         :char-lowercase?   :exec      '(true)
    :char    '(\π)         :char-lowercase?   :exec      '(true)
    :char    '(\ß)         :char-lowercase?   :exec      '(true)
    :char    '(\℥)         :char-lowercase?   :exec      '(false)
    :char    '(\⒦)         :char-lowercase?   :exec      '(false)
    :char    '(\ⓝ)         :char-lowercase?   :exec      '(true)
   ;; missing args
    :char    '()           :char-lowercase?   :exec     '())


(tabular
  (fact ":char-uppercase? returns true when the :char item is uppercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\r)         :char-uppercase?   :exec      '(false)
    :char    '(\R)         :char-uppercase?   :exec      '(true)
    :char    '(\1)         :char-uppercase?   :exec      '(false)
    :char    '(\space)     :char-uppercase?   :exec      '(false)
    :char    '(\æ)         :char-uppercase?   :exec      '(false)
    :char    '(\Æ)         :char-uppercase?   :exec      '(true)
    :char    '(\Ə)         :char-uppercase?   :exec      '(true)
    :char    '(\Ȝ)         :char-uppercase?   :exec      '(true)
    :char    '(\ʀ)         :char-uppercase?   :exec      '(false)
    :char    '(\ᴆ)         :char-uppercase?   :exec      '(false)
    :char    '(\Ψ)         :char-uppercase?   :exec      '(true)
    :char    '(\∃)         :char-uppercase?   :exec      '(false)
    :char    '(\∀)         :char-uppercase?   :exec      '(false)
    :char    '(\ℍ)         :char-uppercase?   :exec      '(true)
    :char    '(\ℚ)         :char-uppercase?   :exec      '(true)
    :char    '(\ℜ)         :char-uppercase?   :exec      '(true)
    :char    '(\℞)         :char-uppercase?   :exec      '(false)
    :char    '(\₨)         :char-uppercase?   :exec      '(false)
    :char    '(\Ⓕ)         :char-uppercase?  :exec     '(true)
   ;; missing args
    :char    '()           :char-uppercase?   :exec     '())


(tabular
  (fact ":char-whitespace? returns true when the :char item is any kind of whitespace"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items              ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\space)           :char-whitespace?   :exec      '(true)
    :char    '(\newline)         :char-whitespace?   :exec      '(true)
    :char    '(\e)               :char-whitespace?   :exec      '(false)
    :char    '(\tab)             :char-whitespace?   :exec      '(true)
    :char    '(\formfeed)        :char-whitespace?   :exec      '(true)
    :char    '(\backspace)       :char-whitespace?   :exec      '(false)
    :char    '(\return)          :char-whitespace?   :exec      '(true)
    :char    '(\␠)               :char-whitespace?   :exec      '(false)
    :char    '(\u00A0)           :char-whitespace?   :exec      '(false)
    :char    '(\u1361)           :char-whitespace?   :exec      '(false)
    :char    '(\u1680)           :char-whitespace?   :exec      '(true)
    :char    '(\u2002)           :char-whitespace?   :exec      '(true)
    :char    '(\u2003)           :char-whitespace?   :exec      '(true)
    :char    '(\u2004)           :char-whitespace?   :exec      '(true)
    :char    '(\u2005)           :char-whitespace?   :exec      '(true)
    :char    '(\u2006)           :char-whitespace?   :exec      '(true)
    :char    '(\u2007)           :char-whitespace?   :exec      '(false)
    :char    '(\u2008)           :char-whitespace?   :exec      '(true)
    :char    '(\u2009)           :char-whitespace?   :exec      '(true)
    :char    '(\u200A)           :char-whitespace?   :exec      '(true)
    :char    '(\u200B)           :char-whitespace?   :exec      '(false)
    :char    '(\u202F)           :char-whitespace?   :exec      '(false)
    :char    '(\u205F)           :char-whitespace?   :exec      '(true)
    :char    '(\u3000)           :char-whitespace?   :exec      '(true)
    :char    '(\u303F)           :char-whitespace?   :exec      '(false)
    :char    '(\u0007)           :char-whitespace?   :exec      '(false)
    :char    '(\u0011)           :char-whitespace?   :exec      '(false)
   ;; missing args
    :char    '()                 :char-whitespace?   :exec      '())


;; visible

(tabular
  (fact ":char-stackdepth returns the number of items on the :char stack (to :scalar)"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction  ?get-stack     ?expected
    :char    '(\r \e \l \p)  :char-stackdepth   :exec       '(4)
    :char    '(\R)           :char-stackdepth   :exec       '(1)
    :char    '()             :char-stackdepth   :exec       '(0))


(tabular
  (fact ":char-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction  ?get-stack     ?expected
    :char    '(\r \e \l \p)  :char-empty?   :exec         '(false)
    :char    '()             :char-empty?   :exec         '(true))


;; equatable


(tabular
  (fact ":char-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; identical
    :char    '(\r \s)       :char-equal?      :exec         '(false)
    :char    '(\s \r)       :char-equal?      :exec         '(false)
    :char    '(\r \r)       :char-equal?      :exec         '(true)
    ;; missing args
    :char    '(\s)          :char-equal?      :exec         '()
    :char    '(\s)          :char-equal?      :char           '(\s)
    :char    '()            :char-equal?      :exec         '()
    :char    '()            :char-equal?      :char           '())


(tabular
  (fact ":char-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; different
    :char    '(\r \s)       :char-notequal?      :exec         '(true)
    :char    '(\s \r)       :char-notequal?      :exec         '(true)
    :char    '(\r \r)       :char-notequal?      :exec         '(false)
    ;; missing args
    :char    '(\s)          :char-notequal?      :exec         '()
    :char    '(\s)          :char-notequal?      :char           '(\s)
    :char    '()            :char-notequal?      :exec         '()
    :char    '()            :char-notequal?      :char           '())


;; comparable


(tabular
  (fact ":char<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char<?      :exec         '(false)
    :char    '(\s \r)       :char<?      :exec         '(true)
    :char    '(\r \r)       :char<?      :exec         '(false)
    ;; missing args
    :char    '(\s)          :char<?      :exec         '()
    :char    '(\s)          :char<?      :char         '(\s)
    :char    '()            :char<?      :exec         '()
    :char    '()            :char<?      :char         '())


(tabular
  (fact ":char≤? returns a :boolean indicating whether :first ≤ :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char≤?      :exec         '(false)
    :char    '(\s \r)       :char≤?      :exec         '(true)
    :char    '(\r \r)       :char≤?      :exec         '(true)
    ;; missing args
    :char    '(\s)          :char≤?      :exec         '()
    :char    '(\s)          :char≤?      :char           '(\s)
    :char    '()            :char≤?      :exec         '()
    :char    '()            :char≤?      :char           '())


(tabular
  (fact ":char≥? returns a :boolean indicating whether :first ≥ :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char≥?      :exec         '(true)
    :char    '(\s \r)       :char≥?      :exec         '(false)
    :char    '(\r \r)       :char≥?      :exec         '(true)
    ;; missing args
    :char    '(\s)          :char≥?      :exec         '()
    :char    '(\s)          :char≥?      :char           '(\s)
    :char    '()            :char≥?      :exec         '()
    :char    '()            :char≥?      :char           '())


(tabular
  (fact ":char>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char>?      :exec         '(true)
    :char    '(\s \r)       :char>?      :exec         '(false)
    :char    '(\r \r)       :char>?      :exec         '(false)
    ;; missing args
    :char    '(\s)          :char>?      :exec         '()
    :char    '(\s)          :char>?      :char           '(\s)
    :char    '()            :char>?      :exec         '()
    :char    '()            :char>?      :char           '())


(tabular
  (fact ":char-max returns the 'larger' of the top two :char items"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char-max      :exec         '(\s)
    :char    '(\s \r)       :char-max      :exec         '(\s)
    :char    '(\r \r)       :char-max      :exec         '(\r)
    ; ;; missing args
    :char    '(\s)          :char-max      :char        '(\s)
    :char    '()            :char-max      :char        '()
    )


(tabular
  (fact ":char-min returns the 'smaller' of the top two :char items"
    (register-type-and-check-instruction
        ?set-stack ?items char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char-min      :exec         '(\r)
    :char    '(\s \r)       :char-min      :exec         '(\r)
    :char    '(\r \r)       :char-min      :exec         '(\r)
    ; ;; missing args
    :char    '(\s)          :char-min      :char        '(\s)
    :char    '()            :char-min      :char        '()
    )


;; movable
