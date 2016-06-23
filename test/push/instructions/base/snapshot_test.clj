(ns push.instructions.base.snapshot_test
  (:require [push.interpreter.core :as i]
            [push.util.stack-manipulation :as u]
            [push.type.definitions.snapshot :as snap]
            [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.test-helpers]
        [push.type.item.snapshot])
  )



(def simple       (push/interpreter))
(def has-bindings (push/interpreter :bindings {:x '(1 2)}))
(def has-stacks   (push/interpreter :stacks   {:exec '(10101) :foo '(99 88)}))
(def has-config   (push/interpreter :config   {:bar :nope!}))
(def has-it-all   (push/interpreter :bindings {:x '(1 2)}
                                    :stacks   {:exec   '(10101 20202)
                                               :return '(99 88)
                                               :print  '(:OLD)
                                               :log    '(:OLD)
                                               :error  '(:OLD)}
                                    :config   {:bar :nope!}))



(tabular
  (fact  "snapshot-begin pushes a new :snapshot item to that stack"
    (register-type-and-check-instruction-in-this-interpreter
      simple
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()    :snapshot-begin    :snapshot      
                                                (list (snap/snapshot simple))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact  "snapshot-begin empties the `:exec` stack in the stored copy but makes no other changes to it"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()    :snapshot-begin      :exec      
                                              (get-in has-it-all [:stacks :exec])
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :snapshot     '()    :snapshot-begin    :snapshot   

                  (list 
                    (assoc-in (snap/snapshot has-it-all)
                               [:stacks :exec]
                               '()))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact  "snapshot-begin empties the `:return` stack in the running stacks, not the :exec stack"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items       ?instruction      ?get-stack     ?expected
    :return      '(99 88)    :snapshot-begin      :return      '()
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    :exec        '(99 88)    :snapshot-begin      :exec        '(99 88)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )





(tabular
  (fact  "snapshot-end pops the top :snapshot item"
    (check-instruction-here-using-this
      has-it-all
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:snapshot (list (snap/snapshot has-it-all))}
                      :snapshot-end       {:snapshot '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact  "snapshot-end doesn't overwrite the current :print stack with the stored one"
    (check-instruction-here-using-this
      (assoc-in has-it-all [:stacks :print] '(:NEW))
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:snapshot (list (snap/snapshot has-it-all))}
                      :snapshot-end       {:print '(:NEW)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact  "snapshot-end doesn't overwrite the current :error stack with the stored one"
    (check-instruction-here-using-this
      (assoc-in has-it-all [:stacks :error] '(:NEW))
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:snapshot (list (snap/snapshot has-it-all))}
                      :snapshot-end       {:error '(:NEW)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )




(tabular
  (fact  "snapshot-end doesn't overwrite the current :unknown stack with the stored one"
    (check-instruction-here-using-this
      (assoc-in has-it-all [:stacks :unknown] '(:NEW))
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:snapshot (list (snap/snapshot has-it-all))}
                      :snapshot-end       {:unknown '(:NEW)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )



(tabular
  (fact  "snapshot-end doesn't overwrite the current :log stack with the stored one"
    (check-instruction-here-using-this
      (assoc-in has-it-all [:stacks :log] '(:NEW))
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {:snapshot (list (snap/snapshot has-it-all))}
                      :snapshot-end       {:log '(:NEW)}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(fact (get-in has-it-all [:stacks :exec]) => '(10101 20202))

(fact (get-in (snap/snapshot has-it-all) [:stacks :exec]) => '(10101 20202))


(tabular
  (fact  "snapshot-end puts the current :exec and :return stacks onto the retrieved :exec stack"
    (check-instruction-here-using-this
      (push/merge-stacks
        has-it-all {:exec '(:X1 :X2)
                            :return '(:r1 :r2)
                            :snapshot (list (snap/snapshot has-it-all))})
      ?new-stacks ?instruction) => (contains ?expected))

    ?new-stacks        ?instruction       ?expected
    {}                :snapshot-end       {:exec '((:r1 :r2) (:X1 :X2) 10101 20202)
                                           :snapshot '()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(fact "snapshot-end overwrites the current :bindings with the old ones"
  (:bindings simple) => {}

  (:bindings
    (i/execute-instruction
      (push/merge-stacks
        simple {:snapshot (list (snap/snapshot has-it-all))}) 
      :snapshot-end)) => (:bindings has-it-all))



(fact "snapshot-end overwrites the current :config with the old ones"
  (:config simple) => (:config (push/interpreter))
  
  (:config
    (i/execute-instruction
      (push/merge-stacks
        simple {:snapshot (list (snap/snapshot has-it-all))}) 
      :snapshot-end)) => (:config has-it-all))





(tabular
  (fact  "snapshot-new pushes a new :snapshot item to that stack, without the top `:exec` item"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()      :snapshot-new    :snapshot      
                                              #(get-in % [:stacks :exec] '(20202))
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact  "snapshot-new keeps the top `:exec` item on the running stacks"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()      :snapshot-new    :exec           '(10101)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact  "snapshot-new keeps the top `:exec` item on the running stacks"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()      :snapshot-new    :exec           '(10101)
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact  "snapshot-new clears the `:return` stack on the running stacks"
    (register-type-and-check-instruction-in-this-interpreter
      has-it-all
      ?set-stack ?items snapshot-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items    ?instruction     ?get-stack     ?expected
    :snapshot     '()      :snapshot-new    :return         '()
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
