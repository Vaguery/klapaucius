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


(fact ":integer-subtract uses Clojure's -' function to auto-promote results"
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


;; :integer-multiply


(fact ":integer-multiply multiplies integers"
  (let [can-multiply (temp-register knows-some-integers integer-multiply)]
    (peek-i (do-this can-multiply :integer-multiply)) =>
      '(-55 3333333333333333333 7777777777777777777)))


(fact ":integer-multpliy uses Clojure's *' function to auto-promote results"
  (let [can-multiply (temp-register knows-some-integers integer-multiply)]
    (peek-i
      (-> can-multiply
        (do-this :integer-multiply)
        (do-this :integer-multiply)
        (do-this :integer-multiply))) =not=> (throws #"overflow")))


(fact ":integer-multiply respects the :needs limit"
  (let [can-multiply (temp-register knows-some-integers integer-multiply)]
    (peek-i
      (-> can-multiply
          (do-this :integer-multiply)
          (do-this :integer-multiply)
          (do-this :integer-multiply)
          (do-this :integer-multiply)
          (do-this :integer-multiply))) =not=> (throws)))



;; :integer-divide
    


(future-fact ":integer-divide divides integers to produce an integer result"
    (peek-i
      (do-this 
        (temp-register 
          (i/make-interpreter :stacks {:integer '(4 20)})
            integer-divide)
        :integer-divide)) => '(5)
    (peek-i
      (do-this 
        (temp-register 
          (i/make-interpreter :stacks {:integer '(6 20)})
            integer-divide)
        :integer-divide)) => '(3)
    (peek-i
      (do-this 
        (temp-register 
          (i/make-interpreter :stacks {:integer '(-21 20)})
            integer-divide)
        :integer-divide)) => '(0))


(future-fact ":integer-divide leaves the :integer stack unchanged if the denominator is 0"
    (peek-i
      (do-this 
        (temp-register 
          (i/make-interpreter :stacks {:integer '(0 20)})
            integer-divide)
        :integer-divide)) => '(0 20))

