(ns push.types.type.tagspace
  (:require [push.instructions.core :as core]
            [push.types.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            )
  (:use push.types.type.generator))


;; SUPPORT


(defrecord TagSpace [contents])


(defn make-tagspace
  "Creates a new empty tagspace"
  ([] (->TagSpace (sorted-map)))
  ([starting-items] (->TagSpace (into (sorted-map) starting-items)))
  )


(defn tagspace?
  "Returns `true` if the item is a `:tagspace`, and `false` otherwise."
  [item]
  (= (type item) push.types.type.tagspace.TagSpace))


(defn store-in-tagspace
  "Stores an item in the numeric index indicated in the tagspace record"
  [ts item idx]
  (assoc-in ts [:contents idx] item))


(defn find-in-tagspace
  "Takes a tagspace and a numeric key, and returns the last first item at or after the index in the tagspace. If the index is larger than the largest key, it 'wraps around' and returns the first item."
  [ts idx]
  (let [contents (:contents ts)
        keepers (filter (fn [[k v]] (<= idx k)) contents)]
    (if (empty? keepers)
      (second (first contents))
      (second (first keepers)))))


;; INSTRUCTIONS


(def tagspace-count
  (core/build-instruction
    tagspace-count
    "`:tagspace-count` pops the top `:tagspace` item and pushes a list containing the number of keys and the tagspace itself onto the `:exec` stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (count (:contents %1)) %1) :as :countlist)
    (d/push-onto :exec :countlist)))



(def tagspace-keys
  (core/build-instruction
    tagspace-keys
    "`:tagspace-keys` pops the top `:tagspace` item and pushes a list containing all of its keys (as a list) and the tagspace itself onto the `:exec` stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (or (keys (:contents %1)) (list)) %1) :as :keylist)
    (d/push-onto :exec :keylist)))



(def tagspace-lookupint
  (core/build-instruction
    tagspace-lookupint
    "`:tagspace-lookupint` pops the top `:integer` and the top `:tagspace`. The indicated item is looked up and pushed to `:exec`; if the `:tagspace` is empty, no item is pushed to `:exec`. The `:tagspace` is returned to that stack. (Note this behavior differs from most other `:tagspace` functions in that the `:tagspace` is returned immediately.)"
    :tags #{:tagspace :collection}
    (d/consume-top-of :integer :as :idx)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:idx :ts] #(find-in-tagspace %2 %1) :as :result)
    (d/push-onto :exec :result)
    (d/push-onto :tagspace :ts)))



(def tagspace-lookupintegers
  (core/build-instruction
    tagspace-lookupintegers
    "`:tagspace-lookupintegers` pops the top `:integers` item and the top `:tagspace`. Every element of the `:integers` is used as a key and looked up, consolidated into a list, and pushed to `:exec`; if the `:tagspace` is empty, an empty list is pushed to `:exec`. The `:tagspace` is returned to that stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :integers :as :indices)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:indices :ts] 
      #(remove nil? (map (fn [k] (find-in-tagspace %2 k)) %1)) :as :results)
    (d/push-onto :exec :results)
    (d/push-onto :tagspace :ts)))



(def tagspace-lookupfloat
  (core/build-instruction
    tagspace-lookupfloat
    "`:tagspace-lookupfloat` pops the top `:float` and the top `:tagspace`. The indicated item is looked up and pushed to `:exec`; if the `:tagspace` is empty, no item is pushed to `:exec`. The `:tagspace` is returned to that stack. (Note this behavior differs from most other `:tagspace` functions in that the `:tagspace` is returned immediately.)"
    :tags #{:tagspace :collection}
    (d/consume-top-of :float :as :idx)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:idx :ts] #(find-in-tagspace %2 %1) :as :result)
    (d/push-onto :exec :result)
    (d/push-onto :tagspace :ts)))



(def tagspace-lookupfloats
  (core/build-instruction
    tagspace-lookupfloats
    "`:tagspace-lookupfloats` pops the top `:floats` item and the top `:tagspace`. Every element of the `:floats` is used as a key and looked up, consolidated into a list, and pushed to `:exec`; if the `:tagspace` is empty, an empty list is pushed to `:exec`. The `:tagspace` is returned to that stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :floats :as :indices)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:indices :ts]
      #(remove nil? (map (fn [k] (find-in-tagspace %2 k)) %1)) :as :results)
    (d/push-onto :exec :results)
    (d/push-onto :tagspace :ts)))



(def tagspace-lookupvector
  (core/build-instruction
    tagspace-lookupvector
    "`:tagspace-lookupvector` pops the top `:vector` item and the top `:tagspace`. For each item in the `:vector`, if it is not a number it is copied into the result list, and if it is a number it is used to look up an item in the `:tagspace`. If the `:tagspace` is empty, the result will be the `:vector` with all numbers removed. The `:tagspace` is returned to that stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :vector :as :keys)
    (d/consume-top-of :tagspace :as :ts)
    (d/calculate [:keys :ts]
      #(remove nil?
        (map
          (fn [k] (if (number? k) (find-in-tagspace %2 k) k))
          %1)) :as :results)
    (d/push-onto :exec :results)
    (d/push-onto :tagspace :ts)))



(def tagspace-max
  (core/build-instruction
    tagspace-max
    "`:tagspace-max` pops the top `:tagspace` item and pushes a list containing its highest-valued key and the tagspace itself onto the `:exec` stack. If there is no key, only the tagspace is pushed."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(first (last (seq (:contents %1)))) :as :key)
    (d/calculate [:arg :key] #(if (nil? %2) %1 (list %2 %1)) :as :maxkeylist)
    (d/push-onto :exec :maxkeylist)))



(def tagspace-merge
  (core/build-instruction
    tagspace-merge
    "`:tagspace-merge` pops the top two `:tagspace` items (call them A and B respectively) and pushes a new `:tagspace` item with the contents of A merged into B."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg2)
    (d/consume-top-of :tagspace :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(make-tagspace (into (sorted-map) (merge (:contents %1) (:contents %2)))) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-min
  (core/build-instruction
    tagspace-min
    "`:tagspace-min` pops the top `:tagspace` item and pushes a list containing its lowest-valued key and the tagspace itself onto the `:exec` stack. If there is no key, only the tagspace is pushed."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(first (first (seq (:contents %1)))) :as :key)
    (d/calculate [:arg :key] #(if (nil? %2) %1 (list %2 %1)) :as :minkeylist)
    (d/push-onto :exec :minkeylist)))



(def tagspace-new
  (core/build-instruction
    tagspace-new
    "`:tagspace-new` creates a new, empty `:tagspace` item and pushes it to the stack."
    :tags #{:tagspace}
    (d/calculate [] #(make-tagspace) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-offsetfloat
  (core/build-instruction
    tagspace-offsetfloat
    "`:tagspace-offsetfloat` pops the top `:tagspace` item and the top `:float`, and pushes a new `:tagspace` in which the numeric keys have all had the `:float` added to them."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :float :as :offset)
    (d/calculate [:arg1 :offset]
      #(make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (+' k %2) v))
          (sorted-map)
          (:contents %1))) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-offsetint
  (core/build-instruction
    tagspace-offsetint
    "`:tagspace-offsetint` pops the top `:tagspace` item and the top `:integer`, and pushes a new `:tagspace` in which the numeric keys have all had the `:integer` added to them."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :integer :as :offset)
    (d/calculate [:arg1 :offset]
      #(make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (+' k %2) v))
          (sorted-map)
          (:contents %1))) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-scalefloat
  (core/build-instruction
    tagspace-scalefloat
    "`:tagspace-scalefloat` pops the top `:tagspace` item and the top `:float`, and pushes a new `:tagspace` in which the numeric keys have all been multipled by the `:float` (even if it is negative or zero)."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :float :as :scale)
    (d/calculate [:arg1 :scale]
      #(make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (*' k %2) v))
          (sorted-map)
          (:contents %1))) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-scaleint
  (core/build-instruction
    tagspace-scaleint
    "`:tagspace-scaleint` pops the top `:tagspace` item and the top `:integer`, and pushes a new `:tagspace` in which the numeric keys have all been multipled by the `:integer` (even if it is negative or zero)."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :integer :as :scale)
    (d/calculate [:arg1 :scale]
      #(make-tagspace
        (reduce-kv
          (fn [r k v] (assoc r (*' k %2) v))
          (sorted-map)
          (:contents %1))) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-splitwithfloat
  (core/build-instruction
    tagspace-splitwithfloat
    "`:tagspace-splitwithfloat` pops the top `:tagspace` item and the top `:float`, and pushes two new `:tagspace` items in a list to `:exec`, which contain all the items with keys _below_ the `:float`, and all the keys above (inclusive)."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :float :as :cutoff)
    (d/calculate [:arg1 :cutoff]
      #(map 
          make-tagspace
          (vals 
            (reduce-kv
              (fn [r k v] (if (< k %2) 
                            (assoc-in r [:low k] v)
                            (assoc-in r [:high k] v)))
              {:low (sorted-map) :high (sorted-map)}
              (:contents %1)))) :as :result)
    (d/push-onto :exec :result)))



(def tagspace-splitwithint
  (core/build-instruction
    tagspace-splitwithint
    "`:tagspace-splitwithint` pops the top `:tagspace` item and the top `:integer`, and pushes two new `:tagspace` items in a list to `:exec`, which contain all the items with keys _below_ the `:integer`, and all the keys above (inclusive)."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg1)
    (d/consume-top-of :integer :as :cutoff)
    (d/calculate [:arg1 :cutoff]
      #(map 
          make-tagspace
          (vals 
            (reduce-kv
              (fn [r k v] (if (< k %2) 
                            (assoc-in r [:low k] v)
                            (assoc-in r [:high k] v)))
              {:low (sorted-map) :high (sorted-map)}
              (:contents %1)))) :as :result)
    (d/push-onto :exec :result)))



(def tagspace-tidywithfloats
  (core/build-instruction
    tagspace-tidywithfloats
    "`:tagspace-tidywithfloats` pops the top `:tagspace` item and the top two `:float` items (call them END and START respectively), and pushes a new `:tagspace` in which the first item is at index START, the last is at END, all the rest are evenly distributed between. The indices are all coerced to be `:float` values. If the two are identical, then only the last item of the collection will be retained as it will overwrite the others."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :ts)
    (d/consume-top-of :float :as :end)
    (d/consume-top-of :float :as :start)
    (d/calculate [:ts] #(vals (:contents %1)) :as :items)
    (d/calculate [:items] #(count %1) :as :how-many)
    (d/calculate [:start :end :how-many]
      #(if (< %3 2) 0 (/ (-' %2 %1) (dec %3))) :as :delta)
    (d/calculate [:how-many :start :delta]
        #(map double (take %1 (iterate (partial + %3) %2))) :as :indices)
    (d/calculate [:indices :items] #(make-tagspace (zipmap %1 %2)) :as :result)
    (d/push-onto :tagspace :result)))




(def tagspace-tidywithints
  (core/build-instruction
    tagspace-tidywithints
    "`:tagspace-tidywithints` pops the top `:tagspace` item and the top two `:integer` items (call them END and START respectively), and pushes a new `:tagspace` in which the first item is at index START, the last is at END, all the rest are evenly distributed between. The indices are all coerced to be `:integer` values. If the two are identical, then only the last item of the collection will be retained as it will overwrite the others."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :ts)
    (d/consume-top-of :integer :as :end)
    (d/consume-top-of :integer :as :start)
    (d/calculate [:ts] #(vals (:contents %1)) :as :items)
    (d/calculate [:items] #(count %1) :as :how-many)
    (d/calculate [:start :end :how-many]
      #(if (< %3 2) 0 (/ (-' %2 %1) (dec %3))) :as :delta)
    (d/calculate [:how-many :start :delta]
        #(map long (take %1 (iterate (partial + %3) %2))) :as :indices)
    (d/calculate [:indices :items] #(make-tagspace (zipmap %1 %2)) :as :result)
    (d/push-onto :tagspace :result)))



(def tagspace-values
  (core/build-instruction
    tagspace-values
    "`:tagspace-values` pops the top `:tagspace` item and pushes a list containing all of its stored values (as a list) and the tagspace itself onto the `:exec` stack."
    :tags #{:tagspace :collection}
    (d/consume-top-of :tagspace :as :arg)
    (d/calculate [:arg] #(list (or (vals (:contents %1)) (list)) %1) :as :valList)
    (d/push-onto :exec :valList)))



(def tagspace-type
  "builds the `:tagspace` collection type, which can hold arbitrary and mixed contents and uses numeric indices"
  (let [typename :tagspace]
  (-> (t/make-type  :tagspace
                    :recognized-by tagspace?
                    :attributes #{:collection :tagspace})
      (t/attach-instruction , tagspace-count)
      (t/attach-instruction , tagspace-keys)
      (t/attach-instruction , tagspace-lookupint)
      (t/attach-instruction , tagspace-lookupintegers)
      (t/attach-instruction , tagspace-lookupfloat)
      (t/attach-instruction , tagspace-lookupfloats)
      (t/attach-instruction , tagspace-lookupvector)
      (t/attach-instruction , tagspace-max)
      (t/attach-instruction , tagspace-merge)
      (t/attach-instruction , tagspace-min)
      (t/attach-instruction , tagspace-new)
      (t/attach-instruction , tagspace-offsetfloat)
      (t/attach-instruction , tagspace-offsetint)
      (t/attach-instruction , tagspace-scalefloat)
      (t/attach-instruction , tagspace-scaleint)
      (t/attach-instruction , tagspace-splitwithfloat)
      (t/attach-instruction , tagspace-splitwithint)
      (t/attach-instruction , tagspace-tidywithfloats)
      (t/attach-instruction , tagspace-tidywithints)
      (t/attach-instruction , tagspace-values)
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
