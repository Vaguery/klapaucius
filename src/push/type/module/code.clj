(ns push.type.module.code
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.util.stack-manipulation :as stacks]
            [push.util.code-wrangling :as u]
            [push.instructions.aspects :as aspects]
            [clojure.math.numeric-tower :as math]
            [push.util.numerics :as n]
            ))




;; INSTRUCTIONS


(def code-append
  (core/build-instruction
    code-append
    "`code-append` concatenates the top :code item to the end of the second :code item. If either argument isn't a list, it's made into one first. If the result would be larger than :max-collection-size points, it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:arg2] #(if (coll? %1) %1 (list %1)) :as :list2)
    (d/calculate [:list1 :list2] #(concat %1 %2) :as :both)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:both :limit] #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :code :result)))



(def code-atom?
  (t/simple-1-in-predicate
    "`:code-atom?` pushes `true` if the top `:code` item is not a collection"
      :code "atom?" #(not (coll? %1))))



(def code-cons 
  (core/build-instruction
    code-cons
    "`:code-cons` pops the top two `:code` items. If the first one is a list, it conjoins the second item to that; if it's not a list, it makes it one, then conjoins. If the result would be larger than :max-code-points it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :item2)
    (d/consume-top-of :code :as :item1)
    (d/calculate [:item2] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :item1] #(conj %1 %2) :as :conjed)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:conjed :limit]
      #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :code :result)))



(def code-container (t/simple-2-in-1-out-instruction
  "`:code-container` pops the top two `:code` items. It performs a depth-first traversal of the second code item (if it is a list or not), looking for duplicates of the first item. If it finds one, then the _parent_ node of the tree is returned as a list. If the item is not found, or there is no parent (the two items are identical), there is no return value."
  :code "container" #(first (u/containers-in %1 %2))))



(def code-contains?
  (core/build-instruction
    code-contains?
    "`:code-contains?` pops the top two items from the `:code` stack, and pushes `true` if the second one is contained (as any subtree) in the first, or if they are identical."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2] #(u/contains-anywhere? %1 %2) :as :found)
    (d/push-onto :boolean :found)))



(def code-do
  (core/build-instruction
    code-do
    "`:code-do` makes a copy of the top `:code` item, and builds a continuation
      `'([top code item] :code-pop)`
      which is pushed to the `:exec` stack"
    :tags #{:complex :base}
    (d/save-top-of :code :as :do-this)
    (d/calculate [:do-this] #(list %1 :code-pop) :as :continuation)
    (d/push-onto :exec :continuation)))



(def code-do*
  (core/build-instruction
    code-do*
    "`:code-do*` pops the top `:code` item, and pushes it onto the `:exec` stack"
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/push-onto :exec :do-this)))



(def code-do*count
  (core/build-instruction
    code-do*count
    "`:code-do*count` pops the top item of `:code` and the top `:scalar`. It constructs a continuation depending on whether the `:scalar` is positive:

      - `[s]` positive?: `'([s] 0 :code-quote [code] :code-do*range)`
      - `[s]` zero or negative?: `'([s] :code-quote [code])`

    This continuation is pushed to the `:exec` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :counter)
    (d/calculate [:counter] #(pos? %1) :as :go?)
    (d/calculate
      [:do-this :counter :go?] 
      #(if %3 
        (list %2 0 :code-quote %1 :code-do*range) 
        (list %2 :code-quote %1)) :as :continuation)
    (d/push-onto :exec :continuation)))



(def code-do*range
  (core/build-instruction
    code-do*range
    "`:code-do*range` pops the top item of `:code` and the top two `:scalar` values (call them `end` and `start`, respectively, with `end` being the top `:scalar` item). It constructs a continuation depending on the relation between the `end` and `start` values:

      - `end` > `(inc start)`: `'([start] [code] ((inc [start]) [end] :code-quote [code] :code-do*Range))`
      - `end` < `(dec start)`: `'([start] [code] ((dec [start]) [end] :code-quote [code] :code-do*Range))`
      - (`end` - `start`) ≤ 1: `'((dec [start]) [code])`

    This continuation is pushed to the `:exec` stack."
    :tags #{:complex :base}
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
          (list %2 %1 (list %4 %3 :code-quote %1 :code-do*range))) :as :continuation)
    (d/push-onto :exec :continuation)))



(def code-do*times
  (core/build-instruction
    code-do*times
    "`:code-do*times` pops the top item of `:code` and the top `:scalar` value (call it `counter`). It constructs a continuation depending on whether `counter` is positive, zero or negative:

      - `counter` ≤ 0: `[code]` (the popped `:code` item)
      - `counter` > 0: `'([code] ((dec [counter]) :code-quote [code] :code-do*times))`

    This continuation is pushed to the `:exec` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :do-this)
    (d/consume-top-of :scalar :as :count)
    (d/calculate [:count] #((complement pos?) %1) :as :done?)
    (d/calculate [:count] #(dec' %1) :as :next)
    (d/calculate
      [:do-this :count :next :done?] 
      #(if %4
           %1
           (list %1 (list %3 :code-quote %1 :code-do*times))) :as :continuation)
    (d/push-onto :exec :continuation)))



(def code-drop      
  (core/build-instruction
    code-drop
    "`:code-drop` pops the top `:code` and `:scalar` items. It wraps the `:code` item in a list if it isn't one, and forces the scalar into an index range. It then pushes the result of `(drop index code)`."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :i]
      #(if (empty? %1) 0 (n/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:list :idx] #(drop %2 %1) :as :result)
    (d/push-onto :code :result)))



(def code-extract
  (core/build-instruction
    code-extract
    "`:code-extract` pops the top `:code` and `:scalar` stacks. It counts the number of code points (that is, lists and items in lists, not other collections) in the `:code` item, then forces the `:scalar` to a suitable index range. It then returns the indexed component of the `:code`, using a depth-first traversal."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(u/count-code-points %1) :as :size)
    (d/calculate [:size :i] #(n/scalar-to-index %2 %1) :as :idx)
    (d/calculate [:c :idx] #(u/nth-code-point %1 %2) :as :result)
    (d/push-onto :code :result)))



(def code-first (t/simple-1-in-1-out-instruction 
  "`:code-first` examines the top `:code` item to determine if it's a code block (not a vector, map, record or other collection type!) If it is, the function returns its first item, otherwise the item itself it returned."
  :code "first" #(if (seq? %) (first %) %)))



(def code-if
  (core/build-instruction
    code-if
    "`:code-if` pops two items from the `:code` stack and one from the `:boolean` stack. If the `:boolean` is `true`, it pushes the second `:code` item back; if false, the first `:code` item. The other popped `:code` item is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :boolean :as :which)
    (d/calculate [:which :arg1 :arg2] #(if %1 %2 %3) :as :that)
    (d/push-onto :exec :that)))



(def code-insert
  (core/build-instruction
    code-insert
    "`:code-insert` pops the top two `:code` items (call them `A` and `B` respectively), and the top `:scalar`. It counts the number of code points in `B` (that is, lists and items in lists, not other collections), then forces the `:scalar` to a suitable index range. It then pushes the result when the indexed node of `B` is replaced with `A`. If the result would be larger than :max-collection-size, it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :a)
    (d/consume-top-of :code :as :b)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:b] #(u/count-code-points %1) :as :size)
    (d/calculate [:i :size] #(n/scalar-to-index %1 %2) :as :idx)
    (d/calculate [:a :b :idx] #(u/replace-nth-in-code %2 %1 %3) :as :replaced)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:replaced :limit] #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :code :result)))



(def code-length
  (core/build-instruction
    code-length
    "`:code-length` pops the top `:code` item, and if it is a Collection it counts the number of items in its root and pushes that value to the `:scalar` stack. 1 is pushed to `:scalar` for items that are not Collections."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(if (coll? %1) (count %1) 1) :as :len)
    (d/push-onto :scalar :len)))



(def code-list
  (core/build-instruction
    code-list
    "`:code-list` pops the top two items from the `:code` stack, returning a list of two elements: of the first item, then the second. If the result would be larger than :max-collection-size, it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2] #(list %1 %2) :as :both)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:both :limit]
      #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :code :result)))



(def code-map
  (core/build-instruction
    code-map
    "`:code-map` pops the top items of the `:code` and `:exec` stacks (call them \"C\" and \"E\", respectively), and pushes a continuation to `:exec`. If C is a list of 2 or more elements, `'(:code-quote () (:code-quote (first C) E) :code-cons (:code-quote (rest C) :code-reduce E))`; if a list of 1 item, `'(:code-quote () (:code-quote (first C) E) :code-cons)`; if an empty list, no continuation results; if not a list, `'(:code-quote () (:code-quote C E) :code-cons)`. If the continuation would be larger than :max-collection-size it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :item)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:item] #(if (seq? %1) %1 (list %1)) :as :collection)
    (d/calculate [:collection :fn]
      #(cond  (empty? %1)
                nil
              (= 1 (count %1))
                (list :code-quote '() 
                      (list :code-quote (first %1) %2) :code-cons)
              :else
                (list :code-quote '() 
                      (list :code-quote (first %1) %2) :code-cons
                      (list :code-quote (rest %1) :code-reduce %2)))
      :as :continuation)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:continuation :limit]
      #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :exec :result)))



(def code-reduce
  (core/build-instruction
    code-reduce
    "`:code-reduce` pops the top items of the `:code` and `:exec` stacks (call them \"C\" and \"E\", respectively), and pushes a continuation to `:exec`. If C is a list of 2 or more elements, `'((:code-quote (first C) E) :code-cons (:code-quote (rest C) :code-reduce E ))`; if a list of 1 item, `'((:code-quote (first C) E) :code-cons)`; if an empty list, no continuation results; if not a list, `'((:code-quote C E) :code-cons)`. If the continuation would be larger than :max-collection-size it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :item)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:item] #(if (seq? %1) %1 (list %1)) :as :collection)
    (d/calculate [:collection :fn]
      #(
        cond  (empty? %1)
                nil
              (= 1 (count %1))
                (list (list :code-quote (first %1) %2) :code-cons)
              :else
                (list (list :code-quote (first %1) %2) :code-cons
                      (list :code-quote (rest %1) :code-reduce %2)))
      :as :continuation)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:continuation :limit]
      #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :exec :result)))



(def code-member?
  (core/build-instruction
    code-member?
    "`:code-member?` pops two items from the `:code` stack; call them `A` (the top one) and `B` (the second one). First, if `A` is not a Collection, it is wrapped in a list. The result of `true` is pushed to the `:boolean` stack if `B` is a member of this modified `A`, or `false` if not."
    :tags #{:complex :predicate :base}
    (d/consume-top-of :code :as :arg1)
    (d/consume-top-of :code :as :arg2)
    (d/calculate [:arg1] #(if (coll? %1) %1 (list %1)) :as :list1)
    (d/calculate [:list1 :arg2] #(not (not-any? #{%2} %1)) :as :present)
    (d/push-onto :boolean :present)))



(def code-noop
  (core/build-instruction
    code-noop
    "`:code-noop` has no effect on the stacks."
    :tags #{:complex :base}))



(def code-nth
  (core/build-instruction
    code-nth
    "`:code-nth` pops the top `:code` and `:scalar` items. It wraps the `:code` item in a list if it isn't one, and forces the scalar into an index range by taking `(mod scalar (count code)`. It then pushes the indexed item of the (listified) `:code` item onto the `:code` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :c)
    (d/consume-top-of :scalar :as :i)
    (d/calculate [:c] #(if (seq? %1) %1 (list %1)) :as :list)
    (d/calculate [:list :i]
      #(if (empty? %1) 0 (n/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:list :idx] #(if (empty? %1) nil (nth %1 %2)) :as :result)
    (d/push-onto :code :result)))



(def code-null? (t/simple-1-in-predicate
  "`:code-null?` pushes `true` if the top `:code` item is an empty collection"
  :code "null?" #(and (coll? %) (empty? %))))



(def code-points
  (core/build-instruction
    code-points
    "`:code-points` pops the top item from the `:code` stack, and treats it as a tree of seqs and non-seq items. If it is an empty list, or any literal (including a vector, map, set or other collection type), the result is 1; if it is a list containing items, they are also counted, including any contents of sub-lists, and so on. _Note_ the difference from `:code-size`, which counts contents of all Collections, not just (code) lists. The result is pushed to the `:scalar` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(u/count-code-points %1) :as :size)
    (d/push-onto :scalar :size)))



(def code-position
  (core/build-instruction
    code-position
    "`:code-position` pops the top two `:code` items (call them `A` and `B`, respectively). It pushes the index of the first occurrence of `A` in `B`, or -1 if it is not found."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(if (seq? %1) %1 (list %1)) :as :listed)
    (d/calculate [:listed :arg2] #(.indexOf %1 %2) :as :idx)
    (d/push-onto :scalar :idx)))



(def code-quote
  (core/build-instruction
    code-quote
    "`:code-quote` pops the top item from the `:exec` stack and puts it onto the `:code` stack."
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg1)
    (d/push-onto :code :arg1)))



(def code-rest (t/simple-1-in-1-out-instruction
  "`:code-rest` examines the top `:code` item; if it's a Collection, it removes
  the first item and returns the reduced list; if it's not a Collection, it returns
  the original item"
  :code "rest" #(if (coll? %1) (rest %1) (list))))



(def code-size
  (core/build-instruction
    code-size
    "`:code-size` pops the top item from the `:code` stack, and totals the number of items it contains anywhere, in any nested Collection of any type. The root of the item counts as 1, and every element (including sub-Collections) nested inside that add 1 more. Items in lists, vectors, sets, and maps are counted. Maps are counted as a collection of key-value pairs, each key and value are an item in a pair, and if they themselves are nested items those are traversed as well. (_Note_ that this differs from `:code-points` by counting the contents of Collections, as opposed to lists only.) The result is pushed to the `:scalar` stack."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1] #(u/count-collection-points %1) :as :size)
    (d/push-onto :scalar :size)))



(def code-subst
  (core/build-instruction
    code-subst
    "`:code-subst` pops the top three `:code` items (call them `A` `B` and `C`, respectively). It replaces all occurrences of `B` in `C` (in a depth-first traversal) with `A`. If the result is larger than max-collection-size, it is discarded."
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg3)
    (d/consume-top-of :code :as :arg2)
    (d/consume-top-of :code :as :arg1)
    (d/calculate [:arg1 :arg2 :arg3] #(u/replace-in-code %1 %2 %3) :as :replaced)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:replaced :limit] #(if (< (u/count-code-points %1) %2) %1 nil) :as :result)
    (d/push-onto :code :result)))



(def code-wrap (t/simple-1-in-1-out-instruction
  "`:code-wrap` puts the top item on the `:code` stack into a one-element list"
  :code "wrap" #(list %1)))



(def code-return
  (core/build-instruction
    code-return
    "`:code-return` pops the top `:code` item, wraps it in a list with :code-quote, and pushes that form onto the :return stack"
    :tags #{:complex :base}
    (d/consume-top-of :code :as :arg)
    (d/calculate [:arg] #(list :code-quote %1) :as :form)
    (d/push-onto :return :form)))



(def code-module
  ( ->  (t/make-module  :code
                        :attributes #{:complex :base})
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
        (t/attach-instruction , code-atom?)
        (t/attach-instruction , code-cons)
        (t/attach-instruction , code-container)
        (t/attach-instruction , code-contains?)
        (t/attach-instruction , code-do)
        (t/attach-instruction , code-do*)
        (t/attach-instruction , code-do*count)
        (t/attach-instruction , code-do*range)
        (t/attach-instruction , code-do*times)
        (t/attach-instruction , code-drop)
        (t/attach-instruction , code-extract)
        (t/attach-instruction , code-first)
        (t/attach-instruction , code-if)
        (t/attach-instruction , code-insert)
        (t/attach-instruction , code-length)
        (t/attach-instruction , code-list)
        (t/attach-instruction , code-map)
        (t/attach-instruction , code-member?)
        (t/attach-instruction , code-noop)
        (t/attach-instruction , code-nth)
        (t/attach-instruction , code-null?)
        (t/attach-instruction , code-points)
        (t/attach-instruction , code-position)
        (t/attach-instruction , code-quote)
        (t/attach-instruction , code-reduce)
        (t/attach-instruction , code-rest)
        (t/attach-instruction , code-return)
        (t/attach-instruction , code-size)
        (t/attach-instruction , code-subst)
        (t/attach-instruction , code-wrap)
        ))
