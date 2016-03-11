(ns acceptance.stress-tests
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
            [push.util.stack-manipulation :as u]
            [clojure.string :as s]
            [demo.examples.plane-geometry.definitions :as geom]
            [push.instructions.extra.stack-combinators :as combo])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn all-instructions
  [interpreter]
  (keys (:instructions interpreter)))


;; some random code generators


(defn random-integer
  ([] (rand-int 1000000000))
  ([range] (rand-int range)))


(defn random-integers
  ([] (random-integers 1000000000))
  ([range] (into [] (repeatedly (random-integer 10) #(random-integer range)))))



(defn random-boolean
  []
  (> 0.5 (rand)))


(defn random-booleans
  [] (into [] (repeatedly (random-integer 10) #(random-boolean))))



(defn random-float
  ([] (random-float 100000))
  ([r] (/ (double (random-integer r)) 256)))


(defn random-floats
  ([] (random-floats 100000))
  ([range] (into [] (repeatedly (random-integer 10) #(random-float range)))))



(defn random-char
  [] (char (+ 8 (random-integer 200))))


(defn random-chars
  [] (into [] (repeatedly (random-integer 10) #(random-char))))


(defn random-string
  [] (str (s/join (repeatedly (inc (random-integer 20)) #(random-char)))))


(defn random-strings
  [] (into [] (repeatedly (random-integer 10) #(random-string))))


(defn random-point
  [] (geom/make-point (random-float) (random-integer)))


(defn random-circle
  [] (geom/make-circle-from-xyxy
        (random-float) 
        (random-integer) 
        (random-float) 
        (random-integer)))


(defn random-line
  [] (geom/make-line-from-xyxy
        (random-float) 
        (random-integer) 
        (random-float) 
        (random-integer)))


(defn any-input
  [interpreter]
  (rand-nth (keys (:bindings interpreter))))


(defn any-instruction
  [interpreter]
  (rand-nth (all-instructions interpreter)))


(defn bunch-a-junk
  [interpreter how-much-junk]
  (remove nil? (repeatedly how-much-junk
                                  #(condp = (rand-int 30)
                                     0 (random-integer)
                                     1 (random-float)
                                     2 (random-boolean)
                                     3 (any-input interpreter)
                                     4 (random-char)
                                     5 (random-string)
                                     6 (into '() (bunch-a-junk interpreter 5))
                                     7 (random-integers 5000)
                                     8 (random-booleans)
                                     9 (random-floats 40)
                                     10  (random-chars)
                                     11 (random-strings)
                                     ; 12 (random-point)
                                     ; 13 (random-circle)
                                     ; 14 (random-line)
                                     12 (into #{} (bunch-a-junk interpreter 8))

                                     (any-instruction interpreter)))))


(defn timeout [timeout-ms callback]
 (let [fut (future (callback))
       ret (deref fut timeout-ms ::timed-out)]
   (when (= ret ::timed-out)
     (do (future-cancel fut) (throw (Exception. "timed out"))))
   ret))

;; (timeout 100 #(do (Thread/sleep 1000) (println "I finished")))


(def geometry-types
  (map combo/extend-combinators
    [geom/precise-circle geom/precise-line geom/precise-point]))


(defn overloaded-interpreter
  [& args]
  (-> (apply make-everything-interpreter args)
      (register-types geometry-types)))


(defn random-program-interpreter
  [i len]
  (let [some-junk (into [] (remove nil? (bunch-a-junk (make-everything-interpreter) i)))
        interpreter (overloaded-interpreter 
                      :config {:step-limit 50000}
                      :bindings some-junk)]
    (assoc interpreter :program (into [] (bunch-a-junk interpreter len)))))



(defn run-with-wordy-try-block
  [interpreter]
  (try
    (do
      (println (str (:counter (run-n interpreter 10000)))))
    (catch Exception e 
      (do 
        (println
          (str "caught exception: " 
             (.getMessage e)
             " running "
             (pr-str (:program interpreter)) "\n" (pr-str (:bindings interpreter))))
          (throw (Exception. (.getMessage e)))))))


;; actual tests; they will run hot!

(future-fact "I can create 10000 random programs without an exception"
  :slow :acceptance 
  (do (println "creating and discarding 10000 random programs")
      (count (repeatedly 10000 #(random-program-interpreter 10 100)))) => 10000)


;; the following monstrosity is an "acceptance test" for hand-running, at the moment.
;; it's intended to give a bit more info about the inevitable bugs that
;; only appear when random programs are executed by an interpreter, in a
;; bit more of a complex context; by the time you read this, it might be
;; commented out. If you want to run it, be warned it will spew all kinds
;; of literally random text to the STDOUT stream.
(fact "I can create and step through 10000 random programs without an exception"
  :slow :acceptance
  (do (println "creating and running 10000 random programs")
      (dotimes [n 10000] 
        (let [rando (assoc-in (reset-interpreter (random-program-interpreter 10 200))
                      [:config :step-limit] 3000)] 
          (try
            (timeout 120000 #(do
              ; (println (str "\n\n" n " : " (pr-str (:program rando)) "\n" (pr-str (:bindings rando))))
              (loop [s rando]
                (if (is-done? s)
                  (println (str n
                                "  "
                                (:counter s) 
                                (reduce-kv
                                  (fn [line k v]
                                    (str line "," (count (get-in s [:stacks k]))))
                                  ""
                                  (:stacks s))
                                (reduce-kv
                                  (fn [line k v]
                                    (str line "," (count (get-in s [:bindings k]))))
                                  "**"
                                  (:bindings s))

                            ))
                  (recur (do 
                    ; (println (u/peek-at-stack s :log)) 
                    (step s)))))))
              (catch Exception e (do 
                                    (println 
                                      (str "caught exception: " 
                                         (.getMessage e)
                                         " running "
                                         (pr-str (:program rando)) "\n" (pr-str (:bindings rando))))
                                      (throw (Exception. (.getMessage e))))))))) =not=> (throws))




;; the following monstrosity is an "acceptance test" for hand-running, at the moment.
;; it's intended to give a bit more info about the inevitable bugs that
;; only appear when random programs are executed by an interpreter, in a
;; bit more of a complex context; by the time you read this, it might be
;; commented out. If you want to run it, be warned it will spew all kinds
;; of literally random text to the STDOUT stream.
(future-fact "I can create & run 10000 large random programs for up to 5000 steps each without an exception"
  :slow :acceptance
  (do (println "creating and running 10000 interpreters in parallel")
    (let [my-interpreters 
      (repeatedly 10000 #(reset-interpreter (random-program-interpreter 10 1000))) ]
        (doall (pmap run-with-wordy-try-block my-interpreters))
      )) =not=> (throws))
