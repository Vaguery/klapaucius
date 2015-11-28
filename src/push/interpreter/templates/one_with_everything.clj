(ns push.interpreter.templates.one-with-everything
  (:require [push.util.stack-manipulation :as u])
  (:use push.types.base.boolean)
  (:use push.types.base.char)
  (:use push.types.modules.code)
  (:use push.types.modules.exec)
  (:use push.types.base.float)
  (:use push.types.base.integer)
  (:use push.types.base.string)
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
  )


(def all-kinds-of-types
  (map extend-combinators
    [classic-integer-type
    classic-boolean-type
    classic-char-type
    classic-float-type
    classic-string-type

    (build-vectorized-type classic-boolean-type)
    (build-vectorized-type classic-char-type)
    (build-vectorized-type classic-float-type)
    (build-vectorized-type classic-integer-type)
    (build-vectorized-type classic-string-type)

    standard-vector-type
    standard-set-type

    ]))


(def all-kinds-of-modules
  (map extend-combinators
    [classic-exec-module
     classic-log-module
     classic-error-module
     classic-code-module
     classic-environment-module
     classic-print-module
     
     standard-introspection-module]))


(defn make-everything-interpreter
  "A convenience function that creates a new Interpreter record set up 'like Clojush'.

  With no arguments, it has an empty :program, and these types are loaded (in this order):
  
  - classic-boolean-type
  - classic-char-type
  - classic-code-module
  - classic-environment-module
  - classic-exec-module
  - classic-integer-type
  - classic-float-type
  - classic-log-module
  - classic-print-module
  - classic-string-type
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
  - :inputs (either a vector of values or a hashmap of named bindings)
  - :config
  - :counter

  (other interpreter values should be set after initialization)"
  [& {:keys [program stacks inputs config counter done?]
      :or {program []
           stacks {}
           inputs {}
           instructions {}
           config {}
           counter 0
           done? false}}]
  (let [all-stacks (merge core-stacks stacks)]
    (-> (->Interpreter  program 
                        '()        ;; types
                        []         ;; router
                        all-stacks 
                        {}         ;; inputs
                        {}         ;; instructions
                        (merge basic-interpreter-default-config config)
                        counter
                        done?)
        (register-types all-kinds-of-types)
        (register-modules all-kinds-of-modules)
        (register-inputs inputs)
        )))
