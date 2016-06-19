(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
            [push.util.stack-manipulation :as u]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000 :lenient true}
                                   :bindings (:bindings prisoner)
                                   :program (:program prisoner))
      reset-interpreter))



(defn check-on-prisoner
  [prisoner]
  (let [interpreter (overloaded-interpreter prisoner)]
    (try
      (do
        (println (str "\n\nrunning:" (pr-str (:program interpreter)) "\nwith inputs: " (pr-str (:bindings interpreter))))
        (loop [s interpreter]
          (if (is-done? s)
            (println "DONE")
            (recur (do 
              (println (str "\n>>> " (:counter s)
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n>>> ATTEMPTING " (first (u/get-stack s :exec)) 
                            "\n items on :tagspace " (u/get-stack s :tagspace)
                            "\n items on :scalars " (u/get-stack s :scalars)
                            "\n items on :scalar " (u/get-stack s :scalar)
                            "\n\n"
                            (pr-str (u/peek-at-stack s :log))
                            
                              ))
              (step s))))))
      (catch Exception e (do 
                            (println 
                              (str "caught exception: " 
                                    (.getMessage e)
                                     " running "
                                     (pr-str (:program interpreter)) "\n"
                                     (pr-str (:bindings interpreter))))
                            (throw (Exception. (.getMessage e))))))))

(def prisoners
  [
  
  {
    :program 

    '[:code-rotate :strings-indexof 73/77 \B #{:booleans->tagspace 33479535 [0.1171875 0.0703125 0.140625 0.125 0.01171875 0.07421875 0.08203125 0.12890625] true false [false true false true true false true true] 113/174 [true true false false true false false]} :vector-take :code-shove :ref-pop :string-savestack :generator-yank :scalar-flush :char-echo :set-shove 635794908 :ref-lookup :chars-indexof :refs-notequal? :set-equal? [] :scalar≥? :tagspace-min :booleans-reverse 145/106 [\Ù \. \t \ \] [] [\Û \} \ \±] :exec-flipstack :vector-flipstack :set-echo :strings-flush :strings-return :tagspace-lookup :scalar<? :chars-swap [2478 616 3579 1003 1933 4239] (553744980 [\[ \ \t \³ \ \_ \] #{44.91015625 #{(:string-savestack #{true 34753508 ([true true true] :set-storestack :tagspace-values :scalars-cycler false) :input!8 :boolean-arity3 :strings-yankdup :string≥? :exec-shove} :code-rotate :scalar-liftstack [0.12109375 0.03125 0.0625 0.0390625 0.14453125 0.00390625 0.09375 0.08203125]) [\« \º \Ñ \â \Ö \] :vector-cycler :boolean-rotate 303.67578125 [0.1484375 0.015625 0.078125 0.03515625 0.078125 0.04296875 0.03515625] :exec-store :booleans-echoall} :set-equal? :string-replacefirst :scalars-savestack 506520263M :string-notequal? :string-rotate} :code-noop false) :string-equal? :string-cutflip 155289388M ["ËºÒ¥¹°j á»É«ow" "ÄdÝE" "3xÌßMf a<" "^" "0Ò#Ú´»ªD d¸Z"] ["Nåvçrb?o¬i^"] :environment-begin 109/106 :set-cycler #{:scalars-save 68/33 :code-againlater 91/75 [0.0625 0.109375 0.078125 0.03125 0.11328125 0.0859375 0.02734375 0.02734375] :string-shatter [")Y0Jâ1" "º" ")z@-¾byÂ(ÚhÄÔººxCF" "o+JÓ" "a/l'§åD{08" "UÙ¶M\"i¿Ä" "J;Ö ãå¾L."] :strings-shove} :ref-echoall [0.09765625 0.14453125 0.0625 0.0390625 0.0859375 0.0234375 0.125 0.03125] [] [false true true false false true true false true] :vector-storestack [true true false true true true] :code->set (\Ò :booleans-cutstack :vector-butlast 487603154M :scalars-flush) 294480967 :strings-do*each :set->tagspace :vector-echoall :code-shove "NyqfÔ~×©Ü­¾ÏÍ®b}_" :scalars-shove [0.0234375 0.11328125 0.125 0.046875 0.14453125 0.0234375] :refs-remove 44.4765625 (["ª$ËÎÎI{[" "wSxFÎ9N/Ö!¥Ö5á!*" "E" "p[6» pP£g0'½"] :scalar-many :tagspace-split [true false] :boolean->code) ["Ä]G¨u{H­;«*G¹¦¶"] :code-stackdepth [\J] :string-replacechar [false true true] :booleans-cycler "ji~|G¨CU`­/D" :ref-equal? :strings-length :tagspace-stackdepth \ true :scalar>? \j ["]r^gÚ±ªxz|Kn¯" "¬\\Õ¬$ÅaYD=³;I" "#-Q~" "L¶" "v½¤99¯»Èªçã6Ø" "¶zYz«½MÈ0Õ"] 116.328125 ([4500 3453 2477 2982 3504 4229 4692 987] [false true false true false false false] [true true true true] :code-return 181/113) :booleans-occurrencesof :scalar-yankdup :booleans-remove #{:scalar-flipstack :tagspace-split false [0.140625 0.05859375 0.08203125 0.109375 0.0625 0.00390625 0.125 0.08203125] :input!7 :code-empty? :code-position (259.640625 :vector-cutstack :boolean-later [\I \5 \Ü \ \. \ \= \7 \] :scalars-concat)} \& 572504271 "Â½9Ú8¼­-¥?}«" :string-sampler :char->integer :refs-generalize :generator-return-pop :booleans-first 886810168 "NÛAaã5{;ÃvÃ!\\(Y^@" :vector-remove \Å :vector-flush :code-length true [0.12890625 0.125 0.08984375 0.05078125 0.1484375 0.0390625 0.01953125 0.06640625] [1749 4274 2321 412 2651 3566 922 1011] [1536 3767 3901 2003 1586] :exec-dup :code-length [\` \ \á \S] "/!£iÆcw§+4Á_" :boolean-equal? :booleans-shove [625 4256 1194 101 4046] :strings-shove :ref-empty? [true false false true false true] 97/135 :string-echo :scalars-save :boolean-not 82.796875 91/173 ["*pÏ­" "LÍTP9g4>{©]Ò´j,\\»;" "u¾KÓEç²"] :scalars-contains? :string-echo :tagspace-liftstack [] [false false false false false true false false false] :boolean-cutstack false :chars-set :vector-dup (:set-tag [0.0390625 0.12109375 0.12890625 0.0546875 0.0234375 0.08984375] 913897447 :exec-yankdup :char-tag) :exec-return-pop [] [4139 579 129 2264 2491 1908 952 2000] "^9ÃàÐO" 144/7 :booleans-save ((#{\ :input!3 :boolean-3bittable 50/57 [0.05859375 0.1484375 0.1171875 0.03125 0.08984375 0.1015625] :code-do*count :boolean-arity3 :tagspace-offset} :print-space (:input!8 :vector-reverse 291158073 (:boolean-cutstack :push-bindings 383.0078125 false [true true false false true false true]) :tagspace-count) :input!7 :set-save) :chars-yankdup :scalar-abs :vector-rerunall 294196831M) :scalar-many :tagspace-cycler :scalar-echo :booleans-stackdepth 25/36 #{136.46875 763088959M :string≤? 671222076M :char-return :boolean-againlater 106910464 :scalars-occurrencesof} :booleans-first :code-comprehension :input!9 \J :exec-pop 586349683M :chars-swap :char-echo 109/127 781961198M :input!7 :input!10 :booleans-againlater 3/4 771553885M :generator-totalistic3 ([1900 2121 1372 1212 3464 2664 975 2500] false \@ [true true true] :chars-save) :input!1 #{204.1796875 #{:booleans->tagspace 127/90 "%ËÔ¡m<uØ*13Ø¹N¦ß4" [2932] \ :generator-pop :scalar-return-pop :set-later} ["m¯<cd" "g}Sæ" "aq©Â=5¨½z¬­ µ" "&D]`º½¤¹Òlæ" "!tÙvÌ?" "GT%Í" "Õ/å)/TÑÃS #uZÕ¬j¼" "|¾Ði7Rq|Ó" "2á%µ\""] :exec-dup :ref-tag [3421 3936 4831 4145 1616 4778] :exec-yankdup :tagspace-echo} :char-notequal? \ã :booleans-emptyitem? :ref-clear [0.125 0.0 0.0234375 0.06640625 0.1484375 0.0390625 0.12109375 0.14453125] :vector-return-pop :refs-cutstack :ref-liftstack :chars-equal? :scalar-flipstack 15/167 :refs-notequal? 358.796875 [] :code-swap [true false false false false false true false] (#{#{421420600M :string>? :tagspace-later [0.04296875 0.0703125 0.08203125 0.09765625 0.0234375 0.08203125 0.14453125 0.12890625] :booleans-pop :tagspace-rotate :chars-save 30/7} :ref-clear :push-refcycler :tagspace-dup [\£ \Ä \Q] 127427157M :strings-rotate 400091781M} :scalars-savestack :exec-liftstack \v #{:scalar-some :vector-flipstack ["§Ê(g_Éy©\""] true :ref-later :input!4 :code-container [844 4918 2354 3229 3126 398 1569 1734]}) :vector-first :scalars-do*each #{182.7734375 10/11 :code-nth ["Ò?9£>OaR" "Ly¨" "ÙÝâ" "/«Ê" "J³?×?¹+u¡°fVÞs " "ªFØ?Ò" "¾¹¾á*qkgÜ" "ÊFÞ" "­â"] :scalar-π 270.95703125 false :scalars-portion} :boolean-arity3 :integer-uniform :scalar-ln1p (:chars-concat :push-bindingcount :vector-portion 125.5859375 :code-echo) :set->code :string-replacefirstchar ["@9Ûh%D9" "Á<µÆÙØ[|7ÓX" "G¿ÌV"] true 36.9765625 :scalar-divide :boolean-save \[ "ÙMÓÕI$2À" :scalars-yank :scalar-dec :refs-do*each "," false]



    :bindings 

    '{:input!2 (\), :input!9 (844948673M), :input!3 (true), :input!10 (:refs-return-pop), :input!1 (:strings-flush), :input!8 (:chars-length), :input!4 ((:scalar-log10 :string>? :boolean-later false :boolean-later)), :input!7 ([2583 2407 3351 4952 1963]), :input!5 ([\Ù \¯ \Y \# \Ï \{]), :input!6 ((:refs-occurrencesof :vector-portion :string-tag :exec->string :code-quote))}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

