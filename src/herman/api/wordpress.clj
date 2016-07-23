(ns herman.api.wordpress
  (:require  [herman.config :as cfg]
             [clj-http.client :as client]
             [clojure.data.json :as json]
             [clojure.tools.logging :as log]))

(defn get-last [count]
  (let [uri (str (cfg/config :wordpress-uri) "/get_recent_posts?include=url&count=" count)
        response (client/get uri {:insecure :true :as :json})]
    (log/info "WP response: " response)
    (doall (map :url  (-> response
                          :body
                          :posts )))))

(defn get-tag-posts [tag count]
  (let [uri (str (cfg/config :wordpress-uri) "/get_tag_posts?slug=" tag "&include=url&count=" count)
        response (client/get uri {:insecure :true :as :json})]
    (log/info "WP response: " response)
    (doall (map :url  (-> response
                          :body
                          :posts )))))

(defn get-search-posts [search count]
  (let [uri (str (cfg/config :wordpress-uri)
                 "/get_search_results?search="
                 search
                 "&include=url&count="
                 count)
        response (client/get uri {:insecure :true :as :json})]
    (log/info "WP response: " response)
    (doall (map :url  (-> response
                          :body
                          :posts )))))

(def translit-table-ru-en
  {\a "a"
   \b "b"
   \c "c"
   \d "d"
   \e "e"
   \f "f"
   \g "g"
   \h "h"
   \i "i"
   \j "j"
   \k "k"
   \l "l"
   \m "m"
   \n "n"
   \o "o"
   \p "p"
   \q "q"
   \r "r"
   \s "s"
   \t "t"
   \u "u"
   \v "v"
   \w "w"
   \x "x"
   \y "y"
   \z "z"

   \1  "1"
   \2  "2"
   \3  "3"
   \4  "4"
   \5  "5"
   \6  "6"
   \7  "7"
   \8  "8"
   \9  "9"
   \0  "0"

   \а  "a"
   \б  "b"
   \в  "v"
   \г  "g"
   \д  "d"
   \е  "e"
   \ё  "e"
   \ж  "zh"
   \з  "z"
   \и  "i"
   \й  "j"
   \к  "k"
   \л  "l"
   \м  "m"
   \н  "n"
   \о  "o"
   \п  "p"
   \р  "r"
   \с  "s"
   \т  "t"
   \у  "u"
   \ф  "f"
   \х  "kh"
   \ц  "c"
   \ч  "ch"
   \ш  "sh"
   \щ  "shh"
   \ъ  ""
   \ы  "y"
   \ь  ""
   \э  "e"
   \ю  "yu"
   \я  "ya"
   \space "-"}
  )

(defn make-translit [table s]
  (->> s
       (map #(get table % " "))
       (apply str)))

(defn make-translit-ru-en [s]
  (make-translit translit-table-ru-en s))

;;(w/get-tag-posts (w/make-translit-ru-en "библиотека") 5)
