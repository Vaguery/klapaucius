(ns push.interpreter.interpreter-running-test
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.types.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:require [push.interpreter.templates.minimum :as m])
  (:require [push.interpreter.templates.classic :as c])
  (:require [push.instructions.aspects :as aspects])
  (:use [push.interpreter.core])
  )


(def just-basic (m/basic-interpreter))
(def classy (c/classic-interpreter))


(fact "`handle-item` pushes an item to the :unknown stack if unrecognized when :config :lenient? is true"
  (let [junk {:x 1 :y 2}]
    (u/get-stack
      (handle-item 
        (c/classic-interpreter :config {:lenient? true})
        junk)
      :unknown) => '({:x 1, :y 2})))


(fact "`handle-item` throws an exception on unrecognized items when :config :lenient? is not true (or unset)"
  (handle-item classy just-basic) =>  (throws #"Push Parsing Error: "))


(fact "handle-item pushes unknown keywords to the :ref stack, regardless of :lenient? setting"
  (u/get-stack (handle-item just-basic :foo) :ref) => '(:foo))


;; the router order is: :bound-keyword? :instruction? [router] :unknown


(fact "`router-sees?` checks the router predicates and returns true if one matches"
  (let [abbr #'push.interpreter.core/router-sees?]
    (abbr classy :not-likely) => nil
    (abbr (m/basic-interpreter 
      :router [[(fn [item] (= item :not-likely)) :integer]]) :not-likely) => true))


(fact "`handle-item` checks the :router list"
  (let [he-knows-foo (m/basic-interpreter :router [[integer? :code]])]
        ;; this will route integers to the :code stack, and not to :integer
    (:stacks (handle-item he-knows-foo 99) :code) => (contains {:code '(99)})))


(def foo-type 
  (-> (types/make-type :foo :recognizer integer?)
      aspects/make-visible
      aspects/make-comparable
      aspects/make-equatable
      aspects/make-movable))


(fact "types added to the router with `register-type` are used by `handle-item`"
  (:router (register-type just-basic foo-type)) =>
    [ [(:recognizer foo-type) :foo] ]
  (u/get-stack (handle-item (register-type just-basic foo-type) 99) :integer) => '()
  (u/get-stack (handle-item (register-type just-basic foo-type) 99) :foo) => '(99))


(fact "handle-item sends integers to :integer"
  (u/get-stack (handle-item classy 8) :integer) => '(8)
  (u/get-stack (handle-item classy -8) :integer) => '(-8)
  (u/get-stack (handle-item (c/classic-interpreter :stacks {:integer '(1)}) -8) :integer) =>
    '(-8 1))


; (fact "handle-item handles integer overflow")


(fact "handle-item sends floats to :float"
  (u/get-stack (handle-item classy 8.0) :float) => '(8.0)
  (u/get-stack (handle-item classy -8.0) :float) => '(-8.0)
  (u/get-stack (handle-item (c/classic-interpreter :stacks {:float '(1.0)}) -8.0) :float) =>
    '(-8.0 1.0))


; (fact "handle-item handles float overflow")
; (fact "handle-item handles float underflow")


(fact "handle-item sends booleans to :boolean"
  (u/get-stack (handle-item classy false) :boolean) => '(false)
  (u/get-stack (handle-item classy true) :boolean) => '(true)
  (u/get-stack (handle-item (c/classic-interpreter :stacks {:boolean '(false)}) true) :boolean) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (u/get-stack (handle-item classy \J) :char) => '(\J)
  (u/get-stack (handle-item classy \o) :char) => '(\o)
  (u/get-stack (handle-item (c/classic-interpreter :stacks {:char '(\Y)}) \e) :char) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (u/get-stack (handle-item classy "foo") :string) => '("foo")
  (u/get-stack (handle-item classy "") :string) => '("")
  (u/get-stack (handle-item (c/classic-interpreter :stacks {:string '("bar")}) "baz") :string) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' lists onto :exec"
  (u/get-stack (handle-item classy '(1 2 3)) :exec) => '(1 2 3)
  (u/get-stack (handle-item classy '(1 (2) (3))) :exec) => '(1 (2) (3))
  (u/get-stack (handle-item classy '(1 () ())) :exec) => '(1 () ())
  (u/get-stack (handle-item classy '()) :exec) => '())


(fact "the :exec stack stays a list when a list is unwrapped onto it"
  (list? (u/get-stack (handle-item classy '(1 2 3)) :exec)) => 
    true
  (list? (u/get-stack (handle-item classy '(1 (2) (3))) :exec)) => 
    true
  (list? (u/get-stack (handle-item classy '(1 () ())) :exec)) => 
    true
  (list? (u/get-stack (handle-item classy '()) :exec)) => 
    true)


(fact "handle-item will execute a registered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (m/basic-interpreter :instructions registry)]
   (handle-item he-knows-foo :foo) => 761))
    ;; an intentionally surprising result


(fact "handle-item will not execute an unregistered instruction"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] 761))
       registry {:foo foo}
       he-knows-foo (m/basic-interpreter :instructions registry)]
   (handle-item he-knows-foo {:thing 9}) => (throws #"Push Parsing Error:")))


;; some fixtures:


(def intProductToFloat
  (instr/build-instruction intProductToFloat
    (dsl/consume-top-of :integer :as :arg1)
    (dsl/consume-top-of :integer :as :arg2)
    (dsl/calculate [:arg1 :arg2] #(float (* %1 %2)) :as :p)
    (dsl/push-onto :float :p)))


(def knows-some-things
  (register-instruction
    (m/basic-interpreter 
      :program [1.1 2.2 :intProductToFloat]
      :counter 22
      :stacks {:integer '(1 2 3)
               :exec '(:intProductToFloat)}
      :config {:step-limit 23})
    intProductToFloat))


;; clear-all-stacks


(fact "`clear-all-stacks` empties every stack"
  (:stacks (clear-all-stacks knows-some-things)) => m/minimal-stacks)


;; reset-interpreter


(fact "calling `reset-interpreter` loads the program onto :exec"
  (u/get-stack knows-some-things :exec) => '(:intProductToFloat)
  (u/get-stack (reset-interpreter knows-some-things) :exec) =>
    '(1.1 2.2 :intProductToFloat))


(fact "calling `reset-interpreter` clears the other stacks"
  (u/get-stack knows-some-things :integer) => '(1 2 3)
  (:stacks (reset-interpreter knows-some-things)) => 
    (merge m/minimal-stacks {:exec '(1.1 2.2 :intProductToFloat)}))


(fact "`reset-interpreter` sets the counter to 0"
  (let [counted (assoc knows-some-things :counter 9912)]
    (:counter counted) => 9912
    (:counter (reset-interpreter counted)) => 0))


;; recycle-interpreter

(fact "calling `recycle-interpreter` sets up a new program"
  (:program knows-some-things) => [1.1 2.2 :intProductToFloat]
  (:program (recycle-interpreter knows-some-things [1 2 3 4])) => [1 2 3 4])



(fact "calling `recycle-interpreter` sets up new inputs (optionally)"
  (:bindings knows-some-things) => {}
  (:bindings (recycle-interpreter knows-some-things [])) => {}
  (:bindings (recycle-interpreter knows-some-things [] :bindings [1 2 3])) =>
    {:input!1 '(1), :input!2 '(2), :input!3 '(3)}
  (:bindings (recycle-interpreter knows-some-things [] :bindings {:a 8 :b 11})) =>
    {:a '(8), :b '(11)})



;; increment-counter


(fact "`increment-counter` increments the counter"
  (:counter knows-some-things) => 22
  (:counter (increment-counter knows-some-things)) => 23)


;; is-done?


(fact "`is-done?` checks the Interpreter for various halting states"
  (is-done? just-basic) => true
  (is-done? knows-some-things) => false) ;; counter 22, limit 23


(fact "`is-done?` checks the [:config :step-limit] against the :counter"
  (is-done? just-basic) => true
  (is-done? (m/basic-interpreter :stacks {:exec '()}  :config {:step-limit 99})) => true
  (is-done? (m/basic-interpreter :stacks {:exec '(2)} :config {:step-limit 99})) => false)


(fact "`is-done?` returns true when :exec and :environment are empty, but not when :exec is empty and :environment has at least one item"
  (is-done? (m/basic-interpreter :stacks {:exec '()}  :config {:step-limit 99})) => true
  (is-done? (m/basic-interpreter 
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
  (:counter just-basic) => 0
  (:counter (step just-basic)) => 0
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
            just-basic
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
            (assoc-in just-basic [:stacks :unknown] '(7 77 777))
            {:unknown '(1 2 3)})) =>
    (contains {:unknown '(7 77 777)}))


(fact "`merge-environment` keeps :error stack"
  (:stacks (u/merge-environment 
            (assoc-in just-basic [:stacks :error] '(7 77 777))
            {:error '(1 2 3)})) =>
    (contains {:error '(7 77 777)}))


(fact "`merge-environment` keeps :log stack"
  (:stacks (u/merge-environment 
            (assoc-in just-basic [:stacks :log] '(7 77 777))
            {:log '(1 2 3)})) =>
    (contains {:log '(7 77 777)}))


(fact "`merge-environment` keeps :print stack"
  (:stacks (u/merge-environment 
            (assoc-in just-basic [:stacks :print] '(7 77 777))
            {:print '(1 2 3)})) =>
    (contains {:print '(7 77 777)}))


(fact "`merge-environment` does not keep the :exec stack (that's for :end-environment to do by hand)"
  (:stacks (u/merge-environment 
            (assoc-in just-basic [:stacks :exec] '(7 77 777))
            {:exec '(88)})) =>
    (contains {:exec '(88)}))


;; popping saved :environments


(def ready-to-pop 
  (c/classic-interpreter 
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
  (c/classic-interpreter 
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


;; `run-n`

;; a fixture or two


(def simple-things (c/classic-interpreter 
                      :program [1 2 false :integer-add true :boolean-or]
                      :config {:step-limit 1000}))


(fact "calling `run-n` with an Interpreter and a step argument returns the Interpreter at that step"
  (:stacks (run-n simple-things 0)) => (contains
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
  (:stacks (run-n simple-things 1)) => (contains
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
  (:stacks (run-n simple-things 5)) => (contains
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

;; checking for program typography for Lee et al

(fact "I can run a program that is passed in as a seq"
  (:stacks (run-n
            (c/classic-interpreter 
              :program '(1 2 ((false :integer-add) (true)) :boolean-or)
              :config {:step-limit 1000}) 1000)) => (contains
                              {:boolean '(true), 
                               :char    '(), 
                               :code    '(), 
                               :error   '(), 
                               :exec    '(), 
                               :float   '(), 
                               :integer '(3),
                               :print   '(), 
                               :string  '(), 
                               :unknown '()}))


(def forever-8
  (c/classic-interpreter :program [1 :exec-y 8]))


(fact "`run-n` doesn't care about halting conditions"
  (:stacks (run-n forever-8 0)) => (contains
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
  (:stacks (run-n forever-8 33)) => (contains
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
  (count (u/get-stack (run-n forever-8 12000) :integer)) => 4000
  (count (u/get-stack (run-n forever-8 50000) :integer)) => 16667

  (:done? (run-n simple-things 0)) =>           false
  (:done? (run-n simple-things 5)) =>           true
  (:done? (run-n simple-things 111)) =>         true
  (:done? (run-n simple-things 6)) =>           true
  (:done? (run-n forever-8 0)) =>               false
  (:done? (run-n forever-8 5)) =>               true
  (:done? (run-n forever-8 111)) =>             true
  (:done? (run-n just-basic 1926)) =>  false)


(fact "`run-n` produces a log"
    (u/get-stack (run-n simple-things 177) :log) => '({:item :boolean-or, :step 6}
                                                    {:item true, :step 5} 
                                                    {:item :integer-add, :step 4} 
                                                    {:item false, :step 3} 
                                                    {:item 2, :step 2} 
                                                    {:item 1, :step 1}))


;; entire-run


(fact "`entire-run` produces a lazy seq of all the steps from the initialization to the stated endpoint"
  (count (entire-run simple-things 22)) => 22
  (map #(u/get-stack % :integer) (entire-run simple-things 22)) =>
    '(() (1) (2 1) (2 1) (3) (3) (3) (3) (3) (3)
      (3) (3) (3) (3) (3) (3) (3) (3) (3) (3) (3) (3)))


;; last-changed-step


(fact "`last-changed-step` returns the last point at which an interpreter stack contents change, within the specified number of steps, when running the specified program"
  (:counter (last-changed-step simple-things 22000)) => 6
  (u/get-stack (last-changed-step simple-things 22000) :exec) => '()

  (:counter (last-changed-step forever-8 1000)) => 1000
  (u/get-stack (last-changed-step forever-8 1000) :exec) => '(:exec-y 8))


(fact "`run-until-done` runs an Interpreter until it reaches the first step when `:done?` is true"
  (:counter (run-until-done just-basic)) => 0
  (u/get-stack (run-until-done just-basic) :log) => '()

  (:counter (run-until-done simple-things)) => (:counter (run-n simple-things 1000))

  (:counter (run-until-done forever-8)) => 0 ; because it's step-limit isn't set explicitly!

  (:counter (run-until-done (assoc-in forever-8 [:config :step-limit] 1300))) => 1300)



;; push-program-to-code


(fact "push-program-to-code pushes the stored :program (as a code block) to :code stack"
  (u/get-stack (push-program-to-code forever-8) :code) => '((1 :exec-y 8))

  (u/get-stack (push-program-to-code simple-things) :code) =>
    '((1 2 false :integer-add true :boolean-or)))


;; reset-interpreter and push-program-to-code


(fact "reset-interpreter will invoke push-program-to-code if :config contains {:preload-code? true}"
  (u/get-stack (reset-interpreter knows-some-things) :code) => '()

  (:config knows-some-things) => {:lenient? false, :max-collection-size 131072, :step-limit 23}
  
  (:config
    (reset-interpreter
      (reconfigure knows-some-things {:preload-code? true}))) =>
    {:lenient? false, :preload-code? true, :max-collection-size 131072, :step-limit 23}


  (u/get-stack
    (reset-interpreter
      (reconfigure knows-some-things {:preload-code? true}))
    :code) => '((1.1 2.2 :intProductToFloat)))
