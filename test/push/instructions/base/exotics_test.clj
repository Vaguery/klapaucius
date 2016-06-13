(ns push.instructions.base.exotics_test
  (:require [push.interpreter.core :as i])
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.util.exotics])
  (:use [push.types.type.scalar])
  (:use [push.instructions.library.exotic])
  )



(fact "rewrite-digits"
  (rewrite-digits 12345 3) => 69208
  (rewrite-digits 63119988 3) => 5196527
  (rewrite-digits 8 1) => 8
  (rewrite-digits 8 2) => 6
  (rewrite-digits 8 3) => 4
  (rewrite-digits -63119988 3) => -5196527
  (rewrite-digits -50000 3) => -50055
  (rewrite-digits Long/MIN_VALUE 3) => -3783295979768903679N) ;; not pushed!



(tabular
  (fact ":integer-totalistic3 does some weird stuff"
    (register-type-and-check-instruction
        ?set-stack ?items exotic-module ?instruction ?get-stack) => ?expected)

    ?set-stack  ?items    ?instruction             ?get-stack     ?expected
    :scalar     '(1182)     :integer-totalistic3   :scalar       '(114)
    :scalar     '(-39812M)  :integer-totalistic3   :scalar       '(-8164)
    :scalar     '(235235235)       
                            :integer-totalistic3   :scalar       '(0)
    :scalar     (list Long/MIN_VALUE)       
                            :integer-totalistic3   :scalar       '(-3783295979768903679)
    :scalar     '(123456788161617/826317623) 
                            :integer-totalistic3   :scalar       '(473960)
    :scalar     '(-123456788161617.826317623) 
                            :integer-totalistic3   :scalar       '(-692581375838490)
    )