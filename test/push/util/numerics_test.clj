(ns push.util.numerics-test
  (:use midje.sweet)
  (:use push.util.numerics))



(fact "pN applies `with-precision` to an arbitrary thing"
  (* 99M 1/3) => (throws)
  (with-precision 100 (* 99M 1/3)) => 33.000M
  (pN (* 99M 1/3)) => 33.00M
  (pN (* 99M 1/3)) => 33.00M
  (pN 1/3) => 1/3
  (pN 3M) => 3M
  )



(fact "scalar-to-index"
  (scalar-to-index 9 2) => 1
  (scalar-to-index 9.1 2) => 1.0
  (scalar-to-index 9.9 2) => 1.0
  (scalar-to-index 10.9 2) => 0.0

  (scalar-to-index -9 2) => 1
  (scalar-to-index -9.1 2) => 0.0
  (scalar-to-index -10.9 2) => 1.0

  (scalar-to-index 0 99) => 0
  (scalar-to-index 1/3 99) => 0N
  (scalar-to-index 97/3 99) => 32N
  (scalar-to-index 100/3 99) => 33N
  (scalar-to-index 199/2 99) => 0N
  (scalar-to-index -199/2 99) => 98N
  (scalar-to-index -97/3 99) => 66N
  (scalar-to-index -100/3 99) => 65N
  (scalar-to-index -1/3 99) => 98N

  (scalar-to-index 1.0M 99) => 1
  (scalar-to-index 1.1M 99) => 1N
  (scalar-to-index 2.1M 99) => 2N
  (scalar-to-index 99.1M 99) => 0N
  (scalar-to-index 99M 99) => 0
  )



(fact "within-1?"
  (within-1? 9 2) => false
  (within-1? 1.9 1.9) => true
  (within-1? 1.9 2.9) => true
  (within-1? 1 3) => false
  (within-1? Math/PI 22/7) => true
  )



(future "within-1? blows up with BigDecimal"
  (within-1? 9M 28/3) => throws
)



(fact "integerish?"
  (integerish? 8) => true
  (integerish? 8M) => true
  (integerish? 899998273948234276348762384768273648276384762843N) => true
  (integerish? -899998273948234276348762384768273648276384762843N) => true
  (integerish? 899998273948234276348762384768273648276384762843.1M) => false
  (integerish? 8.0) => true
  (integerish? 1e+7) => true
  (integerish? 827198729347628734678263862834768237648276348726834762M) => true
  )



(fact "infinite?"
  (infinite? 88) => false
  (infinite? ∞) => true
  (infinite? -∞) => true
  )


(fact "infty?"
  (infty? 88) => false
  (infty? ∞) => true
  (infty? -∞) => false
  (infty? (/ 1.0 0.0)) => true
  )


(fact "ninfty?"
  (ninfty? 88) => false
  (ninfty? ∞) => false
  (ninfty? -∞) => true
  (ninfty? (/ -1.0 0.0)) => true
  )

