(ns push.router.core-test
  (:use midje.sweet)
  (:use [push.router.core])
  )


;; PushRouter records


(fact "`make-router` constructs a router with reasonable defaults"
  (let [foo (make-router :foo )]
    (:name foo) => :foo
    ((:recognizer foo) 9) => false
    (:target-stack foo) => :foo
    ((:preprocessor foo) 99121) => 99121 
  ))


(fact "I can override the `make-router` defaults with keywords"
  (let [foo (make-router :foo
              :recognizer integer?
              :target-stack :number
              :preprocessor #(* 2 %))]
    (:name foo) => :foo
    ((:recognizer foo) 9) => true
    (:target-stack foo) => :number
    ((:preprocessor foo) 99121) => 198242
  ))


(fact "`router-recognize?` applies the :recognizer of a router to an item"
  (let [foo (make-router :foo
              :recognizer integer?)]
    (router-recognize? foo 99) => true
    (router-recognize? foo 9.9) => false
    (router-recognize? (make-router :bar) 7) => false
    (router-recognize? (make-router :bar) :anything) => false
  ))


(fact "I can override some defaults if I like"
  (router-recognize? (make-router :foo :recognizer float?) 3.2) => true
  (router-recognize? (make-router :foo :recognizer float?) 32) => false
  (router-recognize? (make-router :foo :recognizer integer?) 32) => true
  )


(fact "I can apply the preprocessor with `router-preprocess`"
  (router-preprocess (make-router :foo) 99) => 99 ;; default
  (router-preprocess (make-router :foo :preprocessor #(* 2 %)) 99) => 198
  )