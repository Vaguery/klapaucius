(ns push.instructions.base.string_test
  (:require [push.interpreter.core :as i])
  (:use [push.util.test-helpers])
  (:use [push.types.base.string])  ;; sets up classic-string-type
  (:use midje.sweet))


;; these are tests of an Interpreter with the classic-string-type registered
;; the instructions under test are those stored IN THAT TYPE


; string_frominteger
; string_fromfloat
; string_fromboolean
; string_fromchar

; string_conjchar
; string_take
; string_substring
; string_first
; string_last
; string_nth
; string_rest
; string_butlast
; string_length
; string_reverse
; string_parse_to_chars
; string_split
; string_emptystring
; string_contains
; string_containschar
; string_indexofchar
; string_occurrencesofchar
; string_replace
; string_replacefirst
; string_replacechar
; string_replacefirstchar
; string_removechar
; string_setchar
; exec_string_iterate
; string_readchar
; string_readline
; string_whitespace


;; specific string behavior


(tabular
  (fact ":string-concat returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; concatenation
    :string    '("foo" "bar")   :string-concat  :string     '("barfoo")
    :string    '("foo " "bar ") :string-concat  :string     '("bar foo ")
    :string    '("foo" "bar\n") :string-concat  :string     '("bar\nfoo")
    :string    '("" "2")        :string-concat  :string     '("2")
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" "\n" )
                                :string-concat  :string     '("\n\b8")
   ;; missing args
    :string    '("foo")         :string-concat  :string     '("foo"))


; (tabular
;   (fact ":char-digit? returns true when the :char item is an numeric digit"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items         ?instruction  ?get-stack   ?expected
;     ;; anding
;     :char    '(\R)           :char-digit?   :boolean     '(false)
;     :char    '(\8)           :char-digit?   :boolean     '(true)
;     :char    '(\e)           :char-digit?   :boolean     '(false)
;     :char    '(\space)       :char-digit?   :boolean     '(false)
;     :char    '(\2)           :char-digit?   :boolean     '(true)
;     :char    '(\٧)           :char-digit?   :boolean     '(true)   ;; Arabic
;     :char    '(\൬)           :char-digit?   :boolean     '(true)  ;; Malayalam
;     :char    '(\④)           :char-digit?   :boolean     '(false)
;     :char    '(\⒋)           :char-digit?   :boolean     '(false)
;     :char    '(\⓽)           :char-digit?   :boolean     '(false)
;     :char    '(\➏)           :char-digit?   :boolean     '(false)
;     :char    '(\８)           :char-digit?   :boolean     '(true)   ;; fullwidth (Asian)
;     :char    '(\Ⅷ)           :char-digit?   :boolean    '(false)
;    ;; missing args
;     :char    '()             :char-digit?   :boolean     '())


; (tabular
;   (fact ":char-whitespace? returns true when the :char item is any kind of whitespace"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items              ?instruction  ?get-stack     ?expected
;     ;; anding
;     :char    '(\space)           :char-whitespace?   :boolean     '(true)
;     :char    '(\newline)         :char-whitespace?   :boolean     '(true)
;     :char    '(\e)               :char-whitespace?   :boolean     '(false)
;     :char    '(\tab)             :char-whitespace?   :boolean     '(true)
;     :char    '(\formfeed)        :char-whitespace?   :boolean     '(true)
;     :char    '(\backspace)       :char-whitespace?   :boolean     '(false)
;     :char    '(\return)          :char-whitespace?   :boolean     '(true)
;     :char    '(\␠)               :char-whitespace?   :boolean     '(false)
;     :char    '(\u00A0)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u1361)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u1680)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2002)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2003)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2004)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2005)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2006)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2007)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u2008)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u2009)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u200A)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u200B)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u202F)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u205F)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u3000)           :char-whitespace?   :boolean     '(true)
;     :char    '(\u303F)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u0007)           :char-whitespace?   :boolean     '(false)
;     :char    '(\u0011)           :char-whitespace?   :boolean     '(false)
;    ;; missing args
;     :char    '()                 :char-whitespace?   :boolean     '())



; (tabular
;   (fact ":char-lowercase? returns true when the :char item is lowercase (per Java)"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items     ?instruction  ?get-stack     ?expected
;     ;; anding
;     :char    '(\r)         :char-lowercase?   :boolean     '(true)
;     :char    '(\R)         :char-lowercase?   :boolean     '(false)
;     :char    '(\1)         :char-lowercase?   :boolean     '(false)
;     :char    '(\space)     :char-lowercase?   :boolean     '(false)
;     :char    '(\æ)         :char-lowercase?   :boolean     '(true)
;     :char    '(\ƛ)         :char-lowercase?   :boolean     '(true)
;     :char    '(\ǯ)         :char-lowercase?   :boolean     '(true)
;     :char    '(\ɷ)         :char-lowercase?   :boolean     '(true)
;     :char    '(\ʯ)         :char-lowercase?   :boolean     '(true)
;     :char    '(\π)         :char-lowercase?   :boolean     '(true)
;     :char    '(\ß)         :char-lowercase?   :boolean     '(true)
;     :char    '(\℥)         :char-lowercase?   :boolean     '(false)
;     :char    '(\⒦)         :char-lowercase?   :boolean     '(false)
;     :char    '(\ⓝ)         :char-lowercase?   :boolean     '(true)
;    ;; missing args
;     :char    '()           :char-lowercase?   :boolean     '())



; (tabular
;   (fact ":char-uppercase? returns true when the :char item is uppercase (per Java)"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items     ?instruction  ?get-stack     ?expected
;     ;; anding
;     :char    '(\r)         :char-uppercase?   :boolean     '(false)
;     :char    '(\R)         :char-uppercase?   :boolean     '(true)
;     :char    '(\1)         :char-uppercase?   :boolean     '(false)
;     :char    '(\space)     :char-uppercase?   :boolean     '(false)
;     :char    '(\æ)         :char-uppercase?   :boolean     '(false)
;     :char    '(\Æ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\Ə)         :char-uppercase?   :boolean     '(true)
;     :char    '(\Ȝ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\ʀ)         :char-uppercase?   :boolean     '(false)
;     :char    '(\ᴆ)         :char-uppercase?   :boolean     '(false)
;     :char    '(\Ψ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\∃)         :char-uppercase?   :boolean     '(false)
;     :char    '(\∀)         :char-uppercase?   :boolean     '(false)
;     :char    '(\ℍ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\ℚ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\ℜ)         :char-uppercase?   :boolean     '(true)
;     :char    '(\℞)         :char-uppercase?   :boolean     '(false)
;     :char    '(\₨)         :char-uppercase?   :boolean     '(false)
;     :char    '(\Ⓕ)         :char-uppercase?   :boolean     '(true)
;    ;; missing args
;     :char    '()           :char-uppercase?   :boolean     '())


; ;; visible

; (tabular
;   (fact ":char-stackdepth returns the number of items on the :char stack (to :integer)"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items        ?instruction  ?get-stack     ?expected
;     ;; anding
;     :char    '(\r \e \l \p)  :char-stackdepth   :integer     '(4)
;     :char    '(\R)           :char-stackdepth   :integer     '(1)
;     :char    '()             :char-stackdepth   :integer     '(0))


; (tabular
;   (fact ":char-empty? returns the true (to :boolean stack) if the stack is empty"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items        ?instruction  ?get-stack     ?expected
;     ;; anding
;     :char    '(\r \e \l \p)  :char-empty?   :boolean     '(false)
;     :char    '()             :char-empty?   :boolean     '(true))


; ;; equatable


; (tabular
;   (fact ":char-equal? returns a :boolean indicating whether :first = :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char-equal?      :boolean        '(false)
;     :char    '(\s \r)       :char-equal?      :boolean        '(false)
;     :char    '(\r \r)       :char-equal?      :boolean        '(true)
;     ;; missing args    
;     :char    '(\s)          :char-equal?      :boolean        '()
;     :char    '(\s)          :char-equal?      :char           '(\s)
;     :char    '()            :char-equal?      :boolean        '()
;     :char    '()            :char-equal?      :char           '())


; (tabular
;   (fact ":char-notequal? returns a :boolean indicating whether :first ≠ :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char-notequal?      :boolean        '(true)
;     :char    '(\s \r)       :char-notequal?      :boolean        '(true)
;     :char    '(\r \r)       :char-notequal?      :boolean        '(false)
;     ;; missing args    
;     :char    '(\s)          :char-notequal?      :boolean        '()
;     :char    '(\s)          :char-notequal?      :char           '(\s)
;     :char    '()            :char-notequal?      :boolean        '()
;     :char    '()            :char-notequal?      :char           '())


; ;; comparable


; (tabular
;   (fact ":char<? returns a :boolean indicating whether :first < :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char<?      :boolean        '(false)
;     :char    '(\s \r)       :char<?      :boolean        '(true)
;     :char    '(\r \r)       :char<?      :boolean        '(false)
;     ;; missing args    
;     :char    '(\s)          :char<?      :boolean        '()
;     :char    '(\s)          :char<?      :char           '(\s)
;     :char    '()            :char<?      :boolean        '()
;     :char    '()            :char<?      :char           '())


; (tabular
;   (fact ":char≤? returns a :boolean indicating whether :first ≤ :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char≤?      :boolean        '(false)
;     :char    '(\s \r)       :char≤?      :boolean        '(true)
;     :char    '(\r \r)       :char≤?      :boolean        '(true)
;     ;; missing args    
;     :char    '(\s)          :char≤?      :boolean        '()
;     :char    '(\s)          :char≤?      :char           '(\s)
;     :char    '()            :char≤?      :boolean        '()
;     :char    '()            :char≤?      :char           '())


; (tabular
;   (fact ":char≥? returns a :boolean indicating whether :first ≥ :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char≥?      :boolean        '(true)
;     :char    '(\s \r)       :char≥?      :boolean        '(false)
;     :char    '(\r \r)       :char≥?      :boolean        '(true)
;     ;; missing args    
;     :char    '(\s)          :char≥?      :boolean        '()
;     :char    '(\s)          :char≥?      :char           '(\s)
;     :char    '()            :char≥?      :boolean        '()
;     :char    '()            :char≥?      :char           '())


; (tabular
;   (fact ":char>? returns a :boolean indicating whether :first > :second"
;     (register-type-and-check-instruction
;         ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

;     ?set-stack  ?items    ?instruction  ?get-stack     ?expected
;     ;; just the math
;     :char    '(\r \s)       :char>?      :boolean        '(true)
;     :char    '(\s \r)       :char>?      :boolean        '(false)
;     :char    '(\r \r)       :char>?      :boolean        '(false)
;     ;; missing args    
;     :char    '(\s)          :char>?      :boolean        '()
;     :char    '(\s)          :char>?      :char           '(\s)
;     :char    '()            :char>?      :boolean        '()
;     :char    '()            :char>?      :char           '())


; ;; movable