(ns herman.config
  (:require [clojure.java.io :as io]))

(defn read-file [file]
  (if (and file (.exists (io/as-file file)))
    (-> file slurp read-string)
    {}))

(def ^:dynamic *config* (atom {}))

(defn reload-config! []
  (reset! *config* (read-file "config.edn")))

(defn config [key]
  (get @*config* key))

