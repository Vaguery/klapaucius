(ns push.types.modules.environment_test
  (:use midje.sweet)
  (:use [push.util.test-helpers])
  (:use [push.types.module.environment])
  )


(fact "environment-module has :name ':environment'"
  (:name environment-module) => :environment)


(fact "environment-module has the expected :attributes"
  (:attributes environment-module) =>
    (contains #{:complex :base}))

