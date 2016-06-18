(ns push.types.type.complex
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.router.core :as r]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.types.definitions.complex :as complex]
            ))




(def complex-add
  (core/build-instruction
    complex-add
    "`:complex-add` pops the top two `:complex` items and pushes their sum; if adding them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"
    :tags #{:complex :numeric}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-sum %1 %2) :as :sum)
    (d/calculate [:sum] #(complex/complex-NaN? %1) :as :nan)
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
    (d/calculate [:denominator] #(complex/complex-zero? %1) :as :div0)
    (d/calculate [:div0 :denominator :numerator]
      #(if %1 nil (complex/complex-quotient %3 %2)) :as :quotient)
    (d/calculate [:div0 :quotient]
      #(and (not %1) (complex/complex-NaN? %2)) :as :nan)
    (d/calculate [:nan :quotient] #(if %1 nil %2) :as :quotient)
    (d/push-onto :complex :quotient)
    (d/calculate [:div0]
      #(if %1 ":complex-divide Div0" nil) :as :warn1)
    (d/calculate [:nan]
      #(if %1 ":complex-divide produced NaN" nil) :as :warn2)
    (d/record-an-error :from :warn1)
    (d/record-an-error :from :warn2)
    ))



(def complex-multiply
  (core/build-instruction
    complex-multiply
    "`:complex-multiply` pops the top two `:complex` values and pushes their product to `:complex`. If the result is or contains `NaN`, an `:error` is produced instead of the product. If the multiplication of a Clojure `bigdec` and `rational` would produce a `Non-terminating decimal expansion` exception, the `bigdec` is converted to a `long` or `double` implicitly, depending on whether it's integral or not."
    :tags #{:arithmetic :base :dangerous}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-product %1 %2) :as :prelim)
    (d/calculate [:prelim] #(complex/complex-NaN? %1) :as :nan)
    (d/calculate [:nan :prelim] #(if %1 nil %2) :as :product)
    (d/calculate [:nan]
      #(if %1 ":complex-multiply produced NaN" nil) :as :warning)
    (d/push-onto :complex :product)
    (d/record-an-error :from :warning)
    ))




(def complex-subtract
  (core/build-instruction
    complex-subtract
    "`:complex-subtract` pops the top two `:complex` items and pushes their difference; if subtracting them would cause an arithmetic error (for example, if one is a `bigdec` and the other a `rational`) then an `:error` is the result"
    :tags #{:complex :numeric}
    (d/consume-top-of :complex :as :arg2)
    (d/consume-top-of :complex :as :arg1)
    (d/calculate [:arg1 :arg2] #(complex/complex-diff %1 %2) :as :diff)
    (d/calculate [:diff] #(complex/complex-NaN? %1) :as :nan)
    (d/calculate [:nan :diff] #(if %1 nil %2) :as :diff)
    (d/calculate [:nan] #(if %1 ":complex-subtract produced NaN" nil) :as :warning)
    (d/push-onto :complex :diff)
    (d/record-an-error :from :warning)
    ))




(def complex-type
  (-> (t/make-type  :complex
                    :recognized-by push.types.definitions.complex/complex?
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
        (t/attach-instruction , complex-multiply)
        (t/attach-instruction , complex-subtract)

  ))

