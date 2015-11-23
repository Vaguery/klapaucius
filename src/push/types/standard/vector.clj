(ns push.types.standard.vector
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  (:require [push.instructions.modules.print :as print])
  (:require [push.instructions.modules.environment :as env])
  (:require [push.types.standard.vectorized :as v])
  )




(def standard-vector-type
  "builds the basic `:vector` type, which can hold arbitrary and mixed contents"
  (let [typename :vector
        componentname :code]
  (-> (t/make-type  :vector
                      :recognizer vector?
                      :attributes #{:collection :vector})
      t/make-visible 
      t/make-equatable
      t/make-movable
      print/make-printable
      env/make-returnable
      (t/attach-instruction , (v/x-butlast-instruction typename))
      (t/attach-instruction , (v/x-concat-instruction typename))
      (t/attach-instruction , (v/x-conj-instruction typename componentname))
      (t/attach-instruction , (v/x-contains?-instruction typename componentname))
      (t/attach-instruction , (v/x-do*each-instruction typename))
      (t/attach-instruction , (v/x-emptyitem?-instruction typename))
      (t/attach-instruction , (v/x-first-instruction typename componentname))
      (t/attach-instruction , (v/x-indexof-instruction typename componentname))
      (t/attach-instruction , (v/x-last-instruction typename componentname))
      (t/attach-instruction , (v/x-length-instruction typename))
      (t/attach-instruction , (v/x-new-instruction typename))
      (t/attach-instruction , (v/x-nth-instruction typename componentname))
      (t/attach-instruction , (v/x-occurrencesof-instruction typename componentname))
      (t/attach-instruction , (v/x-portion-instruction typename))
      (t/attach-instruction , (v/x-shatter-instruction typename componentname))
      (t/attach-instruction , (v/x-remove-instruction typename componentname))
      (t/attach-instruction , (v/x-replace-instruction typename componentname))
      (t/attach-instruction , (v/x-replacefirst-instruction typename componentname))
      (t/attach-instruction , (v/x-rest-instruction typename))
      (t/attach-instruction , (v/x-set-instruction typename componentname))
      (t/attach-instruction , (v/x-take-instruction typename))
      (t/attach-instruction , (v/x-reverse-instruction typename))
      )))

