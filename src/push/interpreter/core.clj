(ns push.interpreter.core
  (:require [push.util.stack-manipulation :as u])
  (:require [push.util.exceptions :as oops])
  (:use [push.util.type-checkers])
  )


(defrecord Interpreter [program
                        types
                        router
                        stacks
                        inputs
                        instructions 
                        config 
                        counter 
                        done?])


(defn make-interpreter
  "simple wrapper around ->Interpreter"
  [program types router stacks inputs instructions config counter done?]
  (->Interpreter
    program types router stacks inputs instructions config counter done?))


(defn register-type
  "Takes an Interpreter record, and a PushType record, and adds the PushType to the :types collection in the Interpeter; adds the type's :name as a new stack (if not already present); appends the type's :recognizer to the :router vector; adds the type's internally defined instructions to the Interpreter's registry automatically."
  [interpreter type]
  (let [old-types (:types interpreter)
        old-stacks (:stacks interpreter)
        old-router (:router interpreter)
        old-instructions (:instructions interpreter)]
        (-> interpreter
            (assoc :types (conj old-types type))
            (assoc :router (conj old-router [(:recognizer type) (:name type)]))
            (assoc :stacks (merge {(:name type) '()}  old-stacks))
            (assoc :instructions (merge old-instructions (:instructions type))))))


(defn register-module
  "Takes an Interpreter record, and a module; adds the module's internally defined instructions to the Interpreter's registry automatically."
  [interpreter module]
  (let [old-types (:types interpreter)
        old-instructions (:instructions interpreter)]
    (-> interpreter
      (assoc :types (conj old-types module))
      (assoc :instructions (merge old-instructions (:instructions module))))))


(defn register-types
  "Takes an Interpreter record, and a list of PushType records. Calls `register-type` on each of the types in turn."
  [interpreter types]
  (reduce #(register-type %1 %2) interpreter types))


(defn register-modules
  "Takes an Interpreter record, and a list of modules. Calls `register-module` on each of those in turn."
  [interpreter modules]
  (reduce #(register-module %1 %2) interpreter modules))


(defn register-input
  "Takes an Interpreter record, a keyword and any item, and adds the item as a value stored under the keyword in the :inputs hashmap."
  ([interpreter value]
    (let [next-index (inc (count (:inputs interpreter)))
          next-input (keyword (str "input!" next-index))]
      (register-input interpreter next-input value)))
  ([interpreter kwd value]
    (assoc-in interpreter [:inputs kwd] value)))


(defn register-inputs
  "Takes an Interpreter record, and a hashmap of key-value items; merges them into the :inputs map if the Interpreter."
  [interpreter values]
  (cond (vector? values)
    (reduce (partial register-input) interpreter values)
  :else
    (assoc interpreter :inputs (merge (:inputs interpreter) values))))


(defn input?
  "Takes an interpreter, and a keyword, and returns true if the keyword is a key in the :inputs hashmap"
  [interpreter kwd]
  (contains? (:inputs interpreter) kwd))


(defn- add-instruction
  "Takes an Interpreter and an Instruction (record), and adds the instruction to the :instructions registry of the interpreter, without checking for prior definitions."
  [interpreter instruction]
  (assoc-in
    interpreter
    [:instructions (:token instruction)]
    instruction))


(defn register-instruction
  "Takes an Interpreter and an Instruction, and attempts to add the Instruction to the :instructions map of the Interpreter, keyed by its `:token`."
  [interpreter instruction]
  (let [token (:token instruction)
        registry (:instructions interpreter)]
    (if (contains? registry token)
      (oops/throw-redefined-instruction-error token))
      (add-instruction interpreter instruction)))


;;; reconfigure


(defn reconfigure
  "takes an interpreter and a map of configuration pairs, and merges the map with the current interpreter map"
  [interpreter new-config]
  (let [old-config (:config interpreter)]
    (assoc interpreter :config (merge old-config new-config))))


;;; manipulating Interpreter state


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


(defn- ready-for-instruction?
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


(defn push-item
  "Takes an Interpreter, a stack name and a Clojure expression, and
  returns the Interpreter with the item pushed onto the specified
  stack. If the stack doesn't already exist, it is created."
  [interpreter stack item]
  (let [old-stack (get-in interpreter [:stacks stack])]
    (assoc-in interpreter [:stacks stack] (conj old-stack item))))


(defn missing-args-message
  [interpreter token]
  (let [t (:counter interpreter)]
    {:step t :item (str token " missing arguments")}))


(defn execute-instruction
  "Takes an Interpreter and a token, and executes the registered
  Instruction using the Interpreter as the (only) argument. Raises an
  exception if the token is not registered."
  [interpreter token]
  (let [unrecognized (not (recognizes-instruction? interpreter token))
        ready (ready-for-instruction? interpreter token)]
  (cond
    unrecognized (oops/throw-unknown-instruction-error token)
    ready (apply-instruction interpreter token)
    :else (push-item 
            interpreter 
            :error 
            (missing-args-message interpreter token)))))


(defn load-items
  "Takes an Interpreter, a stack name, and a collection of items. Puts
  all the items onto the named stack, one at time (probably reversing
  them along the way."
  [interpreter stackname item-list]
  (let [old-stack (get-in interpreter [:stacks stackname])
        new-stack (into old-stack (reverse item-list))]
    (u/set-stack interpreter stackname new-stack)))


(defn- instruction?
  "takes an Interpreter and a keyword, and returns true if the keyword
  is a key of the :instructions registry in that Interpreter instance"
  [interpreter token]
  (contains? (:instructions interpreter) token))


(defn- router-sees?
  "Takes an Interpreter and an item, and returns true if any predicate
  defined in its :router collection matches, nil otherwise (NOTE)."
  [interpreter item]
  (let [recognizers (:router interpreter)]
    (some #(apply (first %) [item]) recognizers)))


(defn- route-item
  "Takes an Interpreter and an item it recognizes (which should be
  established upstream) and sends the item to the designated stack
  determined by the first matching router predicate."
  [interpreter item]
  (let [recognizers (:router interpreter)]
    (push-item 
      interpreter 
      (second (first (filter #(apply (first %) [item]) recognizers)))
      item)))


(defn- handle-unknown-item
  "Takes an Interpreter and an item. If the :config :lenient? flag is
  true, it pushes an unknown item to the :unknown stack; otherwise it
  calls `throw-unknown-push-item-error`"
  [interpreter item]
  (if (get-in interpreter [:config :lenient?])
    (push-item interpreter :unknown item)
    (oops/throw-unknown-push-item-error item)))


(defn handle-item
  "Takes an Interpreter and an item, and either recognizes and invokes
  a keyword registered in that Interpreter as an input or instruction,
  or sends the item to the correct stack (if it exists). Throws an
  exception if the Clojure expression is not recognized explicitly as
  a registered instruction or some other kind of Push literal."
  [interpreter item]
  (cond
    (input? interpreter item)
      (push-item interpreter :exec (item (:inputs interpreter)))
    (instruction? interpreter item)
      (execute-instruction interpreter item)
    (router-sees? interpreter item) (route-item interpreter item)
    (pushcode? item) (load-items interpreter :exec item)
    :else (handle-unknown-item interpreter item)))


(defn clear-all-stacks
  "removes all items from all stacks in an Interpreter"
  [interpreter]
  (let [stacklist (keys (:stacks interpreter))]
    (assoc interpreter :stacks
      (reduce #(assoc %1 %2 '()) {} stacklist))))


(defn push-program-to-code
  "when called, this copies the stored `:program` into the `:code` stack as a block"
  [interpreter]
  (push-item interpreter :code (seq (:program interpreter))))


(defn prep-code-stack
  "when called, this checks the :config of the interpreter and if :preload-code? is truthy it will copy the :program to a code block on top of the :code stack"
  [interpreter]
  (if (get-in interpreter [:config :preload-code?])
    (push-program-to-code interpreter)
    interpreter))


(defn reset-interpreter
  "takes an Interpreter instance and:
  - sets the counter to 0
  - clears all non-:exec stacks
  - puts the program onto the :exec stack"
  [interpreter]
    (-> interpreter
        (clear-all-stacks)
        (assoc , :counter 0)
        (load-items :exec (:program interpreter))
        prep-code-stack))


(defn recycle-interpreter
  "takes an Interpreter instance, a program and new inputs; resets and runs the new setup"
  [interpreter program & {:keys [inputs] :or {inputs []}}]
    (-> interpreter
        (assoc , :program program)
        (register-inputs , inputs)
        reset-interpreter))


(defn increment-counter
  "takes an Interpreter and increments its :counter (without otherwise
  changing it)"
  [interpreter]
  (assoc interpreter :counter (inc (:counter interpreter))))


(defn step-limit
  "reads the [:config :step-limit] value of an Interpreter; returns 0 if nil"
  [interpreter]
  (if-let [stop (get-in interpreter [:config :step-limit])]
    stop
    0))


(defn is-done?
  "Takes and Interpreter and checks various halting conditions.
  Returns true if any is true. Does not change interpreter state."
  [interpreter]
  (let [limit (step-limit interpreter)]
    (or  (and (empty? (u/get-stack interpreter :exec))
              (empty? (u/get-stack interpreter :environment)))
         (>= (:counter interpreter) limit))))


(defn- set-doneness
  "Takes an interpreter and sets its :done? to the `is-done?` result"
  [interpreter]
  (assoc interpreter :done? (is-done? interpreter)))


(defn log-routed-item
  "Takes an Interpreter and any item, and pushes a 'time-stamped' map
  of that item on the Interpreter's :log stack. The 'time-stamp' is the
  counter of the Interpreter when called."
  [interpreter item]
  (push-item interpreter :log {:step (:counter interpreter) :item item}))


(defn soft-environment-ending
  "Called when an Interpreter has an empty :exec stack but a stored :environment on that stack. Merges the stored stacks, keeps the persistent ones, combines the :exec stacks and puts the :return on top."
  [interpreter]
  (let [returns       (u/get-stack interpreter :return)
        current-exec  (u/get-stack interpreter :exec)
        environments  (u/get-stack interpreter :environment)
        retrieved     (first environments)
        old-exec      (:exec retrieved)
        new-exec      (into '() (reverse (concat (reverse returns) current-exec old-exec)))]
    (-> (u/merge-environment interpreter retrieved)
        (u/set-stack , :exec new-exec)
        (u/set-stack , :environment (pop environments))
        (increment-counter ,)
        (log-routed-item , "ENVIRONMENT STACK POPPED")
        (set-doneness ,))))


(defn step
  "Takes an Interpreter, pops one item off :exec, routes it to the
  router, increments the counter. If the :exec stack is empty, does
  nothing."
  [interpreter]
  (let [old-exec (u/get-stack interpreter :exec)]
    (if-not (is-done? interpreter)
      (if (empty? old-exec)
        (soft-environment-ending interpreter)
        (let [next-item (first old-exec)
              new-exec (pop old-exec)]
          (-> interpreter
              (increment-counter)
              (u/set-stack :exec new-exec)
              (handle-item next-item)
              (log-routed-item next-item)
              (set-doneness))))
      interpreter)))


(defn run-n
  "Takes an Interpreter, calls `reset` on it, and calls `step`
  on that reset state for `tick` iterations. Returns the Interpreter
  state at the end.

  Can be called for any non-negative integer `tick` value, regardless
  of halting state."
  [interpreter tick]
  (let [start (assoc-in (reset-interpreter interpreter) [:config :step-limit] tick)]
    (nth (iterate step start) tick)))


(defn entire-run
  "Takes an Interpreter, calls `reset` on it, and returns a (lazy) seq containing all of the steps from the start to the specified end point."
  [interpreter tick]
  (let [start (assoc-in (reset-interpreter interpreter) [:config :step-limit] tick)]
    (take tick (iterate step start))))


(defn last-changed-step
  "Runs a program in the specified interpreter (with a reset) and returns the last step at which the stacks changed"
  [interpreter tick]
  (loop [i (assoc-in (reset-interpreter interpreter) [:config :step-limit] tick)
         stacks (:stacks i)]
    (if (or
          (>= (:counter i) tick)
          (is-done? i)
          (= (:stacks (step i)) stacks))
      (step i)
      (recur (step i) (:stacks (step i))))))


(defn run-until-done
  "Takes an Interpreter, calls `reset` on it, and calls `step`
  on that reset state until :done? is true.

  Does not check for infinite loops."
  [interpreter]
  (let [start (reset-interpreter interpreter)]
    (first (filter is-done? (iterate step start)))))


