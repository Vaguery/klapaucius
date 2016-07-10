(ns push.type.definitions.complex_test
  (:use midje.sweet)
  (:use [push.type.definitions.complex])
  (:use [push.util.numerics :only [∞,-∞]])
  )


(fact "complex? returns true if the item is a Complex record"
  (complex? 88) => false
  (complex? (->Complex 8 2)) => true
  )


(fact "complexify returns a new Complex record with the argument as its :re component and :im 0"
  (complexify 1/3) => (->Complex 1/3 0)
  )



(fact "conjugate returns the conjugate of a Complex record"
  (conjugate (->Complex 1/3 0.4)) => (->Complex 1/3 -0.4)
  )



(fact "complex-sum returns the sum of two Complex records, or throws a Non-terminating decimal expansion exception"
  (complex-sum (->Complex 1/3 0.4) (->Complex 22 1M)) => 
    (->Complex 67/3 1.4)
  (complex-sum (->Complex 1/3 0.4) (->Complex 22M 1M)) => 
   (throws #"Non-terminating decimal expansion")
  )



(fact "complex-diff returns the sum of two Complex records, or throws a Non-terminating decimal expansion exception"
  (complex-diff (->Complex 1/3 0.4) (->Complex 22 1M)) => 
    (->Complex -65/3 -0.6)
  (complex-diff (->Complex 1/3 0.4) (->Complex 22M 1M)) => 
    (throws #"Non-terminating decimal expansion")
  )



(fact "complex-product returns the product of two Complex records, or throws a Non-terminating decimal expansion exception"
  (complex-product (->Complex 1/3 0.4) (->Complex 22 1)) => 
    (->Complex 6.933333333333333 9.133333333333335)
  (complex-product (->Complex 1/3 2/5) (->Complex 22 1)) => 
    (->Complex 104/15 137/15)
  (complex-product (->Complex 1M 2M) (->Complex 3M 4M)) => 
    (->Complex -5M 10M)
  (complex-product (->Complex 1M 2M) (->Complex 1/3 1/7)) => 
    (throws #"Non-terminating decimal expansion")
  )



(fact "complex-quotient returns the quotient of two Complex records, or throws a Non-terminating decimal expansion exception"
  (complex-quotient (->Complex 3 2) (->Complex 3 2)) => (->Complex 1 0)
  (complex-quotient (->Complex 3 2) (->Complex 4 6)) => (->Complex 6/13 -5/26)
  (complex-quotient (->Complex 10 -10) (->Complex 5 -5)) => (->Complex 2 0)
  (complex-quotient (->Complex 1/3 -10) (->Complex 3 1/7)) =>
    (->Complex -21/442 -4417/1326)
  (complex-quotient (->Complex 10M -10M) (->Complex 5 -5)) => (->Complex 2M 0M)
  (complex-quotient (->Complex 1.2M 3.4M) (->Complex 1/2 1/7)) =>
    (throws #"Non-terminating decimal expansion")
  (complex-quotient (->Complex 10M -10M) (->Complex 1/3 -5)) =>
    (throws #"Non-terminating decimal expansion")
  )


(fact "complex-quotient does stuff when dividing by 0"
  (complex-quotient (->Complex 3 2) (->Complex 0 0)) => (throws #"Divide by zero")
)



(fact "complex-infinite? returns true if either real or imaginary part is positive or negative infinity"
  (complex-infinite? (->Complex ∞ 9)) => true
  (complex-infinite? (->Complex 2 2)) => false
  (complex-infinite? (->Complex 3 ∞)) => true
  (complex-infinite? (->Complex -∞ ∞)) => true
  )


