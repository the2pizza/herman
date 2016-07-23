(ns herman.datomic.functions
  (:import datomic.Util java.util.Random)
  (:require [datomic.api :as d]
            [herman.config :as cfg]
            [clojure.java.io :as io]))



(defn read-all
  "Read all forms in f, where f is any resource that can
   be opened by io/reader"
  [f]
  (Util/readAll (io/reader f)))

(defn transact-all
  "Load and run all transactions from f, where f is any
   resource that can be opened by io/reader."
  [conn f]
  (loop [n 0
         [tx & more] (read-all f)]
    (if tx
      (recur (+ n (count (:tx-data  @(d/transact conn tx))))
             more)
      {:datoms n})))

(defn get-admins-ids [conn]
  (let [db (d/db conn)
        admins (d/q '[:find ?e
                      :where [?e :user/admin true]]
                    db)]

    (->> (apply list admins)
         (map (partial apply long))
         (map (partial d/entity db))
         (map :user/id))))

(defn get-news-reader-ids [conn]
  (let [db (d/db conn)
        news-readers (d/q '[:find ?e
                            :where [?e :user/broadcast true]
                            [?e :user/active true]]
                          db)]

    (->> (apply list news-readers)
         (map (partial apply long))
         (map (partial d/entity db))
         (map :user/id))))

(defn get-active-ids [conn]
  (let [db (d/db conn)
        active (d/q '[:find ?a
                      :where [?a :user/active true]]
                    db)]

    (->> (apply list active)
         (map (partial apply long))
         (map (partial d/entity db))
         (map :user/id))))

(defn admin? [conn chat-id]
  (let [db (d/db conn)
        r (d/q '[:find ?u
                 :in $ ?chat-id
                 :where [?u :user/id ?chat-id]] db, chat-id)]
    (->> (apply list r)
         (map (partial apply long))
         (map (partial d/entity db))
         (map :user/admin))))


