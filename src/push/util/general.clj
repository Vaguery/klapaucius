(ns push.util.general)

(defn extract-keyword-argument
  "Helper function (for use in macros) that parses argument lists specified by keyword.
  Returns the specified keyword variable, if any; returns nil otherwise."
  [kwd args]
  (second (drop-while (complement #{kwd}) args)))


(defn extract-docstring
  "Helper function (for use in macros) that extracts a string if it's the second
  item in a list; returns nil otherwise"
  [args]
  (some #(when (string? %) %) args))


(defn extract-splat-argument
  "Helper function that helps parse arguments specified by keyword.
  Returns the rest of the arg list, after all keyworded arguments (and the
  values associated with them) have been stripped. Assumes they're all first,
  and that every one is a key-value pair."
  [args]
  (let [stringless (if (string? (first args)) (rest args) args)]
    (if (some keyword? stringless)
      (into '() (butlast (first (split-with (complement keyword?) (reverse stringless)))))
      stringless)))
