(ns push.instructions.base.char_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.char])  ;; sets up classic-char-type
  )



;; specific char behavior


(tabular
  (fact ":char-allfromstring puts every char in the top :string onto the :char stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected
    ;; all the letters
    :string    '("foo")         :char-allfromstring   :char     '(\f \o \o)
    :string    '("4\n5")        :char-allfromstring   :char     '(\4 \newline \5)
    :string    '("")            :char-allfromstring   :char     '()
    ;; missing args
    :string    '()              :char-allfromstring   :char     '())



(tabular
  (fact ":char-digit? returns true when the :char item is an numeric digit"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; exploring Java's recognizers
    :char    '(\R)           :char-digit?   :boolean     '(false)
    :char    '(\8)           :char-digit?   :boolean     '(true)
    :char    '(\e)           :char-digit?   :boolean     '(false)
    :char    '(\space)       :char-digit?   :boolean     '(false)
    :char    '(\2)           :char-digit?   :boolean     '(true)
    :char    '(\٧)           :char-digit?   :boolean     '(true)   ;; Arabic
    :char    '(\൬)           :char-digit?   :boolean     '(true)  ;; Malayalam
    :char    '(\④)           :char-digit?   :boolean     '(false)
    :char    '(\⒋)           :char-digit?   :boolean     '(false)
    :char    '(\⓽)           :char-digit?   :boolean     '(false)
    :char    '(\➏)           :char-digit?   :boolean     '(false)
    :char    '(\８)           :char-digit?   :boolean     '(true)   ;; fullwidth (Asian)
    :char    '(\Ⅷ)           :char-digit?   :boolean    '(false)
   ;; missing args
    :char    '()             :char-digit?   :boolean     '())


;; fixture

(def zerochar-list (list (char 0)))

(tabular
  (fact ":char-frominteger drops the top :integer into [0..128] and pushes that ASCII character"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected
    ;; all the letters
    :integer    '(88)         :char-frominteger   :char         '(\X)
    :integer    '(37)         :char-frominteger   :char         '(\%)
    :integer    '(-37)        :char-frominteger   :char         '(\[)
    :integer    '(200)        :char-frominteger   :char         '(\H)
    ;; edge cases
    :integer    '(0)          :char-frominteger   :char         zerochar-list
    :integer    '(128)        :char-frominteger   :char         zerochar-list
    :integer    '(256)        :char-frominteger   :char         zerochar-list
    :integer    '(-128)       :char-frominteger   :char         zerochar-list
    ;; missing args
    :integer    '()           :char-frominteger   :char         '())


(tabular
  (fact ":char-fromfloat drops the top :float down to an integer value in [0..128] and pushes that ASCII character"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack         ?expected
    ;; all the letters
    :float    '(88.9)         :char-fromfloat   :char         '(\X)
    :float    '(37.2)         :char-fromfloat   :char         '(\%)
    :float    '(-37.9)        :char-fromfloat   :char         '(\[)
    :float    '(200.2)        :char-fromfloat   :char         '(\H)
    ;; edge cases
    :float    '(0.2)          :char-fromfloat   :char         zerochar-list
    :float    '(128.2)        :char-fromfloat   :char         zerochar-list
    :float    '(256.2)        :char-fromfloat   :char         zerochar-list
    :float    '(-128.2)       :char-fromfloat   :char         zerochar-list
    ;; missing args
    :float    '()             :char-fromfloat   :char         '())



(tabular
  (fact ":char-letter? returns true when the :char item is an alphabetic letter (LC or UC)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; exploring Java's recognizers
    :char    '(\R)           :char-letter?   :boolean     '(true)
    :char    '(\8)           :char-letter?   :boolean     '(false)
    :char    '(\e)           :char-letter?   :boolean     '(true)
    :char    '(\space)       :char-letter?   :boolean     '(false)
    :char    '(\2)           :char-letter?   :boolean     '(false)
    :char    '(\ø)           :char-letter?   :boolean     '(true)
    :char    '(\á)           :char-letter?   :boolean     '(true)
    :char    '(\Ñ)           :char-letter?   :boolean     '(true)
    :char    '(\Ω)           :char-letter?   :boolean     '(true)
    :char    '(\£)           :char-letter?   :boolean     '(false)
    :char    '(\ℜ)           :char-letter?   :boolean     '(true)
    :char    '(\♫)           :char-letter?   :boolean     '(false)
   ;; missing args
    :char    '()             :char-letter?   :boolean     '())


(tabular
  (fact ":char-lowercase? returns true when the :char item is lowercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\r)         :char-lowercase?   :boolean     '(true)
    :char    '(\R)         :char-lowercase?   :boolean     '(false)
    :char    '(\1)         :char-lowercase?   :boolean     '(false)
    :char    '(\space)     :char-lowercase?   :boolean     '(false)
    :char    '(\æ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ƛ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ǯ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ɷ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ʯ)         :char-lowercase?   :boolean     '(true)
    :char    '(\π)         :char-lowercase?   :boolean     '(true)
    :char    '(\ß)         :char-lowercase?   :boolean     '(true)
    :char    '(\℥)         :char-lowercase?   :boolean     '(false)
    :char    '(\⒦)         :char-lowercase?   :boolean     '(false)
    :char    '(\ⓝ)         :char-lowercase?   :boolean     '(true)
   ;; missing args
    :char    '()           :char-lowercase?   :boolean     '())


(tabular
  (fact ":char-uppercase? returns true when the :char item is uppercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\r)         :char-uppercase?   :boolean     '(false)
    :char    '(\R)         :char-uppercase?   :boolean     '(true)
    :char    '(\1)         :char-uppercase?   :boolean     '(false)
    :char    '(\space)     :char-uppercase?   :boolean     '(false)
    :char    '(\æ)         :char-uppercase?   :boolean     '(false)
    :char    '(\Æ)         :char-uppercase?   :boolean     '(true)
    :char    '(\Ə)         :char-uppercase?   :boolean     '(true)
    :char    '(\Ȝ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ʀ)         :char-uppercase?   :boolean     '(false)
    :char    '(\ᴆ)         :char-uppercase?   :boolean     '(false)
    :char    '(\Ψ)         :char-uppercase?   :boolean     '(true)
    :char    '(\∃)         :char-uppercase?   :boolean     '(false)
    :char    '(\∀)         :char-uppercase?   :boolean     '(false)
    :char    '(\ℍ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ℚ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ℜ)         :char-uppercase?   :boolean     '(true)
    :char    '(\℞)         :char-uppercase?   :boolean     '(false)
    :char    '(\₨)         :char-uppercase?   :boolean     '(false)
    :char    '(\Ⓕ)         :char-uppercase?   :boolean     '(true)
   ;; missing args
    :char    '()           :char-uppercase?   :boolean     '())


(tabular
  (fact ":char-whitespace? returns true when the :char item is any kind of whitespace"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items              ?instruction  ?get-stack     ?expected
    ;; exploring Java's recognizers
    :char    '(\space)           :char-whitespace?   :boolean     '(true)
    :char    '(\newline)         :char-whitespace?   :boolean     '(true)
    :char    '(\e)               :char-whitespace?   :boolean     '(false)
    :char    '(\tab)             :char-whitespace?   :boolean     '(true)
    :char    '(\formfeed)        :char-whitespace?   :boolean     '(true)
    :char    '(\backspace)       :char-whitespace?   :boolean     '(false)
    :char    '(\return)          :char-whitespace?   :boolean     '(true)
    :char    '(\␠)               :char-whitespace?   :boolean     '(false)
    :char    '(\u00A0)           :char-whitespace?   :boolean     '(false)
    :char    '(\u1361)           :char-whitespace?   :boolean     '(false)
    :char    '(\u1680)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2002)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2003)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2004)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2005)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2006)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2007)           :char-whitespace?   :boolean     '(false)
    :char    '(\u2008)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2009)           :char-whitespace?   :boolean     '(true)
    :char    '(\u200A)           :char-whitespace?   :boolean     '(true)
    :char    '(\u200B)           :char-whitespace?   :boolean     '(false)
    :char    '(\u202F)           :char-whitespace?   :boolean     '(false)
    :char    '(\u205F)           :char-whitespace?   :boolean     '(true)
    :char    '(\u3000)           :char-whitespace?   :boolean     '(true)
    :char    '(\u303F)           :char-whitespace?   :boolean     '(false)
    :char    '(\u0007)           :char-whitespace?   :boolean     '(false)
    :char    '(\u0011)           :char-whitespace?   :boolean     '(false)
   ;; missing args
    :char    '()                 :char-whitespace?   :boolean     '())


;; visible

(tabular
  (fact ":char-stackdepth returns the number of items on the :char stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction  ?get-stack     ?expected
    :char    '(\r \e \l \p)  :char-stackdepth   :integer     '(4)
    :char    '(\R)           :char-stackdepth   :integer     '(1)
    :char    '()             :char-stackdepth   :integer     '(0))


(tabular
  (fact ":char-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items        ?instruction  ?get-stack     ?expected
    :char    '(\r \e \l \p)  :char-empty?   :boolean     '(false)
    :char    '()             :char-empty?   :boolean     '(true))


;; equatable


(tabular
  (fact ":char-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; identical
    :char    '(\r \s)       :char-equal?      :boolean        '(false)
    :char    '(\s \r)       :char-equal?      :boolean        '(false)
    :char    '(\r \r)       :char-equal?      :boolean        '(true)
    ;; missing args    
    :char    '(\s)          :char-equal?      :boolean        '()
    :char    '(\s)          :char-equal?      :char           '(\s)
    :char    '()            :char-equal?      :boolean        '()
    :char    '()            :char-equal?      :char           '())


(tabular
  (fact ":char-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; different
    :char    '(\r \s)       :char-notequal?      :boolean        '(true)
    :char    '(\s \r)       :char-notequal?      :boolean        '(true)
    :char    '(\r \r)       :char-notequal?      :boolean        '(false)
    ;; missing args    
    :char    '(\s)          :char-notequal?      :boolean        '()
    :char    '(\s)          :char-notequal?      :char           '(\s)
    :char    '()            :char-notequal?      :boolean        '()
    :char    '()            :char-notequal?      :char           '())


;; comparable


(tabular
  (fact ":char<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char<?      :boolean        '(false)
    :char    '(\s \r)       :char<?      :boolean        '(true)
    :char    '(\r \r)       :char<?      :boolean        '(false)
    ;; missing args    
    :char    '(\s)          :char<?      :boolean        '()
    :char    '(\s)          :char<?      :char           '(\s)
    :char    '()            :char<?      :boolean        '()
    :char    '()            :char<?      :char           '())


(tabular
  (fact ":char≤? returns a :boolean indicating whether :first ≤ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char≤?      :boolean        '(false)
    :char    '(\s \r)       :char≤?      :boolean        '(true)
    :char    '(\r \r)       :char≤?      :boolean        '(true)
    ;; missing args    
    :char    '(\s)          :char≤?      :boolean        '()
    :char    '(\s)          :char≤?      :char           '(\s)
    :char    '()            :char≤?      :boolean        '()
    :char    '()            :char≤?      :char           '())


(tabular
  (fact ":char≥? returns a :boolean indicating whether :first ≥ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char≥?      :boolean        '(true)
    :char    '(\s \r)       :char≥?      :boolean        '(false)
    :char    '(\r \r)       :char≥?      :boolean        '(true)
    ;; missing args    
    :char    '(\s)          :char≥?      :boolean        '()
    :char    '(\s)          :char≥?      :char           '(\s)
    :char    '()            :char≥?      :boolean        '()
    :char    '()            :char≥?      :char           '())


(tabular
  (fact ":char>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char>?      :boolean        '(true)
    :char    '(\s \r)       :char>?      :boolean        '(false)
    :char    '(\r \r)       :char>?      :boolean        '(false)
    ;; missing args    
    :char    '(\s)          :char>?      :boolean        '()
    :char    '(\s)          :char>?      :char           '(\s)
    :char    '()            :char>?      :boolean        '()
    :char    '()            :char>?      :char           '())


(tabular
  (fact ":char-max returns the 'larger' of the top two :char items"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char-max      :char        '(\s)
    :char    '(\s \r)       :char-max      :char        '(\s)
    :char    '(\r \r)       :char-max      :char        '(\r)
    ; ;; missing args    
    :char    '(\s)          :char-max      :char        '(\s)
    :char    '()            :char-max      :char        '()
    )


(tabular
  (fact ":char-min returns the 'smaller' of the top two :char items"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :char    '(\r \s)       :char-min      :char        '(\r)
    :char    '(\s \r)       :char-min      :char        '(\r)
    :char    '(\r \r)       :char-min      :char        '(\r)
    ; ;; missing args    
    :char    '(\s)          :char-min      :char        '(\s)
    :char    '()            :char-min      :char        '()
    )


;; movable