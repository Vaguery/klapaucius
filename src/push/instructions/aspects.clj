(ns push.instructions.aspects
  (:require [push.types.core :as t])
  (:use [push.instructions.aspects.comparable])
  (:use [push.instructions.aspects.equatable])
  (:use push.instructions.aspects.movable)
  (:use push.instructions.aspects.printable)
  (:use push.instructions.aspects.quotable)
  (:use push.instructions.aspects.returnable)
  (:use push.instructions.aspects.storable)
  (:use push.instructions.aspects.visible)
  )



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
  "takes a PushType and adds the :movable attribute, and the
  :pushtype-dup, :pushtype-flush, :pushtype-pop, :pushtype-rotate,
  :pushtype-shove, :pushtype-swap, :pushtype-yank and
  :pushtype-yankdup instructions to its :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (dup-instruction pushtype))
      (t/attach-instruction (flush-instruction pushtype))
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
      (t/attach-instruction (store-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :storable))))


(defn make-visible
  "takes a PushType and adds the :visible attribute, and the
  :pushtype-stackdepth and :pushtype-empty? instructions to its
  :instructions collection"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (stackdepth-instruction pushtype))
      (t/attach-instruction (empty?-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :visible))))

