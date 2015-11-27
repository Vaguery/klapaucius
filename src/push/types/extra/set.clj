(ns push.types.extra.set
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:use push.instructions.aspects.equatable)
  (:use push.instructions.aspects.movable)
  (:use push.instructions.aspects.printable)
  (:use push.instructions.aspects.returnable)
  (:use push.instructions.aspects.visible)
  (:require [clojure.set :as sets])
  )



(def set-union
  (t/simple-2-in-1-out-instruction
    "`:set-union` pops the top two `:set` items, and pushes their union"
    :set "union" 'sets/union))


(def standard-set-type
  "builds the basic `:set` type, which can hold arbitrary and mixed contents"
  (let [typename :set]
  (-> (t/make-type  :set
                    :recognizer set?
                    :attributes #{:collection :set})
      make-visible 
      make-equatable
      make-movable
      make-printable
      make-returnable
      (t/attach-instruction , set-union)
      )))

