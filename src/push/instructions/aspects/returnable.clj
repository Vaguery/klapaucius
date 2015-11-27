(ns push.instructions.aspects.returnable
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:require [push.types.core :as t])
  )


(defn return-instruction
  "returns a new x-return instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes it to the `:return` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto :return :arg1)))))


(defn return-pop-instruction
  "returns a new x-return-pop instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return-pop")
        token (keyword (str (name typename) "-pop"))]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` creates a new `" typename
        "-pop` token shoves it to the _bottom_ of the `:return` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-stack :return :as :old-stack)
      `(push.instructions.dsl/calculate [:old-stack] 
          #(concat %1 (list ~token)) :as :new-stack)
      `(push.instructions.dsl/replace-stack :return :new-stack)))))


(defn make-returnable
  "takes a PushType and adds the :returnable attribute and the `:X-return` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (return-instruction pushtype))
      (t/attach-instruction (return-pop-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :returnable))))
