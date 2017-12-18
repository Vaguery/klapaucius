(ns push.type.item.char
  (:require [push.instructions.dsl        :as d]
            [push.instructions.core       :as i]
            [push.type.core               :as t]
            [push.util.stack-manipulation :as u]
            [push.instructions.aspects    :as aspects]
            [push.util.code-wrangling     :as fix]
            [push.util.numerics           :as num]
            ))

(def char-digit? (i/simple-1-in-predicate
  "`:char-digit? pushes `true` if the top `:char` is a digit (per Java `Character/isDigit`)"
  :char "digit?" #(Character/isDigit %1)
  ))


(def char-letter? (i/simple-1-in-predicate
  "`:char-letter? pushes `true` if the top `:char` is a letter (per Java `Character/isLetter`)"
  :char "letter?" #(Character/isLetter %1)
  ))



(def char-lowercase? (i/simple-1-in-predicate
  "`:char-lowercase? pushes `true` if the top `:char` is lowercase (per Java `Character/isLowerCase`)"
  :char "lowercase?" #(Character/isLowerCase %1)
  ))



(def char-uppercase? (i/simple-1-in-predicate
  "`:char-uppercase? pushes `true` if the top `:char` is uppercase (per Java `Character/isUpperCase`)"
  :char "uppercase?" #(Character/isUpperCase %1)
  ))



(def char-whitespace? (i/simple-1-in-predicate
  "`:char-whitespace? pushes `true` if the top `:char` is a whitespace (per Java `Character/isWhitespace`)"
  :char "whitespace?" #(Character/isWhitespace %1)
  ))



;; conversion



(def scalar->asciichar
  (i/build-instruction
    scalar->asciichar
    "`:scalar->asciichar` pops the top `:scalar` value, reduces it modulo 128, and pushes the `:char` that is represented by that ASCII value"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(char (num/scalar-to-index %1 128)) :as :c)
    (d/return-item :c)
    ))



(def scalar->char
  (i/build-instruction
    scalar->char
    "`:scalar->char` pops the top `:scalar` value, reduces it modulo 65535, and pushes the `:char` that is represented by that unicode value"

    (d/consume-top-of :scalar :as :arg)
    (d/calculate [:arg]
      #(char (num/scalar-to-index %1 65535)) :as :c)
    (d/return-item :c)
    ))




(def string->chars
  (i/build-instruction
    string->chars
    "`:string->chars` pops the top `:string` item, and returns a codeblock containing the characters of that string"

    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] #(into '() (reverse (vec %1))) :as :results)
    (d/return-item :results)
    ))



(def char-type
  ( ->  (t/make-type  :char
                      :recognized-by char?
                      :attributes #{:string})
        aspects/make-set-able
        aspects/make-comparable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , char-digit?)
        (t/attach-instruction , char-letter?)
        (t/attach-instruction , char-lowercase?)
        (t/attach-instruction , char-uppercase?)
        (t/attach-instruction , char-whitespace?)
        (t/attach-instruction , scalar->asciichar)
        (t/attach-instruction , scalar->char)
        (t/attach-instruction , string->chars)
        ))
