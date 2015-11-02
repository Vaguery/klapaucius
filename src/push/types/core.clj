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


(defn attach-function
  [pushtype function]
  (let [old-instructions (:instructions pushtype)]
    (assoc pushtype :instructions (conj old-instructions function))))


;;;; type-associated instructions

;; :visible

(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:visible}
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
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/calculate [:depth] #(zero? %1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (stackdepth-instruction pushtype))
      (attach-function (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))


;; :comparable


(defn equal?-instruction
  "returns a new x-equal? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-equal?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn notequal?-instruction
  "returns a new x-notequal? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-notequal?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(not= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (equal?-instruction pushtype))
      (attach-function (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))


;; comparable


(defn lessthan?-instruction
  "returns a new x-<? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "<?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(< %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn lessthanorequal?-instruction
  "returns a new x≤? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "≤?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(<= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn greaterthanorequal?-instruction
  "returns a new x≥? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "≥?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(>= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn greaterthan?-instruction
  "returns a new x>? instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) ">?")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(> %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)
      ))))


(defn min-instruction
  "returns a new x-min instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-min")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(min %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)
      ))))



(defn max-instruction
  "returns a new x-max instruction for a PushType"
  [pushtype]
  (let [typename (:stackname pushtype)
        instruction-name (str (name typename) "-max")]
    (eval (list
      'push.instructions.instructions-core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(max %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)
      ))))


(defn make-comparable
  "takes a PushType and adds the :comparable attribute, and the
  :pushtype>?, :pushtype≥?, :pushtype<?, :pushtype≤?, :pushtype-min and
  :pushtype-max instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-function (lessthan?-instruction pushtype))
      (attach-function (lessthanorequal?-instruction pushtype))
      (attach-function (greaterthan?-instruction pushtype))
      (attach-function (greaterthanorequal?-instruction pushtype))
      (attach-function (min-instruction pushtype))
      (attach-function (max-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :comparable))))

