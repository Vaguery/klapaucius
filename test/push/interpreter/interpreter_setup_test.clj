(ns push.interpreter.interpreter-setup-test
  (:require [push.util.stack-manipulation :as u]
            [push.instructions.core :as i]
            [push.instructions.dsl :as d]
            [push.type.core :as types]
            [push.interpreter.templates.one-with-everything :as everything]
            [push.instructions.aspects :as aspects]
            [push.interpreter.templates.minimum :as m]
            [push.interpreter.definitions :as record]
            [push.core :as push])
  (:use midje.sweet)
  (:use [push.util.type-checkers :only (boolean?)])
  (:use push.interpreter.core)
  )


(def just-basic (m/basic-interpreter))
(def supreme (push/interpreter))


;; program


(fact "a new Interpreter will have an empty program"
  (:program just-basic) => [])


(fact "a Push program (a vector) can be passed into basic-interpreter"
  (:program (m/basic-interpreter :program [1 2 3])) => [1 2 3])


(fact "a Push program can be passed as a seq if desired"
  (:program (m/basic-interpreter :program '(1 2 3))) => [1 2 3])


;; config defaults


(fact "a `basic-interpreter` has its :step-limit set to 0 by default"
  (:step-limit (:config just-basic)) => 0)


(fact "a `basic-interpreter` has its :max-collection-size set to 128k by default"
  (:max-collection-size (:config just-basic)) => (* 128 1024))


(fact "a core interpreter has its :max-collection-size set to 128k by default"
  (:max-collection-size (:config (push/interpreter))) => (* 128 1024))



; a fixture or two


(def foo-type
  (-> (types/make-type :foo :recognized-by integer?)
      aspects/make-visible
      aspects/make-comparable
      aspects/make-equatable
      aspects/make-movable))


(fact "foo-type knows some things (just checking)"
  (keys (:instructions foo-type)) =>
    (contains
      [:foo-againlater :foo-stackdepth :foo-empty? :foo<?
      :foo≤? :foo>? :foo≥? :foo-min :foo-max
      :foo-equal? :foo-notequal? :foo-dup :foo-flush
      :foo-pop :foo-rotate :foo-shove :foo-swap
      :foo-yank :foo-yankdup] :gaps-ok :in-any-order)
    (:name foo-type) => :foo)


(def bar-type
  (-> (types/make-type :bar :recognized-by keyword?)
      aspects/make-visible
      aspects/make-equatable
      aspects/make-movable))


(fact "bar-type knows some things (also just checking)"
  (keys (:instructions bar-type)) =>
    (contains
      '(:bar-againlater :bar-notequal? :bar-dup :bar-swap :bar-rotate
        :bar-flush :bar-stackdepth :bar-equal? :bar-empty?
        :bar-pop :bar-yank :bar-yankdup :bar-shove) :gaps-ok :in-any-order)
    (:name bar-type) => :bar)


;;;; types


;; router


(fact "if unspecified, the :router table is empty"
  (:routers just-basic) => [])


(fact "a :routers table can be added manually"
  (:routers (m/basic-interpreter :routers [[integer? :code]])) => [[integer? :code]])


;; register-type


(fact "`register-type` adds the type passed in to an Interpreter"
  (:types (register-type just-basic foo-type)) =>
    (contains foo-type))


(fact "`register-type` adds the stack type to the Interpreter stacks, if needed"
  (keys (:stacks (register-type just-basic foo-type))) => (contains :foo))


(fact "`register-type` adds any instructions attached to the type to the Interpreter"
  (keys (:instructions (register-type just-basic foo-type))) =>
    (contains [:foo-rotate :foo-equal? :foo>? :foo-stackdepth :foo-notequal?
                :foo<? :foo-pop :foo-flush :foo-empty? :foo-dup :foo-min :foo≥?
                :foo-swap :foo-max :foo-shove :foo≤? :foo-yankdup :foo-yank] :gaps-ok :in-any-order))


(fact "`register-type` adds the type's :router to the Interpreter's :router collection"
  (map :name (:routers just-basic)) => []
  (map :name (:routers (register-type just-basic foo-type))) =>[:foo]
  (map :name (:routers (->  just-basic
                (register-type foo-type)
                (register-type bar-type)))) => [:foo :bar]
  )


(fact "`register-type` does not empty a stack if it already contains stuff"
  (let [loaded-with-foo (m/basic-interpreter :stacks {:foo '(7 77 777)})]
    (u/get-stack loaded-with-foo :foo) => '(7 77 777)
    (u/get-stack (register-type loaded-with-foo foo-type) :foo) => '(7 77 777)))


;; register-module


;; a fixture


(def foo-barbaz
  (i/build-instruction
    foo-barbaz))


(def foo-qux
  (i/build-instruction
    foo-barqux))


(def foo-module
  (-> (types/make-module :foo)
      (types/attach-instruction , foo-barbaz)
      (types/attach-instruction , foo-qux)))


(fact "`register-module` adds any instructions attached to the module to the Interpreter"
  (keys (:instructions
    (register-module just-basic foo-module))) => '(:foo-barbaz :foo-barqux))


;; register-types


(fact "`register-types` will register a collection of PushTypes"
  (:types (register-types just-basic [foo-type bar-type])) =>
    (contains [foo-type bar-type] :in-any-order))


(fact "`register-types` sets up all the stacks"
  (keys (:stacks (register-types just-basic [foo-type bar-type]))) =>
    (contains [:foo :bar] :in-any-order :gaps-ok))


(fact "`register-types` adds all the instructions"
  (keys (:instructions (register-types just-basic
                                        [foo-type bar-type]))) =>
    (contains [:bar-againlater :bar-dup :bar-empty? :bar-equal? :bar-flush :bar-notequal?
      :bar-pop :bar-rotate :bar-shove :bar-stackdepth :bar-swap
      :bar-yank :bar-yankdup :foo-againlater :foo-dup :foo-empty? :foo-equal?
      :foo-flush :foo-max :foo-min :foo-notequal? :foo-pop :foo-rotate
      :foo-shove :foo-stackdepth :foo-swap :foo-yank :foo-yankdup
      :foo<? :foo>? :foo≤? :foo≥?] :gaps-ok :in-any-order))


;; register-modules


;; a fixture


(def bar-barbaz
  (i/build-instruction
    bar-barbaz))


(def bar-module
  (-> (types/make-module :bar)
      (types/attach-instruction , bar-barbaz)))


(fact "`register-modules` adds all the instructions"
  (sort (keys (:instructions (register-modules just-basic
                                               [foo-module bar-module])))) =>
    '(:bar-barbaz :foo-barbaz :foo-barqux))


;; registering types on initialization


(fact "a list of PushTypes can be passed into `basic-interpreter` and are added to :types"
  (map :name (:types (m/basic-interpreter :types [foo-type]))) =>
    '(:foo)
  (map :name (:types (m/basic-interpreter :types [foo-type bar-type]))) =>
    (just [:bar :foo]))


(fact "if a PushType is passed into `basic-interpreter`, its stack is added"
    (keys (:stacks (m/basic-interpreter :types [foo-type]))) =>
            (contains :foo))


(fact "if a PushType is passed into `basic-interpreter`, its instructions are registered"
  (keys (:instructions (m/basic-interpreter :types [foo-type]))) =>
    (contains [:foo-rotate :foo-equal? :foo>? :foo-stackdepth :foo-notequal?
               :foo<? :foo-pop :foo-flush :foo-empty? :foo-dup :foo-min
               :foo≥? :foo-swap :foo-max :foo-shove :foo≤? :foo-yankdup
               :foo-yank] :gaps-ok :in-any-order))


(fact "if a PushType is passed into `basic-interpreter`, its recognizer is added to the :router"
  (let [foo-recognizer [(:recognized-by foo-type) :foo] ]
    (map :name (:routers (m/basic-interpreter :types [foo-type]))) => [:foo]
      ))


;; finesse (justified paranoia)


(fact "registering a new type in an Interpreter with stuff defined still leaves that stuff intact"
  (let [knows-foo (m/basic-interpreter :types [foo-type])]
    (map :name (:types (register-type knows-foo bar-type))) =>
        '(:bar :foo)
    (keys (:stacks (register-type knows-foo bar-type))) =>
        (contains [ :foo :bar] :in-any-order :gaps-ok)
    (keys (:instructions (register-type knows-foo bar-type))) =>
        (contains [:bar-notequal? :foo-rotate :foo-equal? :foo>? :bar-dup
                  :foo-stackdepth :bar-swap :foo-notequal? :bar-rotate :foo<?
                  :foo-pop :bar-flush :foo-flush :foo-empty? :bar-stackdepth
                  :foo-dup :foo-min :foo≥? :bar-equal? :bar-empty? :foo-swap
                  :foo-max :foo-shove :foo≤? :bar-pop :foo-yankdup :bar-yank
                  :bar-yankdup :bar-shove :foo-yank] :gaps-ok :in-any-order)))


;; instructions


(fact "a new Interpreter will have an :instructions map, empty by default"
  (:instructions just-basic) => {})


;; bindings


(fact "a new Interpreter passed an :bindings vector will have the bindings registered"
  (:bindings (m/basic-interpreter :bindings [1 2 3 4])) =>
    {:input!1 '(1), :input!2 '(2), :input!3 '(3), :input!4 '(4)})


(fact "a new Interpreter passed an :bindings hashmap will have the bindings registered"
  (:bindings (m/basic-interpreter :bindings {:a 2 :b 4})) => {:a '(2), :b '(4)})


;; config


(fact "a new Interpreter will have a :config map"
  (nil? (:config just-basic)) => false
  (empty? (:config just-basic)) => false)


(fact "a new Interpreter can have :config items set or overridden at creation"
  (:config (m/basic-interpreter :config {:lenient? true :foo 8})) =>
    (contains {:foo 8}))


(fact "all new Interpreters should have a :max-collection-size 131072"
  (:config (record/make-interpreter)) =>
    (contains {:max-collection-size 131072}))


;; counter


(fact "a new Interpreter will have counter = 0"
  (:counter just-basic) => 0)


(fact "a counter value can be passed into basic-interpreter"
  (:counter (m/basic-interpreter :counter 771)) => 771 )


;; done?


(fact "a new interpreter will have a default :done? setting of false"
  (:done? just-basic) => false )


(fact "a new interpreter can have :done? set as an option"
  (:done? (m/basic-interpreter :done? true)) => true )



(fact "the core stack types are defined"
  (keys m/minimal-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :snapshot
                                    :error
                                    :exec
                                    :scalar
                                    :log
                                    :print
                                    :return
                                    :string
                                    :unknown] :in-any-order))


;; non-core but standard library core types: :tag, :genome, :puck, etc
; must be loaded after initialization


(fact "a new Interpreter will have all the core stacks"
  (keys (:stacks just-basic)) => (keys m/minimal-stacks))


(fact "basic-interpreter can be passed a hashmap of populated stacks to merge into the core"
  (u/get-stack just-basic :scalar ) => '()
  (u/get-stack (m/basic-interpreter :stacks {:scalar '(7 6 5)}) :scalar ) => '(7 6 5)
  (u/get-stack (m/basic-interpreter :stacks {:foo '(7 6 5)}) :foo ) => '(7 6 5)
  (u/get-stack (m/basic-interpreter :stacks {:foo '(7 6 5)}) :scalar ) => '()
  (u/get-stack just-basic :foo ) => '())


;;;; manipulating existing interpreters


;; register-instruction


(fact "register-instruction adds an Instruction to the registry in a specified Interpreter"
  (let [foo (i/make-instruction :foo)]
    (keys (:instructions
      (register-instruction just-basic foo))) => '(:foo)
    (:foo (:instructions (register-instruction just-basic foo))) => foo))


(fact "register-instruction throws an exception if a token is reassigned "
  (let [foo (i/make-instruction :foo)]
    (register-instruction (register-instruction just-basic foo) foo) =>
      (throws Exception "Push Instruction Redefined:':foo'")))


;; forget-instruction

(fact "forget-instruction drops the indicated instruction from the Interpreter"
  (keys (:instructions supreme)) => (contains :scalar-add)
  (keys (:instructions
    (forget-instruction supreme :scalar-add))) =not=> (contains :scalar-add)
  (keys (:instructions
    (forget-instruction supreme :foo-bar))) => (keys (:instructions supreme)))



;; contains-at-least?


(fact "contains-at-least? returns true if the count of the specified stack is >= the number"
  (let [abbr #'push.interpreter.core/contains-at-least?]
  (abbr (m/basic-interpreter :stacks {:scalar '(1 2 3)}) :scalar 3) => true
  (abbr (m/basic-interpreter :stacks {:scalar '(0)}) :scalar 3) => false
  (abbr (m/basic-interpreter :stacks {:scalar '()}) :scalar 0) => true
  (abbr (m/basic-interpreter :stacks {:scalar '(1 2 3)}) :scalar 2) => true))



;; ready-for-instruction?


(fact "ready-for-instruction? returns false if the :needs of the specified instruction aren't met"
  (let [abbr #'push.interpreter.core/ready-for-instruction?
        foo
          (i/make-instruction :foo :needs {:bar 2})
        an-int
          (register-instruction (m/basic-interpreter :stacks {:bar '(1)}) foo)
        many-ints
          (register-instruction (m/basic-interpreter :stacks {:bar '(1 2 3 4)}) foo)]
    (count (u/get-stack an-int :bar)) => 1
    (abbr an-int :foo) => false
    (count (u/get-stack many-ints :bar )) => 4
    (#'push.interpreter.core/contains-at-least?
        many-ints :bar 2) => true
    (abbr many-ints :foo) => true))


(fact "ready-for-instruction? returns false if the named instruction is not registered"
  (let [abbr #'push.interpreter.core/ready-for-instruction?
        foo
          (i/make-instruction :foo :needs {:bar 2})
        an-int
          (register-instruction (m/basic-interpreter :stacks {:bar '(1)}) foo)]
    (abbr an-int :bar) => false))


;; execute-instruction


(fact "execute-instruction applies the named instruction to the Interpreter itself"
  (let [foo (i/make-instruction :foo :transaction (fn [a] 99))
        bar (i/make-instruction :bar :transaction (fn [a] a))
        he-knows-foo (register-instruction
          (register-instruction just-basic foo) bar)]
    (keys (:instructions he-knows-foo)) => (just :foo :bar)
    (execute-instruction he-knows-foo :foo) => 99
    (execute-instruction he-knows-foo :bar) => he-knows-foo
    ))


(fact "execute-instruction will add an :error message if the needs aren't met"
  (let [foo (i/make-instruction :foo :needs {:bar 3} :transaction (fn [a] 99))
        he-knows-foo (register-instruction just-basic foo)]
    (u/get-stack (execute-instruction he-knows-foo :foo) :error) =>
      '({:step 0 :item ":foo missing arguments"})
    (execute-instruction
      (u/set-stack he-knows-foo :bar '(1 2 3 4)) :foo) => 99
    ))


(fact "execute-instruction will throw an Exception if the token is not registered"
    (execute-instruction just-basic :foo) => (throws #"Unknown Push instruction:"))


;; utilities and helpers


(fact "u/get-stack is a convenience function for reading the named stack"
  (u/get-stack (m/basic-interpreter :stacks {:boolean '(false true)}) :boolean) =>
    '(false true))


(fact "u/get-stack will happily provide an empty stack if one's missing"
  (u/get-stack just-basic :foo) => '())


(fact "u/get-stack will return an empty list for an existing (but empty) stack"
  (u/get-stack just-basic :scalar) => '())


(fact "u/set-stack replaces a stack completely"
  (u/get-stack (u/set-stack just-basic :bar '(1 2 3)) :bar) => '(1 2 3)
  )


(fact "boolean? checks that an item is specifically one of the LITERALS true or false"
  (boolean? 8) => false
  (boolean? true) => true
  (boolean? false) => true
  (boolean? nil) => false
  (boolean? '()) => false)


(fact "instruction? checks that an item is a registered Instruction in a given Interpreter"
 (let [abbr #'push.interpreter.core/instruction?
       foo (i/make-instruction :foo)
       registry {:foo foo}
       he-knows-foo (m/basic-interpreter :instructions registry)]
   (abbr he-knows-foo :foo) => true
   (abbr he-knows-foo :bar) => false))


;; dealing with stack items


(fact "push-item pushes the specified item to the stack, returning the updated Interpreter"
  (u/get-stack (push-item just-basic :bar 9) :bar) => '(9)
  (u/get-stack (push-item
    (m/basic-interpreter :stacks {:bar '(1 2 3)}) :bar 9) :bar) => '(9 1 2 3))


(fact "push-item does not do type-checking"
  (u/get-stack (
    push-item (m/basic-interpreter :stacks {:scalar '(1 2 3)}) :scalar false)  :scalar) =>
    '(false 1 2 3))


(fact "push-item will create a stack if told to"
  (u/get-stack (push-item just-basic :foo [1 :weird :thing]) :foo) =>
    '([1 :weird :thing]))


(fact "push-item will not change the interpreter if the item is nil"
  (u/get-stack (push-item just-basic :scalar nil) :scalar) => '())


;; reconfigure


(fact "reconfigure merges its argument hash with the interpreter's current :config"
  (let [some-config (m/basic-interpreter :config {:lenient? true :foo 8})]
    (:config some-config) =>
      {:foo 8, :lenient? true, :max-collection-size 131072, :step-limit 0}

    (:config (reconfigure some-config {:foo nil :bar 888})) =>
      {:bar 888, :foo nil, :lenient? true, :max-collection-size 131072, :step-limit 0}))
