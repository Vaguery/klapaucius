(ns push.types.base.char
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.util.stack-manipulation :as u])
  )


;; character-specific


(def char-allfromstring
  (core/build-instruction
    char-allfromstring
    "`:char-allfromstring` pops the top `:string` item, and pushes every character (in the same order as the string) onto the `:char` stack. Thus a string \"foo\" will be pushed onto the `:char` stack as `'(\\f \\o \\o ...)`"
    :tags #{:string :conversion :base}
    (d/consume-top-of :string :as :arg)
    (d/consume-stack :char :as :old-stack)
    (d/calculate [:arg :old-stack]
      #(if (empty? %1) %2 
        (u/make-it-a-real-list (concat (vec %1) %2))) :as :new-stack)
    (d/calculate [:new-stack] #(into '() (reverse %1)) :as :kludged)
    (d/replace-stack :char :kludged)))


(def char-digit? (t/simple-1-in-predicate
  "`:char-digit? pushes `true` if the top `:char` is a digit (per Java `Character/isDigit`)"
  :char "digit?" #(Character/isDigit %1)))


(def char-asciifrominteger
  (core/build-instruction
    char-asciifrominteger
    "`:char-asciifrominteger` pops the top `:integer` value, reduces it modulo 128, and pushes the `:char` that is represented by that ASCII value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(char (mod %1 128)) :as :c)
    (d/push-onto :char :c)))


(def char-frominteger
  (core/build-instruction
    char-frominteger
    "`:char-frominteger` pops the top `:integer` value, reduces it modulo 65535, and pushes the `:char` that is represented by that unicode value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(char (mod %1 65535)) :as :c)
    (d/push-onto :char :c)))


(def char-asciifromfloat
  (core/build-instruction
    char-asciifromfloat
    :tags #{:string :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(char (mod (bigint %1) 128)) :as :c)
    (d/push-onto :char :c)))


(def char-letter? (t/simple-1-in-predicate
  "`:char-letter? pushes `true` if the top `:char` is a letter (per Java `Character/isLetter`)"
  :char "letter?" #(Character/isLetter %1)))


(def char-lowercase? (t/simple-1-in-predicate
  "`:char-lowercase? pushes `true` if the top `:char` is lowercase (per Java `Character/isLowerCase`)"
  :char "lowercase?" #(Character/isLowerCase %1)))


(def char-uppercase? (t/simple-1-in-predicate
  "`:char-uppercase? pushes `true` if the top `:char` is uppercase (per Java `Character/isUpperCase`)"
  :char "uppercase?" #(Character/isUpperCase %1)))


(def char-whitespace? (t/simple-1-in-predicate
  "`:char-whitespace? pushes `true` if the top `:char` is a whitespace (per Java `Character/isWhitespace`)"
  :char "whitespace?" #(Character/isWhitespace %1)))


(def classic-char-type
  ( ->  (t/make-type  :char
                      :recognizer char?
                      :attributes #{:string})
        t/make-visible 
        t/make-equatable
        t/make-comparable
        t/make-movable
        (t/attach-instruction , char-allfromstring)
        (t/attach-instruction , char-letter?)
        (t/attach-instruction , char-digit?)
        (t/attach-instruction , char-whitespace?)
        (t/attach-instruction , char-asciifrominteger)
        (t/attach-instruction , char-frominteger)
        (t/attach-instruction , char-asciifromfloat)
        (t/attach-instruction , char-lowercase?)
        (t/attach-instruction , char-uppercase?)
        ))

