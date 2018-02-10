(defproject klapaucius "0.1.29"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2015
            :key "mit"}
  :description "Push language interpreter"
  :url "https://github.com/Vaguery/klapaucius"
  :dependencies [[org.clojure/clojure            "1.9.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [com.climate/claypoole          "1.1.4"]
                 [inflections                   "0.13.0"]
                 [dire                           "0.5.4"]
                 [criterium                      "0.4.4"]
                 ]
  :deploy-repositories [["releases"
                          :clojars
                          {:url #"https://clojars.org/klapaucius" :creds :gpg}]
                        ["snapshots"
                          :clojars
                          {:url #"https://clojars.org/klapaucius" :creds :gpg}]
                          ]
  :profiles {:dev {:dependencies [[midje "1.9.1"]]
                   :plugins      [[lein-midje "3.2.1"]
                                  [lein-ancient "0.6.15"]
                                  ]}})
