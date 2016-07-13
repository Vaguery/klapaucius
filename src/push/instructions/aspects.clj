(ns push.instructions.aspects
  (:use [push.instructions.aspects.buildable])
  (:use [push.instructions.aspects.comparable])
  (:use [push.instructions.aspects.equatable])
  (:use push.instructions.aspects.movable)
  (:use push.instructions.aspects.printable)
  (:use push.instructions.aspects.quotable)
  (:use push.instructions.aspects.repeatable-and-cycling)
  (:use push.instructions.aspects.returnable)
  (:use [push.instructions.aspects.set-able])
  (:use push.instructions.aspects.storable)
  (:use push.instructions.aspects.taggable)
  (:use push.instructions.aspects.to-tagspace)
  (:use push.instructions.aspects.visible)
  (:require [push.type.core :as t]
            ))



(defn make-buildable
  "takes a PushType and adds the :buildable attribute, and the associated instructions, to that type"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (make-instruction pushtype))
      (t/attach-instruction (parts-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :buildable))))



(defn make-set-able
  "takes a PushType and adds the :set-able attribute, and the associated instructions to that type"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (as-set-instruction pushtype))
      (t/attach-instruction (conj-set-instruction pushtype))
      (t/attach-instruction (in-set?-instruction pushtype))
      (t/attach-instruction (toset-instruction pushtype))
      (t/attach-instruction (intoset-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :set-able))))




(defn make-comparable
  "takes a PushType and adds the :comparable attribute, and the
  :pushtype>?, :pushtype≥?, :pushtype<?, :pushtype≤?, :pushtype-min and
  :pushtype-max instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (lessthan?-instruction pushtype))
      (t/attach-instruction (lessthanorequal?-instruction pushtype))
      (t/attach-instruction (greaterthan?-instruction pushtype))
      (t/attach-instruction (greaterthanorequal?-instruction pushtype))
      (t/attach-instruction (min-instruction pushtype))
      (t/attach-instruction (max-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :comparable))))



(defn make-equatable
  "takes a PushType and adds the :equatable attribute, and the
  :pushtype-equal? and :pushtype-notequal? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (equal?-instruction pushtype))
      (t/attach-instruction (notequal?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :equatable))))



(defn make-movable
  "takes a PushType and adds the :movable attribute, and the :pushtype-againlater, :pushtype-dup, :pushtype-flush, :pushtype-later, :pushtype-pop, :pushtype-rotate, :pushtype-shove, :pushtype-swap, :pushtype-yank and :pushtype-yankdup instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (againlater-instruction pushtype))
      (t/attach-instruction (cutflip-instruction pushtype))
      (t/attach-instruction (cutstack-instruction pushtype))
      (t/attach-instruction (dup-instruction pushtype))
      (t/attach-instruction (flipstack-instruction pushtype))
      (t/attach-instruction (flush-instruction pushtype))
      (t/attach-instruction (later-instruction pushtype))
      (t/attach-instruction (liftstack-instruction pushtype))
      (t/attach-instruction (pop-instruction pushtype))
      (t/attach-instruction (rotate-instruction pushtype))
      (t/attach-instruction (shove-instruction pushtype))
      (t/attach-instruction (swap-instruction pushtype))
      (t/attach-instruction (yank-instruction pushtype))
      (t/attach-instruction (yankdup-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :movable))))



(defn make-printable
  "takes a PushType and adds the :printable attribute and the `:print-X` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (print-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :printable))))



(defn make-quotable
  "takes a PushType and adds the :quotable attribute, and the :pushtype->code instruction :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (tocode-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :quotable))))


(defn make-repeatable
  "takes a PushType and adds the :repeatable attribute, and the
  :pushtype-echo instruction to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (echo-instruction pushtype))
      (t/attach-instruction (echoall-instruction pushtype))
      (t/attach-instruction (rerunall-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :repeatable))))



(defn make-cycling
  "takes a PushType and adds the :cycling attribute, and the
  :pushtype-cycler, :pushtype-indexedcycler, :pushtype-items instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (comprehension-instruction pushtype))
      (t/attach-instruction (cycler-instruction pushtype))
      (t/attach-instruction (sampler-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :cycling))))



(defn make-returnable
  "takes a PushType and adds the :returnable attribute and the `:X-return` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (return-instruction pushtype))
      (t/attach-instruction (return-pop-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :returnable))))



(defn make-storable
  "takes a PushType and adds the :storable attribute and the associated instructions"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (save-instruction pushtype))
      (t/attach-instruction (savestack-instruction pushtype))
      (t/attach-instruction (store-instruction pushtype))
      (t/attach-instruction (storestack-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :storable))))



(defn make-taggable
  "takes a PushType and adds the :taggable attribute and the associated instructions"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (tag-instruction pushtype))
      (t/attach-instruction (tagstack-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :taggable))))



(defn make-into-tagspaces
  "takes a PushType and adds the :to-tagspace attribute, and the
  :pushtype->tagspacefloat and :pushtype->tagspace instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (to-tagspace pushtype))
      (assoc :attributes (conj (:attributes pushtype) :to-tagspace))))



(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (stackdepth-instruction pushtype))
      (t/attach-instruction (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))

