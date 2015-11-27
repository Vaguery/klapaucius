(ns push.instructions.base.print_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.interpreter.core :as i])
  (:require [push.types.core :as t])
  (use push.instructions.aspects.printable)
  (:use push.types.modules.print) 
  )


(tabular
  (fact ":print-newline pushes a single newline character to the :print stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-print-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    ;; start again
    :print    '(9)         :print-newline        :print      '(\newline 9))


(tabular
  (fact ":print-space pushes a single space character to the :print stack"
    (register-type-and-check-instruction
        ?set-stack ?items classic-print-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items      ?instruction      ?get-stack     ?expected
    ;; start again
    :print    '(9)         :print-space        :print      '(\space 9)
    )

(fact "`make-printable` adds the :printable attribute to a PushType record"
  (:attributes (make-printable (t/make-type :foo))) => #{:printable})


(fact "`make-printable` takes adds appropriate instructions to a PushType record"
  (keys (:instructions
    (make-printable (t/make-type :foo)))) => '(:foo-print))

