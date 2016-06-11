(ns push.util.exotics)


(defn char-to-digits
  [c]
  (- (long c) 48))



(defn extend-short-list
  [items target]
  (loop [extended items
         n 0]
    (if (>= (count extended) target)
      extended
      (recur (concat extended (list (nth extended n)))
             (inc n)))))



(defn rewrite-digits
  "Applies a totalistic rewrite rule to the digits of an integer value, using a given window size and preserving its sign"
  [number window]
  (let [digits    (seq (str (max number (-' number))))
        extended  (extend-short-list digits (+ (count digits) (dec window)))
        windows   (partition window 1 extended)
        sums      (map #(apply + (map char-to-digits %)) windows)
        rewrote   (apply str (map #(mod % 10) sums))
        chomped   (clojure.string/replace-first rewrote #"(^0+)" "")
        as-number (eval (read-string (if (empty? chomped) "0" chomped)))]
    (if (neg? number)
      (- as-number)
      as-number)))


;; boolean 


(defn bit-to-int [b] (if b 1 0))


(defn integer-to-truth-table
  "Takes an integer value and a (positive) number of bits to use, and returns the n-bit truth table in canonical order. If the number of bits is lower than that needed, only the minimum number are returned; no fewer than two bits will ever be returned."
  [i bits]
  (let [len       (nth (iterate (partial * 2) 1) bits)
        seed      (.toString (biginteger i) 2)
        shortfall (- len (count seed))]
  (if (and (pos? bits) ((complement neg?) i))
    (into []
      (reverse
        (map 
          {\0 false \1 true} 
          (concat 
            (repeat shortfall \0) 
            (seq seed)))))
    (throw (Exception. "integer-to-truth-table argument error")))))
