(ns push.types.core
  (:require [push.instructions.core :as core])
  (:require [push.instructions.dsl :as dsl])
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
  [type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'core/build-instruction
      instruction-name
      :tags #{:arithmetic :base}
      `(d/consume-top-of ~stackname :as :arg1)
      `(d/calculate [:arg1] #(~operation %1) :as :result)
      `(d/push-onto ~stackname :result)))))


(defn simple-2-in-1-out-instruction
  "returns a standard :typed arity-2 function, where the output
  and inputs are all the same type"
  [type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'core/build-instruction
      instruction-name
      :tags #{:arithmetic :base}
      `(d/consume-top-of ~stackname :as :arg2)
      `(d/consume-top-of ~stackname :as :arg1)
      `(d/calculate [:arg1 :arg2] #(~operation %1 %2) :as :result)
      `(d/push-onto ~stackname :result)))))


(defn simple-1-in-predicate
  "returns a standard :typed arity-1 function, where the output is
  a :boolean and inputs are the same type"
  [type word operation]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "-" word)]
    (eval (list
      'core/build-instruction
      instruction-name
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
      'core/build-instruction
      instruction-name
      :tags #{:complex :base :conversion}
      `(d/consume-top-of ~stackname :as :arg)
      '(d/push-onto :code :arg)))))


;; :visible


(defn stackdepth-instruction
  "returns a new x-stackdepth instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-stackdepth")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/push-onto :integer :depth)))))


(defn empty?-instruction
  "returns a new x-empty? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-empty?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:visible}
      `(push.instructions.dsl/count-of ~typename :as :depth)
      '(push.instructions.dsl/calculate [:depth] #(zero? %1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (attach-instruction (stackdepth-instruction pushtype))
      (attach-instruction (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))


;; :comparable


(defn equal?-instruction
  "returns a new x-equal? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-equal?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
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
      :tags #{:equatable}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(not= %1 %2) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


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


(defn lessthan?-instruction
  "returns a new x-<? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "<?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(< (compare %1 %2) 0) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn lessthanorequal?-instruction
  "returns a new x≤? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "≤?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(< (compare %1 %2) 1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn greaterthanorequal?-instruction
  "returns a new x≥? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "≥?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(> (compare %1 %2) -1) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn greaterthan?-instruction
  "returns a new x>? instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) ">?")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(> (compare %1 %2) 0) :as :check)
      '(push.instructions.dsl/push-onto :boolean :check)))))


(defn min-instruction
  "returns a new x-min instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-min")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(min %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)))))


(defn max-instruction
  "returns a new x-max instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-max")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:comparison}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      '(push.instructions.dsl/calculate [:arg1 :arg2] #(max %1 %2) :as :winner)
      `(push.instructions.dsl/push-onto ~typename :winner)))))


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


(defn dup-instruction
  "returns a new x-dup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-dup")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/save-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg1)))))


(defn flush-instruction
  "returns a new x-flush instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flush")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/delete-stack ~typename)))))


(defn pop-instruction
  "returns a new x-pop instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-pop")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/delete-top-of ~typename)))))


(defn rotate-instruction
  "returns a new x-rotate instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-rotate")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg3)
      `(push.instructions.dsl/push-onto ~typename :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg3)))))


(defn shove-instruction
  "returns a new x-shove instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-shove")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/consume-top-of ~typename :as :shoved-item)
      `(push.instructions.dsl/insert-as-nth-of ~typename :shoved-item :at :index)))))



(defn swap-instruction
  "returns a new x-swap instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-swap")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/consume-top-of ~typename :as :arg2)
      `(push.instructions.dsl/push-onto ~typename :arg1)
      `(push.instructions.dsl/push-onto ~typename :arg2)))))


(defn yank-instruction
  "returns a new x-yank instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yank")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/consume-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))


(defn yankdup-instruction
  "returns a new x-yankdup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yankdup")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      :tags #{:combinator}
      '(push.instructions.dsl/consume-top-of :integer :as :index)
      `(push.instructions.dsl/count-of ~typename :as :how-many)
      `(push.instructions.dsl/save-nth-of ~typename :at :index :as :yanked-item)
      `(push.instructions.dsl/push-onto ~typename :yanked-item)))))


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
