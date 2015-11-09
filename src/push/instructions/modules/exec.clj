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
; exec_if
; exec_when
; exec_k
; exec_s
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


(def exec-noop
  (core/build-instruction
    exec-noop
    :tags #{:complex :base}))


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
        (t/attach-instruction , exec-y)
        ))

