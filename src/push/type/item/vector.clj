(ns push.type.item.vector
  (:require [push.instructions.dsl     :as d]
            [push.instructions.core    :as i]
            [push.util.code-wrangling  :as u]
            [push.type.core            :as t]
            [push.type.item.vectorized :as v]
            [push.instructions.aspects :as aspects]
            ))

(def vector-refilter
  (i/build-instruction
    vector-refilter
    "`:vector-refilter` pops the top `:vector` value, and sends it to the `:exec` stack"

    (d/consume-top-of :vector :as :arg)
    (d/return-item :arg)
    ))


(def vector-refilterall
  (i/build-instruction
    vector-refilterall
    "`:vector-refilterall` puts the entire `:vector` stack on top of the `:exec` stack"

    (d/consume-stack :vector :as :old-stack)
    (d/calculate [:old-stack] #(u/list! (reverse %1)) :as :result)
    (d/return-item :result)
    ))



(def standard-vector-type
  "builds the basic `:vector` type, which can hold arbitrary and mixed contents"
  (let [typename      :vector
        componentname :code]
  (-> (t/make-type  :vector
                    :recognized-by vector?
                    :attributes #{:collection :vector})
      aspects/make-set-able
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
      (t/attach-instruction , (v/x-build-instruction typename componentname))
      (t/attach-instruction , (v/x-butlast-instruction typename))
      (t/attach-instruction , (v/x-byexample-instruction typename componentname))
      (t/attach-instruction , (v/x-concat-instruction typename))
      (t/attach-instruction , (v/x-conj-instruction typename componentname))
      (t/attach-instruction , (v/x-contains?-instruction typename componentname))
      (t/attach-instruction , (v/x-cyclevector-instruction typename componentname))
      (t/attach-instruction , (v/x-distinct-instruction typename))
      (t/attach-instruction , (v/x-do*each-instruction typename))
      (t/attach-instruction , (v/x-emptyitem?-instruction typename))
      (t/attach-instruction , (v/x-fillvector-instruction typename componentname))
      (t/attach-instruction , (v/x-first-instruction typename componentname))
      (t/attach-instruction , (v/x-indexof-instruction typename componentname))
      (t/attach-instruction , (v/x-items-instruction typename))
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
