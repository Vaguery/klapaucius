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
  (make-type :foo :manifest {:A   :scalar
                             :B   :image
                             :C   :rgb
                             :D  :scalar}
                  :builder #(hash-map :A %1 :B %2 :C %3 :D %4)
                  ))

(def simple-type
  (make-type :simple :manifest {:x :something}
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




(def foo-maker
  (construct-instruction foo-type))


(fact "construct-instruction constructs an instruction with the correct pieces"
  (keys foo-maker) => '(:token :docstring :tags :needs :products :transaction)

  (:token foo-maker) => :foo-construct
  (:docstring foo-maker) => "`:foo-construct` constructs a new `:foo` item from its components, {:scalar 2, :image 1, :rgb 1}."
  (:needs foo-maker) => {:image 1, :rgb 1, :scalar 2}
  (:products foo-maker) => {:exec 1}
  )




(def simple-maker
  (construct-instruction simple-type))


(fact "construct-instruction constructs an instruction with the correct pieces"
  (keys simple-maker) => '(:token :docstring :tags :needs :products :transaction)

  (:token simple-maker) => :simple-construct
  (:docstring simple-maker) => "`:simple-construct` constructs a new `:simple` item from its components, {:something 1}."
  (:needs simple-maker) => {:something 1}
  (:products simple-maker) => {:exec 1}
  )



(fact "the resulting instruction works as described"
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks {:something '(99) :simple '()})
        simple-maker)
      :simple-construct)
    :something) => '()
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks {:something '(99) :simple '()})
        simple-maker)
      :simple-construct)
    :exec) => '([99])
)


(fact "the resulting instruction works as described"
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:scalar '(11 33) :image '(22) :rgb '(-99)})
        foo-maker)
      :foo-construct)
    :exec) => '({:A 33, :B 22, :C -99, :D 11})
  (get-stack
    (i/execute-instruction
      (i/register-instruction
        (m/basic-interpreter :stacks
          {:scalar '(11 33) :image '(22) :rgb '(-99)})
        foo-maker)
      :foo-construct)
    :scalar) => '()
)




(fact "the order of parts produced is in Push ass-backwards argument order"
  (get-stack
    (i/execute-instruction
      (push/interpreter
        :stacks {:scalar '(11 33) :boolean '(true true)})
      :interval-construct)
    :exec) => (list (iv/make-open-interval 33 11 ))
)


;;;; manifest


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


(fact "construct-instruction raises an exception if there is no :manifest or :builder value for a given type"
  (construct-instruction push.type.module.print/print-module) =>
    (throws #"cannot be constructed")
  (parts-instruction push.type.module.print/print-module) =>
    (throws #"cannot be constructed"))
