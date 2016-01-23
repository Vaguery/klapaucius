(ns push.util.legacy-test
  (:use midje.sweet)
  (:use push.util.legacy))


(fact "input symbols are made into keywords"
  (translate-input 'in66) => :in66)


(fact "instruction symbols are made into keywords"
  (translate-instruction 'integer_add) => :integer-add
  (translate-instruction 'code_fromzipchildren) => :not-supported-code_fromzipchildren
  (translate-instruction 'float_gt) => :float>?)


(fact "keywords that aren't recognized at all are flagged"
  (translate-instruction 'foobarbaz) => :unrecognized-foobarbaz)


(fact "primitives raise an exception if they end up in translate-instruction"
  (translate-instruction 88) => (throws #"cannot translate items that")
  (translate-instruction [1 2 3]) => (throws #"cannot translate items that")
  (translate-instruction '(1 2 3)) => (throws #"cannot translate items that"))


;; translate-item

(fact "translate-item works on inputs"
  (translate-item 'in55) => :in55
  (translate-item 'in5) => :in5
  (translate-item 'in055) => :in055)
  

(fact "translate-item tries instructions if it doesn't match inputs"
  (translate-item 'something_in55) => :unrecognized-something_in55)


(fact "translate-item passes most literals through"
  (translate-item 8812) => 8812
  (translate-item "foo") => "foo"
  (translate-item false) => false
  (translate-item 8.2) => 8.2
  (translate-item [1 2 3]) => [1 2 3]
  (translate-item \g) => \g)



(fact "translate-item recurses into lists (as such)"
  (translate-item '(integer_add boolean_or (float_div 77))) =>
    '(:integer-add :boolean-or (:float-divide 77)))


;; clojush->klapaucius

(fact "clojush->klapaucius returns a vector klapaucius program, with all translation done"
  (translate-item '(integer_add boolean_or (float_div 77))) =>
    '[:integer-add :boolean-or (:float-divide 77)])


(fact "there are a lot of recognized instruction names, so I thought you'd like to see how many"
  (count (remove #(re-seq #"not-supported" (str %)) (vals merged-dictionary))) => 355)
