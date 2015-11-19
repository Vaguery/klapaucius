(ns push.instructions.modules.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.instructions.modules.environment])
  )


(fact "classic-environment-module has :name ':environment'"
  (:name classic-environment-module) => :environment)


(fact "classic-environment-module has the expected :attributes"
  (:attributes classic-environment-module) =>
    (contains #{:complex :base}))

