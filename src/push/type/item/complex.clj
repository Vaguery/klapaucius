(ns push.type.item.complex
  (:require [push.instructions.dsl         :as d]
            [push.instructions.core        :as i]
            [clojure.math.numeric-tower    :as nt :refer [sqrt]]
            [push.type.core                :as t]
            [push.instructions.aspects     :as aspects]
            [push.type.definitions.complex :as complex]
            ))

(def complex-add
  (i/build-instruction
    complex-add
    "`:complex-add` pops the top two `:complex` items and pushes their sum; if adding them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"

    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-sum %1 %2) :as :sum)
    (d/calculate [:sum] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :sum]#(when-not %1 %2):as :sum)
    (d/calculate [:nan] #(when %1 ":complex-add produced NaN") :as :warning)
    (d/return-item :sum)
    (d/record-an-error :from :warning)
    ))


(def complex-conjugate
  (i/build-instruction
    complex-conjugate
    "`:complex-conjugate` pops the top `:complex` item and pushes its complex conjugate"

    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1] #(complex/conjugate %1) :as :cc)
    (d/return-item :cc)
    ))



(def complex-divide
  (i/build-instruction
    complex-divide
    "`:complex-divide` pops the top two `:complex` items and pushes their quotient; if dividing them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`, or division by zero is attempted) then an `:error` is the result"

    (d/consume-top-of :complex :as :denominator)
    (d/consume-top-of :complex :as :numerator)
    (d/calculate [:numerator :denominator]
      #(complex/complex-quotient %1 %2) :as :quotient)
    (d/calculate [:quotient]
      #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :quotient] #(when-not %1 %2) :as :quotient)
    (d/return-item :quotient)
    (d/calculate [:nan]
      #(when %1 ":complex-divide produced NaN") :as :warn2)
    (d/record-an-error :from :warn2)
    ))




(def complex-infinite?
  (i/build-instruction
    complex-infinite?
    "`:complex-infinite?` pops the top `:complex` value and pushes `true` if either component is ∞ or -∞."

    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg] #(complex/complex-infinite? %1) :as :result)
    (d/return-item :result)
    ))




(def complex-multiply
  (i/build-instruction
    complex-multiply
    "`:complex-multiply` pops the top two `:complex` values and pushes their product to `:complex`. If the result is or contains `NaN`, an `:error` is produced instead of the product. "

    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-product %1 %2) :as :prelim)
    (d/calculate [:prelim] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :prelim]#(when-not %1 %2):as :product)
    (d/calculate [:nan] #(when %1 ":complex-multiply produced NaN") :as :warning)
    (d/return-item :product)
    (d/record-an-error :from :warning)
    ))



(def complex-norm
  (i/build-instruction
    complex-norm
    "`:complex-norm` pops the top `:complex` value, calculates its norm, and pushes that result to `:scalar`."

    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg]
      #(let [r (:re %1) i (:im %1)]
          (nt/sqrt (+' (*' r r) (*' i i)))) :as :result)
    (d/return-item :result)
    ))




(def complex-reciprocal
  (i/build-instruction
    complex-reciprocal
    "`:complex-reciprocal` pops the top `:complex` value and pushes its reciprocal."

    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg]
        #(complex/complex-quotient (complex/complexify 1) %1) :as :prelim)
    (d/calculate [:prelim] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(when-not %1 %2) :as :product)
    (d/calculate [:nan] #(when %1 ":complex-reciprocal produced NaN") :as :warning)
    (d/return-item :product)
    (d/record-an-error :from :warning)
    ))




(def complex-scale
  (i/build-instruction
    complex-scale
    "`:complex-scale` pops the top `:complex` value and the top `:scalar`, and pushes their product to `:complex`."

    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2]
        #(complex/complexify (*' (:re %1) %2)
                             (*' (:im %1) %2)) :as :result)
    (d/return-item :result)
    ))



(def complex-shift
  (i/build-instruction
    complex-shift
    "`:complex-shift` pops the top `:complex` value and the top `:scalar`, and pushes the result of adding the `:scalar` to each component of the `:complex` value."

    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2]
        #(complex/complexify (+' (:re %1) %2)
                             (+' (:im %1) %2)) :as :result)
    (d/return-item :result)
    ))



(def complex-subtract
  (i/build-instruction
    complex-subtract
    "`:complex-subtract` pops the top two `:complex` items and pushes their difference; if subtracting them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"

    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-diff %1 %2) :as :diff)
    (d/calculate [:diff] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :diff]#(when-not %1 %2):as :diff)
    (d/calculate [:nan] #(when %1 ":complex-subtract produced NaN") :as :warning)
    (d/return-item :diff)
    (d/record-an-error :from :warning)
    ))



(def complex-zero
  (i/build-instruction
    complex-zero
    "`:complex-zero` pushes {re:0 im:0} onto `:complex`."

    (d/calculate [] (fn [] (complex/complexify 0)) :as :new)
    (d/return-item :new)
    ))




(def scalar-complexify
  (i/build-instruction
    scalar-complexify
    "`:scalar-complexify` pops the top `:scalar` value and pushes a new `:complex` item with 0 imaginary component."

    (d/consume-top-of :scalar :as :re)
    (d/calculate [:re] #(complex/complexify %1 0) :as :new)
    (d/return-item :new)
    ))




(def complex-type
  (-> (t/make-type  :complex
                    :recognized-by complex/complex?
                    :attributes #{:numeric}
                    :manifest {:re :scalar
                               :im :scalar}
                    :builder complex/complexify
                    )
        aspects/make-buildable
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-set-able
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , complex-add)
        (t/attach-instruction , complex-conjugate)
        (t/attach-instruction , complex-divide)
        (t/attach-instruction , complex-infinite?)
        (t/attach-instruction , complex-multiply)
        (t/attach-instruction , complex-norm)
        (t/attach-instruction , complex-reciprocal)
        (t/attach-instruction , complex-scale)
        (t/attach-instruction , complex-shift)
        (t/attach-instruction , complex-subtract)
        (t/attach-instruction , complex-zero)
        (t/attach-instruction , scalar-complexify)

  ))
