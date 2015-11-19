(ns push.instructions.base.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.modules.environment])
  )


(tabular
  (fact ":environment-new saves copies of all the stacks (the whole hash) to the :environment stack, then clears the :exec and :return stacks"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-environment-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :boolean  '(false)
     :integer  '(2 9)
     :exec     '(8 9 10)}     :environment-new     {:code     '(:foo)
                                                    :boolean  '(false)
                                                    :integer  '(2 9)
                                                    :exec     '(8)
                                                    :environment '(
                                                     {:boolean (false), 
                                                      :char (), 
                                                      :code (:foo), 
                                                      :environment (), 
                                                      :error (), 
                                                      :exec (9 10), 
                                                      :float (), 
                                                      :integer (2 9), 
                                                      :log (), 
                                                      :print (),
                                                      :return () 
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(8)}          :environment-new     {:exec '(8)
                                                    :environment '(
                                                     {:boolean (), 
                                                      :char (), 
                                                      :code (), 
                                                      :environment (), 
                                                      :error (), 
                                                      :exec (), 
                                                      :float (), 
                                                      :integer (), 
                                                      :log (), 
                                                      :print (),
                                                      :return (),
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(8 9 10)
     :return '(1 2 3)}        :environment-new     {:exec '(8)
                                                    :return '()
                                                    :environment '(
                                                     {:boolean (), 
                                                      :char (), 
                                                      :code (), 
                                                      :environment (), 
                                                      :error (), 
                                                      :exec (9 10), 
                                                      :float (), 
                                                      :integer (), 
                                                      :log (), 
                                                      :print (),
                                                      :return (1 2 3),
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact ":environment-begin saves copies of all the stacks (the whole hash) to the :environment stack _except_ :exec, then clears the :return stack"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks classic-environment-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :boolean  '(false)
     :integer  '(2 9)
     :return   '(:a :b)
     :exec     '(8 9 10)}     :environment-begin   {:code     '(:foo)
                                                    :boolean  '(false)
                                                    :integer  '(2 9)
                                                    :exec     '(8 9 10)   ;;;; this
                                                    :return   '()         ;;;; this
                                                    :environment '(
                                                     {:boolean (false), 
                                                      :char (), 
                                                      :code (:foo), 
                                                      :environment (), 
                                                      :error (), 
                                                      :exec (),       ;;;;;;;; this
                                                      :float (), 
                                                      :integer (2 9), 
                                                      :log (), 
                                                      :print (),
                                                      :return (:a :b) ;;;;;;;; this
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;Î;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '()}           :environment-begin     {:exec '()}
    ;;;;;;;;;;Î;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )
