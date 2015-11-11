(ns push.types.standard.zipper
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )

;; TODO


; zip_next
; zip_prev
; zip_down
; zip_up
; zip_left
; zip_leftmost
; zip_right
; zip_rightmost
; zip_end?
; zip_branch?
; zip_replace_fromcode
; zip_replace_fromexec
; zip_insert_right_fromcode
; zip_insert_right_fromexec
; zip_insert_left_fromcode
; zip_insert_left_fromexec
; zip_insert_child_fromcode
; zip_insert_child_fromexec
; zip_append_child_fromcode
; zip_append_child_fromexec
; zip_remove
; zip_fromcode
; zip_fromexec
; code_fromzipnode
; exec_fromzipnode
; code_fromziproot
; exec_fromziproot
; code_fromzipchildren
; exec_fromzipchildren
; code_fromziplefts
; exec_fromziplefts
; code_fromziprights
; exec_fromziprights


; (def zipper-type
;   ( ->  (t/make-type  :zipper
;                       :recognizer ;; no idea
;                       :attributes #{:trees})
;         t/make-visible 
;         t/make-movable
;         ))

