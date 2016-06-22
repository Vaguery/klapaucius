(ns push.type.definitions.generator)



(defrecord Generator [state step-function origin])



(defn make-generator
  "Takes an initial state and a generator function that transforms the state into its next value. Stores the initial state in the :origin field so it can reset and detect loops. The function should have arity 1 and return a new state value. If the state turns out to be nil, no record is built and nil is returned instead."
  ([state step-function origin]
    (if (nil? state)
      nil
      (->Generator state step-function origin)))
  ([state step-function]
    (make-generator state step-function state)))



(defn generator?
  "Returns `true` if the item is a `:generator`, and `false` otherwise."
  [item]
  (= (type item) push.type.definitions.generator.Generator))



(defn step-generator
  "Takes a generator, and applies its step-function to its state. Returns a list containing the new result, and the updated generator."
  [g]
  (let [gen       (:step-function g) 
        new-value (apply gen (list (:state g)))]
    (if (nil? new-value) nil (make-generator new-value gen (:origin g)))))

