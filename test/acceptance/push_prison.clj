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
            [acceptance.new-stress-tests :as stress]
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
                 :max-collection-size 131072}
        :bindings (:bindings prisoner)
        :program (:program prisoner))
      reset-interpreter))


(defn standard-run
  [prisoner]
  (println (str "\n\nrunning: " (:program prisoner)))
  (stress/run-program-in-standardized-interpreter
    (push/interpreter)
    (:program prisoner)
    (:bindings prisoner)
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
              (println (str ">>> " (:counter s)
                            ; "\nstacks (pts): " (fix/count-collection-points (:stacks s))
                            ; "\nbindings (pts): " (fix/count-collection-points (:bindings s))
                            ; "\nall items in :log (sum of chars): "
                            ; (apply (juxt max +')
                            ;   (into '(0)
                            ;     (map
                            ;       #(count (str (:item %)))
                            ;       (u/get-stack s :log))))
                            ; "\n>>> ATTEMPTING " (pr-str (first (u/get-stack s :exec)))
                            (pr-str (u/peek-at-stack s :log))
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


(defn slurp-prisoner
  [filename]
  (edn/read-string {:readers edn-readers}
                   (slurp filename)))

(def prisoners
  "This reads all files in the glob 'test/acceptance/prisoners/prisoner-*' into a vector of `prisoner` maps."
  (let [prison "test/acceptance/prisoners"
        files (file-seq (clojure.java.io/file prison))]
    (reduce
      (fn [dudes f]
        (if (re-find #"prisoner-" (. f getName))
          (conj dudes (slurp-prisoner (str prison "/" (. f getName))))
          dudes))
      []
      files)
      ))


; (fact "no exceptions are raised when I step through any of these problematic programs"
;   :debug :acceptance
;   (map step-through-prisoner prisoners) =not=> (throws))

(fact "no exceptions are raised when I run (lazily) any of these problematic programs"
  :debug :acceptance
  (map standard-run prisoners) =not=> (throws))
