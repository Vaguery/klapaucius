(ns push.design-spikes.clojush-running
  (:use midje.sweet)
  (:use clojure.pprint)
  (:require [push.core :as push]
            [push.interpreter.core :as i]
            [push.util.legacy :as legacy]))

(def weirdo
  '(in3 in3 in2 integer_shove integer_add in1 integer_mult in3 in3 integer_mult exec_do*count (exec_s in3 exec_dup (in2 integer_mult in1) integer_mult exec_do*count (in3 exec_dup (in2 integer_mult in1) integer_pop exec_y integer_add)))
    )

(fact "I can translate it"
  (legacy/clojush->klapaucius weirdo) => 
    '[:in3 :in3 :in2 :integer-shove :integer-add :in1 :integer-multiply :in3 :in3 :integer-multiply :exec-do*count (:exec-s :in3 :exec-dup (:in2 :integer-multiply :in1) :integer-multiply :exec-do*count (:in3 :exec-dup (:in2 :integer-multiply :in1) :integer-pop :exec-y :integer-add))]
    )

(fact "I can run it and see the logs"
  (take 5
   (reverse (push/get-stack
    (push/run
      (push/interpreter)
      (legacy/clojush->klapaucius weirdo) 100 :bindings {:in1 3 :in2 4 :in3 5})
    :log))) => 
  '({:item :in3, :step 1} {:item 5, :step 2} {:item :in3, :step 3} {:item 5, :step 4} {:item :in2, :step 5}))



(defn stepper
  [bindings]
  (i/reset-interpreter
    (push/interpreter
      :program (legacy/clojush->klapaucius weirdo)
      :bindings bindings
      :config {:step-limit 500})))



; (loop [s (stepper {:in1 2 :in2 5 :in3 7})
;        i 0]
;   (println (str "\n" i ":"))
;   (println (select-keys (:stacks s) [:integer :exec :error]))
;   (if (>= i 100)
;     (println i " steps completed")
;     (recur (i/step s)
;            (inc i))))




