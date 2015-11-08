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


; ;; visible


(tabular
  (fact ":string-stackdepth returns the number of items on the :string stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :string    '("a" "" "b")   :string-stackdepth   :integer      '(3)
    :string    '("nn\tmm")     :string-stackdepth   :integer      '(1)
    :string    '()             :string-stackdepth   :integer      '(0))


(tabular
  (fact ":string-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :string    '("foo" "bar")  :string-empty?   :boolean     '(false)
    :string    '()             :string-empty?   :boolean     '(true))


; ;; equatable


(tabular
  (fact ":string-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :string    '("o" "p")       :string-equal?      :boolean        '(false)
    :string    '("p" "o")       :string-equal?      :boolean        '(false)
    :string    '("o" "o")       :string-equal?      :boolean        '(true)
    ;; missing args    
    :string    '("p")           :string-equal?      :boolean        '()
    :string    '("p")           :string-equal?      :string         '("p")
    :string    '()              :string-equal?      :boolean        '()
    :string    '()              :string-equal?      :string         '())


(tabular
  (fact ":string-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction  ?get-stack     ?expected
    ;; different
    :string    '("y" "z")       :string-notequal?      :boolean        '(true)
    :string    '("z" "y")       :string-notequal?      :boolean        '(true)
    :string    '("y" "y")       :string-notequal?      :boolean        '(false)
    ;; missing args    
    :string    '("z")           :string-notequal?      :boolean        '()
    :string    '("z")           :string-notequal?      :string         '("z")
    :string    '()              :string-notequal?      :boolean        '()
    :string    '()              :string-notequal?      :string         '())


; ;; comparable


(tabular
  (fact ":string<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string<?      :boolean        '(false)
    :string    '("abd" "abc")       :string<?      :boolean        '(true)
    :string    '("acc" "abc")       :string<?      :boolean        '(true)
    :string    '("bbc" "abc")       :string<?      :boolean        '(true)
    :string    '("def" "abc")       :string<?      :boolean        '(true)
    :string    '("ab" "abc")        :string<?      :boolean        '(false)
    :string    '("abc" "ab")        :string<?      :boolean        '(true)
    :string    '("abc" "abc")       :string<?      :boolean        '(false)
    :string    '("" "")             :string<?      :boolean        '(false)
    ;; missing args    
    :string    '("def")             :string<?      :boolean        '()
    :string    '("def")             :string<?      :string         '("def")
    :string    '()                  :string<?      :boolean        '()
    :string    '()                  :string<?      :string         '())


(tabular
  (fact ":string≤? returns a :boolean indicating whether :first ≤ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string≤?      :boolean        '(false)
    :string    '("abd" "abc")       :string≤?      :boolean        '(true)
    :string    '("acc" "abc")       :string≤?      :boolean        '(true)
    :string    '("bbc" "abc")       :string≤?      :boolean        '(true)
    :string    '("def" "abc")       :string≤?      :boolean        '(true)
    :string    '("ab" "abc")        :string≤?      :boolean        '(false)
    :string    '("abc" "ab")        :string≤?      :boolean        '(true)
    :string    '("abc" "abc")       :string≤?      :boolean        '(true)
    :string    '("" "")             :string≤?      :boolean        '(true)
    ;; missing args    
    :string    '("def")             :string≤?      :boolean        '()
    :string    '("def")             :string≤?      :string         '("def")
    :string    '()                  :string≤?      :boolean        '()
    :string    '()                  :string≤?      :string         '())



(tabular
  (fact ":string≥? returns a :boolean indicating whether :first ≥ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string≥?      :boolean        '(true)
    :string    '("abd" "abc")       :string≥?      :boolean        '(false)
    :string    '("acc" "abc")       :string≥?      :boolean        '(false)
    :string    '("bbc" "abc")       :string≥?      :boolean        '(false)
    :string    '("def" "abc")       :string≥?      :boolean        '(false)
    :string    '("ab" "abc")        :string≥?      :boolean        '(true)
    :string    '("abc" "ab")        :string≥?      :boolean        '(false)
    :string    '("abc" "abc")       :string≥?      :boolean        '(true)
    :string    '("" "")             :string≥?      :boolean        '(true)
    ;; missing args    
    :string    '("def")             :string≥?      :boolean        '()
    :string    '("def")             :string≥?      :string         '("def")
    :string    '()                  :string≥?      :boolean        '()
    :string    '()                  :string≥?      :string         '())


(tabular
  (fact ":string>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string>?      :boolean        '(true)
    :string    '("abd" "abc")       :string>?      :boolean        '(false)
    :string    '("acc" "abc")       :string>?      :boolean        '(false)
    :string    '("bbc" "abc")       :string>?      :boolean        '(false)
    :string    '("def" "abc")       :string>?      :boolean        '(false)
    :string    '("ab" "abc")        :string>?      :boolean        '(true)
    :string    '("abc" "ab")        :string>?      :boolean        '(false)
    :string    '("abc" "abc")       :string>?      :boolean        '(false)
    :string    '("" "")             :string>?      :boolean        '(false)
    ;; missing args    
    :string    '("def")             :string>?      :boolean        '()
    :string    '("def")             :string>?      :string         '("def")
    :string    '()                  :string>?      :boolean        '()
    :string    '()                  :string>?      :string         '())


; ;; movable