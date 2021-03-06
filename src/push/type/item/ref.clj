(ns push.type.item.ref
  (:require [push.instructions.dsl           :as d]
            [push.instructions.core          :as i]
            [push.type.core                  :as t]
            [push.instructions.aspects       :as aspects]
            [push.util.numerics              :as num]
            [push.type.definitions.tagspace  :as ts]
            [push.type.definitions.quoted    :as qc]
            ))


(def ref-ARGS
  (i/build-instruction
    ref-ARGS
    "`:ref-ARGS` (1) pushes the top item currently stored in the special `:ARGS` binding onto `:exec`, and (2) pushes the keyword `:ARGS` onto the `:ref` stack."
    (d/calculate [] (fn [] :ARGS) :as :which-binding)
    (d/save-top-of-binding :which-binding :as :value)
    (d/push-onto :ref :which-binding)
    (d/return-item :value)
    ))



(def ref-clear
  (i/build-instruction
    ref-clear
    "`:ref-clear` pops the top `:ref` keyword and clears all items currently bound to that keyword in the Interpreter's binding table. The variable remains recognized, it simply has no bound values."

    (d/consume-top-of :ref :as :arg)
    (d/clear-binding :arg)
    ))




(def ref-cyclevector
  (i/build-instruction
    ref-cyclevector
    "`:ref-cyclevector` pops the top `:ref` item and the top two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of elements from the `:ref`'s current stack are made into a single vector (by cycling) and pushed to `:exec`. If the `:ref` has no bound values, an empty vector is pushed."

    (d/consume-top-of :ref :as :which)
    (d/save-binding-stack :which :as :contents)
    (d/consume-top-of :scalar :as :scale)
    (d/consume-top-of :scalar :as :raw-count)
    (d/calculate [:scale]
      #(nth [10 100 1000] (num/scalar-to-index %1 3)) :as :relative)
    (d/calculate [:raw-count :relative]
      #(num/scalar-to-index %1 %2) :as :size)
    (d/calculate [:contents :size] #(vec (take %2 (cycle %1))) :as :result)
    (d/return-item :result)
    ))




(def ref-dump
  (i/build-instruction
    ref-dump
    "`:ref-dump` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:exec` stack as a single block"

    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/return-item :newblock)
    ))



(def ref-dump-tagspace
  (i/build-instruction
    ref-dump-tagspace
    "`:ref-dump-tagspace` pops the top `:ref` keyword and looks up the entire current contents of that binding's stack; this is used to construct a `:tagspace`, using the integer position of each as the key scalars. If the `:ref` is empty, an empty `:tagspace` is returned; the binding contents remain in place."

    (d/consume-top-of :ref :as :ref)
    (d/save-binding-stack :ref :as :contents)
    (d/calculate [:contents]
      #(ts/make-tagspace (zipmap (range 0 (count %1)) %1)) :as :result)
    (d/return-item :result)
    ))



(def ref-exchange
  (i/build-instruction
    ref-exchange
    "`:ref-exchange` pops the top two `:ref` keywords; if they are references to the same binding, there is no effect; if either or both is an undefined `:ref`, it has an empty value stack created as needed"

    (d/consume-top-of :ref :as :arg1)
    (d/consume-top-of :ref :as :arg2)
    (d/save-binding-stack :arg1 :as :values1)
    (d/save-binding-stack :arg2 :as :values2)
    (d/replace-binding :values1 :into :arg2)
    (d/replace-binding :values2 :into :arg1)
    ))



(def ref-fillvector
  (i/build-instruction
    ref-fillvector
    "`:ref-fillvector` pops the top `:ref` item and the top two `:scalar` items (call them `scale` and `raw-count`, respectively). The `scale` value is used to determine whether to convert `raw-count` into a :few, :some, :many or :lots value, and then the appropriate number of copies of the `:ref`'s current value are made into a single vector and pushed to `:exec`. If the `:ref` has no bound values, an empty vector is pushed."

    (d/consume-top-of :ref :as :which)
    (d/save-top-of-binding :which :as :item)
    (d/consume-top-of :scalar :as :scale)
    (d/consume-top-of :scalar :as :raw-count)
    (d/calculate [:scale]
      #(nth [10 100 1000] (num/scalar-to-index %1 3)) :as :relative)
    (d/calculate [:raw-count :relative]
      #(num/scalar-to-index %1 %2) :as :size)
    (d/calculate [:item :size]
      #(if (nil? %1) [] (vec (take %2 (repeat %1)))) :as :result)
    (d/return-item :result)
    ))




(def ref-forget
  (i/build-instruction
    ref-forget
    "`:ref-forget` pops the top `:ref` keyword and clears the entire binding currently associated with it, key and all. NOTE: this is permitted to erase an `:input` binding."

    (d/consume-top-of :ref :as :arg)
    (d/forget-binding :arg)
    ))



(def ref-fullquote
  (i/build-instruction
    ref-fullquote
    "`:ref-fullquote` pops the top `:ref` keyword and pushes the entire current contents of that binding's stack onto the `:exec` stack as a quoted codeblock"

    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :newblock)
    (d/calculate [:newblock] #(qc/push-quote %1) :as :result)
    (d/return-item :result)
    ))



(def ref-known?
  (i/build-instruction
    ref-known?
    "`:ref-known?` pops the top `:ref` keyword and `true` if it is one of the defined `:binding` keys"

    (d/consume-top-of :ref :as :arg)
    (d/save-bindings :as :known) ;; just the keys
    (d/calculate [:known :arg] #(boolean (some #{%2} %1)) :as :result)
    (d/return-item :result)
    ))



(def ref-lookup
  (i/build-instruction
    ref-lookup
    "`:ref-lookup` pops the top `:ref` keyword and pushes a copy of the top item on its stack onto the `:exec` stack"

    (d/consume-top-of :ref :as :arg)
    (d/save-top-of-binding :arg :as :value)
    (d/return-item :value)
    ))



(def ref-new
  (i/build-instruction
    ref-new
    "`:ref-new` creates a new (randomly-named) `:ref` keyword and pushes it to that stack"

    (d/calculate [] #(keyword (gensym "ref!")) :as :newref)
    (d/push-onto :ref :newref)
    ))



(def ref-peek
  (i/build-instruction
    ref-peek
    "`:ref-peek` pops the top `:ref` keyword and pushes a copy of the top item on its stack onto the `:exec` stack; it then returns the `:ref` to that stack"

    (d/consume-top-of :ref :as :arg)
    (d/save-top-of-binding :arg :as :value)
    (d/push-onto :ref :arg)
    (d/return-item :value)
    ))



(def ref->vector
  (i/build-instruction
    ref->vector
    "`:ref->vector` pops the top `:ref` keyword and copies its entire stack of contents into a new `:vector` item, which is pushed to `:exec`"

    (d/consume-top-of :ref :as :arg)
    (d/save-binding-stack :arg :as :contents)
    (d/calculate [:contents] vec :as :result)
    (d/return-item :result)
    ))






(def ref-type
  ( ->  (t/make-type    :ref
                        :recognized-by keyword?
                        :attributes #{:base})


        (t/attach-instruction ref-ARGS)
        (t/attach-instruction ref-clear)
        (t/attach-instruction ref-cyclevector)
        (t/attach-instruction ref-dump)
        (t/attach-instruction ref-dump-tagspace)
        (t/attach-instruction ref-exchange)
        (t/attach-instruction ref-fillvector)
        (t/attach-instruction ref-forget)
        (t/attach-instruction ref-fullquote)
        (t/attach-instruction ref-known?)
        (t/attach-instruction ref-lookup)
        (t/attach-instruction ref-new)
        (t/attach-instruction ref-peek)
        (t/attach-instruction ref->vector)

        aspects/make-set-able
        aspects/make-equatable
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        ))
