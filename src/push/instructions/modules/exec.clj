(ns push.instructions.modules.exec
  (:require [push.instructions.core :as core])
  (:require [push.types.core :as t])
  (:require [push.instructions.dsl :as d])
  )


;; exec-specific

; exec_do*while  ;; ???
; exec_fromzipnode
; exec_fromziproot
; exec_fromzipchildren
; exec_fromziplefts
; exec_fromziprights


(def exec-do*count
  (core/build-instruction
    exec-do*count
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :integer :as :counter)
    (d/calculate [:counter] #(zero? %1) :as :done?)
    (d/calculate [:counter] #(+ %1 (compare 0 %1)) :as :next)
    (d/push-onto :integer :next)
    (d/calculate
      [:do-this :counter :next :done?] 
      #(if %4
           %1
           (list %1 (list %3 :exec-do*count %1))) :as :continuation)
    (d/push-onto :exec :continuation)))


(def exec-do*range
  (core/build-instruction
    exec-do*range
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :integer :as :end)
    (d/consume-top-of :integer :as :start)
    (d/calculate [:start :end] #(= %1 %2) :as :done?)
    (d/calculate [:start :end] #(+ %1 (compare %2 %1)) :as :next)
    (d/push-onto :integer :next)
    (d/calculate
      [:do-this :start :end :next :done?] 
      #(if %5
           %1
           (list %1 (list %4 %3 :exec-do*range %1))) :as :continuation)
    (d/push-onto :exec :continuation)))



(def exec-do*times
  (core/build-instruction
    exec-do*times
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :integer :as :counter)
    (d/calculate [:counter] #(zero? %1) :as :done?)
    (d/calculate [:counter] #(+ %1 (compare 0 %1)) :as :next)
    (d/calculate
      [:do-this :counter :next :done?] 
      #(if %4
           %1
           (list %1 (list %3 :exec-do*times %1))) :as :continuation)
    (d/push-onto :exec :continuation)))



(def exec-k 
  (t/simple-2-in-1-out-instruction :exec "k" (fn [a b] b)))


(def exec-if
  (core/build-instruction
    exec-if
    :tags #{:complex :base}
    (d/consume-top-of :boolean :as :decider)
    (d/consume-top-of :exec :as :option1)
    (d/consume-top-of :exec :as :option2)
    (d/calculate [:decider :option1 :option2] #(if %1 %2 %3) :as :result)
    (d/push-onto :exec :result)))


(def exec-noop
  (core/build-instruction
    exec-noop
    :tags #{:complex :base}))


(def exec-s
  (core/build-instruction
    exec-s
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :a)
    (d/consume-top-of :exec :as :b)
    (d/consume-top-of :exec :as :c)
    (d/calculate [:b :c] #(list %1 %2) :as :bc)
    (d/push-onto :exec :bc)
    (d/push-onto :exec :c)
    (d/push-onto :exec :a)))


(def exec-when
  (core/build-instruction
    exec-when
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :boolean :as :really?)
    (d/calculate [:do-this :really?] #(if %2 %1 '()) :as :continuation)
    (d/push-onto :exec :continuation)))


(def exec-while
  (core/build-instruction
    exec-while
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :do-this)
    (d/consume-top-of :boolean :as :again?)
    (d/calculate [:do-this :again?] #(if %2 (list %1 :exec-while %1) '()) :as :continuation)
    (d/push-onto :exec :continuation)))


(def exec-y
  (core/build-instruction
    exec-y
    :tags #{:complex :base}
    (d/consume-top-of :exec :as :arg)
    (d/calculate [:arg] #(list :exec-y %1) :as :result)
    (d/push-onto :exec :result)
    (d/push-onto :exec :arg)))


(def classic-exec-module
  ( ->  (t/make-module  :exec
                        :attributes #{:complex :base})
        t/make-visible 
        t/make-equatable
        t/make-movable
        (t/attach-instruction , exec-do*count)
        (t/attach-instruction , exec-do*range)
        (t/attach-instruction , exec-do*times)
        (t/attach-instruction , exec-k)
        (t/attach-instruction , exec-noop)
        (t/attach-instruction , exec-s)
        (t/attach-instruction , exec-if)
        (t/attach-instruction , exec-when)
        (t/attach-instruction , exec-while)
        (t/attach-instruction , exec-y)
        ))

