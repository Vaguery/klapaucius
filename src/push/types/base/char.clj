(ns push.types.base.char
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; character-specific


(def char-allfromstring
  (core/build-instruction
    char-allfromstring
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg)
    (d/consume-stack :char :as :old-stack)
    (d/calculate [:arg :old-stack]
      #(if (empty? %1) %2 (concat (seq %1) %2)) :as :new-stack)
    (d/calculate [:new-stack] #(into '() (reverse %1)) :as :kludged)
    (d/replace-stack :char :kludged)))


(def char-letter?
  (core/build-instruction
    char-letter?
    :tags #{:string :base}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(Character/isLetter %1) :as :check)
    (d/push-onto :boolean :check)))


(def char-digit?
  (core/build-instruction
    char-digit?
    :tags #{:string :base}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(Character/isDigit %1) :as :check)
    (d/push-onto :boolean :check)))


(def char-whitespace?
  (core/build-instruction
    char-whitespace?
    :tags #{:string :base}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(Character/isWhitespace %1) :as :check)
    (d/push-onto :boolean :check)))


(def char-lowercase?
  (core/build-instruction
    char-lowercase?
    :tags #{:string :base}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(Character/isLowerCase %1) :as :check)
    (d/push-onto :boolean :check)))


(def char-uppercase?
  (core/build-instruction
    char-uppercase?
    :tags #{:string :base}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(Character/isUpperCase %1) :as :check)
    (d/push-onto :boolean :check)))


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
        (t/attach-instruction , char-lowercase?)
        (t/attach-instruction , char-uppercase?)
        ))

