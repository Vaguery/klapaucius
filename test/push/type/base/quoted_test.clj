(ns push.type.base.quoted_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.type-checkers])
  (:use [push.type.definitions.quoted])
  (:use [push.type.item.quoted])
  )

(fact "quoted-code? returns true if the item is a QuotedCode record"
  (quoted-code? 88) => false
  (quoted-code? (push-quote 88)) => true
  )


(fact "push-quote creates a new QuotedCode record with the given value"
  (:value (push-quote 88)) => 88
  (:value (push-quote nil)) => nil
  (:value (push-quote (push-quote 88))) => (push-quote 88)
  (:value (push-quote '())) => '())
