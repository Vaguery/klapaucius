(ns push.types.core
  (:require [push.interpreter.interpreter-core :as i])
  (:require [push.instructions.instructions-core :as core])
  (:require [push.instructions.dsl :as dsl])
)


(defrecord PushType [stackname recognizer attributes])


(defn make-type
  "Create a PushType record from a stackname (keyword), with
  optional :recognizer and :attributes"
  [stackname & {
    :keys [recognizer attributes] 
    :or {attributes #{}}}]
  (->PushType stackname recognizer attributes))


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (assoc pushtype :attributes (conj (:attributes pushtype) :visible)))


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

