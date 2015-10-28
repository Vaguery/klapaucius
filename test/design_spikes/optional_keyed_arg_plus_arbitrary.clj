(ns design-spikes.optional-keyed-arg-plus-arbitrary
  (:use midje.sweet))

;; trying to learn how to do some fancy destructuring

(defn foo
  [always & many]
  (str always "," many))


(fact "no optional"
  (foo 88) => "88,")


(fact "with an optional"
  (foo 88 99) => "88,(99)")


(fact "with stuff"
  (foo 88 99 111 222) => "88,(99 111 222)")


(fact "keywords?"
  (foo 88 :tags 999) => "88,(:tags 999)")

(defn bar 
  [always & {:keys [tags]} ]
    (str always "," tags))


(fact "no optional"
  (bar 88) => "88,")


(fact "with an optional"
  (bar 88 :tags #{99 111}) => "88,#{99 111}")


;(grault arg1 :tags #{:sometags} :step1 :step2 :step3)
