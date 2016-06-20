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
  (scalar-to-index 9.1 2) => 0
  (scalar-to-index 9.9 2) => 0
  (scalar-to-index 10.9 2) => 1

  (scalar-to-index -9 2) => 1
  (scalar-to-index -9.1 2) => 1
  (scalar-to-index -10.9 2) => 0

  (scalar-to-index 0 99) => 0
  (scalar-to-index 1/3 99) => 1
  (scalar-to-index 97/3 99) => 33
  (scalar-to-index 100/3 99) => 34
  (scalar-to-index 199/2 99) => 1
  (scalar-to-index -199/2 99) => 0
  (scalar-to-index -97/3 99) => 67
  (scalar-to-index -100/3 99) => 66
  (scalar-to-index -1/3 99) => 0

  (scalar-to-index 1.0M 99) => 1
  (scalar-to-index 1.1M 99) => 2
  (scalar-to-index 2.1M 99) => 3
  (scalar-to-index 99.1M 99) => 1
  (scalar-to-index 99M 99) => 0
  )



(fact "within-1?"
  (within-1? 9 2) => false
  (within-1? 1.9 1.9) => true
  (within-1? 1.9 2.9) => true
  (within-1? 1 3) => false
  (within-1? Math/PI 22/7) => true
  )



(future-fact "within-1? doesn't blow up with BigDecimal"
  (within-1? 9M 28/3) =not=> throws
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



(fact "downsample-bigdec"
  (downsample-bigdec 9M) => 9
  (downsample-bigdec 9.1M) => 9.1
  (downsample-bigdec 9000000000000000000000000000000000000000000000.1M) => 9.0E45
  (downsample-bigdec 9000000000000000000000000000000000000000000000.0M) => 9.0E45
  (downsample-bigdec -9000000000000000000000000000000000000000000000.0M) => -9.0E45
  )

