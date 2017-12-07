(defproject klapaucius "0.1.26"
  :description "Push language interpreter"
  :url "https://github.com/Vaguery/klapaucius"
  :dependencies [[org.clojure/clojure            "1.8.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [com.climate/claypoole          "1.1.4"]
                 [inflections                   "0.13.0"]
                 [dire                           "0.5.4"]
                 [criterium                      "0.4.4"]
                 ]
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]
                        ]
  :profiles {:dev {:dependencies [[midje "1.9.0"]]
                   :plugins      [[lein-midje "3.2.1"]
                                  [lein-ancient "0.6.14"]
                                  ]}}
  :hiera  {:path "target/ns-hierarchy.png"
           :vertical false
           :show-external true
           :cluster-depth 2
           :trim-ns-prefix true
           :ignore-ns #{}}
           )
