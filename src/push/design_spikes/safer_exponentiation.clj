(ns push.design-spikes.safer-exponentiation
  (:use midje.sweet)
  (:use clojure.pprint)
  (:require [clojure.math.numeric-tower :as nt])
  )




; (def b [1/3 2/3 4/3 8/3 16/3 32/3 64/3])
; (def e [0 1/3 2/3 4/3 8/3 16/3 32/3 64/3 100/3 500/3 1000/3 2000/3])


; ;; log_b(m^n) = n * log_b(m)
; ;;
; ;; thus, log_2(b^e) = e * log_2(b)


; (def digits
;   (for [y e
;         x b]
;     (* 2 y (Math/log1p x))))


; (println (count e) digits)



; (defn expt-timing
;   [base exp]
;   (Double/parseDouble
;       (nth 
;         (clojure.string/split
;           (with-out-str (time (dotimes [n 1000] (nt/expt base exp))))
;           #" ")
;         2)))


; (def times
;   (for [y e
;         x b]
;     (expt-timing x y)))

; (println (count e) times)


