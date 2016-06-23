(ns push.type.definitions.snapshot)


;; Snapshot records


(defrecord Snapshot [stacks bindings config])



(defn snapshot
  "Constructs a Snapshot record from an interpreter, keeping all its stacks, bindings and config. NOTE it does not clear any state."
  [interpreter]
  (->Snapshot (:stacks interpreter)
              (:bindings interpreter)
              (:config interpreter)))



(defn snapshot?
  "recognizes a Push `:snapshot` item"
  [item]
  (instance? push.type.definitions.snapshot.Snapshot item))
