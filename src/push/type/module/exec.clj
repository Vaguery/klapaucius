(ns push.type.module.exec
  (:require [push.instructions.dsl     :as d]
            [push.instructions.core    :as i]
            [push.util.numerics        :as n]
            [push.type.core            :as t]
            [push.instructions.aspects :as aspects]
            ))


;; INSTRUCTIONS


(def exec-do*count
  (i/build-instruction
    exec-do*count
    "`:exec-do*count` pops the top item of `:exec` and the top `:scalar`. It constructs a continuation depending on whether the `:scalar` is non-negative:

      - `[s]` positive?: `(0 [s] :exec-do*range [item])`
      - `[s]` zero or negative?: `([s] [item])`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] #((complement pos?) %1) :as :done?)
    (d/calculate
      [:do-this :counter :done?]
      #(if %3
        (list (dec %2) %1)
        (list 0 %2 :exec-do*range %1)) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doabunch*count
  (i/build-instruction
    exec-doabunch*count
    "`:exec-doabunch*count` pops the top item of `:exec` and the top `:scalar`. The `scalar` is brought down to the range `[-100,100]` using push.util.numerics/bunch. It constructs a continuation depending on whether the `:scalar` is non-negative:

      - `[s]` positive?: `(0 [s] :exec-do*range [item])`
      - `[s]` zero or negative?: `([s] [item])`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] n/bunch :as :scaled-counter)
    (d/calculate [:scaled-counter]
      #((complement pos?) %1) :as :done?)
    (d/calculate
      [:do-this :scaled-counter :done?]
      #(if %3
        (list (dec %2) %1)
        (list 0 %2 :exec-doabunch*range %1)) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doafew*count
  (i/build-instruction
    exec-doafew*count
    "`:exec-doafew*count` pops the top item of `:exec` and the top `:scalar`. The `scalar` is brought down to the range `[-10,10]` using push.util.numerics/few. It constructs a continuation depending on whether the `:scalar` is non-negative:

      - `[s]` positive?: `(0 [s] :exec-do*range [item])`
      - `[s]` zero or negative?: `([s] [item])`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] n/few :as :scaled-counter)
    (d/calculate [:scaled-counter]
      #((complement pos?) %1) :as :done?)
    (d/calculate
      [:do-this :scaled-counter :done?]
      #(if %3
        (list (dec %2) %1)
        (list 0 %2 :exec-doafew*range %1)) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-do*range
  (i/build-instruction
    exec-do*range
      "`:exec-do*count` pops the top item of `:exec` and the top two `:scalar` values (call them `end` and `start`, respectively). It constructs a continuation depending on whether the relation between `start` and `end`, which will (when interpreted) send the current `start` value to the `:scalar` stack, execute the `:exec` item, send updated indices to the `:scalar` stack, and then repeat the loop:

      - `start` < `end` and more than 1 different:
        `'([start] [item] ([start+1] [end] :exec-do*range [item]))`
      - `start` > `end` and more than 1 different:
        `'([start] [item] ([start-1] [end] :exec-do*range [item]))`
      - `start` within 1 of `end`:
        `'([end] [item])`

    This continuation is pushed to the `:exec` stack. "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:start :end]
      #(n/within-1? %1 %2) :as :done?)
    (d/calculate [:start :end]  #(+' %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?]
      #(cond
        (nil? %4) nil
        (nil? %5) nil
        %5 (list %4 %1)
        :else
           (list %2 %1 (list %4 %3 :exec-do*range %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doafew*range
  (i/build-instruction
    exec-doafew*range
      "`:exec-doafew*range` pops the top item of `:exec` and the top two `:scalar` values (call them `end` and `start`, respectively). The first thing it does is scale `start` and `end` to fall in the range `[-10,10]`. Then it constructs a continuation depending on whether the relation between `start` and `end`, which will (when interpreted) send the current `start` value to the `:scalar` stack, execute the `:exec` item, send updated indices to the `:scalar` stack, and then repeat the loop:

      - `start` < `end` and more than 1 different:
        `'([start] [item] ([start+1] [end] :exec-doafew*range [item]))`
      - `start` > `end` and more than 1 different:
        `'([start] [item] ([start-1] [end] :exec-doafew*range [item]))`
      - `start` within 1 of `end`:
        `'([end] [item])`

    This continuation is pushed to the `:exec` stack. "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:start] n/few :as :start)
    (d/calculate [:end] n/few :as :end)
    (d/calculate [:start :end]
      #(n/within-1? %1 %2) :as :done?)
    (d/calculate [:start :end]  #(+' %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?]
      #(cond
        (nil? %4) nil
        (nil? %5) nil
        %5 (list %4 %1)
        :else
           (list %2 %1 (list %4 %3 :exec-doafew*range %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doabunch*range
  (i/build-instruction
    exec-doabunch*range
      "`:exec-doabunch*range` pops the top item of `:exec` and the top two `:scalar` values (call them `end` and `start`, respectively). The first thing it does is scale `start` and `end` to fall in the range `[-10,10]`. Then it constructs a continuation depending on whether the relation between `start` and `end`, which will (when interpreted) send the current `start` value to the `:scalar` stack, execute the `:exec` item, send updated indices to the `:scalar` stack, and then repeat the loop:

      - `start` < `end` and more than 1 different:
        `'([start] [item] ([start+1] [end] :exec-doabunch*range [item]))`
      - `start` > `end` and more than 1 different:
        `'([start] [item] ([start-1] [end] :exec-doabunch*range [item]))`
      - `start` within 1 of `end`:
        `'([end] [item])`

    This continuation is pushed to the `:exec` stack. "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:start] n/bunch :as :start)
    (d/calculate [:end] n/bunch :as :end)
    (d/calculate [:start :end]
      #(n/within-1? %1 %2) :as :done?)
    (d/calculate [:start :end]  #(+' %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?]
      #(cond
        (nil? %4) nil
        (nil? %5) nil
        %5 (list %4 %1)
        :else
           (list %2 %1 (list %4 %3 :exec-doabunch*range %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-do*times
  (i/build-instruction
    exec-do*times
      "`:exec-do*times` pops the top item of `:exec` and the top `:scalar` value (call it `counter`. It constructs a continuation depending on whether the `counter` is positive, negative or zero, which is pushed to `:exec`:

        - negative: `item` (not in a codeblock)
        - zero:     `item` (not in a codeblock)
        - positive: `(item ([counter-1] :exec-do*times item))`
        "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] #((complement pos?) %1) :as :done?)
    (d/calculate [:counter] #(+ %1 (compare 0 %1)) :as :next)
    (d/calculate
      [:do-this :counter :next :done?]
      #(if %4
           %1
           (list %1 (list %3 :exec-do*times %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doafew*times
  (i/build-instruction
    exec-doafew*times
      "`:exec-doafew*times` pops the top item of `:exec` and the top `:scalar` value (call it `counter`). It first scales `counter` to fall in the range [-10,10] with push.util.numerics/few. It constructs a continuation depending on whether the `counter` is positive, negative or zero, which is pushed to `:exec`:

        - negative: `item` (not in a codeblock)
        - zero:     `item` (not in a codeblock)
        - positive: `(item ([counter-1] :exec-doafew*times item))`
        "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] #(n/few %1) :as :counter)
    (d/calculate [:counter] #((complement pos?) %1) :as :done?)
    (d/calculate [:counter] #(+ %1 (compare 0 %1)) :as :next)
    (d/calculate
      [:do-this :counter :next :done?]
      #(if %4
           %1
           (list %1 (list %3 :exec-doafew*times %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-doabunch*times
  (i/build-instruction
    exec-doabunch*times
      "`:exec-doabunch*times` pops the top item of `:exec` and the top `:scalar` value (call it `counter`). It first scales `counter` to fall in the range [-10,10] with push.util.numerics/few. It constructs a continuation depending on whether the `counter` is positive, negative or zero, which is pushed to `:exec`:

        - negative: `item` (not in a codeblock)
        - zero:     `item` (not in a codeblock)
        - positive: `(item ([counter-1] :exec-doabunch*times item))`
        "

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] #(n/bunch %1) :as :counter)
    (d/calculate [:counter] #((complement pos?) %1) :as :done?)
    (d/calculate [:counter] #(+ %1 (compare 0 %1)) :as :next)
    (d/calculate
      [:do-this :counter :next :done?]
      #(if %4
           %1
           (list %1 (list %3 :exec-doabunch*times %1))) :as :continuation)
    (d/return-item :continuation)
    ))


(def exec-if
  (i/build-instruction
    exec-if
    "`:exec-if` pops the top `:boolean` item and the top two `:exec` items (call the top one `A` and the second `B`). If the `:boolean` is `true`, it pushes `A` onto `:exec`, otherwise it pushes `B` onto `:exec`."

    (d/consume-top-of :boolean :as :decider)
    (d/consume-top-of :exec :as :option1)
    (d/consume-top-of :exec :as :option2)
    (d/calculate [:decider :option1 :option2] #(if %1 %2 %3) :as :result)
    (d/return-item :result)
    ))



(def exec-k
  (i/simple-2-in-1-out-instruction
    "`:exec-k` pops the top two `:exec` items, and pushes the top one back onto `:exec` (discarding the second one, in other words)"
    :exec
    "k"
    (fn [a b] b)
    ))



(def exec-laterloop
  (i/build-instruction
    exec-laterloop
    "`:exec-laterloop` pops the top item from the `:exec` stack, and the top `:scalar item. It calculates an index in the `:exec` stack based on the `:scalar` value, and pushes a continuation in the form `'([item] :exec-laterloop [item])` to the END of the `:exec` stack. If the `:exec` stack is empty except for the looping item, then the loop ceases and nothing is pushed."

    (d/consume-top-of :exec :as :arg)
    (d/count-of :exec :as :exec-size)
    (d/calculate [:exec-size :arg]
      #(if (zero? %1) %2 (list %2 :exec-laterloop %2)) :as :continuation)
    (d/consume-stack :exec :as :oldstack)
    (d/calculate [:oldstack :continuation]
      #(into '() (conj (reverse %1) %2)) :as :newstack)
    (d/replace-stack :exec :newstack)
    ))



(def exec-noop
  (i/build-instruction
    exec-noop
    "`:exec-noop does not affect the stacks"
    ))


(def exec-s
  (i/build-instruction
    exec-s
    "`:exec-s` pops three items off the `:exec` stack; call them `A, `B` and `C`, from top to third items. It then pushes a code block containing three items, onto `:exec`:

    1. `A`
    2. `C`
    3. the list `'(B C)`

    As a result, the top of the `:exec` stack will read: `'((A C (B C)) ...)`"

    (d/consume-top-of :exec :as :a)
    (d/consume-top-of :exec :as :b)
    (d/consume-top-of :exec :as :c)
    (d/calculate [:b :c] list :as :bc)
    (d/return-codeblock :a :c :bc)
    ))



(def exec-when
  (i/build-instruction
    exec-when
    "`:exec-when` pops the top of the `:exec` and `:boolean` stacks. If the `:boolean` is `true`, it pushes the item back onto `:exec` (otherwise nothing is pushed)."

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :boolean :as :really?)
    (d/calculate [:do-this :really?] #(when %2 %1) :as :result)
    (d/return-item :result)
    ))



(def exec-do*while
  (i/build-instruction
    exec-do*while
    "`:exec-do*while` pops the top of the `:exec` stack. It builds and pushes a continuation to the `:exec` stack which is `'([item] :exec-while [item])` (_Note_ the instruction is `:exec-while` in the continuation form)."

    (d/consume-top-of :exec :as :do-this)
    (d/calculate [:do-this] #(list %1 :exec-while %1) :as :continuation)
    (d/return-item :continuation)
    ))



(def exec-while
  (i/build-instruction
    exec-while
    "`:exec-while` pops the top of the `:exec` stack, and the top `:boolean`. It builds and pushes a continuation to the `:exec` stack based on the `:boolean` value:

      - `true`: `'([item] :exec-while [item])`
      - `false`: `'()`"

    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :boolean :as :again?)
    (d/calculate [:do-this :again?] #(if %2 (list %1 :exec-while %1) '()) :as :continuation)
    (d/return-item :continuation)
    ))




(def exec-module
  ( ->  (t/make-module  :exec
                        :attributes #{:complex :base})
        aspects/make-set-able
        aspects/make-cycling
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , exec-do*count)
        (t/attach-instruction , exec-do*range)
        (t/attach-instruction , exec-do*times)
        (t/attach-instruction , exec-doabunch*count)
        (t/attach-instruction , exec-doabunch*range)
        (t/attach-instruction , exec-doabunch*times)
        (t/attach-instruction , exec-doafew*count)
        (t/attach-instruction , exec-doafew*range)
        (t/attach-instruction , exec-doafew*times)
        (t/attach-instruction , exec-k)
        (t/attach-instruction , exec-laterloop)
        (t/attach-instruction , exec-noop)
        (t/attach-instruction , exec-s)
        (t/attach-instruction , exec-if)
        (t/attach-instruction , exec-do*while)
        (t/attach-instruction , exec-when)
        (t/attach-instruction , exec-while)
        ))
