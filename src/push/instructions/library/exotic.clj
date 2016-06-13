(ns push.instructions.library.exotic
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.exotics :as x]
            ))


(def integer-totalistic3
  (core/build-instruction
    integer-totalistic3
    "`:integer-totalistic3` pops the top `:scalar`. It is turned into an integer using `(bigint x)`. Then each digit is replaced by the sum of its current value and the two neighbors to the right, modulo 10, wrapping cyclically around the number."
    :tags #{:numeric :exotic}
    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg] #(x/rewrite-digits (bigint %1) 3) :as :result)
    (d/push-onto :scalar :result)))




(def exotic-module
  ( ->  (t/make-module  :exotic
                        :attributes #{:exotic})
        (t/attach-instruction , integer-totalistic3)
        ))