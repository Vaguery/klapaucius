(ns push.instructions.base.string_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.string])  ;; sets up classic-string-type
  )


;; these are tests of an Interpreter with the classic-string-type registered
;; the instructions under test are those stored IN THAT TYPE

;; work in progress
;; these instructions from Clojush are yet to be implemented:

; assemblers and disassemblers

; string_frominteger
; string_fromfloat
; string_fromboolean
; string_fromchar
; string_parse_to_chars
; string_conjchar

; getters and setters

; string_setchar
; string_readchar
; string_readline
; string_whitespace
; string_emptystring
; string_nth
; string_take
; string_substring
; string_first
; string_last
; string_rest
; string_butlast

; string methods qua methods

; string_split
; string_contains
; string_containschar
; string_indexofchar
; string_occurrencesofchar
; string_replace
; string_replacefirst
; string_replacechar
; string_replacefirstchar
; string_removechar
; exec_string_iterate


;; specific string behavior


(tabular
  (fact ":string-butlast removes the last char from a string argument"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; lost the tail
    :string    '("foo")         :string-butlast  :string     '("fo")
    :string    '(" foo ")       :string-butlast  :string     '(" foo")
    :string    '("\n\t\t\n")    :string-butlast  :string     '("\n\t\t")
    :string    '("\"\"")        :string-butlast  :string     '("\"")
    :string    '("")            :string-butlast  :string     '("")
    ;; missing args
    :string    '()              :string-butlast  :string     '())


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


(tabular
  (fact ":string-emptystring? removes the last char from a string argument"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anything?
    :string    '("foo")         :string-emptystring?  :boolean     '(false)
    :string    '("")            :string-emptystring?  :boolean     '(true)
    :string    '("\n")          :string-emptystring?  :boolean     '(false)
    ;; missing args
    :string    '()              :string-emptystring?  :boolean     '())


(tabular
  (fact ":string-first returns the 1st :char of the string"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; initial
    :string    '("foo")         :string-first  :char     '(\f)
    :string    '(" foo ")       :string-first  :char     '(\space)
    :string    '("\n\t\t\n")    :string-first  :char     '(\newline)
    :string    '("\u2665\u2666")
                                :string-first  :char     '(\u2665)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-first  :char     '(\backspace)
    ;; missing args
    :string    '()              :string-first  :char     '())


(tabular
  (fact ":string-indexofchar returns the index of :char (or -1)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}           
                                :string-indexofchar       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(0)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\a)
     :string '("ere he was able")}           
                                :string-indexofchar       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(8)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}           
                                :string-indexofchar       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(-1)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}           
                                :string-indexofchar       
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}           
                                :string-indexofchar       
                                                  {:char '(\e)
                                                   :string '()
                                                   :integer '()})


(tabular
  (fact ":string-last returns the last :char of the string"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; final
    :string    '("foo")         :string-last  :char     '(\o)
    :string    '(" foo ")       :string-last  :char     '(\space)
    :string    '("\n\t\t\n")    :string-last  :char     '(\newline)
    :string    '("\u2665\u2666")
                                :string-last  :char     '(\u2666)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-last  :char     '(\8)
    ;; missing args
    :string    '()              :string-last  :char     '())


(tabular
  (fact ":string-length returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; length
    :string    '("foo")         :string-length  :integer     '(3)
    :string    '(" foo ")       :string-length  :integer     '(5)
    :string    '("foo\n\t\t\n") :string-length  :integer     '(7)
    :string    '("\u2665")      :string-length  :integer     '(1)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-length  :integer     '(2)
    ;; missing args
    :string    '()              :string-length  :integer     '())



(tabular
  (fact ":string-occurrencesofchar counts :char in :string (as :int)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}           
                                :string-occurrencesofchar       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(4)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}           
                                :string-occurrencesofchar       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(0)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}           
                                :string-occurrencesofchar       
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}           
                                :string-occurrencesofchar       
                                                  {:char '(\e)
                                                   :string '()
                                                   :integer '()})


(tabular
  (fact ":string-reverse returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; backwards
    :string    '("foo")         :string-reverse  :string     '("oof")
    :string    '(" foo ")       :string-reverse  :string     '(" oof ")
    :string    '("foo\n\t\t\n") :string-reverse  :string     '("\n\t\t\noof")
    :string    '("\u2665\u2666")
                                :string-reverse  :string     '("\u2666\u2665")
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-reverse  :string     '("8\b")
    ;; missing args
    :string    '()              :string-reverse  :string     '())

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