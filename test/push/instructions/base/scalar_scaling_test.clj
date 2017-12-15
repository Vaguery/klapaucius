(ns push.instructions.base.scalar_scaling_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.type.item.scalar])
  (:require [push.util.numerics :as num :refer [∞,-∞]])
  )

;; fixtures

(def cljNaN  (Math/sin num/∞))
(def maxDouble  (Double/MAX_VALUE))



(tabular
  (fact ":scalar-few reduces the top :scalar rem 10"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-few     :exec         '(7)
    :scalar     '(-22212)    :scalar-few     :exec         '(-2)
    :scalar     '(79)        :scalar-few     :exec         '(9)
    :scalar     '(0)         :scalar-few     :exec         '(0)

    :scalar     '(32677.5)   :scalar-few     :exec         '(7.5)
    :scalar     '(-22212.5)  :scalar-few     :exec         '(-2.5)
    :scalar     '(79.5)      :scalar-few     :exec         '(9.5)
    :scalar     '(0.5)       :scalar-few     :exec         '(0.5)

    :scalar     '(32677/2)   :scalar-few     :exec         '(17/2)
    :scalar     '(-22213/2)  :scalar-few     :exec         '(-13/2)
    :scalar     '(79/2)      :scalar-few     :exec         '(19/2)
    :scalar     '(0/2)       :scalar-few     :exec         '(0)

    :scalar     '(377777777772M)
                             :scalar-few     :exec         '(2M)
    :scalar     '(3777777.77772M)
                             :scalar-few     :exec         '(7.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-few     :exec         '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-few     :exec         '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-few     :exec         '(7)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-few     :exec         '(0N)
    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-few     :exec         '(1N)
    )


(tabular
  (fact ":scalar-few produces an :error when the arg is infinite or NaN"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items        ?instruction    ?get-stack   ?expected
    :scalar     (list num/∞)   :scalar-few     :exec         '()
    :scalar     (list num/∞)   :scalar-few     :error        '({:item "Infinite or NaN", :step 0})
    :scalar     (list cljNaN)  :scalar-few     :exec         '()
    :scalar     (list cljNaN)  :scalar-few     :error        '({:item "Infinite or NaN", :step 0})
    )


(tabular
  (fact ":scalar-lots reduces the top :scalar rem 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-lots    :exec         '(2677)
    :scalar     '(-22212)    :scalar-lots    :exec         '(-2212)
    :scalar     '(79)        :scalar-lots    :exec         '(79)
    :scalar     '(0)         :scalar-lots    :exec         '(0)

    :scalar     '(32677.5)   :scalar-lots    :exec         '(2677.5)
    :scalar     '(-22212.5)  :scalar-lots    :exec         '(-2212.5)
    :scalar     '(79.5)      :scalar-lots    :exec         '(79.5)
    :scalar     '(0.5)       :scalar-lots    :exec         '(0.5)

    :scalar     '(32677/2)   :scalar-lots    :exec         '(12677/2)
    :scalar     '(-22213/2)  :scalar-lots    :exec         '(-2213/2)
    :scalar     '(79/2)      :scalar-lots    :exec         '(79/2)
    :scalar     '(0/2)       :scalar-lots    :exec         '(0)

    :scalar     '(377777777772M)
                             :scalar-lots    :exec         '(7772M)
    :scalar     '(3777777.77772M)
                             :scalar-lots    :exec         '(7777.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-lots    :exec         '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-lots    :exec         '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-lots    :exec         '(5807)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-lots    :exec         '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-lots    :exec         '(1N)
    )




(tabular
  (fact ":scalar-lots creates `:error` results with big or infinite arguments"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items         ?instruction    ?get-stack   ?expected
    :scalar     (list cljNaN)    :scalar-lots    :exec      '()
    :scalar     (list num/∞)     :scalar-lots    :exec      '()
    :scalar     (list num/∞)     :scalar-lots    :error     '({:item "Infinite or NaN", :step 0})
    :scalar     (list num/-∞)    :scalar-lots    :exec      '()
    :scalar     (list num/-∞)    :scalar-lots    :error     '({:item "Infinite or NaN", :step 0})
    )



(tabular
  (fact ":scalar-many reduces the top :scalar mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-many    :exec         '(677)
    :scalar     '(-22212)    :scalar-many    :exec         '(-212)
    :scalar     '(79)        :scalar-many    :exec         '(79)
    :scalar     '(0)         :scalar-many    :exec         '(0)

    :scalar     '(32677.5)   :scalar-many    :exec         '(677.5)
    :scalar     '(-22212.5)  :scalar-many    :exec         '(-212.5)
    :scalar     '(79.5)      :scalar-many    :exec         '(79.5)
    :scalar     '(0.5)       :scalar-many    :exec         '(0.5)

    :scalar     '(32677/2)   :scalar-many    :exec         '(677/2)
    :scalar     '(-22213/2)  :scalar-many    :exec         '(-213/2)
    :scalar     '(79/2)      :scalar-many    :exec         '(79/2)
    :scalar     '(0/2)       :scalar-many    :exec         '(0)

    :scalar     '(377777777772M)
                             :scalar-many    :exec         '(772M)
    :scalar     '(3777777.77772M)
                             :scalar-many    :exec         '(777.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-many    :exec         '(0N)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-many    :exec         '(807)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-many    :exec         '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-many    :exec         '(1N)
    )




(tabular
  (fact ":scalar-many creates appropriate results with infinite arguments"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     (list num/∞)     :scalar-many    :exec         '()
    :scalar     (list num/∞)     :scalar-many    :error        '({:item "Infinite or NaN",
                                                              :step 0})
    :scalar     (list cljNaN)    :scalar-many    :exec         '()
    :scalar     (list cljNaN)    :scalar-many    :error        '({:item "Infinite or NaN", :step 0})
    )



(tabular
  (future-fact ":scalar-many is not susceptible to the Clojure modulo bug"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     (list maxDouble)
                             :scalar-many    :scalar       '(:literally-I-don't-know)
  )


(tabular
  (fact ":scalar-bunch reduces the top :scalar mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-bunch    :exec         '(77)
    :scalar     '(-22212)    :scalar-bunch    :exec         '(-12)
    :scalar     '(79)        :scalar-bunch    :exec         '(79)
    :scalar     '(0)         :scalar-bunch    :exec         '(0)

    :scalar     '(32677.5)   :scalar-bunch    :exec         '(77.5)
    :scalar     '(-22212.5)  :scalar-bunch    :exec         '(-12.5)
    :scalar     '(79.5)      :scalar-bunch    :exec         '(79.5)
    :scalar     '(0.5)       :scalar-bunch    :exec         '(0.5)

    :scalar     '(32677/2)   :scalar-bunch    :exec         '(77/2)
    :scalar     '(-22213/2)  :scalar-bunch    :exec         '(-13/2)
    :scalar     '(79/2)      :scalar-bunch    :exec         '(79/2)
    :scalar     '(0/2)       :scalar-bunch    :exec         '(0)

    :scalar     '(377777777772M)
                             :scalar-bunch    :exec         '(72M)
    :scalar     '(3777777.77772M)
                             :scalar-bunch    :exec         '(77.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-bunch    :exec         '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-bunch    :exec         '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-bunch    :exec         '(7)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-bunch    :exec         '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-bunch    :exec         '(1N)
    )


(tabular
  (fact ":scalar-bunch creates appropriate results with infinite arguments"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction       ?get-stack   ?expected
    :scalar     (list num/∞)     :scalar-bunch    :exec         '()
    :scalar     (list num/∞)     :scalar-bunch    :error        '({:item "Infinite or NaN",
                                                              :step 0})
    :scalar     (list cljNaN)    :scalar-bunch    :exec         '()
    :scalar     (list cljNaN)    :scalar-bunch    :error        '({:item "Infinite or NaN", :step 0})
    )
