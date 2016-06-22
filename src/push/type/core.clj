(ns push.type.core
  (:require [push.instructions.core :as core]
            [push.instructions.dsl :as dsl]
            [push.router.core :as router])
  )


(def core-stacks
  "the basic types central to the Push language"
    {:boolean '()
    :char '()
    :code '()
    :scalar '() 
    :exec '()
    :string '()
    })


(defrecord PushType [name router attributes instructions])


(defn make-type
  "Create a PushType record from a name (keyword) and a router (PushRouter record), with optional :attributes and :instructions"
  [name & {
    :keys [recognized-by router attributes instructions] 
    :or {recognized-by (constantly false)
         router (router/make-router name :recognizer recognized-by)
         attributes #{}
         instructions {}}}]
  (->PushType name router attributes instructions))


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


;;;; Modules (like PushTypes, but not about the stack items)


(defn make-module
  "creates a simple hash-map that is a type-like bundle
  of attributes and instructions"
  [module-name & 
    {:keys [attributes instructions] 
       :or {attributes #{} instructions {}}}]
  {:name module-name
   :attributes attributes
   :instructions instructions})


;;;; type-associated instructions


;; some generic instruction constructors


(defn simple-1-in-1-out-instruction
  "returns a standard :typed arity-1 function, where the output
  and input are the same type"
  [docstring type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'core/build-instruction
      instruction-name
      docstring
      :tags #{:arithmetic :base}
      `(dsl/consume-top-of ~stackname :as :arg1)
      `(dsl/calculate [:arg1] #(~operation %1) :as :result)
      `(dsl/push-onto ~stackname :result)))))


(defn simple-2-in-1-out-instruction
  "returns a standard :typed arity-2 function, where the output
  and inputs are all the same type"
  [docstring type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'core/build-instruction
      instruction-name
      docstring
      :tags #{:arithmetic :base}
      `(dsl/consume-top-of ~stackname :as :arg2)
      `(dsl/consume-top-of ~stackname :as :arg1)
      `(dsl/calculate [:arg1 :arg2] #(~operation %1 %2) :as :result)
      `(dsl/push-onto ~stackname :result)))))


(defn simple-1-in-predicate
  "returns a standard :typed arity-1 predicate function, where the output is
  a :boolean and inputs are the same type"
  [docstring type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      docstring
      :tags #{:arithmetic :base}
      `(dsl/consume-top-of ~stackname :as :arg1)
      `(dsl/calculate [:arg1] #(~operation %1) :as :result)
      `(dsl/push-onto :boolean :result)))))


(defn simple-item-to-code-instruction
  "returns a standard arity-1 function, which moves the top item from the named stack to the :code stack"
  [type]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "->code")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" stackname "` item and pushes it to `:code`")
      :tags #{:complex :base :conversion}
      `(dsl/consume-top-of ~stackname :as :arg)
      `(dsl/push-onto :code :arg)))))

