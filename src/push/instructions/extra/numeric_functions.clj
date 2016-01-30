(ns push.instructions.extra.numeric-functions
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


; (def integer-uniform
;   (core/build-instruction
;     integer-uniform
;     "`:integer-uniform` pops the top `:integer` value, and pushes a uniform random integer sampled from the range [0,:int). If the integer is negative, a negative result is returned."
;     :tags #{:numeric :random}
;     (d/consume-top-of :integer :as :arg)
;     (d/calculate [:arg] #(cond
;                           (neg? %1) (- (rand-int %1))
;                           (zero? %1) 0 
;                           :else (rand-int %1)) :as :result)
;     (d/push-onto :integer :result)))




;;;;;;;;;;;;;;;;;


(def numeric-functions-module
  ( ->  (t/make-module  :numeric-functions
                        :attributes #{:numeric})

        ; (t/attach-instruction , integer-uniform)
        ))
