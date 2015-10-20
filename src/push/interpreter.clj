(ns push.interpreter)


(defrecord Interpreter [program stacks])


(def core-stacks
  "the basic types central to the Push language"
    {:boolean '()
    :char '()
    :code '()
    :input '()
    :integer '() 
    :exec '()
    :float '()
    :string '()
    })


(defn make-interpreter
  "creates a new Interpreter record
  If no arguments are given, the Interpreter has an empty :program and ony core :stacks;
  if a program is given, it is stored;
  if a hashmap of stack values is given, that is merged onto the core empty stacks."
  ([] (make-interpreter []))
  ([program] (->Interpreter program core-stacks))
  ([program stacks] (->Interpreter program (merge core-stacks stacks)))
  )