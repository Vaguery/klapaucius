(defproject org.clojars.vaguery/push-in-clojure "0.1.0-SNAPSHOT"
  :description "Push language interpreter"
  :url "https://github.com/Vaguery/push-in-clojure"
  :dependencies [[org.clojure/clojure            "1.7.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [org.apfloat/apfloat            "1.8.2"]]
  :plugins      [[lein-gorilla                   "0.3.5"]]
  :profiles {:dev {:dependencies [[midje "1.8.2"]]}})
  
  