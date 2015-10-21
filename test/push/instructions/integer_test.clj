(ns push.instructions.integer-test
  (:use midje.sweet)
  (:use push.interpreter)
  (:require [push.instructions.integer :as int]))


;; arithmetic


(def no-nums (make-interpreter))
(def one-num (make-interpreter :stacks {:integer '(1)}))
(def two-nums (make-interpreter :stacks {:integer '(3 7)}))
(def many-nums (make-interpreter :stacks {:integer '(3 7 -2 33)}))


;; integer_add


; (def no-nums   (register-instruction (make-interpreter) int/integer-add))
; (def one-num   (register-instruction 
;   (make-interpreter :stacks {:integer '(1)}) int/integer-add))
; (def two-nums  (register-instruction 
;   (make-interpreter :stacks {:integer '(3 7)}) int/integer-add))
; (def many-nums (register-instruction
;   (make-interpreter :stacks {:integer '(3 7 -2 33)}) int/integer-add))


(future-fact ":integer_add has no effect when the :needs aren't met"
  (execute-instruction no-nums :integer_add) => no-nums
  (execute-instruction one-num :integer_add) => one-num
  )

(future-fact ":integer_add adds numbers when the :needs are met"
  (get-stack (execute-instruction two-nums :integer_add) :integer) => '(10)
  (get-stack (execute-instruction many-nums :integer_add) :integer) => '(10 -2 33)
  )