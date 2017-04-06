(ns push.instructions.aspects.comparable
  (:use [push.instructions.core
          :only (build-instruction)]
        [push.instructions.dsl]))



(defn lessthan?-instruction
  "returns a new x-<? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "<?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename "` items and pushes `true` if the top item is less than the second, `false` otherwise.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(< (compare %1 %2) 0) :as :check)
      `(push-onto :boolean :check)))))



(defn lessthanorequal?-instruction
  "returns a new x≤? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "≤?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename "` items and pushes `true` if the top item is less than or equal to the second, `false` otherwise.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(< (compare %1 %2) 1) :as :check)
      `(push-onto :boolean :check)))))



(defn greaterthanorequal?-instruction
  "returns a new x≥? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "≥?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `true` if the top item is greater than or equal to the second, `false` otherwise.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(> (compare %1 %2) -1) :as :check)
      `(push-onto :boolean :check)))))



(defn greaterthan?-instruction
  "returns a new x>? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) ">?")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename
        "` items and pushes `true` if the top item is greater than the second, `false` otherwise.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(> (compare %1 %2) 0) :as :check)
      `(push-onto :boolean :check)))))



(defn min-instruction
  "returns a new x-min instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-min")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename "` items and pushes the _smaller_ of the two.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(if (pos? (compare %1 %2)) %2 %1) :as :winner)
      `(push-onto ~typename :winner)))))



(defn max-instruction
  "returns a new x-max instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-max")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top two `" typename "` items and pushes the _larger_ of the two.")
      :tags #{:comparison}

      `(consume-top-of ~typename :as :arg2)
      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1 :arg2] #(if (neg? (compare %1 %2)) %2 %1) :as :winner)
      `(push-onto ~typename :winner)))))
