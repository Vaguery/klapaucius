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
                            "\n items on :code " (u/get-stack s :code)
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

    '[:refs-swap [] \T [2896 2422 680 2051 4285 4638] 146.58984375 \¦ :strings-yank :char-yankdup ["ÆºT*É>" "Ð"] :boolean-againlater :tagspace-dup :vector-emptyitem? 41/8 :booleans-remove [] #push.type.definitions.interval.Interval{:min 4/45, :max 289.98828125, :min-open? true, :max-open? false} :vector-vremove #push.type.definitions.complex.Complex{:re 714720463, :im 194.19921875} [\k \| \ \\ \}] 875299204M :boolean-pop [0.10546875 0.09375 0.13671875 0.1171875 0.015625 0.0703125] 460681559M :strings-save #push.type.definitions.complex.Complex{:re 429077686, :im 70.3984375} true :set-cutflip 282.2734375 (:vector-yank 175/44 507742574 301839036 40183353) 198.48828125 524874324M #push.type.definitions.complex.Complex{:re 999595954, :im 319.53125} :string-againlater :input!10 :complex-dup ["Ýe·6|W°#Ï{â:ncÂßr_H" "" "¾*^Ø:^°" ")<'¿&?«SRª'Ñ" "r9&Ë" "ÓÃ.²GEÜKk¯" "!^?U°Ä&Âd§\\{N" "Id0#j«¥(ÊK]ßrhMp6\"U"] :code-cons true #{333.0859375 :char-max :vector-items :strings-do*each :exec-string-iterate 18288076M :scalars-cutstack 811892392M} [3018 1800 3520 4490 73 1449 2396] :generator-shove :set-yankdup #{:ref-clear :booleans-stackdepth [600] [] #push.type.definitions.complex.Complex{:re 571153181, :im 350.12109375} true #push.type.definitions.complex.Complex{:re 565711502, :im 330.2578125} false} :complexes-rerunall [\» \+ \Ç \Ô \B \(] \« :interval-in-set? :string-cutflip ["Å\\ãÒ[NÃÊJ" "µ'~" "+¾s?â77>d'È" "I&æÞ\"~ÑÜÇTJ \\" "Ä·/ÇßJá¡#æ"] [2108 4239 133 363] :refs-shove 34/35 :char-tagstack #push.type.definitions.interval.Interval{:min 27/35, :max 324.37890625, :min-open? false, :max-open? true} ["8@mGÛÍAÍäuÊvn" "à" "5­B§jÚ_±äÃ$»_" ""] :vector-conj 495412103 288.40234375 :scalar-max 59/151 :refs-in-set? #push.type.definitions.interval.Interval{:min 30/119, :max 15.390625, :min-open? true, :max-open? true} :char-savestack :vector-portion :float-uniform :complexes-emptyitem? 86/79 [\2 \/ \· \g \- \ \}] :string-flush #push.type.definitions.complex.Complex{:re 492245832, :im 73.6796875} 494054178M :interval-subtract :snapshot-liftstack "SæB¦Vs§«g\"fbÒä'\"" [] :chars-remove 110024169 ["ÎÝÂ¦SM]aBLª¨" "ÈÜ\\m\\2Ã1" "·a4GÆ9«u`T" "áák?G¤ÕU" "\\'xb¡6" "4ßàâq2}£xwB\"ß¹Ò\"·." "%£Ôi"] 350.83203125 [0.12890625 0.13671875 0.00390625] :refs-pt-crossover "»pvq(¼1{ZÏV¶±TÛ\\7h\"" "ºKÀCÎäD3YÙË2·" ["cÞçJâdÀu§ºº\"" "" "'áåfn6~gdºFÒ}Üy"] :strings-conj-set false [147 3415 2155 287 602 2648 1784] 164/113 :booleans-liftstack :chars-occurrencesof :scalars-equal? 189/193 [0.0390625 0.03125 0.015625 0.1328125 0.1015625 0.0546875] \ :strings-conj :ref-flush [false false true false false true true] :booleans-conj :string-flipstack :char-stackdepth :code-cutstack :boolean-3bittable #push.type.definitions.complex.Complex{:re 58648027, :im 257.390625} :generator-savestack :ref-cutflip (:ref-storestack "»?Ñ^Fy" :code-stackdepth false #push.type.definitions.interval.Interval{:min 15/74, :max 147.74609375, :min-open? false, :max-open? true}) :complexes-generalize :exec-tag :interval-rebracket [\Ö \Ë \W] :push-nthref :string-tagstack :tagspace-filter #{:interval-empty? #{(["F°" "å(N27AhÁ;77 KxV6Ä" "[;ÜÄxeËTS¶!`WE|" "ÚjH#rFi0fÃ" "Ú8" "4ÚI!Zj@@Ç6K" "ÓPÕ®¨" "Gà"] :refs-save true 284714889M #push.type.definitions.interval.Interval{:min 8/35, :max 12.59765625, :min-open? false, :max-open? false}) #push.type.definitions.interval.Interval{:min 15/58, :max 380.75390625, :min-open? true, :max-open? true} #push.type.definitions.complex.Complex{:re 920710995, :im 315.23828125} [] :code-rotate #push.type.definitions.complex.Complex{:re 590638288, :im 189.33203125} :input!1 [true true false false]} :vector-cutflip [false false false false true false false] :input!10 [\2 \Ì \Ó \< \×] 19/25 :generator->code} :input!7 37/195 :interval-swap :intervals-liftstack true :scalars-do*each [false false false false] :scalars-storestack :string-removechar #{760112767M :booleans-indexof 18/89 #{#push.type.definitions.complex.Complex{:re 994935643, :im 192.18359375} :complexes-emptyitem? [] 75167604M #{#push.type.definitions.interval.Interval{:min 17/38, :max 87.9765625, :min-open? true, :max-open? false} :error-empty? :strings-yank [true true false true true false false] #push.type.definitions.complex.Complex{:re 959032187, :im 382.15234375} :intervals-intoset [\Ö] 375511420M} 324790162M :generator-empty? #{:interval-savestack :vector-againlater :booleans-remove #push.type.definitions.complex.Complex{:re 506839190, :im 84.9921875} :interval-reflect :strings-occurrencesof :input!8 #{:chars-intoset :scalar-conj-set :strings-save :booleans-indexof "«]gl^(Çi" :boolean->signedfloat ["¶/¯­" "A%¿4tMàÕbä" "b" ";ÜWµÑII3." "aÆß8" "!qªO1ÌÆÎ#²9V" "RÁ"] :input!4}}} :scalars-butlast 16/5 #push.type.definitions.interval.Interval{:min 124/169, :max 47.515625, :min-open? false, :max-open? false} "t%3Zj2s1àÀÙ"} :complexes-last :char-echo 87/79 :code-nth :ref-ARGS :complex-scale ["NÅÎ»äçp" "¦¸ádª´%v.¢d0Â¼u7Ð4" "Z&|ä=·;@¿F" "«Ô" "?cy" "gz4[3=3)«Î?" "yB ÄÑ"] :strings-cutstack :scalar-power false \k "ÕË" :strings-savestack :scalar-ln1p false [\µ] :chars-print 506619403M :booleans-print :booleans-rerunall [3318] #{:strings-store [3810 1190 3264 415 2193 3120 3274 2601] :code-sampler :tagspace-swap 14315311M "D+'lÎÂ" #push.type.definitions.interval.Interval{:min 94/59, :max 356.046875, :min-open? false, :max-open? false} 228500999} 477645276M ([2898 4563 1414] :generator-tagstack :generator-stepper [457 3400 1557 3622 3187] false) :scalars-cutstack (:strings-distinct :snapshot-flipstack [0.015625 0.0546875 0.1328125 0.02734375] :tagspace-values :complex-savestack) #push.type.definitions.complex.Complex{:re 335359285, :im 195.125} :chars-conj-set :intervals-intoset :boolean-later \ 856956052M [3957] :interval-shove :scalars-conj-set :scalar-abs 55/102 #{63.484375 192.07421875 ["Y«DB]±al¹" "äÁÜÖ¢B®ÁIV" "s#i²h§" " d¼Yk_{F6«å?wÄÓ"] #{:set-print #push.type.definitions.complex.Complex{:re 943572432, :im 267.359375} :scalars-fillvector [0.125 0.109375 0.0234375 0.12890625 0.07421875] :tagspace-keys "¼@ÌÚ¤H~8ËÝ¸" :intervals-fillvector 452625365} :refs-shatter :refs-cycler [0.0 0.09765625 0.04296875 0.0546875 0.015625 0.0703125 0.03125 0.0859375 0.078125] :input!7} :snapshot-storestack [4956] [0.0703125 0.02734375 0.0390625 0.0703125 0.12890625 0.12109375 0.09765625 0.03515625] :input!6 81/149 [\a \ \ \ \E] :code-notequal? [1640 108 4699] [\¹ \ \¸ \¶ \Y \Á \Ú \&] #{172.9765625 :generator-counter #push.type.definitions.interval.Interval{:min 191/113, :max 16.16796875, :min-open? false, :max-open? true} [] :interval-recenter [\r \# \] 93/134 #push.type.definitions.complex.Complex{:re 514143220, :im 225.44140625}} [true] [true false false true true] 269263762 23.42578125 :strings-notequal? :code-store #push.type.definitions.complex.Complex{:re 240606752, :im 256.69921875} :generator-savestack [\¾ \? \R \R \3 \Ë \× \Õ \P] ["Ò¨e" "rÑä66äºX" "ß*zÆ7TZ&" "ÓÄHb{8´Emrº¿gäÌá9" ">tK¹Þ½Æ_" "Û4>:å"] :complex-swap :interval-stackdepth :scalar-some \] #{:string-cutstack :vector-store [true false true false true false true true false] :strings-nth :boolean-or :strings-first [111 3975 876 4895 2426] ["}e¸M'ÓT" "u`Î5" "/;ª\"È­£Ô_" "F>ÐD"]} :tagspace-keyvector :chars-as-set :ref-in-set? [0.01171875] :intervals-rerunall #{192/137 35/41 #push.type.definitions.complex.Complex{:re 275773043, :im 139.515625} :interval-flipstack [791 969 3929 2560 4706 3717 4074] [0.15234375 0.1015625 0.01171875 0.00390625 0.0 0.109375 0.01171875] (:scalars-rest :refs-tag [1521 4771 4523 1557 4891 4086 2911 63] #push.type.definitions.complex.Complex{:re 336759459, :im 28.3984375} :scalar-bigdec?) ["ÛÚ[" "sÇx" "5«À" "?1Îl~ g®" "*|ã6¡*" ".MIT¼ÏÜÕt³4Ê" "!Ä)ØXå" "-1ä|¥«µ$j»Ne"]} :code-reduce 638902998 :scalar-dup 119/82 356266010 ([\Y \) \Ý \« \Ì \ã \Ö \ \2] :scalar-bigdec? 901396847 :strings-indexof :push-refcycler) (:ref-flush :input!9 :ref-cutflip :string-butlast :strings-vremove)]



    :bindings 

    '{:input!2 (#push.type.definitions.complex.Complex{:re 881071330, :im 204.9453125}), :input!9 (:code-dup), :input!3 (:code-insert), :input!10 (:refs-reverse), :input!1 (93/73), :input!8 (#push.type.definitions.interval.Interval{:min 36/61, :max 363.921875, :min-open? true, :max-open? true}), :input!4 ([0.03125 0.0 0.12890625]), :input!7 (292.51953125), :input!5 ("&ËÞs±?Hqµ"), :input!6 (:vector-rest)}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

