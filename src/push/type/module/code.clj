(ns push.type.module.code
  (:require [push.instructions.dsl        :as d]
            [push.instructions.core       :as i]
            [push.util.numerics           :as n]
            [push.type.core               :as t]
            [push.util.code-wrangling     :as u]
            [push.instructions.aspects    :as aspects]
            [push.util.stack-manipulation :as stacks]
            [push.type.definitions.quoted :as qc]
            ))


;; helper

(defn q!
  [item]
  (qc/push-quote item))

;;

(def code-append
  (i/build-instruction
    code-append
    "`code-append` concatenates the top :code item to the end of the second :code item. If either argument isn't a list, it's made into one first. If the result would be larger than :max-collection-size points, it is discarded and an `:error` is pushed instead"

    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:arg2] #(if (coll? %1) %1 (list %1)) :as :list2)
    (d/calculate [:list1 :list2]
      #(q! (u/list! (concat %1 %2))) :as :result)
    (d/return-item :result)
    ))


(def code-append!
  (i/build-instruction
    code-append!
    "`code-append!` returns the code block `(:code-append :code-do*)`"

    (d/calculate [] #(list :code-append :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-atom?
  (i/simple-1-in-predicate
    "`:code-atom?` returns `true` if the top `:code` item is not a collection"
      :code
      "atom?"
      #(not (coll? %1))
      ))


(def code-cons
  (i/build-instruction
    code-cons
    "`:code-cons` pops the top two `:code` items. If the first one is a list, it conjoins the second item to that; if it's not a list, it makes it one, then conjoins."

    (d/consume-top-of :code :as :item2)
    (d/consume-top-of :code :as :item1)
    (d/calculate [:item2] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :item1]
      #(q! (u/list! (conj %1 %2))) :as :result)
    (d/return-item :result)
    ))


(def code-cons!
  (i/build-instruction
    code-cons!
    "`code-cons!` returns the code block `(:code-cons :code-do*)`"

    (d/calculate [] #(list :code-cons :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-container (i/simple-2-in-1-out-instruction
  "`:code-container` pops the top two `:code` items. It performs a depth-first traversal of the second code item (if it is a list or not), looking for duplicates of the first item. If it finds one, then the _parent_ node of the tree is returned as quoted code. If the item is not found, or there is no parent (the two items are identical), there is no return value."
    :code
    "container"
    #(q! (first (u/containers-in %1 %2)))
    ))


(def code-container!
  (i/build-instruction
    code-container!
    "`code-container!` returns the code block `(:code-container :code-do*)`"

    (d/calculate [] #(list :code-container :code-do*) :as :result)
    (d/return-item :result)
    ))



(def code-contains?
  (i/build-instruction
    code-contains?
    "`:code-contains?` pops the top two items from the `:code` stack, and returns `true` if the second one is contained (as any subtree) in the first, or if they are identical."

    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2] #(u/contains-anywhere? %1 %2) :as :found)
    (d/return-item :found)
    ))



(def code-do
  (i/build-instruction
    code-do
    "`:code-do` pops the top `:code` item, and returns a continuation `'([quoted top code item] [unquoted top code item] :code-pop)`"

    (d/consume-top-of :code :as :do-this)
    (d/calculate [:do-this]
      #(list (q! %1) %1 :code-pop) :as :continuation)
    (d/return-item :continuation)
    ))



(def code-do*
  (i/build-instruction
    code-do*
    "`:code-do*` pops the top `:code` item, and returns it (unquoted)"

    (d/consume-top-of :code :as :do-this)
    (d/return-item :do-this)
    ))



(def code-do*count
  (i/build-instruction
    code-do*count
    "`:code-do*count` pops the top item of `:code` and the top `:scalar`. It constructs a continuation, depending on whether the `:scalar` is positive:

      - `[s]` positive?: `'([s] 0 [quoted code] :code-do*range)`
      - `[s]` zero or negative?: `'([s] [quoted code])`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] pos? :as :go?)
    (d/calculate
      [:do-this :counter :go?]
      #(if %3
        (list %2 0 (q! %1) :code-do*range)
        (list %2 (q! %1))) :as :continuation)
    (d/return-item :continuation)
    ))



(def code-do*range
  (i/build-instruction
    code-do*range
    "`:code-do*range` pops the top item of `:code` and the top two `:scalar` values (call them `end` and `start`, respectively, with `end` being the top `:scalar` item). It constructs a continuation depending on the relation between the `end` and `start` values:

      - `end` > `(inc start)`: `'([start] [code] ((inc [start]) [end] [quoted code] :code-do*Range))`
      - `end` < `(dec start)`: `'([start] [code] ((dec [start]) [end] [quoted code] :code-do*Range))`
      - (`end` - `start`) ≤ 1: `'((dec [start]) [unquoted code])`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:start :end] #(n/within-1? %1 %2) :as :done?)
    (d/calculate [:start :end] #(+' %1 (compare %2 %1)) :as :next)
    (d/calculate
      [:do-this :start :end :next :done?]
      #(cond
        (nil? %4) nil
        (nil? %5) nil
        %5 (list %4 %1)
        :else
          (list %2 %1 (list %4 %3 (q! %1) :code-do*range))) :as :continuation)
    (d/return-item :continuation)
    ))



(def code-do*times
  (i/build-instruction
    code-do*times
    "`:code-do*times` pops the top item of `:code` and the top `:scalar` value (call it `counter`). It constructs a continuation depending on whether `counter` is positive, zero or negative:

      - `counter` ≤ 0: `[code]` (the popped `:code` item)
      - `counter` > 0: `'([unquoted code] ((dec [counter]) [quoted code] :code-do*times))`

    This continuation is pushed to the `:exec` stack."

    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :count)
    (d/calculate [:count] #((complement pos?) %1) :as :done?)
    (d/calculate [:count] dec' :as :next)
    (d/calculate
      [:do-this :count :next :done?]
      #(if %4
           %1
           (list %1 (list %3 (q! %1) :code-do*times))) :as :continuation)
    (d/return-item :continuation)
    ))



(def code-drop
  (i/build-instruction
    code-drop
    "`:code-drop` pops the top `:code` and `:scalar` items. It wraps the `:code` item in a list if it isn't one, and forces the scalar into an index range. It then returns the result of `(drop index code)`."

    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :i]
      #(if (empty? %1) 0 (n/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:list :idx]
      #(q! (drop %2 %1)) :as :result)
    (d/return-item :result)
    ))


(def code-drop!
  (i/build-instruction
    code-drop!
    "`code-drop!` returns the code block `(:code-drop :code-do*)`"

    (d/calculate [] #(list :code-drop :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-extract
  (i/build-instruction
    code-extract
    "`:code-extract` pops the top `:code` and `:scalar` stacks. It counts the number of code points (that is, lists and items in lists, not other collections) in the `:code` item, then forces the `:scalar` to a suitable index range. It then returns the indexed component of the `:code`, using a depth-first traversal."

    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(u/count-code-points %1) :as :size)
    (d/calculate [:size :i]
      #(n/scalar-to-index %2 %1) :as :idx)
    (d/calculate [:c :idx]
      #(q! (u/nth-code-point %1 %2)) :as :result)
    (d/return-item :result)
    ))


(def code-extract!
  (i/build-instruction
    code-extract!
    "`code-extract!` returns the code block `(:code-extract :code-do*)`"

    (d/calculate [] #(list :code-extract :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-first (i/simple-1-in-1-out-instruction
  "`:code-first` examines the top `:code` item to determine if it's a code block (not a vector, map, record or other collection type!) If it is, the function returns its first item, otherwise the item itself it returned."
  :code
  "first"
  #(q! (if (seq? %) (first %) %))
  ))


(def code-first!
  (i/build-instruction
    code-first!
    "`code-first!` returns the code block `(:code-first :code-do*)`"

    (d/calculate [] #(list :code-first :code-do*) :as :result)
    (d/return-item :result)
    ))



(def code-if
  (i/build-instruction
    code-if
    "`:code-if` pops two items from the `:code` stack and one from the `:boolean` stack. If the `:boolean` is `true`, it returns the first `:code` item (quoted); if false, it returns the second `:code` item."

    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :boolean :as :which)
    (d/calculate [:which :arg1 :arg2]
      #(q! (if %1 %2 %3)) :as :that)
    (d/return-item :that)
    ))



(def code-insert
  (i/build-instruction
    code-insert
    "`:code-insert` pops the top two `:code` items (call them `A` and `B` respectively), and the top `:scalar`. It counts the number of code points in `B` (that is, lists and items in lists, not other collections), then forces the `:scalar` to a suitable index range. It then pushes the result when the indexed node of `B` is replaced with `A`."

    (d/consume-top-of :code :as :a)
    (d/consume-top-of :code :as :b)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:b] #(u/count-code-points %1) :as :size)
    (d/calculate [:i :size] #(n/scalar-to-index %1 %2) :as :idx)
    (d/calculate [:a :b :idx]
      #(q! (u/replace-nth-in-code %2 %1 %3)) :as :result)
    (d/return-item :result)
    ))


(def code-insert!
  (i/build-instruction
    code-insert!
    "`code-insert!` returns the code block `(:code-insert :code-do*)`"

    (d/calculate [] #(list :code-insert :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-length
  (i/build-instruction
    code-length
    "`:code-length` pops the top `:code` item, and if it is a collection (including a :vector, :set, :tagspace or other thing) it counts the number of items in its root and returns that value, or 1 if it is not a collection."

    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(if (coll? %1) (count %1) 1) :as :len)
    (d/return-item :len)
    ))



(def code-list
  (i/build-instruction
    code-list
    "`:code-list` pops the top two items from the `:code` stack, returning a quoted codeblock containing the two elements."

    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(q! (list %1 %2)) :as :result)
    (d/return-item :result)
    ))


(def code-list!
  (i/build-instruction
    code-list!
    "`code-list!` returns the code block `(:code-list :code-do*)`"

    (d/calculate [] #(list :code-list :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-map
  (i/build-instruction
    code-map
    "`:code-map` pops the top items of the `:code` and `:exec` stacks (call them \"C\" and \"E\", respectively), and pushes a continuation to `:exec`. If C is a list of 2 or more elements, `'([quoted '()] ([quoted (first C)] E) :code-cons ([quoted (rest C)] :code-reduce E))`; if a list of 1 item, `'([quoted '()] ([quoted (first C)] E) :code-cons)`; if an empty list, no continuation results; if not a list, `'([quoted '()] ([quoted C] E) :code-cons)`."

    (d/consume-top-of :code :as :item)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:item] #(if (seq? %1) %1 (list %1)) :as :collection)
    (d/calculate [:collection :fn]
      #(cond  (empty? %1)
                nil
              (= 1 (count %1))
                (list (q! '())
                      (list (q! (first %1)) %2) :code-cons)
              :else
                (list (q! '())
                      (list (q! (first %1)) %2) :code-cons
                      (list (q! (rest %1)) :code-reduce %2)))
      :as :results)
    (d/return-item :results)
    ))



(def code-reduce
  (i/build-instruction
    code-reduce
    "`:code-reduce` pops the top items of the `:code` and `:exec` stacks (call them \"C\" and \"E\", respectively), and pushes a continuation to `:exec`. If C is a list of 2 or more elements, `'(([quoted (first C)] E) :code-cons ([quoted (rest C)] :code-reduce E ))`; if a list of 1 item, `'(([quoted (first C)] E) :code-cons)`; if an empty list, no continuation results; if not a list, `'(([quoted C] E) :code-cons)`."

    (d/consume-top-of :code :as :item)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:item] #(if (seq? %1) %1 (list %1)) :as :collection)
    (d/calculate [:collection :fn]
      #(
        cond  (empty? %1)
                nil
              (= 1 (count %1))
                (list (list (q! (first %1)) %2) :code-cons)
              :else
                (list (list (q! (first %1)) %2) :code-cons
                      (list (q! (rest %1)) :code-reduce %2)))
      :as :results)
    (d/return-item :results)
    ))



(def code-member?
  (i/build-instruction
    code-member?
    "`:code-member?` pops two items from the `:code` stack; call them `A` (the top one) and `B` (the second one). First, if `A` is not a code block, it is wrapped in a list. The result of `true` is pushed to the `:boolean` stack if `B` is a member of this modified `A`, or `false` if not."

    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :code :as :arg2)
    (d/calculate [:arg1] #(if (seq? %1) %1 (list %1)) :as :list1)
    (d/calculate [:list1 :arg2] #(not (not-any? #{%2} %1)) :as :present)
    (d/return-item :present)
    ))



(def code-noop
  (i/build-instruction
    code-noop
    "`:code-noop` has no effect on the interpreter beyond incrementing the counter"
    ))



(def code-nth
  (i/build-instruction
    code-nth
    "`:code-nth` pops the top `:code` and `:scalar` items. It wraps the `:code` item in a list if it isn't one, and forces the scalar into an index range by taking `(mod scalar (count code)`. It then returns the indexed item of the (listified) `:code` item."

    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :i]
      #(if (empty? %1) 0 (n/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:list :idx]
      #(q! (when (seq %1) (nth %1 %2))) :as :result)
    (d/return-item :result)
    ))


(def code-nth!
  (i/build-instruction
    code-nth!
    "`code-nth!` returns the code block `(:code-nth :code-do*)`"

    (d/calculate [] #(list :code-nth :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-null? (i/simple-1-in-predicate
  "`:code-null?` pushes `true` if the top `:code` item is an empty code block"
  :code
  "null?"
  #(and (seq? %) (empty? %))
  ))


(def code-points
  (i/build-instruction
    code-points
    "`:code-points` pops the top item from the `:code` stack, and treats it as a tree of seqs and non-seq items. If it is an empty list, or any literal (including a vector, map, set or other collection type), the result is 1; if it is a list containing items, they are also counted, including any contents of sub-lists, and so on. _Note_ the difference from `:code-size`, which counts contents of all collections (including :set and :vector elements), not just (code) lists."

    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(u/count-code-points %1) :as :size)
    (d/return-item :size)
    ))



(def code-position
  (i/build-instruction
    code-position
    "`:code-position` pops the top two `:code` items (call them `A` and `B`, respectively). It pushes the index of the first occurrence of `A` in `B`, or -1 if it is not found."

    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (seq? %1) %1 (list %1)) :as :listed)
    (d/calculate [:listed :arg2] #(.indexOf %1 %2) :as :idx)
    (d/return-item :idx)
    ))



(def code-quote
  (i/build-instruction
    code-quote
    "`:code-quote` pops the top item from the `:exec` stack and converts it to QuotedCode"

    (d/consume-top-of :exec :as :arg)
    (d/calculate [:arg] #(q! %1) :as :result)
    (d/return-item :result)
    ))



(def code-rest (i/simple-1-in-1-out-instruction
  "`:code-rest` examines the top `:code` item; if it's a code block, it removes
  the first item and returns the reduced list, otherwise it returns an empty code block"
  :code
  "rest"
  #(q! (if (seq? %1) (rest %1) (list)))
  ))


(def code-rest!
  (i/build-instruction
    code-rest!
    "`code-rest!` returns the code block `(:code-rest :code-do*)`"

    (d/calculate [] #(list :code-rest :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-size
  (i/build-instruction
    code-size
    "`:code-size` pops the top item from the `:code` stack, and totals the number of items it contains anywhere, in any nested Collection of any type. The root of the item counts as 1, and every element (including sub-Collections) nested inside that add 1 more. Items in lists, vectors, sets, and maps are counted. Maps are counted as a collection of key-value pairs, each key and value are an item in a pair, and if they themselves are nested items those are traversed as well. (_Note_ that this differs from `:code-points` by counting the contents of all collections, as opposed to lists only.) Includes items in :set, :vector, :tagspace and other Record types."

    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(u/count-collection-points %1) :as :size)
    (d/return-item :size)
    ))



(def code-subst
  (i/build-instruction
    code-subst
    "`:code-subst` pops the top three `:code` items (call them `A` `B` and `C`, respectively). It replaces all occurrences of `B` in `C` (in a depth-first traversal) with `A`."

    (d/consume-top-of :code :as :arg3)
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2 :arg3]
      #(q! (u/replace-in-code %1 %2 %3)) :as :result)
    (d/return-item :result)
    ))


(def code-subst!
  (i/build-instruction
    code-subst!
    "`code-subst!` returns the code block `(:code-subst :code-do*)`"

    (d/calculate [] #(list :code-subst :code-do*) :as :result)
    (d/return-item :result)
    ))



(def code-wrap (i/simple-1-in-1-out-instruction
  "`:code-wrap` puts the top item on the `:code` stack into a one-element list"
  :code
  "wrap"
  #(q! (list %1))
  ))


(def code-wrap!
  (i/build-instruction
    code-wrap!
    "`code-wrap!` returns the code block `(:code-wrap :code-do*)`"

    (d/calculate [] #(list :code-wrap :code-do*) :as :result)
    (d/return-item :result)
    ))


(def code-return
  (i/build-instruction
    code-return
    "`:code-return` pops the top `:code` item and pushes it (quoted) on the :return stack"

    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(q! %1) :as :form)
    (d/push-onto :return :form)
    ))



(def code-module
  ( ->  (t/make-module  :code
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
        (t/attach-instruction , code-append)
        (t/attach-instruction , code-append!)
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-cons)
        (t/attach-instruction , code-cons!)
        (t/attach-instruction , code-container)
        (t/attach-instruction , code-container!)
        (t/attach-instruction , code-contains?)
        (t/attach-instruction , code-do)
        (t/attach-instruction , code-do*)
        (t/attach-instruction , code-do*count)
        (t/attach-instruction , code-do*range)
        (t/attach-instruction , code-do*times)
        (t/attach-instruction , code-drop)
        (t/attach-instruction , code-drop!)
        (t/attach-instruction , code-extract)
        (t/attach-instruction , code-extract!)
        (t/attach-instruction , code-first)
        (t/attach-instruction , code-first!)
        (t/attach-instruction , code-if)
        (t/attach-instruction , code-insert)
        (t/attach-instruction , code-insert!)
        (t/attach-instruction , code-length)
        (t/attach-instruction , code-list)
        (t/attach-instruction , code-list!)
        (t/attach-instruction , code-map)
        (t/attach-instruction , code-member?)
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-nth)
        (t/attach-instruction , code-nth!)
        (t/attach-instruction , code-null?)
        (t/attach-instruction , code-points)
        (t/attach-instruction , code-position)
        (t/attach-instruction , code-quote)
        (t/attach-instruction , code-reduce)
        (t/attach-instruction , code-rest)
        (t/attach-instruction , code-rest!)
        (t/attach-instruction , code-return)
        (t/attach-instruction , code-size)
        (t/attach-instruction , code-subst)
        (t/attach-instruction , code-subst!)
        (t/attach-instruction , code-wrap)
        (t/attach-instruction , code-wrap!)
        ))
