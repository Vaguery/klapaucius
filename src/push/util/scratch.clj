(ns push.util.scratch)

(defn scratch-read
  "returns whatever is stored under the named key in the Interpreter's :scratch map"
  [interpreter key]
  (get-in interpreter [:scratch key]))


(defn scratch-write
  "stores whatever if passed in under the named key in the Interpreter's scratch map"
  [interpreter key item]
  (assoc-in interpreter [:scratch key] item))


(defn scratch-forget
  "sets the value indicated in the Interpreter's :scratch map to nil"
  [interpreter key]
  (update-in interpreter [:scratch] dissoc key))


(defn scratch-replace
  "convenience function that sets the whole :scratch map for an Interpreter"
  [interpreter new-map]
  (assoc interpreter :scratch new-map))


(defn scratch-save-arg
  "appends an item to the :ARGS collection in the Interpreter's :scratch map"
  [interpreter item]
  (update-in interpreter [:scratch :ARGS] conj item))


(defn scratch-ARGS
  "returns the :ARGS collection from the Interpreter's scratch map"
  [interpreter]
  (get-in interpreter [:scratch :ARGS] '()))
