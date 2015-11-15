(ns push.types.base.string_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.base.string])
  )


(fact "classic-string-type has :name ':string'"
  (:name classic-string-type) => :string)


(fact "classic-string-type has the correct :recognizer"
  (:recognizer classic-string-type) => (exactly string?))


(fact "classic-string-type has the expected :attributes"
  (:attributes classic-string-type) =>
    (contains #{:equatable :comparable :movable :string :visible}))


(fact "classic-string-type knows the :equatable instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-equal? :string-notequal?] :in-any-order :gaps-ok))


(fact "classic-string-type knows the :visible instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-stackdepth :string-empty?] :in-any-order :gaps-ok))


(fact "classic-string-type knows the :movable instructions"
  (keys (:instructions classic-string-type)) =>
    (contains [:string-shove :string-pop :string-dup :string-rotate :string-yank :string-yankdup :string-flush :string-swap] :in-any-order :gaps-ok))


;;; utilities

(fact "I can escape a whole bunch of bad characters using `str-to-pattern`"
  (re-pattern "Ǚ(ͧȈȊȣ͵·ċ(") => (throws #"Unclosed group near")
  (re-pattern (str-to-pattern "Ǚ(ͧȈȊȣ͵·ċ(")) => #"Ǚ\(ͧȈȊȣ͵·ċ\("

  (re-pattern "ƥ{Ƀί") => (throws #"Illegal repetition near index")
  (re-pattern (str-to-pattern "ƥ{Ƀί")) => #"ƥ\{Ƀί"

  (re-pattern "ʦ͌̀ĩȌϗE̜Ɓ[ÃǶϞǼ͐÷") => (throws #"Unclosed character class near")
  (re-pattern (str-to-pattern "ʦ͌̀ĩȌϗE̜Ɓ[ÃǶϞǼ͐÷")) => #"ʦ͌̀ĩȌϗE̜Ɓ\[ÃǶϞǼ͐÷"

  (re-pattern "+̠Sʠńə˧¶˧ſǺε") => (throws #"Dangling")
  (re-pattern (str-to-pattern "+̠Sʠńə˧¶˧ſǺε")) => #"\+̠Sʠńə˧¶˧ſǺε"
)
