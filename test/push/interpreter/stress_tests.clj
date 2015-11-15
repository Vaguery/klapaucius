(ns push.interpreter.stress-tests
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.types.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:require [clojure.string :as s])
  (:use [push.interpreter.core])
  )


(defn all-instructions
  [interpreter]
  (keys (:instructions interpreter)))


;; some random code generators


(defn random-integer
  ([] (rand-int 1000000000))
  ([range] (rand-int range)))


(defn random-boolean
  []
  (> 0.5 (rand)))


(defn random-float
  ([] (random-float 100000))
  ([r] (/ (double (random-integer r)) 256)))


(defn random-char
  [] (char (random-integer 1000)))


(defn random-string
  [] (str (s/join (repeatedly (inc (random-integer 20)) #(random-char)))))


(defn any-input
  [interpreter]
  (rand-nth (keys (:inputs interpreter))))


(defn any-instruction
  [interpreter]
  (rand-nth (all-instructions interpreter)))


(defn bunch-a-junk
  [interpreter how-much-junk]
  (repeatedly how-much-junk #(condp = (rand-int 10)
                                     0 (random-integer)
                                     1 (random-float)
                                     2 (random-boolean)
                                     3 (any-input interpreter)
                                     4 (random-char)
                                     5 (random-string)
                                     (any-instruction interpreter))))


(defn random-program-interpreter
  [i len]
  (let [some-junk (into [] (remove nil? (bunch-a-junk (make-classic-interpreter) i)))
        interpreter (make-classic-interpreter 
                      :config {:step-limit 1000}
                      :inputs some-junk)]
    (assoc interpreter :program (into [] (bunch-a-junk interpreter len)))))



; (fact "I can create 10000 random programs without an exception"
;   :slow :acceptance 
;   (count (repeatedly 10000 #(random-program-interpreter 10 1000))) => 10000)


(fact "I can create and run 1000 random programs without an exception"
  :slow :acceptance
  (dotimes [n 1000] 
    (let [rando (reset-interpreter (random-program-interpreter 10 200))] 
      (try
        (loop [s rando]
          (if (is-done? s)
            (println (str n "  " (:counter s)))
            (recur (do 
              ;;(println (u/peek-at-stack s :log)) 
              (step s)))))
        (catch Exception e (println 
                              (str "caught exception: " 
                                 (.getMessage e)
                                 " running "
                                 (pr-str (:program rando))
                                 )))))))
