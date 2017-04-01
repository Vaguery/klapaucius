(ns push.type.item.string
  (:require [push.instructions.core :as core]
            [push.type.core :as t]
            [push.instructions.dsl :as d]
            [clojure.string :as strings]
            [push.instructions.aspects :as aspects]
            [push.util.numerics :as num]
            [clojure.edn :as edn]
            )
  (:use push.type.item.tagspace)
)


;; SUPPORT



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
       (strings/join)
       re-pattern))



;; EXTERNAL TYPES



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
      :tags #{:string :base :conversion}
      `(d/consume-top-of ~stackname :as :arg)
      '(d/calculate [:arg] #(str %1) :as :printed)
      '(d/push-onto :string :printed)))))



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
  (core/build-instruction
    string->scalar
    "`:string->scalar` pops the top `:string` item and attempts to convert it into a `:scalar` value. The rules (in order of precedence) are: (1) If the string is empty, there is no result (no `:scalar` is pushed). (2) If the string contains whitespace, then it is broken into individual space-delimited tokens, and each of those is processed as follows, in turn, until the _first_ successful token creates a `:scalar` value, using the Clojure EDN reader as its standard. If no token produces any readable numeric result, no result is pushed. Tokens that produce reader errors are ignored. The first successful numeric value is pushed."
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg)
    (d/calculate [:arg] #(clojure.string/split %1 #"\s+") :as :chunks)
    (d/calculate [:chunks] #(valid-numbers-only %1) :as :numbers)
    (d/calculate [:numbers] #(first %1) :as :result)
    (d/push-onto :scalar :result)))




(def exec-string-iterate
  (core/build-instruction
    exec-string-iterate
    "`:exec-string-iterate` pops the top `:exec` item and the `:string`, and pushes a continuation form to the `:exec` stack that is either:

    - when the string is empty: nil [nothing is pushed]
    - when there is 1 character: `'([head] [item])`
    - when there are more characters, say `tail` is the string lacking its first character, and `head` is the first character: `'([head] [item] [tail] :exec-string-iterate [item])`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :exec :as :fn)
    (d/calculate [:s] #(first %1) :as :head)
    (d/calculate [:s] #(strings/join (rest %1)) :as :tail)
    (d/calculate [:fn :s :head :tail]
        #(cond (empty? %2) nil
               (empty? %4) (list %3 %1)
               :else (list %3 %1 %4 :exec-string-iterate %1)) :as :continuation)
    (d/push-onto :exec :continuation)))



(def string-butlast (t/simple-1-in-1-out-instruction
  "`:string-butlast` returns the top `:string` item lacking its last character"
    :string "butlast" #(clojure.string/join (butlast %1))))



(def string-concat
  (core/build-instruction
    string-concat
    "`:string-concat` pops the top two `:string` items, and pushes the result of concatenating the top item at the end of the second item. If the result would be longer than :max-collection-size, it is discarded."
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg2)
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1 :arg2]
      #(strings/join (list %1 (str %2))) :as :longer)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:longer :limit]
      #(if (< (count %1) %2) %1 nil) :as :result)
    (d/push-onto :string :result)))



(def string-conjchar
  (core/build-instruction
    string-conjchar
    "`:string-conjchar` pops the top `:string` and the top `:char`, and pushes a new `:string` with the `:char` added at the end"
    :tags #{:string :base}
    (d/consume-top-of :char :as :c)
    (d/consume-top-of :string :as :s)
    (d/calculate [:s :c] #(strings/join (list %1 (str %2))) :as :longer)
    (d/push-onto :string :longer)))



(def string-contains?
  (core/build-instruction
    string-contains?
    "`:string-contains?` pops two strings (call them `A` and `B`, respectively). It pushes `true` if `B` is a substring of `A`, or `false` otherwise"
    :tags #{:string :base}
    (d/consume-top-of :string :as :host)
    (d/consume-top-of :string :as :target)
    (d/calculate [:host :target]
      #(boolean (re-find (re-pattern (str-to-pattern %2)) %1)) :as :found?)
    (d/push-onto :boolean :found?)))



(def string-containschar?
  (core/build-instruction
    string-containschar?
    "`:string-containschar?` pops the top `:string` and `:char` values, and pushes `true` if the string contains the character, `false` otherwise"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(boolean (some #{%2} (vec %1))) :as :found?)
    (d/push-onto :boolean :found?)))



(def string-emptystring? (t/simple-1-in-predicate
  "`:string-emptystring? pushes `true` if the top `:string` has no characters"
  :string "emptystring?" empty?))



(def string-first
  (core/build-instruction
    string-first
    "`:string-first` pops the top `:string` value, and pushes its first `:char`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(first %1) :as :c)
    (d/push-onto :char :c)))



(def string-indexofchar
  (core/build-instruction
    string-indexofchar
    "`:string-indexofchar` pops the top `:string` and the top `:char`, and pushes a `:scalar` which is the index of the first occurrence of `:char` in `:string`, or -1 if it's not found"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(.indexOf %1 (long %2)) :as :where)
    (d/push-onto :scalar :where)))



(def string-last
  (core/build-instruction
    string-last
    "`:string-last` pops the top `:string` item, and pushes its last `:char` (if it has one)"
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(last %1) :as :c)
    (d/push-onto :char :c)))



(def string-length
  (core/build-instruction
    string-length
    "`:string-length` pops the top `:string` and pushes its length (counting unicode-aware characters) to the `:scalar` stack"
    :tags #{:string :base}
    (d/consume-top-of :string :as :arg1)
    (d/calculate [:arg1] #(count %1) :as :len)
    (d/push-onto :scalar :len)))



(def string-nth
  (core/build-instruction
    string-nth
    "`:string-last` pops the top `:string` item and an index value from the `:scalar` stack, and pushes the indexed `:char` (if it has one); if the index is out of range, it is reduced to an integer value in range via `(mod (ceil index) (count string))`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s :idx] #(if (empty? %1) nil (nth %1 %2)) :as :result)
    (d/push-onto :char :result)))



(def string-occurrencesofchar
  (core/build-instruction
    string-occurrencesofchar
    "`:string-occurrencesofchar` pops the top `:string` and `:char` values, and pushes to `:integer` the number of times the `:char` appears in the `:string`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(get (frequencies %1) %2 0) :as :count)
    (d/push-onto :scalar :count)))



(def string-removechar
  (core/build-instruction
    string-removechar
    "`:string-removechar` pops the top `:string` and `:char` items, and pushes the string with _every_ occurrence of the `:char` removed from it"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/calculate [:s :c] #(clojure.string/join (remove #{%2} %1)) :as :gone)
    (d/push-onto :string :gone)))



(def string-replace
  (core/build-instruction
    string-replace
    "`:string-replace` pops the top three `:string` values; call them `C`, `B` and `A`, respectively. It pushes the `:string` which results when `B` is replaced with `C` everywhere it occurs as a substring in `A`. The maximum :string length is 131072 (128k) characters; if this is exceeded, the result is discarded."
    :tags #{:string :base}
    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] #(strings/replace %1 %2 %3) :as :replaced)
    (d/save-max-collection-size :as :limit)
    (d/calculate [:replaced :limit] #(if (< (count %1) %2) %1 nil) :as :result)
    (d/push-onto :string :result)))



(def string-replacechar
  (core/build-instruction
    string-replacechar
    "`:string-replacechar` pops the top `:string` and two `:char` items, replacing every occurrence of the top `:char` with the second in the `:string`, and pushing the result"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] #(strings/replace %1 %2 %3) :as :different)
    (d/push-onto :string :different)))



(def string-replacefirst
  (core/build-instruction
    string-replacefirst
    "`:string-replace` pops the top three `:string` values; call them `C`, `B` and `A`, respectively. It pushes the `:string` which results when `B` is replaced with `C` in the first place it occurs as a substring in `A`."
    :tags #{:string :base}
    (d/consume-top-of :string :as :s3)
    (d/consume-top-of :string :as :s2)
    (d/consume-top-of :string :as :s1)
    (d/calculate [:s1 :s2 :s3] #(strings/replace-first %1 %2 %3) :as :different)
    (d/push-onto :string :different)))



(def string-replacefirstchar
  (core/build-instruction
    string-replacefirstchar
    "`:string-replacefirstchar` pops the top `:string` and two `:char` items, replacing the first occurrence of the top `:char` (if any) with the second in the `:string`, and pushing the result"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c1)
    (d/consume-top-of :char :as :c2)
    (d/calculate [:s :c1 :c2] #(strings/replace-first %1 %2 %3) :as :different)
    (d/push-onto :string :different)))



(def string-rest (t/simple-1-in-1-out-instruction
  "`:string-rest` returns the top `:string` item, lacking its first character"
  :string "rest" #(clojure.string/join (rest %1))))



(def string-reverse (t/simple-1-in-1-out-instruction
  "`:string-reverse` returns the top `:string` item with its characters reversed"
  :string "reverse" 'strings/reverse))



(def string-setchar
  (core/build-instruction
    string-setchar
    "`:string-setchar` pops the top `:string`, `:char` and `:scalar` values. It replaces the character at the indexed position in the `:string` with the popped `:char`, reducing it to be in range by `(mod index (count string))` if necessary."
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :char :as :c)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s :idx :c] #(strings/join (assoc (vec %1) (long %2) %3)) :as :result)
    (d/push-onto :string :result)))



(def string-shatter   ;; string-parse-into-chars
  (core/build-instruction
    string-shatter
    "`:string-shatter` pops the top `:string` item, and pushes all of its characters as individual strings onto the `:string` stack. The result of shattering the `:string` \"bar\" would be the `:string` stack starting with `'(\"b\" \"a\" \"r\"...)`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s1)
    (d/consume-stack :string :as :old)
    (d/calculate [:s1] #(reverse (map str (seq %1))) :as :letters)
    (d/calculate [:old :letters] #(into %1 %2) :as :new)
    (d/replace-stack :string :new)))



(def string-solid? (t/simple-1-in-predicate
    "`:string-solid? pushes `true` if the top `:string` contains no whitespace"
    :string "solid?" #(boolean (re-matches #"\S+" %1))))



(def string-splitonspaces
  (core/build-instruction
    string-splitonspaces
    "`:string-splitonspaces` pops the top `:string` item, and pushes all the sub-strings remaining when it is split on any run of any number of whitespace characters.  The result of shattering the `:string` \"b a r\" would be the `:string` stack starting with `'(\"b\" \"a\" \"r\"...)`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-stack :string :as :old)
    (d/calculate [:s] #(strings/split %1 #"\s+") :as :words)
    (d/calculate [:words :old] #(into %2 (reverse %1)) :as :new)
    (d/replace-stack :string :new)))



(def string-spacey? (t/simple-1-in-predicate
  "`:string-spacey? pushes `true` if the top `:string` has any whitespace"
  :string "spacey?" #(boolean (re-matches #"\s+" %1))))



(def string-substring
  (core/build-instruction
    string-substring
    "`:string-substring` pops the top `:string` item, and two `:scalar` values (call them `A` and `B`). The values of `A` and `B` are _cropped_ into a suitable range for the string (truncated to lying within `[0,(count string)-1]`; _note_ not modulo the length!), and then a substring is extracted and pushed which falls between the lower and the higher of the two values."
    :tags #{:string :base}
    (d/consume-top-of :string :as :s)
    (d/consume-top-of :scalar :as :a)
    (d/consume-top-of :scalar :as :b)
    (d/calculate [:s :a]
      #(min (count %1) (max 0 (Math/ceil %2))) :as :cropped-a)
    (d/calculate [:s :b]
      #(min (count %1) (max 0 (Math/ceil %2))) :as :cropped-b)
    (d/calculate [:s :cropped-a :cropped-b]
        #(subs %1 (min %2 %3) (max %2 %3)) :as :result)
    (d/push-onto :string :result)))



(def string-take
  (core/build-instruction
    string-take
    "`:string-take` pops the top `:string` and `:scalar` items, and pushes the string resulting when the string is truncated to the length indicated; if the `:scalar` is outside the range permitted, it is brought into range using `(mod index (count string))`"
    :tags #{:string :base}
    (d/consume-top-of :string :as :s1)
    (d/consume-top-of :scalar :as :where)
    (d/calculate [:s1 :where]
      #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
    (d/calculate [:s1 :idx] #(strings/join (take %2 %1)) :as :leftovers)
    (d/push-onto :string :leftovers)))


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
