(ns push.instructions.dsl
  (:require [push.util.stack-manipulation   :as stack]
            [push.util.code-wrangling       :as util]
            [push.util.numerics             :as num]
            [push.util.exceptions           :as oops]
            [push.interpreter.core          :as i]
            [push.type.definitions.snapshot :as snap]
            [push.util.scratch              :as sc]
            [dire.core                      :as dire   :refer [with-handler!]]
            ))

;; The PushDSL uses the :scratch map in an Interpreter record

;;;;;;;;;;;;;;;;;;;;;;;;;
(defn get-max-collection-size
  "returns the current :max-collection-size setting from the Interpreter"
  [interpreter]
  (get-in interpreter [:config :max-collection-size] 0))



(defn oversized-item?
  "takes an interpreter, stackname stack and an item; returns `true` if there is room to push (pr in any way put) the item on the stack; throws an exception otherwise"
  [interpreter stackname item]
  (let [limit      (get-max-collection-size interpreter)
        stack      (stack/get-stack interpreter stackname)
        pile-size  (count stack)
        extra-junk (util/count-collection-points item)]
    (< limit (+ pile-size extra-junk))
    ))


(defn nth-item-of-stack
  "Takes an interpreter, a stackname, and a raw scalar value; returns the value of the indicated item"
  [interpreter stackname raw-index]
  (let [stack (stack/get-stack interpreter stackname)]
    (cond
      (empty? stack)
        nil
      (not (number? raw-index))
        (oops/throw-invalid-index-exception raw-index)
      :else
        (nth stack (num/scalar-to-index raw-index (count stack)))
        )))


(defn delete-nth-item-of-stack
  "Takes an interpreter, a stackname, and a raw scalar value"
  [interpreter stackname raw-index]
  (let [old-stack (stack/get-stack interpreter stackname)]
    (cond
      (empty? old-stack)
        interpreter
      (not (number? raw-index))
        (oops/throw-invalid-index-exception raw-index)
      :else
        (let [idx (num/scalar-to-index raw-index (count old-stack))]
          (stack/set-stack
            interpreter
            stackname
            (util/list! (concat (take idx old-stack) (drop (inc idx) old-stack)))
            )))))


(defn consume-nth-as
  "deletes the indicated item and stores it in the scratch variable named and also the ARGS"
  [interpreter stackname raw-index new-name]
  (let [item (nth-item-of-stack interpreter stackname raw-index)]
    (-> interpreter
        (sc/scratch-write , new-name item)
        (sc/scratch-save-arg , item)
        (delete-nth-item-of-stack , stackname raw-index)
        )))


(defn insert-as-nth
  "adds the item in the indicated position in the named stack"
  [interpreter stackname item raw-index]
  (let [old-stack (stack/get-stack interpreter stackname)]
  (cond
    (oversized-item? interpreter stackname item)
      (oops/throw-stack-oversize-exception (:current-item interpreter) stackname)
    (number? raw-index)
      (let [idx     (num/scalar-to-index raw-index (inc (count old-stack)))
            updated (util/list!
                      (concat (take idx old-stack)
                              (list item)
                              (drop idx old-stack)))]
        (stack/set-stack interpreter stackname updated))
    :else
      (oops/throw-invalid-index-exception raw-index)
      )))


(defn delete-top-of
  "?"
  [interpreter stackname]
  (let [old-stack (stack/get-stack interpreter stackname)
        top-item  (first old-stack)]
    (stack/set-stack interpreter stackname (rest old-stack))
    ))


(defn add-error-message!
  "Creates a new `:error` item on the interpreter's stack, with the current `:step` and `:item` field containing the string passed in"
  [interpreter item]
  (let [e (stack/get-stack interpreter :error)
        t (:counter interpreter)
        new-error {:step t :item item}]
    (i/push-item interpreter :error new-error)))

;; working with bindings

(defn oversized-binding?
  "Returns `true` if adding the item to the binding's stack would push it over the interpreter's max-collection-size limit, or false if it would be OK. NOTE: Counts the items in the stack and the _program points_ in the item."
  [interpreter binding-name new-item]
  (let [item-size (util/count-collection-points new-item)
        binding-size (count (get-in interpreter [:bindings binding-name] '()))
        limit (get-max-collection-size interpreter)]
    (< limit (+' item-size binding-size))))


(defn valid-binding-key
  [interpreter scratch-key]
  (if (or (nil? scratch-key)
          (nil? (sc/scratch-read interpreter scratch-key)))
    (keyword (gensym "ref!"))
    (sc/scratch-read interpreter scratch-key)
    ))


(defn bind-item
  "?"
  [interpreter item-key & {:keys [into]}]
  (let [item        (sc/scratch-read interpreter item-key)
        binding-key (valid-binding-key interpreter into)]
    (if (keyword? binding-key)
      (if (oversized-binding? interpreter binding-key item)
        (oops/throw-binding-oversize-exception)
        (i/bind-value interpreter binding-key item))
      (oops/throw-invalid-binding-key binding-key)
      )))


(defn delete-stack
  "Removes an entire named stack."
  [interpreter stackname]
  (let [old-stack (stack/get-stack interpreter stackname)]
    (stack/clear-stack interpreter stackname)
    ))



(defn oversized-stack?
  "Returns `true` if adding the item to the stack would push it over the interpreter's max-collection-size limit, or false if it would be OK. NOTE: Counts the items in the stack and the _program points_ in the item."
  [interpreter stack item]
  (let [item-size (util/count-collection-points item)
        stack-size (count stack)
        limit (get-max-collection-size interpreter)]
    (< limit (+' item-size stack-size))
    ))

;; config variables

(defn set-config-item
  [interpreter keyword value]
  (assoc-in interpreter [:config keyword] value))








;;;;;;;;;;;;;;;;;;;;;;;;
;; DSL instructions
;;;;;;;;;;;;;;;;;;;;;;;;


(defn save-max-collection-size
  "?"
  [interpreter & {:keys [as]}]
  (let [value (get-max-collection-size interpreter)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as value)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-snapshot
  "?"
  [interpreter]
  (let [old-env (or (stack/get-stack interpreter :snapshot) '())
        snap    (snap/snapshot interpreter)
        bigness (util/count-collection-points snap)
        limit   (get-max-collection-size interpreter)]
    (if (< limit bigness)
      (oops/throw-snapshot-oversize-exception)
      (stack/set-stack interpreter :snapshot (util/list! (conj old-env snap))))
      ))


(dire/with-handler! #'save-snapshot
  "Handles oversize errors in `save-snapshot`"
  #(re-find #"snapshot is over size limit" (.getMessage %))
  (fn [e interpreter]
    (add-error-message! interpreter (.getMessage e))
    ))


(dire/with-handler! #'bind-item
  "Handles oversize errors in `bind-item`"
  #(re-find #"binding is over size limit" (.getMessage %))
  (fn [e interpreter item & {:keys [into]}]
    (add-error-message! interpreter (.getMessage e))
    ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn clear-binding
  "?"
  [interpreter kwd]
  (let [binding-name (sc/scratch-read interpreter kwd)]
    (if (some #{binding-name} (keys (:bindings interpreter)))
      (assoc-in interpreter [:bindings binding-name] '())
      interpreter
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn consume-stack
  "?"
  [interpreter stackname & {:keys [as]}]
  (let [old-stack (stack/get-stack interpreter stackname)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write
        (stack/clear-stack interpreter stackname) as old-stack)
        )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn consume-top-of
  "?"
  [interpreter stackname & {:keys [as]}]
  (let [old-stack (stack/get-stack interpreter stackname)
        old-args (sc/scratch-ARGS interpreter)]
    (cond (empty? old-stack)
            (oops/throw-empty-stack-exception stackname)
          (nil? as)
            (oops/throw-missing-key-exception :as)
          :else
            (let [top-item (first old-stack)]
              (-> (stack/set-stack interpreter stackname (rest old-stack))
                   (sc/scratch-save-arg , top-item)
                   (sc/scratch-write , as top-item))
                   ))))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn count-of
  "?"
  [interpreter stackname & {:keys [as]}]
  (if-let [scratch-var as]
    (let [stack (stack/get-stack interpreter stackname)]
      (sc/scratch-write interpreter scratch-var (count stack)))
    (oops/throw-missing-key-exception :as)))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn consume-nth-of
  "?"
  [interpreter stackname & {:keys [at as]}]
  (cond
    (nil? as)
      (oops/throw-missing-key-exception :as)
    (nil? at)
      (oops/throw-missing-key-exception :at)
    (number? at)
      (consume-nth-as interpreter stackname at as)
    :else
      (let [raw-index (sc/scratch-read interpreter at)]
        (if (number? raw-index)
          (consume-nth-as interpreter stackname raw-index as)
          (oops/throw-invalid-index-exception raw-index)
          ))))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn delete-nth-of
  "?"
  [interpreter stackname & {:keys [at]}]
  (cond
    (nil? at)
      (oops/throw-missing-key-exception :at)
    (number? at)
      (delete-nth-item-of-stack interpreter stackname at)
    :else
      (let [raw-index (sc/scratch-read interpreter at)]
        (if (number? raw-index)
          (delete-nth-item-of-stack interpreter stackname raw-index)
          (oops/throw-invalid-index-exception raw-index)
          ))))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn forget-binding
  "?"
  [interpreter kwd]
  (let [binding-name (sc/scratch-read interpreter kwd)
        old-bindings (:bindings interpreter)]
    (assoc interpreter :bindings (dissoc old-bindings binding-name))
    ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn insert-as-nth-of
  "?"
  [interpreter stackname kwd & {:keys [at]}]
  (let [item (sc/scratch-read interpreter kwd)]
    (cond
      (nil? at)
        (oops/throw-missing-key-exception :at)
      (number? at)
        (insert-as-nth interpreter stackname item at)
      :else
        (let [raw-index (sc/scratch-read interpreter at)]
          (if (number? raw-index)
            (insert-as-nth interpreter stackname item raw-index)
            (oops/throw-invalid-index-exception raw-index)
            )))))


(dire/with-handler! #'insert-as-nth-of
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter stackname kwd & {:keys [as at]}]
      (add-error-message! interpreter (.getMessage e))
      ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn push-onto
  "?"
  [interpreter stackname kwd]
  (let [old-stack   (stack/get-stack interpreter stackname)
        new-item    (sc/scratch-read interpreter kwd)
        too-big?    (oversized-stack? interpreter old-stack new-item)
        counter     (:counter interpreter)
        instruction (:current-item interpreter)
        new-stack   (if (or (nil? new-item) too-big?)
                        old-stack
                        (util/list! (conj old-stack new-item)))]
      (if too-big?
        (oops/throw-stack-oversize-exception instruction stackname)
        (stack/set-stack interpreter stackname new-stack)
        )))


(dire/with-handler! #'push-onto
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter stackname kwd & {:keys [as at]}]
      (add-error-message! interpreter (.getMessage e))
      ))

;;;;;;;;;;;;;;;;;;;;;;;;
(defn append-onto
  "?"
  [interpreter stackname kwd]
  (let [old-stack   (stack/get-stack interpreter stackname)
        new-item    (sc/scratch-read interpreter kwd)
        too-big?    (oversized-stack? interpreter old-stack new-item)
        counter     (:counter interpreter)
        instruction (:current-item interpreter)
        new-stack   (if (or (nil? new-item) too-big?)
                        old-stack
                        (util/list! (concat old-stack (list new-item))))]
      (if too-big?
        (oops/throw-stack-oversize-exception instruction stackname)
        (stack/set-stack interpreter stackname new-stack)
        )))


(dire/with-handler! #'append-onto
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter stackname kwd & {:keys [as at]}]
      (add-error-message! interpreter (.getMessage e))
      ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn push-these-onto
  "?"
  [interpreter stackname keywords]
  (let [old-stack     (stack/get-stack interpreter stackname)]
    (let [new-items   (map #(sc/scratch-read interpreter %) keywords)
          too-big?    (oversized-stack? interpreter old-stack new-items)
          instruction (:current-item interpreter)
          new-stack   (if (or (nil? new-items) too-big?)
                        old-stack
                        (util/list! (into old-stack (remove nil? new-items))))]
      (if too-big?
        (oops/throw-stack-oversize-exception instruction stackname)
        (stack/set-stack interpreter stackname new-stack)
        ))))

(dire/with-handler! #'push-these-onto
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter stackname kwd & {:keys [as at]}]
      (add-error-message! interpreter (.getMessage e))
      ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn quote-all-bindings
  "?"
  [interpreter]
  (set-config-item interpreter :quote-refs? true)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn quote-no-bindings
  "?"
  [interpreter]
  (set-config-item interpreter :quote-refs? false)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn replace-binding
  "?"
  [interpreter item & {:keys [into]}]
  (let [new-item (sc/scratch-read interpreter item)
        where    (if (nil? into)
                    (keyword (gensym "ref!"))
                    (sc/scratch-read interpreter into)) ]
    (cond
      (not (keyword? where))
        (oops/throw-invalid-binding-key where)
      (nil? new-item)
        (assoc-in interpreter [:bindings where] '())
      (seq? new-item)
        (assoc-in interpreter [:bindings where] new-item)
      :else
        (assoc-in interpreter [:bindings where] (list new-item))
        )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn replace-stack
  "?"
  [interpreter stackname kwd]
  (let [replacement (sc/scratch-read interpreter kwd)
        new-stack (cond (nil? replacement) (list)
                        (seq? replacement) replacement
                        :else (list replacement))]
      (stack/set-stack interpreter stackname new-stack)
      ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn retrieve-snapshot-state
  "?"
  [interpreter & {:keys [using]}]
  (if (nil? using)
    (oops/throw-missing-key-exception using)
    (stack/merge-snapshot interpreter (sc/scratch-read interpreter using))
    ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-top-of-binding
  "?"
  [interpreter which & {:keys [as]}]
  (let [variable (sc/scratch-read interpreter which)
        value    (i/peek-at-binding interpreter variable)]
    (if (nil? as)
      (oops/throw-missing-key-exception ":as")
      (sc/scratch-write interpreter as value)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-binding-stack
  "?"
  [interpreter which & {:keys [as]}]
  (let [binding-name
          (get-in interpreter [:bindings (sc/scratch-read interpreter which)] '())]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as binding-name)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-stack
  "?"
  [interpreter stackname & {:keys [as]}]
  (let [old-stack (stack/get-stack interpreter stackname)]
    (if (some? as)
      (sc/scratch-write interpreter as old-stack)
      (oops/throw-missing-key-exception :as)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-nth-of
  "?"
  [interpreter stackname & {:keys [as at]}]
  (cond
    (nil? as)
      (oops/throw-missing-key-exception :as)
    (nil? at)
      (oops/throw-missing-key-exception :at)
    (number? at)
      (sc/scratch-write interpreter as
        (nth-item-of-stack interpreter stackname at))
    :else ;; keyword
      (sc/scratch-write interpreter as
        (nth-item-of-stack interpreter stackname
          (sc/scratch-read interpreter at)))
          ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-top-of
  "?"
  [interpreter stackname & {:keys [as]}]
  (let [old-stack (stack/get-stack interpreter stackname)]
    (cond
      (empty? old-stack)
        (oops/throw-empty-stack-exception stackname)
      (nil? as)
        (oops/throw-missing-key-exception ":as")
      :else
        (let [top-item (first old-stack)]
          (sc/scratch-write interpreter as top-item)
          ))))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-bindings
  "?"
  [interpreter & {:keys [as]}]
  (let [varnames (sort (keys (:bindings interpreter)))]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as varnames)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-instructions
  "?"
  [interpreter & {:keys [as]}]
  (let [fxns (set (keys (:instructions interpreter)))]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as fxns)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn save-counter
  "?"
  [interpreter & {:keys [as]}]
  (let [c (:counter interpreter)]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as c)
      )))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-storing-arguments
  "?"
  [interpreter]
  (set-config-item interpreter :store-args? true)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn start-cycling-arguments
  "?"
  [interpreter]
  (set-config-item interpreter :cycle-args? true)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn stop-storing-arguments
  "?"
  [interpreter]
  (set-config-item interpreter :store-args? false)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn stop-cycling-arguments
  "?"
  [interpreter]
  (set-config-item interpreter :cycle-args? false)
  )

;;;;;;;;;;;;;;;;;;;;;;;;

(defn record-an-error
  "?"
  [interpreter & {:keys [from]}]
  (let [c (:counter interpreter)
        old-err (stack/get-stack interpreter :error)]
    (if (nil? from)
      (oops/throw-missing-key-exception :from)
      (let [msg (sc/scratch-read interpreter from)]
        (if (nil? msg)
          interpreter
          (let [err-item {:step c :item msg}
                new-err (util/list! (conj old-err err-item))]
            (stack/set-stack interpreter :error new-err)
            ))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn return-item
  "?"
  [interpreter kwd]
  (let [old-stack   (stack/get-stack interpreter :exec)
        new-item    (sc/scratch-read interpreter kwd)
        too-big?    (oversized-stack? interpreter old-stack new-item)
        counter     (:counter interpreter)
        instruction (:current-item interpreter)
        new-stack   (if (or (nil? new-item) too-big?)
                        old-stack
                        (util/list! (conj old-stack new-item)))]
      (if too-big?
        (oops/throw-stack-oversize-exception instruction :exec)
        (stack/set-stack interpreter :exec new-stack)
        )))


(dire/with-handler! #'return-item
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter kwd]
      (add-error-message! interpreter (.getMessage e))
      ))

;;;;;;;;;;;;;;;;;;;;;;;;

(defn return-codeblock
  "?"
  [interpreter & keywords]
  (let [old-stack   (stack/get-stack interpreter :exec)
        new-items   (util/list!
                      (remove nil?
                        (map #(sc/scratch-read interpreter %) keywords)))
        too-big?    (oversized-stack? interpreter old-stack new-items)
        counter     (:counter interpreter)
        instruction (:current-item interpreter)
        new-stack   (if (or (nil? new-items) too-big?)
                        old-stack
                        (util/list! (conj old-stack new-items)))]
      (if too-big?
        (oops/throw-stack-oversize-exception instruction :exec)
        (stack/set-stack interpreter :exec new-stack)
        )))

(dire/with-handler! #'return-codeblock
  "?"
  #(re-find #"tried to push an oversized item to" (.getMessage %))
  (fn
    [e interpreter & keywords]
      (add-error-message! interpreter (.getMessage e))
      ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn print-item
  "Takes the indicated scratch variable's contents, applies `(pr-str _)` and pushes the result to the `:print` stack. NOTE it does not do size checking."
  [interpreter scratchname]
  (let [old-stack   (stack/get-stack interpreter :print)
        new-item    (sc/scratch-read interpreter scratchname)
        counter     (:counter interpreter)
        instruction (:current-item interpreter)
        new-stack   (if (nil? new-item)
                      old-stack
                      (util/list! (conj old-stack (pr-str new-item))))]
        (stack/set-stack interpreter :print new-stack)
        ))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;;  The all-important calculate
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn calculate
  "?"
  [interpreter args fxn & {:keys [as]}]
  (let [locals (map #(sc/scratch-read interpreter %) args)
        result (if (vector? args)
                  (apply fxn locals)
                  (oops/throw-function-argument-exception args))]
    (if (nil? as)
      (oops/throw-missing-key-exception :as)
      (sc/scratch-write interpreter as result)
      )))

(dire/with-handler! #'calculate
  "Handles Div0 errors in `calculate`"
  #(re-find #"Divide by zero" (.getMessage %))
  (fn
    [e interpreter args fxn & {:keys [as]}]
      (add-error-message! interpreter (.getMessage e))
      ))

(dire/with-handler! #'calculate
  "Handles bigdec vs rational errors in `calculate`"
  #(re-find #"Non-terminating decimal expansion" (.getMessage %))
  (fn
    [e interpreter args fxn & {:keys [as]}]
      (add-error-message! interpreter (.getMessage e))
      ))

(dire/with-handler! #'calculate
  "Handles bad result (Infinite or NaN) runtime errors in `calculate`"
  #(re-find #"Infinite or NaN" (.getMessage %))
  (fn
    [e interpreter args fxn & {:keys [as]}]
      (add-error-message! interpreter (.getMessage e))
      ))

(dire/with-handler! #'calculate
  "Handles bad result (Infinite or NaN) runtime errors in `calculate`"
  java.lang.NullPointerException
  (fn
    [e interpreter args fxn & {:keys [as]}]
    (add-error-message! interpreter (.getMessage e))
    ))

(dire/with-handler! #'calculate
  "pre-defined REAL crashes due to mal-formed DSL syntax; this handler passes the exceptions out to the system (avoiding getting caught in the general case, below)"
  #(re-find #"Wrong number of args" (.getMessage %))
  (fn [e & args] (throw e)
  ))

(dire/with-handler! #'calculate
  "Handles unknown and mysterious exceptions from `calculate`"
  java.lang.RuntimeException
  (fn
    [e interpreter args fxn & {:keys [as]}]
      (do (println "CALCULATION RUNTIME EXCEPTION")
          (println (str
            "\nmessage: " (.getMessage e)
            "\nscratch: " (:scratch interpreter)
            "\nargs: " args
            "\nhandling item " (:current-item interpreter)))
          (throw (Exception. "BAD THING HAPPENED"))
          )))
