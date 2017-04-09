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
  (scalar-to-index 10.9 2) => 1.0

  (scalar-to-index -9 2) => 1
  (scalar-to-index -9.1 2) => 1.0
  (scalar-to-index -10.9 2) => 0

  (scalar-to-index -1.1 5) => 4.0
  (scalar-to-index -0.1 5) => 0
  (scalar-to-index 0 5) => 0
  (scalar-to-index 0.1 5) => 1.0
  (scalar-to-index 1.1 5) => 2.0
  (scalar-to-index 2.1 5) => 3.0
  (scalar-to-index 3.1 5) => 4.0
  (scalar-to-index 4.1 5) => 0
  (scalar-to-index 5.1 5) => 1.0
  (scalar-to-index 6.1 5) => 2.0
  (scalar-to-index 7.1 5) => 3.0


  (scalar-to-index 0 99) => 0
  (scalar-to-index 1/3 99) => 1N
  (scalar-to-index 97/3 99) => 33N
  (scalar-to-index 100/3 99) => 34N
  (scalar-to-index 199/2 99) => 1N
  (scalar-to-index -199/2 99) => 0
  (scalar-to-index -97/3 99) => 67N
  (scalar-to-index -100/3 99) => 66N
  (scalar-to-index -1/3 99) => 0

  (scalar-to-index 1.0M 99) => 1
  (scalar-to-index 1.1M 99) => 2N
  (scalar-to-index 2.1M 99) => 3N
  (scalar-to-index 99.1M 99) => 1N
  (scalar-to-index 99M 99) => 0


  (scalar-to-index ∞ 99) => 0
  (scalar-to-index -∞ 99) => 0

  (scalar-to-index (clojure.math.numeric-tower/sqrt -99) 66) => 0
  (scalar-to-index (clojure.math.numeric-tower/sqrt -99) 66) => 0
  (scalar-to-index (/ ∞ ∞) 99) => 0
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


(fact "few"
  (few 8.25) => 8.25
  (few 18.25) => 8.25
  (few 0) => 0
  (few -1112018.25) => -8.25
  (few -888.25) => -8.25
  (few 77172/7) => 32/7
  (few -77172/7) => -32/7
  )

(fact "bunch"
  (bunch 8.25) => 8.25
  (bunch 18.25) => 18.25
  (bunch 0) => 0
  (bunch -1112018.25) => -18.25
  (bunch -888.25) => -88.25
  (bunch 77172/7) => 172/7
  (bunch -77172/7) => -172/7
  )

(fact "many"
  (many 8.25) => 8.25
  (many 18.25) => 18.25
  (many 0) => 0
  (many -1112018.25) => -18.25
  (many -888.25) => -888.25
  (many 111177172/7) => 3172/7
  (many -111177172/7) => -3172/7
  )

(fact "lots"
  (lots 8.25) => 8.25
  (lots 18.25) => 18.25
  (lots 0) => 0
  (lots -1112018.25) => -2018.25
  (lots -888.25) => -888.25
  (lots 111177172/7) => 17172/7
  (lots -111177172/7) => -17172/7
  )
