(ns push.instructions.base.string_test
  (:require [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.string])
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
    :string    '("88")       :string->scalar      :scalar       '(88)
    :string    '("88.8")     :string->scalar      :scalar       '(88.8)
    :string    '("6.2e7")    :string->scalar     :scalar       '(6.2e7)
    :string    '("88N")      :string->scalar     :scalar       '(88N)
    :string    '("3/11")     :string->scalar     :scalar       '(3/11)
  )


(tabular
  (fact "the very very difficult :string->scalar instruction takes the first number it finds"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction       ?get-stack     ?expected
    :string    '("hey 88")     :string->scalar     :scalar       '(88)
    :string    '("(neg 88.8")  :string->scalar     :scalar       '(88.8)
    :string    '("\n\n6.2e7")  :string->scalar     :scalar       '(6.2e7)
    :string    '("{:x 88N}")   :string->scalar     :scalar       '(88N)
    :string    '("(+ 3/11 9)") :string->scalar     :scalar       '(3/11)
  )

(tabular
  (fact "the very very difficult :string->scalar instruction works when no string is present"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction       ?get-stack     ?expected
    :string    '("hey88")      :string->scalar     :scalar       '()
    :string    '("(88.8)")     :string->scalar     :scalar       '()
    :string    '("")           :string->scalar     :scalar       '()
    :string    '("string")     :string->scalar     :scalar       '()
    :string    '(":foo")       :string->scalar     :scalar       '()
  )


(tabular
  (fact ":string->scalars returns a complete vector of numeric items in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction       ?get-stack     ?expected
    :string    '("")            :string->scalars     :scalars       '([])
    :string    '("8, 9, 10")    :string->scalars     :scalars       '([8 9 10])
    :string    '("17.2 3.1 a")  :string->scalars     :scalars       '([17.2 3.1])
    :string    '("nothing 5M")  :string->scalars     :scalars       '([5M])
    :string    '("1e7 0x8 061") :string->scalars     :scalars       '([1.0E7 8 49])
    :string    '("፹ 〺 Ⅸ ⅔")   :string->scalars     :scalars       '([])
    :string    '("(->Complex -∞ 2/3)")
                                :string->scalars     :scalars       '([2/3])
  )



(tabular
  (fact " :boolean->string, :code->string, :exec->string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected
    :boolean    '(false)       :boolean->string      :string       '("false")
    :boolean    '(true)        :boolean->string      :string       '("true")
    :boolean    '()            :boolean->string      :string       '()

    :char       '(\Y)          :char->string         :string       '("Y")
    :char       '(\u262F)      :char->string         :string       '("☯")
    :char       '()            :char->string         :string       '()

    :code     '((88 :code-do)) :code->string         :string       '("(88 :code-do)")
    :code     '([1 [3 5]])     :code->string         :string       '("[1 [3 5]]")
    :code     '(''99)          :code->string         :string       '("(quote (quote 99))")
    :code     '(1/8)           :code->string         :string       '("1/8")
    :code     '({:a [1 2]})    :code->string         :string       '("{:a [1 2]}")
    :code     '()              :code->string         :string       '()

    :exec     '((88 :code-do)) :exec->string         :string       '("(88 :code-do)")
    :exec     '([1 [3 5]])     :exec->string         :string       '("[1 [3 5]]")
    :exec     '(''99)          :exec->string         :string       '("(quote (quote 99))")
    :exec     '(1/8)           :exec->string         :string       '("1/8")
    :exec     '({:a [1 2]})    :exec->string         :string       '("{:a [1 2]}")
    :exec     '()              :exec->string         :string       '())


;; quotable

(tabular
  (fact ":string->code move the top :string item to :code"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items       ?instruction      ?get-stack     ?expected
    ;; move it!
    :string       '("92")    :string->code         :code       '("92")
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
  (fact ":string-concat checks for oversized results and pushes an `:error` if one arises"
    (register-type-and-check-instruction-in-this-interpreter
      teeny
      ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :string     '("foo" "bar")
                          :string-concat    :string
                                                           '("barfoo")
    :string     '("fools" "barge")
                          :string-concat    :string
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
     :char    '(\w)}         :string-conjchar      {:string '("foow")
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
                                                   :boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\q)
     :string '("ere he was able")}
                                :string-containschar?
                                                  {:char '()
                                                   :string '()
                                                   :boolean '(false)} )


(tabular
  (fact ":string-contains? returns true if the second string is in the first"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction  ?get-stack   ?expected
    :string    '("foo" "bar")       :string-contains?  :boolean     '(false)
    :string    '("foo" "barfoobar") :string-contains?  :boolean     '(false)
    :string    '("barfoobar" "foo") :string-contains?  :boolean     '(true)
    :string    '("foo" "foo")       :string-contains?  :boolean     '(true)
    :string    '("" "foo")          :string-contains?  :boolean     '(false)
    :string    '("foo" "")          :string-contains?  :boolean     '(true)
    :string    '("" "")             :string-contains?  :boolean     '(true))


(tabular
  (fact ":string-emptystring? returns true if the argument is exactly \"\""
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :scalar '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\a)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :scalar '(8)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '()
                                                   :scalar '(-1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-indexofchar
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}
                                :string-indexofchar
                                                  {:char '(\e)
                                                   :string '()
                                                   :scalar '()})


(tabular
  (fact ":string-last returns the last :char of the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; length
    :string    '("foo")         :string-length  :scalar     '(3)
    :string    '(" foo ")       :string-length  :scalar     '(5)
    :string    '("foo\n\t\t\n") :string-length  :scalar     '(7)
    :string    '("\u2665")      :string-length  :scalar     '(1)
    ;; because Java is weird enough to let you inline backspace characters
    :string    '("\b8" )        :string-length  :scalar     '(2)
    ;; missing args
    :string    '()              :string-length  :scalar     '())



(tabular
  (fact ":string-nth returns the indexed character (modulo length)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :string  '("ere he was able")}
                                :string-nth
                                                  {:char '(\e)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2)
     :string '("ere he was able")}
                                :string-nth
                                                  {:char '(\l)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(16)
     :string '("ere he was able")}
                                :string-nth
                                                  {:char '(\r)
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(16)
     :string '("")}
                                :string-nth
                                                  {:char '()
                                                   :string '()
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:scalar '()
     :string '("ere he was able")}
                                :string-nth
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :string '()}
                                :string-nth
                                                  {:char '()
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
                                                   :scalar '(4)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-occurrencesofchar
                                                  {:char '()
                                                   :string '()
                                                   :scalar '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:char   '()
     :string '("ere he was able")}
                                :string-occurrencesofchar
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\e)
     :string '()}
                                :string-occurrencesofchar
                                                  {:char '(\e)
                                                   :string '()
                                                   :scalar '()})


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
                                                   :string '("r h was abl")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space)
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :string '("erehewasable")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z)
     :string '("ere he was able")}
                                :string-removechar
                                                  {:char '()
                                                   :string '("ere he was able")}
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
                                :string-replace  :string     '("aXbaaXbb")
    :string    '("Napoleon" "he" "ere he was able")
                                :string-replace  :string     '("ere Napoleon was able")
    :string    '(" " "\n" "foo\n\t\t\n")
                                :string-replace  :string     '("foo \t\t ")
    :string    '("" "a" "aabbaaabbb")
                                :string-replace  :string     '("bbbbb")
    :string    '("X" "" "aabbaaabbb")
                                :string-replace  :string     '("XaXaXbXbXaXaXaXbXbXbX")
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
                          :string-replace    :string
                                                           '("foofoo")
    :string     '("foo" "a" "aaaa")
                          :string-replace    :string
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
                                :string-replacefirst  :string     '("aXbaaabbb")
    :string    '("Napoleon" "he" "ere he was able")
                                :string-replacefirst  :string     '("ere Napoleon was able")
    :string    '(" " "\n" "foo\n\t\t\n")
                                :string-replacefirst  :string     '("foo \t\t\n")
    :string    '("" "a" "aabbaaabbb")
                                :string-replacefirst  :string     '("abbaaabbb")
    :string    '("X" "" "aabbaaabbb")
                                :string-replacefirst  :string     '("Xaabbaaabbb")
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
                                                   :string '("frf hf was ablf")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space \•)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :string '("ere•he•was•able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z \q)
     :string '("ere he was able")}
                                :string-replacechar
                                                  {:char '()
                                                   :string '("ere he was able")}
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
                                                   :string '("fre he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\space \•)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :string '("ere•he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:char   '(\z \q)
     :string '("ere he was able")}
                                :string-replacefirstchar
                                                  {:char '()
                                                   :string '("ere he was able")}
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
     :char    '(\O)}          :string-setchar        {:string '("fOo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :scalar  '(6)
     :char    '(\O)}          :string-setchar        {:string '("Ooo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :scalar  '(-2)
     :char    '(\O)}          :string-setchar        {:string '("fOo")
                                                     :char '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("")
     :scalar  '(11)
     :char    '(\O)}          :string-setchar        {:string '("O")
                                                     :char '()})



(tabular
  (fact ":string-shatter pushes all the letters as individual strings"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; shatter
    :string    '("foo")         :string-shatter  :string     '("f" "o" "o")
    :string    '("foo\n\t")     :string-shatter  :string     '("f" "o" "o" "\n" "\t")
    :string    '("\u2665")      :string-shatter  :string     '("\u2665")
    :string    '("" "x")        :string-shatter  :string     '("x")
    ;; missing args
    :string    '()              :string-shatter  :string     '())




(tabular
  (fact ":string-solid? returns true if there is no whitespace anywhere in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; we good here?
    :string    '("foo")         :string-solid?  :boolean     '(true)
    :string    '("foo_bar")     :string-solid?  :boolean     '(true)
    :string    '("foo/bar")     :string-solid?  :boolean     '(true)
    :string    '("a\u1722b")    :string-solid?  :boolean     '(true)
    :string    '("")            :string-solid?  :boolean     '(false)
    :string    '("foo bar")     :string-solid?  :boolean     '(false)
    :string    '("foo\nbar")    :string-solid?  :boolean     '(false)
    :string    '(" foo")        :string-solid?  :boolean     '(false)
    ;; missing args
    :string    '()              :string-solid?  :boolean     '())



(tabular
  (fact ":string-spacey? returns true if there is no NON-whitespace anywhere in the string"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; we good here?
    :string    '("foo")         :string-spacey?  :boolean     '(false)
    :string    '("       ")     :string-spacey?  :boolean     '(true)
    :string    '("\n\n\n\n")    :string-spacey?  :boolean     '(true)
    :string    '("\n\n\n\n.")    :string-spacey?  :boolean    '(false)
    :string    '("")            :string-spacey?  :boolean     '(false)
    ;; missing args
    :string    '()              :string-spacey?  :boolean     '())


(tabular
  (fact ":string-splitonspaces pushes the space-delimited parts of :string onto the stack"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; chunks
    :string    '("a b c d e")   :string-splitonspaces  :string     '("a" "b" "c" "d" "e")
    :string    '(" foo ")       :string-splitonspaces  :string     '("" "foo")
    :string    '("\na\tb\tc\n\n")
                                :string-splitonspaces  :string   '("" "a" "b" "c")
    :string    '("\"\"")        :string-splitonspaces  :string     '("\"\"")
    :string    '("")            :string-splitonspaces  :string     '("")
    ;; missing args
    :string    '()              :string-splitonspaces  :string     '())



(tabular
  (fact ":string-substring clips out the substring determined by two :int args (with cropping and sorting)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(2 12)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:string '("e he was a")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(12 2)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:string '("e he was a")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(33 -712)                    ;; these get cropped to 0, count s
     :string   '("ere he was able")}
                                :string-substring
                                                  {:string '("ere he was able")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar  '(4 4)
     :string   '("ere he was able")}
                                :string-substring
                                                  {:string '("")})


(tabular
  (fact ":string-take returns first (mod idx (count string)) characters"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(8)
     :string '("ere he was able")}
                                :string-take
                                                  {:string '("ere he w")
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(-3)
     :string '("ere he was able")}
                                :string-take
                                                  {:string '("ere he was a")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(99)
     :string '("ere he was able")}
                                :string-take
                                                  {:string '("ere he wa")
                                                   :scalar  '()}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar    '(99)
     :string '("")}
                                :string-take
                                                  {:string '("")
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
    :string    '("a" "" "b")   :string-stackdepth   :scalar      '(3)
    :string    '("nn\tmm")     :string-stackdepth   :scalar      '(1)
    :string    '()             :string-stackdepth   :scalar      '(0))


(tabular
  (fact ":string-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :string    '("foo" "bar")  :string-empty?   :boolean     '(false)
    :string    '()             :string-empty?   :boolean     '(true))


; ;; equatable


(tabular
  (fact ":string-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

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


(tabular
  (fact ":string-max returns the 'larger' of the top two :string items"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :string    '("foo" "bar")   :string-max      :string        '("foo")
    :string    '("bar" "foo")   :string-max      :string        '("foo")
    :string    '("foo" "foo")   :string-max      :string        '("foo")
    ; ;; missing args
    :string    '("bar")         :string-max      :string        '("bar")
    :string    '()              :string-max      :string        '()
    )


(tabular
  (fact ":string-min returns the 'smaller' of the top two :string items"
    (register-type-and-check-instruction
        ?set-stack ?items string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction  ?get-stack     ?expected
    ;; note: these use (compare A B), not (< A B)
    :string    '("foo" "bar")    :string-min      :string        '("bar")
    :string    '("bar" "foo")    :string-min      :string        '("bar")
    :string    '("foo" "foo")    :string-min      :string        '("foo")
    ; ;; missing args
    :string    '("bar")          :string-min      :string        '("bar")
    :string    '()               :string-min      :string        '()
    )

; ;; movable
