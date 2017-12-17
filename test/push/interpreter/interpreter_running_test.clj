(ns push.interpreter.interpreter-running-test
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.type.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:require [push.interpreter.templates.minimum :as m])
  (:require [push.instructions.aspects :as aspects])
  (:require [push.core :as push])
  (:require [push.type.module.code :as code])
  (:require [push.router.core :as router])
  (:require [push.type.definitions.quoted :as qc])
  (:require [push.type.definitions.snapshot :as snap])
  (:use [push.interpreter.core])
  )


(def just-basic (m/basic-interpreter))
(def supreme (push/interpreter))


(fact "`handle-item` pushes an item to the :unknown stack if unrecognized when :config :lenient? is true"
  (let [junk {:x 1 :y 2}]
    (u/get-stack
      (handle-item
        (push/interpreter :config {:lenient? true})
        junk)
      :unknown) => '({:x 1, :y 2})))


(fact "`handle-item` throws an exception on unrecognized items when :config :lenient? is not true (or unset)"
  (handle-item
    (push/interpreter :config {:lenient? false})
    (push/interpreter)) =>  (throws #"Push Parsing Error: "))


(fact "handle-item pushes unknown keywords to the :ref stack, regardless of :lenient? setting"
  (u/get-stack (handle-item just-basic :foo) :ref) => '(:foo))


(fact "handle-item will push unregistered keywords, but not bound ones with empty bindings"
  (u/get-stack (handle-item
    (push/interpreter :bindings {:a 8}) :a) :ref) => '() ;; lookup
  (u/get-stack (handle-item
    (push/interpreter :bindings {:a 8}) :a) :exec) => '(8) ;; lookup
  (u/get-stack (handle-item
    (push/interpreter :bindings {:a 8 :b nil}) :b) :ref) => '()
  (u/get-stack (handle-item
    (push/interpreter :bindings {:a 8 :b nil}) :b) :exec) => '()


  (let [dumb (push/interpreter :bindings {:a 8 :b nil})]
    (routers-see? dumb :X)=> true
    (routers-see? dumb :a)=> true
    (routers-see? dumb :b)=> true
    (u/get-stack (handle-item dumb :a) :ref) => '()
    (u/get-stack (handle-item dumb :b) :ref) => '()
    (u/get-stack (handle-item dumb :X) :ref) => '(:X)
    (u/get-stack (handle-item dumb :a) :exec) => '(8)
    (u/get-stack (handle-item dumb :b) :exec) => '()
    (u/get-stack (handle-item dumb :X) :exec) => '()
    ))



;; the router order is: :bound-keyword? :instruction? [router] :unknown


(fact "`handle-item` checks the :router list"
  (let [number-code (types/make-type :foo
                                      :router (router/make-router
                                        :foo
                                        :recognizer integer?
                                        :target-stack :code))
        weird-interpreter
          (register-type
            (m/basic-interpreter :config {:lenient? true})
            number-code)]
    (map :name (:routers weird-interpreter)) => [:foo]
    (routers-see? weird-interpreter 99) => true
    (routers-see? weird-interpreter 2.2) => false
    (u/get-stack (handle-item weird-interpreter 999) :code) => '(999)
    (u/get-stack (handle-item weird-interpreter 2.2) :code) => '()
    (u/get-stack (handle-item weird-interpreter 2.2) :unknown) => '(2.2)
    ))



(def foo-type
  (-> (types/make-type :foo :recognized-by integer?)
      aspects/make-visible
      aspects/make-comparable
      aspects/make-equatable
      aspects/make-movable))


(fact "types added to the router with `register-type` are used by `handle-item`"
  (map :name (:routers (register-type just-basic foo-type))) => [:foo]
  (u/get-stack (handle-item (register-type just-basic foo-type) 99) :scalar) => '()
  (u/get-stack (handle-item (register-type just-basic foo-type) 99) :foo) => '(99))



(fact "handle-item sends scalars to :scalar (when told to)"
  (u/get-stack (handle-item supreme 8) :scalar) => '(8)
  (u/get-stack (handle-item supreme -8) :scalar) => '(-8)
  (:stacks (push/interpreter :stacks {:scalar '(1)})) =>
    (contains {:scalar '(1)})
  (:stacks
    (handle-item (push/interpreter :stacks {:scalar '(1)}) -8)) =>
    (contains {:scalar '(-8 1)})
    )



(fact "handle-item unquotes QuotedCode items it routes"
  (u/get-stack (handle-item supreme
    (qc/push-quote 88)) :scalar) => '()
  (u/get-stack (handle-item supreme
    (qc/push-quote 88)) :code) => '(88)
  (u/get-stack (handle-item supreme
    (qc/push-quote
      (qc/push-quote 88))) :code) => (list (qc/push-quote 88))
  )


; :quote-refs?


(fact "if the :quote-refs? flag is true in the interpreter, all keywords ALWAYS go to :refs"
  (let [knows-a (push/interpreter :bindings {:a 8})]
    (:bindings knows-a) => {:a '(8)}
    (bound-keyword? knows-a :a) => true
    (u/get-stack (handle-item knows-a :a) :ref) => '()   ;; normal
    (u/get-stack (handle-item knows-a :a) :exec) => '(8)) ;; normal

  (let [kinda-knows-a
          (assoc-in
            (push/interpreter :bindings {:a 8})
            [:config :quote-refs?]
            true)]
    (:bindings kinda-knows-a) => {:a '(8)}
    (bound-keyword? kinda-knows-a :a) => true
    (u/get-stack (handle-item kinda-knows-a :a) :ref) => '(:a)   ;; quoting!
    (u/get-stack (handle-item kinda-knows-a :a) :exec) => '()    ;; quoting
    (u/get-stack (handle-item kinda-knows-a :boolean-not) :ref) => '()
                                                                 ;; but not insttructions!
  ))


(fact "handle-item sends booleans to :boolean"
  (u/get-stack (handle-item supreme false) :boolean) => '(false)
  (u/get-stack (handle-item supreme true) :boolean) => '(true)
  (u/get-stack (handle-item (push/interpreter :stacks {:boolean '(false)}) true) :boolean) =>
    '(true false))


(fact "handle-item sends characters to :char"
  (u/get-stack (handle-item supreme \J) :char) => '(\J)
  (u/get-stack (handle-item supreme \o) :char) => '(\o)
  (u/get-stack (handle-item (push/interpreter :stacks {:char '(\Y)}) \e) :char) =>
    '(\e \Y))


(fact "handle-item sends strings to :string"
  (u/get-stack (handle-item supreme "foo") :string) => '("foo")
  (u/get-stack (handle-item supreme "") :string) => '("")
  (u/get-stack (handle-item (push/interpreter :stacks {:string '("bar")}) "baz") :string) =>
    '("baz" "bar"))


(fact "handle-item 'unwraps' lists onto :exec"
  (u/get-stack (handle-item supreme '(1 2 3)) :exec) => '(1 2 3)
  (u/get-stack (handle-item supreme '(1 (2) (3))) :exec) => '(1 (2) (3))
  (u/get-stack (handle-item supreme '(1 () ())) :exec) => '(1 () ())
  (u/get-stack (handle-item supreme '()) :exec) => '())


(fact "the :exec stack stays a list when a list is unwrapped onto it"
  (list? (u/get-stack (handle-item supreme '(1 2 3)) :exec)) =>
    true
  (list? (u/get-stack (handle-item supreme '(1 (2) (3))) :exec)) =>
    true
  (list? (u/get-stack (handle-item supreme '(1 () ())) :exec)) =>
    true
  (list? (u/get-stack (handle-item supreme '()) :exec)) =>
    true)




(fact "handle-item will execute an unregistered instruction if :lenient is true (and will record it in `:current-item`)"
 (let [foo-noop (instr/make-instruction :foo-noop)
       registry {:foo-noop foo-noop}
       he-knows-foo (m/basic-interpreter
                      :instructions registry
                      :config {:lenient? true})]
   (handle-item he-knows-foo :foo-noop) =>
    (assoc he-knows-foo :current-item :foo-noop)
    ))





(fact "handle-item will not execute an unregistered instruction if :lenient is false"
 (let [foo (instr/make-instruction :foo :transaction (fn [a] [761]))
       registry {:foo foo}
       he-knows-foo (m/basic-interpreter :instructions registry :config {:lenient? false})]
   (handle-item he-knows-foo {:thing 9}) => (throws #"Push Parsing Error:")))


(fact "handle-item records the item being processed in `:current-item` of the interpreter (for error reporting and introspection)"
  (:current-item (handle-item supreme false)) => false
  (:current-item (handle-item supreme 99)) => 99
  (:current-item (handle-item supreme :j)) => :j
  (:current-item (handle-item supreme nil)) => nil
)


;; argument retention


(fact "store-item-in-ARGS does what it sounds like, but only if :store-args? is true"
  (let [i (push/interpreter :config {:store-args? true})]
    (:bindings i) => {}
    (:bindings (store-item-in-ARGS i 9999)) => '{:ARGS (9999)}))



(fact "store-item-in-ARGS does what it sounds like, but not if :store-args? is false"
  (let [i (push/interpreter :config {:store-args? false})]
    (:bindings i) => {}
    (:bindings (store-item-in-ARGS i 9999)) => '{}))



(fact "append-item-to-exec does what it sounds like, but only if :cycle-args? is true"
  (let [i (push/interpreter :config {:cycle-args? true})]
    (u/get-stack i :exec) => '()
    (u/get-stack (append-item-to-exec i '(9999)) :exec) => '((9999))))



(fact "append-item-to-exec does what it sounds like, but not if :cycle-args? is false"
  (let [i (push/interpreter :config {:cycle-args? false})]
    (u/get-stack i :exec) => '()
    (u/get-stack (append-item-to-exec i '(9999)) :exec) => '()))



(fact "`apply-instruction`"
  (let [i (push/interpreter
            :stacks {:scalar '(1 2 3)}
            :config {:store-args? true})]
    (:bindings (apply-instruction i :scalar-add)) => {:ARGS '((2 1))}
    ))



(fact "when :store-args? is true (in :config), the arguments consumed by instructions are saved as a seq onto the :ARGS ref"
  (let [i (push/interpreter
            :stacks {:scalar '(1 2 3)}
            :config {:store-args? true})]
    (:config (handle-item i :scalar-add)) => (contains {:store-args? true})
    (:bindings (handle-item i :scalar-add)) => (contains '{:ARGS ((2 1))})
    (u/get-stack (handle-item i :scalar-add) :exec) => '(3)
    ))



(fact "when :cycle-args? is true (in :config), the arguments consumed by instructions are saved as a code block at the end of the `:exec` stack"
  (let [i (push/interpreter
            :stacks {:scalar '(1 2 3) :exec '()}
            :config {:cycle-args? true})]
    (:config (handle-item i :scalar-add)) => (contains {:cycle-args? true})
    (u/get-stack (handle-item i :scalar-add) :exec) => '(3 (2 1))
    ))



(fact "when both :store-args? and :cycle-args? are true (in :config), the arguments consumed by instructions are saved in both locations"
  (let [i (push/interpreter
            :stacks {:scalar '(1 2 3) :exec '()}
            :config {:store-args? true :cycle-args? true})]
    (:config (handle-item i :scalar-add)) => (contains {:cycle-args? true})
    (:bindings (handle-item i :scalar-add)) => (contains '{:ARGS ((2 1))})
    (u/get-stack (handle-item i :scalar-add) :exec) => '(3 (2 1))
    ))


;; some fixtures:


(def intProductToFloat
  (instr/build-instruction intProductToFloat
    (dsl/consume-top-of :scalar :as :arg1)
    (dsl/consume-top-of :scalar :as :arg2)
    (dsl/calculate [:arg1 :arg2] #(float (* %1 %2)) :as :p)
    (dsl/push-onto :float :p)))


(def knows-some-things
  (register-instruction
    (m/basic-interpreter
      :program [1.1 2.2 :intProductToFloat]
      :counter 22
      :stacks {:scalar '(1 2 3)
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
  (u/get-stack knows-some-things :scalar) => '(1 2 3)
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


(fact "`is-done?` returns true when :exec and :snapshot are empty, but not when :exec is empty and :snapshot has at least one item"
  (is-done? (m/basic-interpreter :stacks {:exec '()}  :config {:step-limit 99})) => true
  (is-done? (m/basic-interpreter
              :stacks {:exec '() :snapshot '({:scalar '(9)})}
              :config {:step-limit 99})) => false)

;; logging

(fact "calling `log-routed-item` adds an item to the `:log` stack with a 'timestamp'"
  (u/get-stack (log-routed-item knows-some-things :exec-foo) :log) =>
    '({:item ":exec-foo", :step 22})
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
    '({:item ":intProductToFloat", :step 23}))


(fact "calling `step` doesn't log anything if no step was taken"
  (u/get-stack (step (step knows-some-things)) :log) =>
    '({:item ":intProductToFloat", :step 23}))


;; merging old snapshots

(def s (snap/snapshot (assoc knows-some-things :bindings {:foo '()})))

(fact "snapshot record has stuff"
  (:bindings s) => {:foo '()}
  (:config s) => (:config knows-some-things)
  (:stacks s) => (:stacks knows-some-things) )


(fact "merge-snapshot overwrites :bindings"
  (:bindings (u/merge-snapshot just-basic s)) => (:bindings s))


(fact "merge-snapshot overwrites :config"
  (:config
    (u/merge-snapshot (assoc just-basic :config {}) s)) =>
      (:config s))


(fact "`merge-snapshot` keeps :unknown stack"
  (:stacks (u/merge-snapshot
            (assoc-in just-basic [:stacks :unknown] '(7 77 777))
            (assoc-in s [:stacks :unknown] '(1 2 3)))) =>
    (contains {:unknown '(7 77 777)}))


(fact "`merge-snapshot` keeps :error stack"
  (:stacks (u/merge-snapshot
            (assoc-in just-basic [:stacks :error] '(7 77 777))
            (assoc-in s [:stacks :error] '(1 2 3)))) =>
    (contains {:error '(7 77 777)}))


(fact "`merge-snapshot` keeps :log stack"
  (:stacks (u/merge-snapshot
            (assoc-in just-basic [:stacks :log] '(7 77 777))
            (assoc-in s [:stacks :log] '(1 2 3)))) =>
    (contains {:log '(7 77 777)}))


(fact "`merge-snapshot` keeps :print stack"
  (:stacks (u/merge-snapshot
            (assoc-in just-basic [:stacks :print] '(7 77 777))
            (assoc-in s [:stacks :print] '(1 2 3)))) =>
    (contains {:print '(7 77 777)}))


(fact "`merge-snapshot` DOES NOT keep :exec stack"
  (:stacks (u/merge-snapshot
            (assoc-in just-basic [:stacks :exec] '(7 77 777))
            (assoc-in s [:stacks :exec] '(1 2 3)))) =>
    (contains {:exec '(1 2 3)}))



;; popping saved :snapshots

(def s (snap/snapshot
        (assoc-in knows-some-things [:stacks :exec] '(888 777))))


(def ready-to-pop
  (push/interpreter
    :config {:step-limit 1000}
    :stacks {:exec '()
             :snapshot (list s)
             :log '(:log1 :log2)
             :error '(:error1 :error2)
             :return '(:return1 :return2)
             :unknown '(:nope :no-idea)
             :scalar '(1 2 3)
             }))


(def unready-to-pop
  (push/interpreter
    :config {:step-limit 1000}
    :stacks {:exec '()
             :snapshot '()
             :log '(:log1 :log2)
             :error '(:error1 :error2)
             :return '(:return1 :return2)
             :unknown '(:nope :no-idea)
             :scalar '(1 2 3)
             }))


(fact "calling `step` merges a stored snapshot if there is one and the :exec stack is empty"

  (is-done? ready-to-pop) => false

  (:stacks (step ready-to-pop)) => (contains
    '{ :error (:error1 :error2),
       :exec (:return2 :return1),
       :log ({:item "SNAPSHOT STACK POPPED", :step 1} :log1 :log2),
       :print (),
       :scalar (1 2 3),
       :snapshot (),
       :unknown (:nope :no-idea)}))


(fact "the counter advances when the :snapshot pops"
  (inc (:counter ready-to-pop)) => (:counter (step ready-to-pop)))


(fact "calling `step` does nothing if there's no stored snapshot"
  (is-done? unready-to-pop) => true

  (:stacks (step unready-to-pop)) =>
    '{:boolean (), :booleans (), :char (), :chars (), :code (), :complex (), :complexes (), :snapshot (), :error (:error1 :error2), :exec (), :generator (), :interval (), :intervals(),

      :log (:log1 :log2),

      :print (), :quoted (), :ref (), :refs (), :return (:return1 :return2),

      :scalar (1 2 3),

      :scalars (), :set (), :string (), :strings (), :tagspace (),

      :unknown (:nope :no-idea),

      :vector ()})


(fact "the counter does not advance when the :snapshot does not pop!"
  (:counter unready-to-pop) => (:counter (step unready-to-pop)))


;; `run-n`

;; a fixture or two


(def simple-things (push/interpreter
                      :program [1 2 false :scalar-add true :boolean-or]
                      :config {:step-limit 1000}))


(fact "calling `run-n` with an Interpreter and a step argument returns the Interpreter at that step"
  (:stacks (run-n simple-things 0)) => (contains
                              {:boolean '(),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(1 2 false :scalar-add true :boolean-or),
                               :scalar '(),
                               :print   '(),
                               :string  '(),
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run-n simple-things 1)) => (contains
                              {:boolean '(),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(2 false :scalar-add true :boolean-or),
                               :scalar  '(1),
                               :print   '(),
                               :string  '(),
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run-n simple-things 6)) => (contains
                              {:boolean '(true false),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(:boolean-or),
                               :scalar  '(3),
                               :print   '(),
                               :string  '(),
                               :unknown '()}))

;; checking for program typography for Lee et al

(fact "I can run a program that is passed in as a seq"
  (:stacks (run-n
            (push/interpreter
              :program '(1 2 ((false :scalar-add) (true)) :boolean-or)
              :config {:step-limit 1000}) 1000)) => (contains
                              {:boolean '(true),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(),
                               :scalar '(3),
                               :print   '(),
                               :string  '(),
                               :unknown '()}))


(def forever-8
  (push/interpreter :program [:exec-laterloop 1 2 3 4 5 6 7 8]
                    :config {:step-limit 6}))


(fact "`run-n` doesn't care about halting conditions"
  (:stacks (run-n forever-8 0)) => (contains
                              {:boolean '(),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(:exec-laterloop 1 2 3 4 5 6 7 8),
                               :scalar  '(),
                               :print   '(),
                               :string  '(),
                               :unknown '()})
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (:stacks (run-n forever-8 6)) => (contains
                              {:boolean '(),
                               :char    '(),
                               :code    '(),
                               :error   '(),
                               :exec    '(7 8 (1 :exec-laterloop 1)),
                               :scalar  '(6 5 4 3 2),
                               :print   '(),
                               :string  '(),
                               :unknown '()})

  (:done? (run-n simple-things 0)) =>           false
  (:done? (run-n simple-things 5)) =>           true
  (:done? (run-n simple-things 111)) =>         true
  (:done? (run-n simple-things 6)) =>           true
  (:done? (run-n forever-8 0)) =>               false
  (:done? (run-n forever-8 5)) =>               true
  (:done? (run-n forever-8 111)) =>             true
  (:done? (run-n just-basic 1926)) =>  false)


(fact "`run-n` produces a log"
    (u/get-stack (run-n simple-things 177) :log) => '(
      {:item "true" :step 8}
      {:item ":boolean-or" :step 7}
      {:item "true" :step 6}
      {:item "3" :step 5}
      {:item ":scalar-add" :step 4}
      {:item "false" :step 3}
      {:item "2" :step 2}
      {:item "1" :step 1}
      ))


;; entire-run


(fact "`entire-run` produces a lazy seq of all the steps from the initialization to the stated endpoint"
  (count (entire-run simple-things 22)) => 22
  (map #(u/get-stack % :scalar) (entire-run simple-things 22)) =>
    '(() (1) (2 1) (2 1) () (3) (3) (3) (3) (3)
      (3) (3) (3) (3) (3) (3) (3) (3) (3) (3) (3) (3)))


;; last-changed-step


(fact "`last-changed-step` returns the last point at which an interpreter stack contents change, within the specified number of steps, when running the specified program"
  (:counter (last-changed-step simple-things 22000)) => 8
  (u/get-stack (last-changed-step simple-things 22000) :exec) => '()

  (:counter (last-changed-step forever-8 1000)) => 12
  (u/get-stack (last-changed-step forever-8 1000) :exec) => '())


(fact "`run-until-done` runs an Interpreter until it reaches the first step when `:done?` is true"
  (:counter (run-until-done just-basic)) => 0
  (u/get-stack (run-until-done just-basic) :log) => '()

  (:counter (run-until-done simple-things)) => (:counter (run-n simple-things 1000))

  (:counter (run-until-done (push/interpreter :program [1 2 3]))) => 0

  (:counter (run-until-done (assoc-in forever-8 [:config :step-limit] 5))) => 5)



;; push-program-to-code


(fact "push-program-to-code pushes the stored :program (as a code block) to :code stack"
  (u/get-stack (push-program-to-code forever-8) :code) =>
    '((:exec-laterloop 1 2 3 4 5 6 7 8))

  (u/get-stack (push-program-to-code simple-things) :code) =>
    '((1 2 false :scalar-add true :boolean-or)))


;; reset-interpreter and push-program-to-code


(fact "reset-interpreter will invoke push-program-to-code if :config contains {:preload-code? true}"
  (u/get-stack (reset-interpreter knows-some-things) :code) => '()

  (:config knows-some-things) => {:lenient? true, :max-collection-size 131072, :step-limit 23}

  (:config
    (reset-interpreter
      (reconfigure knows-some-things {:preload-code? true}))) =>
    {:lenient? true, :preload-code? true, :max-collection-size 131072, :step-limit 23}


  (u/get-stack
    (reset-interpreter
      (reconfigure knows-some-things {:preload-code? true}))
    :code) => '((1.1 2.2 :intProductToFloat)))
