(ns push.instructions.modules.print
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.visible)
  )


(defn print-instruction
  "returns a new x-print instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-print")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes it to the `:print` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto :print :arg1)))))


(def print-newline
  (core/build-instruction
    print-newline
    "`:print-newline` pushes a single newline character to the `:print` stack"
    :tags #{:print :io :base}
    (d/calculate [] (fn [] \newline) :as :newline)
    (d/push-onto :print :newline)))


(def print-space
  (core/build-instruction
    print-space
    "`:print-space` pushes a single space character to the `:print` stack"
    :tags #{:print :io :base}
    (d/calculate [] (fn [] \space) :as :space)
    (d/push-onto :print :space)))


(defn make-printable
  "takes a PushType and adds the :printable attribute and the `:print-X` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (print-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :printable))))



(def classic-print-module
  ( ->  (t/make-module  :print
                        :attributes #{:io :base})
        make-visible
        (t/attach-instruction print-newline)
        (t/attach-instruction print-space)
        ))

