(ns push.type.module.behavior
  (:require [push.instructions.dsl :as d]
            [push.instructions.core :as i]
            [push.type.core :as t]
            [push.instructions.aspects :as aspects]
            ))


;; interpreter state toggles


(def quote-refs
  (i/build-instruction
    push-quoterefs
    "`:push-quoterefs` toggles the interpreter state so that all known binding keywords are pushed automatically to the :ref stack without being resolved"
    :tags #{:binding}
    (d/quote-all-bindings)))



(def unquote-refs
  (i/build-instruction
    push-unquoterefs
    "`:push-unquoterefs` toggles the interpreter state so that all known binding keywords are resolved immediately, not pushed to the :ref stack"
    :tags #{:binding}
    (d/quote-no-bindings)))


(def store-args
  (i/build-instruction
    push-storeARGS
    "`:push-storeARGS` sets the `:store-args?` value of the interpreter's `:config`. While it is `true`, the interpreter will save arguments consumed by instructions into the `binding` named `:ARGS`."
    :tags #{:binding}
    (d/start-storing-arguments)))



(def discard-args
  (i/build-instruction
    push-nostoreARGS
    "`:push-nostoreARGS` unsets the `:store-args?` value of the interpreter's `:config`. While it is `false`, the interpreter will not save the arguments consumed by instructions into the `:ARGS` binding."
    :tags #{:binding}
    (d/stop-storing-arguments)))


(def cycle-args
  (i/build-instruction
    push-cycleARGS
    "`:push-cycleARGS` sets the `:cycle-args?` value of the interpreter's `:config`. While it is `true`, the interpreter will save arguments consumed by instructions to the end of its own `:exec` stack."
    :tags #{:binding}
    (d/start-cycling-arguments)))



(def nocycle-args
  (i/build-instruction
    push-nocycleARGS
    "`:push-nocycleARGS` unsets the `:cycle-args?` value of the interpreter's `:config`. While it is `false`, the interpreter will NOT save arguments consumed by instructions to the end of its own `:exec` stack."
    :tags #{:binding}
    (d/stop-cycling-arguments)))



(def behavior-module
  ( ->  (t/make-module  :behavior
                        :attributes #{})

        (t/attach-instruction cycle-args)
        (t/attach-instruction discard-args)
        (t/attach-instruction nocycle-args)
        (t/attach-instruction quote-refs)
        (t/attach-instruction store-args)
        (t/attach-instruction unquote-refs)
        ))
