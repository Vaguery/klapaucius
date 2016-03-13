(ns push.types.base.string_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.string])
  )


(fact "string-type has :name ':string'"
  (:name string-type) => :string)


(fact "string-type has the correct :recognizer"
  (:recognizer string-type) => (exactly string?))


(fact "string-type has the expected :attributes"
  (:attributes string-type) =>
    (contains #{:equatable :comparable :movable :string :visible}))


(fact "string-type knows the :equatable instructions"
  (keys (:instructions string-type)) =>
    (contains [:string-equal? :string-notequal?] :in-any-order :gaps-ok))


(fact "string-type knows the :visible instructions"
  (keys (:instructions string-type)) =>
    (contains [:string-stackdepth :string-empty?] :in-any-order :gaps-ok))


(fact "string-type knows the :movable instructions"
  (keys (:instructions string-type)) =>
    (contains [:string-shove :string-pop :string-dup :string-rotate :string-yank :string-yankdup :string-flush :string-swap] :in-any-order :gaps-ok))


(fact "string-type knows the :printable instructions"
  (keys (:instructions string-type)) => (contains [:string-print]))


(fact "string-type knows the :returnable instructions"
  (keys (:instructions string-type)) => (contains [:string-return]))


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

  (re-pattern "́\\") => (throws #"Unexpected internal error")
  (re-pattern (str-to-pattern "́\\")) => #"́\\"
)

(fact "`explosive-replacement?` checks for more patterns after replacement than before"
  (explosive-replacement? "abc" "abcabc" "ab") => true
  (explosive-replacement? "abc" "abcabc" "x") => false
  (explosive-replacement? "abc" "aabbcc" "abc") => false
  (explosive-replacement? "aaa" "aaaa" "a") => true
  (explosive-replacement? "aaa" "aaa" "a") => false
  (explosive-replacement? "aaa" "aa" "a") => false
  )
