(ns push.type.definitions.span)


;; Span records


(defrecord Span [start end start-open? end-open?])



(defn span?
  "a type checker that returns true if the argument is a Span record"
  [item]
  (instance? push.type.definitions.span.Span item))



(defn make-span
  "Takes `:start` and `:end` scalar numeric arguments and by default returns a closed `Span` record. Optionally takes `:start-open?` and `:end-open?` boolean keyword arguments, which can create a partially or fully open span."
  [start end & {:keys [start-open? end-open?] :or {start-open? false end-open? false}}]
  (->Span start end start-open? end-open?)
  )



(defn make-open-span
  "Takes `:start` and `:end` scalar numeric arguments and returns a doubly-open  `Span` record."
  [start end]
  (->Span start end true true))



(defn start-closed?
  "Takes a Span record, and returns true if its `:start-open?` is false, and vice versa"
  [span]
  (not (:start-open? span)))



(defn end-closed?
  "Takes a Span record, and returns true if its `:end-open?` is false, and vice versa"
  [span]
  (not (:end-open? span)))



(defn span-reverse
  "Takes a span, and reverses `:start` and `:end` and both `open?` flags"
  [span]
  (make-span (:end span) (:start span)
             :start-open? (:end-open? span)
             :end-open? (:start-open? span)))



(defn span-coincide?
  "Takes two spans, and returns `true` if they are identical, or if the reverse of the second is identical to the first"
  [span1 span2]
  (or (= span1 span2) (= span1 (span-reverse span2))))



(defn span-empty?
  "Takes a Span and returns `true` if it has identical `:start` and `:end`, and either is open"
  [span]
  (and (= (:start span) (:end span))
       (or (:start-open? span) (:end-open? span))))



(defn span-orientation
  "Takes a Span and returns 1 if the `:start < :end`, -1 if the `:start > :end`, 0 if `:start = :end`."
  [span]
  (- (compare (:start span) (:end span))))



(defn span-include?
  "Takes a Span record and a scalar, and returns true if the scalar falls strictly within the Span"
  [span n]
  (let [s   (:start span)
        e   (:end span)
        so? (:start-open? span)
        eo? (:end-open? span)]
    (cond
      (and (= s n) so?) false
      (and (= e n) eo?) false
      (and (not (span-empty? span)) (= s e n)) true
      :else (not= (compare s n) (compare e n)))))



(defn contains-span-end?
  "Takes two spans. Returns `true` if either end of the second one falls strictly within the first, whether or not it is open."
  [span1 span2]
  (let [s1       (:start span1)
        e1       (:end span1)
        s1open   (make-open-span s1 e1)
        s1closed (make-span s1 e1)
        s2       (:start span2)
        e2       (:end span2)]
    (or
      (if (:start-open? span2)
        (span-include? s1open s2)
        (span-include? span1 s2))
      (if (:end-open? span2)
        (span-include? s1open e2)
        (span-include? span1 e2)))))




(defn span-surrounds?
  "Takes two spans, and returns `true` if the first one completely surrounds the second one; that is if both ends fall strictly within the first span."
  [span1 span2]
  (let [span1closed (make-span (:start span1) (:end span1))
        s2   (:start span2)
        e2   (:end span2)]
    (and (if (:start-open? span2)
            (span-include? span1closed s2)
            (span-include? span1 s2))
         (if (:end-open? span2)
            (span-include? span1closed s2)
            (span-include? span1 e2))
         )))



(defn span-overlap?
  "Takes two spans, and returns `true` if they strictly overlap (taking into account openness of ends)."
  [span1 span2]
  (let [s1 (:start span1)
        e1 (:end span1)
        s1open (make-open-span s1 e1)
        s2 (:start span2)
        e2 (:end span2)
        s2open (make-open-span s2 e2)]
    (cond
      (span-empty? span1) false
      (span-empty? span2) false
      (and (span-coincide? s1open s2open)) true
      (contains-span-end? span1 span2) true
      (contains-span-end? span2 span1) true
      (span-coincide? span1 span2) true
      :else false
      )))


