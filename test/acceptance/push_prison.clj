(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [clojure.string :as s]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [push.core :as push]
          )
  (:use [push.type.definitions.complex]
        [push.type.definitions.interval]
        [push.type.definitions.quoted])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter
        :config {:step-limit 20000
                 :lenient true
                 :max-collection-size 32768}
        :bindings (:bindings prisoner)
        :program (:program prisoner))
      reset-interpreter))


(defn run-prisoner-lazily
  [prisoner]
  (push/run
    (overloaded-interpreter prisoner)
    (:program prisoner)
    20000
    :bindings (:bindings prisoner)
    ))


(defn step-through-prisoner
  [prisoner]
  (let [interpreter (overloaded-interpreter prisoner)]
    (try
      (do
        (println (str "\n\nstepping:"
                      (pr-str (:program interpreter))
                      "\nwith inputs: "
                      (pr-str (:bindings interpreter))))
        (loop [s interpreter]
          (if (is-done? s)
            (println "DONE")
            (recur (do
              (println (str "\n>>> " (:counter s)
                            "\nstacks (pts): " (fix/count-collection-points (:stacks s))
                            "\nbindings (pts): " (fix/count-collection-points (:bindings s))
                            "\nall items in :log (sum of chars): "
                            (apply (juxt max +')
                              (into '(0)
                                (map
                                  #(count (str (:item %)))
                                  (u/get-stack s :log))))
                            ; "\n items on :exec " (u/get-stack s :exec)
                            ; "\n>>> ATTEMPTING " (pr-str (first (u/get-stack s :exec)))
                            ; "\n items on :OUTPUT " (get-in s [:bindings :OUTPUT] '())
                            ; "\n items on :scalar " (u/get-stack s :scalar)
                            ; "\n items on :return " (u/get-stack s :return)
                            ; "\n"
                            ; (pr-str (u/peek-at-stack s :log))
                            ))
              (step s))))))
      (catch Exception e (do
                            (println
                              (str "caught exception: "
                                    (.getMessage e)
                                     " running \n"
                                     (pr-str (:program interpreter)) "\n"
                                     (pr-str (:bindings interpreter))))
                            (throw (Exception. (.getMessage e))))))))


(def edn-readers
  {'push.type.definitions.interval.Interval map->Interval
   'push.type.definitions.complex.Complex map->Complex
   'push.type.definitions.quoted.QuotedCode map->QuotedCode
    })

(def prisoners
  [
    (edn/read-string {:readers edn-readers}
                     (slurp "test/acceptance/prisoners/e445321c-22bf-45b2-acce-3aac6acffe61.txt"))
  ])


(fact "no exceptions are raised when I step through any of these problematic programs"
  :debug :acceptance
  (map step-through-prisoner prisoners) =not=> (throws))

(fact "no exceptions are raised when I run (lazily) any of these problematic programs"
  :debug :acceptance
  (map run-prisoner-lazily prisoners) =not=> (throws))
