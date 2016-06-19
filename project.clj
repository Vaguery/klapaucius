(defproject klapaucius "0.1.19-SNAPSHOT"
  :description "Push language interpreter"
  :url "https://github.com/Vaguery/klapaucius"
  :dependencies [[org.clojure/clojure            "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.apfloat/apfloat            "1.8.2"]
                 [marick/structural-typing       "2.0.1"]
                 [com.climate/claypoole          "1.1.2"]
                 [dire                           "0.5.4"]]
  :plugins      [[lein-gorilla                   "0.3.5"]]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :profiles {:dev {:dependencies [[midje "1.8.3"]]}})
  