(ns design-spikes.generators-spike
  (:use midje.sweet))


(defn n-digitize
  [i n]
  (format (str "%0" n "d") i))


(defn wrap
  [string window]
  (concat string (subs string 0 (dec window))))


(defn cycle-triples
  [string window]
  (partition window 1 string))


(defn add-em
  [chars]
  (mod (apply + (map #(- (int %) 48) chars)) 10))


(defn step
  [string window]
  (apply str (map add-em (cycle-triples (wrap string window) window))))


; (println (for [x (range 2881291 2881300)]
;   (str (format (str "%0" 7 "d") x) ": " (count (set (take 10000 (iterate #(step % 3) (n-digitize x 7))))))
;   ))
  
