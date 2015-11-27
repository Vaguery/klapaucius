(ns push.interpreter.interpreter-running-test
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.types.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:require [push.instructions.aspects.equatable :as equatable])
  (:require [push.instructions.aspects.movable :as movable])
  (:require [push.instructions.aspects.comparable :as comparable])
  (:require [push.instructions.aspects.visible :as visible])
  (:use [push.interpreter.core])
  )


;;; the router and handle-item


;; unknown items


(fact "`handle-item` pushes an item to the :unknown stack if unrecognized when :config :lenient? is true"
  (let [junk :this-is-an-unknown-item]
    (u/get-stack
      (handle-item 
        (make-classic-interpreter :config {:lenient? true})
        junk)
      :unknown) => '(:this-is-an-unknown-item)))


(fact "`handle-item` pushes an item to the :unknown stack if 
  unrecognized when :config :lenient? is not true (or unset)"
  (handle-item (make-classic-interpreter) (basic-interpreter)) => 
    (throws #"Push Parsing Error: Cannot interpret '"))


;; the router order is: :input? :instruction? [router] :unknown


(fact "`router-sees?` checks the router predicates and returns true if one matches"
  (let [abbr #'push.interpreter.core/router-sees?]
    (abbr (make-classic-interpreter) :not-likely) => nil
    (abbr (basic-interpreter 
      :router [[(fn [item] (= item :not-likely)) :integer]]) :not-likely) => true))


(fact "`handle-item` checks the :router list"
  (let [he-knows-foo (basic-interpreter :router [[integer? :code]])]
        ;; this will route integers to the :code stack, and not to :integer
    (:stacks (handle-item he-knows-foo 99) :code) => (contains {:code '(99)})))


(def foo-type 
  (-> (types/make-type :foo :recognizer integer?)
      visible/make-visible
      comparable/make-comparable
      equatable/make-equatable
      movable/make-movable))


(fact "types added to the router with `register-type` are used by `handle-item`"
  (:router (register-type (basic-interpreter) foo-type)) =>
    [ [(:recognizer foo-type) :foo] ]
  (u/get-stack (handle-item (register-type (basic-interpreter) foo-type) 99) :integer) => '()
  (u/get-stack (handle-item (register-type (basic-interpreter) foo-type) 99) :foo) => '(99))


(fact "handle-item sends integers to :integer"
  (u/get-stack (handle-item (make-classic-interpreter) 8) :integer) => '(8)
  (u/get-stack (handle-item (make-classic-interpreter) -8) :integer) => '(-8)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:integer '(1)}) -8) :integer) =>
    '(-8 1))


; (fact "handle-item handles integer overflow")


(fact "handle-item sends floats to :float"
  (u/get-stack (handle-item (make-classic-interpreter) 8.0) :float) => '(8.0)
  (u/get-stack (handle-item (make-classic-interpreter) -8.0) :float) => '(-8.0)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:float '(1.0)}) -8.0) :float) =>
    '(-8.0 1.0))


; (fact "handle-item handles float overflow")
; (fact "handle-item handles float underflow")


(fact "handle-item sends booleans to :boolean"
  (u/get-stack (handle-item (make-classic-interpreter) false) :boolean) => '(false)
  (u/get-stack (handle-item (make-classic-interpreter) true) :boolean) => '(true)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:boolean '(false)}) true) :boolean) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (u/get-stack (handle-item (make-classic-interpreter) \J) :char) => '(\J)
  (u/get-stack (handle-item (make-classic-interpreter) \o) :char) => '(\o)
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:char '(\Y)}) \e) :char) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (u/get-stack (handle-item (make-classic-interpreter) "foo") :string) => '("foo")
  (u/get-stack (handle-item (make-classic-interpreter) "") :string) => '("")
  (u/get-stack (handle-item (make-classic-interpreter :stacks {:string '("bar")}) "baz") :string) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' lists onto :exec"
  (u/get-stack (handle-item (make-classic-interpreter) '(1 2 3)) :exec) => '(1 2 3)
  (u/get-stack (handle-item (make-classic-interpreter) '(1 (2) (3))) :exec) => '(1 (2) (3))
  (u/get-stack (handle-item (make-classic-interpreter) '(1 () ())) :exec) => '(1 () ())
  (u/get-stack (handle-item (make-classic-interpreter) '()) :exec) => '())


(fact "the :exec stack stays a list when a list is unwrapped onto it"
  (list? (u/get-stack (handle-item (make-classic-interpreter) '(1 2 3)) :exec)) => 
    true
  (list? (u/get-stack (handle-item (make-classic-interpreter) '(1 (2) (3))) :exec)) => 
    true
  (list? (u/get-stack (handle-item (make-classic-interpreter) '(1 () ())) :exec)) => 
    true
  (list? (u/get-stack (handle-item (make-classic-interpreter) '()) :exec)) => 
    true)


(fact "handle-item will execute a registered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (basic-interpreter :instructions registry)]
   (handle-item he-knows-foo :foo) => 761))
    ;; an intentionally surprising result


(fact "handle-item will not execute an unregistered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (basic-interpreter :instructions registry)]
   (handle-item he-knows-foo :bar) => (throws #"Push Parsing Error:")))


;; some fixtures:


(def intProductToFloat
  (instr/build-instruction intProductToFloat
    (dsl/consume-top-of :integer :as :arg1)
    (dsl/consume-top-of :integer :as :arg2)
    (dsl/calculate [:arg1 :arg2] #(float (* %1 %2)) :as :p)
    (dsl/push-onto :float :p)))


(def knows-some-things
  (register-instruction
    (basic-interpreter 
      :program [1.1 2.2 :intProductToFloat]
      :counter 22
      :stacks {:integer '(1 2 3)
               :exec '(:intProductToFloat)}
      :config {:step-limit 23})
    intProductToFloat))


;; clear-all-stacks


(fact "`clear-all-stacks` empties every stack"
  (:stacks (clear-all-stacks knows-some-things)) => core-stacks)


;; reset-interpreter


(fact "calling `reset-interpreter` loads the program onto :exec"
  (u/get-stack knows-some-things :exec) => '(:intProductToFloat)
  (u/get-stack (reset-interpreter knows-some-things) :exec) =>
    '(1.1 2.2 :intProductToFloat))


(fact "calling `reset-interpreter` clears the other stacks"
  (u/get-stack knows-some-things :integer) => '(1 2 3)
  (:stacks (reset-interpreter knows-some-things)) => 
    (merge core-stacks {:exec '(1.1 2.2 :intProductToFloat)}))


(fact "`reset-interpreter` sets the counter to 0"
  (let [counted (assoc knows-some-things :counter 9912)]
    (:counter counted) => 9912
    (:counter (reset-interpreter counted)) => 0))


;; recycle-interpreter

(fact "calling `recycle-interpreter` sets up a new program"
  (:program knows-some-things) => [1.1 2.2 :intProductToFloat]
  (:program (recycle-interpreter knows-some-things [1 2 3 4])) => [1 2 3 4])


(fact "calling `recycle-interpreter` sets up new inputs (optionally)"
  (:inputs knows-some-things) => {}
  (:inputs (recycle-interpreter knows-some-things [])) => {}
  (:inputs (recycle-interpreter knows-some-things [] :inputs [1 2 3])) =>
    {:input!1 1, :input!2 2, :input!3 3}
  (:inputs (recycle-interpreter knows-some-things [] :inputs {:a 8 :b 11})) =>
    {:a 8, :b 11})



;; increment-counter


(fact "`increment-counter` increments the counter"
  (:counter knows-some-things) => 22
  (:counter (increment-counter knows-some-things)) => 23)


;; is-done?


(fact "`is-done?` checks the Interpreter for various halting states"
  (is-done? (basic-interpreter)) => true
  (is-done? knows-some-things) => false) ;; counter 22, limit 23


(fact "`is-done?` checks the [:config :step-limit] against the :counter"
  (is-done? (basic-interpreter)) => true
  (is-done? (basic-interpreter :stacks {:exec '()}  :config {:step-limit 99})) => true
  (is-done? (basic-interpreter :stacks {:exec '(2)} :config {:step-limit 99})) => false)


(fact "`is-done?` returns true when :exec and :environment are empty, but not when :exec is empty and :environment has at least one item"
  (is-done? (basic-interpreter :stacks {:exec '()}  :config {:step-limit 99})) => true
  (is-done? (basic-interpreter 
              :stacks {:exec '() :environment '({:integer '(9)})}
              :config {:step-limit 99})) => false)

;; logging

(fact "calling `log-routed-item` adds an item to the `:log` stack with a 'timestamp'"
  (u/get-stack (log-routed-item knows-some-things :exec-foo) :log) => 
    '({:item :exec-foo, :step 22})
  )

;; step


(fact "calling `step` consumes one item from the :exec stack (if any, and if not done)"
  (u/get-stack knows-some-things :exec) => '(:intProductToFloat)
  (u/get-stack (step knows-some-things) :exec) => '())


(fact "calling `step` increments the counter (if something happens)"
  (:counter knows-some-things) => 22
  (:counter (step knows-some-things)) => 23)


(fact "calling `step` doesn't affect the counter if :exec is empty of :done? is true"
  (:counter (basic-interpreter)) => 0
  (:counter (step (basic-interpreter))) => 0
  (:counter (step (u/clear-stack knows-some-things :exec))) => 22)


(fact "calling `step` sets the :done? flag if a halting condition is encountered"
  (is-done? knows-some-things) => false
  (:done? knows-some-things) => false
  (:done? (step knows-some-things)) => true)


(fact "calling `step` won't advance the counter if `:done?` is true at the beginning"
  (:step-limit (:config knows-some-things)) => 23
  (:counter knows-some-things) => 22
  (:done? (step knows-some-things)) => true
  (inc (:counter knows-some-things)) => (:counter (step knows-some-things))
  (:counter (step knows-some-things)) => (:counter (step (step knows-some-things))))


(fact "calling `step` (usually) writes to the :log stack"
  (u/get-stack (step knows-some-things) :log) => 
    '({:item :intProductToFloat, :step 23}))


(fact "calling `step` doesn't log anything if no step was taken"
  (u/get-stack (step (step knows-some-things)) :log) =>
    '({:item :intProductToFloat, :step 23}))


;; merging old environments

(fact "`merge-environment` overwrites most stacks"
  (:stacks (u/merge-environment 
            (basic-interpreter)
            {:integer '(1 2 3)
             :boolean '(false)
             :exec '(:foo)})) =>
    '{:boolean (false),
      :char (), 
      :code (), 
      :environment (), 
      :error (), 
      :exec (:foo), 
      :float (),
      :integer (1 2 3), 
      :log (), 
      :print (), 
      :return (), 
      :string (), 
      :unknown ()})


(fact "`merge-environment` keeps :unknown stack"
  (:stacks (u/merge-environment 
            (assoc-in (basic-interpreter) [:stacks :unknown] '(7 77 777))
            {:unknown '(1 2 3)})) =>
    (contains {:unknown '(7 77 777)}))


(fact "`merge-environment` keeps :error stack"
  (:stacks (u/merge-environment 
            (assoc-in (basic-interpreter) [:stacks :error] '(7 77 777))
            {:error '(1 2 3)})) =>
    (contains {:error '(7 77 777)}))


(fact "`merge-environment` keeps :log stack"
  (:stacks (u/merge-environment 
            (assoc-in (basic-interpreter) [:stacks :log] '(7 77 777))
            {:log '(1 2 3)})) =>
    (contains {:log '(7 77 777)}))


(fact "`merge-environment` keeps :print stack"
  (:stacks (u/merge-environment 
            (assoc-in (basic-interpreter) [:stacks :print] '(7 77 777))
            {:print '(1 2 3)})) =>
    (contains {:print '(7 77 777)}))


(fact "`merge-environment` does not keep the :exec stack (that's for :end-environment to do by hand)"
  (:stacks (u/merge-environment 
            (assoc-in (basic-interpreter) [:stacks :exec] '(7 77 777))
            {:exec '(88)})) =>
    (contains {:exec '(88)}))


;; popping saved :environments


(def ready-to-pop 
  (make-classic-interpreter 
    :config {:step-limit 1000}
    :stacks {:exec '() 
             :environment '({:exec (3 33)})
             :log '(:log1 :log2)
             :error '(:error1 :error2)
             :return '(:return1 :return2)
             :unknown '(:nope :no-idea)
             :integer '(1 2 3)
             }))


(def unready-to-pop 
  (make-classic-interpreter 
    :config {:step-limit 1000}
    :stacks {:exec '() 
             :environment '()
             :log '(:log1 :log2)
             :error '(:error1 :error2)
             :return '(:return1 :return2)
             :unknown '(:nope :no-idea)
             :integer '(1 2 3)
             }))


(fact "calling `step` merges a stored environment if there is one and the :exec stack is empty"
  (is-done? ready-to-pop) => false

  (:stacks (step ready-to-pop)) =>
    '{:boolean (), 
      :char (), 
      :code (), 
      :environment (), 
      :error (:error1 :error2), 
      :exec (:return2 :return1 3 33), 
      :float (), 
      :integer (), 
      :log ({:item "ENVIRONMENT STACK POPPED", :step 1} :log1 :log2), 
      :print (), 
      :return (), 
      :string (), 
      :unknown (:nope :no-idea)})


(fact "the counter advances when the :environment pops"
  (inc (:counter ready-to-pop)) => (:counter (step ready-to-pop)))


(fact "calling `step` does nothing if there's no stored environment"
  (is-done? unready-to-pop) => true

  (:stacks (step unready-to-pop)) =>
    '{:boolean (),
      :char (), 
      :code (), 
      :environment (), 
      :error (:error1 :error2), 
      :exec (), 
      :float (), 
      :integer (1 2 3), 
      :log (:log1 :log2), 
      :print (), 
      :return (:return1 :return2), 
      :string (), 
      :unknown (:nope :no-idea)})


(fact "the counter does not advance when the :environment does not pop!"
  (:counter unready-to-pop) => (:counter (step unready-to-pop)))


;; interrogating an Interpreter instance


(future-fact "calling `produce-gazetteer` prints a list of all registered instructions, all bound inputs, all registered types and modules"
  (produce-gazetteer (make-classic-interpreter :inputs [1 2 false 6.3 '(:code-do)])) => "")


;; `run`

;; a fixture or two


(def simple-things (make-classic-interpreter 
                      :program [1 2 false :integer-add true :boolean-or]
                      :config {:step-limit 1000}))


(fact "calling `run` with an Interpreter and a step argument returns the Interpreter at that step"
  (:stacks (run simple-things 0)) => (contains
                              {:boolean '(), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(1 2 false :integer-add true :boolean-or), 
                               :float   '(), 
                               :integer '(),
                               :print   '(), 
                               :string  '(), 
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run simple-things 1)) => (contains
                              {:boolean '(), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(2 false :integer-add true :boolean-or), 
                               :float   '(), 
                               :integer '(1), 
                               :print   '(), 
                               :string  '(), 
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run simple-things 5)) => (contains
                              {:boolean '(true false), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(:boolean-or), 
                               :float   '(), 
                               :integer '(3), 
                               :print   '(), 
                               :string  '(), 
                               :unknown '()}))


(def forever-8
  (make-classic-interpreter :program [1 :exec-y 8]))


(fact "`run` doesn't care about halting conditions"
  (:stacks (run forever-8 0)) => (contains
                              {:boolean '(), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(1 :exec-y 8), 
                               :float   '(), 
                               :integer '(), 
                               :print   '(), 
                               :string  '(), 
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run forever-8 33)) => (contains
                              {:boolean '(), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(8 :exec-y 8), 
                               :float   '(), 
                               :integer '(8 8 8 8 8 8 8 8 8 8 1), 
                               :print   '(), 
                               :string  '(), 
                               :unknown '()})
  (count (u/get-stack (run forever-8 12000) :integer)) => 4000
  (count (u/get-stack (run forever-8 50000) :integer)) => 16667

  (:done? (run simple-things 0)) =>           false
  (:done? (run simple-things 5)) =>           true
  (:done? (run simple-things 111)) =>         true
  (:done? (run simple-things 6)) =>           true
  (:done? (run forever-8 0)) =>               false
  (:done? (run forever-8 5)) =>               true
  (:done? (run forever-8 111)) =>             true
  (:done? (run (basic-interpreter) 1926)) =>  false)


(fact "`run` produces a log"
    (u/get-stack (run simple-things 177) :log) => '({:item :boolean-or, :step 6}
                                                    {:item true, :step 5} 
                                                    {:item :integer-add, :step 4} 
                                                    {:item false, :step 3} 
                                                    {:item 2, :step 2} 
                                                    {:item 1, :step 1}))


(fact "`run-until-done` runs an Interpreter until it reaches the first step when `:done?` is true"
  (:counter (run-until-done (basic-interpreter))) => 0
  (u/get-stack (run-until-done (basic-interpreter)) :log) => '()

  (:counter (run-until-done simple-things)) => (:counter (run simple-things 1000))

  (:counter (run-until-done forever-8)) => 0 ; because it's step-limit isn't set explicitly!

  (:counter (run-until-done (assoc-in forever-8 [:config :step-limit] 1300))) => 1300
)