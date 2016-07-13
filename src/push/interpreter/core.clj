(ns push.interpreter.core
  (:require [push.util.stack-manipulation :as u]
            [push.util.exceptions :as oops]
            [push.router.core :as router])
  (:use [push.interpreter.definitions])
  (:use [push.util.type-checkers])
  )


(defn register-type
  "Takes an Interpreter record, and a PushType record, and adds the PushType to the :types collection in the Interpeter; adds the type's :name as a new stack (if not already present); appends the type's :router to the Interpreter's :routers vector; adds the type's internally defined instructions to the Interpreter's registry automatically."
  [interpreter type]
  (let [old-types (:types interpreter)
        old-stacks (:stacks interpreter)
        old-routers (:routers interpreter)
        old-instructions (:instructions interpreter)]
        (-> interpreter
            (assoc :types (conj old-types type))
            (assoc :routers
              (conj old-routers (:router type)))
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


(defn bind-value
  "Takes an interpreter, a keyword, and any item. If the keyword is already registered in the interpreter's :bindings hashmap, the item is pushed to that; otherwise, a new binding is made first. If the item is nil, the binding is created (if necessary) but nothing is pushed to the stack."
  [interpreter kwd item]
  (let [current-stack (get-in interpreter [:bindings kwd] '())]
    (if (nil? item)
      (assoc-in interpreter [:bindings kwd] current-stack)
      (assoc-in interpreter [:bindings kwd] (conj current-stack item)))))


(defn peek-at-binding
  "Takes an interpreter and a keyword. Returns the top item (if any) on the indicated :bindings stack, or `nil` if the keyword is not recognized or there are no items on the stack."
  [interpreter kwd]
  (first (get-in interpreter [:bindings kwd])))


(defn bind-input
  "If the arguments are an Interpreter, a keyword and any item, it will store the item under the keyword key in the Interpreter's :bindings hashmap, pushing the item onto the top of the indicated value stack. If no keyword is given, one is constructed automatically."
  ([interpreter item]
    (let [next-index (inc (count (:bindings interpreter)))
          next-key (keyword (str "input!" next-index))]
      (bind-value interpreter next-key item)))
  ([interpreter kwd item]
    (bind-value interpreter kwd item)))


(defn bind-inputs
  "Takes an Interpreter record, and a hashmap of key-value items. If the interpreter already has some of the bindings assigned, the new values are pushed onto the old stacks."
  [interpreter values]
  (cond
    (vector? values)
      (reduce (partial bind-input) interpreter values)
   (map? values)
    (reduce-kv
      (fn [i k v] (bind-input i k v))
      interpreter
      values)
    :else (throw (Exception. "cannot bind inputs"))))


(defn bound-keyword?
  "Takes an interpreter, and a keyword, and returns true if the keyword is a key in the :bindings hashmap"
  [interpreter kwd]
  (contains? (:bindings interpreter) kwd))


(defn add-instruction
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


(defn forget-instruction
  "Takes an Interpreter and a keyword (the name of an instruction, supposedly). Un-registers ('forgets') the instruction, returning the Interpreter"
  [interpreter kw]
  (let [old-instructions (:instructions interpreter)]
    (assoc interpreter :instructions (dissoc old-instructions kw))))


;;; reconfigure


(defn reconfigure
  "takes an interpreter and a map of configuration pairs, and merges the map with the current interpreter map"
  [interpreter new-config]
  (let [old-config (:config interpreter)]
    (assoc interpreter :config (merge old-config new-config))))


;;; manipulating Interpreter state


(defn contains-at-least?
  "Takes an interpreter, a stack name, and a count; returns true if
  the named stack exists, and has at least that many items on it"
  [interpreter stack limit]
    (<= 
      limit 
      (count (u/get-stack interpreter stack))))


(defn recognizes-instruction?
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
  (let [needs (get-in interpreter [:instructions token :needs] )]
    (and
      (recognizes-instruction? interpreter token)
      (reduce-kv
        (fn [truth k v]
          (and truth (contains-at-least? interpreter k v)))
        true
        needs))))


(defn get-instruction
  [interpreter token]
  (get-in interpreter [:instructions token]))




(defn apply-instruction
  "Takes an interpreter and a token. Returns the interpreter. If the `:store-args?` value is `true` in the interpreter's `:config`, the arguments will be saved onto the `:ARGS` binding. If the `:cycle-args?` value is `true` in the interpreter's `:config`, the arguments will (also) be appended to the tail of `:exec`. NOTE: returns ONLY the interpreter, not the state tuple."
  [interpreter token]
  (let [applied ((:transaction (get-instruction interpreter token)) interpreter)
                ;; ^^ this executes the instruction on the interpreter, returns tuple
                ;; of [interpreter scratch]
        updated (first applied)            
                ;; just the modified interpreter resulting from the instruction
        store?  (get-in updated [:config :store-args?] false)
        cycle?  (get-in updated [:config :cycle-args?] false)
        args    (:ARGS (second applied))
        ]
    (-> (if store?
          (bind-value updated :ARGS args)
          updated)
    )))
      



(defn push-item
  "Takes an Interpreter, a stack name and a Clojure expression, and returns the Interpreter with the item pushed onto the specified stack. If the stack doesn't already exist, it is created. If the item is nil, no change occurs."
  [interpreter stack item]
  (if (nil? item)
    interpreter
    (let [old-stack (get-in interpreter [:stacks stack])]
      (assoc-in interpreter [:stacks stack] (conj old-stack item)))))


(defn missing-args-message
  [interpreter token]
  (let [t (:counter interpreter)]
    {:step t :item (str token " missing arguments")}))


(defn execute-instruction
  "Takes an Interpreter and a token, and executes the registered Instruction using the Interpreter as the (only) argument. Raises an exception if the token is not registered."
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


(defn instruction?
  "takes an Interpreter and a keyword, and returns true if the keyword
  is a key of the :instructions registry in that Interpreter instance"
  [interpreter token]
  (contains? (:instructions interpreter) token))


(defn routers-see?
  "Takes an Interpreter and an item, and returns true if any of its :routers collection matches. NOTE: returns nil otherwise!"
  [interpreter item]
  (let [recognizers (:routers interpreter)]
    (boolean (some #(router/router-recognize? % item) recognizers))))


(defn route-item
  "Takes an Interpreter and an item it recognizes (which should be established upstream) and sends the item to the designated stack determined by the first matching router predicate."
  [interpreter item]
  (let [all-routers (:routers interpreter)
        active-router (first (filter #(router/router-recognize? % item) all-routers))
        preprocessor (:preprocessor active-router)
        preprocessed-item (preprocessor item)
        target-stack (:target-stack active-router)]
    (push-item 
      interpreter 
      target-stack
      preprocessed-item)))


(defn handle-unknown-item
  "Takes an Interpreter and an item. If the :config :lenient? flag is
  true, it pushes an unknown item to the :unknown stack; otherwise it
  calls `throw-unknown-push-item-error`"
  [interpreter item]
  (if (keyword? item)
    (push-item interpreter :ref item)
    (if (get-in interpreter [:config :lenient?])
      (push-item interpreter :unknown item)
      (oops/throw-unknown-push-item-error item))))


(defn handle-item
  "Takes an Interpreter and an item, and either recognizes and invokes
  a keyword registered in that Interpreter as an binding or instruction,
  or sends the item to the correct stack (if it exists). Throws an
  exception if the Clojure expression is not recognized explicitly as
  a registered instruction or some other kind of Push literal."
  [interpreter item]
  (cond
    (keyword? item)
      (cond
        (bound-keyword? interpreter item)
          (if (get-in interpreter [:config :quote-refs?])
            (push-item interpreter :ref item)
            (push-item interpreter :exec (peek-at-binding interpreter item)))
        (instruction? interpreter item)
          (execute-instruction interpreter item)
        :else (push-item interpreter :ref item))
    (routers-see? interpreter item) (route-item interpreter item)
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
  "takes an Interpreter instance, a program and new bindings; resets and runs the new setup"
  [interpreter program & {:keys [bindings] :or {bindings []}}]
    (-> interpreter
        (assoc , :program program)
        (assoc , :bindings {})
        (bind-inputs , bindings)
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
              (empty? (u/get-stack interpreter :snapshot)))
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


(defn soft-snapshot-ending
  "Called when an Interpreter has an empty :exec stack but a stored :snapshot on that stack. Merges the stored stacks, keeps the persistent ones, combines the :exec stacks and puts the :return on top."
  [interpreter]
  (let [returns       (u/get-stack interpreter :return)
        current-exec  (u/get-stack interpreter :exec)
        snapshots  (u/get-stack interpreter :snapshot)
        retrieved     (first snapshots)
        old-exec      (:exec retrieved)
        new-exec      (into '() (reverse (concat (reverse returns) current-exec old-exec)))]
    (-> (u/merge-snapshot interpreter retrieved)
        (u/set-stack , :exec new-exec)
        (u/set-stack , :snapshot (pop snapshots))
        (increment-counter ,)
        (log-routed-item , "SNAPSHOT STACK POPPED")
        (set-doneness ,))))


(defn step
  "Takes an Interpreter, pops one item off :exec, routes it to the
  router, increments the counter. If the :exec stack is empty, does
  nothing."
  [interpreter]
  (let [old-exec (u/get-stack interpreter :exec)]
    (if-not (is-done? interpreter)
      (if (empty? old-exec)
        (soft-snapshot-ending interpreter)
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


