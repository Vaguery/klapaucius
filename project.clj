(defproject push "0.0.1-SNAPSHOT"
  :description "Push language interpreter"
  :dependencies [[org.clojure/clojure            "1.7.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :plugins      [[lein-gorilla                   "0.3.5"]]
  :profiles {:dev {:dependencies [[midje "1.8.2"]]}})
  
  