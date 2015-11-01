(ns push.types.core
  (:require [push.interpreter.interpreter-core :as i])
  (:require [push.instructions.instructions-core :as core])
  (:require [push.instructions.dsl :as dsl])
)


(defrecord PushType [stackname recognizer attributes instructions])


(defn make-type
  "Create a PushType record from a stackname (keyword), with
  optional :recognizer :attributes and :instructions"
  [stackname & {
    :keys [recognizer attributes instructions] 
    :or {attributes #{} instructions []}}]
  (->PushType stackname recognizer attributes instructions))



;;;; type-associated instructions


(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:introspection}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/push-onto :integer :depth)
      ))))


(defn empty?-instruction
  "returns a new x-empty? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-empty?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:introspection}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/calculate [:depth] #(zero? %1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn attach-stackdepth-function
  [pushtype]
  (let [old-instructions (:instructions pushtype)]
    (assoc pushtype :instructions
      (conj old-instructions (stackdepth-instruction pushtype)))))


(defn attach-empty?-function
  [pushtype]
  (let [old-instructions (:instructions pushtype)]
    (assoc pushtype :instructions
      (conj old-instructions (empty?-instruction pushtype)))))

;;;; stored generic instructions


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      attach-stackdepth-function
      attach-empty?-function
      (assoc :attributes (conj (:attributes pushtype) :visible))))

