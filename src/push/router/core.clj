(ns push.router.core)



(defrecord PushRouter [name recognizer target-stack preprocessor])


(defn make-router
  "Creates a PushRouter record from a name (keyword), recognizer (predicate function, defaults to false), an optional target stack name (keyword, defaults to :name), and an optional preprocessor (which defaults to the identity function)."
  [name & {
    :keys [recognizer target-stack preprocessor]
    :or {recognizer (constantly false)
         target-stack name
         preprocessor identity}}]
  (->PushRouter name recognizer target-stack preprocessor))


(defn router-recognize?
  "takes a `PushRouter` and any item, and returns the result of applying the `:recognizer` to the item"
  [router item]
  ((:recognizer router) item))


(defn router-preprocess
  "takes a `PushRouter` and any item, and applies the defined :preprocessor in the router to the item"
  [router item]
  ((:preprocessor router) item))