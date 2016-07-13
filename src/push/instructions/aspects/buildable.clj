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
        argsymbols   (reduce (fn [s i] (conj s (symbol (name i))))
                        [] 
                        argvector)
        maker        (:builder pushtype)]
    (list
      `(calculate 
          ~argvector 
          (fn ~argsymbols (apply ~maker ~argsymbols)) 
          :as :result)
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
    (if (or (nil? parts) (nil? (:builder pushtype)))
      (throw (Exception. "a make instruction cannot be constructed for a type lacking a :parts or :builder value"))
      (eval 
        (concat
          (list
            `build-instruction
            instruction-name
            (str "`:" instruction-name "` constructs a new `" typename "` item from its component parts, " (component-list pushtype) "."))
          (collect-components pushtype)
          (invoke-builder pushtype)
        )))))



(defn invoke-breaker
  "Takes a pushtype, and returns a list containing one `calculate` and one `push-onto` DSL command, which constructs a code block from the named keys in the type's `:parts` field. The pushtype being broken should _probably_ be a Clojure `record`."
  [pushtype]
  (let [pieces       (:parts pushtype)
        argvector    (into [] (reverse (keys pieces)))]
    (list
      `(calculate [:arg] #(map (into {} %1) ~argvector) :as :continuation)
      `(push-onto :exec :continuation))
    ))



(defn parts-instruction
  "returns a new x-parts instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-parts")
        parts (:parts pushtype)]
    (if (or (nil? parts) (nil? (:builder pushtype)))
      (throw (Exception. "a parts instruction cannot be constructed for a type lacking a :parts or :builder value"))
      (eval 
        (concat
          (list
            `build-instruction
            instruction-name
            (str "`:" instruction-name "` constructs a new code block from the component parts of the top `" typename "` item (in the order '(" (apply str (interpose " " (keys parts))) ") and pushes that onto the `:exec` stack.")
            `(consume-top-of ~typename :as :arg))
          (invoke-breaker pushtype)
        )))))


