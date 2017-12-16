(ns push.type.definitions.quoted)


(defrecord QuotedCode [value])


(defn push-quote
  "takes any Push item (single literal, keyword, or code block) and returns a QuotedCode Push item with that value; if the item is nil, returns nil"
  [item]
  (if (nil? item) nil (->QuotedCode item)))


(defn quoted-code?
  "a type checker that returns true if the argument is a QuotedCode record"
  [item]
  (instance? push.type.definitions.quoted.QuotedCode item))
