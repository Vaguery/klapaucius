(ns push.interpreter)


(defrecord Interpreter [program stacks instructions])


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
  With no arguments, it has an empty :program, the :stacks include core types and are empty, and no :instructions are registered. Any of these can be specified by key."
  [& {:keys [program stacks instructions]
      :or {program []
           stacks core-stacks
           instructions {}}}]
  (->Interpreter program (merge core-stacks stacks) instructions))


(defrecord Instruction [token needs makes function])


(defn make-instruction
  "creates a new Instruction record instance"
  [token & {
    :keys [needs makes function] 
    :or { needs {}
          makes {}
          function identity}}]
  (->Instruction token needs makes function))


(defn register-instruction
  "Takes an Interpreter and an Instruction, and attempts to add the Instruction to the :instructions map of the Interpreter, keyed by its :token."
  [interpreter instruction]
  (let [token (:token instruction)
        registry (:instructions interpreter)]
    (if (contains? registry token)
      (throw (Exception. (str "Push Instruction Redefined:'" token "'")))
      (assoc-in interpreter [:instructions (:token instruction)] instruction))))


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


