(ns push.instructions.base.code_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.modules.code])            ;; sets up classic-code-module
  )


;; all the basic conversions

(tabular
  (fact ":code-fromboolean move the top :boolean item to :code;
         :code-frominteger
         :code-fromfloat
         :code-fromstring
         :code-fromchar"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; move it!
    :boolean    '(false)        :code-fromboolean      :code        '(false)
    :boolean    '()             :code-fromboolean      :code        '()
    :char       '(\y)           :code-fromchar         :code        '(\y)
    :char       '()             :code-fromchar         :code        '()
    :float      '(0.)           :code-fromfloat        :code        '(0.)
    :float      '()             :code-fromfloat        :code        '()
    :integer    '(88)           :code-frominteger      :code        '(88)
    :integer    '()             :code-frominteger      :code        '()
    :string     '("88")         :code-fromstring       :code        '("88")    
    :string     '()             :code-fromstring       :code        '()
    )



(tabular
  (fact ":code-append concats two :code items, wrapping them in lists first if they aren't already"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-append        :code        '((8 9 1.1))
    :code    '(2 3)                 :code-append        :code        '((3 2))
    :code    '(() 3)                :code-append        :code        '((3))
    :code    '(2 ())                :code-append        :code        '((2))
    :code    '(() ())               :code-append        :code        '(())
    :code    '(2)                   :code-append        :code        '(2))


(tabular
  (fact ":code-atom? pushes true to :boolean if the top :code is not a list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; are you alone?
    :code    '(1.1 '(8 9))         :code-atom?        :boolean        '(true)
    :code    '(() 8)               :code-atom?        :boolean        '(false)
    ;; …except in silence
    :code    '()                   :code-atom?        :boolean        '())


(tabular
  (fact ":code-cons conj's the second :code item onto the first, coercing it to a list if necessary"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-cons        :code        '(((8 9) 1.1))
    :code    '(2 3)                 :code-cons        :code        '((3 2))
    :code    '(() 3)                :code-cons        :code        '((3))
    :code    '(2 ())                :code-cons        :code        '((() 2))
    :code    '(() ())               :code-cons        :code        '((()))
    :code    '(2)                   :code-cons        :code        '(2))



(tabular
  (fact ":code-container returns the smallest, first container of code/1 in code/2"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '(8 (8 9))          :code-container        :code        '((8 9))
    :code    '(2 2)              :code-container        :code        '(())
    :code    '(2 (1 (2 (3))))    :code-container        :code        '((2 (3)))
    :code    '(() (()))          :code-container        :code        '((()))
    :code    '((3) (0 ((1 2) ((3) 4))))  
                                 :code-container        :code        '(((3) 4))
    :code    '((3) (0 ((1 (3)) ((3) 4))))  
                                 :code-container        :code        '((1 (3)))
    :code    '((1 (2)) (1 (2) 3))
                                 :code-container        :code        '(())
    :code    '(2)                :code-container        :code        '(2)
    )



(tabular
  (fact ":code-contains? returns true if the second item contains (or is) the first anywhere"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '(8 (8 9))          :code-contains?        :boolean        '(true)
    :code    '(2 2)              :code-contains?        :boolean        '(true)
    :code    '(2 3)              :code-contains?        :boolean        '(false)
    :code    '(() (()))          :code-contains?        :boolean        '(true)
    :code    '(2 ((1 2) (3 4)))  :code-contains?        :boolean        '(true)
    :code    '((1 (2)) (1 (2) 3))
                                 :code-contains?        :boolean        '(false)
    :code    '(2)                :code-contains?        :boolean        '())



(tabular
  (fact ":code-do executes the top :code item and :code-pop"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; do it
    :code    '((1.1) (8 9))         :code-do        :exec        '(((1.1) :code-pop))
    :code    '((1.1) (8 9))         :code-do        :code        '((1.1) (8 9))
    :code    '(2 3)                 :code-do        :exec        '((2 :code-pop))
    :code    '(2 3)                 :code-do        :code        '(2 3)
    :code    '(() 3)                :code-do        :exec        '((() :code-pop))
    :code    '()                    :code-do        :exec        '())


(tabular
  (fact ":code-do* executes the top :code item and :code-pop"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; JUST do it
    :code    '((1.1) (8 9))         :code-do*        :exec        '((1.1))
    :code    '((1.1) (8 9))         :code-do*        :code        '((8 9))
    :code    '(2 3)                 :code-do*        :exec        '(2)
    :code    '(2 3)                 :code-do*        :code        '(3)
    :code    '(() 3)                :code-do*        :exec        '(())
    :code    '()                    :code-do*        :exec        '())


(tabular
  (fact ":code-do*count does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(2 9)}         :code-do*count     {:exec '(
                                                      (2 0 :code-quote :foo :code-do*range))
                                                   :integer '(9)
                                                   :code '(:bar)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(-2 -9)}         :code-do*count     {:exec '((-2 :code-quote :foo))
                                                   :integer '(-9)
                                                   :code '(:bar)} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(0 -9)}         :code-do*count     {:exec '((0 :code-quote :foo))
                                                   :integer '(-9)
                                                   :code '(:bar)} 
    ; ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:code     '()
     :integer  '(0 -9)}         :code-do*count     {:exec '()
                                                   :integer '(0 -9)
                                                   :code '()} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :integer  '()}             :code-do*count     {:exec '()
                                                   :integer '()
                                                   :code '(:foo)})


(tabular
  (fact ":code-do*range does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(2 9)}         :code-do*range     {:exec '((9 :foo 
                                                    (8 2 :code-quote :foo :code-do*range)))
                                                   :integer '()
                                                   :code '(:bar)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(-2 -9)}         :code-do*range     {:exec '((-9 :foo 
                                                    (-8 -2 :code-quote :foo :code-do*range)))
                                                   :integer '()
                                                   :code '(:bar)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(2 2)}         :code-do*range     {:exec '((2 :foo))
                                                   :integer '()
                                                   :code '(:bar)} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ;; missing arguments
    {:code     '()
     :integer  '(-2 -9)}         :code-do*range     {:exec '()
                                                     :integer '(-2 -9)
                                                     :code '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :integer  '(-2)}         :code-do*range     {:exec '()
                                                     :integer '(-2)
                                                     :code '(:foo)})



(tabular
  (fact ":code-do*times does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(2 9)}         :code-do*times     {:exec '((:foo 
                                                     (1 :code-quote :foo :code-do*times)))
                                                   :integer '(9)
                                                   :code '(:bar)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(-2 -9)}       :code-do*times     {:exec '(:foo)
                                                   :integer '(-9)
                                                   :code '(:bar)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :integer  '(0 2)}          :code-do*times     {:exec '(:foo)
                                                   :integer '(2)
                                                   :code '(:bar)} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ;; missing arguments
    {:code     '()
     :integer  '(-2 -9)}        :code-do*times      {:exec '()
                                                     :integer '(-2 -9)
                                                     :code '()} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :integer  '()}           :code-do*times        {:exec '()
                                                     :integer '()
                                                     :code '(:foo)})


(tabular
  (fact ":code-first pushes the first item of the top :code item, if it's a list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; pick a card
    :code    '((1.1) (8 9))       :code-first        :code        '(1.1 (8 9))
    :code    '((2 3))             :code-first        :code        '(2)
    :code    '(() 3)              :code-first        :code        '(3)
    :code    '(2)                 :code-first        :code        '(2)
    :code    '(((3)))             :code-first        :code        '((3))
    :code    '()                  :code-first        :code        '())


(tabular
  (fact ":code-if pushes the second :code item to :exec if true, otherwise the first"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected

    {:code     '(:foo :bar)
     :boolean  '(true)}         :code-if            {:code '()
                                                     :boolean '()
                                                     :exec '(:bar)} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :boolean  '(false)}         :code-if            {:code '()
                                                     :boolean '()
                                                     :exec '(:foo)} 
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ; ; ;; missing arguments
    {:code     '(:foo)
     :boolean  '(false)}         :code-if            {:code '(:foo)
                                                     :boolean '(false)
                                                     :exec '()} 
    ; ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo :bar)
     :boolean  '()}         :code-if                {:code '(:foo :bar)
                                                     :boolean '()
                                                     :exec '()} )


(tabular
  (fact ":code-length pushes the count of the top :code item (1 if a literal) onto :integer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; pick a number
    :code    '((1 2 3) (8 9))     :code-length        :integer        '(3)
    :code    '((2))               :code-length        :integer        '(1)
    :code    '(() 3)              :code-length        :integer        '(0)
    :code    '(2)                 :code-length        :integer        '(1)
    :code    '((2 (3)))           :code-length        :integer        '(2)
    :code    '()                  :code-length        :integer        '())


(tabular
  (fact ":code-list puts the top 2 :code items into a list on the :code stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; stick 'em together
    :code    '((1.1) (8 9))         :code-list        :code        '(((8 9) (1.1)))
    :code    '(2 3)                 :code-list        :code        '((3 2))
    :code    '(() 3)                :code-list        :code        '((3 ()))
    :code    '(2 ())                :code-list        :code        '((() 2))
    :code    '(() ())               :code-list        :code        '((() ()))
    :code    '(2)                   :code-list        :code        '(2))



(tabular
  (fact ":code-map does complicated things involving continuations (see tests)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '((1 2 3) :bar)
     :exec  '(:code-length)}   :code-map     {:exec '((:code-quote () 
                                               (:code-quote 1 :code-length) :code-cons
                                               (:code-quote 2 :code-length) :code-cons
                                               (:code-quote 3 :code-length) :code-cons))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(1 :bar)
     :exec  '(:code-length)}   :code-map     {:exec '((:code-quote () 
                                               (:code-quote 1 :code-length) :code-cons))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code  '(() :bar)
     :exec  '(99)}             :code-map     {:exec '((:code-quote ()))})


(tabular
  (fact ":code-member? pushes true if the second item is found in the root of the first"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; 
    :code    '((1 2) 2)           :code-member?        :boolean        '(true)
    :code    '((2 3) 4)           :code-member?        :boolean        '(false)
    :code    '(() 3)              :code-member?        :boolean        '(false)
    :code    '(((3) 3) (3))       :code-member?        :boolean        '(true)
    :code    '(3 (3 4))           :code-member?        :boolean        '(false)
    :code    '(() ())             :code-member?        :boolean        '(false)
    :code    '()                  :code-member?        :boolean        '())


(tabular
  (fact ":code-noop don't do shit"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; be vewwy quiet
    :code    '(1.1 '(8 9))         :code-noop        :code        '(1.1 '(8 9))
    :code    '()                   :code-noop        :code        '())
     

(tabular
  (fact ":code-nth takes the nth item of :code, using that modulo trick"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :integer '(1)}            :code-nth     {:code '(2 :bar)
                                              :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :integer '(10)}            :code-nth     {:code '(2 :bar)
                                              :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '((1 2 3) :bar)
     :integer '(-4)}            :code-nth     {:code '(3 :bar)
                                              :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '(77)
     :integer '(1)}            :code-nth     {:code '(77)
                                              :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code    '()
     :integer '(1)}            :code-nth     {:code '()
                                              :integer '(1)})




(tabular
  (fact ":code-null? pushes true to :boolean if the top :code item is an empty list, false otherwise"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; an echoing sound is heard
    :code    '(1.1 (8 9))         :code-null?        :boolean        '(false)
    :code    '(() 8)               :code-null?        :boolean        '(true)
    
;;;;; PROBLEM HERE
    :code    '(() 8)              :code-null?        :boolean        '(true)
    ;; …except in silence
    :code    '()                   :code-null?        :boolean        '())


(tabular
  (fact ":code-quote moves the top :exec item to :code"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-code-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    {:exec '(1 2 3)
     :code '(false)}           :code-quote            {:exec '(2 3)
                                                       :code '(1 false)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '((1 2) 3)
     :code '(true)}            :code-quote            {:exec '(3)
                                                       :code '((1 2) true)} 
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;; missing arguments
    {:exec '()
     :code '(true)}            :code-quote            {:exec '()
                                                       :code '(true)})

(tabular
  (fact ":code-rest pushes all but the first item of a :code list; if the item is not a list, pushes an empty list"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; what's left for me now?
    :code    '((1 2 3) (8 9))     :code-rest        :code        '((2 3) (8 9))
    :code    '((2))               :code-rest        :code        '(())
    :code    '(() 3)              :code-rest        :code        '(() 3)
    :code    '(2)                 :code-rest        :code        '(())
    :code    '((2 (3)))           :code-rest        :code        '(((3)))
    :code    '()                  :code-rest        :code        '())


(tabular
  (fact ":code-size counts the number of points in the top :code item"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '((1 2 3) (8 9))     :code-size        :integer        '(4)
    :code    '((2))               :code-size        :integer        '(2)
    :code    '(() 3)              :code-size        :integer        '(1)
    :code    '(2)                 :code-size        :integer        '(1)
    :code    '((1 (2 (3))))       :code-size        :integer        '(6)
    :code    '([1 2 3])           :code-size        :integer        '(4)
    :code    '(#{1 2 3})          :code-size        :integer        '(4)
    :code    '({1 2 3 4})         :code-size        :integer        '(7)
    :code    '()                  :code-size        :integer        '())


(tabular
  (fact ":code-wrap returns a :the top :code item in an extra list layer"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; wrap
    :code    '(1)               :code-wrap      :code        '((1))
    :code    '((3 4))           :code-wrap      :code        '(((3 4)))
    :code    '(())              :code-wrap      :code        '((()))
    ;; missing args     
    :code    '()                :code-wrap      :code          '())

;; visible


(tabular
  (fact ":code-stackdepth returns the number of items on the :code stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :code    '(1.1 2.2 3.3)      :code-stackdepth   :integer      '(3)
    :code    '(1.0)              :code-stackdepth   :integer      '(1)
    :code    '()                 :code-stackdepth   :integer      '(0))
   

(tabular
  (fact ":code-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :code    '(0.2 1.3e7)        :code-empty?   :boolean     '(false)
    :code    '()                 :code-empty?   :boolean     '(true))


; ;; equatable


(tabular
  (fact ":code-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :code    '((1 2) (3 4))     :code-equal?      :boolean        '(false)
    :code    '((3 4) (1 2))     :code-equal?      :boolean        '(false)
    :code    '((1 2) (1 2))     :code-equal?      :boolean        '(true)
    ;; missing args     
    :code    '((3 4))           :code-equal?      :boolean        '()
    :code    '((3 4))           :code-equal?      :code           '((3 4))
    :code    '()                :code-equal?      :boolean        '()
    :code    '()                :code-equal?      :code           '())


(tabular
  (fact ":code-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-code-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :code    '((1) (88))       :code-notequal?      :boolean      '(true)
    :code    '((88) (1))       :code-notequal?      :boolean      '(true)
    :code    '((1) (1))        :code-notequal?      :boolean      '(false)
    ;; missing args    
    :code    '((88))           :code-notequal?      :boolean      '()
    :code    '((88))           :code-notequal?      :code         '((88))
    :code    '()               :code-notequal?      :boolean      '()
    :code    '()               :code-notequal?      :code         '())


; ;; movable