(ns push.type.item.complex
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.router.core :as r]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.type.definitions.complex :as complex]
            [clojure.math.numeric-tower :as nt]
            ))




(def complex-add
  (core/build-instruction
    complex-add
    "`:complex-add` pops the top two `:complex` items and pushes their sum; if adding them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"
    :tags #{:complex :numeric}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-sum %1 %2) :as :sum)
    (d/calculate [:sum] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :sum] #(if %1 nil %2) :as :sum)
    (d/calculate [:nan] #(if %1 ":complex-add produced NaN" nil) :as :warning)
    (d/push-onto :complex :sum)
    (d/record-an-error :from :warning)
    ))




(def complex-conjugate
  (core/build-instruction
    complex-conjugate
    "`:complex-conjugate` pops the top `:complex` item and pushes its complex conjugate"
    :tags #{:complex :numeric}
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1] #(complex/conjugate %1) :as :cc)
    (d/push-onto :complex :cc)
    ))



(def complex-divide
  (core/build-instruction
    complex-divide
    "`:complex-divide` pops the top two `:complex` items and pushes their quotient; if dividing them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`, or division by zero is attempted) then an `:error` is the result"
    :tags #{:complex :numeric}

    (d/consume-top-of :complex :as :denominator)
    (d/consume-top-of :complex :as :numerator)
    (d/calculate [:numerator :denominator]
      #(complex/complex-quotient %1 %2) :as :quotient)
    (d/calculate [:quotient]
      #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :quotient] #(if %1 nil %2) :as :quotient)
    (d/push-onto :complex :quotient)
    (d/calculate [:nan]
      #(if %1 ":complex-divide produced NaN" nil) :as :warn2)
    (d/record-an-error :from :warn2)
    ))




(def complex-infinite?
  (core/build-instruction
    complex-infinite?
    "`:complex-infinite?` pops the top `:complex` value and pushes `true` if either component is ∞ or -∞."
    :tags #{:arithmetic :base}
    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg] #(complex/complex-infinite? %1) :as :result)
    (d/push-onto :boolean :result)
    ))




(def complex-multiply
  (core/build-instruction
    complex-multiply
    "`:complex-multiply` pops the top two `:complex` values and pushes their product to `:complex`. If the result is or contains `NaN`, an `:error` is produced instead of the product. "
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-product %1 %2) :as :prelim)
    (d/calculate [:prelim] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(if %1 nil %2) :as :product)
    (d/calculate [:nan]
      #(if %1 ":complex-multiply produced NaN" nil) :as :warning)
    (d/push-onto :complex :product)
    (d/record-an-error :from :warning)
    ))



(def complex-norm
  (core/build-instruction
    complex-norm
    "`:complex-norm` pops the top `:complex` value, calculates its norm, and pushes that result to `:scalar`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg]
      #(let [r (:re %1) i (:im %1)]
          (nt/sqrt (+' (*' r r) (*' i i)))) :as :result)
    (d/push-onto :scalar :result)
    ))



(def complex-parts
  (core/build-instruction
    complex-parts
    "`:complex-parts` pops the top `:complex` value and pushes a code block containing its real and imaginary parts (in that order) onto `:exec`"
    :tags #{:complex}
    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg] #(vals %1) :as :parts)
    (d/push-onto :exec :parts)
    ))



(def complex-reciprocal
  (core/build-instruction
    complex-reciprocal
    "`:complex-reciprocal` pops the top `:complex` value and pushes its reciprocal."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :complex :as :arg)
    (d/calculate [:arg]
        #(complex/complex-quotient (complex/complexify 1) %1) :as :prelim)
    (d/calculate [:prelim] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :prelim] #(if %1 nil %2) :as :product)
    (d/calculate [:nan]
      #(if %1 ":complex-reciprocal produced NaN" nil) :as :warning)
    (d/push-onto :complex :product)
    (d/record-an-error :from :warning)
    ))





(def complex-scale
  (core/build-instruction
    complex-scale
    "`:complex-scale` pops the top `:complex` value and the top `:scalar`, and pushes their product to `:complex`."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2]
        #(complex/complexify (*' (:re %1) %2)
                             (*' (:im %1) %2)) :as :result)
    (d/push-onto :complex :result)
    ))



(def complex-shift
  (core/build-instruction
    complex-shift
    "`:complex-shift` pops the top `:complex` value and the top `:scalar`, and pushes the result of adding the `:scalar` to each component of the `:complex` value."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :scalar :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2]
        #(complex/complexify (+' (:re %1) %2)
                             (+' (:im %1) %2)) :as :result)
    (d/push-onto :complex :result)
    ))



(def complex-subtract
  (core/build-instruction
    complex-subtract
    "`:complex-subtract` pops the top two `:complex` items and pushes their difference; if subtracting them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"
    :tags #{:complex :numeric}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-diff %1 %2) :as :diff)
    (d/calculate [:diff] #(if %1 (complex/complex-NaN? %1) false) :as :nan)
    (d/calculate [:nan :diff] #(if %1 nil %2) :as :diff)
    (d/calculate [:nan] #(if %1 ":complex-subtract produced NaN" nil) :as :warning)
    (d/push-onto :complex :diff)
    (d/record-an-error :from :warning)
    ))



(def complex-zero
  (core/build-instruction
    complex-zero
    "`:complex-zero` pushes {re:0 im:0} onto `:complex`."
    :tags #{:complex}
    (d/calculate [] (fn [] (complex/complexify 0)) :as :new)
    (d/push-onto :complex :new)
    ))



(def scalar->complex
  (core/build-instruction
    scalar->complex
    "`:scalar->complex` pops the top two `:scalar` values (call them IM and RE, respectively) and pushes a new `:complex` item with those components."
    :tags #{:complex}
    (d/consume-top-of :scalar :as :im)
    (d/consume-top-of :scalar :as :re)
    (d/calculate [:re :im] #(complex/complexify %1 %2) :as :new)
    (d/push-onto :complex :new)
    ))



(def scalar-complexify
  (core/build-instruction
    scalar-complexify
    "`:scalar-complexify` pops the top `:scalar` value and pushes a new `:complex` item with 0 imaginary component."
    :tags #{:complex}
    (d/consume-top-of :scalar :as :re)
    (d/calculate [:re] #(complex/complexify %1 0) :as :new)
    (d/push-onto :complex :new)
    ))




(def complex-type
  (-> (t/make-type  :complex
                    :recognized-by push.type.definitions.complex/complex?
                    :attributes #{:numeric})
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible 
        (t/attach-instruction , complex-add)
        (t/attach-instruction , complex-conjugate)
        (t/attach-instruction , complex-divide)
        (t/attach-instruction , complex-infinite?)
        (t/attach-instruction , complex-multiply)
        (t/attach-instruction , complex-norm)
        (t/attach-instruction , complex-parts)
        (t/attach-instruction , complex-reciprocal)
        (t/attach-instruction , complex-scale)
        (t/attach-instruction , complex-shift)
        (t/attach-instruction , complex-subtract)
        (t/attach-instruction , complex-zero)
        (t/attach-instruction , scalar-complexify)
        (t/attach-instruction , scalar->complex)

  ))

