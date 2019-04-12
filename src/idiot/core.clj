(ns idiot.core
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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
