(ns push.interpreter.templates.one-with-everything
  (:require [push.util.stack-manipulation :as u]
            [push.interpreter.templates.minimum :as m]
            [push.util.exceptions :as oops])
  (:use push.types.type.boolean)
  (:use push.types.type.char)
  (:use push.types.module.code)
  (:use push.types.module.exec)
  (:use push.types.type.float)
  (:use push.types.type.integer)
  (:use push.types.type.rational)
  (:use push.types.type.scalar)
  (:use push.types.type.string)
  (:use push.types.type.ref)
  (:use push.types.module.environment)
  (:use push.types.module.print)
  (:use push.types.module.log)
  (:use push.types.module.error)
  (:use push.types.type.generator)
  (:use push.types.type.quoted)
  (:use push.types.type.tagspace)
  (:use push.types.type.vectorized)
  (:use push.types.type.vector)
  (:use push.types.type.set)
  (:use [push.interpreter.core])
  (:use [push.util.type-checkers])

  (:use push.types.module.introspection)
  (:use push.types.module.random-scalars)
  )


(def all-kinds-of-types
  [ integer-type
    boolean-type
    char-type
    float-type
    generator-type
    quoted-type
    ref-type
    string-type
    tagspace-type
    rational-type
    scalar-type

    (build-vectorized-type boolean-type)
    (build-vectorized-type char-type)
    (build-vectorized-type float-type)
    (build-vectorized-type integer-type)
    (build-vectorized-type ref-type)
    (build-vectorized-type string-type)
    (build-vectorized-type rational-type)
    (build-vectorized-type scalar-type)

    standard-vector-type
    standard-set-type
  ])


(def all-kinds-of-modules
  [ exec-module
    log-module
    error-module
    code-module
    environment-module
    print-module
    
    standard-introspection-module
    random-scalars-module])


(defn make-everything-interpreter
  "A convenience function that creates a new Interpreter record set up 'like Clojush'.

  With no arguments, it has an empty :program, and these types are loaded (in this order):
  
  - boolean-type
  - char-type
  - code-module
  - environment-module
  - exec-module
  - integer-type
  - float-type
  - generator-type
  - log-module
  - print-module
  - quoted-type
  - ref-type
  - string-type
  - rational-type
  - scalar-type
  - booleans-type
  - chars-type
  - integers-type
  - floats-type
  - strings-type
  - rationals-type
  - scalars-type
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
    (-> (->Interpreter  program 
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
