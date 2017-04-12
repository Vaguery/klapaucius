(ns util.trace-test
  (:require [push.core :as push]
            [push.interpreter.core :as core]
            [acceptance.util :as generators])
  (:use     [midje.sweet]
            [util.trace])
            )

(fact "yaml-from-interpreter-stacks"
  (yaml-from-interpreter-stacks
    (push/interpreter :program [1 2 :scalar-add]
                      :bindings {:x 77})) =>
  "boolean:\nbooleans:\nchar:\nchars:\ncode:\ncomplex:\ncomplexes:\nerror:\nexec:\n  - 1\n  - 2\n  - :scalar-add\ngenerator:\ninterval:\nintervals:\nlog:\nprint:\nquoted:\nref:\nrefs:\nreturn:\nscalar:\nscalars:\nset:\nsnapshot:\nstring:\nstrings:\ntagspace:\nunknown:\nvector:\nx:\n  - 77\n"
  )


(fact "I can produce a huge folder full of stack details that can be used to produce a stack trace drawing"
  :slow :trace

  (println "This is a long-running CPU hog of a test. Use the tags if you're sure you want to run it.")
  (defn fixture-interpreter
    []
    (push/interpreter
      :bindings {:x1 9 :x2 false}
      :config {:step-limit 10000}))


  (defn set-fixture-bindings
    [i]
    (assoc i :bindings (generators/some-bindings 5 10 4/5 i)))


  (defn set-fixture-program
    [i]
    (assoc i :program (generators/some-program 400 10 1/15 i)))


  (defn loaded-interpreter
    []
    (-> (fixture-interpreter)
        set-fixture-bindings
        set-fixture-program))



  (def trace-interpreter
    (let [with-stuff (loaded-interpreter)]
      (push/merge-stacks
        (core/reset-interpreter
          with-stuff)
        (generators/preloaded-stacks 10 10 2/5 with-stuff))))


  (loop [time  0
         state trace-interpreter]
    (when-not (or (> time 1000) (core/is-done? state))
      (spit (str "test/util/yamls/test-" (format "%04d" time) ".txt")
        (yaml-from-interpreter-stacks state))
      (recur (inc time)
             (core/step state))
             ))
)
