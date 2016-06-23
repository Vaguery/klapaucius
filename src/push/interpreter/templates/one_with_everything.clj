(ns push.interpreter.templates.one-with-everything
  (:require [push.util.stack-manipulation :as u]
            [push.interpreter.templates.minimum :as m]
            [push.util.exceptions :as oops]
            [push.interpreter.definitions :as defs])
  (:use push.type.item.boolean)
  (:use push.type.item.char)
  (:use push.type.module.code)
  (:use push.type.item.complex)
  (:use push.type.module.exec)
  (:use push.type.item.scalar)
  (:use push.type.item.string)
  (:use push.type.item.ref)
  (:use push.type.item.snapshot)
  (:use push.type.module.print)
  (:use push.type.module.log)
  (:use push.type.module.error)
  (:use push.type.item.generator)
  (:use push.type.item.quoted)
  (:use push.type.item.tagspace)
  (:use push.type.item.vectorized)
  (:use push.type.item.vector)
  (:use push.type.item.set)
  (:use [push.interpreter.core])
  (:use [push.util.type-checkers])

  (:use push.type.module.introspection)
  (:use push.type.module.random-scalars)
  )


(def all-kinds-of-types
  [ 
    boolean-type
    char-type
    generator-type
    quoted-type
    ref-type
    string-type
    tagspace-type
    scalar-type
    complex-type
    snapshot-type


    (build-vectorized-type boolean-type)
    (build-vectorized-type char-type)
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
    
    standard-introspection-module
    random-scalars-module])


(defn make-everything-interpreter
  "A convenience function that creates a new Interpreter with (almost) every defined type and instruction.

  With no arguments, it has an empty :program, and these types are loaded (in this order):
  
  - boolean-type
  - char-type
  - code-module
  - snapshot-type
  - exec-module
  - generator-type
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
          program 
          '()        ;; types
          []         ;; router
          all-stacks 
          {}         ;; inputs
          {}         ;; instructions
          (merge m/interpreter-default-config config)
          counter
          done?)
        (register-types all-kinds-of-types)
        (register-modules all-kinds-of-modules)
        (bind-inputs bindings)
        )))
