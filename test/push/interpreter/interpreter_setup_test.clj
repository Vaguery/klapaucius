(ns push.interpreter.interpreter-setup-test
  (:use midje.sweet)
  (:use [push.util.type-checkers :only (boolean?)])
  (:use push.util.stack-manipulation)
  (:use [push.instructions.dsl])
  (:use [push.interpreter.core])
  (:require [push.instructions.core :as i])
  (:require [push.types.core :as types])
  )


;;;; initialization with `basic-interpreter`


;; NOTE: `basic-interpreter` is intended to be used to build EMPTY and/or
;; "basic" interpreters with only bare-bones functionality, not competent
;; Push-aware ones.


;; To run Push programs immediately, you want `make-classic-interpreter` below.


;; program


(fact "a new Interpreter will have an empty program"
  (:program (basic-interpreter)) => [])


(fact "a Push program (a vector) can be passed into basic-interpreter"
  (:program (basic-interpreter :program [1 2 3])) => [1 2 3])


; a fixture or two 


(def foo-type 
  (-> (types/make-type :foo :recognizer integer?)
      types/make-visible
      types/make-comparable
      types/make-equatable
      types/make-movable))


(fact "foo-type knows some things (just checking)"
  (keys (:instructions foo-type)) =>
    (contains
      [:foo-stackdepth :foo-empty? :foo<? 
      :foo≤? :foo>? :foo≥? :foo-min :foo-max 
      :foo-equal? :foo-notequal? :foo-dup :foo-flush 
      :foo-pop :foo-rotate :foo-shove :foo-swap 
      :foo-yank :foo-yankdup] :in-any-order)
    (:stackname foo-type) => :foo)


(def bar-type 
  (-> (types/make-type :bar :recognizer keyword?)
      types/make-visible
      types/make-equatable
      types/make-movable))


(fact "bar-type knows some things (also just checking)"
  (keys (:instructions bar-type)) =>
    (contains
      '(:bar-notequal? :bar-dup :bar-swap :bar-rotate 
        :bar-flush :bar-stackdepth :bar-equal? :bar-empty? 
        :bar-pop :bar-yank :bar-yankdup :bar-shove) :in-any-order)
    (:stackname bar-type) => :bar)


;;;; types


;; router


(fact "if unspecified, the :router table is empty"
  (:router (basic-interpreter)) => [])


(fact "a :router table can be added manually"
  (:router (basic-interpreter :router [[integer? :code]])) => [[integer? :code]])


;; register-type


(fact "`register-type` adds the type passed in to an Interpreter"
  (:types (register-type (basic-interpreter) foo-type)) => 
    (contains foo-type))


(fact "`register-type` adds the stack type to the Interpreter stacks, if needed"
  (keys (:stacks (register-type (basic-interpreter) foo-type))) => (contains :foo))


(fact "`register-type` adds any instructions attached to the type to the Interpreter"
  (keys (:instructions (register-type (basic-interpreter) foo-type))) => 
    (contains [:foo-rotate :foo-equal? :foo>? :foo-stackdepth :foo-notequal?
                :foo<? :foo-pop :foo-flush :foo-empty? :foo-dup :foo-min :foo≥? 
                :foo-swap :foo-max :foo-shove :foo≤? :foo-yankdup :foo-yank] :in-any-order))


(fact "`register-type` adds the :recognizer to the Interpreter's :router collection"
  (:router (basic-interpreter)) => []
  (:router (register-type (basic-interpreter) foo-type)) =>
    [ [(:recognizer foo-type) :foo] ]

  (:router (->  (basic-interpreter) 
                (register-type foo-type)
                (register-type bar-type))) =>
    [ [(:recognizer foo-type) :foo] 
      [(:recognizer bar-type) :bar] ])


;; register-types


(fact "`register-types` will register a collection of PushTypes"
  (:types (register-types (basic-interpreter) [foo-type bar-type])) => 
    (contains [foo-type bar-type] :in-any-order))


(fact "`register-types` sets up all the stacks"
  (keys (:stacks (register-types (basic-interpreter) [foo-type bar-type]))) =>
    (contains [:foo :bar] :in-any-order :gaps-ok))


(fact "`register-types` adds all the instructions"
  (sort (keys (:instructions (register-types (basic-interpreter)
                                        [foo-type bar-type])))) => 
    '(:bar-dup :bar-empty? :bar-equal? :bar-flush :bar-notequal? 
      :bar-pop :bar-rotate :bar-shove :bar-stackdepth :bar-swap 
      :bar-yank :bar-yankdup :foo-dup :foo-empty? :foo-equal? 
      :foo-flush :foo-max :foo-min :foo-notequal? :foo-pop :foo-rotate 
      :foo-shove :foo-stackdepth :foo-swap :foo-yank :foo-yankdup 
      :foo<? :foo>? :foo≤? :foo≥?))


;; registering types on initialization


(fact "a list of PushTypes can be passed into `basic-interpreter` and are added to :types"
  (map :stackname (:types (basic-interpreter :types [foo-type]))) => 
    '(:foo)
  (map :stackname (:types (basic-interpreter :types [foo-type bar-type]))) => 
    (just [:bar :foo]))


(fact "if a PushType is passed into `basic-interpreter`, its stack is added"
    (keys (:stacks (basic-interpreter :types [foo-type]))) =>
            (contains :foo))


(fact "if a PushType is passed into `basic-interpreter`, its instructions are registered"
  (keys (:instructions (basic-interpreter :types [foo-type]))) =>
    (contains [:foo-rotate :foo-equal? :foo>? :foo-stackdepth :foo-notequal? 
               :foo<? :foo-pop :foo-flush :foo-empty? :foo-dup :foo-min 
               :foo≥? :foo-swap :foo-max :foo-shove :foo≤? :foo-yankdup 
               :foo-yank] :in-any-order))


(fact "if a PushType is passed into `basic-interpreter`, its recognizer is added to the :router"
  (let [foo-recognizer [(:recognizer foo-type) :foo] ]
    (:router (basic-interpreter :types [foo-type])) => (contains [foo-recognizer])))


;; finesse (justified paranoia)


(fact "registering a new type in an Interpreter with stuff defined still leaves that stuff intact"
  (let [knows-foo (basic-interpreter :types [foo-type])]
    (map :stackname (:types (register-type knows-foo bar-type))) => 
        '(:bar :foo)
    (keys (:stacks (register-type knows-foo bar-type))) =>
        (contains [ :foo :bar] :in-any-order :gaps-ok)
    (keys (:instructions (register-type knows-foo bar-type))) =>
        (contains [:bar-notequal? :foo-rotate :foo-equal? :foo>? :bar-dup 
                  :foo-stackdepth :bar-swap :foo-notequal? :bar-rotate :foo<? 
                  :foo-pop :bar-flush :foo-flush :foo-empty? :bar-stackdepth 
                  :foo-dup :foo-min :foo≥? :bar-equal? :bar-empty? :foo-swap 
                  :foo-max :foo-shove :foo≤? :bar-pop :foo-yankdup :bar-yank 
                  :bar-yankdup :bar-shove :foo-yank] :in-any-order)))


;; instructions


(fact "a new Interpreter will have an :instructions map, empty by default"
  (:instructions (basic-interpreter)) => {})


;; inputs


(fact "a new Interpreter passed an :inputs vector will have the bindings registered"
  (:inputs (basic-interpreter :inputs [1 2 3 4])) =>
    {:input!1 1, :input!2 2, :input!3 3, :input!4 4})


(fact "a new Interpreter passed an :inputs hashmap will have the bindings registered"
  (:inputs (basic-interpreter :inputs {:a 2 :b 4})) => {:a 2, :b 4})


;; config


(fact "a new Interpreter will have a :config map"
  (:config (basic-interpreter)) => {})


(fact "a new Interpreter can have :config items set at creation"
  (:config (basic-interpreter :config {:lenient? true})) => {:lenient? true})


;; counter


(fact "a new Interpreter will have counter = 0"
  (:counter (basic-interpreter)) => 0)


(fact "a counter value can be passed into basic-interpreter"
  (:counter (basic-interpreter :counter 771)) => 771 )


;; done?


(fact "a new interpreter will have a default :done? setting of false"
  (:done? (basic-interpreter)) => false )


(fact "a new interpreter can have :done? set as an option"
  (:done? (basic-interpreter :done? true)) => true )



(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :error
                                    :exec 
                                    :float 
                                    :integer
                                    :print
                                    :string
                                    :unknown] :in-any-order))


;; non-core but standard library core types: :tag, :genome, :return, :print, :puck, etc
; must be loaded after initialization


(fact "a new Interpreter will have all the core stacks"
  (keys (:stacks (basic-interpreter))) => (keys core-stacks))


(fact "basic-interpreter can be passed a hashmap of populated stacks to merge into the core"
  (get-stack (basic-interpreter) :integer ) => '()
  (get-stack (basic-interpreter :stacks {:integer '(7 6 5)}) :integer ) => '(7 6 5)
  (get-stack (basic-interpreter :stacks {:foo '(7 6 5)}) :foo ) => '(7 6 5)
  (get-stack (basic-interpreter :stacks {:foo '(7 6 5)}) :integer ) => '()
  (get-stack (basic-interpreter) :foo ) => nil)


;;;; make-classic-interpreter


;; this function creates a Push interpreter that pre-loads all the core types
;; and their instructions on creation


(fact "`make-classic-interpreter` has `classic-integer-type` registered"
  (:types (make-classic-interpreter)) =>
    (contains push.types.base.integer/classic-integer-type))


(fact "`make-classic-interpreter` has `classic-boolean-type` registered"
  (:types (make-classic-interpreter)) =>
    (contains push.types.base.boolean/classic-boolean-type))


(future-fact "`make-classic-interpreter` has `classic-float-type` registered"
  (:types (make-classic-interpreter)) =>
    (contains push.types.base.float/classic-float-type))


(future-fact "`make-classic-interpreter` has `classic-code-type` registered"
  (:types (make-classic-interpreter)) =>
    (contains push.types.base.code/classic-code-type))


(future-fact "`make-classic-interpreter` has `classic-exec-type` registered"
  (:types (make-classic-interpreter)) =>
    (contains push.types.base.exec/classic-exec-type))


(future-fact "`make-classic-interpreter` can have its :stacks set")


(future-fact "`make-classic-interpreter` can have its :inputs set")


(future-fact "`make-classic-interpreter` can have its :config set")


(future-fact "`make-classic-interpreter` can have its :counter set")


;;;; manipulating existing interpreters


;; register-instruction


(fact "register-instruction adds an Instruction to the registry in a specified Interpreter"
  (let [foo (i/make-instruction :foo)]
    (keys (:instructions 
      (register-instruction (basic-interpreter) foo))) => '(:foo)
    (:foo (:instructions (register-instruction (basic-interpreter) foo))) => foo))


(fact "register-instruction throws an exception if a token is reassigned (because that's what Clojush does)"
  (let [foo (i/make-instruction :foo)]
    (register-instruction (register-instruction (basic-interpreter) foo) foo) =>
      (throws Exception "Push Instruction Redefined:':foo'")))


;; contains-at-least?


(fact "contains-at-least? returns true if the count of the specified stack is >= the number"
  (let [abbr #'push.interpreter.core/contains-at-least?]
  (abbr (basic-interpreter) :integer 0) => true
  (abbr (basic-interpreter) :integer 3) => false
  (abbr (basic-interpreter :stacks {:integer '(1 2 3)}) :integer 3) => true
  (abbr (basic-interpreter :stacks {:integer '(1 2 3)}) :integer 2) => true))


(fact "contains-at-least? returns false if the named stack isn't present"
  (let [abbr #'push.interpreter.core/contains-at-least?]

  (abbr (basic-interpreter) :foo 0) => false
  (abbr (basic-interpreter) :boolean 0) => true))


;; ready-for-instruction?


(fact "ready-for-instruction? returns false if the :needs of the specified instruction aren't met"
  (let [abbr #'push.interpreter.core/ready-for-instruction?
        foo
          (i/make-instruction :foo :needs {:integer 2})
        an-int
          (register-instruction (basic-interpreter :stacks {:integer '(1)}) foo)
        many-ints
          (register-instruction (basic-interpreter :stacks {:integer '(1 2 3 4)}) foo)]
    (count (get-stack an-int :integer)) => 1
    (abbr an-int :foo) => false
    (count (get-stack many-ints :integer )) => 4
    (#'push.interpreter.core/contains-at-least?
        many-ints :integer 2) => true
    (abbr many-ints :foo) => true))


(fact "ready-for-instruction? returns false if the named instruction is not registered"
  (let [abbr #'push.interpreter.core/ready-for-instruction?
        foo
          (i/make-instruction :foo :needs {:integer 2})
        an-int
          (register-instruction (basic-interpreter :stacks {:integer '(1)}) foo)]
    (abbr an-int :bar) => false))


;; execute-instruction


(fact "execute-instruction applies the named instruction to the Interpreter itself"
  (let [foo (i/make-instruction :foo :transaction (fn [a] 99))
        bar (i/make-instruction :bar)
        he-knows-foo (register-instruction
          (register-instruction (basic-interpreter) foo) bar)]
    (keys (:instructions he-knows-foo)) => (just :foo :bar)
    (execute-instruction he-knows-foo :foo) => 99
    (execute-instruction he-knows-foo :bar) => he-knows-foo))


(fact "execute-instruction will not change the Interpreter if the needs aren't met"
  (let [foo (i/make-instruction :foo :needs {:integer 3} :transaction (fn [a] 99))
      he-knows-foo (register-instruction (basic-interpreter) foo)]
    (execute-instruction he-knows-foo :foo) => he-knows-foo ;; not enough integers
    (execute-instruction
      (assoc-in he-knows-foo [:stacks :integer] '(1 2 3 4)) :foo) => 99))


(fact "execute-instruction will throw an Exception if the token is not registered"
    (execute-instruction (basic-interpreter) :foo) => (throws #"Unknown Push instruction:"))


;; utilities and helpers


(fact "get-stack is a convenience function for reading the named stack"
  (get-stack (basic-interpreter :stacks {:boolean '(false true)}) :boolean) =>
    '(false true))


(fact "get-stack will happily look up and return any named stack"
  (get-stack (basic-interpreter) :foo) => nil)


(fact "get-stack will return an empty list for an existing (but empty) stack"
  (get-stack (basic-interpreter) :integer) => '())


(fact "set-stack replaces a stack completely"
  (get-stack (set-stack (basic-interpreter) :integer '(1 2 3)) :integer) => '(1 2 3)
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
       he-knows-foo (basic-interpreter :instructions registry)]
   (abbr he-knows-foo :foo) => true
   (abbr he-knows-foo :bar) => false))


;; dealing with stack items


(fact "push-item pushes the specified item to the stack, returning the updated Interpreter"
  (get-stack (push-item (basic-interpreter) :integer 9) :integer) => '(9)
  (get-stack (push-item 
    (basic-interpreter :stacks {:integer '(1 2 3)}) :integer 9) :integer) => '(9 1 2 3))


(fact "push-item does not do type-checking"
  (get-stack (
    push-item (basic-interpreter :stacks {:integer '(1 2 3)}) :integer false)  :integer) =>
    '(false 1 2 3))


(fact "push-item will create a stack if told to"
  (get-stack (push-item (basic-interpreter) :foo [1 :weird :thing]) :foo) =>
    '([1 :weird :thing]))



