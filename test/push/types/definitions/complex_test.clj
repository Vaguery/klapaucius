(ns push.types.definitions.complex_test
  (:use midje.sweet)
  (:use [push.types.definitions.complex])
  )

(fact "complex? returns true if the item is a Complex record"
  (complex? 88) => false
  (complex? (->Complex 8 2)) => true
  )


(fact "complexify returns a new Complex record with the argument as its :re component and :im 0"
  (complexify 1/3) => (->Complex 1/3 0)
  )


(fact "complex-sum returns the sum of two Complex records"
  (complex-sum (->Complex 1/3 0.4) (->Complex 22 1M)) => 
    (->Complex 67/3 1.4)
  )


(fact "complex-diff returns the sum of two Complex records"
  (complex-diff (->Complex 1/3 0.4) (->Complex 22 1M)) => 
    (->Complex -65/3 -0.6)
  )


(fact "complex-product returns the product of two Complex records"
  (complex-product (->Complex 1/3 0.4) (->Complex 22 1)) => 
    (->Complex 6.933333333333333 9.133333333333335)
  (complex-product (->Complex 1/3 2/5) (->Complex 22 1)) => 
    (->Complex 104/15 137/15)
  (complex-product (->Complex 1M 2M) (->Complex 3M 4M)) => 
    (->Complex -5M 10M)
  (complex-product (->Complex 1M 2M) (->Complex 1/3 1/7)) => 
    (->Complex -5M 10M)
  )