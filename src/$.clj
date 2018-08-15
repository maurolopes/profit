(ns $
  (:require [profit.core :refer [print-tag-form]])
  (:refer-clojure :exclude [-> ->>]))

(defmacro $ [form]
  `(print-tag-form "$" ~form))

(defmacro ->
  ([form] `(print-tag-form "->" ~form))
  ([form tag] `(print-tag-form ~tag ~form)))

(defmacro ->>
  ([form] `(print-tag-form "->>" ~form))
  ([tag form] `(print-tag-form ~tag ~form)))

(def print-form-fn profit.core/print-form-fn)

(defn set-generic-tags! []
  (set! clojure.core/*default-data-reader-fn* #'profit.core/data-reader-fn))

(defn unset-generic-tags! []
  (set! clojure.core/*default-data-reader-fn* nil))
