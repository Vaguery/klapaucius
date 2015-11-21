(ns push.instructions.standard.vectorized_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.types.standard.vectorized])
  )


(def foo-type (t/make-type :foo
                           :recognizer number?
                           :attributes #{:foo}))

(def foos-type (build-vectorized-type foo-type))


(fact "foos-type knows some instructions"
  (keys (:instructions foos-type)) =>
    (contains [:foos-concat :foos-dup] :in-any-order :gaps-ok))


(tabular
  (fact "`foos-butlast` pushes the butlast of the first :foos vector back onto :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-butlast     {:foos   '([1 2])
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-butlast     {:foos   '()   ;;; NOTE nil
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-butlast     {:foos   '([1 2])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-concat` concatenates the top two foos vectors"
    (register-type-and-check-instruction
        ?set-stack ?items foos-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items                 ?instruction      ?get-stack   ?expected
    :foos       '([3 4] [1 2])         :foos-concat      :foos       '([1 2 3 4])
    :foos       '([] [1 2])            :foos-concat      :foos       '([1 2])
    :foos       '([3 4] [])            :foos-concat      :foos       '([3 4])
    :foos       '([] [])               :foos-concat      :foos       '([])
    :foos       '([1 2 3])             :foos-concat      :foos       '([1 2 3])
    )


(tabular
  (fact "`foos-conj` conjes the top :foo item onto the top :foos vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-conj        {:foos   '([1 2 3 9.9])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-conj        {:foos   '([9.9])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-conj        {:foos   '([1 2 3])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-contains?` pushes `true` if the top :foo item is present in the top :foos vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(2)}            :foos-contains?    {:foos    '()
                                                  :foo     '()
                                                  :boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 9 3])
     :foo    '(2)}            :foos-contains?    {:foos    '()
                                                  :foo     '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(2)}            :foos-contains?    {:foos    '()
                                                  :foo     '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '([1 2 3])}      :foos-contains?    {:foos    '()
                                                  :foo     '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-do*each` constructs a complex continuation (see below)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction    ?expected

    {:foos   '([1 2 3])
     :exec   '(:bar)}       :foos-do*each   {:foos '()
                                             :exec '((1 :bar [2 3]
                                                    :foos-do*each :bar))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1])
     :exec   '(:bar)}       :foos-do*each   {:foos '()
                                             :exec '((1 :bar []
                                                    :foos-do*each :bar))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :exec   '( (9 99) )}   :foos-do*each   {:foos '()
                                             :exec '((1 (9 99) [2 3]
                                                    :foos-do*each (9 99)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :exec   '( (9 99) )}   :foos-do*each   {:foos '()
                                             :exec '((() (9 99) []
                                                    :foos-do*each (9 99)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-emptyitem?` pushes an true to :boolean if the top :foos is empty"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:foos '([1 2 3])}        :foos-emptyitem?     { :foos '()
                                                 :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([])}             :foos-emptyitem?     { :foos '()
                                                 :boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact "`foos-first` pushes the first item of the top :foos vector onto :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-first       {:foos   '()
                                                 :foo    '(1 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-first       {:foos   '()   ;;; NOTE nil
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-first       {:foos   '()
                                                 :foo    '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-indexof` pushes an :integer indicating where :foo is in :foos (or -1)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3])
     :foo    '(3)}          :foos-indexof       {:foos    '()
                                                 :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '(99)}         :foos-indexof       {:foos    '()
                                                 :integer '(-1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 1])
     :foo    '(1)}          :foos-indexof       {:foos    '()
                                                 :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-last` pushes the last item of the top :foos vector onto :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-last        {:foos   '()
                                                 :foo    '(3 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-last        {:foos   '()   ;;; NOTE nil
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-last        {:foos   '()
                                                 :foo    '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-length` pushes the length of the top :foos vector onto :integer"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :integer  '()}           :foos-length        {:foos    '()
                                                   :integer '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2])
     :integer  '()}           :foos-length        {:foos    '()
                                                   :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :integer  '()}           :foos-length        {:foos    '()
                                                   :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-new` pushes an empty :foos vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:foos '([1 2 3])}        :foos-new        {:foos '([] [1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '()}               :foos-new        {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-nth` pops an :integer to index the position in the nth :foos item to push to :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :integer  '(0)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(3)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(-7)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :integer  '(2)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-occurrencesof` pushes an :integer how many :foo occur in :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3])
     :foo    '(3)}         :foos-occurrencesof   {:foos    '()
                                                  :integer '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 1 3])
     :foo    '(1)}         :foos-occurrencesof   {:foos    '()
                                                  :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 1])
     :foo    '(99)}        :foos-occurrencesof  {:foos    '()
                                                  :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-portion` pops two :integer values and does some crazy math to extract a subvector from the top `:foos item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3 4 5 6])
     :integer  '(2 3)}          :foos-portion        {:foos '([3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(2 2)}          :foos-portion        {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(11 2)}          :foos-portion        {:foos '([3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(22 -11)}          :foos-portion        {:foos '([1 2 3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(19 19)}          :foos-portion        {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(0 1)}          :foos-portion        {:foos '([1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(0 3)}          :foos-portion        {:foos '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :integer  '(3 0)}          :foos-portion        {:foos '([1 2 3])}
    )


(tabular
  (fact "`foos-shatter` pushes the items from the first :foos vector onto :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}       :foos-shatter       {:foos '()
                                                :foo  '(1 2 3 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}       :foos-shatter       {:foos '()   
                                                :foo  '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}          :foos-shatter       {:foos '()
                                                :foo  '(1 2 3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-remove` pops the top :foos and :foo items, pushing the former purged of all appearances of the latter"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :foo      '(2)}           :foos-remove     {:foos '([1 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 1])
     :foo      '(1)}           :foos-remove     {:foos '([2])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :foo      '(9)}           :foos-remove     {:foos '([1 2 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 1 1 1])
     :foo      '(1)}           :foos-remove     {:foos '([])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-replace` replaces all occurrences of :foo/2 with :foo/1"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(99 2)}          :foos-replace    {:foos    '([1 99 3])
                                                  :foo     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '(99 8)}          :foos-replace    {:foos    '([1 2 3])
                                                  :foo     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([2 2 2])
     :foo    '(3 2)}          :foos-replace    {:foos    '([3 3 3])
                                                  :foo     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(99 2)}          :foos-replace    {:foos    '([])
                                                  :foo     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-replacefirst` replaces the first appearance of :foo/2 with :foo/1"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 1])
     :foo    '(99 1)}       :foos-replacefirst    {:foos  '([99 2 1])
                                                  :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 1])
     :foo    '(99 8)}       :foos-replacefirst    {:foos  '([1 2 1])
                                                  :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([2 2 2])
     :foo    '(3 2)}        :foos-replacefirst    {:foos  '([3 2 2])
                                                  :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(99 2)}       :foos-replacefirst    {:foos  '([])
                                                  :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-rest` pushes the rest of the first :foos vector back onto :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-rest       {:foos   '([2 3])
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-rest       {:foos   '([])    ;;; NOTE not nil!
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-rest       {:foos   '([2 3])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-reverse` pushes the reverse of the first :foos vector back onto :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :foo    '(9.9)}          :foos-reverse       {:foos   '([3 2 1])
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9.9)}          :foos-reverse       {:foos   '([])  
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-set` pops an :integer to index the position in the top :foos item to replace with the top :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :integer  '(0)
     :foo      '(99)}          :foos-set        {:foos '([99 2 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(2)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(-2)
     :foo      '(99)}          :foos-set        {:foos '([1 99 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(11)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(11)
     :foo      '()}          :foos-set          {:foos '([1 2 3])
                                                 :integer  '(11)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :integer  '(11)
     :foo      '(8)}          :foos-set       {:foos   '([]) ;; NOTE behavior!
                                               :integer '()
                                               :foo '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-take` pops an :integer to index the position in the top :foos item to trim to"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :integer  '(1)}           :foos-take        {:foos    '([1])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2])
     :integer  '(0)}           :foos-take        {:foos    '([]) ;; NOTE empty
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(10)}           :foos-take        {:foos    '([1 2])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(-11)}           :foos-take        {:foos    '([1])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :integer  '(-12)}           :foos-take        {:foos    '([])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
