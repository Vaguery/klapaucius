(ns push.interpreter.definitions)


(defrecord Interpreter [program
                        types
                        routers
                        stacks
                        bindings
                        instructions 
                        config 
                        counter 
                        done?])


(defn make-interpreter
  "simple wrapper around ->Interpreter"
  [program types routers stacks bindings instructions config counter done?]
  (->Interpreter
    program
    types
    routers
    stacks
    bindings
    instructions
    (merge {:lenient? true :max-collection-size 131072} config)
    counter
    done?))


