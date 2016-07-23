(defproject herman "0.1.0-SNAPSHOT"
  :description "Telegram Bot"
  :url "https://github.com/bigvillru/herman"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.374"]
                 [com.datomic/datomic-pro "0.9.5350"
                              :exclusions [org.slf4j/slf4j-nop
                                           org.slf4j/slf4j-log4j12
                                           commons-codec
                                           joda-time
                                           org.apache.httpcomponents/httpclient]]
                 [ch.qos.logback/logback-classic "1.1.3"]]
  :main herman.core
  :aot [herman.core]
  :profiles
  {:app {:dependencies [[aleph "0.4.1-beta2"]
                        [org.clojure/tools.namespace "0.2.11"]
                        [cider/cider-nrepl "0.11.0-SNAPSHOT"]
                        [clj-http "2.0.1"]
                        [org.clojure/clojure "1.8.0"]
                        [org.clojure/core.async "0.2.374"]
                        [org.clojure/data.json "0.2.6"]
                        [org.clojure/tools.analyzer.jvm "0.6.9"]
                        [org.clojure/tools.cli "0.3.3"]
                        [org.clojure/tools.logging "0.3.1"]
                        [org.clojure/tools.nrepl "0.2.12"]
                        [prismatic/plumbing "0.5.2"]
                        [ring/ring-defaults "0.1.5"]
                        [compojure "1.4.0" :exclusions [ring/ring-core
                                                        commons-fileupload
                                                        clj-time
                                                        joda-time]]
                        [manifold "0.1.2"]]}
   :cljs {:dependencies [[cljs-http "0.1.39"]
                         [cljsjs/react "0.14.3-0"]
                         [cljsjs/react-dom "0.14.3-1"]
                         [cljsjs/react-dom-server "0.14.3-0"]
                         [com.cemerick/url "0.1.1"]
                         [org.clojure/clojurescript "1.7.228"]
                         [org.omcljs/om "1.0.0-alpha28"]
                         [prismatic/om-tools "0.4.0"]
                         [racehub/om-bootstrap "0.6.1"]
                         [sablono "0.5.3"]
                         [secretary "1.2.3"]]
            :plugins      [[lein-cljsbuild "1.1.2"]]}}
  :aliases {"app"        ["with-profile" "+app"]
            "cljs"       ["with-profile" "cljs"]
            "build-bot" ["cljs" "cljsbuild" "once" "prod"]}
  :cljsbuild {:builds
              [{:source-paths ["src-cljs"]
                :id           "prod"
                :compiler
                {:main           herman.web.dashboard
                 :asset-path     "/js/dashboard-dev"
                 :output-to      "resources/public/js/dashboard.js"
                 :output-dir     "resources/public/js/dashboard"
                 :compiler-stats true
                 :source-map     "resources/public/js/dashboard.js.map"
                 :optimizations  :advanced
                 :cache-analysis true
                 :parallel-build true
                 :pretty-print   false}}]}
  :jvm-opts ["-Dfile.encoding=UTF-8" "-Xmx2000M" "-server" "-Dlogback.configurationFile=logback-cogbot.xml"]
  :repositories {"my.datomic.com"
                {:url      "https://my.datomic.com/repo"
                 :username "s8tn1kv@gmail.com"
                 :password "6a227626-78f9-468d-984f-6aabd50ce694"}})
