(ns push.instructions.aspects.buildable
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]))


(defn component-list
  "Takes a pushtype, reads its `:parts`, and produces a human-readable string from that."
  [pushtype]
  (let [needs (:parts pushtype)]
    (frequencies (vals needs))
    ))


(defn collect-components
  "Takes a pushtype, and returns a sequence of `consume-top-of` DSL commands that will pop the required items in scratch variables with the same names as the type's keys"
  [pushtype]
  (reduce-kv
    (fn [steps k v]
      (conj steps `(consume-top-of ~v :as ~k)))
    '()
    (:parts pushtype)))


(defn invoke-builder
  "Takes a pushtype, and returns a list containing one `calculate` and one `push-onto` DSL command, which assume that the components for the type's `:builder` are already stored in the scratch variables with the same name"
  [pushtype]
  (let [typename     (:name pushtype)
        pieces       (:parts pushtype)
        argvector    (into [] (keys pieces))
        argsymbols   (reduce
                        (fn [s i] (conj s (symbol (name i))))
                        [] 
                        argvector)
        maker        (:builder pushtype)]
    (list
      `(calculate ~argvector (fn ~argsymbols (~maker ~@argsymbols)) :as :result)
      `(push-onto ~typename :result)
      )
    ))


(defn make-instruction
  "returns a new x-make instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-make")
        parts (:parts pushtype)
        partstring (component-list pushtype)]
    (eval 
      (concat
        (list
          `build-instruction
          instruction-name
          (str "`:" instruction-name "` constructs a new `" typename "` item from its components parts, " (component-list pushtype) "."))
        (collect-components pushtype)
        (invoke-builder pushtype)
      ))))



