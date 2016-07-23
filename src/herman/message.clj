(ns herman.message
  (:require [clojure.tools.logging :as log]
            [herman.config :as cfg]
            [herman.api.wordpress :as wordpress]
            [herman.api.telegram :as telegram]
            [herman.api.puppy :as puppy]
            [datomic.api :as d]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [herman.datomic.functions :as f]))


(defn help-message []
  (str "Привет я Герман, бот Большой Деревни\n"
       "Я умею:\n"
       "/start - начать общение \n"
       "/stop  - закончить общение \n"
       "/subscribe - подписаться на рассылку новостей \n"
       "/unsubscribe - отписаться от рассылки новостей \n"
       "/tag - поиск по тегу\n"
       "Например /tag библиотека\n"
       "/search - поиск по слову или фразе\n"
       "Например /search библиотка ленина\n"
       "/report Сообщение - отправить сообщение разработчикам\n"
       "Например /report Привет!\n"
       "/last - последняя новость\n"
       "/lastX - X последних новостей, \n где X цифра от одного до 10. Например /last5 \n"))

(defn start [conn username chat-id]
  @(d/transact
    conn
    [[:constructPerson
      {:user/name username
       :user/id chat-id
       :user/active true
       :user/broadcast true
       :user/admin false}]])
  (telegram/send-message chat-id (str "Приятно познакомиться, " username
                                      "\n /help для списка того что я умею")))

(defn stop [conn username chat-id]
  @(d/transact
    conn
    [{:user/id chat-id ;; this finds the existing entity
      :db/id #db/id [:db.part/user]  ;; will be replaced by existing id
      :user/active false}])
  (telegram/send-message chat-id (str "Ой всё")))

(defn admin [conn chat-id password]
  (if (= password "7c2wctC5")
    (do @(d/transact
          conn
          [{:user/id chat-id ;; this finds the existing entity
            :db/id #db/id [:db.part/user]  ;; will be replaced by existing id
            :user/admin true}])
        (telegram/send-message chat-id (str "Теперь ты можешь рулить")))
    (telegram/send-message chat-id (str "Пароль неверный :("))))

(defn unsubscribe [conn chat-id]
  @(d/transact
    conn
    [{:user/id chat-id ;; this finds the existing entity
      :db/id #db/id [:db.part/user]  ;; will be replaced by existing id
      :user/broadcast false}])
  (telegram/send-message chat-id (str "Теперь ты отписан от новостей")))

(defn report [chat-id text]
  (let [admin-id (cfg/config :admin-id)]
    (telegram/send-message chat-id (str "Сообщение отправлено разработчикам. Спасибо!"))
    (telegram/send-message admin-id (str "Сообщение от: " chat-id "\n" text))))

(defn message-user [text]
  (let [raw-message (string/split text #"\s+")
        chat-id (second raw-message)
        message (drop 2 raw-message)]
    (telegram/send-message chat-id (string/join " " message))))

(defn subscribe [conn chat-id]
  @(d/transact
    conn
    [{:user/id chat-id ;; this finds the existing entity
      :db/id #db/id [:db.part/user]  ;; will be replaced by existing id
      :user/broadcast true}])
  (telegram/send-message chat-id (str "Теперь ты подписан на новости")))

(defn multiple-send [conn text chat-id]
  (if (f/admin? conn chat-id)
    (do
      (doseq [id (f/get-news-reader-ids conn)]
        (telegram/send-message id text))
      (telegram/send-message chat-id (str "Сообщение: " text " отправлено")))
    (telegram/send-message chat-id (str "Изивни, у тебя недостаточно маны для этого"))))

(defn search [chat-id word count]
  (dorun
   (map
    (partial telegram/send-message chat-id)
    (wordpress/get-search-posts word count))))

(defn tag [chat-id word count]
  (dorun
   (map
    (partial telegram/send-message chat-id)
    (wordpress/get-tag-posts (wordpress/make-translit-ru-en word) count))))

(defn route [conn message]
  (log/info (str "Message: " message))
  (let [chat-id (:chat-id message)
        text (:text message)
        username (:username message)]

    (condp = (first (string/split text #"\s+"))
      "/help"  (telegram/send-message chat-id (help-message))
      "/start" (start conn username chat-id)
      "/stop"  (stop conn username chat-id)
      "/ping"  (telegram/send-message chat-id "pong")
      "/admin" (admin conn chat-id (subs text 7))
      "/unsubscribe" (unsubscribe conn chat-id)
      "/subscribe" (subscribe conn chat-id)
      "/message" (message-user text)
      "/search" (search chat-id (string/join " "
                                             (rest (string/split text #"\s+"))) 5)
      "/tag"  (tag chat-id (string/join " "
                                        (rest (string/split text #"\s+"))) 5)
      "/report" (report chat-id
                        (string/join " "
                                     (rest (string/split text #"\s+"))))
      "/send" (multiple-send conn
                             (string/join " "
                                          (rest (string/split text #"\s+")))
                             chat-id)
      "/last"  (telegram/send-message chat-id (first (wordpress/get-last 1)))
      (->> (range 2 11)
           (map (fn [n] (str "/last" n)))
           (apply vector)
           (some #{text})) (dorun
                            (map
                             (partial telegram/send-message chat-id)
                             (wordpress/get-last (subs text 5))))

      (telegram/send-message chat-id "Бебебе"))))

