(ns push.instructions.base.char_test
  (:require [push.interpreter.core :as i])
  (:use [push.util.test-helpers])
  (:use [push.types.base.char])  ;; sets up classic-char-type
  (:use midje.sweet))


;; these are tests of an Interpreter with the classic-char-type registered
;; the instructions under test are those stored IN THAT TYPE


; char_allfromstring
; char_frominteger
; char_fromfloat


;; specific char behavior

(tabular
  (fact ":char-letter? returns true when the :char item is an alphabetic letter (LC or UC)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :char    '(\R)           :char-letter?   :boolean     '(true)
    :char    '(\8)           :char-letter?   :boolean     '(false)
    :char    '(\e)           :char-letter?   :boolean     '(true)
    :char    '(\space)       :char-letter?   :boolean     '(false)
    :char    '(\2)           :char-letter?   :boolean     '(false)
    :char    '(\ø)           :char-letter?   :boolean     '(true)
    :char    '(\á)           :char-letter?   :boolean     '(true)
    :char    '(\Ñ)           :char-letter?   :boolean     '(true)
    :char    '(\Ω)           :char-letter?   :boolean     '(true)
    :char    '(\£)           :char-letter?   :boolean     '(false)
    :char    '(\ℜ)           :char-letter?   :boolean     '(true)
    :char    '(\♫)           :char-letter?   :boolean     '(false)
   ;; missing args
    :char    '()             :char-letter?   :boolean     '())


(tabular
  (fact ":char-digit? returns true when the :char item is an numeric digit"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items         ?instruction  ?get-stack   ?expected
    ;; anding
    :char    '(\R)           :char-digit?   :boolean     '(false)
    :char    '(\8)           :char-digit?   :boolean     '(true)
    :char    '(\e)           :char-digit?   :boolean     '(false)
    :char    '(\space)       :char-digit?   :boolean     '(false)
    :char    '(\2)           :char-digit?   :boolean     '(true)
    :char    '(\٧)           :char-digit?   :boolean     '(true)   ;; Arabic
    :char    '(\൬)           :char-digit?   :boolean     '(true)  ;; Malayalam
    :char    '(\④)           :char-digit?   :boolean     '(false)
    :char    '(\⒋)           :char-digit?   :boolean     '(false)
    :char    '(\⓽)           :char-digit?   :boolean     '(false)
    :char    '(\➏)           :char-digit?   :boolean     '(false)
    :char    '(\８)           :char-digit?   :boolean     '(true)   ;; fullwidth (Asian)
    :char    '(\Ⅷ)           :char-digit?   :boolean    '(false)
   ;; missing args
    :char    '()             :char-digit?   :boolean     '())


(tabular
  (fact ":char-whitespace? returns true when the :char item is any kind of whitespace"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items              ?instruction  ?get-stack     ?expected
    ;; anding
    :char    '(\space)           :char-whitespace?   :boolean     '(true)
    :char    '(\newline)         :char-whitespace?   :boolean     '(true)
    :char    '(\e)               :char-whitespace?   :boolean     '(false)
    :char    '(\tab)             :char-whitespace?   :boolean     '(true)
    :char    '(\formfeed)        :char-whitespace?   :boolean     '(true)
    :char    '(\backspace)       :char-whitespace?   :boolean     '(false)
    :char    '(\return)          :char-whitespace?   :boolean     '(true)
    :char    '(\␠)               :char-whitespace?   :boolean     '(false)
    :char    '(\u00A0)           :char-whitespace?   :boolean     '(false)
    :char    '(\u1361)           :char-whitespace?   :boolean     '(false)
    :char    '(\u1680)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2002)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2003)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2004)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2005)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2006)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2007)           :char-whitespace?   :boolean     '(false)
    :char    '(\u2008)           :char-whitespace?   :boolean     '(true)
    :char    '(\u2009)           :char-whitespace?   :boolean     '(true)
    :char    '(\u200A)           :char-whitespace?   :boolean     '(true)
    :char    '(\u200B)           :char-whitespace?   :boolean     '(false)
    :char    '(\u202F)           :char-whitespace?   :boolean     '(false)
    :char    '(\u205F)           :char-whitespace?   :boolean     '(true)
    :char    '(\u3000)           :char-whitespace?   :boolean     '(true)
    :char    '(\u303F)           :char-whitespace?   :boolean     '(false)
    :char    '(\u0007)           :char-whitespace?   :boolean     '(false)
    :char    '(\u0011)           :char-whitespace?   :boolean     '(false)
   ;; missing args
    :char    '()                 :char-whitespace?   :boolean     '())



(tabular
  (fact ":char-lowercase? returns true when the :char item is lowercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; anding
    :char    '(\r)         :char-lowercase?   :boolean     '(true)
    :char    '(\R)         :char-lowercase?   :boolean     '(false)
    :char    '(\1)         :char-lowercase?   :boolean     '(false)
    :char    '(\space)     :char-lowercase?   :boolean     '(false)
    :char    '(\æ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ƛ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ǯ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ɷ)         :char-lowercase?   :boolean     '(true)
    :char    '(\ʯ)         :char-lowercase?   :boolean     '(true)
    :char    '(\π)         :char-lowercase?   :boolean     '(true)
    :char    '(\ß)         :char-lowercase?   :boolean     '(true)
    :char    '(\℥)         :char-lowercase?   :boolean     '(false)
    :char    '(\⒦)         :char-lowercase?   :boolean     '(false)
    :char    '(\ⓝ)         :char-lowercase?   :boolean     '(true)
   ;; missing args
    :char    '()           :char-lowercase?   :boolean     '())



(tabular
  (fact ":char-uppercase? returns true when the :char item is uppercase (per Java)"
    (register-type-and-check-instruction
        ?set-stack ?items classic-char-type ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items     ?instruction  ?get-stack     ?expected
    ;; anding
    :char    '(\r)         :char-uppercase?   :boolean     '(false)
    :char    '(\R)         :char-uppercase?   :boolean     '(true)
    :char    '(\1)         :char-uppercase?   :boolean     '(false)
    :char    '(\space)     :char-uppercase?   :boolean     '(false)
    :char    '(\æ)         :char-uppercase?   :boolean     '(false)
    :char    '(\Æ)         :char-uppercase?   :boolean     '(true)
    :char    '(\Ə)         :char-uppercase?   :boolean     '(true)
    :char    '(\Ȝ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ʀ)         :char-uppercase?   :boolean     '(false)
    :char    '(\ᴆ)         :char-uppercase?   :boolean     '(false)
    :char    '(\Ψ)         :char-uppercase?   :boolean     '(true)
    :char    '(\∃)         :char-uppercase?   :boolean     '(false)
    :char    '(\∀)         :char-uppercase?   :boolean     '(false)
    :char    '(\ℍ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ℚ)         :char-uppercase?   :boolean     '(true)
    :char    '(\ℜ)         :char-uppercase?   :boolean     '(true)
    :char    '(\℞)         :char-uppercase?   :boolean     '(false)
    :char    '(\₨)         :char-uppercase?   :boolean     '(false)
    :char    '(\🄷)         :char-uppercase?   :boolean     '(false)
    :char    '(\🄖)         :char-uppercase?   :boolean     '(false)
    :char    '(\Ⓕ)         :char-uppercase?   :boolean     '(false)
    :char    '(\🅴)         :char-uppercase?   :boolean     '(false)
   ;; missing args
    :char    '()           :char-uppercase?   :boolean     '())

