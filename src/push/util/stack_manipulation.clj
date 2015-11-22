(ns push.util.stack-manipulation)


(defn get-stack
  "A convenience function which returns the named stack from the
  interpreter"
  [interpreter stack]
  (get-in interpreter [:stacks stack]))


(defn set-stack
  "A convenience function which replaces the named stack with the
  indicated list"
  [interpreter stack new-value]
  (assoc-in interpreter [:stacks stack] new-value))


(defn clear-stack
  "Empties the named stack."
  [interpreter stack]
  (assoc-in interpreter [:stacks stack] (list)))


(defn peek-at-stack
  "Returns the top item on the named stack."
  [interpreter stack]
  (peek (get-in interpreter [:stacks stack])))


(defn merge-environment
  "Takes an Interpreter and a hash-map of stacks. Replace ALL the current Interpreter stacks with those in the hash-map, except :print, :log, :unknown and :error (which are retained from the current state)."
  [interpreter old-environment]
  (let [permanent
              {:print   (get-stack interpreter :print)
               :log     (get-stack interpreter :log)
               :error   (get-stack interpreter :error)
               :unknown (get-stack interpreter :unknown)}
        tabula-rasa 
          (reduce-kv (fn [m k v] (assoc m k '())) {} (:stacks interpreter))]
    (assoc 
      interpreter 
      :stacks 
      (merge tabula-rasa old-environment permanent))))




