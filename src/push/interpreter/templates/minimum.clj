(ns push.interpreter.templates.minimum
  (:require [push.interpreter.core :as i])
  )


(def minimal-stacks
  "the stacks expected in any Push interpreter"
    {:boolean '()
    :char '()
    :code '()
    :environment '()
    :error '()
    :scalar '() 
    :exec '()
    :log '()
    :string '()
    :print '()
    :return '()
    :unknown '()
    })


(def interpreter-default-config
  { :lenient? true
    :max-collection-size 131072
    :step-limit 0      })



(defn basic-interpreter
  "A convenience function that creates a new Interpreter record with no types or instructions.

  With no arguments, it has an empty :program and the counter is 0.

  Optional arguments include

  - :program (defaults to an empty vector)
  - :stacks (a hashmap, with contents)
  - :bindings (either a vector of values or a hashmap of named bindings)
  - :config
  - :router (a vector of routing rules)
  - :instructions (a hash-map of instructions by keyword name)
  - :counter

  (other interpreter values should be set after initialization)"

  ; [program types router stacks bindings instructions config counter done?]

  [& {:keys 
        [program types router stacks bindings instructions config counter done?]
      :or {program []
           types '()
           router []
           stacks {}
           bindings {}
           instructions {}
           config {:lenient? true}
           counter 0
           done? false}}]
  (let [all-stacks (merge minimal-stacks stacks)]
    (-> (i/make-interpreter
          program 
          '()            ;; types are registered below
          router         ;; router
          (merge minimal-stacks stacks)
          {}             ;; inputs are registered below
          instructions   ;; instructions
          (merge interpreter-default-config config)
          counter
          done?)
        (i/register-types  , types)
        (i/bind-inputs , bindings)
  )))
