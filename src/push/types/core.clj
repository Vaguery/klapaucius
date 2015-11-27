(ns push.types.core
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
  (:use [push.instructions.aspects.visible])
  (:use [push.instructions.aspects.equatable])
  (:use [push.instructions.aspects.comparable])
  (:use [push.instructions.aspects.movable])
  )


(def core-stacks
  "the basic types central to the Push language"
    {:boolean '()
    :char '()
    :code '()
    :integer '() 
    :exec '()
    :float '()
    :string '()
    })


(defrecord PushType [name recognizer attributes instructions])


(defn make-type
  "Create a PushType record from a name (keyword), with
  optional :recognizer :attributes and :instructions"
  [name & {
    :keys [recognizer attributes instructions] 
    :or {recognizer (constantly false) attributes #{} instructions {}}}]
  (->PushType name recognizer attributes instructions))


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
      `(d/consume-top-of ~stackname :as :arg1)
      `(d/calculate [:arg1] #(~operation %1) :as :result)
      `(d/push-onto ~stackname :result)))))


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
      `(d/consume-top-of ~stackname :as :arg2)
      `(d/consume-top-of ~stackname :as :arg1)
      `(d/calculate [:arg1 :arg2] #(~operation %1 %2) :as :result)
      `(d/push-onto ~stackname :result)))))


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
      `(d/consume-top-of ~stackname :as :arg1)
      `(d/calculate [:arg1] #(~operation %1) :as :result)
      '(d/push-onto :boolean :result)))))


(defn simple-item-to-code-instruction
  "returns a standard arity-1 function, which moves the top item from the named stack to the :code stack"
  [type]
  (let [stackname (keyword type)
        instruction-name (str "code-from" (name stackname))]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" stackname "` item and pushes it to `:code`")
      :tags #{:complex :base :conversion}
      `(d/consume-top-of ~stackname :as :arg)
      '(d/push-onto :code :arg)))))


;; visible


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-instruction (stackdepth-instruction pushtype))
      (attach-instruction (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))


;; :equatable


(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-instruction (equal?-instruction pushtype))
      (attach-instruction (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))


;; comparable


(defn make-comparable
  "takes a PushType and adds the :comparable attribute, and the
  :pushtype>?, :pushtype≥?, :pushtype<?, :pushtype≤?, :pushtype-min and
  :pushtype-max instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-instruction (lessthan?-instruction pushtype))
      (attach-instruction (lessthanorequal?-instruction pushtype))
      (attach-instruction (greaterthan?-instruction pushtype))
      (attach-instruction (greaterthanorequal?-instruction pushtype))
      (attach-instruction (min-instruction pushtype))
      (attach-instruction (max-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :comparable))))


;; movable


(defn make-movable
  "takes a PushType and adds the :movable attribute, and the
  :pushtype-dup, :pushtype-flush, :pushtype-pop, :pushtype-rotate,
  :pushtype-shove, :pushtype-swap, :pushtype-yank and
  :pushtype-yankdup instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-instruction (dup-instruction pushtype))
      (attach-instruction (flush-instruction pushtype))
      (attach-instruction (pop-instruction pushtype))
      (attach-instruction (rotate-instruction pushtype))
      (attach-instruction (shove-instruction pushtype))
      (attach-instruction (swap-instruction pushtype))
      (attach-instruction (yank-instruction pushtype))
      (attach-instruction (yankdup-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :movable))))
