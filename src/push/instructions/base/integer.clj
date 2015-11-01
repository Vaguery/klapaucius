(ns push.instructions.base.integer
  (:require [push.instructions.instructions-core :as core])
  (:use [push.instructions.dsl]))


(def integer-add
  (core/build-instruction
    integer-add
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(+' %1 %2) :as :sum)
    (push-onto :integer :sum)))


(def integer-dec
  (core/build-instruction
    integer-dec
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1] #(dec' %1) :as :next)
    (push-onto :integer :next)))


(def integer-inc
  (core/build-instruction
    integer-inc
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1] #(inc' %1) :as :next)
    (push-onto :integer :next)))


(def integer-multiply
  (core/build-instruction
    integer-multiply
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(*' %1 %2) :as :prod)
    (push-onto :integer :prod)))


(def integer-subtract
  (core/build-instruction
    integer-subtract
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(-' %1 %2) :as :diff)
    (push-onto :integer :diff)))


(def integer-divide
  (core/build-instruction
    integer-divide
    :tags #{:arithmetic :base :dangerous}
    (consume-top-of :integer :as :denominator)
    (consume-top-of :integer :as :numerator)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %1 (int (/ %2 %1))) :as :quotient)
    (push-these-onto :integer [:replacement :quotient])))


(def integer-mod
  (core/build-instruction
    integer-mod
    :tags #{:arithmetic :base :dangerous}
    (consume-top-of :integer :as :denominator)
    (consume-top-of :integer :as :numerator)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %2 nil) :as :replacement)
    (calculate [:denominator :numerator]
      #(if (zero? %1) %1 (mod %2 %1)) :as :remainder)
    (push-these-onto :integer [:replacement :remainder])))


;; comparison


(def integer-lt
  (core/build-instruction
    integer-lt
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(< %1 %2) :as :less?)
    (push-onto :boolean :less?)))


(def integer-lte
  (core/build-instruction
    integer-lte
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(<= %1 %2) :as :lte?)
    (push-onto :boolean :lte?)))


(def integer-gt
  (core/build-instruction
    integer-gt
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(> %1 %2) :as :more?)
    (push-onto :boolean :more?)))


(def integer-gte
  (core/build-instruction
    integer-gte
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(>= %1 %2) :as :more?)
    (push-onto :boolean :more?)))


(def integer-eq
  (core/build-instruction
    integer-eq
    :tags #{:numeric :base :comparison}
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg1)
    (calculate [:arg1 :arg2] #(= %1 %2) :as :same?)
    (push-onto :boolean :same?)))


(def integer-max
  (core/build-instruction
    integer-max
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(max %1 %2) :as :biggest)
    (push-onto :integer :biggest)))


(def integer-min
  (core/build-instruction
    integer-min
    :tags #{:arithmetic :base}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (calculate [:arg1 :arg2] #(min %1 %2) :as :smallest)
    (push-onto :integer :smallest)))


;; introspection (will eventually be generated magically)


(def integer-empty?
  (core/build-instruction
    integer-empty?
    :tags #{:numeric :base :introspection}
    (count-of :integer :as :depth)
    (calculate [:depth] #(zero? %1) :as :check)
    (push-onto :boolean :check)))


(def integer-stackdepth
  (core/build-instruction
    integer-stackdepth
    :tags #{:numeric :base :introspection}
    (count-of :integer :as :depth)
    (push-onto :integer :depth)))


;; combinators (will eventually be generated automatically)


(def integer-dup
  (core/build-instruction
    integer-dup
    :tags #{:numeric :base :combinator}
    (save-top-of :integer :as :again)
    (push-onto :integer :again)))


(def integer-flush
  (core/build-instruction
    integer-flush
    :tags #{:numeric :base :combinator}
    (delete-stack :integer)))


(def integer-pop
  (core/build-instruction
    integer-pop
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :gone)))


(def integer-rotate
  (core/build-instruction
    integer-rotate
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (consume-top-of :integer :as :arg3)
    (push-onto :integer :arg2)
    (push-onto :integer :arg1)
    (push-onto :integer :arg3)))


(def integer-shove
  (core/build-instruction
    integer-shove
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :index)
    (consume-top-of :integer :as :shoved-item)
    (insert-as-nth-of :integer :shoved-item :at :index)))


(def integer-swap
  (core/build-instruction
    integer-swap
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :arg1)
    (consume-top-of :integer :as :arg2)
    (push-onto :integer :arg1)
    (push-onto :integer :arg2)))


(def integer-yank
  (core/build-instruction
    integer-yank
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :index)
    (count-of :integer :as :how-many)
    (consume-nth-of :integer :at :index :as :yanked-item)
    (push-onto :integer :yanked-item)))


(def integer-yankdup
  (core/build-instruction
    integer-yankdup
    :tags #{:numeric :base :combinator}
    (consume-top-of :integer :as :index)
    (count-of :integer :as :how-many)
    (save-nth-of :integer :at :index :as :yanked-item)
    (push-onto :integer :yanked-item)))

