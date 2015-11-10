(ns push.util.code-wrangling)


(defn count-points
  "Takes a nested list (or any other nested collection) and counts the total
  number of collections and items in those collections. Literal 'nil' is 1;
  an empty list '() or #{} or {} is 1; each hash-map pair is 3 (read as a tuple,
  plus its two items). A vector is 1+count, a matrix is 1 + count(rows) + count(items),
  and so on. COUNT ALL THE THINGS."
  [item & {:keys [counter] :or {counter 0}}]
  (cond
    (map? item)
      (reduce #(+ %1 (count-points %2)) (inc counter) (vec item))
    (coll? item)
      (reduce #(+ %1 (count-points %2)) (inc counter) item)
    :else
      (inc counter)))