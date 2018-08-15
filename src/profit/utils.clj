(ns profit.utils
  (:require [clojure.pprint :refer [pprint]]))

(defn pprint-str [x]
  (let [str (with-out-str (pprint x))]
    (subs str 0 (dec (count str)))))

(defn color-str [text]
  (let [start-color "\033[37;1;45m\033[1m"
        restore-color "\033[0m"]
    (str start-color text restore-color)))

(defn truncate-str [s max-length]
  (let [len (count s)]
    (if (<= len max-length)
      s
      (str (subs s 0 (- max-length 3)) "..."))))
