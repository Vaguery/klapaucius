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


(defn throw-unknown-push-item-error
  [item]
  (throw (Exception. (str 
    "Push Parsing Error: Cannot interpret '" item "' as a Push item."))))


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
                        k 
                        " is not an integer"))))


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
