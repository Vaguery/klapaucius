(ns push.instructions.base.ref_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.types.base.ref])
  )


(fact "ref-type knows some instructions"
  (keys (:instructions ref-type)) =>
    (contains [:ref-equal? :ref-flush :ref->code :ref-stackdepth] :in-any-order :gaps-ok))




(tabular
  (fact ":ref-new creates a new `:ref!\\d\\d\\d` keyword"
    (register-type-and-check-instruction
        ?set-stack ?items ref-type ?instruction ?get-stack) =>
            ?expected)

    ?set-stack  ?items      ?instruction  ?get-stack   ?expected
    :ref        '()         :ref-new       :ref        #(keyword? (first %))
    :ref        '()         :ref-new       :ref        #(re-seq #":ref\!\d+" (str (first %))))


