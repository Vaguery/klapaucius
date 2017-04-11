(ns push.interpreter.templates.one-with-everything
  (:require [push.interpreter.templates.minimum :as m]
            [push.interpreter.definitions :as defs]
            [push.interpreter.core :as i]
            )
  (:use     [push.type.module.behavior]
            [push.type.item.boolean]
            [push.type.item.char]
            [push.type.module.code]
            [push.type.item.complex]
            [push.type.module.exec]
            [push.type.module.error]
            [push.type.item.generator]
            [push.type.item.interval]
            [push.type.module.introspection]
            [push.type.module.log]
            [push.type.module.print]
            [push.type.item.quoted]
            [push.type.module.random-scalars]
            [push.type.item.ref]
            [push.type.item.scalar]
            [push.type.item.snapshot]
            [push.type.item.string]
            [push.type.item.tagspace]
            [push.type.item.vectorized]
            [push.type.item.vector]
            [push.type.item.set]
            ))

(def all-kinds-of-types
  [ boolean-type
    char-type
    generator-type
    interval-type
    quoted-type
    ref-type
    string-type
    tagspace-type
    scalar-type
    complex-type
    snapshot-type
    (build-vectorized-type boolean-type)
    (build-vectorized-type char-type)
    (build-vectorized-type interval-type)
    (build-vectorized-type ref-type)
    (build-vectorized-type string-type)
    (build-vectorized-type scalar-type)
    (build-vectorized-type complex-type)

    standard-vector-type
    standard-set-type
  ])


(def all-kinds-of-modules
  [ exec-module
    log-module
    error-module
    code-module
    print-module
    behavior-module

    standard-introspection-module
    random-scalars-module
    ])


(defn make-everything-interpreter
  "A convenience function that creates a new Interpreter with (almost) every defined type and instruction.

  With no arguments, it has an empty :program, and these types are loaded (in this order):

  - boolean-type
  - char-type
  - code-module
  - snapshot-type
  - exec-module
  - generator-type
  - interval-type
  - log-module
  - print-module
  - quoted-type
  - ref-type
  - string-type
  - scalar-type
  - complex-type
  - booleans-type
  - chars-type
  - strings-type
  - scalars-type
  - complexes-type
  - standard-set-type
  - tagspace-type
  - standard-vector-type (loaded last as a default)
  and the counter is 0.

  Optional arguments include

  - :program (defaults to an empty vector)
  - :stacks (a hashmap, with contents)
  - :bindings (either a vector of values or a hashmap of named bindings)
  - :config
  - :counter

  (other interpreter values should be set after initialization)"
  [& {:keys [program stacks bindings config counter done?]
      :or {program []
           stacks {}
           bindings {}
           instructions {}
           config {:lenient? true}
           counter 0
           done? false}}]
  (let [all-stacks (merge m/minimal-stacks stacks)]
    (-> (defs/make-interpreter
          :program program
          :types (list)
          :routers []
          :stacks all-stacks
          :bindings {}
          :instructions {}
          :config (merge m/interpreter-default-config config)
          :counter counter
          :done? done?)
        (i/register-types all-kinds-of-types)
        (i/register-modules all-kinds-of-modules)
        (i/bind-inputs bindings)
        )))
