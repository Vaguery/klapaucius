(ns push.types.base.exec
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; exec-specific

; exec_noop
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


(def classic-exec-type
  ( ->  (t/make-type  :exec
                      :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        ))

