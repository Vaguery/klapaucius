(ns push.type.item.tagspace
  (:require [push.instructions.dsl          :as d]
            [push.type.item.generator       :as g]
            [push.instructions.core         :as i]
            [push.util.numerics             :as n]
            [push.type.core                 :as t]
            [push.instructions.aspects      :as aspects]
            [push.type.definitions.tagspace :as ts]
            [push.util.code-wrangling       :as util]
            [dire.core                      :refer [with-handler!]]
            ))


(def tagspace-count
  (i/build-instruction
    tagspace-count
    "`:tagspace-count` pops the top `:tagspace` item and pushes a list containing the number of keys and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (count (:contents %1)) %1) :as :countlist)
    (d/return-item :countlist)))



(def tagspace-keys
  (i/build-instruction
    tagspace-keys
    "`:tagspace-keys` pops the top `:tagspace` item and pushes a list containing all of its keys (as a list) and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (or (keys (:contents %1)) (list)) %1) :as :keylist)
    (d/return-item :keylist)))



(def tagspace-keyset
  (i/build-instruction
    tagspace-keyset
    "`:tagspace-keyset` pops the top `:tagspace` item and pushes a `:set` containing all of its keys and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg]
      #(list (set (or (keys (:contents %1)) (list))) %1) :as :keyset)
    (d/return-item :keyset)))



(def tagspace-keyvector
  (i/build-instruction
    tagspace-keyvector
    "`:tagspace-keyvector` pops the top `:tagspace` item and pushes a code block containing all of its keys (as a `:vector`) and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg]
      #(list (vec (or (keys (:contents %1)) (list))) %1) :as :keyvec)
    (d/return-item :keyvec)))




(def tagspace-lookup
  (i/build-instruction
    tagspace-lookup
    "`:tagspace-lookup` pops the top `:scalar` and the top `:tagspace`. The indicated item is looked up and pushed to `:exec` in a codeblock containing `(tagspace item)`."

    (d/consume-top-of :scalar :as :idx)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:idx :ts] #(ts/find-in-tagspace %2 %1) :as :item)
    (d/calculate [:ts :item]
      #(if (nil? %2) (list %1) (list %1 %2)) :as :result)
    (d/return-item :result)
    ))



(def tagspace-lookupscalars
  (i/build-instruction
    tagspace-lookupscalars
    "`:tagspace-lookupscalars` pops the top `:scalars` item and the top `:tagspace`. Every element of the `:scalars` is used as a key and looked up, consolidated into a list, and pushed to `:exec`; if the `:tagspace` is empty, an empty list is pushed to `:exec`. The `:tagspace` is returned to that stack."

    (d/consume-top-of :scalars :as :indices)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:indices :ts]
      #(map (fn [k] (ts/find-in-tagspace %2 k)) %1) :as :items)
    (d/calculate [:items] #(when %1 (remove nil? %1)) :as :items)
    (d/calculate [:ts :items] #(list %1 (util/list! %2)) :as :results)
    (d/return-item :results)
    ))



(def tagspace-lookupvector
  (i/build-instruction
    tagspace-lookupvector
    "`:tagspace-lookupvector` pops the top `:vector` item and the top `:tagspace`. For each item in the `:vector`, if it is not a number it is copied into the result list, and if it is a number it is used to look up an item in the `:tagspace`. If the `:tagspace` is empty, the result will be the `:vector` with all numbers removed. The `:tagspace` is returned to that stack."

    (d/consume-top-of :vector :as :keys)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:keys :ts]
      #(remove nil?
        (map
          (fn [k] (if (number? k) (ts/find-in-tagspace %2 k) k))
          %1)) :as :items)
    (d/calculate [:ts :items] #(list %1 %2) :as :results)
    (d/return-item :results)
    ))



(def tagspace-max
  (i/build-instruction
    tagspace-max
    "`:tagspace-max` pops the top `:tagspace` item and pushes a list containing its highest-valued key and the tagspace itself onto the `:exec` stack. If there is no key, only the tagspace is pushed."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(first (last (seq (:contents %1)))) :as :key)
    (d/calculate [:arg :key] #(if (nil? %2) %1 (list %2 %1)) :as :maxkeylist)
    (d/return-item :maxkeylist)))



(def tagspace-merge
  (i/build-instruction
    tagspace-merge
    "`:tagspace-merge` pops the top two `:tagspace` items (call them A and B respectively) and pushes a new `:tagspace` item with the contents of A merged into B."

    (d/consume-top-of :tagspace :as :arg2)
    (d/consume-top-of :tagspace :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(ts/make-tagspace (merge (:contents %1) (:contents %2))) :as :result)
    (d/return-item :result)
    ))



(def tagspace-min
  (i/build-instruction
    tagspace-min
    "`:tagspace-min` pops the top `:tagspace` item and pushes a list containing its lowest-valued key and the tagspace itself onto the `:exec` stack. If there is no key, only the tagspace is pushed."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(ffirst (seq (:contents %1))) :as :key)
    (d/calculate [:arg :key] #(if (nil? %2) %1 (list %2 %1)) :as :minkeylist)
    (d/return-item :minkeylist)))



(def tagspace-normalize
  (i/build-instruction
    tagspace-normalize
    "`:tagspace-normalize` pops the top `:tagspace` item, and pushes a new `:tagspace` in which the first item is at index 0 and all items are indexed with integers indicating their positional order."

    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts] #(vals (:contents %1)) :as :items)
    (d/calculate [:items] #(ts/make-tagspace (zipmap (range) %1 )) :as :result)
    (d/return-item :result)
    ))



(def tagspace-new
  (i/build-instruction
    tagspace-new
    "`:tagspace-new` creates a new, empty `:tagspace` item and pushes it to the stack."

    (d/calculate [] ts/make-tagspace :as :result)
    (d/return-item :result)))



(def tagspace-offset
  (i/build-instruction
    tagspace-offset
    "`:tagspace-offset` pops the top `:tagspace` item and the top `:scalar`, and pushes a new `:tagspace` in which the numeric keys have all had the `:scalar` added to them."

    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :scalar :as :offset)
    (d/calculate [:arg1 :offset]
      #(ts/make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (+' k %2) v))
          {}
          (:contents %1))) :as :result)
    (d/return-item :result)))



(def tagspace-scale
  (i/build-instruction
    tagspace-scale
    "`:tagspace-scale` pops the top `:tagspace` item and the top `:scalar`, and pushes a new `:tagspace` in which the numeric keys have all been multipled by the `:scalar` (even if it is negative or zero)."

    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :scalar :as :scale)
    (d/calculate [:arg1 :scale]
      #(ts/make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (*' k %2) v))
          {}
          (:contents %1))) :as :result)
    (d/return-item :result)))



(def tagspace-cutoff
  (i/build-instruction
    tagspace-cutoff
    "`:tagspace-cutoff` pops the top `:tagspace` item and the top `:scalar`, and pushes two new `:tagspace` items in a list to `:exec`, which contain all the items with keys _below_ the `:scalar`, and another with all the keys at or above (inclusive)."

    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :scalar :as :cutoff)
    (d/calculate [:arg1 :cutoff]
      #(map
          ts/make-tagspace
          (vals
            (reduce-kv
              (fn [r k v] (if (< k %2)
                            (assoc-in r [:low k] v)
                            (assoc-in r [:high k] v)))
              {:low {} :high {}}
              (:contents %1)))) :as :result)
    (d/return-item :result)))







(def tagspace-tidy
  (i/build-instruction
    tagspace-tidy
    "`:tagspace-tidy` pops the top `:tagspace` item and the top two `:scalar` items (call them END and START respectively), and pushes a new `:tagspace` in which the first item is at index START, the last is at END, all the rest are evenly distributed between. If START and END are identical, then only the last item of the collection will be retained as it will overwrite the others."

    (d/consume-top-of :tagspace :as :ts)
    (d/consume-top-of :scalar :as :end)
    (d/consume-top-of :scalar :as :start)
    (d/calculate [:ts] #(vals (:contents %1)) :as :items)
    (d/calculate [:items] count :as :how-many)
    (d/calculate [:start :end :how-many]
      #(if (< %3 2) 0 (/ (-' %2 %1) (dec %3))) :as :delta)
    (d/calculate [:how-many :start :delta]
      #(when %3 (n/index-maker %1 %2 %3)) :as :indices)
    (d/calculate [:indices :items]
      #(when %1 (ts/make-tagspace (zipmap %1 %2))) :as :result)
    (d/return-item :result)
    ))



(def tagspace-valuefilter
  (i/build-instruction
    tagspace-valuefilter
    "`:tagspace-valuefilter` pops the top `:set` item and the top `:tagspace`, and pushes a new `:tagspace` only containing the _values_ present in the `:set`."

    (d/consume-top-of :set :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(ts/make-tagspace
        (filter
          (fn [kv] (boolean (%2 (second kv))))
          (seq (:contents %1)))) :as :result)
    (d/return-item :result)
    ))



(def tagspace-valueremove
  (i/build-instruction
    tagspace-valueremove
    "`:tagspace-valueremove` pops the top `:set` item and the top `:tagspace`, and pushes a new `:tagspace` only containing the _values_ NOT present in the `:set`."

    (d/consume-top-of :set :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(ts/make-tagspace
        (remove
          (fn [kv] (boolean (%2 (second kv))))
          (seq (:contents %1)))) :as :result)
    (d/return-item :result)
    ))



(def tagspace-valuesplit
  (i/build-instruction
    tagspace-valuesplit
    "`:tagspace-valuesplit` pops the top `:set` item and the top `:tagspace`, and pushes a list containing two new `:tagspace` items: the first has items the _values_ present in the `:set`, the second all the items NOT present."

    (d/consume-top-of :set :as :allowed)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:ts :allowed]
      #(list
        (ts/make-tagspace
          (filter
            (fn [kv] (boolean (%2 (second kv))))
            (seq (:contents %1))))
        (ts/make-tagspace
          (remove
            (fn [kv] (boolean (%2 (second kv))))
            (seq (:contents %1))))) :as :result)
    (d/return-item :result)
    ))



(def tagspace-values
  (i/build-instruction
    tagspace-values
    "`:tagspace-values` pops the top `:tagspace` item and pushes a list containing all of its stored values (as a list) and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (or (vals (:contents %1)) (list)) %1) :as :valList)
    (d/return-item :valList)
    ))



(def tagspace-valueset
  (i/build-instruction
    tagspace-valueset
    "`:tagspace-valueset` pops the top `:tagspace` item and pushes a list containing all of its stored values (as a set) and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg]
      #(list (set (or (vals (:contents %1)) (list))) %1) :as :valSet)
    (d/return-item :valSet)
    ))



(def tagspace-valuevector
  (i/build-instruction
    tagspace-valuevector
    "`:tagspace-valuevector` pops the top `:tagspace` item and pushes a list containing all of its stored values (as a vector) and the tagspace itself onto the `:exec` stack."

    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg]
      #(list (vec (or (vals (:contents %1)) (list))) %1) :as :valSet)
    (d/return-item :valSet)
    ))





(def tagspace-type
  "builds the `:tagspace` collection type, which can hold arbitrary and mixed contents and uses numeric indices"
  (let [typename :tagspace]
  (-> (t/make-type  :tagspace
                    :recognized-by ts/tagspace?
                    :attributes #{:collection :tagspace})
      (t/attach-instruction , tagspace-count)
      (t/attach-instruction , tagspace-keys)
      (t/attach-instruction , tagspace-keyset)
      (t/attach-instruction , tagspace-keyvector)
      (t/attach-instruction , tagspace-lookup)
      (t/attach-instruction , tagspace-lookupscalars)
      (t/attach-instruction , tagspace-lookupvector)
      (t/attach-instruction , tagspace-max)
      (t/attach-instruction , tagspace-merge)
      (t/attach-instruction , tagspace-min)
      (t/attach-instruction , tagspace-new)
      (t/attach-instruction , tagspace-normalize)
      (t/attach-instruction , tagspace-offset)
      (t/attach-instruction , tagspace-scale)
      (t/attach-instruction , tagspace-cutoff)
      (t/attach-instruction , tagspace-tidy)
      (t/attach-instruction , tagspace-valuefilter)
      (t/attach-instruction , tagspace-valueremove)
      (t/attach-instruction , tagspace-values)
      (t/attach-instruction , tagspace-valueset)
      (t/attach-instruction , tagspace-valuesplit)
      (t/attach-instruction , tagspace-valuevector)
      aspects/make-cycling
      aspects/make-equatable
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-repeatable
      aspects/make-returnable
      aspects/make-storable
      aspects/make-taggable
      aspects/make-visible
      )))
