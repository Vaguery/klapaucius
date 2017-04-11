(ns push.interpreter.definitions)


(defrecord Interpreter [program
                        types
                        routers
                        stacks
                        bindings
                        instructions
                        config
                        counter
                        done?
                        scratch])

(defn make-interpreter
  "complicated wrapper around ->Interpreter, with numerous optional keyword arguments"
  [& { :keys [program
              types
              routers
              stacks
              bindings
              instructions
              config
              counter
              done?
              scratch]
      :or {   program      []
              types        (list)
              routers      []
              stacks       {}
              bindings     {}
              instructions {}
              config       {}
              counter      0
              done?        false
              scratch      {}
              }}]
  (->Interpreter
    program
    types
    routers
    stacks
    bindings
    instructions
    (merge {:lenient? true :max-collection-size 131072} config)
    counter
    done?
    scratch))
