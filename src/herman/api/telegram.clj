(ns herman.api.telegram
  (:require [clojure.tools.logging :as log]
            [herman.config :as cfg]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [herman.api.wordpress :as wordpress]))

(defn get-me []
  (let [tg-uri (str (cfg/config :telegram-uri) (cfg/config :telegram-token))]
    (log/info (str tg-uri "/getMe"))
    (log/info (client/get (str tg-uri "/getMe") {:accept :json}))))

(defn set-webhook  [link]
  (let [tg-uri (str (cfg/config :telegram-uri) (cfg/config :telegram-token))]
    (log/info (client/post (str tg-uri "/setWebhook")
                           {:body (json/write-str {"url" link })
                            :headers {"X-Api-Version" "2"}
                            :content-type :json
                            :socket-timeout 1000
                            :conn-timeout 1000
                            :accept :json}))))

(defn send-message [chat-id message]
  (let [tg-uri (str (cfg/config :telegram-uri) (cfg/config :telegram-token))]
    (log/info (client/post (str tg-uri "/sendMessage")
                           {:body (json/write-str {:text message
                                                   :chat_id chat-id})
                            :headers {"X-Api-Version" "2"}
                            :content-type :json
                            :socket-timeout 1000
                            :conn-timeout 1000
                            :accept :json}))))


