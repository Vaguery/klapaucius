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



(fact "safe-add"
  (safe-add 9 10) => 19
  (safe-add 9 1/3) => 28/3
  (safe-add 9M 0.3) => 9.3
  (safe-add 9M 1/3) => 28/3
  (safe-add 9.8M 1/3) => (+ 9.8 1/3)

  ;; examples from bugs
  (safe-add  789266345N 52478280M) => 841744625M

  (safe-add 9 "nope!") => (throws)
  )



(fact "safe-diff"
  (safe-diff 9 10) => -1
  (safe-diff 9 1/3) => 26/3
  (safe-diff 9M 0.3) => 8.7
  (safe-diff 9M 1/3) => 26/3
  (safe-diff 9.8M 1/3) => (- 9.8 1/3)

  (safe-diff 9 "nope!") => (throws)
  )



(fact "safe-times"
  (safe-times 9 10) => 90
  (safe-times 9 1/3) => 3
  (safe-times 9M 0.3) => (* 9M 0.3)
  (safe-times 9M 1/3) => 3N
  (safe-times 9.8M 1/3) => (* 9.8 1/3)

  (safe-times 9 "nope!") => (throws)
  )


(fact "safe-quotient"
  (safe-quotient 9 10) => 9/10
  (safe-quotient 9 1/3) => 27N
  (safe-quotient 9M 0.3) => (/ 9M 0.3)
  (safe-quotient 9M 1/3) => 27N
  (safe-quotient 10M 13/4) => 40/13
  (safe-quotient 9.8M 1/3) => (/ 9.8 1/3)
  (safe-quotient 92 0) => (throws #"Divide by")

  (safe-quotient 9.8M 0) => (throws #"Divide by")       ;; passes it on
  (safe-quotient 9 "nope!") => (throws)                 ;; passes it on
  )



(fact "safe-modulo"
  (safe-modulo 9 10) => 9
  (safe-modulo 9 13/3) => 1/3
  (safe-modulo 9M 0.7) => (mod 9M 0.7)
  (safe-modulo 9M 13/3) => 1/3
  (safe-modulo 10M 13/7) => 5/7
  (safe-modulo 9.8M 1/3) => (mod 9.8 1/3)
  (safe-modulo 92 0) => (throws #"Divide by")
  (safe-modulo 9.8M 0) => (throws #"Divide by")       ;; passes it on
  (safe-modulo 9 "nope!") => (throws)                 ;; passes it on
  )