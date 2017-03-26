(ns acceptance.new-stress-tests
  (:require [push.core :as push]
            [com.climate.claypoole :as cp]
            [com.climate.claypoole.lazy :as lazy]
            [acceptance.util :as util]
            [push.util.code-wrangling :as fix]
            )
  (:use midje.sweet)
  )


;;;; setup

(def my-interpreter (push/interpreter))
(def cohort-size 10000)
(def program-size 1000)
(def erc-scale 10)
(def erc-prob 3/5)


;;;;

(defn run-program
  [interpreter program bindings]
  (push/run
    interpreter
    program
    20000
    :bindings bindings
    :config {:step-limit 20000 :lenient true :max-collection-size 138072}))


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

(def sample-programs
  (map-indexed
    (fn [idx p] [idx p])
    (take
      cohort-size
      (repeatedly
        #(util/some-program
          program-size
          erc-scale
          erc-prob
          my-interpreter)))))


(def sample-bindings
  (assoc
    (util/some-bindings 10 erc-scale erc-prob my-interpreter)
    :OUTPUT nil))



(defn spit-prisoner-file
  [program bindings exception-message]
  (println "caught exception: ")
  (println (str exception-message))
  (spit
    (str "test/acceptance/prisoners/"
         (.toString (java.util.UUID/randomUUID))
         ".txt")
    (pr-str
      { :error exception-message
        :program program
        :bindings bindings}
        )))


(defn launch-some-workers
  [interpreter bindings numbered-programs]
  (doall
    (lazy/upmap 32
      #(try
        (.write *out*
          (str "\n\n"
            (first %) ": "
            (interpreter-details
              (run-program interpreter (second %) bindings))))
        (catch Exception e
          (.write *out* (str "failure at " (first %)))
          (spit-prisoner-file (second %) bindings (.getMessage e))
          ;(throw (Exception. (.getMessage e))))))
          ))
      numbered-programs)
      ))


(fact "run some workers in parallel"
  :danger :parallel
  (launch-some-workers
    my-interpreter
    sample-bindings
    sample-programs) =not=> (throws))
