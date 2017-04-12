(ns util.trace
  (:require [push.core :as push]
            [push.interpreter.core :as i]
            ))


(defn print-item
  [item]
  (cond
    (char? item)
      (format "%04x" (int item))
    (seq? item)
      (pr-str (doall item))
    :else
      (pr-str item)))


(defn print-one-stack
  [keyword items]
  (reduce
    (fn [printout item] (str printout "  - " (print-item item) "\n"))
    (str "" (name keyword) ":\n")
    items))


(defn print-whole-map
  [the-map]
  (reduce-kv
    (fn [printout keyword items]
      (str printout (print-one-stack keyword items)))
      ""
    (into (sorted-map) the-map)
    ))


(defn yaml-from-interpreter-stacks
  [interpreter]
  (let [s (:stacks interpreter)
        b (:bindings interpreter)]
    (str (print-whole-map s) (print-whole-map b))
    ))
