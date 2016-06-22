(ns push.instructions.base.environment_test
  (:require [push.interpreter.core :as i]
            [push.util.stack-manipulation :as u]
            [push.interpreter.templates.one-with-everything :as owe])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.module.environment])
  )




(tabular
  (fact ":environment-new saves copies of all the stacks (the whole hash) to the :environment stack, then brings back the top item of the :exec stack, and empties the :return stack"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks environment-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :boolean  '(false)
     :scalar   '(2 9)
     :exec     '(8 9 10)}     :environment-new     '{:boolean (false), :char (), :code (:foo), :environment ({:boolean (false), :char (), :code (:foo), :environment (), :error (), :exec (9 10), :log (), :print (), :return (), :scalar (2 9), :string (), :unknown ()}), :error (), :exec (8), :log (), :print (), :return (), :scalar (2 9), :string (), :unknown ()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(8)}          :environment-new     '{:boolean (), :char (), :code (), :environment ({:boolean (), :char (), :code (), :environment (), :error (), :exec (), :log (), :print (), :return (), :scalar (), :string (), :unknown ()}), :error (), :exec (8), :log (), :print (), :return (), :scalar (), :string (), :unknown ()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec   '(8 9 10)
     :return '(1 2 3)}        :environment-new     '{:boolean (), :char (), :code (), :environment ({:boolean (), :char (), :code (), :environment (), :error (), :exec (9 10), :log (), :print (), :return (1 2 3), :scalar (), :string (), :unknown ()}), :error (), :exec (8), :log (), :print (), :return (), :scalar (), :string (), :unknown ()}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    )


(tabular
  (fact ":environment-begin saves copies of all the stacks (the whole hash) to the :environment stack _except_ :exec, then clears the :return stack"
    (check-instruction-with-all-kinds-of-stack-stuff
        ?new-stacks environment-module ?instruction) => (contains ?expected))

    ?new-stacks                ?instruction             ?expected
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:code     '(:foo)
     :boolean  '(false)
     :scalar   '(2 9)
     :return   '(:a :b)
     :exec     '(8 9 10)}     :environment-begin   {:code     '(:foo)
                                                    :boolean  '(false)
                                                    :scalar   '(2 9)
                                                    :exec     '(8 9 10)   ;;;; this
                                                    :return   '()         ;;;; this
                                                    :environment '(
                                                     {:boolean (false), 
                                                      :char (), 
                                                      :code (:foo), 
                                                      :environment (), 
                                                      :error (), 
                                                      :exec (),       ;;;;;;;; this
                                                      :scalar (2 9), 
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

(def starting-here (owe/make-everything-interpreter :stacks {
                                                        :scalar '(1 2)
                                                        :error '(:oops :ow)
                                                        :print '("hi")
                                                        :log '(1 2 3)
                                                        :unknown '(:weird)
                                                        :exec '(:foo :bar)}))


(def has-memories (i/execute-instruction starting-here :environment-new))

(def new-memories (-> has-memories
                      (u/set-stack , :exec '(9 99 999))
                      (u/set-stack , :unknown '(:huh :weird))
                      (u/set-stack , :scalar '(7 77 777))
                      (u/set-stack , :return '(1 11 1111))
                      (u/set-stack , :boolean '(false false))
                      (u/set-stack , :log '(1 2 3 4 5 6 7))
                      ))


(fact "starting-here is as expected"
  (:stacks starting-here) =>
    '{:boolean (), :booleans (), :char (), :chars (), :code (), :complex (), :complexes (), :environment (), :error (:oops :ow), :exec (:foo :bar), :generator (), :log (1 2 3), :print ("hi"), :quoted (),  :ref (), :refs (), :return (), :scalar (1 2), :scalars (), :set (), :string (), :strings (), :tagspace (), :unknown (:weird), :vector ()})


(fact "has-memories is as expected"
    (:stacks has-memories) =>
    '{:boolean (), :booleans (), :char (), :chars (), :code (), :complex (), :complexes (), :environment ({:boolean (), :booleans (), :char (), :chars (), :code (), :complex (), :complexes (), :environment (), :error (:oops :ow), :exec (:bar), :generator (), :log (1 2 3), :print ("hi"), :quoted (), :ref (), :refs (), :return (), :scalar (1 2), :scalars (), :set (), :string (), :strings (), :tagspace (), :unknown (:weird), :vector ()}), :error (:oops :ow), :exec (:foo), :generator (), :log (1 2 3), :print ("hi"), :quoted (), :ref (), :refs (), :return (), :scalar (1 2), :scalars (), :set (), :string (), :strings (), :tagspace (), :unknown (:weird), :vector ()})


(fact "new-memories is as expected"
    (:stacks new-memories) => 
    '{:boolean (false false),
      :booleans (),
      :char (),
      :chars (),
      :code (),
      :complex (),
      :complexes (),
      :environment (
        {:boolean (), 
         :booleans (),
         :char (),
         :chars (),
         :code (),
         :complex (),
         :complexes (),
         :environment (),
         :error (:oops :ow),
         :exec (:bar),
         :generator (),
         :log (1 2 3),
         :print ("hi"),
         :quoted (),
         :ref (),
         :refs (),
         :return (),
         :scalar (1 2),
         :scalars (),
         :set (),
         :string (),
         :strings (),
         :tagspace (),
         :unknown (:weird),
         :vector ()}),
      :error (:oops :ow),
      :exec (9 99 999),
      :generator (),
      :log (1 2 3 4 5 6 7),
      :print ("hi"),
      :quoted (),
      :ref (),
      :refs (),
      :return (1 11 1111),
      :scalar (7 77 777),
      :scalars (),
      :set (),
      :string (),
      :strings (),
      :tagspace (),
      :unknown (:huh :weird),
      :vector ()})


(fact ":environment-end keeps the :print, :log, :unknown and :error stacks, replaces the rest with the top :environment stack values, then pushes the :exec and :return stacks onto the unarchived :exec"
  (:stacks (i/execute-instruction has-memories :environment-end)) =>
    '{:boolean (),
      :booleans (),
      :char (),
      :chars (),
      :code (),
      :complex (),
      :complexes (),
      :environment (),
      :error (:oops :ow),              ;; kept
      :exec (:foo :bar),               ;; return; new exec; old exec
      :generator (),
      :log (1 2 3),                    ;; kept
      :print ("hi"),                   ;; kept
      :quoted (),
      :ref (),
      :refs (),
      :return (),
      :scalar (1 2),
      :scalars (),
      :set (),
      :string (),
      :strings (),
      :tagspace (),
      :unknown (:weird),               ;; kept
      :vector ()}


  (:stacks (i/execute-instruction new-memories :environment-end)) =>
    '{:boolean (),                       ;; overwritten
      :booleans (),
      :char (),
      :chars (),
      :code (),
      :complex (),
      :complexes (),
      :environment (),                   ;; popped
      :error (:oops :ow),                ;; retained
      :exec (1111 11 1 9 99 999 :bar),   ;; returns; new exec; old exec
      :generator (),
      :log (1 2 3 4 5 6 7),              ;; retained
      :print ("hi"),
      :quoted (),
      :ref (),
      :refs (),
      :return (),                        ;; popped and sent to :exec
      :scalar (1 2),                     ;; overwritten
      :scalars (),
      :set (),
      :string (),
      :strings (),
      :tagspace (),
      :unknown (:huh :weird),            ;; retained
      :vector ()}
      )