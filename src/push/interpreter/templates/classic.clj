(ns push.interpreter.templates.classic
  (:require [push.interpreter.templates.minimum :as min])
  (:require [push.interpreter.core :as i])
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
  )


(defn classic-interpreter
  "A convenience funciton that creates a new Interpreter record set up 'like Clojush'.

  With no arguments, it has an empty :program, the :stacks include
  core types and are empty, these types are loaded (in this order):
  
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

  and the counter is 0.

  Optional arguments include

  - :program (defaults to an empty vector)
  - :stacks (a hashmap, with contents)
  - :inputs (either a vector of values or a hashmap of named bindings)
  - :config
  - :counter

  (other interpreter values should be set after initialization)"

  ; [program types router stacks inputs instructions config counter done?]

  [& {:keys [program types router stacks inputs instructions config counter done?]
      :or {program []
           types '()
           router []
           stacks {}
           inputs {}
           instructions {}
           config {}
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
                              [push.types.base.integer/classic-integer-type
                               push.types.base.boolean/classic-boolean-type
                               push.types.base.char/classic-char-type
                               push.types.base.float/classic-float-type
                               push.types.base.string/classic-string-type
                               ]
                               types))
        (i/register-modules , [push.types.modules.exec/classic-exec-module
                               push.types.modules.log/classic-log-module
                               push.types.modules.error/classic-error-module
                               push.types.modules.code/classic-code-module
                               push.types.modules.environment/classic-environment-module
                               push.types.modules.print/classic-print-module
                               ])
        (i/register-inputs , inputs)
        )))

