(ns push.instructions.base.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.util.stack-manipulation :as u])
  (:use [push.instructions.modules.environment])
  )


(tabular
  (fact ":environment-new saves copies of all the stacks (the whole hash) to the :environment stack, then brings back the top item of the :exec stack, and empties the :return stack"
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
                                                        :log '(1 2 3)
                                                        :unknown '(:weird)
                                                        :exec '(:foo :bar)}))


(def has-memories (i/execute-instruction starting-here :environment-new))

(def new-memories (-> has-memories
                      (u/set-stack , :exec '(9 99 999))
                      (u/set-stack , :unknown '(:huh :weird))
                      (u/set-stack , :integer '(7 77 777))
                      (u/set-stack , :return '(1 11 1111))
                      (u/set-stack , :boolean '(false false))
                      (u/set-stack , :log '(1 2 3 4 5 6 7))
                      ))


(fact "starting-here is as expected"
  (:stacks starting-here) =>
    '{:boolean (), 
      :char (), 
      :code (), 
      :environment (), 
      :error (:oops :ow), 
      :exec (:foo :bar), 
      :float (), 
      :integer (1 2), 
      :log (1 2 3), 
      :print ("hi"), 
      :return (), 
      :string (), 
      :unknown (:weird)})


(fact "has-memories is as expected"
    (:stacks has-memories) =>
    '{:boolean (), 
      :char (), 
      :code (), 
      :environment (
        { :boolean (), 
          :char (), 
          :code (), 
          :environment (), 
          :error (:oops :ow), 
          :exec (:bar), 
          :float (), 
          :integer (1 2), 
          :log (1 2 3), 
          :print ("hi"), 
          :return (), 
          :string (), 
          :unknown (:weird)}), 
      :error (:oops :ow), 
      :exec (:foo), 
      :float (), 
      :integer (1 2), 
      :log (1 2 3), 
      :print ("hi"), 
      :return (), 
      :string (), 
      :unknown (:weird)})


(fact "new-memories is as expected"
    (:stacks new-memories) => 
    '{:boolean (false false), 
      :char (), 
      :code (), 
      :environment (
        { :boolean (), 
          :char (), 
          :code (), 
          :environment (), 
          :error (:oops :ow), 
          :exec (:bar), 
          :float (), 
          :integer (1 2), 
          :log (1 2 3), 
          :print ("hi"), 
          :return (), 
          :string (), 
          :unknown (:weird)}), 
      :error (:oops :ow), 
      :exec (9 99 999), 
      :float (), 
      :integer (7 77 777), 
      :log (1 2 3 4 5 6 7), 
      :print ("hi"), 
      :return (1 11 1111), 
      :string (), 
      :unknown (:huh :weird)})


(fact ":environment-end keeps the :print, :log, :unknown and :error stacks, replaces the rest with the top :environment stack values, then pushes the :exec and :return stacks onto the unarchived :exec"
  (:stacks (i/execute-instruction has-memories :environment-end)) =>
    '{:boolean (), 
      :char (), 
      :code (), 
      :environment (), 
      :error (:oops :ow),               ;; kept
      :exec (:foo :bar),                ;; return; new exec; old exec
      :float (), 
      :integer (1 2), 
      :log (1 2 3),                     ;; kept
      :print ("hi"),                    ;; kept
      :return (), 
      :string (), 
      :unknown (:weird)}                ;; kept


  (:stacks (i/execute-instruction new-memories :environment-end)) =>
    '{:boolean (),                            ;; overwritten
      :char (),
      :code (), 
      :environment (),                        ;; popped
      :error (:oops :ow),                     ;; retained
      :exec (1111 11 1 9 99 999 :bar),        ;; returns; new exec; old exec
      :float (), 
      :integer (1 2),                         ;; overwritten
      :log (1 2 3 4 5 6 7),                   ;; retained
      :print ("hi"), 
      :return (),                             ;; popped and sent to :exec
      :string (), 
      :unknown (:huh :weird)}                 ;; retained

)