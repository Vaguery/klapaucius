(ns push.type.core
  (:require [push.router.core :as router]
            ))


(def core-stacks
  "the basic types central to the Push language"
    {:boolean '()
    :char '()
    :code '()
    :scalar '()
    :exec '()
    :string '()
    })


(defrecord PushType [name router attributes instructions manifest builder])


(defn make-type
  "Create a PushType record from a name (keyword) and a router (PushRouter record), with optional :attributes and :instructions"
  [name & {
    :keys [recognized-by router attributes instructions manifest builder]
    :or {recognized-by (constantly false)
         router (router/make-router name :recognizer recognized-by)
         attributes #{}
         instructions {}
         manifest {}
         builder nil}}]
  (->PushType name router attributes instructions manifest builder))


(defn recognize?
  "takes a PushType and any item, and returns true of the `:recognizer` of the type's `:router` returns true when applied to the item"
  [type item]
  (router/router-recognize? (:router type) item))


(defn attach-instruction
  [pushtype instruction]
  (let [old-instructions (:instructions pushtype)
        new-key (:token instruction)]
    (assoc
      pushtype
      :instructions
      (merge old-instructions (hash-map new-key instruction)))))


(defn conditional-attach-instruction
  "attaches an instruction to a pushtype only if the predicate is `true`"
  [pushtype predicate instruction]
  (if predicate
    (attach-instruction pushtype instruction)
    pushtype
    ))

;;;; Modules (like PushTypes, but not about the stack items)


(defn make-module
  "creates a simple hash-map that is a type-like bundle of attributes and instructions"
  [module-name &
    {:keys [attributes instructions]
       :or {attributes #{} instructions {}}}]
  {:name module-name
   :attributes attributes
   :instructions instructions})
