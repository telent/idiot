(ns idiot.core
  (:require [clojurewerkz.machine-head.client :as mh]
            [java-time :as jt])
  (:gen-class))

;;; make a (hacky, low-budget) database in which we can store
;;; incoming messages

(def message-store-key
  (juxt :topic :ts #(java.lang.System/identityHashCode %)))

(defn ->comparator [keyfn]
  (fn [x y] (compare (keyfn x) (keyfn y))))

(def the-message-store
  (atom (sorted-set-by (->comparator message-store-key))))

(defn store-message [m]
  (swap! the-message-store conj m))

;;; search the store for messages on a given topic
(defn messages-for-topic [topic]
  (subseq @messages  >= {:topic topic} < {:topic (str topic "~")}))

;;; hook up an mqtt listener to push messages into the store
;;; and activate processing nodes that are interested in them

(def mqtt (mh/connect #_"tcp://loaclhost.lan:1883"
                      "tcp://localhost:1883"
                      {:username "sensors"
                       :password "123456"}))

(defn receive-mqtt [^String topic a ^bytes payload]
  (let [p (String. payload "UTF-8")
        message {:ts (jt/instant)
                 :topic topic
                 :message payload}]
    ;; note that no transformation of the payload is
    ;; attempted here, we just store bytes.  Without knowing
    ;; where it came from we don't know if its a string or a
    ;; integer or ...
    (println a)
    (store-message message)
    #_(ping-listeners topic)))
    
(mh/subscribe mqtt {"sensors/#" 0} #'receive-mqtt)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
