(ns herman.web.web-server
  (:require [aleph.http :as http]
            [clojure.tools.logging :as log]
            [herman.web.web-service :as service]
            [herman.config :as cfg]))

(def instance "Global var to hold service instance." nil)

(defn set-instance! [val]
  (alter-var-root #'instance (constantly val)))

(defn start []
  (let [port (cfg/config :port)]
    (set-instance! (http/start-server service/handler {:port port}))
    (log/info (str "Aleph Server created. Awaiting connections on port " port "."))))

(defn stop []
  (when instance
    (log/info "Aleph Server stopped.")
    (.close instance)
    (set-instance! nil)))
