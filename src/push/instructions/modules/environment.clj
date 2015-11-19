(ns push.instructions.modules.environment
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


(defn return-instruction
  "returns a new x-return instruction for a PushType or stackname"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-return")]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename
        "` item and pushes it to the `:return` stack.")
      :tags #{:io}
      `(push.instructions.dsl/consume-top-of ~typename :as :arg1)
      `(push.instructions.dsl/push-onto :print :arg1)))))


(def environment-new
  (core/build-instruction
    environment-new
    "`environment-new` pops the top `:exec` item, archives all the remaining current stacks to a new :environment item, and then continues with only the popped item in place of the archived `:exec` stack."
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :run-this)
    (d/archive-all-stacks)
    (d/delete-stack :exec)
    (d/push-onto :exec :run-this)))


; environment_begin
; environment_end
; return_fromexec
; return_frominteger
; return_fromfloat
; return_fromboolean
; return_fromstring
; return_fromchar
; return_fromcode
; return_exec_pop
; return_code_pop
; return_integer_pop
; return_float_pop
; return_boolean_pop
; return_zip_pop
; return_string_pop
; return_char_pop
; return_tagspace


(defn make-returnable
  "takes a PushType and adds the :returnable attribute and the `:X-return` instruction"
  [pushtype]
  (-> pushtype
      (t/attach-instruction (return-instruction pushtype))
      (assoc :attributes (conj (:attributes pushtype) :returnable))))



(def classic-environment-module
  ( ->  (t/make-module  :environment
                        :attributes #{:complex :base})
        (t/attach-instruction , environment-new)
))

