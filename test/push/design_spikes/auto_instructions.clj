(ns push.design-spikes.auto-instructions
  (:use midje.sweet)
  (:use push.interpreter))

;; This is a design spike. That means it's NOT TO BE USED.
;; IT'S A SKETCH.

(def rgba { :typename :rgba
            :signature {:red :integer
                        :green :integer 
                        :blue :integer
                        :alpha :float}})

(defn make-setter
  [newtype argument argtype]
  (let [funcname (str (name newtype) "_set_" (name argument) "_from_" (name argtype))]
    funcname
  ))

(defn make-getter
  [newtype argument argtype]
  (let [funcname (str (name newtype) "_get_" (name argument))]
    funcname
  ))


(fact "make-setter makes a setter"
  (make-setter :rgba :red :integer) => "rgba_set_red_from_integer")

(fact "make-getter makes a getter"
  (make-getter :rgba :red :integer) => "rgba_get_red")

(defn all-setters-n-getters
  [newtype]
  (let [n (:typename newtype)]
    (sort (flatten 
      (map (fn [[k v]] [(make-setter n k v) (make-getter n k v)]) (:signature newtype))))))


(fact "all-s-n-gs does the thing"
  (all-setters-n-getters rgba) => '("rgba_get_alpha" "rgba_get_blue" "rgba_get_green" "rgba_get_red" "rgba_set_alpha_from_float" "rgba_set_blue_from_integer" "rgba_set_green_from_integer" "rgba_set_red_from_integer"))