(ns push.instructions.base.exec_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:use [push.instructions.modules.exec])
  )



;; these are tests of an Interpreter with the classic-exec-module registered
;; the instructions under test are those stored IN THAT TYPE

;; work in progress
;; these instructions from Clojush are yet to be implemented:

; assemblers and disassemblers

; exec_fromzipnode
; exec_fromziproot
; exec_fromzipchildren
; exec_fromziplefts
; exec_fromziprights


; exec methods qua methods


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


(tabular
  (fact ":exec-noop does nothing"
    (register-type-and-check-instruction
        ?set-stack ?items classic-exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :exec    '(1.1 2.2)          :exec-noop          :exec         '(1.1 2.2) 
    :exec    '(1.0)              :exec-noop          :exec         '(1.0)     
    :exec    '()                 :exec-noop          :exec         '())



;; visible


(tabular
  (fact ":exec-stackdepth returns the number of items on the :exec stack (to :integer)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items            ?instruction      ?get-stack     ?expected
    ;; how many?
    :exec    '(1.1 2.2 3.3)      :exec-stackdepth   :integer      '(3)
    :exec    '(1.0)              :exec-stackdepth   :integer      '(1)
    :exec    '()                 :exec-stackdepth   :integer      '(0))
   

(tabular
  (fact ":exec-empty? returns the true (to :boolean stack) if the stack is empty"
    (register-type-and-check-instruction
        ?set-stack ?items classic-exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items          ?instruction  ?get-stack     ?expected
    ;; none?
    :exec    '(0.2 1.3e7)        :exec-empty?   :boolean     '(false)
    :exec    '()                 :exec-empty?   :boolean     '(true))


;; equatable


(tabular
  (fact ":exec-equal? returns a :boolean indicating whether :first = :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction      ?get-stack     ?expected
    ;; same?
    :exec    '((1 2) (3 4))     :exec-equal?      :boolean        '(false)
    :exec    '((3 4) (1 2))     :exec-equal?      :boolean        '(false)
    :exec    '((1 2) (1 2))     :exec-equal?      :boolean        '(true)
    ;; missing args     
    :exec    '((3 4))           :exec-equal?      :boolean        '()
    :exec    '((3 4))           :exec-equal?      :exec           '((3 4))
    :exec    '()                :exec-equal?      :boolean        '()
    :exec    '()                :exec-equal?      :exec           '())


(tabular
  (fact ":exec-notequal? returns a :boolean indicating whether :first ≠ :second"
    (register-type-and-check-instruction
        ?set-stack ?items classic-exec-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items           ?instruction      ?get-stack     ?expected
    ;; different
    :exec    '((1) (88))       :exec-notequal?      :boolean      '(true)
    :exec    '((88) (1))       :exec-notequal?      :boolean      '(true)
    :exec    '((1) (1))        :exec-notequal?      :boolean      '(false)
    ;; missing args    
    :exec    '((88))           :exec-notequal?      :boolean      '()
    :exec    '((88))           :exec-notequal?      :exec         '((88))
    :exec    '()               :exec-notequal?      :boolean      '()
    :exec    '()               :exec-notequal?      :exec         '())

; ;; movable