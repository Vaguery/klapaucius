(ns push.interpreter.templates.classic
  (:require [push.interpreter.templates.minimum :as min]
            [push.interpreter.core :as i]
            [push.types.type.boolean]
            [push.types.type.char]
            [push.types.module.code]
            [push.types.module.exec]
            [push.types.type.float]
            [push.types.type.integer]
            [push.types.type.string]
            [push.types.module.environment]
            [push.types.module.print]
            [push.types.module.log]
            [push.types.module.error])
  )


(defn classic-interpreter
  "A convenience funciton that creates a new Interpreter record set up 'like Clojush'.

  With no arguments, it has an empty :program, the :stacks include
  core types and are empty, these types are loaded (in this order):
  
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

  and the counter is 0.

  Optional arguments include

  - :program (defaults to an empty vector)
  - :stacks (a hashmap, with contents)
  - :bindings (either a vector of values or a hashmap of named bindings)
  - :config
  - :counter

  (other interpreter values should be set after initialization)"

  ; [program types router stacks bindings instructions config counter done?]

  [& {:keys [program types router stacks bindings instructions config counter done?]
      :or {program []
           types '()
           router []
           stacks {}
           bindings {}
           instructions {}
           config {:lenient? true}
           counter 0
           done? false}}]
  (let [all-stacks (merge min/minimal-stacks stacks)]
    (-> (i/make-interpreter
          program 
          '()            ;; types are registered below
          router         ;; router
          all-stacks 
          {}             ;; inputs are registered below
          instructions   ;; instructions
          (merge min/interpreter-default-config config)
          counter
          done?)
        (i/register-types , (concat
                              [push.types.type.integer/integer-type
                               push.types.type.boolean/boolean-type
                               push.types.type.char/char-type
                               push.types.type.float/float-type
                               push.types.type.string/string-type
                               ]
                               types))
        (i/register-modules , [push.types.module.exec/exec-module
                               push.types.module.log/log-module
                               push.types.module.error/error-module
                               push.types.module.code/code-module
                               push.types.module.environment/environment-module
                               push.types.module.print/print-module
                               ])
        (i/bind-inputs , bindings)
        )))

