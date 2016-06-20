(ns push.instructions.standard.tagspace-test
  (:require [push.interpreter.core :as i]
            [push.types.core :as t])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.tagspace])
  (:use [push.instructions.aspects])
  )




(tabular
  (fact ":tagspace-new pushes a new tagspace"
    (register-type-and-check-instruction
        ?set-stack ?items tagspace-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction    ?get-stack     ?expected
    :tagspace   '()         :tagspace-new    :tagspace    (list (make-tagspace)))




(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-count` pushes the number of stored items to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:tagspace (list taggy)}  :tagspace-count   {:exec  (list (list 3 taggy))
                                                 :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                              :tagspace-count   {:exec
                                                 (list (list 0 (make-tagspace)))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-keys` pushes the keys and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks         ?instruction       ?expected
    {:tagspace
      (list taggy)}     :tagspace-keys     {:exec  (list (list '(-4 2 6) taggy))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                        :tagspace-keys     {:exec  (list (list '() (make-tagspace)))
                                            :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-values` pushes the values and tagspace to exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected

    {:tagspace (list taggy)}  :tagspace-values   {:exec (list (list '(3 1 5) taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                              :tagspace-values   {:exec (list (list '()
                                                          (make-tagspace)))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookup` pops a :scalar and a :tagspace and retrieves the indicated item"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction          ?expected
    {:exec '()
     :scalar '(3)
     :tagspace (list taggy)}  :tagspace-lookup       {:exec '(4)
                                                      :scalar  '()
                                                      :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(33)
     :tagspace (list taggy)}    :tagspace-lookup     {:exec '(2)
                                                      :scalar  '()
                                                      :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(5/2)
     :tagspace (list taggy)}    :tagspace-lookup     {:exec '(4)
                                                      :scalar  '()
                                                      :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(-1007777778176487612847628374682734823M)
     :tagspace (list taggy)}    :tagspace-lookup     {:exec '(2)
                                                      :scalar  '()
                                                      :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalar '(133)
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookup     {:exec '()
                                                      :scalar '()
                                                      :tagspace
                                                        (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupscalars` pops a :scalars and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :scalars '([-3 0 3 6 9])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec     '((2 2 4 2 2))
                                                          :scalars  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalars '([])
     :tagspace (list taggy)}    :tagspace-lookupscalars      {:exec     '(())
                                                          :scalars  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :scalars '([1 8])
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupscalars      {:exec     '(())
                                                          :scalars  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let 
  [taggy (make-tagspace {1M 2 3M 4})]
(tabular
  (fact "`:tagspace-lookupscalars` bebefits unusually from the safety of `find-in-tagspace` stability"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :scalars '([1/3 2/7])
     :tagspace (list taggy)}    :tagspace-lookupscalars  {:exec     '((2 2))
                                                          :scalars  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-lookupvector` pops an :vector and a :tagspace and retrieves ALL the indicated items"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected

    {:exec '()
     :vector '([-3.2 :foo 3 6 9])
     :tagspace (list taggy)}    :tagspace-lookupvector  {:exec     '((2 :foo 4 2 2))
                                                          :vector  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([-3 :foo \g :scalar-add [6.2] 11/8])
     :tagspace (list taggy)}    :tagspace-lookupvector   {:exec 
                                                            '((2 :foo \g :scalar-add [6.2] 4))
                                                          :vector  '()
                                                          :tagspace (list taggy)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec '()
     :vector '([1 8])
     :tagspace (list (make-tagspace))}  
                                :tagspace-lookupvector   {:exec     '(())
                                                          :vector  '()
                                                          :tagspace (list (make-tagspace))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy1  (make-tagspace {1 2 3 4 5 6})
   taggy2  (make-tagspace {1 -2 3 -4 5.5 6.6})]

(tabular
  (fact "`:tagspace-merge` merges two tagspaces"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge    {:tagspace (list (make-tagspace
                                                    {5.5 6.6, 1 2, 3 4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 taggy1)}
                               :tagspace-merge    {:tagspace (list (make-tagspace
                                                    {5.5 6.6, 1 -2, 3 -4, 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list taggy2 (make-tagspace))}
                               :tagspace-merge    {:tagspace (list (make-tagspace
                                                    {5.5 6.6, 1 -2, 3 -4}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy1  (make-tagspace {1M 2})
   taggy2  (make-tagspace {1/3 2/3})]

(tabular
  (fact "`:tagspace-merge` consumes arguments but captures runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected
    {:tagspace (list taggy1 taggy2)}
                               :tagspace-merge    {:tagspace '()
                                                   :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace)]

(tabular
  (fact "`:foo-tag` pops a :scalar and a :foo and stores the latter in a :tagspace"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:foo '("bar")
     :scalar '(71.71)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '()
                                              :tagspace (list
                                                (assoc-in taggy 
                                                          [:contents 71.71]
                                                          "bar"))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:foo '()
     :scalar '(71.71)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '(71.71)
                                              :tagspace (list taggy)}
    ))



(let 
  [foo-type     (make-taggable (t/make-type :foo))
   taggy        (make-tagspace {1M 2})]

(tabular
  (fact "`:foo-tag` consumes arguments but reports runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks foo-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:foo '("bar")
     :scalar '(1/7)
     :tagspace (list taggy)}    :foo-tag     {:foo '()
                                              :scalar  '()
                                              :tagspace '()
                                              :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-max` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected

    {:tagspace (list taggy)}    :tagspace-max  {:exec  (list (list 6 taggy))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-max  {:exec  (list (make-tagspace))
                                                :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {6 5 -4 3 2 1})]
(tabular
  (fact "`:tagspace-min` pushes the smallest key to :exec"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction       ?expected

    {:tagspace (list taggy)}    :tagspace-min    {:exec (list (list -4 taggy))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace))}   
                                :tagspace-min    {:exec (list (make-tagspace))
                                                  :tagspace '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-offset` pops a :scalar and a :tagspace and moves the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected
    {:scalar '(17)
     :tagspace (list taggy)}  :tagspace-offset     {:scalar  '()
                                                    :tagspace (list (make-tagspace
                                                      {18 2, 20 4, 22 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-121)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :tagspace (list (make-tagspace
                                                      {-120 2, -118 4, -116 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3/2)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :tagspace (list (make-tagspace
                                                      {5/2 2, 9/2 4, 13/2 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-12.345M)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :tagspace (list (make-tagspace
                                                      {-11.345M 2,
                                                        -9.345M 4,
                                                        -7.345M 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :tagspace (list taggy)}   :tagspace-offset    {:scalar  '()
                                                    :tagspace (list (make-tagspace
                                                      {1 2 3 4 5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1/3 2 3/5 4 5/7 6})]
(tabular
  (fact "`:tagspace-offset` creates an `:error` if the arguments cause a runtime error"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction              ?expected
    {:scalar '(17M)
     :tagspace (list taggy)}  :tagspace-offset     {:scalar  '()
                                                    :tagspace '()
                                                    :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-scale` pops a :scalar and a :tagspace and scales all the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar '(3)
     :tagspace (list taggy)}   :tagspace-scale   {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {3 2, 9 4, 15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-3)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {-3 2, -9 4, -15 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(1/3)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {1/3 2, 5/3 6, 1 4}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-0.25)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {-1.25 6, -0.75 4, -0.25 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(0)
     :tagspace (list taggy)}    :tagspace-scale  {:scalar '()
                                                  :tagspace (list (make-tagspace
                                                    {0 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1/3 2 3/7 4 5/9 6})]
(tabular
  (fact "`:tagspace-scale` catches runtime errors"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction      ?expected
    {:scalar '(3M)
     :tagspace (list taggy)}   :tagspace-scale   {:scalar  '()
                                                  :tagspace '()
                                                  :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-split` pops a :scalar and a :tagspace and produces two new tagspaces (pushed to :exec)"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:scalar '(3)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2})
                                                  (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(4)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2 3 4})
                                                  (make-tagspace {5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(5/2)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2})
                                                  (make-tagspace {3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-22)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {})
                                                  (make-tagspace {1 2 3 4 5 6})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(77123)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :exec (list (list
                                                  (make-tagspace {1 2 3 4 5 6})
                                                  (make-tagspace {})))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))





(let 
  [taggy (make-tagspace {1M 2})]
(tabular
  (fact "`:tagspace-split` consumes arguments but captures runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction     ?expected
    {:scalar '(1/3)
     :tagspace (list taggy)}  :tagspace-split  {:scalar  '()
                                                :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidy` pops two :scalars and a :tagspace and cleans up the keys"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:scalar '(321 2/3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {2/3 2, 965/6 4, 321N 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2.7 3.7)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {-2.7 6, 0.5 4, 3.7 2}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3 3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {3 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(3/5 1/5)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {1/5 2, 2/5 4, 3/5 6}))}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:scalar '(-2 3.75)
     :tagspace (list (make-tagspace {}))}
                                :tagspace-tidy   {:scalar  '()
                                                  :tagspace (list (make-tagspace {}))}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))



(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-tidy` consumes arguments but reports runtime errors if they occur"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:scalar '(321M 2/3)
     :tagspace (list taggy)}    :tagspace-tidy   {:scalar  '()
                                                  :tagspace '()
                                                  :error '({:item "Non-terminating decimal expansion; no exact representable decimal result.", :step 0})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ))




(let 
  [taggy (make-tagspace {1 2 3 4 5 6})]
(tabular
  (fact "`:tagspace-normalize` pops a :tagspace and cleans up the keys by setting the first to 0, and the rest to the following integer values"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks tagspace-type ?instruction) => (contains ?expected))

    ?new-stacks                 ?instruction      ?expected
    {:tagspace (list taggy)}    :tagspace-normalize  
                                                 {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {0 2, 1 4, 2 6}))}
    ; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:tagspace (list (make-tagspace {9 2 17 4 -2 6}))}    
                                :tagspace-normalize  
                                                 {:scalar  '()
                                                  :tagspace (list (make-tagspace
                                                    {0 6, 1 2, 2 4}))}

    ))


