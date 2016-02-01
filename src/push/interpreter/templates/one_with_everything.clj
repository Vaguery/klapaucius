(ns push.interpreter.templates.one-with-everything
  (:require [push.util.stack-manipulation :as u])
  (:require [push.interpreter.templates.minimum :as m])
  (:use push.types.base.boolean)
  (:use push.types.base.char)
  (:use push.types.modules.code)
  (:use push.types.modules.exec)
  (:use push.types.base.float)
  (:use push.types.base.integer)
  (:use push.types.base.string)
  (:use push.types.base.ref)
  (:use push.types.modules.environment)
  (:use push.types.modules.print)
  (:use push.types.modules.log)
  (:use push.types.modules.error)
  (:use push.types.extra.vectorized)
  (:use push.types.extra.vector)
  (:use push.types.extra.set)
  (:use [push.interpreter.core])
  (:use [push.util.type-checkers])
  (:require [push.util.exceptions :as oops])

  (:use push.instructions.extra.stack-combinators)
  (:use push.instructions.extra.introspection)
  (:use push.instructions.extra.numeric-scaling)
  (:use push.instructions.extra.random-scalars)

  (:use demo.examples.plane-geometry.definitions)
  )


(def all-kinds-of-types
  (map extend-combinators
    [integer-type
    boolean-type
    char-type
    float-type
    ref-type
    string-type

    (build-vectorized-type boolean-type)
    (build-vectorized-type char-type)
    (build-vectorized-type float-type)
    (build-vectorized-type integer-type)
    (build-vectorized-type ref-type)
    (build-vectorized-type string-type)

    standard-vector-type
    standard-set-type

    ; push-circle  ;; demo.examples.plane-geometry.definitions
    ; push-line
    ; push-point

    ]))


(def all-kinds-of-modules
  (map extend-combinators
    [exec-module
     log-module
     error-module
     code-module
     environment-module
     print-module
     
     standard-introspection-module
     numeric-scaling-module
     random-scalars-module
     ]))


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
  - log-module
  - print-module
  - string-type
  - booleans-type
  - chars-type
  - integers-type
  - floats-type
  - strings-type
  - standard-set-type
  - standard-vector-type (loaded last as a default)
  - extra-stack-combinators (for all types that are :movable)
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
           config {}
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
