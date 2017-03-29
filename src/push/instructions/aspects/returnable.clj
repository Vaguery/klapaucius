(ns push.instructions.aspects.returnable
  (:require [push.util.code-wrangling :as fix])
  (:use [push.instructions.core :only (build-instruction)]
        [push.instructions.dsl]))




(defn return-instruction
  "returns a new x-return instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and pushes it to the `:return` stack.")
      :tags #{:io}

      `(consume-top-of ~typename :as :arg1)
      `(push-onto :return :arg1)))))




(defn return-pop-instruction
  "returns a new x-return-pop instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return-pop")
        token (keyword (str (name typename) "-pop"))]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` creates a new `" typename "-pop` token shoves it to the _bottom_ of the `:return` stack.")
      :tags #{:io}

      `(consume-stack :return :as :old-stack)
      `(calculate [:old-stack]
          #(fix/list! (concat %1 (list ~token))) :as :new-stack)
      `(replace-stack :return :new-stack)))))
