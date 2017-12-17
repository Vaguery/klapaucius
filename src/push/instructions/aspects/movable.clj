(ns push.instructions.aspects.movable
  (:require [push.util.code-wrangling :as util
              :refer [list! count-collection-points]]
            [push.util.numerics :as num :refer [scalar-to-index]]
            [push.type.definitions.quoted :as qc])
  (:use     [push.instructions.core :only (build-instruction)]
            [push.instructions.dsl]
            ))



(defn againlater-instruction
  "returns a new x-againlater instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-againlater")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` places a copy of the top `" typename "` item at both the head and tail of the `:exec` stack (and places the `:exec` stack itself in a code block). If the resulting `:exec` stack would be oversized, the item is discarded and an `:error` is pushed without affecting the `:exec` stack.")
      :tags #{:combinator}

      `(consume-top-of ~typename :as :item)
      `(calculate [:item]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :item)
      `(consume-stack :exec :as :oldexecstack)
      `(save-max-collection-size :as :limit)
      `(calculate [:oldexecstack :item]
        #(list %2 %1 %2) :as :newexecstack)
      `(calculate [:limit :newexecstack]
        #(< %1 (util/count-collection-points %2)) :as :oversized)
      `(calculate [:oversized :oldexecstack :newexecstack]
        #(if %1 %2 %3) :as :finalstack)
      `(calculate [:oversized]
        #(when %1 (str ~instruction-name
                       " produced an oversized result")) :as :message)
      `(replace-stack :exec :finalstack)
      `(record-an-error :from :message)
      ))))



(defn cutflip-instruction
  "returns a new x-cutflip instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cutflip")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` consumes a `:scalar` argument and the entire `" typename "` stack. It takes the first [idx] items of the stack and reverses the order of that segment. It returns a nested code block, containing two interior code blocks holding the 'remaining' part of the stack (reversed) and the 'flipped' part of the stack.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :where)
      `(consume-stack ~typename :as :old-stack)
      `(calculate [:old-stack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :old-stack)
      `(calculate [:where :old-stack]
        #(if (empty? %2) 0 (num/scalar-to-index %1 (count %2))) :as :idx)
      `(calculate [:old-stack :idx]
        #(util/list! (take %2 %1)) :as :topchunk)
      `(calculate [:old-stack :idx]
        #(util/list! (reverse (drop %2 %1))) :as :leftovers)
      `(calculate [:leftovers :topchunk] #(list %1 %2) :as :result)
      `(return-item :result)
      ))))



(defn cutstack-instruction
  "returns a new x-cutstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-cutstack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` takes an `:scalar` argument, makes that into an index modulo the `" typename "` stack size, takes the first [idx] items and moves that to the bottom of the stack.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :where)
      `(consume-stack ~typename :as :old-stack)
      `(calculate [:old-stack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :old-stack)
      `(calculate [:where :old-stack]
        #(if (empty? %2) 0 (num/scalar-to-index %1 (count %2))) :as :idx)
      `(calculate [:old-stack :idx]
        #(util/list! (reverse (take %2 %1))) :as :topchunk)
      `(calculate [:old-stack :idx]
        #(util/list! (reverse (drop %2 %1))) :as :leftovers)
      `(calculate [:leftovers :topchunk] #(list %2 %1) :as :result)
      `(return-item :result)
      ))))



(defn dup-instruction
  "returns a new x-dup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-dup")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` examines the top `" typename "` item and pushes a code block containing two copies to `:exec`.")
      :tags #{:combinator}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg1)
      `(calculate [:arg1] #(list %1 %1) :as :duplicated)
      `(return-item :duplicated)
      ))))



(defn flipstack-instruction
  "returns a new x-flipstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flipstack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` reverses the entire `" typename "` stack, pushing it as a code block onto `:exec`.")
      :tags #{:combinator}

      `(consume-stack ~typename :as :old-stack)
      `(calculate [:old-stack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :old-stack)
      `(calculate [:old-stack] #(util/list! (reverse %1)) :as :new)
      `(return-item :new)
      ))))



(defn flush-instruction
  "returns a new x-flush instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-flush")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` discards all items from the `" typename "` stack.")
      :tags #{:combinator}

      `(delete-stack ~typename)
      ))))



(defn later-instruction
  "returns a new x-later instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-later")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `" typename "` item and places it at the tail of the `:exec` stack. If the result would be oversized, the item is discarded and an `:error` is pushed.")
      :tags #{:combinator}

      `(consume-top-of ~typename :as :item)
      `(calculate [:item]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :item)
      `(consume-stack :exec :as :oldstack)
      `(save-max-collection-size :as :limit)
      `(calculate [:oldstack :item]
          #(util/list! (concat %1 (list %2))) :as :newstack)
      `(calculate [:limit :newstack]
        #(< %1 (util/count-collection-points %2)) :as :oversized)
      `(calculate [:oversized :oldstack :newstack]
        #(if %1 %2 %3) :as :finalstack)
      `(calculate [:oversized]
        #(when %1 (str ~instruction-name
                       " produced an oversized result")) :as :message)
      `(calculate [:oversized :item] #(when-not %1 %2) :as :replacement)
      `(replace-stack :exec :finalstack)
      `(record-an-error :from :message)
      ))))



(defn liftstack-instruction
  "returns a new x-liftstack instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-liftstack")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` takes an `:scalar` argument, makes that into an index modulo the `" typename "` stack size, 'cuts' the stack after the first [idx] items and _copies_ everything below that point onto the top of the stack. If the result would have more points (at any level) than `max-collection-size` the result is _discarded_ (!) and an :error is pushed.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :where)
      `(consume-stack ~typename :as :old-stack)
      `(calculate [:old-stack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :old-stack)
      `(calculate [:old-stack :where]
        #(if (empty? %1) 0 (num/scalar-to-index %2 (count %1))) :as :idx)
      `(calculate [:old-stack :idx]
        #(util/list! (reverse (drop %2 %1))) :as :duplicated)
      `(calculate [:old-stack :duplicated]
        #(list (util/list! (reverse %1)) %2) :as :results)
      `(return-item :results)
      ))))



(defn pop-instruction
  "returns a new x-pop instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-pop")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` discards the top item from the `" typename "` stack.")
      :tags #{:combinator}

      `(delete-top-of ~typename)
      ))))



(defn rotate-instruction
  "returns a new x-rotate instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-rotate")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top three items from the `" typename "` stack; call them `A`, `B` and `C`, respectively. It pushes a code block to `:exec` so that the resulting `" typename "` stack will read `'(C A B ...)`")
      :tags #{:combinator}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg1)
      `(consume-top-of ~typename :as :arg2)
      `(calculate [:arg2]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg2)
      `(consume-top-of ~typename :as :arg3)
      `(calculate [:arg3]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg3)
      `(calculate [:arg1 :arg2 :arg3] #(list %2 %1 %3) :as :result)
      `(return-item :result)
      ))))



(defn shove-instruction
  "returns a new x-shove instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-shove")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the entire `" typename
        "` stack and the top `:scalar`. The `:scalar` is used to select a valid index; unlike most other indexed arguments, this is _thresholded_. The stack is returned (to `:exec`) as a code block containing the original top item moved to the indexed position.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :which)
      `(consume-top-of ~typename :as :shoved-item)
      `(calculate [:shoved-item]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :shoved-item)
      `(count-of ~typename :as :how-many)
      `(calculate [:which :how-many]
        #(if (zero? %2) 0 (max 0 (min (Math/ceil %1) %2))) :as :index)
      `(consume-stack ~typename :as :oldstack)
      `(calculate [:oldstack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :oldstack)

      `(calculate [:index :oldstack]
        #(util/list! (reverse (take %1 %2))) :as :old-top)
      `(calculate [:index :oldstack]
        #(util/list! (reverse (drop %1 %2))) :as :old-bottom)
      `(calculate [:old-top :shoved-item :old-bottom]
        #(list %3 %2 %1) :as :results)
      `(return-item :results)
      ))))



(defn swap-instruction
  "returns a new x-swap instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-swap")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` swaps the positions of the top two `" typename
        "` items, pushing the result to `:exec` as a code block.")
      :tags #{:combinator}

      `(consume-top-of ~typename :as :arg1)
      `(calculate [:arg1]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg1)
      `(consume-top-of ~typename :as :arg2)
      `(calculate [:arg2]
        #(if (= :code ~typename) (qc/push-quote %1) %1) :as :arg2)
      `(calculate [:arg1 :arg2] #(list %1 %2) :as :reversed)
      `(return-item :reversed)
      ))))



(defn yank-instruction
  "returns a new x-yank instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yank")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:scalar`. The `:scalar` is brought into range as an index by forcing it into the range `[0,stack_length-1]` (inclusive), and then the item _currently_ found in the indexed position in the `" typename "` stack is _moved_ so that it is on top.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :which)
      `(consume-stack ~typename :as :oldstack)
      `(calculate [:oldstack]
        #(if (= :code ~typename) (map qc/push-quote %1) %1) :as :oldstack)
      `(calculate [:oldstack] count :as :how-many)
      `(calculate [:which :how-many]
          #(if (zero? %2)
              0
              (max 0 (min (Math/ceil %1) (dec %2)))) :as :index)
      `(calculate [:index :oldstack]
        #(if (empty? %2) nil (nth %2 %1)) :as :yanked-item)
      `(calculate [:index :oldstack]
        #(util/list! (reverse (take %1 %2))) :as :old-top)
      `(calculate [:index :oldstack]
        #(util/list! (reverse (drop (inc %1) %2))) :as :old-bottom)
      `(calculate [:old-bottom :yanked-item :old-top]
        #(if (nil? %2) (list %1 %3) (list %1 %3 %2)) :as :results)
      `(return-item :results)
      ))))



(defn yankdup-instruction
  "returns a new x-yankdup instruction for a PushType"
  [pushtype]
  (let [typename (:name pushtype)
        instruction-name (str (name typename) "-yankdup")]
    (eval (list
      `build-instruction
      instruction-name
      (str "`:" instruction-name "` pops the top `:scalar`. The `:scalar` is brought into range as an index by forcing it into the range `[0,stack_length-1]` (inclusive), and then the item _currently_ found in the indexed position in the `" typename "` stack is _copied_ so that a duplicate of it is on top. The entire stack is pushed to `:exec` in a code block.")
      :tags #{:combinator}

      `(consume-top-of :scalar :as :which)
      `(count-of ~typename :as :how-many)
      `(calculate [:which :how-many]
          #(if (zero? %2)
            0
            (max 0 (min (Math/ceil %1) (dec %2)))) :as :index)
      `(consume-stack ~typename :as :oldstack)
      `(calculate [:index :oldstack]
        #(if (empty? %2) nil (nth %2 %1)) :as :yanked-item)
      `(calculate [:oldstack :yanked-item]
        #(if (nil? %2)
          (util/list! (reverse %1))
          (list (util/list! (reverse %1)) %2)) :as :results)
      `(return-item :results)
      ))))
