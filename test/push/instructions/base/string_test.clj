(ns push.instructions.base.string_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.types.base.string])  ;; sets up classic-string-type
  )


;; all the conversions

(tabular
  (fact ":string-frominteger, :string-fromboolean, :string-fromcode, :string-fromexec, :string-fromfloat"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction           ?get-stack     ?expected
    :boolean    '(false)       :string-fromboolean      :string       '("false")
    :boolean    '(true)        :string-fromboolean      :string       '("true")
    :boolean    '()            :string-fromboolean      :string       '()

    :integer    '(11)          :string-frominteger      :string       '("11")
    :integer    '(-11)         :string-frominteger      :string       '("-11")
    :integer    '()            :string-frominteger      :string       '()

    :float      '(117.0)       :string-fromfloat        :string       '("117.0")
    :float      '(-0.3)        :string-fromfloat        :string       '("-0.3")
    :float      '()            :string-fromfloat        :string       '()

    :char       '(\Y)          :string-fromchar         :string       '("Y")
    :char       '(\u262F)      :string-fromchar         :string       '("☯")
    :char       '()            :string-fromchar         :string       '()

    :code     '((88 :code-do)) :string-fromcode         :string       '("(88 :code-do)")
    :code     '([1 [3 5]])     :string-fromcode         :string       '("[1 [3 5]]")
    :code     '(''99)          :string-fromcode         :string       '("(quote (quote 99))")
    :code     '(1/8)           :string-fromcode         :string       '("1/8")
    :code     '({:a [1 2]})    :string-fromcode         :string       '("{:a [1 2]}")
    :code     '()              :string-fromcode         :string       '()

    :exec     '((88 :code-do)) :string-fromexec         :string       '("(88 :code-do)")
    :exec     '([1 [3 5]])     :string-fromexec         :string       '("[1 [3 5]]")
    :exec     '(''99)          :string-fromexec         :string       '("(quote (quote 99))")
    :exec     '(1/8)           :string-fromexec         :string       '("1/8")
    :exec     '({:a [1 2]})    :string-fromexec         :string       '("{:a [1 2]}")
    :exec     '()              :string-fromexec         :string       '())


;; specific string behavior


(tabular
  (fact ":exec-string-iterate chops off characters from a string and 'applies' the top :exec item to them"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:integer-add)
     :string '("ere he was able")}           
                            :exec-string-iterate       
                                                  {:char   '(\e)
                                                   :string '()
                                                   :exec   '((:integer-add 
                                                              "re he was able" 
                                                              :exec-string-iterate
                                                              :integer-add))} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:integer-add)
     :string '("e")}           
                            :exec-string-iterate       
                                                  {:char   '(\e)
                                                   :string '()
                                                   :exec   '(:integer-add)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(:integer-add)
     :string '("")}           
                            :exec-string-iterate       
                                                  {:char   '()
                                                   :string '()
                                                   :exec   '(:integer-add)})




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
  (fact ":string-conjchar attaches the top :char the end of the top :string"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:string  '("foo")
     :char    '(\w)}         :string-conjchar      {:string '("foow")
                                                    :char '()}         )

(tabular
  (fact ":string-containschar? returns the true if the :char is in :string"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
  (fact ":string-nth returns the indexed character (modulo length)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(0)
     :string  '("ere he was able")}           
                                :string-nth       
                                                  {:char '(\e)
                                                   :string '()
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(-2)
     :string '("ere he was able")}           
                                :string-nth       
                                                  {:char '(\l)
                                                   :string '()
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(16)
     :string '("ere he was able")}           
                                :string-nth       
                                                  {:char '(\r)
                                                   :string '()
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(16)
     :string '("")}           
                                :string-nth       
                                                  {:char '()
                                                   :string '()
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:integer '()
     :string '("ere he was able")}           
                                :string-nth       
                                                  {:char '()
                                                   :string '("ere he was able")
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer '(0)
     :string '()}           
                                :string-nth       
                                                  {:char '()
                                                   :string '()
                                                   :integer '(0)})


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
  (fact ":string-removechar takes all occurrences of :char out"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
  (fact ":string-replacefirst the first occurrence of :str/2 with :str/1 in :str/3"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

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
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; lost the head
    :string    '("foo")         :string-rest  :string     '("oo")
    :string    '(" foo ")       :string-rest  :string     '("foo ")
    :string    '("\n\t\t\n")    :string-rest  :string     '("\t\t\n")
    :string    '("\"\"")        :string-rest  :string     '("\"")
    :string    '("")            :string-rest  :string     '("")
    ;; missing args
    :string    '()              :string-rest  :string     '())




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



(tabular
  (fact ":string-setchar changes the char in position :int to :char, modulo :string's length"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :integer '(1)
     :char    '(\O)}          :string-setchar        {:string '("fOo")
                                                     :char '()}         
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :integer '(6)
     :char    '(\O)}          :string-setchar        {:string '("Ooo")
                                                     :char '()}         
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("foo")
     :integer '(-2)
     :char    '(\O)}          :string-setchar        {:string '("fOo")
                                                     :char '()}         
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:string  '("")
     :integer '(11)
     :char    '(\O)}          :string-setchar        {:string '("O")
                                                     :char '()})



(tabular
  (fact ":string-shatter pushes all the letters as individual strings"
    (register-type-and-check-instruction
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
        ?set-stack ?items classic-string-type ?instruction ?get-stack) => ?expected)

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
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer  '(2 12)
     :string   '("ere he was able")}           
                                :string-substring       
                                                  {:string '("e he was a")} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer  '(12 2)
     :string   '("ere he was able")}           
                                :string-substring       
                                                  {:string '("e he was a")} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer  '(33 -712)                    ;; these get cropped to 0, count s
     :string   '("ere he was able")}           
                                :string-substring       
                                                  {:string '("ere he was able")} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer  '(4 4)                    
     :string   '("ere he was able")}           
                                :string-substring       
                                                  {:string '("")})


(tabular
  (fact ":string-take returns first (mod idx (count string)) characters"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-string-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(8)
     :string '("ere he was able")}           
                                :string-take       
                                                  {:string '("ere he w")
                                                   :integer '()} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(-3)
     :string '("ere he was able")}           
                                :string-take       
                                                  {:string '("ere he was a")
                                                   :integer '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(99)
     :string '("ere he was able")}           
                                :string-take       
                                                  {:string '("ere he wa")
                                                   :integer '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(99)
     :string '("")}           
                                :string-take       
                                                  {:string '("")
                                                   :integer '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '()
     :string '("ere he was able")}           
                                :string-take       
                                                  {:string '("ere he was able")
                                                   :integer '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:integer   '(99)
     :string '()}           
                                :string-take       
                                                  {:string '()
                                                   :integer '(99)})


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