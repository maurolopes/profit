(ns profit.core
  (:require [profit.utils :refer [color-str
                                  pprint-str
                                  truncate-str]]))

(def ^:dynamic *max-length* 60)

(defmacro print-tag-form [tag form]
  `(let [res# ~form]
     (println (color-str '~tag)
              (pprint-str res#)
              (color-str "<="))
     res#))

(defmacro print-form [form]
  `(let [res# ~form
         form-expr# '~form
         pfx# (truncate-str (str form-expr#) *max-length*)]
     (println (str (color-str (str (ns-name ~*ns*))) " " (color-str pfx#)))
     (println (pprint-str res#)
              (color-str "<="))
     res#))

(defn print-form-fn [form]
  `(print-form ~form))

(defn data-reader-fn [tag form]
  `(print-tag-form ~tag ~form))
