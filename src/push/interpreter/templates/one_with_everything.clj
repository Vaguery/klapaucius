(ns push.interpreter.templates.one-with-everything
  (:require [push.util.stack-manipulation :as u])
  (:require [push.types.base.boolean])
  (:require [push.types.base.char])
  (:require [push.types.modules.code])
  (:require [push.types.modules.exec])
  (:require [push.types.base.float])
  (:require [push.types.base.integer])
  (:require [push.types.base.string])
  (:require [push.types.modules.environment])
  (:require [push.types.modules.print])
  (:require [push.types.modules.log])
  (:require [push.types.modules.error])
  (:require [push.types.extra.vectorized])
  (:require [push.types.extra.vector])
  (:require [push.util.exceptions :as oops])
  (:use [push.interpreter.core])
  (:use [push.util.type-checkers])
  )


(def all-kinds-of-types
  [ push.types.base.integer/classic-integer-type
    push.types.base.boolean/classic-boolean-type
    push.types.base.char/classic-char-type
    push.types.base.float/classic-float-type
    push.types.base.string/classic-string-type

    (push.types.extra.vectorized/build-vectorized-type
      push.types.base.boolean/classic-boolean-type)
    (push.types.extra.vectorized/build-vectorized-type
      push.types.base.char/classic-char-type)
    (push.types.extra.vectorized/build-vectorized-type
      push.types.base.float/classic-float-type)
    (push.types.extra.vectorized/build-vectorized-type
      push.types.base.integer/classic-integer-type)
    (push.types.extra.vectorized/build-vectorized-type
      push.types.base.string/classic-string-type)

    push.types.extra.vector/standard-vector-type
    ])


(def all-kinds-of-modules
  [push.types.modules.exec/classic-exec-module
   push.types.modules.log/classic-log-module
   push.types.modules.error/classic-error-module
   push.types.modules.code/classic-code-module
   push.types.modules.environment/classic-environment-module
   push.types.modules.print/classic-print-module])


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
  - standard-vector-type (loaded last as a default)
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
