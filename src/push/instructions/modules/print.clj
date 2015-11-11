(ns push.instructions.modules.print
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

; print_exec
; print_integer
; print_float
; print_code
; print_boolean
; print_string
; print_char
; print_vector_integer
; print_vector_float
; print_vector_boolean
; print_vector_string
; print_newline


(def classic-print-module
  ( ->  (t/make-module  :print
                        :attributes #{:io :base})
        t/make-visible 
        ))

