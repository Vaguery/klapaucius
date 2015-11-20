(ns push.instructions.standard.vectorized_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (:use [push.types.standard.vectorized])
  )


(def foo-type (t/make-type :foo
                           :recognizer number?
                           :attributes #{:foo}))

(def foos-type (build-vectorized-type foo-type))


(fact "foos-type knows some instructions"
  (keys (:instructions foos-type)) =>
    (contains [:foos-concat :foos-dup] :in-any-order :gaps-ok))



(tabular
  (fact ""
    (register-type-and-check-instruction
        ?set-stack ?items foos-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items                 ?instruction      ?get-stack   ?expected
    :foos       '([3 4] [1 2])         :foos-concat      :foos       '([1 2 3 4])
    )
