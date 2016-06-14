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



(defn valid-push-integer?
  "checks class is java.lang.Long; used in bounds-checking"
  [i]
  (or (instance? java.lang.Long i)
      (instance? java.lang.Integer i)))


;; INSTRUCTIONS



(def integer-bits
  (core/build-instruction
    integer-bits
    "`:integer-bits` pops the top `:integer` and pushes a list of its 'bits' (as `true` and `false` in low-to-high order. Negative numbers are made positive. The minimal binary representation is used, but always at least two bits."
    :tags #{:numeric :conversion}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg]
      #(into '() (reverse (exotics/integer-to-truth-table (math/abs %1) 1))) :as :bits)
    (d/push-onto :exec :bits)))




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




(def integer-few
  (core/build-instruction
    integer-few
    "`:integer-few` pops the top `:integer` value, and calculates `(mod 10 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10) :as :scaled)
    (d/push-onto :scalar :scaled)))



(def integer-lots
  (core/build-instruction
    integer-lots
    "`:integer-lots` pops the top `:integer` value, and calculates `(mod 10000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 10000) :as :scaled)
    (d/push-onto :scalar :scaled)))



(def integer-many
  (core/build-instruction
    integer-many
    "`:integer-many` pops the top `:integer` value, and calculates `(mod 1000 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 1000) :as :scaled)
    (d/push-onto :scalar :scaled)))



(def integer-some
  (core/build-instruction
    integer-some
    "`:integer-some` pops the top `:integer` value, and calculates `(mod 100 x)`."
    :tags #{:numeric}
    (d/consume-top-of :integer :as :arg)
    (d/calculate [:arg] #(mod %1 100) :as :scaled)
    (d/push-onto :scalar :scaled)))







;; CONVERSIONS





(def char->integer
  (core/build-instruction
    char->integer
    "`:char->integer` pops the top `:char` item, and converts it to an (integer) index"
    :tags #{:base :conversion}
    (d/consume-top-of :char :as :arg1)
    (d/calculate [:arg1] #(long %1) :as :int)
    (d/push-onto :scalar :int)))



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
    (d/push-onto :scalar :result)
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
    (d/push-onto :scalar :result)
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
        (t/attach-instruction , char->integer)
        (t/attach-instruction , float->integer)
        (t/attach-instruction , integer-bits)
        (t/attach-instruction , integer-digits)
        (t/attach-instruction , integer-few)
        (t/attach-instruction , integer-lots)
        (t/attach-instruction , integer-many)
        (t/attach-instruction , integer-some)
        (t/attach-instruction , integer->bits)
        (t/attach-instruction , integer->numerals)
        (t/attach-instruction , string->integer)
        ))

