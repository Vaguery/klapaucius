(ns push.interpreter)


(defrecord Interpreter [stacks])


(defn make-interpreter
  "creates a new Interpreter record"
  []
  (->Interpreter {
    :boolean '()
    :char '()
    :code '()
    :input '()
    :integer '() 
    :exec '()
    :float '()
    :string '()
  }))