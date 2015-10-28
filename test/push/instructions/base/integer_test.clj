(ns push.instructions.base.integer_test
  (:use midje.sweet)
  (:require [push.interpreter.interpreter-core :as i])
  (:use [push.instructions.base.integer]))


;; fixtures


(def knows-some-integers
    (i/make-interpreter 
      :stacks {:integer '(11 -5 3333333333333333333 7777777777777777777)}))
      ;; the last two, when added, push us over the interger overflow limit


;; convenience functions


(defn temp-register
  [interpreter instruction]
  (i/register-instruction interpreter instruction))


(defn peek-i
  [interpreter]
  (i/get-stack interpreter :integer))


(def do-this i/execute-instruction)


;; :integer-add


(fact ":integer-add adds integers"
  (let [can-add (temp-register knows-some-integers integer-add)]
    (peek-i (do-this can-add :integer-add)) =>
    '(6 3333333333333333333 7777777777777777777)))


(fact ":integer-add uses Clojure's +' function to auto-promote results"
  (let [can-add (temp-register knows-some-integers integer-add)]
    (peek-i
      (-> can-add
        (do-this :integer-add)
        (do-this :integer-add)
        (do-this :integer-add))) =not=> (throws #"overflow")))


(fact ":integer-add respects the :needs limit"
  (let [can-add (temp-register knows-some-integers integer-add)]
    (peek-i
      (-> can-add
          (do-this :integer-add)
          (do-this :integer-add)
          (do-this :integer-add)
          (do-this :integer-add) ;; runs out of arguments here
          (do-this :integer-add))) =not=> (throws)))


;; :integer-subtract


(fact ":integer-subtract subtracts integers"
  (let [can-subtract (temp-register knows-some-integers integer-subtract)]
    (peek-i (do-this can-subtract :integer-subtract)) =>
      '(16 3333333333333333333 7777777777777777777)))


(fact ":integer-subtract uses Clojure's +' function to auto-promote results"
  (let [can-subtract (temp-register knows-some-integers integer-subtract)]
    (peek-i
      (-> can-subtract
        (do-this :integer-subtract)
        (do-this :integer-subtract)
        (do-this :integer-subtract))) =not=> (throws #"overflow")))


(fact ":integer-subtract respects the :needs limit"
  (let [can-subtract (temp-register knows-some-integers integer-subtract)]
    (peek-i
      (-> can-subtract
          (do-this :integer-subtract)
          (do-this :integer-subtract)
          (do-this :integer-subtract)
          (do-this :integer-subtract)
          (do-this :integer-subtract))) =not=> (throws)))


