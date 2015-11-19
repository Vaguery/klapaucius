(ns push.instructions.base.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.util.stack-manipulation :as u])
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


;; a big fixture

(def starting-here (i/make-classic-interpreter :stacks {:integer '(1 2)
                                                        :error '(:oops :ow)
                                                        :print '("hi")
                                                        :log '(1 2 3)}))


(def has-memories (i/execute-instruction starting-here :environment-begin))


(def returns-memories (u/set-stack has-memories :return '(1 2 3)))


(fact "has-memories is as expected"
  (:stacks has-memories) => '{:boolean (), :char (), :code (), :environment ({:boolean (), :char (), :code (), :environment (), :error (:oops :ow), :exec (), :float (), :integer (1 2), :log (1 2 3), :print ("hi"), :return (), :string (), :unknown ()}), :error (:oops :ow), :exec (), :float (), :integer (1 2), :log (1 2 3), :print ("hi"), :return (), :string (), :unknown ()})


(fact ":environment-end keeps the :print, :log and :error stacks, replaces the rest with the top :environment stack values, and pushes :return stack onto :exec"
  (:stacks (i/execute-instruction has-memories :environment-end)) =>
    '{:boolean (), :char (), :code (), :environment (), :error (:oops :ow), :exec (()), :float (), :integer (1 2), :log (1 2 3), :print ("hi"), :return (), :string (), :unknown ()}


  (:stacks (i/execute-instruction returns-memories :environment-end)) =>
    '{:boolean (), :char (), :code (), :environment (), 
      :error (:oops :ow),                                    ;;; keeps error
      :exec ((1 2 3)),                                       ;;; return values
      :float (), :integer (1 2),                             
      :log (1 2 3),                                          ;;; keeps log
      :print ("hi"),                                         ;;; keeps print
      :return (),                                            ;;; empties return
      :string (), :unknown ()})

