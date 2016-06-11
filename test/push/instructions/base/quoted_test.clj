(ns push.instructions.base.quoted_test
  (:require [push.core :as p])
  (:require [push.interpreter.core :as i])
  (:require [push.interpreter.templates.minimum :as basic])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.quoted])
  )


(def simple (basic/basic-interpreter))


(fact "I can register the QC type in a new interpreter"
  (map :name (:types (i/register-type simple quoted-type))) => [:quoted]
  (map :name (:routers (i/register-type simple quoted-type))) => [:quoted]
  (:target-stack (first (:routers (i/register-type simple quoted-type)))) => :code
  )


(def owe (p/interpreter :bindings {:a 99}))


(fact "the interpreter recognizes QC items"
  (i/routers-see? owe (push-quote 88)) => true
  )


(fact "handling a QuotedCode item will route the contents of the QC to :code"
  (p/get-stack
    (p/run owe [(push-quote 88)] 100)
    :code) => '(88)
  (p/get-stack
    (p/run owe [(push-quote (push-quote 88))] 100)
    :code) => (list (push-quote 88))
  )