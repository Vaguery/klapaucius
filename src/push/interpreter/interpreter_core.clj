(ns push.interpreter.interpreter-core)


(defrecord Interpreter [program stacks instructions counter])


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
  With no arguments, it has an empty :program, the :stacks include
  core types and are empty, no :instructions are registered, and the
  counter is 0.

  Any of these can be specified by key."
  [& {:keys [program stacks instructions counter]
      :or {program []
           stacks core-stacks
           instructions {}
           counter 0}}]
  (->Interpreter program (merge core-stacks stacks) 
                   instructions counter))


(defn- throw-redefined-instruction-error
  [token]
  (throw (Exception. (str 
                        "Push Instruction Redefined:'"
                        token
                        "'"))))


(defn- throw-unknown-instruction-error
  [token]
  (throw (Exception. (str 
                        "Unknown Push instruction:'" token "'"))))


(defn- throw-unknown-push-item-error
  [item]
  (throw (Exception. (str 
                        "Push Parsing Error: Cannot interpret '"
                        item
                        "' as a Push item."))))


(defn- add-instruction
  "Takes an Interpreter and an Instruction (record), and adds the
  instruction to the :instructions registry of the interpreter,
  without checking for prior definitions."
  [interpreter instruction]
  (assoc-in
    interpreter
    [:instructions (:token instruction)]
    instruction))


(defn register-instruction
  "Takes an Interpreter and an Instruction, and attempts to add the
  Instruction to the :instructions map of the Interpreter, keyed by
  its :token."
  [interpreter instruction]
  (let [token (:token instruction)
        registry (:instructions interpreter)]
    (if (contains? registry token)
      (throw-redefined-instruction-error token))
      (add-instruction interpreter instruction)))


(defn- contains-at-least?
  "Takes an interpreter, a stack name, and a count; returns true if
  the named stack exists, and has at least that many items on it"
  [interpreter stack limit]
  (let [that-stack (get-in interpreter [:stacks stack])]
    (and 
      (<= limit (count that-stack))
      (some? that-stack))))


(defn- recognizes-instruction?
  "Takes an Interpreter and an instruction token, and returns true if
  the token is registered."
  [interpreter token]
  (contains? (:instructions interpreter) token))


(defn ready-for-instruction?
  "Takes an Interpreter (with registered instructions) and a keyword
  instruction token, and returns true if the number of items on the
  stacks meets or exceeds all the specified :needs for that
  instruction. Returns false if the instruction is not registered."
  [interpreter token]
  (let [needs (get-in interpreter [:instructions token :needs])]
    (and
      (recognizes-instruction? interpreter token)
      (reduce-kv
        (fn [truth k v]
          (and truth (contains-at-least? interpreter k v)))
        true
        needs))))


(defn- get-instruction
  [interpreter token]
  (get-in interpreter [:instructions token]))


(defn- apply-instruction
  [interpreter token]
  ((:transaction (get-instruction interpreter token)) interpreter))


(defn execute-instruction
  "Takes an Interpreter and a token, and executes the registered
  Instruction using the Interpreter as the (only) argument. Raises an
  exception if the token is not registered."
  [interpreter token]
  (let [unrecognized (not (recognizes-instruction? interpreter token))
        ready (ready-for-instruction? interpreter token)]
  (cond
    unrecognized (throw-unknown-instruction-error token)
    ready (apply-instruction interpreter token)
    :else interpreter)))


(defn get-stack
  "A convenience function which returns the named stack from the
  interpreter"
  [interpreter stack]
  (get-in interpreter [:stacks stack]))


(defn set-stack
  "A convenience function which replaces the named stack with the
  indicated list"
  [interpreter stack new-value]
  (assoc-in interpreter [:stacks stack] new-value))


(defn push-item
  "Takes an Interpreter, a stack name and a Clojure expression, and
  returns the Interpreter with the item pushed onto the specified
  stack. If the stack doesn't already exist, it is created."
  [interpreter stack item]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (conj old-stack item))))


(defn load-items
  "Takes an Interpreter, a stack name, and a collection of items. Puts
  all the items onto the named stack, one at time (probably reversing
  them along the way."
  [interpreter stackname item-list]
  (let [old-stack (get-in interpreter [:stacks stackname])
        new-stack (into old-stack (reverse item-list))]
    (set-stack interpreter stackname new-stack)))


(defn clear-stack
  "Empties the named stack."
  [interpreter stack]
  (assoc-in interpreter [:stacks stack] (list)))


(defn boolean?
  "a checker that returns true if the argument is the literal `true`
  or the literal `false`"
  [item]
  (or (false? item) (true? item)))


(defn instruction?
  "takes an Interpreter and a keyword, and returns true if the keyword
  is a key of the :instructions registry in that Interpreter instance"
  [interpreter token]
  (contains? (:instructions interpreter) token))


(defn handle-item
  "Takes an Interpreter and an item, and either recognizes and invokes
  a keyword registered in that Interpreter as an instruction, or sends
  the item to the correct stack (if it exists). Throws an exception if
  the Clojure expression is not recognized explicitly as a registered
  instruction or some other kind of Push literal."
  [interpreter item]
  (cond
    (instruction? interpreter item)
      (execute-instruction interpreter item)
    (integer? item) (push-item interpreter :integer item)
    (boolean? item) (push-item interpreter :boolean item)
    (char? item) (push-item interpreter :char item)
    (float? item) (push-item interpreter :float item)
    (string? item) (push-item interpreter :string item)
    (list? item) (load-items interpreter :exec item)
    :else (throw-unknown-push-item-error item)))


(defn clear-all-stacks
  "removes all items from all stacks in an Interpreter"
  [interpreter]
  (let [stacklist (keys (:stacks interpreter))]
    (assoc interpreter :stacks
      (reduce #(assoc %1 %2 '()) {} stacklist))))


(defn reset-interpreter
  "takes an Interpreter instance and:
  - sets the counter to 0
  - clears all non-:exec stacks
  - puts the program onto the :exec stack"
  [interpreter]
    (-> interpreter
        (clear-all-stacks)
        (assoc , :counter 0)
        (load-items :exec (:program interpreter))))


(defn increment-counter
  "takes an Interpreter and increments its :counter (without otherwise
  changing it)"
  [interpreter]
  (assoc interpreter :counter (inc (:counter interpreter))))


(defn step
  "Takes an Interpreter, pops one item off :exec, routes it to the
  router, increments the counter. If the :exec stack is empty, does
  nothing."
  [interpreter]
  (let [old-exec (get-stack interpreter :exec)]
    (if (seq old-exec)
      (let [next-item (first old-exec)
            new-exec (pop old-exec)]
        (-> interpreter
            (increment-counter)
            (set-stack :exec new-exec)
            (handle-item next-item)))
      interpreter)))

