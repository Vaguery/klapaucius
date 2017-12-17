(ns push.type.base.snapshot_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:require [push.core :as push])
  (:use [push.type.definitions.snapshot])
  (:use [push.type.item.snapshot])
  )

;; definitions and utilities


(fact "snapshot constructs one from an interpreter"
  (snapshot (push/interpreter :bindings {:x '(9)} :stacks {:foo '(33)})) =>
    (contains
      '{:bindings {:x ((9))},
       :config {:lenient? true, :max-collection-size 131072, :step-limit 0},
       :stacks {:boolean (), :booleans (), :char (), :chars (), :code (), :complex (), :complexes (), :error (), :exec (), :foo (33), :generator (), :interval (), :intervals(), :log (), :print (), :ref (), :refs (), :return (), :scalar (), :scalars (), :set (), :snapshot (), :string (), :strings (), :tagspace (), :unknown (), :vector ()}} )
    )


(fact "snapshot? recognizes Snapshot records"
  (snapshot? (snapshot (push/interpreter))) => true
  (snapshot? 812) => false
  )


;; snapshot-type


(fact "snapshot-type has :name ':snapshot'"
  (:name snapshot-type) => :snapshot)




(fact "snapshot-type has the expected :attributes"
  (:attributes snapshot-type) =>
    (contains #{:complex :base}))
