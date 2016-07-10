(ns push.util.stack-manipulation)


(defn get-stack
  "A convenience function which returns the named stack from the
  interpreter. Returns an empty list if no key is present."
  [interpreter stack]
  (get-in interpreter [:stacks stack] '()))


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


(defn merge-snapshot
  "Takes an Interpreter and a `:snapshot` item. Replace ALL the current Interpreter stacks with those in the hash-map, except :print, :log, :unknown and :error (which are retained from the current state). The `:bindings` and `:config` from the `:snapshot` are also copied over."
  [interpreter snapshot]
  (let [retained-stacks
              {:print   (get-stack interpreter :print)
               :log     (get-stack interpreter :log)
               :error   (get-stack interpreter :error)
               :unknown (get-stack interpreter :unknown)}
        old-bindings (:bindings snapshot)
        old-config   (:config snapshot)
        old-stacks   (:stacks snapshot)
        new-stacks   (merge (:stacks interpreter) old-stacks)
        new-stacks   (merge new-stacks retained-stacks)]
    (-> interpreter
      (assoc , :stacks new-stacks)
      (assoc , :bindings old-bindings)
      (assoc , :config old-config))))




