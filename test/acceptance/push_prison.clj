(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000 :lenient true :max-collection-size 138072}
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
                            "\n items on :generator " (count (u/get-stack s :generator))
                            "\n items on :vector " (u/get-stack s :vector)
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

    '[665497240M :string-replacechar #{:tagspace-liftstack ["ÏçEyâ" "ÓY\\[412m:Ù²­X¡" "{VC3È"] :generator-save :integer-totalistic3 932213552 :booleans-swap ["ÀMÃ$:ËuT¶0KSU" "Ú­" "6VÝÉ_¬\"Ïh#Ý©K%X" "y8oBºl@ThBÈ°¥V\\ØÞ­ªU"] :complex-multiply} :complexes-comprehension "U?y·$Ú?H¹}Â" [true true true true true] [1398 3878 1823 3641 3013 517 3749] :char-rerunall [true true false false] (768021850 :refs-conj [\C \U \ \Ý \Ç \j \â \L] "¢fÑÌ" 36/35) :scalar->boolean 368.71484375 167.5703125 :ref-echoall 39/29 ["_¾²­É0µ]ÚB¾" "}<Cg&#PsÔµ´^ÌÞa"] :scalar-swap 377182171 83.35546875 [4996 1613 465 4342] :input!4 #push.type.definitions.complex.Complex{:re 631085642, :im 55.73046875} #push.type.definitions.complex.Complex{:re 325596595, :im 384.16796875} :vector-conj 53532922 :input!3 :input!6 :interval-rebracket [0.0625 0.07421875 0.03515625 0.1484375 0.07421875] :scalars-concat 27/22 :booleans-againlater (:strings-conj :strings-pop \ä :exec-liftstack 616469440M) :ref-storestack :refs-vremove [] :interval-stackdepth :tagspace-tidy \f :input!3 [\Ì \] 882098145M [2070 817 3060 1637 879] [0.08984375 0.08984375 0.07421875 0.09375 0.05078125 0.0703125 0.07421875] :code-in-set? [3858 2105 2569 2877 1432 4836 1261 570] :complex-notequal? 250203283M #{"\")!|Ê¸¢ÈÊPÔ S" #{918656710 :push-storeARGS :scalar-multiply :interval-newopen :input!4 :char-savestack 783536330 [1768 4581]} :input!1 647096731 #{:set-pop \C :char<? #push.type.definitions.complex.Complex{:re 507752872, :im 381.61328125} [\ \ª \E \â \J \ \ \6 \r] :vector-vfilter (#push.type.definitions.interval.Interval{:min 9/4, :max 125.19921875, :min-open? false, :max-open? true} 686350391 #push.type.definitions.complex.Complex{:re 34908806, :im 96.92578125} #{:tagspace-max [\!] #{79.29296875 [0.13671875 0.0546875 0.046875] (:intervals-liftstack #push.type.definitions.complex.Complex{:re 965469040, :im 43.8984375} :char-whitespace? :booleans-reverse "ÂÀQ%eÎÅ%") ("0áPå·j¦âX¢=:N¢~Þg" (51.39453125 83853775M :input!1 #push.type.definitions.complex.Complex{:re 706525770, :im 228.16015625} #push.type.definitions.complex.Complex{:re 139872944, :im 292.36328125}) ["Õ[/`$ª Y#Æqs\\&¢" "Ìbyw·]æµHdÔ1\"IN´" "Â" "ÔÈ<QÈbwÃ8Þ" "0¼]â=OÄvÀ«V¡¯" "¯°Ï6¢¢+B"] #push.type.definitions.interval.Interval{:min 67/70, :max 186.41015625, :min-open? true, :max-open? false} ([37 2527 4173 4423] :tagspace-valuesplit [\[ \Ñ \9 \ª \u \¾ \Ä] true :set-storestack)) false :boolean-later [\´] #{[0.05859375 0.13671875 0.0 0.0703125 0.05859375] :scalars-stackdepth \, :chars-byexample true :intervals-dup \Õ \{}} :exec-k 384.59765625 :scalar-equal? :string-contains? :chars-contains?} :ref-flush) :complexes-generalizeall} :string-empty? [false false true true true true true true] :tagspace-return-pop} [0.11328125 0.05078125 0.06640625 0.09375 0.1328125 0.02734375] false #push.type.definitions.interval.Interval{:min 53/156, :max 149.90625, :min-open? true, :max-open? true} :string-flipstack :scalar-reciprocal :set-intersection :vector-tagstack :code-do* #push.type.definitions.interval.Interval{:min 156/163, :max 334.88671875, :min-open? true, :max-open? true} :intervals-sampler [2368] :booleans-tagstack [0.015625] 305400773M #push.type.definitions.interval.Interval{:min 5/13, :max 267.5859375, :min-open? true, :max-open? true} :strings-savestack (:boolean-not 137676988 :strings-butlast :tagspace-values (:strings-reverse #{#push.type.definitions.complex.Complex{:re 324959517, :im 25.53515625} [\Î \# \Y \Ø \B] :tagspace-cycler :booleans-sampler :exec-later "«;L-e-µ²ÚÌ¹¹)6R" [0.07421875 0.04296875 0.1484375 0.109375 0.01953125 0.09375 0.1015625 0.12890625] ["xyÒÓ1Öc9Û.MÙIÎ¢Oh" "7L2Q¶â,À" "×râO<U*" "­" " .)/j]2NÎ"]} :set-yankdup [1656 1753 2975 4760 4959 82 1597 3186] [2506 516 2410 494 4779 2868 3950])) :generator-swap #push.type.definitions.complex.Complex{:re 455627155, :im 81.26953125} #push.type.definitions.interval.Interval{:min 188/155, :max 87.36328125, :min-open? false, :max-open? true} :booleans-vremove :booleans-cutflip [4653 3032 4982 1496 1656 2174 1055 2408] :boolean-rotate #{:intervals-nth (#{230408031M :scalar->code :complex-yank true [229 3905 812 2836 4490 4613 4823 2476] (118/45 :booleans-fillvector "ÃZ)±=Çz@¯>Ù~" 945241288 :intervals-vfilter) :char->integer :input!6} true [1651] :complex-shift #push.type.definitions.complex.Complex{:re 951399970, :im 159.8125}) :refs-byexample :tagspace-flipstack :chars-save :ref-tag :strings-flipstack :string-comprehension} :complexes-cycler #push.type.definitions.interval.Interval{:min 37/130, :max 195.4453125, :min-open? true, :max-open? false} 105/128 #push.type.definitions.complex.Complex{:re 949586358, :im 125.37890625} :input!8 :exec-echoall :chars-reverse #push.type.definitions.interval.Interval{:min 177/98, :max 198.90625, :min-open? false, :max-open? false} (#push.type.definitions.complex.Complex{:re 670662906, :im 176.36328125} :exec-liftstack :set-return-pop :strings-cutflip :input!3) :scalar-in-set? :complex-cutflip :booleans-shove "Va_y¾KÎ?" false :set-cutflip (5/3 :scalar->set #push.type.definitions.interval.Interval{:min 196/113, :max 196.16796875, :min-open? false, :max-open? false} :input!4 #{:set-stackdepth #push.type.definitions.complex.Complex{:re 267439287, :im 137.01171875} :boolean-swap :refs-vsplit "Q.R¡Ïµ¹9ºº\\T0" 90344144M #push.type.definitions.complex.Complex{:re 960434280, :im 40.6484375} :complexes-pt-crossover}) [0.05859375 0.00390625 0.12890625 0.0234375 0.03515625 0.125 0.07421875] :chars-butlast \¨ [2205 1974 316 1619 2109 191 1605] :chars->set #push.type.definitions.interval.Interval{:min 29/36, :max 160.65625, :min-open? true, :max-open? true} :ref-conj-set #{155/114 \* :char-againlater "¦Â!" #{:exec-do*while (:boolean-or :char-later :refs-byexample 970695281 :snapshot-empty?) true :code-null? [0.06640625 0.15234375 0.05859375 0.12109375 0.015625 0.0625 0.05078125 0.03515625] :boolean-flipstack :generator-tagstack [3139 1079 476]} 564649869 #push.type.definitions.interval.Interval{:min 127/53, :max 94.5546875, :min-open? false, :max-open? false} #push.type.definitions.interval.Interval{:min 89/177, :max 156.640625, :min-open? false, :max-open? false}} #push.type.definitions.complex.Complex{:re 348889415, :im 371.5234375} :generator-cutflip :intervals-vremove :refs->tagspace :refs-replace :boolean-stackdepth :tagspace-remove 67/89 :scalars-indexof #{[false true] :input!1 #push.type.definitions.interval.Interval{:min 2/17, :max 199.16796875, :min-open? true, :max-open? true} :complexes-conj #{:complexes-nth "8" "ºQ(" false :strings-liftstack ["ÈQif²Øtºr:TR³·b" "M\"¤<Â8¶Q~=¢hgÀlsã" "'Á¬uw×'K£@ ª³z"] :vector-vremove :input!6} :vector-return :booleans-generalize :scalar-tag} [0.08984375 0.09765625 0.0625 0.15234375 0.00390625 0.125 0.09375 0.140625 0.05078125] :scalar-flipstack false ([\S] [true true false true false true false] \ #{36.8046875 :vector-savestack true :ref-later 9/124 #{:exec-do*count \b #push.type.definitions.interval.Interval{:min 51/35, :max 240.1015625, :min-open? true, :max-open? false} :refs-rerunall 960743911 :scalars-savestack (:exec->string :refs-empty? :char-in-set? \Q 968696034) :string-comprehension} \ :vector-echo} :complex-liftstack) :vector-contains? :chars-dup \l :refs-intoset #{119851294M ["Jxn£¿·mËÒÐ9uÑ" "àoJÓKÑæUÚ¶yPo[" "¼" "°<©" "&YFOÇ" "²µÔ<" "ÙL@f" "·¿9ã3`"] "ÆaÑ@¦³©obM×\"R©" "ÁBÂÑâ" [] :boolean-empty? :set-rerunall (#{:scalars-tagstack :code-do* 198/173 [".)E¶Á´Q," "×ÓKÝ3{¥y°ÊáÄâ[" "h½iÍdÓ3²&ÒA Û3Ç3Ô¦" "®Ïa9ÅÔn" "pÞ\\±ËÛ-" "kN"] 977531308M #{:char-flush :ref-empty? :push-storeARGS "/z»±TZ\"ÎÜfÂ" [true true true false] false \· :intervals-concat} \X :scalar-complexify} :vector-vremove false :intervals-tagstack :snapshot-store)} #push.type.definitions.complex.Complex{:re 653081596, :im 84.4375} :intervals-vremove (:string-cutstack \Ò :complexes-conj-set 31/26 :code-do*times) #push.type.definitions.complex.Complex{:re 974393352, :im 269.3828125} :refs-last 77919632M false :exec-cutflip [3369 4134 2878] :refs-portion ([true true] :intervals-set :complexes-equal? :refs-items [4294 4708 1745 4184 2740 2132 4753]) [true true false false false] 228.07421875 [0.109375 0.1484375 0.10546875 0.09765625] false [2833 4940 2061 1279 31 4760 2043] [0.03125] (:intervals-portion :exec-tag :scalar<? :ref-lookup 218670686) :refs-intoset (:scalars-generalize 200.12890625 :booleans-portion [true false true true false true] [3603 1881 3274 4571 3423 4497 2014]) 768731077 [0.10546875 0.12890625 0.08984375 0.14453125 0.03515625 0.05859375 0.078125] :chars-equal? 838473645 :booleans-length [\g \\ \¬ \+ \] \o] :exec-flipstack :chars-new :strings-do*each [2760 251] \È :complex-add #push.type.definitions.complex.Complex{:re 293901560, :im 158.98828125} :interval-liftstack :booleans-intoset :scalar-power :exec-when \l :intervals-notequal? 282.65625 [\ \ \ \[] ["P¢ØáP*ÙI" "^È" "W0¶@GA3Ìru\"fh¯"] [] :ref-tag :exec->string :snapshot-liftstack (:scalar-max 25/18 :complexes-do*each [\ \ \ \{ \Ý \ \¨ \Ì \À] (193/37 313329831 [3806 3514] :scalars-shove :boolean-flush)) [false true] :scalar-infinite? \ ("v})t9" [\Ò \ª \« \' \S \* \Î] 15.65625 \I :refs-generalizeall) #{[] [0.0546875] (["DÞIË¿" "ZuÛ¥,u¬æ" "¶¡~" "´Ô" "KY­åQd²>-"] :booleans-print :strings-echo :refs-fillvector :code-later) ["AÑ-90Á},~½o¶nÜ³" "=r¦]g¥(" "jÃ" "O" "zÐ§&ÕYo3y" "åÀB²C¸*p)QÄ|£+[¢" "Ì" "DH)W®q,ß&eÄ;"] :strings-emptyitem? :interval-include? :char-flipstack :string-cutflip} [1789 2790] 8355866 :complexes-vsplit :string-sampler #push.type.definitions.complex.Complex{:re 733001355, :im 319.56640625} :string-flush #push.type.definitions.complex.Complex{:re 944020218, :im 390.39453125} [\ \® \6 \d \Ü \F \¯] 537607817M 126.51953125 :tagspace-sampler :strings-portion false #push.type.definitions.interval.Interval{:min 125/72, :max 122.796875, :min-open? false, :max-open? false} :booleans-tagstack #{\" #push.type.definitions.complex.Complex{:re 885743697, :im 262.1875} [] :chars-byexample \ false :boolean-not [0.0078125 0.06640625 0.109375 0.0703125]} "=°¼Ûs!yDx" [0.12890625 0.02734375 0.0703125] :chars-replace [false true true false true true false] #push.type.definitions.complex.Complex{:re 88888465, :im 40.62109375} #push.type.definitions.complex.Complex{:re 12889276, :im 125.21484375} #push.type.definitions.interval.Interval{:min 25/27, :max 240.328125, :min-open? true, :max-open? false} 15.41015625 :scalars-emptyitem? [true false false] 470693362 :set-echo :vector-conj 252.1875 false]



    :bindings 

    '{:input!2 ((:boolean-rotate 304670197M #{144.0546875 [false true] :refs-yank :complexes-cutstack [false true false true false false false false] [\{ \ß \] :booleans-cutstack 103624004M} "8¹#¤]!]>Åf¦?" true)), :input!9 (:vector-remove), :input!3 ("ÈãÓ&`¦\\4u"), :input!10 (:tagspace-notequal?), :input!1 (\), :input!8 (#push.type.definitions.complex.Complex{:re 313128268, :im 20.47265625}), :input!4 (:complex-equal?), :input!7 (1/9), :input!5 ([2176 3991 780 750]), :input!6 ("½¼Ç«{")}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

