(ns acceptance.new-stress-tests
  (:require [push.core :as push]
            [com.climate.claypoole :as cp]
            [com.climate.claypoole.lazy :as lazy]
            [acceptance.util :as util]
            [push.util.code-wrangling :as fix]
            [dire.core :refer [with-handler!]]
            )
  (:use midje.sweet)
  )


;;;; setup

(def my-interpreter (push/interpreter))
(def cohort-size 100)
(def program-size 1000)
(def erc-scale 10)
(def erc-prob 3/5)


;;;;

(defn run-program-in-standardized-interpreter
  [interpreter program bindings]
    (push/run
      interpreter
      program
      20000
      :bindings bindings
      :config {:step-limit 20000
               :lenient true
               :max-collection-size 138072} ;138072
               ))

(defn spit-prisoner-file
  [program bindings exception-message]
  (println "caught exception: ")
  (println (str exception-message))
  (spit
    (str "test/acceptance/prisoners/prisoner-"
         (.toString (java.util.UUID/randomUUID))
         ".txt")
    (pr-str
      { :error exception-message
        :program program
        :bindings bindings}
        )))


(with-handler! #'run-program-in-standardized-interpreter
  "If an interpreter raises an exception, dump a new prisoner file."
  [java.lang.StackOverflowError, java.lang.NullPointerException]
  (fn [e & args]
    (do
      (println
        (str "\n\nSTACK OVERFLOW >>>>>>> " args))
      (println "\n\n saving prisoner: ")
      (spit-prisoner-file
        (first args)
        (second args)
        (.getMessage e)))
        ))


(defn interpreter-details
  [i]
  { :program-size
      (str
        (count (:program i))
        " (" (fix/count-collection-points (:program i)) ")")
    :steps (:counter i)
    :errors (count (push/get-stack i :error))
    :argument-errors
      (count
        (filter
          #(.contains (:item %)
                      "missing arguments")
                      (push/get-stack i :error)))
    :stack-points
      (reduce-kv
        (fn [counts key value]
          (assoc counts key
            (str (count value) ))) ;" (" (fix/count-collection-points value) ")"
        {}
        (:stacks i))
    :binding-points
      (reduce-kv
        (fn [counts key value]
          (assoc counts key
            (str (count value) ))) ;" (" (fix/count-collection-points value) ")"
        {}
        (:bindings i))

  })


;; setup for stress test

(defn sample-program
  [i]
  [i (util/some-program
      program-size
      erc-scale
      erc-prob
      my-interpreter)])



(def sample-bindings
  (assoc
    (util/some-bindings 10 erc-scale erc-prob my-interpreter)
    :OUTPUT nil))


(defn launch-some-workers
  [interpreter bindings how-many]
  (cp/with-shutdown! [net-pool (cp/threadpool 100)]
    (doall
      (lazy/upmap net-pool
        #(.write *out*
          (str "\n\n"
            (first %) ": "
            (interpreter-details
              (run-program-in-standardized-interpreter
                interpreter
                (second %)
                bindings))))
        (map sample-program (range how-many))
        ))))


(fact "run some workers in parallel"
  :danger :parallel
  (launch-some-workers
    my-interpreter
    sample-bindings
    10000) =not=> (throws))
