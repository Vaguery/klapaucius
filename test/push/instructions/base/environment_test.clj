(ns push.instructions.base.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.modules.environment])
  )


(tabular
  (fact ":environment-new saves copies of all the stacks (the whole hash) to the :environment stack"
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
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    {:exec     '(8)}          :environment-new     {:exec     '(8)
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
                                                      :string (), 
                                                      :unknown ()})}
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;)
