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

;;;

;;; defnode introduces a computation node. Arguments are
;;; - a name
;;; - a collection of other nodes or topics which are listened for
;;; - a computation, which is run whenever there is a new message
;;;   from one of the nodes we're listening to
;;;
;;; whenever the result of the computation is non-nil, it is sent to
;;; the message store as a new message.  Not convinced that "non-nil"
;;; is the best way to do this, but haven't thought of a better one yet
;;;
;;; within the computation body we can look up events in the message store.
;;; we would like, probably, to blow up if the computation requests
;;; events on topics which it's not subscribed to, otherwise we can't
;;; be sure that it runs as often as it needs to.


(defnode drop-outliers ["sensors/some-input"]
  (let [reading (latest "sensors/some-input")]
    (if (< 10 reading 40)
      {:topic "node/some-input/clamped" :payload reading}
      nil)))


(defnode front-door-security-light ["sensors/porch/pir"]
  ;; turn on iff the smoothed pir was over threshold for > 10 seconds
  ;; in the last minute
  (let [threshold 127 ; dunno, this is a made up number
        readings (smooth (recent "sensors/porch/pir" 60) 5)]
    ;; this always 
    (if (> (sum-time readings #(> % threshold)) 10) 255 0)))






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
