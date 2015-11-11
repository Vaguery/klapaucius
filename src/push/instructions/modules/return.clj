(ns push.instructions.modules.return
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

; return_fromexec
; return_frominteger
; return_fromfloat
; return_fromboolean
; return_fromstring
; return_fromchar
; return_fromcode
; return_exec_pop
; return_code_pop
; return_integer_pop
; return_float_pop
; return_boolean_pop
; return_zip_pop
; return_string_pop
; return_char_pop
; return_tagspace



(def classic-return-module
  ( ->  (t/make-module  :return
                        :attributes #{:io :base})
        t/make-visible 
        ))

