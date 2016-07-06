(ns push.type.definitions.interval)


;; Interval records


(defrecord Interval [min max min-open? max-open?])


(defn interval?
  "a type checker that returns true if the argument is an Interval record"
  [item]
  (instance? push.type.definitions.interval.Interval item))



(defn make-interval
  "Takes `:min` and `:max` scalar numeric arguments and by default returns a closed `Interval` record. Optionally takes `:min-open?` and `:max-open?` boolean keyword arguments, which can create a partially or fully open interval."
  [low high & {:keys [min-open? max-open?] :or {min-open? false max-open? false}}]
  (->Interval (min low high) (max low high) min-open? max-open?)
  )



(defn make-open-interval
  "Takes `:min` and `:max` scalar numeric arguments and returns a doubly-open `Interval` record."
  [low high]
  (->Interval low high true true))



(defn min-closed?
  "Takes an Interval record, and returns true if its `:min-open?` is false, and vice versa"
  [interval]
  (not (:min-open? interval)))



(defn max-closed?
  "Takes an Interval record, and returns true if its `:max-open?` is false, and vice versa"
  [interval]
  (not (:max-open? interval)))



(defn interval-empty?
  "Takes an Interval and returns `true` if it has identical `:min` and `:max`, and either is open"
  [interval]
  (and (= (:min interval) (:max interval))
       (or (:min-open? interval) (:max-open? interval))))



(defn interval-include?
  "Takes an Interval record and a scalar, and returns true if the scalar falls strictly within the Interval"
  [interval n]
  (let [s   (:min interval)
        e   (:max interval)
        so? (:min-open? interval)
        eo? (:max-open? interval)]
    (cond
      (and (= s n) so?) false
      (and (= e n) eo?) false
      (and (not (interval-empty? interval)) (= s e n)) true
      :else (not= (compare s n) (compare e n)))))



(defn contains-interval-end?
  "Takes two intervals. Returns `true` if either end of the second one falls _strictly_ within the first, whether or not it is open."
  [interval1 interval2]
  (let [s1       (:min interval1)
        e1       (:max interval1)
        s1open   (make-open-interval s1 e1)
        s1closed (make-interval s1 e1)
        s2       (:min interval2)
        e2       (:max interval2)]
    (or
      (if (:min-open? interval2)
        (interval-include? s1open s2)
        (interval-include? interval1 s2))
      (if (:max-open? interval2)
        (interval-include? s1open e2)
        (interval-include? interval1 e2)))))




(defn interval-subset?
  "Takes two intervals, and returns `true` if the second one is a subset of the first."
  [interval1 interval2]
  (let [i1closed (make-interval (:min interval1) (:max interval1))
        s2   (:min interval2)
        e2   (:max interval2)]
    (and (if (:min-open? interval2)
            (interval-include? i1closed s2)
            (interval-include? interval1 s2))
         (if (:max-open? interval2)
            (interval-include? i1closed s2)
            (interval-include? interval1 e2))
         )))



(defn interval-overlap?
  "Takes two intervals, and returns `true` if they strictly overlap (taking into account openness of ends)."
  [interval1 interval2]
  (let [s1 (:min interval1)
        e1 (:max interval1)
        i1open (make-open-interval s1 e1)
        s2 (:min interval2)
        e2 (:max interval2)
        i2open (make-open-interval s2 e2)]
    (cond
      (interval-empty? interval1) false
      (interval-empty? interval2) false
      (= i1open i2open) true
      (contains-interval-end? interval1 interval2) true
      (contains-interval-end? interval2 interval1) true
      (= interval1 interval2) true
      :else false
      )))


