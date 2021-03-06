(ns push.util.exceptions)


;; Push runtime errors


(defn throw-redefined-instruction-error
  [token]
  (throw (Exception. (str
                        "Push Instruction Redefined:'" token "'"))))


(defn throw-unknown-instruction-error
  [token]
  (throw (Exception. (str
                        "Unknown Push instruction:'" token "'"))))


(defn throw-invalid-binding-key
  [item]
  (throw (Exception. (str
    "Push Runtime Error: Cannot use '" item "' as a :bindings key."))))



(defn throw-unknown-push-item-error
  [item]
  (throw (Exception. (str
    "Push Parsing Error: Cannot interpret '" item "' as a Push item."))))


(defn throw-invalid-bit-table-error
  [item]
  (throw (Exception. (str
    "Push Argument Error: scalar-to-truth-table can't be made from " item))))


;; DSL exceptions


(defn throw-empty-stack-exception
  [stackname]
  (throw (Exception. (str
                        "Push DSL runtime error: stack "
                        stackname
                        " is empty."))))


(defn throw-invalid-index-exception
  [k]
  (throw (Exception. (str
                        "Push DSL argument error: "
                        (pr-str k)
                        " is not a valid index"))))


(defn throw-missing-key-exception
  [k]
  (throw  (Exception. (str
                         "Push DSL argument error: missing key: "
                         k))))


(defn throw-unknown-stack-exception
  [stackname]
  (throw (Exception. (str
                        "Push DSL argument error: no "
                        stackname
                        " stackname registered."))))


(defn throw-function-argument-exception
  [args]
  (throw (Exception. (str
                        "Push DSL argument error: '"
                        args
                        "' can't be parsed as vector."))))


(defn throw-unknown-DSL-exception
  [instruction]
  (throw (Exception. (str
                        "Push DSL parse error: '"
                        instruction
                        "' is not a known instruction."))))


(defn throw-snapshot-oversize-exception
  []
  (throw (Exception. "Push runtime error: snapshot is over size limit")))



(defn throw-binding-oversize-exception
  []
  (throw (Exception. "Push runtime error: binding is over size limit")))



(defn throw-stack-oversize-exception
  [instruction stackname]
  (throw (Exception. (str instruction " tried to push an oversized item to "
                          stackname))))
