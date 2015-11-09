(ns push.instructions.modules.exec
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; exec-specific

; exec_do*range
; exec_do*count
; exec_do*times
; exec_while
; exec_do*while
; exec_when
; exec_fromzipnode
; exec_fromziproot
; exec_fromzipchildren
; exec_fromziplefts
; exec_fromziprights


(def exec-k
  (core/build-instruction
    exec-k
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/consume-top-of :exec :as :arg2)
    (d/push-onto :exec :arg1)))


(def exec-if
  (core/build-instruction
    exec-if
    :tags #{:complex :base}
    (d/consume-top-of :bool :as :decider)
    (d/consume-top-of :exec :as :option1)
    (d/consume-top-of :exec :as :option2)
    (d/calculate [:decider :option1 :option2] #(if %1 %2 %3) :as :result)
    (d/push-onto :exec :result)))


(def exec-noop
  (core/build-instruction
    exec-noop
    :tags #{:complex :base}))


(def exec-s
  (core/build-instruction
    exec-s
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :a)
    (d/consume-top-of :exec :as :b)
    (d/consume-top-of :exec :as :c)
    (d/calculate [:b :c] #(list %1 %2) :as :bc)
    (d/push-onto :exec :bc)
    (d/push-onto :exec :c)
    (d/push-onto :exec :a)))


(def exec-y
  (core/build-instruction
    exec-y
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg)
    (d/calculate [:arg] #(list :exec-y %1) :as :result)
    (d/push-onto :exec :result)
    (d/push-onto :exec :arg)))


(def classic-exec-module
  ( ->  (t/make-module  :exec
                        :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , exec-k)
        (t/attach-instruction , exec-noop)
        (t/attach-instruction , exec-s)
        (t/attach-instruction , exec-if)
        (t/attach-instruction , exec-y)
        ))

