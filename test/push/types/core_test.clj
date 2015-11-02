(ns push.types.core-test
  (:use midje.sweet)
  (:use [push.types.core])
  (:require [push.interpreter.interpreter-core :as i]))


;;;; type information


;; PushType records


(fact "`make-type` takes a keyword and recognizer"
  (make-type :integer :recognizer integer?) =>
    {:stackname :integer, :recognizer integer?, :attributes #{}, :instructions []})


(fact "`make-type` takes an optional :attributes set"
  (:attributes (make-type 
                  :integer 
                  :recognizer integer? 
                  :attributes #{:comparable :numeric})) => #{:comparable :numeric})

