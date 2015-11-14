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
    :tags #{:string :conversion :base}
    (d/consume-top-of :string :as :arg)
    (d/consume-stack :char :as :old-stack)
    (d/calculate [:arg :old-stack]
      #(if (empty? %1) %2 
        (u/make-it-a-real-list (concat (seq %1) %2))) :as :new-stack)
    (d/calculate [:new-stack] #(into '() (reverse %1)) :as :kludged)
    (d/replace-stack :char :kludged)))


(def char-digit? (t/simple-1-in-predicate :char "digit?" #(Character/isDigit %1)))


(def char-frominteger
  (core/build-instruction
    char-frominteger
    :tags #{:string :conversion :base}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(char (mod %1 128)) :as :c)
    (d/push-onto :char :c)))


(def char-fromfloat
  (core/build-instruction
    char-fromfloat
    :tags #{:string :conversion :base}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(char (mod (long %1) 128)) :as :c)
    (d/push-onto :char :c)))


(def char-letter? (t/simple-1-in-predicate :char "letter?" #(Character/isLetter %1)))


(def char-lowercase? (t/simple-1-in-predicate :char "lowercase?" #(Character/isLowerCase %1)))


(def char-uppercase? (t/simple-1-in-predicate :char "uppercase?" #(Character/isUpperCase %1)))


(def char-whitespace? (t/simple-1-in-predicate :char "whitespace?" #(Character/isWhitespace %1)))


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
        (t/attach-instruction , char-frominteger)
        (t/attach-instruction , char-fromfloat)
        (t/attach-instruction , char-lowercase?)
        (t/attach-instruction , char-uppercase?)
        ))

