(ns push.instructions.aspects.buildable-test
  (:require [push.interpreter.core :as i]
            [push.interpreter.templates.minimum :as m]
            [push.core :as push]
            [push.type.item.complex :as complex]
            [push.type.item.interval :as interval]
            [push.type.definitions.interval :as iv]
            )
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.instructions.aspects.buildable)
  )


;; buildable aspect


(def foo-type
  (make-type :foo :parts {:A   :scalar
                          :B   :image
                          :C   :rgb
                          :D  :scalar}
                  :builder #(hash-map :A %1 :B %2 :C %3 :D %4)
                  ))

(def simple-type
  (make-type :simple :parts {:x :something}
                     :builder #(conj [] %1)
                     ))




(fact "component-list counts what's needed"
  (component-list foo-type) => {:image 1, :rgb 1, :scalar 2}
  (component-list complex/complex-type) => {:scalar 2}
  (component-list interval/interval-type) => {:boolean 2, :scalar 2}
  )



(fact "collect-components creates DSL steps for each item"
  (collect-components foo-type) => 
    '((push.instructions.dsl/consume-top-of :scalar :as :D)
      (push.instructions.dsl/consume-top-of :rgb :as :C)
      (push.instructions.dsl/consume-top-of :image :as :B)
      (push.instructions.dsl/consume-top-of :scalar :as :A))

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
  (:docstring foo-make-test) => "`:foo-make` constructs a new `:foo` item from its component parts, {:scalar 2, :image 1, :rgb 1}."
  (:needs foo-make-test) => {:image 1, :rgb 1, :scalar 2}
  (:products foo-make-test) => {:foo 1}
  )




(def simple-make-test
  (make-instruction simple-type))


(fact "make-instruction constructs an instruction with the correct pieces"
  (keys simple-make-test) => '(:token :docstring :tags :needs :products :transaction)

  (:token simple-make-test) => :simple-make
  (:docstring simple-make-test) => "`:simple-make` constructs a new `:simple` item from its component parts, {:something 1}."
  (:needs simple-make-test) => {:something 1}
  (:products simple-make-test) => {:simple 1}
  )



(fact "the resulting instruction works as described"
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks {:something '(99) :simple '()})
        simple-make-test)
      :simple-make)
    :something) => '()
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks {:something '(99) :simple '()})
        simple-make-test)
      :simple-make)
    :simple) => '([99])
)


(fact "the resulting instruction works as described"
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:scalar '(11 33) :image '(22) :rgb '(-99)})
        foo-make-test)
      :foo-make)
    :foo) => '({:A 33, :B 22, :C -99, :D 11})
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:scalar '(11 33) :image '(22) :rgb '(-99)})
        foo-make-test)
      :foo-make)
    :scalar) => '()
)




(fact "the order of parts produced is in Push ass-backwards argument order"
  (get-stack
    (i/execute-instruction
      (push/interpreter
        :stacks {:scalar '(11 33) :boolean '(true true)})
      :interval-make)
    :interval) => (list (iv/make-open-interval 33 11 ))
)


;;;; parts


(def foo-parts-test
  (parts-instruction foo-type))

(def simple-parts-test
  (parts-instruction simple-type))



(fact "parts-instruction constructs an instruction with the correct pieces"
  (keys foo-parts-test) => '(:token :docstring :tags :needs :products :transaction)

  (:token foo-parts-test) => :foo-parts
  (:docstring foo-parts-test) => "`:foo-parts` constructs a new code block from the component parts of the top `:foo` item (in the order '(:A :B :C :D) and pushes that onto the `:exec` stack."
  (:needs foo-parts-test) => {:foo 1}
  (:products foo-parts-test) => {:exec 1}
  )


(fact "the resulting instruction works as described"
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:foo '({:A 6 :B 77 :C 888 :D 9999})})
        foo-parts-test)
      :foo-parts)
    :foo) => '()
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:foo '({:A 6 :B 77 :C 888 :D 9999})})
        foo-parts-test)
      :foo-parts)
    :exec) => '((9999 888 77 6)))


;; validation


(fact "make-instruction raises an exception if there is no :parts or :builder value for a given type"
  (make-instruction push.type.module.print/print-module) =>
    (throws #"cannot be constructed")
  (parts-instruction push.type.module.print/print-module) =>
    (throws #"cannot be constructed"))


