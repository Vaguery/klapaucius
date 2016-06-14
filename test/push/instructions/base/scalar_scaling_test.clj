(ns push.instructions.base.scalar_scaling_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.type.scalar])
  )

;; fixtures

(def cljInf  Double/POSITIVE_INFINITY)
(def cljNinf Double/NEGATIVE_INFINITY)
(def cljNaN  (Math/sin Double/POSITIVE_INFINITY))



(tabular
  (fact ":scalar-lots reduces the top :scalar mod 10000"
    (register-type-and-check-instruction
      ?set-stack ?items scalar-type ?instruction ?get-stack) => ?expected)

    ?set-stack   ?items      ?instruction    ?get-stack   ?expected
    :scalar     '(32677)     :scalar-lots    :scalar       '(2677)
    :scalar     '(-22212)    :scalar-lots    :scalar       '(7788)
    :scalar     '(79)        :scalar-lots    :scalar       '(79)
    :scalar     '(0)         :scalar-lots    :scalar       '(0)

    :scalar     '(32677.5)   :scalar-lots    :scalar       '(2677.5)
    :scalar     '(-22212.5)  :scalar-lots    :scalar       '(7787.5)
    :scalar     '(79.5)      :scalar-lots    :scalar       '(79.5)
    :scalar     '(0.5)       :scalar-lots    :scalar       '(0.5)

    :scalar     '(32677/2)   :scalar-lots    :scalar       '(12677/2)
    :scalar     '(-22213/2)  :scalar-lots    :scalar       '(17787/2)
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

  ;; problematic

    ; :scalar     (list cljInf)
    ;                          :scalar-lots    :scalar       '(7772M)
    ; :scalar     (list cljNinf)
    ;                          :scalar-lots    :scalar       '(7777.77772M)



; (tabular
;   (fact ":integer-few reduces the top :integer mod 10"
;     (register-type-and-check-instruction
;       ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

;     ?set-stack   ?items    ?instruction     ?get-stack     ?expected
;     :integer     '(77)     :integer-few     :integer       '(7)
;     :integer     '(-2)     :integer-few     :integer       '(8)
;     :integer     '(8)      :integer-few     :integer       '(8)
;     :integer     '(0)      :integer-few     :integer       '(0))


; (tabular
;   (fact ":integer-some reduces the top :integer mod 100"
;     (register-type-and-check-instruction
;       ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

;     ?set-stack   ?items    ?instruction     ?get-stack     ?expected
;     :integer     '(677)    :integer-some    :integer       '(77)
;     :integer     '(-2912)  :integer-some    :integer       '(88)
;     :integer     '(79)     :integer-some    :integer       '(79)
;     :integer     '(0)      :integer-some    :integer       '(0))


; (tabular
;   (fact ":integer-many reduces the top :integer mod 1000"
;     (register-type-and-check-instruction
;       ?set-stack ?items integer-type ?instruction ?get-stack) => ?expected)

;     ?set-stack   ?items    ?instruction     ?get-stack     ?expected
;     :integer     '(2677)    :integer-many    :integer       '(677)
;     :integer     '(-22212)  :integer-many    :integer       '(788)
;     :integer     '(79)      :integer-many    :integer       '(79)
;     :integer     '(0)       :integer-many    :integer       '(0))
