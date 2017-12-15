(ns push.instructions.base.string_test
  (:require [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.string])
  (:use [push.type.definitions.quoted])
  )


;; a fixture

(def teeny (push/interpreter :config {:max-collection-size 9}))


;; all the conversions


(fact "valid-numbers-only produces collection of parsed numbers, or nil values, given a collection of strings"
  (valid-numbers-only
    ["9" "0000009.9" "0.123" "1e4"
    "0x83c" "8/11" "2r101110001011001" "01271216530"]) =>
    [9 9.9 0.123 10000.0 2108 8/11 23641 182787416]
  (valid-numbers-only
    ["9e" "f99" ".0.123" "(1e4)" "'0x83c" "8/11.0" "01119" "0129"]) => []
  )


(tabular
  (fact "the very very difficult :string->scalar instruction works fine for easy strings"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction       ?get-stack     ?expected
    :string    '("88")       :string->scalar      :exec          '(88)
    :string    '("88.8")     :string->scalar      :exec          '(88.8)
    :string    '("6.2e7")    :string->scalar      :exec          '(6.2e7)
    :string    '("88N")      :string->scalar      :exec          '(88N)
    :string    '("3/11")     :string->scalar      :exec          '(3/11)
  )


(tabular
  (fact "the very very difficult :string->scalar instruction takes the first number it finds"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction       ?get-stack     ?expected
    :string    '("hey 88")     :string->scalar     :exec         '(88)
    :string    '("(neg 88.8")  :string->scalar     :exec         '(88.8)
    :string    '("\n\n6.2e7")  :string->scalar     :exec         '(6.2e7)
    :string    '("{:x 88N}")   :string->scalar     :exec         '(88N)
    :string    '("(+ 3/11 9)") :string->scalar     :exec         '(3/11)
  )

(tabular
  (fact "the very very difficult :string->scalar instruction works when no string is present"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction       ?get-stack     ?expected
    :string    '("hey88")      :string->scalar     :exec         '()
    :string    '("(88.8)")     :string->scalar     :exec         '()
    :string    '("")           :string->scalar     :exec         '()
    :string    '("string")     :string->scalar     :exec         '()
    :string    '(":foo")       :string->scalar     :exec         '()
  )


(tabular
  (fact ":string->scalars returns a complete vector of numeric items in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction       ?get-stack     ?expected
    :string    '("")            :string->scalars    :exec     '([])
    :string    '("8, 9, 10")    :string->scalars    :exec     '([8 9 10])
    :string    '("17.2 3.1 a")  :string->scalars    :exec     '([17.2 3.1])
    :string    '("nothing 5M")  :string->scalars    :exec     '([5M])
    :string    '("1e7 0x8 061") :string->scalars    :exec     '([1.0E7 8 49])
    :string    '("፹ 〺 Ⅸ ⅔")   :string->scalars     :exec     '([])
    :string    '("(->Complex -∞ 2/3)")
                                :string->scalars    :exec      '([2/3])
  )



(tabular
  (fact " :boolean->string, :code->string, :exec->string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected
    :boolean    '(false)       :boolean->string        :exec         '("false")
    :boolean    '(true)        :boolean->string        :exec         '("true")
    :boolean    '()            :boolean->string        :exec         '()

    :char       '(\Y)          :char->string           :exec         '("Y")
    :char       '(\u262F)      :char->string           :exec         '("☯")
    :char       '()            :char->string           :exec         '()

    :code     '((88 :code-do)) :code->string           :exec         '("(88 :code-do)")
    :code     '([1 [3 5]])     :code->string           :exec         '("[1 [3 5]]")
    :code     '(''99)          :code->string           :exec         '("(quote (quote 99))")
    :code     '(1/8)           :code->string           :exec         '("1/8")
    :code     '({:a [1 2]})    :code->string           :exec         '("{:a [1 2]}")
    :code     '()              :code->string           :exec         '()

    :exec     '((88 :code-do)) :exec->string           :exec         '("(88 :code-do)")
    :exec     '([1 [3 5]])     :exec->string           :exec         '("[1 [3 5]]")
    :exec     '(''99)          :exec->string           :exec         '("(quote (quote 99))")
    :exec     '(1/8)           :exec->string           :exec         '("1/8")
    :exec     '({:a [1 2]})    :exec->string           :exec         '("{:a [1 2]}")
    :exec     '()              :exec->string           :exec         '())


;; quotable

(tabular
  (fact ":string->code code-quotes the top :string item (on :exec)"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction      ?get-stack     ?expected
    ;; move it!
    :string       '("92")    :string->code         :exec       (list (push-quote "92"))
    :string       '()        :string->code         :code       '()
    )


;; specific string behavior


(tabular
  (fact ":exec-string-iterate chops off characters from a string and 'applies' the top :exec item to them"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:scalar-add)
     :string '("ere he was able")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '((\e :scalar-add
                                                              "re he was able"
                                                              :exec-string-iterate
                                                              :scalar-add))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:scalar-add :foo)
     :string '("ere he was able")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '((\e :scalar-add
                                                              "re he was able"
                                                              :exec-string-iterate
                                                              :scalar-add) :foo)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '((2 :scalar-add (7)))
     :string '("ere he was able")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '((\e (2 :scalar-add (7))
                                                              "re he was able"
                                                              :exec-string-iterate
                                                              (2 :scalar-add (7))))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:scalar-add)
     :string '("e")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '((\e :scalar-add))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:scalar-add)
     :string '("")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:scalar-add :foo)
     :string '("")}
                            :exec-string-iterate
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '(:foo)})




(tabular
  (fact ":string-butlast removes the last char from a string argument"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; lost the tail
    :string    '("foo")         :string-butlast  :exec     '("fo")
    :string    '(" foo ")       :string-butlast  :exec     '(" foo")
    :string    '("\n\t\t\n")    :string-butlast  :exec     '("\n\t\t")
    :string    '("\"\"")        :string-butlast  :exec     '("\"")
    :string    '("")            :string-butlast  :exec     '("")
    ;; missing args
    :string    '()              :string-butlast  :exec     '())


(tabular
  (fact ":string-concat returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; concatenation
    :string    '("foo" "bar")   :string-concat  :exec      '("barfoo")
    :string    '("foo " "bar ") :string-concat  :exec      '("bar foo ")
    :string    '("foo" "bar\n") :string-concat  :exec      '("bar\nfoo")
    :string    '("" "2")        :string-concat  :exec      '("2")
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" "\n" )
                                :string-concat  :exec      '("\n\b8")
    ;; missing args
    :string    '("foo")         :string-concat  :string      '("foo"))



(tabular
  (fact ":string-concat checks for oversized results and pushes an `:error` if one arises"
    (register-type-and-check-instruction-in-this-interpreter
      teeny
      ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :string     '("foo" "bar")
                          :string-concat    :exec
                                                           '("barfoo")
    :string     '("fools" "barge")
                          :string-concat    :exec
                                                           '()
    :string     '("fools" "barge")
                          :string-concat    :error
                                                           '({:item ":string-concat produced oversized result", :step 0})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact ":string-conjchar attaches the top :char the end of the top :string"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:string  '("foo")
     :char    '(\w)}         :string-conjchar      {:exec '("foow")
                                                    :char '()}         )

(tabular
  (fact ":string-containschar? returns the true if the :char is in :string"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-containschar?
                                                  {:char '()
                                                   :string '()
                                                   :exec '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\q)
     :string '("ere he was able")}
                                :string-containschar?
                                                  {:char '()
                                                   :string '()
                                                   :exec '(false)} )


(tabular
  (fact ":string-contains? returns true if the second string is in the first"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction  ?get-stack   ?expected
    :string    '("foo" "bar")       :string-contains?  :exec     '(false)
    :string    '("foo" "barfoobar") :string-contains?  :exec     '(false)
    :string    '("barfoobar" "foo") :string-contains?  :exec     '(true)
    :string    '("foo" "foo")       :string-contains?  :exec     '(true)
    :string    '("" "foo")          :string-contains?  :exec     '(false)
    :string    '("foo" "")          :string-contains?  :exec     '(true)
    :string    '("" "")             :string-contains?  :exec     '(true))


(tabular
  (fact ":string-emptystring? returns true if the argument is exactly \"\""
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anything?
    :string    '("foo")         :string-emptystring?  :exec      '(false)
    :string    '("")            :string-emptystring?  :exec      '(true)
    :string    '("\n")          :string-emptystring?  :exec      '(false)
    ;; missing args
    :string    '()              :string-emptystring?  :exec      '())


(tabular
  (fact ":string-first returns the 1st :char of the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; initial
    :string    '("foo")         :string-first  :exec     '(\f)
    :string    '(" foo ")       :string-first  :exec     '(\space)
    :string    '("\n\t\t\n")    :string-first  :exec     '(\newline)
    :string    '("\u2665\u2666")
                                :string-first  :exec     '(\u2665)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-first  :exec     '(\backspace)
    ;; missing args
    :string    '()              :string-first  :exec     '())


(tabular
  (fact ":string-indexofchar returns the index of :char (or -1)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :exec '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\a)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :exec '(8)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :exec '(-1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :exec '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}
                                :string-indexofchar
                                                  {:char '(\e)
                                                   :string '()
                                                   :exec '()})


(tabular
  (fact ":string-last returns the last :char of the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; final
    :string    '("foo")         :string-last  :exec       '(\o)
    :string    '(" foo ")       :string-last  :exec       '(\space)
    :string    '("\n\t\t\n")    :string-last  :exec       '(\newline)
    :string    '("\u2665\u2666")
                                :string-last  :exec       '(\u2666)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-last  :exec       '(\8)
    ;; missing args
    :string    '()              :string-last  :exec       '())


(tabular
  (fact ":string-length returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; length
    :string    '("foo")         :string-length  :exec      '(3)
    :string    '(" foo ")       :string-length  :exec      '(5)
    :string    '("foo\n\t\t\n") :string-length  :exec      '(7)
    :string    '("\u2665")      :string-length  :exec      '(1)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-length  :exec      '(2)
    ;; missing args
    :string    '()              :string-length  :exec      '())



(tabular
  (fact ":string-nth returns the indexed character (modulo length)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :string  '("ere he was able")}
                                :string-nth
                                                  {:exec '(\e)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2)
     :string '("ere he was able")}
                                :string-nth
                                                  {:exec '(\l)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(16)
     :string '("ere he was able")}
                                :string-nth
                                                  {:exec '(\r)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(16)
     :string '("")}
                                :string-nth
                                                  {:exec  '()
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:scalar '()
     :string '("ere he was able")}
                                :string-nth
                                                  {:exec  '()
                                                   :string '("ere he was able")
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :string '()}
                                :string-nth
                                                  {:exec  '()
                                                   :string '()
                                                   :scalar '(0)})


(tabular
  (fact ":string-occurrencesofchar counts :char in :string (as :int)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-occurrencesofchar
                                                  {:char '()
                                                   :string '()
                                                   :exec '(4)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-occurrencesofchar
                                                  {:char '()
                                                   :string '()
                                                   :exec '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-occurrencesofchar
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :exec '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}
                                :string-occurrencesofchar
                                                  {:char '(\e)
                                                   :string '()
                                                   :exec '()})


(tabular
  (fact ":string-removechar takes all occurrences of :char out"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :exec '("r h was abl")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space)
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :exec '("erehewasable")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :exec '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}
                                :string-removechar
                                                  {:char '(\e)
                                                   :string '()})



(tabular
  (fact ":string-replace replaces all occurrences of :str/2 with :str/1 in :str/3"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    :string    '("X" "ab" "aabbaaabbb")
                                :string-replace  :exec     '("aXbaaXbb")
    :string    '("Napoleon" "he" "ere he was able")
                                :string-replace  :exec     '("ere Napoleon was able")
    :string    '(" " "\n" "foo\n\t\t\n")
                                :string-replace  :exec     '("foo \t\t ")
    :string    '("" "a" "aabbaaabbb")
                                :string-replace  :exec     '("bbbbb")
    :string    '("X" "" "aabbaaabbb")
                                :string-replace  :exec     '("XaXaXbXbXaXaXaXbXbXbX")
    ;; missing args
    :string    '("a" "b")       :string-replace  :string     '("a" "b")
    :string    '("a")           :string-replace  :string     '("a")
    :string    '("")            :string-replace  :string     '(""))


(tabular
  (fact ":string-replace checks for oversized results and pushes an `:error` if one arises"
    (register-type-and-check-instruction-in-this-interpreter
      teeny
      ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :string     '("foo" "a" "aa")
                          :string-replace    :exec
                                                           '("foofoo")
    :string     '("foo" "a" "aaaa")
                          :string-replace    :exec
                                                           '()
    :string     '("foo" "a" "aaaa")
                          :string-replace    :error
                                                           '({:step 0, :item ":string-replace result too large"})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact ":string-replacefirst the first occurrence of :str/2 with :str/1 in :str/3"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack   ?expected
    :string    '("X" "ab" "aabbaaabbb")
                                :string-replacefirst  :exec     '("aXbaaabbb")
    :string    '("Napoleon" "he" "ere he was able")
                                :string-replacefirst  :exec     '("ere Napoleon was able")
    :string    '(" " "\n" "foo\n\t\t\n")
                                :string-replacefirst  :exec     '("foo \t\t\n")
    :string    '("" "a" "aabbaaabbb")
                                :string-replacefirst  :exec     '("abbaaabbb")
    :string    '("X" "" "aabbaaabbb")
                                :string-replacefirst  :exec     '("Xaabbaaabbb")
    ;; missing args
    :string    '("a" "b")       :string-replacefirst  :string     '("a" "b")
    :string    '("a")           :string-replacefirst  :string     '("a")
    :string    '("")            :string-replacefirst  :string     '(""))


(tabular
  (fact ":string-replacechar replaces all occurrences of :char/1 with :char/2"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e \f)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :exec '("frf hf was ablf")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space \•)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :exec '("ere•he•was•able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z \q)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :exec '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '(\e)
                                                   :string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e \f)
     :string '()}
                                :string-replacechar
                                                  {:char '(\e \f)
                                                   :string '()})



(tabular
  (fact ":string-replacefirstchar replaces the first occurrence of :char/1 with :char/2"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e \f)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :exec '("fre he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space \•)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :exec '("ere•he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z \q)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :exec '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '(\e)
                                                   :string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e \f)
     :string '()}
                                :string-replacefirstchar
                                                  {:char '(\e \f)
                                                   :string '()})



(tabular
  (fact ":string-rest removes the first char from a string argument"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; lost the head
    :string    '("foo")         :string-rest  :exec      '("oo")
    :string    '(" foo ")       :string-rest  :exec      '("foo ")
    :string    '("\n\t\t\n")    :string-rest  :exec      '("\t\t\n")
    :string    '("\"\"")        :string-rest  :exec      '("\"")
    :string    '("")            :string-rest  :exec      '("")
    ;; missing args
    :string    '()              :string-rest  :exec      '())




(tabular
  (fact ":string-reverse returns the second :string item tacked to the end of the first"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; backwards
    :string    '("foo")         :string-reverse  :exec     '("oof")
    :string    '(" foo ")       :string-reverse  :exec     '(" oof ")
    :string    '("foo\n\t\t\n") :string-reverse  :exec     '("\n\t\t\noof")
    :string    '("\u2665\u2666")
                                :string-reverse  :exec     '("\u2666\u2665")
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-reverse  :exec     '("8\b")
    ;; missing args
    :string    '()              :string-reverse  :exec     '())



(tabular
  (fact ":string-setchar changes the char in position :int to :char, modulo :string's length"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :scalar  '(1)
     :char    '(\O)}          :string-setchar        {:exec '("fOo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :scalar  '(6)
     :char    '(\O)}          :string-setchar        {:exec '("Ooo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :scalar  '(-2)
     :char    '(\O)}          :string-setchar        {:exec '("fOo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("")
     :scalar  '(11)
     :char    '(\O)}          :string-setchar        {:exec '("O")
                                                     :char '()})



(tabular
  (fact ":string-shatter pushes all the letters as individual strings"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; shatter
    :string    '("foo")         :string-shatter  :exec     '("f" "o" "o")
    :string    '("foo\n\t")     :string-shatter  :exec     '("f" "o" "o" "\n" "\t")
    :string    '("\u2665")      :string-shatter  :exec     '("\u2665")
    :string    '("" "x")        :string-shatter  :exec     '("x")
    ;; missing args
    :string    '()              :string-shatter  :exec     '())




(tabular
  (fact ":string-solid? returns true if there is no whitespace anywhere in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; we good here?
    :string    '("foo")         :string-solid?  :exec     '(true)
    :string    '("foo_bar")     :string-solid?  :exec     '(true)
    :string    '("foo/bar")     :string-solid?  :exec     '(true)
    :string    '("a\u1722b")    :string-solid?  :exec     '(true)
    :string    '("")            :string-solid?  :exec     '(false)
    :string    '("foo bar")     :string-solid?  :exec     '(false)
    :string    '("foo\nbar")    :string-solid?  :exec     '(false)
    :string    '(" foo")        :string-solid?  :exec     '(false)
    ;; missing args
    :string    '()              :string-solid?  :exec     '())



(tabular
  (fact ":string-spacey? returns true if there is no NON-whitespace anywhere in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; we good here?
    :string    '("foo")         :string-spacey?  :exec     '(false)
    :string    '("       ")     :string-spacey?  :exec     '(true)
    :string    '("\n\n\n\n")    :string-spacey?  :exec     '(true)
    :string    '("\n\n\n\n.")   :string-spacey?  :exec     '(false)
    :string    '("")            :string-spacey?  :exec     '(false)
    ;; missing args
    :string    '()              :string-spacey?  :exec     '())


(tabular
  (fact ":string-splitonspaces pushes the space-delimited parts of :string onto the stack"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; chunks
    :string    '("a b c d e")   :string-splitonspaces  :exec   '("a" "b" "c" "d" "e")
    :string    '(" foo ")       :string-splitonspaces  :exec   '("" "foo")
    :string    '("\na\tb\tc\n\n")
                                :string-splitonspaces  :exec '("" "a" "b" "c")
    :string    '("\"\"")        :string-splitonspaces  :exec   '("\"\"")
    :string    '("")            :string-splitonspaces  :exec   '("")
    ;; missing args
    :string    '()              :string-splitonspaces  :exec   '())



(tabular
  (fact ":string-substring clips out the substring determined by two :int args (with cropping and sorting)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(2 12)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:exec '("e he was a")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(12 2)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:exec '("e he was a")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(33 -712)                    ;; these get cropped to 0, count s
     :string   '("ere he was able")}
                                :string-substring
                                                  {:exec '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(4 4)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:exec '("")})


(tabular
  (fact ":string-take returns first (mod idx (count string)) characters"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(8)
     :string '("ere he was able")}
                                :string-take
                                                  {:exec '("ere he w")
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(-3)
     :string '("ere he was able")}
                                :string-take
                                                  {:exec '("ere he was a")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(99)
     :string '("ere he was able")}
                                :string-take
                                                  {:exec '("ere he wa")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(99)
     :string '("")}
                                :string-take
                                                  {:exec '("")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '()
     :string '("ere he was able")}
                                :string-take
                                                  {:string '("ere he was able")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(99)
     :string '()}
                                :string-take
                                                  {:string '()
                                                   :scalar  '(99)})


; ;; visible


(tabular
  (fact ":string-stackdepth returns the number of items on the :string stack (to :scalar)"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :string    '("a" "" "b")   :string-stackdepth   :exec          '(3)
    :string    '("nn\tmm")     :string-stackdepth   :exec          '(1)
    :string    '()             :string-stackdepth   :exec          '(0))


(tabular
  (fact ":string-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :string    '("foo" "bar")  :string-empty?   :exec        '(false)
    :string    '()             :string-empty?   :exec        '(true))


; ;; equatable


(tabular
  (fact ":string-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :string    '("o" "p")       :string-equal?      :exec           '(false)
    :string    '("p" "o")       :string-equal?      :exec           '(false)
    :string    '("o" "o")       :string-equal?      :exec           '(true)
    ;; missing args
    :string    '("p")           :string-equal?      :exec           '()
    :string    '("p")           :string-equal?      :string         '("p")
    :string    '()              :string-equal?      :exec           '()
    :string    '()              :string-equal?      :string         '())


(tabular
  (fact ":string-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction  ?get-stack     ?expected
    ;; different
    :string    '("y" "z")       :string-notequal?      :exec           '(true)
    :string    '("z" "y")       :string-notequal?      :exec           '(true)
    :string    '("y" "y")       :string-notequal?      :exec           '(false)
    ;; missing args
    :string    '("z")           :string-notequal?      :exec           '()
    :string    '("z")           :string-notequal?      :string         '("z")
    :string    '()              :string-notequal?      :exec           '()
    :string    '()              :string-notequal?      :string         '())


; ;; comparable


(tabular
  (fact ":string<? returns a :boolean indicating whether :first < :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string<?      :exec         '(false)
    :string    '("abd" "abc")       :string<?      :exec         '(true)
    :string    '("acc" "abc")       :string<?      :exec         '(true)
    :string    '("bbc" "abc")       :string<?      :exec         '(true)
    :string    '("def" "abc")       :string<?      :exec         '(true)
    :string    '("ab" "abc")        :string<?      :exec         '(false)
    :string    '("abc" "ab")        :string<?      :exec         '(true)
    :string    '("abc" "abc")       :string<?      :exec         '(false)
    :string    '("" "")             :string<?      :exec         '(false)
    ;; missing args
    :string    '("def")             :string<?      :exec        '()
    :string    '("def")             :string<?      :string      '("def")
    :string    '()                  :string<?      :exec        '()
    :string    '()                  :string<?      :string      '()
    )


(tabular
  (fact ":string≤? returns a :boolean indicating whether :first ≤ :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string≤?      :exec         '(false)
    :string    '("abd" "abc")       :string≤?      :exec         '(true)
    :string    '("acc" "abc")       :string≤?      :exec         '(true)
    :string    '("bbc" "abc")       :string≤?      :exec         '(true)
    :string    '("def" "abc")       :string≤?      :exec         '(true)
    :string    '("ab" "abc")        :string≤?      :exec         '(false)
    :string    '("abc" "ab")        :string≤?      :exec         '(true)
    :string    '("abc" "abc")       :string≤?      :exec         '(true)
    :string    '("" "")             :string≤?      :exec         '(true)
    ;; missing args
    :string    '("def")             :string≤?      :exec         '()
    :string    '("def")             :string≤?      :string         '("def")
    :string    '()                  :string≤?      :exec         '()
    :string    '()                  :string≤?      :string         '())



(tabular
  (fact ":string≥? returns a :boolean indicating whether :first ≥ :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string≥?      :exec         '(true)
    :string    '("abd" "abc")       :string≥?      :exec         '(false)
    :string    '("acc" "abc")       :string≥?      :exec         '(false)
    :string    '("bbc" "abc")       :string≥?      :exec         '(false)
    :string    '("def" "abc")       :string≥?      :exec         '(false)
    :string    '("ab" "abc")        :string≥?      :exec         '(true)
    :string    '("abc" "ab")        :string≥?      :exec         '(false)
    :string    '("abc" "abc")       :string≥?      :exec         '(true)
    :string    '("" "")             :string≥?      :exec         '(true)
    ;; missing args
    :string    '("def")             :string≥?      :exec         '()
    :string    '("def")             :string≥?      :string         '("def")
    :string    '()                  :string≥?      :exec         '()
    :string    '()                  :string≥?      :string         '())


(tabular
  (fact ":string>? returns a :boolean indicating whether :first > :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction  ?get-stack     ?expected
    ;; string comparisons are pretty weird, actually
    :string    '("abc" "def")       :string>?      :exec         '(true)
    :string    '("abd" "abc")       :string>?      :exec         '(false)
    :string    '("acc" "abc")       :string>?      :exec         '(false)
    :string    '("bbc" "abc")       :string>?      :exec         '(false)
    :string    '("def" "abc")       :string>?      :exec         '(false)
    :string    '("ab" "abc")        :string>?      :exec         '(true)
    :string    '("abc" "ab")        :string>?      :exec         '(false)
    :string    '("abc" "abc")       :string>?      :exec         '(false)
    :string    '("" "")             :string>?      :exec         '(false)
    ;; missing args
    :string    '("def")             :string>?      :exec         '()
    :string    '("def")             :string>?      :string         '("def")
    :string    '()                  :string>?      :exec         '()
    :string    '()                  :string>?      :string         '())


(tabular
  (fact ":string-max returns the 'larger' of the top two :string items"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :string    '("foo" "bar")   :string-max      :exec         '("foo")
    :string    '("bar" "foo")   :string-max      :exec         '("foo")
    :string    '("foo" "foo")   :string-max      :exec         '("foo")
    ; ;; missing args
    :string    '("bar")         :string-max      :string        '("bar")
    :string    '()              :string-max      :exec         '()
    )


(tabular
  (fact ":string-min returns the 'smaller' of the top two :string items"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :string    '("foo" "bar")    :string-min      :exec         '("bar")
    :string    '("bar" "foo")    :string-min      :exec         '("bar")
    :string    '("foo" "foo")    :string-min      :exec         '("foo")
    ; ;; missing args
    :string    '("bar")          :string-min      :string        '("bar")
    :string    '()               :string-min      :exec         '()
    )

; ;; movable
