(ns push.push-dsl
  (:use midje.sweet)
  (:use [push.interpreter]))

;; a "PushDSL blob" is just a vector with an interpreter and a hashmap

;; `count-of [stackname :as local]`

(defn throw-unknown-stack-exception
  [stackname]
  (throw 
    (Exception. 
      (str 
        "Push DSL argument error: no "
        stackname
        " stackname registered."))))

(defn count-of
  "Takes an PushDSL blob, a stackname (keyword) and a scratch variable (keyword) in which to store the count.

    Exceptions when:
    - the stack doesn't exist

    Fails silently when:
    - the local is not specified (no :as argument)"
  [[interpreter locals] stackname & {:keys [as]}]
  (if-let [scratch-var as]
    (if-let [stack (get-stack interpreter stackname)]
      (vector interpreter (assoc locals scratch-var (count stack)))
      (throw-unknown-stack-exception stackname))
    (vector interpreter locals)))


(defn throw-empty-stack-exception
  [stackname]
  (throw 
    (Exception. 
      (str 
        "Push DSL runtime error: stack "
        stackname
        " is empty."))))



(defn consume-top-of
  "Takes an PushDSL blob, a stackname (keyword) and a scratch variable (keyword) in which to store the top value from that stack. If no scratch variable is specified, it simply deletes the top item.

    Exceptions when:
    - the stack doesn't exist
    - the stack is empty"
  [ [interpreter locals] stackname & {:keys [as]}]
  (if-let [old-stack (get-stack interpreter stackname)]
    (if-let [top-item (first old-stack)]
      (if (some? as)
        (vector
          (set-stack interpreter stackname (rest old-stack))
          (assoc locals as top-item))
        (vector
          (set-stack interpreter stackname (rest old-stack))
          locals))
      (throw-empty-stack-exception stackname))
    (throw-unknown-stack-exception stackname))
  )