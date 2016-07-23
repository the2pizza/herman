(ns herman.web.web-service
  (:require [aleph.http :as http]
            [clojure.tools.logging :as log]
            [compojure.core :as compojure :refer [GET POST]]
            [compojure.route :as route]
            [clojure.walk :as walk]
            [plumbing.core :refer :all]
            [ring.middleware.defaults :as defaults]
            [ring.util.response :as response]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [herman.message :as m]
            [herman.datomic.functions :as f]
            [datomic.api :as d]
            [herman.config :as cfg]))



(defn connect-db []
  (let [db-uri (cfg/config :datomic-uri)]
    (d/connect "datomic:dev://bigvill.ru:4334/bigvill")))


(def conn (connect-db))

(f/transact-all conn (io/resource "schema/schema.edn"))
(f/transact-all conn (io/resource "datomic/clojure-functions.edn"))

(def db (d/db conn))

(defn extract-message [message]
  (let [chat-id (-> message
                    walk/keywordize-keys
                    :message
                    :chat
                    :id)
        username (-> message
                     walk/keywordize-keys
                     :message
                     :chat
                     :first_name)
        text  (-> message
                  walk/keywordize-keys
                  :message
                  :text)]
    {:username username
     :chat-id chat-id
     :text text }))

(defn handle-web-hook [message]
  (let [msg (json/read-str (slurp (io/input-stream message)))]
    (log/info "Webhook recieved message: " (walk/keywordize-keys msg))
    (m/route conn (extract-message msg))))

(defn telegram-handler [request]
  (try
    (-> request
        :body
        handle-web-hook)
    (-> (response/response "Success")
        (response/content-type "text/plain"))
    (catch Exception e
      (log/error e "Caught exception " (.getMessage e))
      (log/error "On request" request)
      (-> (response/response "Invalid request")
          (response/content-type "text/plain")))))

(defn root-page-handler
  [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body (str "Simple Telegram WebHook") })

(def handler
  (-> (compojure/routes
       (GET "/telegram" request (root-page-handler request))
       (POST "/telegram/api" request (telegram-handler request))
       (route/not-found "No such page"))
      (defaults/wrap-defaults (assoc-in defaults/site-defaults [:security :anti-forgery] false))))
