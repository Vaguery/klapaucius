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
    :scalar     '(32677)     :scalar-few     :scalar       '(7)
    :scalar     '(-22212)    :scalar-few     :scalar       '(-2)
    :scalar     '(79)        :scalar-few     :scalar       '(9)
    :scalar     '(0)         :scalar-few     :scalar       '(0)

    :scalar     '(32677.5)   :scalar-few     :scalar       '(7.5)
    :scalar     '(-22212.5)  :scalar-few     :scalar       '(-2.5)
    :scalar     '(79.5)      :scalar-few     :scalar       '(9.5)
    :scalar     '(0.5)       :scalar-few     :scalar       '(0.5)

    :scalar     '(32677/2)   :scalar-few     :scalar       '(17/2)
    :scalar     '(-22213/2)  :scalar-few     :scalar       '(-13/2)
    :scalar     '(79/2)      :scalar-few     :scalar       '(19/2)
    :scalar     '(0/2)       :scalar-few     :scalar       '(0)

    :scalar     '(377777777772M)
                             :scalar-few     :scalar       '(2M)
    :scalar     '(3777777.77772M)
                             :scalar-few     :scalar       '(7.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-few     :scalar       '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-few     :scalar       '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-few     :scalar       '(7)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-few     :scalar       '(0N)
    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-few     :scalar       '(1N)
    )


(tabular
  (fact ":scalar-few produces an :error when the arg is infinite or NaN"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items        ?instruction    ?get-stack   ?expected
    :scalar     (list num/∞)   :scalar-few     :scalar       '()
    :scalar     (list num/∞)   :scalar-few     :error        '({:item "Infinite or NaN", :step 0})
    :scalar     (list cljNaN)  :scalar-few     :scalar       '()
    :scalar     (list cljNaN)  :scalar-few     :error        '({:item "Infinite or NaN", :step 0})
    )


(tabular
  (fact ":scalar-lots reduces the top :scalar rem 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-lots    :scalar       '(2677)
    :scalar     '(-22212)    :scalar-lots    :scalar       '(-2212)
    :scalar     '(79)        :scalar-lots    :scalar       '(79)
    :scalar     '(0)         :scalar-lots    :scalar       '(0)

    :scalar     '(32677.5)   :scalar-lots    :scalar       '(2677.5)
    :scalar     '(-22212.5)  :scalar-lots    :scalar       '(-2212.5)
    :scalar     '(79.5)      :scalar-lots    :scalar       '(79.5)
    :scalar     '(0.5)       :scalar-lots    :scalar       '(0.5)

    :scalar     '(32677/2)   :scalar-lots    :scalar       '(12677/2)
    :scalar     '(-22213/2)  :scalar-lots    :scalar       '(-2213/2)
    :scalar     '(79/2)      :scalar-lots    :scalar       '(79/2)
    :scalar     '(0/2)       :scalar-lots    :scalar       '(0)

    :scalar     '(377777777772M)
                             :scalar-lots    :scalar       '(7772M)
    :scalar     '(3777777.77772M)
                             :scalar-lots    :scalar       '(7777.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-lots    :scalar       '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-lots    :scalar       '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-lots    :scalar       '(5807)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-lots    :scalar       '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-lots    :scalar       '(1N)
    )




(tabular
  (fact ":scalar-lots creates `:error` results with big or infinite arguments"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items         ?instruction    ?get-stack   ?expected
    :scalar     (list cljNaN)    :scalar-lots    :scalar    '()
    :scalar     (list num/∞)     :scalar-lots    :scalar    '()
    :scalar     (list num/∞)     :scalar-lots    :error     '({:item "Infinite or NaN", :step 0})
    :scalar     (list num/-∞)    :scalar-lots    :scalar    '()
    :scalar     (list num/-∞)    :scalar-lots    :error     '({:item "Infinite or NaN", :step 0})
    )



(tabular
  (fact ":scalar-many reduces the top :scalar mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-many    :scalar       '(677)
    :scalar     '(-22212)    :scalar-many    :scalar       '(788)
    :scalar     '(79)        :scalar-many    :scalar       '(79)
    :scalar     '(0)         :scalar-many    :scalar       '(0)

    :scalar     '(32677.5)   :scalar-many    :scalar       '(677.5)
    :scalar     '(-22212.5)  :scalar-many    :scalar       '(787.5)
    :scalar     '(79.5)      :scalar-many    :scalar       '(79.5)
    :scalar     '(0.5)       :scalar-many    :scalar       '(0.5)

    :scalar     '(32677/2)   :scalar-many    :scalar       '(677/2)
    :scalar     '(-22213/2)  :scalar-many    :scalar       '(1787/2)
    :scalar     '(79/2)      :scalar-many    :scalar       '(79/2)
    :scalar     '(0/2)       :scalar-many    :scalar       '(0)

    :scalar     '(377777777772M)
                             :scalar-many    :scalar       '(772M)
    :scalar     '(3777777.77772M)
                             :scalar-many    :scalar       '(777.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-many    :scalar       '(0N)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-many    :scalar       '(807)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-many    :scalar       '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-many    :scalar       '(1N)
    )




(tabular
  (fact ":scalar-many creates appropriate results with infinite arguments"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     (list num/∞)     :scalar-many    :scalar       '()
    :scalar     (list num/∞)     :scalar-many    :error        '({:item "Infinite or NaN",
                                                              :step 0})
    :scalar     (list num/-∞)    :scalar-many    :scalar       '()
    :scalar     (list num/-∞)    :scalar-many    :error        '({:item "Infinite or NaN",
                                                              :step 0})
    )



(tabular
  (future-fact ":scalar-many is not susceptible to the Clojure modulo bug"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     (list maxDouble)
                             :scalar-many    :scalar       (:literally-I-don't-know)
  )




(tabular
  (fact ":scalar-some reduces the top :scalar mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-some    :scalar       '(77)
    :scalar     '(-22212)    :scalar-some    :scalar       '(88)
    :scalar     '(79)        :scalar-some    :scalar       '(79)
    :scalar     '(0)         :scalar-some    :scalar       '(0)

    :scalar     '(32677.5)   :scalar-some    :scalar       '(77.5)
    :scalar     '(-22212.5)  :scalar-some    :scalar       '(87.5)
    :scalar     '(79.5)      :scalar-some    :scalar       '(79.5)
    :scalar     '(0.5)       :scalar-some    :scalar       '(0.5)

    :scalar     '(32677/2)   :scalar-some    :scalar       '(77/2)
    :scalar     '(-22213/2)  :scalar-some    :scalar       '(187/2)
    :scalar     '(79/2)      :scalar-some    :scalar       '(79/2)
    :scalar     '(0/2)       :scalar-some    :scalar       '(0)

    :scalar     '(377777777772M)
                             :scalar-some    :scalar       '(72M)
    :scalar     '(3777777.77772M)
                             :scalar-some    :scalar       '(77.77772M)
    :scalar     (list (bigint Double/MAX_VALUE))
                             :scalar-some    :scalar       '(0N)
    :scalar     (list Double/MAX_VALUE)
                             :scalar-some    :scalar       '(0.0)
    :scalar     (list Long/MAX_VALUE)
                             :scalar-some    :scalar       '(7)
    :scalar     (list (bigint Double/MIN_VALUE))
                             :scalar-some    :scalar       '(0N)

    :scalar     (list (inc (bigint 1e872M)))
                             :scalar-some    :scalar       '(1N)
    )
