(ns push.util.exotics)


(defn char-to-digits
  [c]
  (- (int c) 48))



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
  (let [digits    (seq (str (max number (- number))))
        extended  (extend-short-list digits (+ (count digits) (dec window)))
        windows   (partition window 1 extended)
        sums      (map #(apply + (map char-to-digits %)) windows)
        rewrote   (apply str (map #(mod % 10) sums))
        chomped   (clojure.string/replace-first rewrote #"(^0+)" "")
        as-number (eval (read-string (if (empty? chomped) "0" chomped)))]
    (if (neg? number)
      (- as-number)
      as-number)))
