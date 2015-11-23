(ns push.instructions.standard.vector_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.types.standard.vector])
  )


(fact "standard-vector-type knows some instructions"
  (keys (:instructions standard-vector-type)) =>
    (contains [:vector-concat :vector-dup] :in-any-order :gaps-ok))


(tabular
  (fact "`vector-butlast` pushes the butlast of the first :vector back onto :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}          :vector-butlast     {:vector   '([1 2])
                                                    :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([])
     :code    '(9.9)}          :vector-butlast     {:vector   '([])  ;; NOTE!
                                                    :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :code    '()}             :vector-butlast     {:vector   '([1 2])
                                                    :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-concat` concatenates the top two vectors"
    (register-type-and-check-instruction
      ?set-stack ?items standard-vector-type ?instruction ?get-stack) => 
    ?expected)

    ?set-stack  ?items                 ?instruction      ?get-stack   ?expected
    :vector       '([3 4] [1 2])     :vector-concat      :vector       '([1 2 3 4])
    :vector       '([] [1 2])        :vector-concat      :vector       '([1 2])
    :vector       '([3 4] [])        :vector-concat      :vector       '([3 4])
    :vector       '([] [])           :vector-concat      :vector       '([])
    :vector       '([1 2 3])         :vector-concat      :vector       '([1 2 3])
    )


(tabular
  (fact "`vector-conj` conjes the top :foo item onto the top :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}          :vector-conj      {:vector   '([1 2 3 9.9])
                                                 :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}          :vector-conj      {:vector   '([9.9])
                                                 :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '()}             :vector-conj      {:vector   '([1 2 3])
                                                 :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-contains?` pushes `true` if the top :code item is present in the top :vector vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(2)}         :vector-contains?    {:vector  '()
                                                  :code    '()
                                                  :boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 9 3])
     :code    '(2)}         :vector-contains?    {:vector  '()
                                                  :code    '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(2)}         :vector-contains?    {:vector  '()
                                                  :code    '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '([1 2 3])}   :vector-contains?    {:vector  '()
                                                  :code    '()
                                                  :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-do*each` constructs a complex continuation (see below)"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks             ?instruction    ?expected

    {:vector '([1 2 3])
     :exec   '(:bar)}       :vector-do*each   {:vector '()
                                                :exec '((1 :bar [2 3]
                                                    :vector-do*each :bar))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector '([1])
     :exec   '(:bar)}       :vector-do*each   {:vector '()
                                                :exec '((1 :bar []
                                                    :vector-do*each :bar))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector '([1 2 3])
     :exec   '( (9 99) )}   :vector-do*each   {:vector '()
                                                :exec '((1 (9 99) [2 3]
                                                    :vector-do*each (9 99)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector '([])
     :exec   '( (9 99) )}   :vector-do*each   {:vector '()
                                                :exec '((() (9 99) []
                                                    :vector-do*each (9 99)))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-emptyitem?` pushes an true to :boolean if the top :vector is empty"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:vector '([1 2 3])}   :vector-emptyitem?     { :vector '()
                                                    :boolean '(false)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector '([])}        :vector-emptyitem?     { :vector '()
                                                    :boolean '(true)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact "`vector-first` pushes the first item of the top :vector onto :code"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}          :vector-first     {:vector  '()
                                                  :code    '(1 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}          :vector-first     {:vector  '()   ;;; NOTE nil
                                                  :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '()}             :vector-first     {:vector  '()
                                                  :code    '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-indexof` pushes an :integer indicating where :code is in :vector (or -1)"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:vector  '([1 2 3])
     :code    '(3)}          :vector-indexof    {:vector    '()
                                                 :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '(99)}         :vector-indexof    {:vector    '()
                                                 :integer '(-1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 1])
     :code    '(1)}          :vector-indexof    {:vector    '()
                                                 :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-last` pushes the last item of the top :vector onto :code"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}          :vector-last     {:vector   '()
                                                 :code    '(3 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}          :vector-last     {:vector   '()   ;;; NOTE nil
                                                 :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '()}             :vector-last     {:vector   '()
                                                 :code    '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-length` pushes the length of the top :vector onto :integer"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector   '([1 2 3])
     :integer  '()}           :vector-length      {:vector    '()
                                                   :integer '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2])
     :integer  '()}           :vector-length      {:vector    '()
                                                   :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([])
     :integer  '()}           :vector-length      {:vector    '()
                                                   :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-new` pushes an empty :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:vector '([1 2 3])}        :vector-new        {:vector '([] [1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector '()}               :vector-new        {:vector '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-nth` pops an :integer to index the position in the nth :vector item to push to :code"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector   '([1 2 3])
     :integer  '(0)
     :code     '()}           :vector-nth       {:vector '()
                                                 :code  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(3)
     :code     '()}           :vector-nth       {:vector '()
                                                 :code  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(-7)
     :code     '()}           :vector-nth       {:vector '()
                                                 :code  '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([])
     :integer  '(2)
     :code     '()}           :vector-nth       {:vector '()
                                                 :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-occurrencesof` pushes an :integer how many copies of the top :code item occur in :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks             ?instruction             ?expected

    {:vector  '([1 2 3])
     :code    '(3)}         :vector-occurrencesof   {:vector    '()
                                                     :integer '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 1 3])
     :code    '(1)}         :vector-occurrencesof   {:vector    '()
                                                     :integer '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 1])
     :code    '(99)}        :vector-occurrencesof  {:vector    '()
                                                     :integer '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-portion` pops two :integer values and does some crazy math to extract a subvector from the top `:vector item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks standard-vector-type ?instruction) => (contains ?expected))

    ?new-stacks                   ?instruction          ?expected

    {:vector   '([1 2 3 4 5 6])
     :integer  '(2 3)}           :vector-portion     {:vector '([3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(2 2)}           :vector-portion     {:vector '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(11 2)}          :vector-portion     {:vector '([3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(22 -11)}        :vector-portion     {:vector '([1 2 3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(19 19)}         :vector-portion     {:vector '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(0 1)}           :vector-portion     {:vector '([1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(0 3)}           :vector-portion     {:vector '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3 4 5 6])
     :integer  '(3 0)}           :vector-portion     {:vector '([1 2 3])}
    )


(tabular
  (fact "`vector-shatter` pushes the items from the first :vector onto :code"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks             ?instruction        ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}       :vector-shatter    {:vector '()
                                                :code  '(1 2 3 9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}       :vector-shatter    {:vector '()   
                                                :code  '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '()}          :vector-shatter    {:vector '()
                                                :code  '(1 2 3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-remove` pops the top :vector and :code items, pushing the former purged of all appearances of the latter"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                  ?instruction     ?expected

    {:vector    '([1 2 3])
     :code      '(2)}           :vector-remove     {:vector '([1 3])
                                                    :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector    '([1 2 1])
     :code      '(1)}           :vector-remove     {:vector '([2])
                                                    :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector    '([1 2 3])
     :code      '(9)}           :vector-remove     {:vector '([1 2 3])
                                                    :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector    '([1 1 1 1])
     :code      '(1)}           :vector-remove     {:vector '([])
                                                    :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-replace` replaces all occurrences of :code/2 with :code/1"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) =>
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(99 2)}        :vector-replace    {:vector   '([1 99 3])
                                                  :code     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '(99 8)}        :vector-replace    {:vector   '([1 2 3])
                                                  :code     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([2 2 2])
     :code    '(3 2)}         :vector-replace    {:vector   '([3 3 3])
                                                  :code     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(99 2)}        :vector-replace    {:vector   '([])
                                                  :code     '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-replacefirst` replaces the first appearance of :code/2 with :code/1"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) =>
    (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:vector  '([1 2 1])
     :code    '(99 1)}     :vector-replacefirst    {:vector  '([99 2 1])
                                                    :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 1])
     :code    '(99 8)}     :vector-replacefirst    {:vector  '([1 2 1])
                                                    :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([2 2 2])
     :code    '(3 2)}      :vector-replacefirst    {:vector  '([3 2 2])
                                                    :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(99 2)}     :vector-replacefirst    {:vector  '([])
                                                    :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-rest` pushes the rest of the first :vector vector back onto :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks            ?instruction     ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}      :vector-rest    {:vector  '([2 3])
                                            :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}      :vector-rest    {:vector  '([])    ;;; NOTE not nil!
                                            :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([1 2 3])
     :code    '()}         :vector-rest    {:vector  '([2 3])
                                            :code    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-reverse` pushes the reverse of the first :vector back onto :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction          ?expected

    {:vector  '([1 2 3])
     :code    '(9.9)}          :vector-reverse       {:vector  '([3 2 1])
                                                      :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector  '([])
     :code    '(9.9)}          :vector-reverse       {:vector  '([])  
                                                      :code    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`vector-set` pops an :integer to index the position in the top :vector item to replace with the top :code"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:vector   '([1 2 3])
     :integer  '(0)
     :code     '(99)}        :vector-set        {:vector '([99 2 3])
                                                 :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(2)
     :code     '(99)}        :vector-set        {:vector '([1 2 99])
                                                 :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(-2)
     :code     '(99)}        :vector-set        {:vector '([1 99 3])
                                                 :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(11)
     :code     '(99)}        :vector-set        {:vector '([1 2 99])
                                                 :code  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(11)
     :code     '()}          :vector-set        {:vector '([1 2 3])
                                                 :integer  '(11)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([])
     :integer  '(11)
     :code     '(8)}         :vector-set        {:vector   '([]) ;; NOTE behavior!
                                                 :integer '()
                                                 :code '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`vector-take` pops an :integer to index the position in the top :vector item to trim to"
    (check-instruction-with-all-kinds-of-stack-stuff
      ?new-stacks standard-vector-type ?instruction) => 
    (contains ?expected))

    ?new-stacks                ?instruction         ?expected

    {:vector   '([1 2 3])
     :integer  '(1)}           :vector-take       {:vector  '([1])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2])
     :integer  '(0)}           :vector-take       {:vector  '([]) ;; NOTE empty
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(10)}          :vector-take       {:vector  '([1 2])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(-11)}         :vector-take       {:vector  '([1])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:vector   '([1 2 3])
     :integer  '(-12)}         :vector-take       {:vector  '([])
                                                   :integer '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
