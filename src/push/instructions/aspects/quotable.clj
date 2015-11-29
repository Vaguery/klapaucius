(ns push.instructions.aspects.quotable
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:require [push.types.core :as t])
  )


(defn tocode-instruction
  "returns a new x->code instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "->code")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` moves the top item of the `"
        typename "` stack to the `:code` stack.")
      :tags #{:visible}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg)
      '(push.instructions.dsl/push-onto :code :arg)))))


;;;;;;;;;;;;;;;;;;


(defn make-quotable
  "takes a PushType and adds the :quotable attribute, and the :pushtype->code instruction :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (tocode-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :quotable))))

