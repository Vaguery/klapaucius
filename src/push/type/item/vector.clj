(ns push.type.item.vector
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [push.instructions.aspects :as aspects]
            [push.type.item.vectorized :as v]
            ))



(def vector-refilter
  (core/build-instruction
    vector-refilter
    "`:vector-refilter` pops the top `:vector` value, and sends it to the `:exec` stack"
    :tags #{:conversion :vector}
    (d/consume-top-of :vector :as :arg)
    (d/push-onto :exec :arg)))



(def vector-refilterall
  (core/build-instruction
    vector-refilterall
    "`:vector-refilterall` puts the entire `:vector` stack on top of the `:exec` stack"
    :tags #{:conversion :vector}
    (d/consume-stack :vector :as :stack)
    (d/consume-stack :exec :as :old-exec)
    (d/calculate [:stack :old-exec]
        #(into '() (reverse (concat %1 %2))) :as :new-exec)
    (d/replace-stack :exec :new-exec)))



(def standard-vector-type
  "builds the basic `:vector` type, which can hold arbitrary and mixed contents"
  (let [typename :vector
        componentname :code]
  (-> (t/make-type  :vector
                      :recognized-by vector?
                      :attributes #{:collection :vector})
      aspects/make-collectible
      aspects/make-cycling
      aspects/make-equatable
      aspects/make-into-tagspaces
      aspects/make-movable
      aspects/make-printable
      aspects/make-quotable
      aspects/make-repeatable
      aspects/make-returnable
      aspects/make-storable
      aspects/make-taggable
      aspects/make-visible 
      (t/attach-instruction , vector-refilter)
      (t/attach-instruction , vector-refilterall)
      (t/attach-instruction , (v/x-butlast-instruction typename))
      (t/attach-instruction , (v/x-byexample-instruction typename componentname))
      (t/attach-instruction , (v/x-concat-instruction typename))
      (t/attach-instruction , (v/x-conj-instruction typename componentname))
      (t/attach-instruction , (v/x-contains?-instruction typename componentname))
      (t/attach-instruction , (v/x-distinct-instruction typename))
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
      (t/attach-instruction , (v/x-pt-crossover-instruction typename))
      (t/attach-instruction , (v/x-shatter-instruction typename componentname))
      (t/attach-instruction , (v/x-remove-instruction typename componentname))
      (t/attach-instruction , (v/x-replace-instruction typename componentname))
      (t/attach-instruction , (v/x-replacefirst-instruction typename componentname))
      (t/attach-instruction , (v/x-rest-instruction typename))
      (t/attach-instruction , (v/x-set-instruction typename componentname))
      (t/attach-instruction , (v/x-take-instruction typename))
      (t/attach-instruction , (v/x-reverse-instruction typename))
      (t/attach-instruction , (v/x-vfilter-instruction typename))
      (t/attach-instruction , (v/x-vremove-instruction typename))
      (t/attach-instruction , (v/x-vsplit-instruction typename))
      )))


