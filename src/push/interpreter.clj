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


(defn push-item
  "Takes an Interpreter, a keyword (naming a stack) and a Clojure expression, and returns the Interpreter with the item pushed onto the specified stack. If the stack doesn't already exist, it is created."
  [interpreter stack item]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (conj old-stack item))
  ))


(defn load-items
  "Takes an Interpreter, a stack name, and a seq, and conj'es each item onto the named stack of the interpreter (using Clojure's `into` transducer). Used primarily for loading code onto the :exec stack."
  [interpreter stack item-list]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (into old-stack (reverse item-list))))
  )


(defn get-stack
  "A convenience function which returns the named stack from the interpreter"
  [stack interpreter]
  (get-in interpreter [:stacks stack]))


(defn boolean?
  "a checker that returns true if the argument is the literal `true` or the literal `false`"
  [item]
  (or (false? item) (true? item)))


(defn handle-item
  "Takes an Interpreter and an item, and either recognizes and invokes a keyword registered in that Interpreter as an instruction, or sends the item to the correct stack (if it exists). Throws an exception if the Clojure expression is not recognized explicitly as a registered instruction or some other kind of Push literal."
  [interpreter item]
  (cond
    (integer? item) (push-item interpreter :integer item)
    (boolean? item) (push-item interpreter :boolean item)
    (char? item) (push-item interpreter :char item)
    (float? item) (push-item interpreter :float item)
    (string? item) (push-item interpreter :string item)
    (list? item) (load-items interpreter :exec item)
    :else (throw
      (Exception. (str "Push Parsing Error: Cannot interpret '" item "' as a Push item.")))
  ))


(defn process-expression
  "takes an Interpreter and any Clojure item, and 'executes' the item within the Interpreter, as if it had been taken from the :exec stack: a literal is processed and sent to the router, an instruction is looked up in the registry, and so forth"
  [interpreter expression]
  (handle-item interpreter expression))