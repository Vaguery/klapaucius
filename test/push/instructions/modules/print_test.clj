(ns push.instructions.modules.print_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.modules.print])
  )



(fact "classic-print-module has :name ':print'"
  (:name classic-print-module) => :print)


(fact "classic-print-module has the expected :attributes"
  (:attributes classic-print-module) =>
    (contains #{:io}))


(fact "classic-print-module knows how to print some literals"
  (keys (:instructions classic-print-module)) =>
    (contains [:print-newline :print-space]))
