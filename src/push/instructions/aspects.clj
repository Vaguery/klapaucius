(ns push.instructions.aspects
  (:require [push.type.core
              :as type
              :refer [attach-instruction]])
  (:use [push.instructions.aspects.buildable]
        [push.instructions.aspects.comparable]
        [push.instructions.aspects.equatable]
        [push.instructions.aspects.movable]
        [push.instructions.aspects.printable]
        [push.instructions.aspects.quotable]
        [push.instructions.aspects.repeatable-and-cycling]
        [push.instructions.aspects.returnable]
        [push.instructions.aspects.set-able]
        [push.instructions.aspects.storable]
        [push.instructions.aspects.taggable]
        [push.instructions.aspects.to-tagspace]
        [push.instructions.aspects.visible]
        ))



(defn make-buildable
  "takes a PushType and adds the :buildable attribute, and the associated instructions, to that type"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (construct-instruction pushtype))
      (type/attach-instruction (parts-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :buildable))))



(defn make-set-able
  "takes a PushType and adds the :set-able attribute, and the associated instructions to that type"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (as-set-instruction pushtype))
      (type/attach-instruction (conj-set-instruction pushtype))
      (type/attach-instruction (in-set?-instruction pushtype))
      (type/attach-instruction (toset-instruction pushtype))
      (type/attach-instruction (intoset-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :set-able))))




(defn make-comparable
  "takes a PushType and adds the :comparable attribute, and the
  :pushtype>?, :pushtype≥?, :pushtype<?, :pushtype≤?, :pushtype-min and
  :pushtype-max instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (lessthan?-instruction pushtype))
      (type/attach-instruction (lessthanorequal?-instruction pushtype))
      (type/attach-instruction (greaterthan?-instruction pushtype))
      (type/attach-instruction (greaterthanorequal?-instruction pushtype))
      (type/attach-instruction (min-instruction pushtype))
      (type/attach-instruction (max-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :comparable))))



(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (equal?-instruction pushtype))
      (type/attach-instruction (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))



(defn make-movable
  "takes a PushType and adds the :movable attribute, and the :pushtype-againlater, :pushtype-dup, :pushtype-flush, :pushtype-later, :pushtype-pop, :pushtype-rotate, :pushtype-shove, :pushtype-swap, :pushtype-yank and :pushtype-yankdup instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (againlater-instruction pushtype))
      (type/attach-instruction (cutflip-instruction pushtype))
      (type/attach-instruction (cutstack-instruction pushtype))
      (type/attach-instruction (dup-instruction pushtype))
      (type/attach-instruction (flipstack-instruction pushtype))
      (type/attach-instruction (flush-instruction pushtype))
      (type/attach-instruction (later-instruction pushtype))
      (type/attach-instruction (liftstack-instruction pushtype))
      (type/attach-instruction (pop-instruction pushtype))
      (type/attach-instruction (rotate-instruction pushtype))
      (type/attach-instruction (shove-instruction pushtype))
      (type/attach-instruction (swap-instruction pushtype))
      (type/attach-instruction (yank-instruction pushtype))
      (type/attach-instruction (yankdup-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :movable))))



(defn make-printable
  "takes a PushType and adds the :printable attribute and the `:print-X` instruction"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (print-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :printable))))



(defn make-quotable
  "takes a PushType and adds the :quotable attribute, and the :pushtype->code instruction :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (tocode-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :quotable))))


(defn make-repeatable
  "takes a PushType and adds the :repeatable attribute, and the
  :pushtype-echo instruction to its :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (echo-instruction pushtype))
      (type/attach-instruction (echoall-instruction pushtype))
      (type/attach-instruction (rerunall-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :repeatable))))



(defn make-cycling
  "takes a PushType and adds the :cycling attribute, and the
  :pushtype-cycler, :pushtype-indexedcycler, :pushtype-items instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (comprehension-instruction pushtype))
      (type/attach-instruction (cycler-instruction pushtype))
      (type/attach-instruction (sampler-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :cycling))))



(defn make-returnable
  "takes a PushType and adds the :returnable attribute and the `:X-return` instruction"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (return-instruction pushtype))
      (type/attach-instruction (return-pop-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :returnable))))



(defn make-storable
  "takes a PushType and adds the :storable attribute and the associated instructions"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (save-instruction pushtype))
      (type/attach-instruction (savestack-instruction pushtype))
      (type/attach-instruction (store-instruction pushtype))
      (type/attach-instruction (storestack-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :storable))))



(defn make-taggable
  "takes a PushType and adds the :taggable attribute and the associated instructions"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (tag-instruction pushtype))
      (type/attach-instruction (tagstack-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :taggable))))



(defn make-into-tagspaces
  "takes a PushType and adds the :to-tagspace attribute, and the
  :pushtype->tagspacefloat and :pushtype->tagspace instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (to-tagspace pushtype))
      (assoc :attributes (conj (:attributes pushtype) :to-tagspace))))



(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (type/attach-instruction (stackdepth-instruction pushtype))
      (type/attach-instruction (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))
