(ns push.instructions.aspects.buildable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m]
            [push.type.item.complex :as complex]
            [push.type.item.interval :as interval]
            )
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.buildable)
  )


;; buildable aspect


(def foo-type
  (make-type :foo :parts {:bar   :scalar
                          :baz   :image
                          :color :rgb
                          :quux  :scalar}
                  :builder #(into [] %1 %2 %3 %4)
                  ))

(def simple-type
  (make-type :simple :parts {:x :something}
                     :builder #(into [] %1)
                     ))




(fact "component-list counts what's needed"
  (component-list foo-type) => {:image 1, :rgb 1, :scalar 2}
  (component-list complex/complex-type) => {:scalar 2}
  (component-list interval/interval-type) => {:boolean 2, :scalar 2}
  )



(fact "collect-components creates DSL steps for each item"
  (collect-components foo-type) => 
    '((push.instructions.dsl/consume-top-of :scalar :as :quux)
      (push.instructions.dsl/consume-top-of :rgb :as :color)
      (push.instructions.dsl/consume-top-of :image :as :baz)
      (push.instructions.dsl/consume-top-of :scalar :as :bar))

  (collect-components complex/complex-type) =>
    '((push.instructions.dsl/consume-top-of :scalar :as :im)
      (push.instructions.dsl/consume-top-of :scalar :as :re))

  (collect-components interval/interval-type) =>
    '((push.instructions.dsl/consume-top-of :boolean :as :max-open?)
      (push.instructions.dsl/consume-top-of :boolean :as :min-open?)
      (push.instructions.dsl/consume-top-of :scalar :as :max)
      (push.instructions.dsl/consume-top-of :scalar :as :min)))




(fact "invoke-builder constructs a list of two DSL steps"
  (count (invoke-builder foo-type)) => 2)




(def foo-make-test
  (make-instruction foo-type))


(fact "make-instruction constructs an instruction with the correct pieces"
  (keys foo-make-test) => '(:token :docstring :tags :needs :products :transaction)

  (:token foo-make-test) => :foo-make
  (:docstring foo-make-test) => "`:foo-make` constructs a new `:foo` item from its components parts, {:scalar 2, :image 1, :rgb 1}."
  (:needs foo-make-test) => {:image 1, :rgb 1, :scalar 2}
  (:products foo-make-test) => {:foo 1}
  )




(def simple-make-test
  (make-instruction simple-type))


(fact "make-instruction constructs an instruction with the correct pieces"
  (keys simple-make-test) => '(:token :docstring :tags :needs :products :transaction)

  (:token simple-make-test) => :simple-make
  (:docstring simple-make-test) => "`:simple-make` constructs a new `:simple` item from its components parts, {:something 1}."
  (:needs simple-make-test) => {:something 1}
  (:products simple-make-test) => {:simple 1}
  )
