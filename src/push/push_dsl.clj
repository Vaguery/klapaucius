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
  "Takes an PushDSL blob, a stackname (keyword) and a scratch variable (keyword) in which to store the top value. Saves the current number of items on that stack into the named local.

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

