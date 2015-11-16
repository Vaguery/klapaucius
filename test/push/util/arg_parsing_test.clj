(ns push.util.arg-parsing-test
  (:use midje.sweet)
  (:use push.util.general)
  )


(fact "`extract-keyword-argument` returns the value of keyworded args"
  (extract-keyword-argument :foo '(7 :foo 8 :bar 9)) => 8
  (extract-keyword-argument :bar '(7 :foo 8 :bar 9 10 11 12)) => 9
  (extract-keyword-argument :baz '(7 :foo 8 :bar 9 10 11 12)) => nil)


(fact "detailed check of working on sets"
  (extract-keyword-argument :foo '('x :foo #{1 2} :bar 7 8 9 10)) => #{1 2})


(fact "`extract-keyword-argument` works for empty arg lists"
  (extract-keyword-argument :foo '()) => nil)


(fact "`extract-docstring` returns the value of the first string, if present"
  (extract-docstring '('foo "hey me!" :foo 8 :bar 9)) => "hey me!")


(fact "`extract-docstring` returns nil if there's no string"
  (extract-docstring '('foo :foo 8 :bar 9)) => nil)


(fact "`extract-splat-argument` returns everything left after the keyword args have been trimmed"
    (extract-splat-argument '(7 :foo 8 :bar 9 10 11 12)) => '(10 11 12)
    (extract-splat-argument '(7 :foo 8 :bar 9 (10 11) [12])) => '((10 11) [12]))


(fact "`extract-splat-argument` works when there aren't any"
  (extract-splat-argument '()) => '()
  (extract-splat-argument '(7 :foo 8 :bar 9)) => '())


(fact "`extract-splat-argument` works that's all there is"
  (extract-splat-argument '(8 1 2 11)) => '(8 1 2 11))

(fact "`extract-splat-argument` works when there's a docstring"
  (extract-splat-argument '("foo" 8 1 2 11)) => '(8 1 2 11))
