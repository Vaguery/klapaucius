(ns push.interpreter)


(defrecord Interpreter [program stacks])

(def core-stacks {:boolean '()
                  :char '()
                  :code '()
                  :input '()
                  :integer '() 
                  :exec '()
                  :float '()
                  :string '()
                  })

(defn make-interpreter
  "creates a new Interpreter record"
  ([] (make-interpreter []))
  ([program] (->Interpreter program core-stacks))
  ([program stacks] (->Interpreter program (merge core-stacks stacks)))
  )