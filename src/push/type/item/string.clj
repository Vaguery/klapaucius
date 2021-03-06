(ns push.type.item.string
  (:require [push.instructions.dsl     :as d]
            [push.instructions.core    :as i]
            [clojure.string            :as s]
            [push.type.core            :as t]
            [push.instructions.aspects :as aspects]
            [clojure.edn               :as edn]
            [push.util.numerics        :as num]
            ))

(defn explosive-replacement?
  "takes three strings: `before`, `after` and `pattern`. Returns `true` if there are more copies of `pattern` in `after` than in `before`"
  [before after pattern]
  (< (count (re-seq (re-pattern pattern) before))
     (count (re-seq (re-pattern pattern) after))))

(def regex-char-esc-smap
  "cadged from http://stackoverflow.com/questions/11671898/escaping-brackets-in-clojure"
  (let [esc-chars (str "()*&^%$#!{}+[]|~.?\\")]
    (zipmap esc-chars
            (map #(str "\\" %) esc-chars))))

(defn str-to-pattern
  "takes a string, escapes characters that would disqualify it from being a pattern, and returns a pattern"
  [string]
  (->> string
       (replace regex-char-esc-smap)
       (s/join)
       re-pattern))


(defn simple-item-to-string-instruction
  "returns a standard arity-1 function, which moves the string representation of the top item from the named stack to the :string stack"
  [type]
  (let [stackname (keyword type)
        instruction-name (str (name stackname) "->string" )]
    (eval (list
      'push.instructions.core/build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top item from the `" stackname
           "` stack and converts it to a `:string` (using Clojure's `str` function)")

      `(d/consume-top-of ~stackname :as :arg)
      '(d/calculate [:arg] str :as :printed)
      '(d/return-item :printed)
      ))))



;; INSTRUCTIONS


(def boolean->string (simple-item-to-string-instruction :boolean))
(def char->string    (simple-item-to-string-instruction :char   ))
(def code->string    (simple-item-to-string-instruction :code   ))
(def exec->string    (simple-item-to-string-instruction :exec   ))
(def scalar->string  (simple-item-to-string-instruction :scalar ))


(defn valid-numbers-only
  "Given a collection of strings, this will parse each one with edn/read-string, ignoring errors, and return only those that parse as Clojure numeric values."
  [chunks]
  (filter
    number?
    (map
      #(try (edn/read-string %) (catch Exception e nil))
      chunks)
      ))


(def string->scalar
  (i/build-instruction
    string->scalar
    "`:string->scalar` pops the top `:string` item and attempts to convert it into a `:scalar` value. The rules (in order of precedence) are: (1) If the string is empty, there is no result (no `:scalar` is pushed). (2) If the string contains whitespace, then it is broken into individual space-delimited tokens, and each of those is processed as follows, in turn, until the _first_ successful token creates a `:scalar` value, using the Clojure EDN reader as its standard. If no token produces any readable numeric result, no result is pushed. Tokens that produce reader errors are ignored. The first successful numeric value is pushed."

    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] #(s/split %1 #"\s+") :as :chunks)
    (d/calculate [:chunks] valid-numbers-only :as :numbers)
    (d/calculate [:numbers] first :as :result)
    (d/return-item :result)
    ))


(def string->scalars
  (i/build-instruction
    string->scalars
    "`:string->scalar` pops the top `:string` item and attempts to convert it into a `:scalars` vector. The rules (in order of precedence) are: (1) If the string is empty, an empty vector is pushed to `:scalars`. (2) If the string contains whitespace and any other characters, then it is broken into individual space-delimited tokens, and each of those is processed using the Clojure EDN reader as its standard. If no token produces any readable numeric result, an empty vector is pushed. Tokens that produce reader errors are ignored and skipped. Each whitespace-delimited numeric item is returned, in order, as part of a single `:scalars` result."

    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] #(s/split %1 #"\s+") :as :chunks)
    (d/calculate [:chunks] valid-numbers-only :as :numbers)
    (d/calculate [:numbers] vec :as :result)
    (d/return-item :result)
    ))


(def exec-string-iterate
  (i/build-instruction
    exec-string-iterate
    "`:exec-string-iterate` pops the top `:exec` item and the `:string`, and pushes a continuation form to the `:exec` stack that is either:

    - when the string is empty: nil [nothing is pushed]
    - when there is 1 character: `'([head] [item])`
    - when there are more characters, say `tail` is the string lacking its first character, and `head` is the first character: `'([head] [item] [tail] :exec-string-iterate [item])`"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:s] first :as :head)
    (d/calculate [:s] #(s/join (rest %1)) :as :tail)
    (d/calculate [:fn :s :head :tail]
        #(cond (empty? %2) nil
               (empty? %4) (list %3 %1)
               :else (list %3 %1 %4 :exec-string-iterate %1)) :as :continuation)
    (d/return-item :continuation)
    ))



(def string-butlast (i/simple-1-in-1-out-instruction
  "`:string-butlast` returns the top `:string` item lacking its last character"
    :string "butlast" #(s/join (butlast %1))
    ))



(def string-concat
  (i/build-instruction
    string-concat
    "`:string-concat` pops the top two `:string` items, and pushes the result of concatenating the top item at the end of the second item. If the result would be longer than :max-collection-size, it is discarded and an `:error` is pushed instead."

    (d/consume-top-of :string :as :arg2)
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(s/join (list %1 (str %2))) :as :longer)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:longer :limit]
      #(when (< (count %1) %2) %1) :as :result)
    (d/calculate [:longer :limit]
      #(when-not (< (count %1) %2)
        ":string-concat produced oversized result") :as :message)
    (d/return-item :result)
    (d/record-an-error :from :message)
    ))



(def string-conjchar
  (i/build-instruction
    string-conjchar
    "`:string-conjchar` pops the top `:string` and the top `:char`, and pushes a new `:string` with the `:char` added at the end"

    (d/consume-top-of :char :as :c)
    (d/consume-top-of :string :as :s)
    (d/calculate [:s :c] #(s/join (list %1 (str %2))) :as :longer)
    (d/return-item :longer)
    ))



(def string-contains?
  (i/build-instruction
    string-contains?
    "`:string-contains?` pops two strings (call them `A` and `B`, respectively). It pushes `true` if `B` is a substring of `A`, or `false` otherwise"

    (d/consume-top-of :string :as :host)
    (d/consume-top-of :string :as :target)
    (d/calculate [:host :target]
      #(boolean (re-find (re-pattern (str-to-pattern %2)) %1)) :as :found?)
    (d/return-item :found?)
    ))



(def string-containschar?
  (i/build-instruction
    string-containschar?
    "`:string-containschar?` pops the top `:string` and `:char` values, and pushes `true` if the string contains the character, `false` otherwise"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(boolean (some #{%2} (vec %1))) :as :found?)
    (d/return-item :found?)
    ))



(def string-emptystring? (i/simple-1-in-predicate
  "`:string-emptystring? pushes `true` if the top `:string` has no characters"
  :string "emptystring?" empty?
  ))



(def string-first
  (i/build-instruction
    string-first
    "`:string-first` pops the top `:string` value, and pushes its first `:char`"

    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] first :as :c)
    (d/return-item :c)
    ))



(def string-indexofchar
  (i/build-instruction
    string-indexofchar
    "`:string-indexofchar` pops the top `:string` and the top `:char`, and pushes a `:scalar` which is the index of the first occurrence of `:char` in `:string`, or -1 if it's not found"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(.indexOf %1 (long %2)) :as :where)
    (d/return-item :where)
    ))



(def string-last
  (i/build-instruction
    string-last
    "`:string-last` pops the top `:string` item, and pushes its last `:char` (if it has one)"

    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] last :as :c)
    (d/return-item :c)
    ))



(def string-length
  (i/build-instruction
    string-length
    "`:string-length` pops the top `:string` and pushes its length (counting unicode-aware characters) to the `:scalar` stack"

    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] count :as :len)
    (d/return-item :len)
    ))



(def string-nth
  (i/build-instruction
    string-nth
    "`:string-last` pops the top `:string` item and an index value from the `:scalar` stack, and pushes the indexed `:char` (if it has one); if the index is out of range, it is reduced to an integer value in range via `(mod (ceil index) (count string))`"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s :idx] #(when (seq %1) (nth %1 %2)) :as :result)
    (d/return-item :result)
    ))



(def string-occurrencesofchar
  (i/build-instruction
    string-occurrencesofchar
    "`:string-occurrencesofchar` pops the top `:string` and `:char` values, and pushes to `:integer` the number of times the `:char` appears in the `:string`"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(get (frequencies %1) %2 0) :as :count)
    (d/return-item :count)
    ))



(def string-removechar
  (i/build-instruction
    string-removechar
    "`:string-removechar` pops the top `:string` and `:char` items, and pushes the string with _every_ occurrence of the `:char` removed from it"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(s/join (remove #{%2} %1)) :as :gone)
    (d/return-item :gone)
    ))



(def string-replace
  (i/build-instruction
    string-replace
    "`:string-replace` pops the top three `:string` values; call them `C`, `B` and `A`, respectively. It pushes the `:string` which results when `B` is replaced with `C` everywhere it occurs as a substring in `A`. The maximum :string the interpreter's `:max-collection-size`; if this is exceeded, the result is discarded and an `:error` is created."

    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] s/replace :as :replaced)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:replaced :limit]
      #(when (<= (count %1) %2) %1) :as :result)
    (d/calculate [:replaced :limit]
      #(when-not (<= (count %1) %2) ":string-replace result too large") :as :message)
    (d/return-item :result)
    (d/record-an-error :from :message)
    ))



(def string-replacechar
  (i/build-instruction
    string-replacechar
    "`:string-replacechar` pops the top `:string` and two `:char` items, replacing every occurrence of the top `:char` with the second in the `:string`, and pushing the result"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] s/replace :as :different)
    (d/return-item :different)
    ))



(def string-replacefirst
  (i/build-instruction
    string-replacefirst
    "`:string-replace` pops the top three `:string` values; call them `C`, `B` and `A`, respectively. It pushes the `:string` which results when `B` is replaced with `C` in the first place it occurs as a substring in `A`."

    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] s/replace-first :as :different)
    (d/return-item :different)
    ))



(def string-replacefirstchar
  (i/build-instruction
    string-replacefirstchar
    "`:string-replacefirstchar` pops the top `:string` and two `:char` items, replacing the first occurrence of the top `:char` (if any) with the second in the `:string`, and pushing the result"

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] s/replace-first :as :different)
    (d/return-item :different)
    ))



(def string-rest (i/simple-1-in-1-out-instruction
  "`:string-rest` returns the top `:string` item, lacking its first character"
  :string "rest" #(s/join (rest %1))
  ))



(def string-reverse (i/simple-1-in-1-out-instruction
  "`:string-reverse` returns the top `:string` item with its characters reversed"
  :string "reverse" 's/reverse
  ))



(def string-setchar
  (i/build-instruction
    string-setchar
    "`:string-setchar` pops the top `:string`, `:char` and `:scalar` values. It replaces the character at the indexed position in the `:string` with the popped `:char`, reducing it to be in range by `(mod index (count string))` if necessary."

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s :idx :c] #(s/join (assoc (vec %1) (long %2) %3)) :as :result)
    (d/return-item :result)
    ))



(def string-shatter   ;; string-parse-into-chars
  (i/build-instruction
    string-shatter
    "`:string-shatter` pops the top `:string` item, and pushes all of its characters as individual strings onto the `:string` stack. The result of shattering the `:string` \"bar\" would be the `:string` stack starting with `'(\"b\" \"a\" \"r\"...)`"

    (d/consume-top-of :string :as :s1)
    (d/consume-stack :string :as :old)
    (d/calculate [:s1] #(reverse (map str (seq %1))) :as :letters)
    (d/calculate [:old :letters] into :as :new)
    (d/replace-stack :exec :new)
    ))



(def string-solid? (i/simple-1-in-predicate
    "`:string-solid? pushes `true` if the top `:string` contains no whitespace"
    :string "solid?" #(boolean (re-matches #"\S+" %1))))



(def string-splitonspaces
  (i/build-instruction
    string-splitonspaces
    "`:string-splitonspaces` pops the top `:string` item, and pushes all the sub-strings remaining when it is split on any run of any number of whitespace characters.  The result of shattering the `:string` \"b a r\" would be the `:string` stack starting with `'(\"b\" \"a\" \"r\"...)`"

    (d/consume-top-of :string :as :s)
    (d/consume-stack :string :as :old)
    (d/calculate [:s] #(s/split %1 #"\s+") :as :words)
    (d/calculate [:words :old] #(into %2 (reverse %1)) :as :new)
    (d/replace-stack :exec :new)
    ))



(def string-spacey? (i/simple-1-in-predicate
  "`:string-spacey? pushes `true` if the top `:string` has any whitespace"
  :string "spacey?" #(boolean (re-matches #"\s+" %1))))



(def string-substring
  (i/build-instruction
    string-substring
    "`:string-substring` pops the top `:string` item, and two `:scalar` values (call them `A` and `B`). The values of `A` and `B` are _cropped_ into a suitable range for the string (truncated to lying within `[0,(count string)-1]`; _note_ not modulo the length!), and then a substring is extracted and pushed which falls between the lower and the higher of the two values."

    (d/consume-top-of :string :as :s)
    (d/consume-top-of :scalar :as :a)
    (d/consume-top-of :scalar :as :b)
    (d/calculate [:s :a]
      #(min (count %1) (max 0 (Math/ceil %2))) :as :cropped-a)
    (d/calculate [:s :b]
      #(min (count %1) (max 0 (Math/ceil %2))) :as :cropped-b)
    (d/calculate [:s :cropped-a :cropped-b]
        #(subs %1 (min %2 %3) (max %2 %3)) :as :result)
    (d/return-item :result)
    ))



(def string-take
  (i/build-instruction
    string-take
    "`:string-take` pops the top `:string` and `:scalar` items, and pushes the string resulting when the string is truncated to the length indicated; if the `:scalar` is outside the range permitted, it is brought into range using `(mod index (count string))`"

    (d/consume-top-of :string :as :s1)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s1 :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s1 :idx] #(s/join (take %2 %1)) :as :leftovers)
    (d/return-item :leftovers)
    ))


(def string-type
  ( ->  (t/make-type  :string
                      :recognized-by string?
                      :attributes #{:string :base})
        aspects/make-set-able
        aspects/make-cycling
        aspects/make-equatable
        aspects/make-comparable
        aspects/make-into-tagspaces
        aspects/make-movable
        aspects/make-printable
        aspects/make-quotable
        aspects/make-repeatable
        aspects/make-returnable
        aspects/make-storable
        aspects/make-taggable
        aspects/make-visible
        (t/attach-instruction , boolean->string)
        (t/attach-instruction , char->string)
        (t/attach-instruction , code->string)
        (t/attach-instruction , exec->string)
        (t/attach-instruction , scalar->string)
        (t/attach-instruction , string->scalar)
        (t/attach-instruction , string->scalars)
        (t/attach-instruction , exec-string-iterate)
        (t/attach-instruction , string-butlast)
        (t/attach-instruction , string-concat)
        (t/attach-instruction , string-conjchar)
        (t/attach-instruction , string-contains?)
        (t/attach-instruction , string-containschar?)
        (t/attach-instruction , string-emptystring?)
        (t/attach-instruction , string-first)
        (t/attach-instruction , string-indexofchar)
        (t/attach-instruction , string-last)
        (t/attach-instruction , string-length)
        (t/attach-instruction , string-nth)
        (t/attach-instruction , string-occurrencesofchar)
        (t/attach-instruction , string-removechar)
        (t/attach-instruction , string-replace)
        (t/attach-instruction , string-replacechar)
        (t/attach-instruction , string-replacefirst)
        (t/attach-instruction , string-replacefirstchar)
        (t/attach-instruction , string-rest)
        (t/attach-instruction , string-reverse)
        (t/attach-instruction , string-setchar)
        (t/attach-instruction , string-shatter)
        (t/attach-instruction , string-solid?)
        (t/attach-instruction , string-splitonspaces)
        (t/attach-instruction , string-spacey?)
        (t/attach-instruction , string-substring)
        (t/attach-instruction , string-take)
        ))
