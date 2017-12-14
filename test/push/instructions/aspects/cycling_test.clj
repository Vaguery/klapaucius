(ns push.instructions.aspects.cycling-test
  (:require [push.interpreter.core :as i]
            [push.core :as push])
  (:use midje.sweet)
  (:use push.util.stack-manipulation)
  (:use push.type.core)
  (:use push.instructions.aspects)
  (:use push.type.item.scalar)
  (:use push.instructions.aspects.repeatable-and-cycling)
  (:use push.type.item.snapshot)
  (:use push.type.definitions.generator)
  )


;; :cycling types


(fact "`make-cycling` takes adds the :cycling attribute to a PushType record"
  (:attributes (make-cycling (make-type :thingie))) => #{:cycling})



(fact "comprehension-instruction returns an Instruction with the correct stuff"

  (let [foo-comprehension (comprehension-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '([1 2 3 4])) ) ]

    (class foo-comprehension) => push.instructions.core.Instruction

    (:needs foo-comprehension) => {:foo 1}
    ; (:products foo-comprehension) => {:generator 1}

    (:token foo-comprehension) => :foo-comprehension

    (keys (:instructions (i/register-instruction context foo-comprehension))) => (contains :foo-comprehension)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-comprehension) :foo-comprehension)
      :foo) => '()

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction context foo-comprehension) :foo-comprehension)
                          :exec))]
      (:state new-g) => '(1 (2 3 4))
      (:origin new-g) => '(1 (2 3 4))
      (:state (step-generator new-g)) => '(2 (3 4))
      (:state (step-generator
                (step-generator new-g))) => '(3 (4))
      (:state (step-generator
                (step-generator
                  (step-generator new-g)))) => '(4 ())

      ;; it eventually dies
      (step-generator
                (step-generator
                  (step-generator
                    (step-generator new-g)))) => nil
      )))



(fact "cycler-instruction returns an Instruction with the correct stuff"

  (let [foo-cycler (cycler-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '("abcd")) ) ]

    (class foo-cycler) => push.instructions.core.Instruction

    (:needs foo-cycler) => {:foo 1}
    ; (:products foo-cycler) => {:generator 1}

    (:token foo-cycler) => :foo-cycler

    (keys (:instructions (i/register-instruction context foo-cycler))) => (contains :foo-cycler)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-cycler) :foo-cycler)
      :foo) => '()

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction context foo-cycler) :foo-cycler)
                          :exec))]
      (:state new-g) => '(\a (\b \c \d \a))
      (:origin new-g) => '(\a (\b \c \d \a))
      (:state (step-generator new-g)) => '(\b (\c \d \a \b))
      (:state (step-generator
                (step-generator new-g))) => '(\c (\d \a \b \c))
      (:state (step-generator
                (step-generator
                  (step-generator new-g)))) => '(\d (\a \b \c \d))
      )

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction
                              (set-stack context :foo '([])) foo-cycler) :foo-cycler)
                          :exec))]
      new-g => nil
      )

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction
                              (set-stack context :foo '(88)) foo-cycler) :foo-cycler)
                          :exec))]
      new-g => nil
      )
    ))



(fact "sampler-instruction returns an Instruction with the correct stuff"

  (let [foo-sampler (sampler-instruction (make-type :foo))
        context (-> (push.core/interpreter)
                    (assoc-in , [:stacks :generator]  '())
                    (assoc-in , [:stacks :foo]  '("abcd")) ) ]

    (class foo-sampler) => push.instructions.core.Instruction

    (:needs foo-sampler) => {:foo 1}
    ; (:products foo-sampler) => {:generator 1}

    (:token foo-sampler) => :foo-sampler

    (keys (:instructions (i/register-instruction context foo-sampler))) => (contains :foo-sampler)

    (i/contains-at-least? context :foo 1) => true

    (get-stack
      (i/execute-instruction (i/register-instruction context foo-sampler) :foo-sampler)
      :foo) => '()


    (fact
      (against-background (rand-nth anything) =streams=> (cycle [\X \Y \Z \W]))
      (let [new-g (first (get-stack
                            (i/execute-instruction
                              (i/register-instruction context foo-sampler) :foo-sampler)
                            :exec))]
        (:state new-g) => \X
        (:origin new-g) => \Y
        (:state (step-generator new-g)) => \Z

        (:state (step-generator
                  (step-generator new-g))) => \X
        (:state (step-generator
                  (step-generator
                    (step-generator new-g)))) => \W
        ))

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction
                              (set-stack context :foo '([])) foo-sampler) :foo-sampler)
                          :exec))]
      new-g => nil
      )

    (let [new-g (first (get-stack
                          (i/execute-instruction
                            (i/register-instruction
                              (set-stack context :foo '(88)) foo-sampler) :foo-sampler)
                          :exec))]
      new-g => nil
      )
    ))
