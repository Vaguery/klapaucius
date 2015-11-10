(ns push.util.code-wrangling)


(defn count-points
  "Takes a nested list (or any other nested collection or item) and counts the total
  number of collections and items in those collections. Literal 'nil' is 1;
  an empty list '() or #{} or {} is 1; each hash-map is itself 1 and each key
  value pair it holds is another 3 (read as a tuple, plus its two items).
  A vector is 1+count, a matrix is 1 + count(rows) + count(items),
  and so on. COUNT ALL THE THINGS."
  [item & {:keys [counter] :or {counter 0}}]
  (cond
    (map? item)
      (reduce #(+ %1 (count-points %2)) (inc counter) (vec item))
    (coll? item)
      (reduce #(+ %1 (count-points %2)) (inc counter) item)
    :else
      (inc counter)))


(defn contains-anywhere?
  "Takes an item that is probably a nested collection, and returns true if the
  second argument appears 'in' it: are they equal? does the first contain the 2nd?
  does any of the items in the first contain the second? and so on recursively.
  Does not check sub-sequences for matches; it will look for a string as a whole,
  a vector only as a whole; but it will find an item as a key or value in a map."
  [item target & found]
  (cond 
    (= item target) true
    (coll? item)
      (reduce #(or %1 (contains-anywhere? %2 target found)) false item)
    :else false
    ))