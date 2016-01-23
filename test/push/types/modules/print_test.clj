(ns push.types.modules.print_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.modules.print])
  )



(fact "print-module has :name ':print'"
  (:name print-module) => :print)


(fact "print-module has the expected :attributes"
  (:attributes print-module) =>
    (contains #{:io}))


(fact "print-module knows how to print some literals"
  (keys (:instructions print-module)) =>
    (contains [:print-newline :print-space]))
