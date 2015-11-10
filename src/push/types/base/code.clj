(ns push.types.base.code
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

(defn push-code? [item] (and (list? item) (= (first item) 'quote)))


;; code-specific

; code_cdr
; code_cons
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
; code_if
; code_insert
; code_length
; code_list
; code_map
; code_member
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
    (d/calculate [:arg1] #(if (seq? %1) %1 (list %1)) :as :list1)
    (d/calculate [:arg2] #(if (seq? %1) %1 (list %1)) :as :list2)
    (d/calculate [:list1 :list2] #(concat %1 %2) :as :both)
    (d/push-onto :code :both)))


(def code-atom?
  (core/build-instruction
    code-atom?
    :tags #{:complex :predicate :base}
    (d/consume-top-of :code :as :c)
    (d/calculate [:c] #(not (seq? %1)) :as :unlisted)
    (d/push-onto :boolean :unlisted)))


(def code-first
  (core/build-instruction
    code-first
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(if (seq? %1) (first %1) %1) :as :item)
    (d/push-onto :code :item)))


(def code-noop
  (core/build-instruction
    code-noop
    :tags #{:complex :base}))


(def code-null?
  (core/build-instruction
    code-null?
    :tags #{:complex :predicate :base}
    (d/consume-top-of :code :as :c)
    (d/calculate [:c] #(and (seq? %1) (empty? %1)) :as :empty)
    (d/push-onto :boolean :empty)))


(def code-quote
  (core/build-instruction
    code-quote
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/push-onto :code :arg1)))


(def classic-code-type
  ( ->  (t/make-type  :code
                      :recognizer push-code?
                      :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , code-append)
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-first)
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-null?)
        (t/attach-instruction , code-quote)
        ))

