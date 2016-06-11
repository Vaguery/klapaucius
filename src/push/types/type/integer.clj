(ns push.types.type.integer
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.util.code-wrangling :as fix]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math]
            [push.util.exotics :as exotics]
            ))


;; SUPPORT


(defn sign
  "returns +1 if the number is strictly positive, -1 if it's strictly negative, 0 if 0"
  [i] (compare i 0))


(defn valid-push-integer?
  "checks class is java.lang.Long; used in bounds-checking"
  [i]
  (or (instance? java.lang.Long i)
      (instance? java.lang.Integer i)))


;; INSTRUCTIONS


(def integer-biggest
  (core/build-instruction
    integer-biggest
    "`:integer-biggest` returns the largest java.lang.Long value."
    :tags #{:numeric}
    (d/calculate [] #(Long/MAX_VALUE) :as :answer)
    (d/push-onto :integer :answer)))



(def integer-smallest
  (core/build-instruction
    integer-smallest
    "`:integer-smallest` returns the smallest java.lang.Long value."
    :tags #{:numeric}
    (d/calculate [] #(Long/MIN_VALUE) :as :answer)
    (d/push-onto :integer :answer)))



(def integer-abs (t/simple-1-in-1-out-instruction
  "`:integer-abs` pushes the absolute value of the top `:integer` item"
  :integer "abs" 'Math/abs))



(def integer-add
  (core/build-instruction
    integer-add
    "`:integer-add` pops the top two `:integer` items, and pushes their sum; if the result is out of bounds, then the arguments are consumed but an `:error` is created instead of the sum"
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg2)
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1 :arg2] #(+' %1 %2) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-add out of bounds") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer-bits
  (core/build-instruction
    integer-bits
    "`:integer-bits` pops the top `:integer` and pushes a list of its 'bits' (as `true` and `false` in low-to-high order. Negative numbers are made positive. The minimal binary representation is used, but always at least two bits."
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg]
      #(into '() (reverse (exotics/integer-to-truth-table (math/abs %1) 1))) :as :bits)
    (d/push-onto :exec :bits)))



(def integer-dec
  (core/build-instruction
    integer-dec
    "`:integer-dec` pops the top `:integer` item, and pushes the next-lower `:integer`; if the result is out of bounds, then the argument is consumed but an `:error` is created instead of the new value"
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(dec' %1) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-dec out of bounds") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer-digits
  (core/build-instruction
    integer-digits
    "`:integer-digits` pops the top `:integer` and pushes a list of its digits (not including negative sign) to `:exec` (so they end up back on `:integer` quickly."
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg]
      #(map (fn [d] (- (int d) 48)) 
        (filter #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9} (seq (str %1)))) :as :numbers)
    (d/push-onto :exec :numbers)))



(def integer-divide
  (core/build-instruction
    integer-divide
    "`:integer-divide` pops the top two `:integer` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0, it replaces the two `:integer` values; if not, it pushes their (integer) quotient."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %1 (/ %2 %1)) :as :quotient)
    (d/push-these-onto :integer [:replacement :quotient])
    (d/calculate [:denominator]
      #(if (zero? %1) ":integer-divide 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def integer-few
  (core/build-instruction
    integer-few
    "`:integer-few` pops the top `:integer` value, and calculates `(mod 10 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10) :as :scaled)
    (d/push-onto :integer :scaled)))



(def integer-inc
  (core/build-instruction
    integer-inc
    "`:integer-inc` pops the top `:integer` item, and pushes the next-higher `:integer`; if the result is out of bounds, then the argument is consumed but an `:error` is created instead of the new value"
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(inc' %1) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-inc out of bounds") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer-lots
  (core/build-instruction
    integer-lots
    "`:integer-lots` pops the top `:integer` value, and calculates `(mod 10000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10000) :as :scaled)
    (d/push-onto :integer :scaled)))



(def integer-many
  (core/build-instruction
    integer-many
    "`:integer-many` pops the top `:integer` value, and calculates `(mod 1000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 1000) :as :scaled)
    (d/push-onto :integer :scaled)))



(def integer-mod
  (core/build-instruction
    integer-mod
    "`:integer-mod` pops the top two `:integer` values (call them `denominator` and `numerator`, respectively). If `denominator` is 0, it replaces the two `:integer` values; if not, it pushes `(mod numerator denominator)`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :integer :as :denominator)
    (d/consume-top-of :integer :as :numerator)
    (d/calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (d/calculate [:denominator :numerator] #(fix/safe-mod %2 %1) :as :remainder)
    (d/push-these-onto :integer [:replacement :remainder])
    (d/calculate [:denominator]
      #(if (zero? %1) ":integer-mod 0 denominator" nil) :as :warning)
    (d/record-an-error :from :warning)))



(def integer-multiply
  (core/build-instruction
    integer-multiply
    "`:integer-multiply` pops the top two `:integer` items, and pushes their product; if the result is out of bounds, then the arguments are consumed but an `:error` is created instead of the sum"
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg2)
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1 :arg2] #(*' %1 %2) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-multiply out of bounds") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer-sign (t/simple-1-in-1-out-instruction
  "`:integer-sign` examines the top `:integer` item, and pushes -1 if negative, 0 if zero, and 1 if positive"
  :integer  "sign" 'sign))



(def integer-some
  (core/build-instruction
    integer-some
    "`:integer-some` pops the top `:integer` value, and calculates `(mod 100 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 100) :as :scaled)
    (d/push-onto :integer :scaled)))



(def integer-subtract
  (core/build-instruction
    integer-subtract
    "`:integer-subtract` pops the top two `:integer` items, and pushes their difference, subtracting the top from the second; if there is an overflow (too large or small) then the arguments are consumed but an `:error` is created instead of the difference"
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg2)
    (d/consume-top-of :integer :as :arg1)
    (d/calculate [:arg1 :arg2] #(-' %1 %2) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-subtract out of bounds") :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer-totalistic3
  (core/build-instruction
    integer-totalistic3
    "`:integer-totalistic3` pops the top `:integer`. Each digit is replaced by the sum of its current value and the two neighbors to the right, modulo 10, wrapping cyclically around the number. If it is negative, the result returned is still negative. If the result is out of bounds, an `:error` is returned instead of a result."
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(exotics/rewrite-digits %1 3) :as :raw)
    (d/calculate [:raw] #(valid-push-integer? %1) :as :valid)
    (d/calculate [:valid :raw] #(if %1 %2 nil) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:valid]
      #(if %1 nil ":integer-totalistic3 out of bounds") :as :warning)
    (d/record-an-error :from :warning)))



;; CONVERSIONS


(def boolean->integer
  (core/build-instruction
    boolean->integer
    "`:boolean->integer` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes 0."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 0) :as :logic)
    (d/push-onto :integer :logic)))



(def boolean->signedint
  (core/build-instruction
    boolean->signedint
    "`:boolean->signedint` pops the top `:boolean`. If it's `true`, it pushes 1; if `false`, it pushes -1."
    :tags #{:base :conversion}
    (d/consume-top-of :boolean :as :arg1)
    (d/calculate [:arg1] #(if %1 1 -1) :as :logic)
    (d/push-onto :integer :logic)))



(def char->integer
  (core/build-instruction
    char->integer
    "`:char->integer` pops the top `:char` item, and converts it to an (integer) index"
    :tags #{:base :conversion}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(long %1) :as :int)
    (d/push-onto :integer :int)))



(def float->integer
  (core/build-instruction
    float->integer
    "`:float->integer` pops the top `:float` item, and converts it to an `:integer` value; if the resulting number would be out of bounds (too large or small), an `:error` is produced instead"
    :tags #{:numeric :base :conversion}
    (d/consume-top-of :float :as :arg)
    (d/calculate [:arg] #(try
                          (long %1)
                          (catch IllegalArgumentException e :OUT_OF_BOUNDS)) :as :raw)
    (d/calculate [:raw] #(= :OUT_OF_BOUNDS %1) :as :invalid)
    (d/calculate [:invalid :raw] #(if %1 nil %2) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:invalid]
      #(if %1 ":float->integer out of bounds" nil) :as :warning)
    (d/record-an-error :from :warning)
    ))



(def integer->bits
  (core/build-instruction
    integer->bits
    "`:integer->bits` pops the top `:integer` and pushes a vector of its 'bits' (as `true` and `false` in low-to-high order) to the `:booleans` stack. Negative numbers are made positive. The minimal binary representation is used, but always at least two bits are produced."
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg]
      #(exotics/integer-to-truth-table (math/abs %1) 1) :as :bits)
    (d/push-onto :booleans :bits)))



(def integer->numerals
  (core/build-instruction
    integer->numerals
    "`:integer->numerals` pops the top `:integer` and pushes a vector of its numerals (not including negative sign or bigint decorations) to `:chars` "
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg]
      #(into [] (filter #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9} (seq (str %1)))) :as :numbers)
    (d/push-onto :chars :numbers)))



(def string->integer
  (core/build-instruction
    string->integer
    "`:string->integer` pops the top `:string` item, and applies `Long/parseLong` to attempt to convert it to a fixed-point value. If successful (that is, if no exception is raised), the result is pushed to `:integer`"
    :tags #{:conversion :base :numeric}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] 
      #(try (Long/parseLong %1) (catch NumberFormatException _ :BAD_BAD_STRING))
        :as :raw)
    (d/calculate [:raw] #(= :BAD_BAD_STRING %1) :as :invalid)
    (d/calculate [:invalid :raw] #(if %1 nil %2) :as :result)
    (d/push-onto :integer :result)
    (d/calculate [:invalid]
      #(if %1 ":string->integer failed" nil) :as :warning)
    (d/record-an-error :from :warning)
    ))


(def integer-type
  ( ->  (t/make-type  :integer
                      :recognized-by valid-push-integer?
                      :attributes #{:numeric})
        aspects/make-comparable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
        (t/attach-instruction , boolean->integer)
        (t/attach-instruction , boolean->signedint)
        (t/attach-instruction , char->integer)
        (t/attach-instruction , float->integer)
        (t/attach-instruction , integer-abs)
        (t/attach-instruction , integer-add)
        (t/attach-instruction , integer-biggest)
        (t/attach-instruction , integer-bits)
        (t/attach-instruction , integer-dec)
        (t/attach-instruction , integer-digits)
        (t/attach-instruction , integer-divide)
        (t/attach-instruction , integer-inc)
        (t/attach-instruction , integer-few)
        (t/attach-instruction , integer-lots)
        (t/attach-instruction , integer-many)
        (t/attach-instruction , integer-mod)
        (t/attach-instruction , integer-multiply)
        (t/attach-instruction , integer-sign)
        (t/attach-instruction , integer-smallest)
        (t/attach-instruction , integer-some)
        (t/attach-instruction , integer-subtract)
        (t/attach-instruction , integer-totalistic3)
        (t/attach-instruction , integer->bits)
        (t/attach-instruction , integer->numerals)
        (t/attach-instruction , string->integer)
        ))

