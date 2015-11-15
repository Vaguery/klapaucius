(ns push.util.arg-parsing-test
  (:use midje.sweet)
  (:use push.util.general)
  )


(fact "`extract-keyword-argument` returns the value of keyworded args"
  (extract-keyword-argument :foo '(7 :foo 8 :bar 9)) => 8
  (extract-keyword-argument :bar '(7 :foo 8 :bar 9 10 11 12)) => 9
  (extract-keyword-argument :baz '(7 :foo 8 :bar 9 10 11 12)) => nil)


(fact "detailed check of working on sets"
  (extract-keyword-argument :foo '('x :foo #{1 2} :bar 7 8 9 10)) => #{1 2}
  )


(fact "`extract-keyword-argument` works for empty arg lists"
  (extract-keyword-argument :foo '()) => nil)




(fact "`extract-splat-argument` returns everything left after the keyword args have been trimmed"
    (extract-splat-argument '(7 :foo 8 :bar 9 10 11 12)) => '(10 11 12)
    (extract-splat-argument '(7 :foo 8 :bar 9 (10 11) [12])) => '((10 11) [12]))


(fact "`extract-splat-argument` works when there aren't any"
  (extract-splat-argument '()) => '()
  (extract-splat-argument '(7 :foo 8 :bar 9)) => '())


(fact "`extract-splat-argument` works that's all there is"
  (extract-splat-argument '(8 1 2 11)) => '(8 1 2 11))
