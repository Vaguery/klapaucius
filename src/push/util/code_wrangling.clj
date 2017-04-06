(ns push.util.code-wrangling
  (:require [push.util.numerics :as num]
            [clojure.zip        :as zip]
            ))


(defn branch?
  "predicate for walking nested code; returns `true` if the item can contain children we want to traverse, even if it doesn't have children now. This counts strings only as single items, not collections of chars."
  [node]
  (cond
    (string? node) false
    (coll? node) true
    (record? node) true
    :else false
    ))


(defn children
  "Returns a `seq` on a node (without checking for type). Used for traversing code objects with `count-collection-points`."
  [node]
  (seq node))


(defn count-collection-points
  "Takes a nested list (or any other nested collection or item) and counts the total number of collections and items in those collections. Literal 'nil' is 1; an empty list '() or #{} or {} is 1; each hash-map is itself 1 and each key value pair it holds is another 3 (read as a tuple, plus its two items). A vector is 1+count, a matrix is 1 + count(rows) + count(items), and so on. COUNT ALL THE THINGS. Except that strings count as single items."
  [item]
  (loop [loc (zip/zipper branch? children (fn [_ c] c) item)
         counter 0]
    (if (zip/end? loc)
      counter
      (recur (zip/next loc)
             (inc counter)))))



(defn count-code-points
  "Takes a nested list and counts the total number of seqs and non-seq items in those collections. Literal 'nil' is 1; an empty list '() or #{} or {} is 1. In other words, it only counts lists and things inside lists, not vectors, maps, or other kinds of collection (and is thus different from `count-collection-points`)."
  [item]
  (loop [loc (zip/seq-zip item)
         counter 0]
    (if (zip/end? loc)
      counter
      (recur  (zip/next loc)
              (if (nil? (zip/node loc))
                counter
                (inc counter))))))




(defn contains-anywhere?
  "Takes an item that is probably a nested collection, and returns true if the second argument appears 'in' it: are they equal? does the first contain the 2nd? does any of the items in the first contain the second? and so on recursively. Does not check sub-sequences for matches; it will look for a string as a whole, a vector only as a whole; but it will find an item as a key or value in a map."
  [item target & found]
  (cond
    (= item target) true
    (coll? item)
      (reduce #(or %1 (contains-anywhere? %2 target found)) false item)
    :else false
    ))



(defn find-in-tree
  "Utility function that determines all the places where a target node or sub-tree exists within another tree. Each location is returned in a vector, which may end up being empty."
  [tree target]
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
    (if (>= counter idx)
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
  "Takes three Push :code items, and traverses the first argument in a depth-first order, replacing every occurrence of the second arg (if any) with the third. The replacement will ONLY occur in code blocks; it will not replace elements of vectors that appear in those code blocks."
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
          (let [this-thing (zip/node loc)]
          (if (and (= this-thing old-code) (= (type this-thing) (type old-code)))
            (zip/next (zip/replace loc placeholder))
            (zip/next loc))))))))



(defn replace-nth-in-code
  "Takes two Push :code items and a number, and replaces the node (counted in depth-first order) of the first code with the second code item."
  [code1 code2 idx]
  (loop [loc (zip/seq-zip code1)
         counter 0]
    (if (>= counter idx)
        (zip/root (zip/replace loc code2))
      (recur (zip/next loc)
             (inc counter)))))



(defn safe-mod
  "Takes two integers, and returns 0 if the second is 0, otherwise (mod A B). Handles infinite arguments. Returns 0 if either argument is NaN."
  [a b]
  (cond
    (Double/isNaN a) 0
    (Double/isNaN b) 0
    (zero? b) 0
    (num/infinite? b) 0
    (num/infinite? a)
      (if (pos? (* a b)) num/∞ num/-∞)
    :else (mod a b)))


(defn list!
  "Forces the collection to become a list (in the same order)."
  [collection]
  (into '() (reverse collection)))


(defn delete-nth
  "Removes an indexed item from a seq; raises an Exception if the seq
  is empty."
  [coll idx]
  {:pre  [(seq coll)
          (not (neg? idx))
          (< idx (count coll))]}
  (list! (concat (take idx coll) (drop 1 (drop idx coll)))))


(defn insert-as-nth
  "Inserts the item so it is in the indicated position of the result. Note bounds of possible range are [0,length] (it can be placed last)."
  [coll item idx]
  {:pre  [(seq? coll)
          (not (neg? idx))
          (<= idx (count coll))]}
  (list! (concat (take idx coll) (list item) (drop idx coll))))
