(ns push.types.core-test
  (:use midje.sweet)
  (:require [push.interpreter.core :as i])
  (:use [push.types.core])
  )


;;;; type information


;; PushType records


(fact "`make-type` takes a keyword and recognizer"
  (make-type :integer :recognizer integer?) =>
    {:stackname :integer, :recognizer integer?, :attributes #{}, :instructions {}})


(fact "`make-type` defaults the :recognizer to #(false)"
  ((:recognizer (make-type :foo)) 99) => false)


(fact "`make-type` takes an optional :attributes set"
  (:attributes (make-type 
                  :integer 
                  :recognizer integer? 
                  :attributes #{:comparable :numeric})) => #{:comparable :numeric})


(fact "the core stack types are defined"
  (keys core-stacks) =>  (contains [:boolean
                                    :char
                                    :code
                                    :exec 
                                    :float 
                                    :integer 
                                    :string] :in-any-order))
