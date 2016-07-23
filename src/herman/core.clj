(ns herman.core
  (:gen-class)
  (:require [cider.nrepl :refer [cider-nrepl-handler]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.tools.logging :as log]
            [clojure.tools.nrepl.server :as nrepl]
            [herman.config :as cfg]
            [herman.api.telegram :as telegram]
            [herman.web.web-server :as web-server]
            [herman.web.web-service :as service]))

(defn -main
  [& args]
  (cfg/reload-config!)
  (println "Herman is here...")

  (let [wp-uri (cfg/config :wordpress-uri)
        tg-uri (str (cfg/config :telegram-uri) (cfg/config :telegram-token))
        hook-uri (cfg/config :webhook-uri)]

    (log/info (str "Wordpress URI: " wp-uri))
    (log/info (str "Telegram URI: " tg-uri))
    (log/info (str "Webhook URI: " hook-uri))
    (log/info "--------------")

    (telegram/set-webhook hook-uri))
  (web-server/start))


