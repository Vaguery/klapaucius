(ns push.instructions.dsl
  (:require [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [push.util.exceptions :as oops]
            [push.interpreter.core :as i]
            [push.type.definitions.snapshot :as snap]
            [dire.core :refer [with-handler!]]
            ))


;;;; a "PushDSL blob" is just a vector containing an interpreter and a hashmap


;; utilities


(defn get-max-collection-size
  "returns the current :max-collection-size setting from the Interpreter"
  [interpreter]
  (get-in interpreter [:config :max-collection-size]))


(defn- list!
  "Jams the argument into a list."
  [collection]
  (into '() (reverse collection)))


(defn- delete-nth
  "Removes an indexed item from a seq; raises an Exception if the seq
  is empty."
  [coll idx]
  {:pre  [(seq coll)
          (not (neg? idx))
          (< idx (count coll))]}
  (list! (concat (take idx coll) (drop 1 (drop idx coll)))))


(defn- insert-as-nth
  "Inserts the item so it is in the indicated position of the result. Note bounds
  of possible range are [0,length] (it can be placed last)."
  [coll item idx]
  {:pre  [(seq? coll)
          (not (neg? idx))
          (<= idx (count coll))]}
  (list! (concat (take idx coll) (list item) (drop idx coll))))


(defn index-from-scratch-ref
  "Takes a keyword and a scratch hashmap. If an integer is stored
  under that key in the hashmap, it's returned. Otherwise raises an
  exception."
  [k locals]
  (let [stored (k locals)]
    (if (number? stored)
      stored
      (oops/throw-invalid-index-exception (k locals)))))


(defn- valid-DSL-index
  "Takes an item (presumably part of a DSL function requiring an :at
  index) and a hashmap, and returns an integer index value, or an
  integer from the hashmap if a keyword. Blows up informatively if
  neither of those is possible."
  [item scratch]
  (cond
    (integer? item) item
    (keyword? item) (index-from-scratch-ref item scratch)
    (nil? item) (oops/throw-missing-key-exception :at)
    :else (oops/throw-invalid-index-exception item)))


(defn- get-nth-of
  "Abstract function invoked by all the X-nth-of DSL functions.
  Takes a PushDSL blob, a stackname, an :at index (integer or keyword)
  (but no :as keyword), and returns the index and the item at that
  index, raising any argument exceptions it finds."
  [[interpreter scratch] stackname & {:keys [at]}]
  (let [old-stack (u/get-stack interpreter stackname)]
    (if (empty? old-stack)
      (oops/throw-empty-stack-exception stackname)
      (let [idx (valid-DSL-index at scratch)
            which (fix/safe-mod idx (count old-stack))]
        [which old-stack]))))


(defn save-ARG
  [scratch item]
  (let [old-args (:ARGS scratch)]
    (assoc scratch :ARGS (conj old-args item))))



(defn add-error-message!
  "Creates a new `:error` item on the interpreter's stack, with the current `:step` and `:item` field containing the string passed in"
  [interpreter item]
  (let [e (u/get-stack interpreter :error)
        t (:counter interpreter)
        new-error {:step t :item item}]
    (i/push-item interpreter :error new-error)))


;; DSL instructions


(defn save-max-collection-size
  "stores the current max-collection-size in a named scratch variable"
  [[interpreter scratch] & {:keys [as]}]
  (let [value (get-in interpreter [:config :max-collection-size])]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      [interpreter (assoc scratch as value)])))



(defn save-snapshot
  "Creates a `:snapshot` item which contains ALL the current :stacks, :bindings and :config hashes. Does not change any of the stack contents. Produces an `:error` instead of a snapshot if snapshot image being saved is oversized (per max-collection-size)."
  [[interpreter scratch]]
  (let [old-env (or (u/get-stack interpreter :snapshot) '())
        snap    (snap/snapshot interpreter)
        bigness (fix/count-collection-points snap)
        limit   (get-max-collection-size interpreter)]
    [ (if (< limit bigness)
        (oops/throw-snapshot-oversize-exception)
        (u/set-stack interpreter :snapshot (conj old-env snap)))
      scratch]))



(with-handler! #'save-snapshot
  "Handles oversize errors in `save-snapshot`"
  #(re-find #"snapshot is over size limit" (.getMessage %))
  (fn
    [e [interpreter scratch]]
      [(add-error-message! interpreter (.getMessage e))
       scratch]
    ))




(defn bind-item
  "Binds the item stored in the second scratch variable under a keyword stored in the first scratch variable argument. If the :into argument is `nil`, a new binding name is generated automatically. If the item to be sotred is a keyword (referring to a scratch variable) an exception is thrown."
  [[interpreter scratch] item & {:keys [into]}]
  (if (nil? into)
    [(i/bind-value interpreter (keyword (gensym "ref!")) item) scratch]
    (if-not (keyword? (into scratch))
      (oops/throw-invalid-binding-key into)
      [(i/bind-value interpreter (into scratch) (item scratch)) scratch])))



(defn clear-binding
  "Takes a PushDSL blob and a scratch keyword. All items stored in the named `:binding` are deleted, though the key itself will remain. If for some reason the scratch argument is not a known binding, there is no bad effect."
  [[interpreter scratch] kwd]
  (let [binding-name (kwd scratch)]
    (if (some #{binding-name} (keys (:bindings interpreter)))
      [(assoc-in interpreter [:bindings binding-name] '()) scratch]
      [interpreter scratch])))



(defn consume-stack
  "Removes an entire named stack; if an `:as local` argument is given,
  it saves the stack in that scratch variable. If no local is given,
  it just deletes the stack.

  Exceptions when:
  - the stack doesn't exist"
  [[interpreter scratch] stackname & {:keys [as]}]
  (let [old-stack (u/get-stack interpreter stackname)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      [(u/clear-stack interpreter stackname)
        (assoc scratch as old-stack)])))




(defn consume-top-of
  "Takes an PushDSL blob, a stackname (keyword) and a scratch variable
  (keyword) in which to store the top value from that stack.

    Exceptions when:
    - the stack doesn't exist
    - the stack is empty"
  [ [interpreter scratch] stackname & {:keys [as]}]
  (let [old-stack (u/get-stack interpreter stackname)
        old-args (:ARGS scratch)]
    (cond (empty? old-stack) (oops/throw-empty-stack-exception stackname)
          (nil? as) (oops/throw-missing-key-exception :as)
          :else (let [top-item (first old-stack)]
                  [(u/set-stack interpreter stackname (rest old-stack))
                   (-> scratch
                       (save-ARG , top-item)
                       (assoc , as top-item)) ]))))
      


(defn count-of
  "Takes an PushDSL blob, a stackname (keyword) and a scratch variable (keyword) in which to store the count. Fails silently when the local is not specified (no :as argument)"
  [[interpreter scratch] stackname & {:keys [as]}]
  (if-let [scratch-var as]
    (let [stack (u/get-stack interpreter stackname)]
      [interpreter (assoc scratch scratch-var (count stack))])
    (oops/throw-missing-key-exception :as)))



(defn consume-nth-of
  "Takes a PushDSL blob, a stackname (keyword), an index argument (an
  integer or a keyword), and a scratch key (also a keyword), copies
  the indicated item from that stack into the scratch variable, and
  deletes it from the stack.

  Exceptions when:
  - the stack doesn't exist
  - the stack is empty
  - no :as argument is present
  - no :at argument is present
  - the :at argument is not a keyword or integer
  - the scratch value passed as a reference is not an integer"
  [[interpreter scratch] stackname & {:keys [as at]}]
  (let [[idx old-stack]
          (get-nth-of [interpreter scratch] stackname :at at)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (let [new-stack (delete-nth old-stack idx)
            saved-item (nth old-stack idx)]
        [(u/set-stack interpreter stackname new-stack)
         (-> scratch
             (save-ARG , saved-item)
             (assoc , as saved-item))]))))


(defn delete-nth-of
  "Usage: `delete-nth-of [stackname :at where]`

  Removes item at index `where` from stack `stackname`. If `where` is 
  an integer, the index deleted is `(mod where (count stackname))`; if
  it is a scratch reference, the numerical value is looked up. If the
  item stored under key `where` is not an integer, an error occurs.

  Exceptions when:
    - the stack doesn't exist
    - the stack is empty
    - no index is given
    - the index is a :keyword that doesn't point to an integer"
  [[interpreter scratch] stackname & {:keys [as at]}]
  (let [[idx old-stack]
          (get-nth-of [interpreter scratch] stackname :at at)]
    (let [new-stack (delete-nth old-stack idx)]
      [(u/set-stack interpreter stackname new-stack) scratch])))


(defn delete-stack
  "Removes an entire named stack."

  [[interpreter scratch] stackname]
  (let [old-stack (u/get-stack interpreter stackname)]
    [(u/clear-stack interpreter stackname) scratch]))


(defn delete-top-of
  "Takes an PushDSL blob and a stackname (keyword); deletes the top
  item of that stack."
  [[interpreter scratch] stackname]
  (let [old-stack (u/get-stack interpreter stackname)
        top-item  (first old-stack)]
    [(u/set-stack interpreter stackname (rest old-stack)) scratch]))


(defn forget-binding
  "Takes a PushDSL blob and a scratch keyword. The interpreter forgets (permanently) the `:binding` stored under in the keyword. If for some reason the scratch argument is not a known binding, there is no bad effect."
  [[interpreter scratch] kwd]
  (let [binding-name (kwd scratch)
        old-bindings (:bindings interpreter)]
    [(assoc interpreter :bindings (dissoc old-bindings binding-name)) scratch]))


(defn insert-as-nth-of
  "Usage: `insert-of-nth-of [stackname local :at where]`

  Place item stored in scratch variable `local` into the named stack
  so it becomes the new item in the `where` position. If `where` is 
  an integer, the index deleted is `(mod where (count stackname))`; if
  it is a scratch reference, the numerical value is looked up. If the
  item stored under key `where` in scratch is not an integer, an error
  occurs.

  Exceptions when:
    - the stack is empty
    - no index is given
    - the index is a :keyword that doesn't point to an integer"
  [[interpreter scratch] stackname kwd & {:keys [as at]}]
    (let [old-stack (u/get-stack interpreter stackname)]
      (let [idx (valid-DSL-index at scratch)
            which (fix/safe-mod idx (inc (count old-stack)))
            new-item (kwd scratch)
            new-stack (insert-as-nth old-stack new-item which)]
        [(u/set-stack interpreter stackname new-stack) scratch])))



(defn oversized-stack?
  "Returns `true` if adding the item to the stack would push it over the interpreter's max-collection-size limit, or false if it would be OK. Counts the items in the stack and the _program points_ in the item."
  [interpreter stack item]
  (let [item-size (fix/count-collection-points item)
        stack-size (count stack)
        limit (get-max-collection-size interpreter)]
    (< limit (+' item-size stack-size))))



(defn push-onto
  "Takes a PushDSL blob, a stackname (keyword) and a scratch key (also keyword), and puts item stored in the scratch variable on top of the named stack. If the scratch item is nil, there is no effect (and no exception); if it is a list, that is pushed _as a single item_ onto the stack (not concatenated). No type checking is used. If the total number of items in the stack and program-points in the item is more than the interpreter's `max-collection-size`, the item is not pushed and an :error is pushed to that stack instead. Does not warn when the keyword isn't defined."
  [[interpreter scratch] stackname kwd]
  (let [old-stack (u/get-stack interpreter stackname)]
    (let [new-item    (kwd scratch)
          too-big?    (oversized-stack? interpreter old-stack new-item)
          error-stack (u/get-stack interpreter :error)
          counter     (:counter interpreter)
          new-stack   (if (or (nil? new-item) too-big?)
                        old-stack
                        (conj old-stack new-item))]
      (if too-big?
        [(u/set-stack
            interpreter
            :error
            (conj error-stack 
                  {:step counter
                   :item (str "oversized push-onto attempted to " stackname)})) scratch]
        [(u/set-stack interpreter stackname new-stack) scratch]))))



(defn push-these-onto
  "Takes a PushDSL blob, a stackname (keyword) and a vector of scratch
  keys (all keywords), and puts each item stored in the scratch
  variables onto top of the named stack, in order specified. If any of
  the stored items is nil, there is no effect (and no exception). No
  type checking is used. Does not warn when the keyword isn't defined."
  [[interpreter scratch] stackname keywords]
  (let [old-stack (u/get-stack interpreter stackname)]
    (let [new-items (map scratch keywords)
          new-stack (into old-stack (remove nil? new-items))]
      [(u/set-stack interpreter stackname new-stack) scratch])))



(defn quote-all-bindings
  "Sets the Interpreter's `:quote-refs?` flag to `true`, so that any keyword that would normally be recognized as a bound variable is instead pushed to the :ref stack without being resolved"
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :quote-refs?] true) scratch])


(defn quote-no-bindings
  "Sets the Interpreter's `:quote-refs?` flag to `false`, so that any keyword that is registered is resolved by examining the items associated with it"
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :quote-refs?] false) scratch])


(defn replace-binding
  "The `item` argument should contain a keyword reference to the scratch map; the optional `:into` argument should be a keyword referring to a scratch variable that holds a keyword (otherwise an exception is raised). If the `:into` argument is absent, a new `:ref` is generated automatically. If the item being stored is itself a `seq`, that list is bound _as the stack_ in the bindings table. If the item is not a `seq`, it becomes the only item stored in the binding."
  [[interpreter scratch] item & {:keys [into]}]
  (let [new-item (item scratch)
        where    (if (nil? into) (keyword (gensym "ref!")) (into scratch))]
    (cond
      (not (keyword? where))
        (oops/throw-invalid-binding-key where)
      (nil? new-item)
        [(assoc-in interpreter [:bindings where] '()) scratch] ;; basically clear it        
      (seq? new-item)
        [(assoc-in interpreter [:bindings where] new-item) scratch]
      :else
        [(assoc-in interpreter [:bindings where] (list new-item)) scratch])))



(defn replace-stack
  "Takes a PushDSL blob, a stackname (keyword) and a scratch key (also keyword), and replaces the named stack with the item stored in the scratch variable. If the contents are a list, the stack is replaced with the entire list; if nil, the stack is emptied; if a non-list item the final stack will contain only that item. Does not warn when the keyword isn't defined."
  [[interpreter scratch] stackname kwd]
  (let [replacement (kwd scratch)
        new-stack (cond (nil? replacement) (list)
                        (seq? replacement) replacement
                        :else (list replacement))]
      [(u/set-stack interpreter stackname new-stack) scratch]))



(defn retrieve-snapshot-state
  "The second argument (:using) is a `:snapshot` item. Delete all stacks from the current Interpreter except :print, :log, :unknown and :error, then merge in the archived stacks. Note: if the archived hash lacks some stacks present in the running stacks, too bad!"
  [[interpreter scratch] & {:keys [using]}]
  (if (nil? using)
    (oops/throw-missing-key-exception using)
    [(u/merge-snapshot interpreter (using scratch)) scratch]))


(defn save-top-of-binding
  "Takes a PushDSL blob, a scratch keyword, and a second :as scratch keyword; looks up the top item in the binding keyed by the first argument, and stores that value (which may be nil) in the second scratch variable. Exception if there is no :as argument. Fine if the result is nil."
  [[interpreter scratch] which & {:keys [as]}]
  (let [v (i/peek-at-binding interpreter (which scratch))]
    (if (nil? as)
      (oops/throw-missing-key-exception ":as")
      [interpreter (assoc scratch as v)])))


(defn save-binding-stack
  "Takes a PushDSL blob, one scratch key (holding a :ref keyword) and a target scratch key, and copies the named binding's stack into the scratch variable (without deleting it). If no binding exists under the stored key, an empty list is saved. Raises an Exception if no :as argument is present"
  [[interpreter scratch] which & {:keys [as]}]
  (let [b (or (get-in interpreter [:bindings (which scratch)]) (list))]
    (if (some? as)
      [interpreter (assoc scratch as b)]
      (oops/throw-missing-key-exception :as))))


(defn save-stack
  "Takes a PushDSL blob, a stackname (keyword) and a scratch key (also
  keyword), and copies that stack into the scratch variable (without
  deleting it).

  Exceptions when:
  - no :as argument is present"
  [[interpreter scratch] stackname & {:keys [as]}]
  (let [old-stack (u/get-stack interpreter stackname)]
    (if (some? as)
      [interpreter (assoc scratch as old-stack)]
      (oops/throw-missing-key-exception :as))))


(defn save-nth-of
  "Takes a PushDSL blob, a stackname (keyword), an index argument (an
  integer or a keyword), and a scratch key (also a keyword), and
  copies the indicated item from that stack into the scratch
  variable (without deleting it).

  Exceptions when:
  - the stack doesn't exist
  - the stack is empty
  - no :as argument is present
  - no :at argument is present
  - the :at argument is not a keyword or integer
  - the scratch value passed as a reference is not an integer"
  [[interpreter scratch] stackname & {:keys [as at]}]
  (let [[idx old-stack]
          (get-nth-of [interpreter scratch] stackname :at at)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (let [saved-item (nth old-stack idx)]
        [interpreter (assoc scratch as saved-item)]))))


(defn save-top-of
  "Takes a PushDSL blob, a stackname (keyword) and a scratch key (also
  keyword), and copies the top item from that stack into the scratch
  variable (without deleting it).

  Exceptions when:
  - no :as argument is present
  - the stack is empty"
  [[interpreter scratch] stackname & {:keys [as]}]
  (let [old-stack (u/get-stack interpreter stackname)]
    (cond (empty? old-stack) (oops/throw-empty-stack-exception stackname)
          (nil? as) (oops/throw-missing-key-exception ":as")
          :else (let [top-item (first old-stack)]
                     [interpreter (assoc scratch as top-item)]))))


(defn save-bindings
  "Saves a sorted list of all the registered :bindings keywords to the named scratch variable"
  [[interpreter scratch] & {:keys [as]}]
  (let [varnames (sort (keys (:bindings interpreter)))]
    (if (nil? as)
          (oops/throw-missing-key-exception :as)
       [interpreter (assoc scratch as varnames)])))


(defn save-instructions
  "Saves a set containing all the registered :instruction keywords to the named scratch variable"
  [[interpreter scratch] & {:keys [as]}]
  (let [fxns (into #{} (keys (:instructions interpreter)))]
    (if (nil? as)
          (oops/throw-missing-key-exception :as)
       [interpreter (assoc scratch as fxns)])))


(defn save-counter
  "Saves the current :counter value to the named scratch variable"
  [[interpreter scratch] & {:keys [as]}]
  (let [c (:counter interpreter)]
    (if (nil? as)
          (oops/throw-missing-key-exception :as)
       [interpreter (assoc scratch as c)])))



(defn start-storing-arguments
  "Sets the Interpreter's `:store-args?` flag to `true`. (Many) arguments consumed by instructions executed will be pushed (as code blocks) onto the `:ARGS` binding"
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :store-args?] true) scratch])



(defn start-cycling-arguments
  "Sets the Interpreter's `:cycle-args?` flag to `true`. (Many) arguments consumed by instructions executed will be pushed (as code blocks) onto the tail of `:exec` when it's true."
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :cycle-args?] true) scratch])



(defn stop-storing-arguments
  "Sets the Interpreter's `:store-args?` flag to `false`. Arguments will be consumed by instructions."
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :store-args?] false) scratch])



(defn stop-cycling-arguments
  "Sets the Interpreter's `:cycle-args?` flag to `false`. Arguments will not be sent to the tail of `:exec`."
  [[interpreter scratch]]
  [(assoc-in interpreter [:config :cycle-args?] false) scratch])



(defn record-an-error
  "Creates a time-stamped entry on the Interpreter's :error stack, with the specified scratch variable as the associeated :item"
  [[interpreter scratch] & {:keys [from]}]
  (let [c (:counter interpreter)
        old-err (u/get-stack interpreter :error)]
    (if (nil? from)
      (oops/throw-missing-key-exception :from)
      (let [msg (from scratch)]
        (if (nil? msg)
          [interpreter scratch]
          (let [err-item {:step c :item msg}
                new-err (conj old-err err-item)]
            [(u/set-stack interpreter :error new-err) scratch]))))))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;  The all-important calculate
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(defn calculate
  "Takes a PushDSL blob, a vector of keywords referring to scratch
  item keys, a function over _those keys_ (using positional notation),
  and an :as keyword in which to store the result of the function.

  Exceptions when:
  - [args] is not a vector
  - no :as argument is present
  - the wrong number of arguments are provided
  - (does not check for nil arguments)"
  [[interpreter scratch] args fxn & {:keys [as]}]
  (let [locals (map scratch args)
        result (if (vector? args)
                  (apply fxn locals)
                  (oops/throw-function-argument-exception args))]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      [interpreter (assoc scratch as result)])))





(with-handler! #'calculate
  "Handles Div0 errors in `calculate`"
  #(re-find #"Divide by zero" (.getMessage %))
  (fn
    [e [interpreter scratch] args fxn & {:keys [as]}]
      [(add-error-message! interpreter (.getMessage e))
       (assoc scratch as nil)]
    ))



(with-handler! #'calculate
  "Handles bigdec vs rational errors in `calculate`"
  #(re-find #"Non-terminating decimal expansion" (.getMessage %))
  (fn
    [e [interpreter scratch] args fxn & {:keys [as]}]
      [(add-error-message! interpreter (.getMessage e))
       (assoc scratch as nil)]
    ))


(with-handler! #'calculate
  "Handles bad result (Infinite or NaN) runtime errors in `calculate`"
  #(re-find #"Infinite or NaN" (.getMessage %))
  (fn
    [e [interpreter scratch] args fxn & {:keys [as]}]
      [(add-error-message! interpreter (.getMessage e))
       (assoc scratch as nil)]
    ))





