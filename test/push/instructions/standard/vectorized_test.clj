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