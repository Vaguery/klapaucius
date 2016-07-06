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
  (->Interval (min low high) (max low high) true true))



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



(defn interval-intersection
  "Takes two Interval records, and returns a new one that contains their intersection, or nil if there is none."
  [i1 i2]
  (if (interval-overlap? i1 i2)
    (let [min1 (:min i1)
          min2 (:min i2)
          max1 (:max i1)
          max2 (:max i2)
          sorted (sort-by
                    first
                    [ [min1 (:min-open? i1)]
                      [min2 (:min-open? i2)]
                      [max1 (:max-open? i1)] 
                      [max2 (:max-open? i2)]])
          new-min (nth sorted 1)
          new-max (nth sorted 2)
          ]
      (make-interval
        (first new-min)
        (first new-max)
        :min-open? (if (= min1 min2)
                       (or (:min-open? i1) (:min-open? i2))
                       (second new-min))
        :max-open? (if (= max1 max2)
                       (or (:max-open? i1) (:max-open? i2))
                       (second new-max))))
    nil))



(defn interval-snug?
  "Takes two Interval records, and returns `true` when they do not overlap, and the max of the first is the same as the :min of the second, and at least one of those ends is closed. `[2,3)` and `[3,4)` are snug. NOTE: this only checks whether i1 is snug to i2, not vice versa"
  [i1 i2]
  (and (not (interval-overlap? i1 i2))
       (and (= (:max i1) (:min i2))
       (or (max-closed? i1) (min-closed? i2)))))




(defn interval-union
  "Takes two Interval records. If they are strictly discontinuous, they are returned in the order given in a list. If they have no gap between them, the list will contain one Interval that is their union."
  [i1 i2]
  (let [min1 (:min i1)
        min2 (:min i2)
        max1 (:max i1)
        max2 (:max i2)
        sorted (sort-by
                  first
                  [ [min1 (:min-open? i1)]
                    [min2 (:min-open? i2)]
                    [max1 (:max-open? i1)] 
                    [max2 (:max-open? i2)]])
        new-min (first sorted)
        new-max (last sorted)]
    (cond
      (interval-overlap? i1 i2)
        (list
          (make-interval
            (first new-min)
            (first new-max)
            :min-open? (if (= min1 min2)
                         (and (:min-open? i1) (:min-open? i2))
                         (second new-min))
            :max-open? (if (= max1 max2)
                         (and (:max-open? i1) (:max-open? i2))
                         (second new-max))))
      (or (interval-snug? i1 i2) (interval-snug? i2 i1))
        (list
          (make-interval
            (first new-min)
            (first new-max)
            :min-open? (second new-min)
            :max-open? (second new-max)))
      :else
        (list i1 i2))))


