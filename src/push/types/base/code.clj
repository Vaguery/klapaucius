(ns push.types.base.code
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

(defn push-code? [item] (and (list? item) (= (first item) 'quote)))


;; code-specific

; code_container
; code_contains
; code_discrepancy
; code_do
; code_do*
; code_do*count
; code_do*range
; code_do*times
; code_extract
; code_fromboolean
; code_fromfloat
; code_frominteger
; code_fromzipchildren
; code_fromziplefts
; code_fromzipnode
; code_fromziprights
; code_fromziproot
; code_insert
; code_map
; code_nth
; code_nthcdr
; code_overlap
; code_position
; code_size
; code_subst
; code_wrap


(def code-append
  (core/build-instruction
    code-append
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:arg2] #(if (coll? %1) %1 (list %1)) :as :list2)
    (d/calculate [:list1 :list2] #(concat %1 %2) :as :both)
    (d/push-onto :code :both)))



(def code-atom? (t/simple-1-in-predicate :code "atom?" #(not (coll? %1))))


(def code-cons (t/simple-2-in-1-out-instruction 
                    :code 
                    "cons" #(if (seq? %2) 
                                (conj %2 %1) 
                                (conj (list %2) %1))))


(def code-first (t/simple-1-in-1-out-instruction :code "first" #(if (seq? %) (first %) %)))


(def code-if
  (core/build-instruction
    code-if
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :boolean :as :which)
    (d/calculate [:which :arg1 :arg2] #(if %1 %2 %3) :as :that)
    (d/push-onto :exec :that)))


(def code-length
  (core/build-instruction
    code-length
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(if (coll? %1) (count %1) 1) :as :len)
    (d/push-onto :integer :len)))


(def code-list (t/simple-2-in-1-out-instruction :code "list" #(list %1 %2)))


(def code-member?
  (core/build-instruction
    code-member?
    :tags #{:complex :predicate :base}
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :code :as :arg2)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:list1 :arg2] #(not (not-any? #{%2} %1)) :as :present)
    (d/push-onto :boolean :present)))


(def code-noop
  (core/build-instruction
    code-noop
    :tags #{:complex :base}))


(def code-null? (t/simple-1-in-predicate :code "null?" #(and (coll? %) (empty? %))))


(def code-quote
  (core/build-instruction
    code-quote
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/push-onto :code :arg1)))


(def code-rest (t/simple-1-in-1-out-instruction 
                    :code 
                    "rest" 
                    #(if (coll? %1) 
                         (rest %1) 
                         (list))))


(def classic-code-type
  ( ->  (t/make-type  :code
                      :recognizer push-code?
                      :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , code-append)
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-cons)
        (t/attach-instruction , code-first)
        (t/attach-instruction , code-if)
        (t/attach-instruction , code-length)
        (t/attach-instruction , code-list)
        (t/attach-instruction , code-member?)
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-null?)
        (t/attach-instruction , code-quote)
        (t/attach-instruction , code-rest)
        ))

