(ns push.instructions.aspects.equatable
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:require [push.types.core :as t])
  )


(defn equal?-instruction
  "returns a new x-equal? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-equal?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `true` if they are equal, `false` otherwise.")
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn notequal?-instruction
  "returns a new x-notequal? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-notequal?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `false` if they are equal, `true` otherwise.")
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(not= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))



;;;;;;;;;;;;;;;;;;;;



(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (equal?-instruction pushtype))
      (t/attach-instruction (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))

