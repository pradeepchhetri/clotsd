(ns clotsd.rest
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(defrecord OpenTSDBEndpoint
  [uri version])

(def ^{:dynamic true} *endpoint* (OpenTSDBEndpoint. (or (System/getenv "OPENTSDB_URL")
                                                        "http://localhost:4242") ""))

(def ^:const throw-exceptions false)

(def ^{:const true} slash "/")
(def ^{:const true} encoding "UTF-8")

(defn get
  ([^String uri]
   (io! (json/decode (:body (http/get uri {:accept :json
                                           :throw-exceptions throw-exceptions})) true)))
  ([^String uri &{:as options}]
   (io! (json/decode (:body (http/get uri (merge options {:accept :json
                                                          :throw-exceptions throw-exceptions}))) true))))

(defn post
  [^String uri &{:keys [body] :as options}]
  (io! (json/decode (:body (http/post uri (merge options {:accept :json
                                                          :body (json/encode body)}))) true)))

(defn post-string
  [^String uri &{:keys [body] :as options}]
  (io! (json/decode (:body (http/post uri (merge options {:accept :json
                                                          :body body}))) true)))

(defn delete
  ([^String uri]
   (io! (json/decode (:body (http/delete uri {:accept :json :throw-exceptions throw-exceptions})) true)))
  ([^String uri &{:keys [body] :as options}]
   (io! (json/decode (:body (http/delete uri (merge options {:accept :json
                                                             :body (json/encode body)
                                                             :throw-exceptions throw-exceptions}))) true))))

(defn put
  [^String uri &{:keys [body] :as options}]
  (io! (json/decode (:body (http/put uri (merge options {:accept :json
                                                         :body (json/encode body)
                                                         :throw-exceptions throw-exceptions}))) true)))

(defn url-with-path
  [& segments]
  (str (:uri *endpoint*) slash (join slash segments)))

(defn push-url
  []
  (url-with-path "api" "put"))

(defn pull-url
  []
  (url-with-path "api" "query"))

(defn search-url
  [^String type]
  (url-with-path "api" "search" type))

(def suggest-url
  []
  (url-with-path "api" "suggest"))

(def tree-url
  ([]
   (url-with-path "api" "tree"))
  ([^String op]
   (url-with-path "api" "tree" op)))


