(ns push.types.base.char
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects])
  )


;; character-specific


(def string->chars
  (core/build-instruction
    string->chars
    "`:string->chars` pops the top `:string` item, and pushes every character (in the same order as the string) onto the `:char` stack. Thus a string \"foo\" will be pushed onto the `:char` stack as `'(\\f \\o \\o ...)`"
    :tags #{:string :conversion :base}
    (d/consume-top-of :string :as :arg)
    (d/consume-stack :char :as :old-stack)
    (d/calculate [:arg :old-stack]
      #(if (empty? %1) %2 (concat (vec %1) %2)) :as :new-stack)
    (d/calculate [:new-stack] #(into '() (reverse %1)) :as :kludged)
    (d/replace-stack :char :kludged)))


(def char-digit? (t/simple-1-in-predicate
  "`:char-digit? pushes `true` if the top `:char` is a digit (per Java `Character/isDigit`)"
  :char "digit?" #(Character/isDigit %1)))


(def integer->asciichar
  (core/build-instruction
    integer->asciichar
    "`:integer->asciichar` pops the top `:integer` value, reduces it modulo 128, and pushes the `:char` that is represented by that ASCII value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(char (mod %1 128)) :as :c)
    (d/push-onto :char :c)))


(def integer->char
  (core/build-instruction
    integer->char
    "`:integer-char` pops the top `:integer` value, reduces it modulo 65535, and pushes the `:char` that is represented by that unicode value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(char (mod %1 65535)) :as :c)
    (d/push-onto :char :c)))


(def float->asciichar
  (core/build-instruction
    float->asciichar
    "`:float->asciichar` pops the top `:float` value, reduces it to an integer modulo 128, and pushes the `:char` that is represented by that ASCII value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(char (mod (bigint %1) 128)) :as :c)
    (d/push-onto :char :c)))


(def float->char
  (core/build-instruction
    float->char
    "`:float->char` pops the top `:float` value, reduces it to an integer modulo 65535, and pushes the `:char` that is represented by that unicode value"
    :tags #{:string :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(char (mod (bigint %1) 65535)) :as :c)
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


(def char-type
  ( ->  (t/make-type  :char
                      :recognizer char?
                      :attributes #{:string})
        aspects/make-visible 
        aspects/make-equatable
        aspects/make-comparable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-returnable
        aspects/make-storable
        (t/attach-instruction , string->chars)
        (t/attach-instruction , char-letter?)
        (t/attach-instruction , char-digit?)
        (t/attach-instruction , char-whitespace?)
        (t/attach-instruction , integer->asciichar)
        (t/attach-instruction , integer->char)
        (t/attach-instruction , float->char)
        (t/attach-instruction , float->asciichar)
        (t/attach-instruction , char-lowercase?)
        (t/attach-instruction , char-uppercase?)
        ))

