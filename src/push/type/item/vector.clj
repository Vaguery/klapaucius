(ns push.type.item.vector
  (:use     [push.instructions.core :only [build-instruction]]
            [push.type.core :only [attach-instruction, make-type]]
            [push.instructions.dsl]
            [push.type.item.vectorized]
            )
  (:require [push.instructions.aspects :as aspects]
            [push.util.code-wrangling :as fix]
            ))



(def vector-refilter
  (build-instruction
    vector-refilter
    "`:vector-refilter` pops the top `:vector` value, and sends it to the `:exec` stack"
    :tags #{:conversion :vector}

    (consume-top-of :vector :as :arg)
    (push-onto :exec :arg)
    ))



(def vector-refilterall
  (build-instruction
    vector-refilterall
    "`:vector-refilterall` puts the entire `:vector` stack on top of the `:exec` stack"
    :tags #{:conversion :vector}

    (consume-stack :vector :as :stack)
    (consume-stack :exec :as :old-exec)
    (calculate [:stack :old-exec]
      #(fix/list! (concat %1 %2)) :as :new-exec)
    (replace-stack :exec :new-exec)
    ))



(def standard-vector-type
  "builds the basic `:vector` type, which can hold arbitrary and mixed contents"
  (let [typename      :vector
        componentname :code]
  (-> (make-type  :vector
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
      (attach-instruction , vector-refilter)
      (attach-instruction , vector-refilterall)
      (attach-instruction , (x-build-instruction typename componentname))
      (attach-instruction , (x-butlast-instruction typename))
      (attach-instruction , (x-byexample-instruction typename componentname))
      (attach-instruction , (x-concat-instruction typename))
      (attach-instruction , (x-conj-instruction typename componentname))
      (attach-instruction , (x-contains?-instruction typename componentname))
      (attach-instruction , (x-cyclevector-instruction typename componentname))
      (attach-instruction , (x-distinct-instruction typename))
      (attach-instruction , (x-do*each-instruction typename))
      (attach-instruction , (x-emptyitem?-instruction typename))
      (attach-instruction , (x-fillvector-instruction typename componentname))
      (attach-instruction , (x-first-instruction typename componentname))
      (attach-instruction , (x-indexof-instruction typename componentname))
      (attach-instruction , (x-items-instruction typename))
      (attach-instruction , (x-last-instruction typename componentname))
      (attach-instruction , (x-length-instruction typename))
      (attach-instruction , (x-new-instruction typename))
      (attach-instruction , (x-nth-instruction typename componentname))
      (attach-instruction , (x-occurrencesof-instruction typename componentname))
      (attach-instruction , (x-portion-instruction typename))
      (attach-instruction , (x-pt-crossover-instruction typename))
      (attach-instruction , (x-shatter-instruction typename componentname))
      (attach-instruction , (x-remove-instruction typename componentname))
      (attach-instruction , (x-replace-instruction typename componentname))
      (attach-instruction , (x-replacefirst-instruction typename componentname))
      (attach-instruction , (x-rest-instruction typename))
      (attach-instruction , (x-set-instruction typename componentname))
      (attach-instruction , (x-take-instruction typename))
      (attach-instruction , (x-reverse-instruction typename))
      (attach-instruction , (x-vfilter-instruction typename))
      (attach-instruction , (x-vremove-instruction typename))
      (attach-instruction , (x-vsplit-instruction typename))
      )))
