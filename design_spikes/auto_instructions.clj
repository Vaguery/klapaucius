(ns push.design-spikes.auto-instructions
  (:use midje.sweet))

;; This is a design spike. That means it's NOT TO BE USED.
;; IT'S A SKETCH.

(def rgba { :typename :rgba
            :signature {:red :integer
                        :green :integer 
                        :blue :integer
                        :alpha :float}})

(defn make-setter
  [newtype argument argtype]
  (let [funcname (str (name newtype) "-set-" (name argument) "-from-" (name argtype))]
    funcname
  ))

(defn make-getter
  [newtype argument argtype]
  (let [funcname (str (name newtype) "-get-" (name argument))]
    funcname
  ))


(fact :spike "make-setter makes a setter"
  (make-setter :rgba :red :integer) => "rgba-set-red-from-integer")

(fact :spike "make-getter makes a getter"
  (make-getter :rgba :red :integer) => "rgba-get-red")

(defn all-setters-n-getters
  [newtype]
  (let [n (:typename newtype)]
    (sort (flatten 
      (map (fn [[k v]] [(make-setter n k v) (make-getter n k v)]) (:signature newtype))))))


(fact :spike "all-setters-n-getters creates the power set of instruction names over get/set for the named atributes specified"
  (all-setters-n-getters rgba) => 
    '("rgba-get-alpha"
      "rgba-get-blue"
      "rgba-get-green"
      "rgba-get-red"
      "rgba-set-alpha-from-float"
      "rgba-set-blue-from-integer"
      "rgba-set-green-from-integer"
      "rgba-set-red-from-integer"))


(def rect { :typename :rect
            :signature {:top :integer
                        :left :integer 
                        :height :integer
                        :width :integer
                        :color :rgba}})


(fact :spike "all-setters-n-getters still works"
  (all-setters-n-getters rect) => 
    '("rect-get-color"
      "rect-get-height"
      "rect-get-left"
      "rect-get-top"
      "rect-get-width"
      "rect-set-color-from-rgba"
      "rect-set-height-from-integer"
      "rect-set-left-from-integer"
      "rect-set-top-from-integer"
      "rect-set-width-from-integer"))


;; BUT NOTICE
;  One could also contrive these so the presence of the :rgba type in the
;  specification for :rect would -automatically- cascade to produce 
;
;  (make-rect int int int int rgba)
;  (make-rect int int int int (make-rgba int int int float))
;  (rect-set-red-in-color int)
;  (rect-set-green-in-color int)
;  (rect-set-blue-in-color int)
;  (rect-set-float-in-color float)
;  etc.