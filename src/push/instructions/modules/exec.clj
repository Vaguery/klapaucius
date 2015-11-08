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
; exec_y
; exec_fromzipnode
; exec_fromziproot
; exec_fromzipchildren
; exec_fromziplefts
; exec_fromziprights


(def exec-noop
  (core/build-instruction
    exec-noop
    :tags #{:complex :base}))


(def classic-exec-module
  ( ->  (t/make-module  :exec
                        :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , exec-noop)
        ))

