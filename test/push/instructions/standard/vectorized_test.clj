(ns push.instructions.standard.vectorized_test
  (:require [push.interpreter.core :as i]
            [push.type.core :as t]
            [push.core :as push]
            [push.instructions.aspects  :as aspects]
            )
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.vectorized])
  )


;; fixtures

(def foo-type (-> (t/make-type :foo
                           :recognized-by number?
                           :attributes #{:foo})
                  aspects/make-comparable , ))


(def foos-type (build-vectorized-type foo-type))

(def teeny (-> (push/interpreter :config {:max-collection-size 9})
               (i/register-type , foos-type)))

;; tests

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
     :foo    '(9.9)}          :foos-butlast     {:foos   '([])  ;; NOTE!
                                                 :foo    '(9.9)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '()}             :foos-butlast     {:foos   '([1 2])
                                                 :foo    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-build` pushes a new :foos vector by consuming as many :foo items as the :scalar indicates"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(1)}            :foos-build       {:foos    '([7] [1 2 3])
                                                  :foo     '(77 777 7777)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(-1)}           :foos-build       {:foos    '([7 77 777] [1 2 3])
                                                  :foo     '(7777)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(3/2)}         :foos-build       {:foos    '([7 77] [1 2 3])
                                                  :foo     '(777 7777)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(0.7)}         :foos-build       {:foos    '([7] [1 2 3])
                                                  :foo     '(77 777 7777)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(0)}            :foos-build       {:foos    '([] [1 2 3])
                                                  :foo     '(7 77 777 7777)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '(7)
     :scalar  '(99)}           :foos-build       {:foos    '([] [1 2 3])
                                                  :foo     '(7)
                                                  :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([1 2 3])
     :foo     '()
     :scalar  '(99)}           :foos-build       {:foos    '([] [1 2 3])
                                                  :foo     '()
                                                  :scalar  '()})


(tabular
  (fact "foos-build produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:foos    '([1 2 3])
     :foo     '(7 77 777 7777)
     :scalar  '(3)}            :foos-build       {:foos    '([7 77 777] [1 2 3])
                                                  :foo     '(7777)
                                                  :error  '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:foos    '(1 2 3 4 5 6 7 8)
      :foo     '(7 77 777 7777)
      :scalar  '(3)}            :foos-build       {:foos    '(1 2 3 4 5 6 7 8)
                                                   :foo     '(7777)
                                                   :error   '({:item ":foos-build tried to push an overized item to :foos", :step 0})}
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
  (fact "foos-concat produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:foos    '([1 2 3][4])}   :foos-concat       {:foos    '([4 1 2 3])
                                                  :error  '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:foos    '([1 2 3][4] 5 6 7 8 9)}
                               :foos-concat       {:foos    '(5 6 7 8 9)
                                                   :error   '({:item ":foos-concat tried to push an overized item to :foos", :step 0})}
      ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
  (fact "foos-conj produces an error when the result is oversized"
    (check-instruction-here-using-this
      teeny
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:foos    '([1 2 3])
     :foo     '(9)}             :foos-conj       {:foos    '([1 2 3 9])
                                                  :foo     '()
                                                  :error   '()}
     ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
     {:foos    '([1 2 3] 4 5 6 7 8)
      :foo     '(9)}             :foos-conj       {:foos    '(4 5 6 7 8)
                                                   :foo     '()
                                                   :error   '({:item ":foos-conj tried to push an overized item to :foos", :step 0})}
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
  (fact "`foos-distinct` removes duplicate items from the :foos vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks              ?instruction     ?expected

    {:foos '([3 1 1 2 3 1 2 2])}
                             :foos-distinct
                                              {:foos '([3 1 2])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([1 2 3])}       :foos-distinct   {:foos '([1 2 3])}
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
                                             :exec '()}
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
  (fact "`foos-vfilter` pushes keeps items in the top item from the second"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                  ?instruction     ?expected

    {:foos '([2 3]
             [1 2 3 4 4 3 2 1])} :foos-vfilter     { :foos '([2 3 3 2])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([]
             [1 2 3 4 4 3 2 1])} :foos-vfilter     { :foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [1 2 3 4 4 3 2 1])} :foos-vfilter     { :foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [99 99 99 99])} :foos-vfilter     { :foos '([99 99 99 99])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact "`foos-vremove` pushes culls items in the top item from the second"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                  ?instruction     ?expected

    {:foos '([2 3]
             [1 2 3 4 4 3 2 1])} :foos-vremove     { :foos '([1 4 4 1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([]
             [1 2 3 4 4 3 2 1])} :foos-vremove     { :foos '([1 2 3 4 4 3 2 1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [1 2 3 4 4 3 2 1])} :foos-vremove     { :foos '([1 2 3 4 4 3 2 1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [99 99 99 99])} :foos-vremove         { :foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact "`foos-vsplit` pushes culls items in the top item from the second"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                  ?instruction     ?expected

    {:foos '([2 3]
             [1 2 3 4 4 3 2 1])} :foos-vsplit     { :exec '( ([2 3 3 2] [1 4 4 1]) )}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([]
             [1 2 3 4 4 3 2 1])} :foos-vsplit     { :exec '( ([] [1 2 3 4 4 3 2 1]) )}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [1 2 3 4 4 3 2 1])} :foos-vsplit     { :exec '( ([] [1 2 3 4 4 3 2 1]) )}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos '([99 99]
             [99 99 99 99])} :foos-vsplit         { :exec '( ([99 99 99 99] []) )}
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
  (fact "`foos-byexample` pops the top :foos item, and builds a new :foos from the :foo stack items (if there are enough), pushing both back to :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction          ?expected

    {:foos   '([1 2 3])
     :foo    '(9 8 7 6 5)}   :foos-byexample   {:foos   '([9 8 7] [1 2 3])
                                                  :foo    '(6 5)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1])
     :foo    '(9 8 7 6 5)}   :foos-byexample   {:foos   '([9] [1])
                                                  :foo    '(8 7 6 5)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :foo    '(9 8 7 6 5)}   :foos-byexample   {:foos   '([] [])
                                                  :foo    '(9 8 7 6 5)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '(9 8)}         :foos-byexample   {:foos   '([1 2 3])
                                                  :foo    '(9 8)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-generalize` pushes the first :foos vector onto :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1 2 3])
     :vector '()}            :foos-generalize   {:foos   '()
                                                 :vector    '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])
     :vector '()}            :foos-generalize   {:foos   '()
                                                 :vector    '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :vector '()}            :foos-generalize   {:foos   '()
                                                 :vector    '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-generalizeall` pushes every :foos vector onto :vector"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos   '([1] [2] [3])
     :vector '()}            :foos-generalizeall   {:foos   '()
                                                    :vector  '([1] [2] [3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([] [] [])
     :vector '([2])}            :foos-generalizeall   {:foos   '()
                                                    :vector  '([] [] [] [2])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :vector '()}            :foos-generalizeall   {:foos   '()
                                                    :vector  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-indexof` pushes a :scalar indicating where :foo is in :foos (or -1)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3])
     :foo    '(3)}          :foos-indexof       {:foos    '()
                                                 :scalar '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3])
     :foo    '(99)}         :foos-indexof       {:foos    '()
                                                 :scalar '(-1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 1])
     :foo    '(1)}          :foos-indexof       {:foos    '()
                                                 :scalar '(0)}
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
  (fact "`foos-length` pushes the length of the top :foos vector onto :scalar"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :scalar   '()}           :foos-length        {:foos    '()
                                                   :scalar  '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2])
     :scalar   '()}           :foos-length        {:foos    '()
                                                   :scalar  '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :scalar   '()}           :foos-length        {:foos    '()
                                                   :scalar  '(0)}
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
  (fact "`foos-nth` pops a :scalar to index the position in the nth :foos item to push to :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :scalar   '(0)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(3)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(1/2)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(-3/2)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(-7)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '(3)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :scalar   '(2)
     :foo      '()}           :foos-nth         {:foos '()
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-occurrencesof` pushes a :scalar how many :foo occur in :foos"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3])
     :foo    '(3)}         :foos-occurrencesof   {:foos    '()
                                                  :scalar  '(1)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 1 3])
     :foo    '(1)}         :foos-occurrencesof   {:foos    '()
                                                  :scalar  '(2)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 1])
     :foo    '(99)}        :foos-occurrencesof  {:foos    '()
                                                  :scalar  '(0)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-portion` pops two :scalar  values and does some crazy math to extract a subvector from the top `:foos item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3 4 5 6])
     :scalar   '(2 3)}            :foos-portion        {:foos '([3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(2 2)}            :foos-portion        {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(11 2)}           :foos-portion        {:foos '([3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(1.1 2.7)}        :foos-portion        {:foos '([2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(3.881 11/4)}     :foos-portion        {:foos '([4])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(22 -11)}         :foos-portion        {:foos '([1 2 3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(19 19)}          :foos-portion        {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(0 1)}            :foos-portion        {:foos '([1])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(0 3)}            :foos-portion        {:foos '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5 6])
     :scalar   '(3 0)}            :foos-portion        {:foos '([1 2 3])}
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
  (fact "`foos-sort` sorts the top :foos item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction     ?expected

    {:foos   '([3 2 1])}          :foos-sort       {:foos '([1 2 3])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([2 2 1 1 8 2])}    :foos-sort       {:foos '([1 1 2 2 2 8])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '( [1.1 2.2 -9e77])} :foos-sort       {:foos '([-9.0E77 1.1 2.2])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])}               :foos-sort       {:foos '([])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact "`foos-order` constructs a :scalars vector of indices from the top :foos item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction     ?expected

    {:foos    '([3 2 1])
     :scalars '()}          :foos-order       {:foos    '()
                                               :scalars '([2 1 0])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([3 9 1 3 9 1])
     :scalars '()}          :foos-order       {:foos    '()
                                               :scalars '([1 2 0 1 2 0])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos    '([:a :i :f :a :b :baz :foo :bar])
     :scalars '()}          :foos-order       {:foos    '()
                                               :scalars '([0 6 4 0 1 3 5 2])}
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
  (fact "`foos-set` pops a :scalar to index the position in the top :foos item to replace with the top :foo"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :scalar   '(0)
     :foo      '(99)}          :foos-set        {:foos '([99 2 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(2)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(1/2)
     :foo      '(99)}          :foos-set        {:foos '([1 99 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(3/2)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(81728176236763776723547234277M)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(-2)
     :foo      '(99)}          :foos-set        {:foos '([1 99 3])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(11)
     :foo      '(99)}          :foos-set        {:foos '([1 2 99])
                                                 :foo  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(11)
     :foo      '()}          :foos-set          {:foos '([1 2 3])
                                                 :scalar   '(11)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([])
     :scalar   '(11)
     :foo      '(8)}          :foos-set       {:foos   '([]) ;; NOTE behavior!
                                               :scalar  '()
                                               :foo '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact "`foos-take` pops a :scalar to index the position in the top :foos item to trim to"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:foos     '([1 2 3])
     :scalar   '(1)}           :foos-take        {:foos    '([1])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2])
     :scalar   '(0)}           :foos-take        {:foos    '([]) ;; NOTE empty
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2])
     :scalar   '(1/2)}         :foos-take        {:foos    '([1])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5])
     :scalar   '(2.4)}         :foos-take        {:foos    '([1 2 3])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5])
     :scalar   '(-2.4)}         :foos-take        {:foos    '([1 2 3 4])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3 4 5])
     :scalar   '(-1.4)}         :foos-take        {:foos    '([1 2 3 4 5])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(10)}           :foos-take        {:foos    '([1 2])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(-11)}           :foos-take        {:foos    '([1])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos     '([1 2 3])
     :scalar   '(-12)}           :foos-take        {:foos    '([])
                                                   :scalar  '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-pt-crossover` does crossover"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3 4 5 6]
               [99 88 77 66])
     :scalar '(3 1)}
                           :foos-pt-crossover       {:foos '([1 2 3 88 77 66]
                                                             [99 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3 4 5 6]
               [99 88 77 66])
     :scalar '(3/2 -11.7)}
                           :foos-pt-crossover       {:foos '([1 2 88 77 66] [99 3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([1 2 3 4 5 6]
               [])
     :scalar '(3/2 -11.7)}
                           :foos-pt-crossover       {:foos '([1 2] [3 4 5 6])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([]
               [99 88 77 66])
     :scalar '(3/2 -11.7)}
                           :foos-pt-crossover       {:foos '([88 77 66] [99])}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )



(tabular
  (fact "`foos-items` dumps a list of contents onto :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '([1 2 3 4 5 6])}
                             :foos-items       {:exec '( (1 2 3 4 5 6) )}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '([])}
                             :foos-items       {:exec '(  )} ;; because `(seq '())` = nil
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact "`foos-fillvector` makes a new vector of N copies of the top :foo, using the two-scalar trick for sizing"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '()
     :foo    '(0)
     :scalar '(3 6)}
                             :foos-fillvector       {:foos '( [0 0 0 0 0 0] )
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(0)
     :scalar '(0 603)}
                             :foos-fillvector       {:foos '( [0 0 0] )
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(0)
     :scalar '(1 603)}
                             :foos-fillvector       {:foos '( [0 0 0] )
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(0)
     :scalar '(-7/2 111)}
                             :foos-fillvector       {:foos '([0])
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(0)
     :scalar '(-11/2 111)}
                             :foos-fillvector       {:foos '([0 0 0 0 0 0 0 0 0 0 0])
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )



(tabular
  (fact "`foos-fillvector` has a size limit"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '()
     :foo    (list (take 10000 (range)))
     :scalar '(2 999)}
                             :foos-fillvector       {:foos '()
                                                     :foo  '()
                                                     :error '("foos-fillvector produced oversized result")}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
)



(tabular
  (fact "`foos-cyclevector` makes a new vector of N copies of the whole :foo stack, using the two-scalar trick for sizing"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foos-type ?instruction) => (contains ?expected))

    ?new-stacks             ?instruction         ?expected

    {:foos   '()
     :foo    '(1/2 3.4)
     :scalar '(3 5)}
                             :foos-cyclevector       {:foos '( [1/2 3.4 1/2 3.4 1/2] )
                                                     :foo  '(1/2 3.4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(1/2 3.4)
     :scalar '(0 603)}
                             :foos-cyclevector       {:foos '( [1/2 3.4 1/2] )
                                                     :foo  '(1/2 3.4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '(1/2 3.4)
     :scalar '(-5/2 111)}
                             :foos-cyclevector       {:foos
                                                       '([1/2 3.4 1/2 3.4 1/2 3.4 1/2 3.4 1/2 3.4 1/2])
                                                     :foo  '(1/2 3.4)
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foos   '()
     :foo    '()
     :scalar '(-1.2246467991473532E-16 1356)}
                             :foos-cyclevector       {:foos '([])
                                                     :foo  '()
                                                     :scalar '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

    )
