(ns push.instructions.aspects.equatable
  (:use [push.instructions.core
          :only (build-instruction)]
        [push.instructions.dsl]))


(defn equal?-instruction
  "returns a new x-equal? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-equal?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `true` if they are equal, `false` otherwise.")

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of ~typename :as :arg2)
      `(calculate [:arg1 :arg2] #(= %1 %2) :as :check)
      `(return-item :check)
      ))))



(defn notequal?-instruction
  "returns a new x-notequal? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-notequal?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `false` if they are equal, `true` otherwise.")

      `(consume-top-of ~typename :as :arg1)
      `(consume-top-of ~typename :as :arg2)
      `(calculate [:arg1 :arg2] #(not= %1 %2) :as :check)
      `(return-item :check)
      ))))
