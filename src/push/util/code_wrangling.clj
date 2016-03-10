(ns push.util.code-wrangling
  (:require [clojure.zip :as zip])
  )


(defn count-collection-points
  "Takes a nested list (or any other nested collection or item) and counts the total number of collections and items in those collections. Literal 'nil' is 1; an empty list '() or #{} or {} is 1; each hash-map is itself 1 and each key value pair it holds is another 3 (read as a tuple, plus its two items). A vector is 1+count, a matrix is 1 + count(rows) + count(items), and so on. COUNT ALL THE THINGS."
  [item & {:keys [counter] :or {counter 0}}]
  (cond
    (map? item)
      (reduce #(+ %1 (count-collection-points %2)) (inc counter) (vec item))
    (coll? item)
      (reduce #(+ %1 (count-collection-points %2)) (inc counter) item)
    :else
      (inc counter)))


(defn count-code-points
  "Takes a nested list and counts the total number of seqs and non-seq items in those collections. Literal 'nil' is 1; an empty list '() or #{} or {} is 1. In other words, it only counts lists and things inside lists, not vectors, maps, or other kinds of collection (and is thus different from `count-collection-points`)."
  [item & {:keys [counter] :or {counter 0}}]
  (cond
    (seq? item)
      (reduce #(+ %1 (count-code-points %2)) (inc counter) item)
    :else
      (inc counter)))


(defn contains-anywhere?
  "Takes an item that is probably a nested collection, and returns true if the second argument appears 'in' it: are they equal? does the first contain the 2nd? does any of the items in the first contain the second? and so on recursively. Does not check sub-sequences for matches; it will look for a string as a whole, a vector only as a whole; but it will find an item as a key or value in a map."
  [item target & found]
  (cond 
    (= item target) true
    (coll? item)
      (reduce #(or %1 (contains-anywhere? %2 target found)) false item)
    :else false
    ))


(defn find-in-tree [tree target]
  (loop [loc (zip/seq-zip (seq tree))
         collector []]
    (if (zip/end? loc)
      collector
      (recur (zip/next loc)                                   ;; next loc
             (if (= (zip/node loc) target)                    ;; next collector
                 (conj collector (zip/node (zip/up loc)))
                 collector)))))


(defn nth-code-point
  "`nth-code-point` takes a :code item (any clojure form) and an integer index, and traverses the code item as a tree (of nested lists and items) in a depth-first order, returning the indexed node."
  [code idx]
  (loop [loc (zip/seq-zip code)
         counter 0]
    (if (= counter idx)
      (zip/node loc)
      (recur (zip/next loc)                                   
             (inc counter)))))


(defn containers-in
  "Takes two items, and searches for a copy of the second item in the first. Returns a vector (filled in depth-first order) of all the _containers_ of that item. If the target is not found in the tree (or if they are identical) a vector containing an empty list will be returned. Does not work with vectors, apparently?"
  [item target]
  (cond
    (not (coll? item)) ['()]
    (= item target) ['()]
    (not (contains-anywhere? item target)) ['()]
    (some #(= target %) item) [item]
    :else
      (let [t (zip/seq-zip (seq item))]
        (find-in-tree t target))))


(defn replace-in-code
  "Takes three Push :code items, and traverses the first argument in a depth-first order, replacing every occurrence of the second arg (if any) with the third."
  [code old-code new-code]
  (let [placeholder (gensym)]
    (loop [loc (zip/seq-zip code)]
      (if (zip/end? loc)
        (loop [fixing (zip/seq-zip (zip/root loc))]
          (if (zip/end? fixing)
            (zip/root fixing)
            (recur
              (if (= (zip/node fixing) placeholder)
                (zip/next (zip/replace fixing new-code))
                (zip/next fixing)))))
        (recur
          (if (= (zip/node loc) old-code)
            (zip/next (zip/replace loc placeholder))
            (zip/next loc)))))))



  ; (w/postwalk-replace {old new} code)

(defn replace-nth-in-code
  "Takes two Push :code items and an integer, and replaces the node (counted in depth-first order) of the first code with the second code item."
  [code1 code2 idx]
  (loop [loc (zip/seq-zip code1)
         counter 0]
    (if (= counter idx)
        (zip/root (zip/replace loc code2))
      (recur (zip/next loc)                                   
             (inc counter)))))


(defn safe-mod
  "Takes two integers, and returns 0 if the second is 0, otherwise (mod A B)"
  [a b]
  (if (zero? b)
      0
      (mod a b)))