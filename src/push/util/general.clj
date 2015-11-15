(ns push.util.general)

(defn extract-keyword-argument
  "Helper function that helps parse arguments specified by keyword.
  Returns the specified keyword variable, if any; returns nil otherwise."
  [kwd args]
  (second (drop-while (complement #{kwd}) args)))


(defn extract-splat-argument
  "Helper function that helps parse arguments specified by keyword.
  Returns the rest of the arg list, after all keyworded arguments (and the
  values associated with them) have been stripped. Assumes they're all first,
  and that every one is a key-value pair."
  [args]
  (if (some keyword? args)
    (into '() (butlast (first (split-with (complement keyword?) (reverse args)))))
    args))      
