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
                            "\n items on :scalar " (u/get-stack s :scalar)
                            "\n items on :complex " (u/get-stack s :complex)
                            "\n items on :snapshot " (count (u/get-stack s :snapshot))
                            "\n points on :tagspace "
                              (fix/count-collection-points (u/get-stack s :tagspace))
                            "\n points on :snapshot "
                              (fix/count-collection-points (u/get-stack s :snapshot))
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

    '[:ref-clear :complex-later :ref-liftstack :string-print :exec-cutstack :input!9 :scalars-rotate true #{125.28515625 126.9921875 :snapshot-againlater :chars-print :generator-echo :booleans-return-pop :set-save :input!5} :exec-later 420025503M [\Æ \ \ä \Ä \w \ \À \Õ] :string-return (:complexes-butlast 676870095 :complexes-generalize :code-againlater :scalar-inc) 906469391M [0.01171875] ["N+" "°¨§}Ä" "eE$I·Pµ^5názçÑ:Lh" "° *º" "h_QÖk" "©¢wG$" "HÁ" "~ÊK"] :boolean-equal? :refs-swap ["Ú`Ð25æx­å" "m,¹ÔE~1G«Dâ@u" "Ö>E«f" "^P`Ê(U" "65ÙlÕm[ÕXk¿±Ô'·"] :code-store \Ì [true] :complex-stackdepth :boolean-or :strings-do*each "Ü~,ÑÉ<}D]¸º¤ä" :chars-cutstack :code-list 308.0234375 [false true true false true true false true true] :boolean->signedint [0.046875 0.046875 0.08984375 0.05078125 0.078125 0.1328125 0.1328125 0.0546875] [2733 4757 2234 2526 4376] [4227] :generator-return-pop \Ó 234722639M :char->code [0.09375 0.1484375 0.0859375 0.08203125 0.08984375 0.0703125 0.0] :exec-later #{:string-stackdepth ["P­" "w|~¦ÔbCÀBär" "E" "Ñx¥iÝ_4Y¨O¾£vÁ"] [4761 1475 2724 3462 4241 3342 580 2883 4851] (#{:strings-stackdepth #{149/52 [0.0703125] [\ \ \Ì \} \ \ \Ñ] :push-bindings ["S>WxD-3V×!R+" "=Ù¨<.ÍmO]]kF¬¿£w"] :strings-rotate :input!5 "¤+=x]Ä"} [4063 379 3613 3045 4793 4651 1200] :scalars-take :char-stackdepth [0.12109375 0.07421875 0.10546875 0.0234375 0.02734375 0.0390625 0.13671875] :boolean-echoall 271872500} true :chars-occurrencesof :exec-do*count :push-bindings) :refs-stackdepth ["»_ÔAÍ" "Õh" "qÊÖW" "ËÔTÝ¿H·æMs*·ÒÖË»-C" "µX²,¯>ÀÂh" "B}+4>VX¡ÔÇªv"] :string-yank :input!5} :tagspace-rerunall :chars-print :refs-liftstack :vector->code :chars-sampler :complex-yank [0.1171875 0.0859375 0.1015625 0.1328125 0.1171875 0.0390625 0.125 0.015625] :strings-save :code-dup :refs-build 206367808M :snapshot-swap :strings-shove [0.015625 0.07421875 0.09765625] :scalars-savestack :vector-butlast :chars-portion \h (:complexes-do*each ["\"§×Æ,`:ÛeäXÐ" ">r#d\"&Õ7°^¬UÐº" "qâ¼@li{Õ" "ã»1Ö6âÆvÀ4ÕÖvG[0 b¼" "!Ç_¥`Æ«a6nªea|tÔz?'" "Ì<ÑÏ©çXØR-ip" "6¾º²"] :booleans-rest #{:scalar->code [1721] :ref-pop [] :refs-rotate :char-stackdepth ["]=»ªGqZ:9½Ù[xÄTÍ\""] :string-solid?} :scalar-reciprocal) :vector-reverse :booleans-store [0.05078125 0.11328125 0.1484375 0.06640625 0.12109375] \ 486531568M :complex-store 122.265625 (false [850 1745 4232 261 4509] :complex-yankdup :vector-indexof 369.59765625) :complexes-later "&à|Ô×£á¤6kË{s;²UmÝ" #{:code-sampler ["UNvdNÐ©¤cfã©" "á" "j¹¹ã²mN" "fºä" "»X§FÐ=câÙ®^Æ)ÒR+"] (:refs-butlast :scalars-replacefirst :chars-flush ["5»" "|aÈ0E" "u¬à·ÍÄ¾¯¿·á©}" "7©r¸¥¢v -° 1b" "ÞÎDMÇ·iW(äbÌ" "lvRqÒ®â" "Hª$æ1"] :scalar-shove) [4871 3709 1815] \µ :booleans-comprehension :vector-echoall (:booleans-cycler (:snapshot-yankdup [] :string-rotate :code-do* :string-print) [4514 1628 2] :exec-pop :chars-cycler)} \Å [false false true true false false false true] 164/135 :boolean->code :vector-later 90.8828125 :chars-liftstack [true true false true false false true true] :string≤? :booleans-equal? :scalars-generalizeall :refs-savestack 337.26953125 [\e \Ë \É \ \u \g \­ \ \ ] 577419746 :complexes-last \[ (:refs-return :input!8 false :exec-empty? :booleans-swap) "¤QZnF¿)l" [1400 1351 3096 746 2451 4202] :ref-save :scalars-emptyitem? :char->code :set-dup 57385572 [0.12109375 0.0546875 0.09375 0.0703125 0.04296875] :chars-return-pop "´ÕÛ$F²[ÛÃOØ" #{:code-return-pop :strings-generalize [\b \a \» \Ð \± \6 \Ø \i \H] [false false false false] (:set-difference [4908 1935 2799 1466 117] :complex-store "wEWZªYÛ¢Ùr¢[?i!)" ["Â´³x.zâ" "É:aÀA±-0¥@¾_5ÜyÇ" "ÈE¯°½ à9¡gÍ)EG3"]) :strings-replace :chars-rotate :booleans-conj} 185.69921875 :strings-storestack :complex-empty? :exec-do*times \y 177.63671875 :chars-new (:refs->code :chars-echoall [0.02734375 0.0] :char-return-pop :complex-divide) [] :char-flush :code-yank [true false true true true] ["" "Ú,·´Z+ P^" "Oå?" "U" "wr)ØÅ>Lb(" "­^½·Æ'" "Ä&9cØ³A|qu!"] [] :vector-pop 686518339M :complexes-againlater \q [\O \§ \G] #{\Â \E :exec-rotate (23/119 :set-difference :complex-flush 127/197 [0.0703125 0.015625 0.01953125 0.10546875 0.11328125 0.1484375 0.0859375 0.12890625 0.01953125]) 3/8 \¯ :scalar-rotate :complexes-do*each} :set-flush :chars-return-pop :complex-equal? :complex-pop [] false [0.14453125 0.08984375 0.14453125 0.01953125 0.13671875 0.109375 0.11328125 0.0703125 0.03125] [0.140625 0.1171875 0.125 0.1484375 0.06640625 0.14453125 0.046875 0.078125] :char-return-pop :code-append :booleans-portion (\Ö :set-comprehension :chars-yank :code-dup :input!2) [true true false true] true :complexes-yankdup :ref-exchange false 259.58984375 :refs-generalizeall \` \Ü :exec-do*times :scalars-first (:set-sampler :exec-store :booleans-byexample [] false) :input!10 :exec-if 410875841M 168/163 :scalar-notequal? :complexes-remove [true false true false false false true] :boolean-cutstack 14/61 "rÞgç_~¡ÑN" :input!5 ["sÖÃ" "l" "ÄÉvµ¤. i" "MÊÎÕ p" "&­|ch%ºÌnc"] :string-stackdepth :vector-shove :tagspace-notequal? 541666079M [0.1328125 0.09375 0.0546875 0.078125] :boolean-liftstack 495404901M false 289311462 :code-points #{:complexes-cutstack :refs-remove :booleans-rest :set-intersection :chars-flipstack :string-flipstack (:strings-cutflip 27/11 :snapshot-tag [true true false false true true true true false] "Ö)zã¯²Ê6çW¢«ÊÆD") :strings-cycler} \ ["¥Iq6Z>â$#" "R,>°Þª7-£²L.·'Éªæ$\\"] :code-pop [] :ref-equal? :vector-shatter :refs-first :input!1 ([\] :scalar-dup [] :input!8 #{888958848 \f :booleans-generalizeall :scalars-conj :boolean->code :ref-save 411541701M :string-echo}) 302.1015625 :scalars-store :input!1 :set-empty? :string-solid? :strings-later :tagspace-save \ #{:code-list [] :vector-take 61/169 ["xyÑhyµ:i$·u¥" "[(e¢Ówo`aÐ'¡âË.àJ*ÖØ" "Id¸/Ô¸ÖFVrE¢ádÏÌ" "ËÊ" "WmÛ£zSse<²)×LbB#" "44F¯D7ÜÂ¤ÊÚxã"] :generator-jumpsome 59/142 771126687} #{:string->tagspace [] :code-savestack :code-null? :booleans-flush :scalars-remove [false] #{:string-stackdepth :booleans-tag ["Ê" "ÞYJP;94¤Û+ÃHÔZ" "NQØÂ" "3ÛªvÛLÖºxtu" "´5>,Â^Àãæ}º{&Ö" "[»j1¡r¢µº¾" "KØ" "arÄ£)9«æ[>]u¡"] [true true false true true true] :strings-flush :scalar->boolean :scalar-liftstack \ß}} :vector-reverse (:snapshot-yankdup 20.66796875 :code-later [0.04296875 0.12890625 0.15234375 0.046875 0.0546875 0.05078125] :booleans-store) :ref-yankdup ["JÄ»R>wdÁC" "Ý/Æ" "t" "Ö}TÓyb" "¤ÄÑzl}µ@ÅKÍÍ°o<À" "P3h¾OªTÄ2Y@x.Ò" "psv¬x9Ê­ÜC¨" "Ù4¤z" "=j"] [\¥ \©] [false false false false true false] :complexes-againlater #{245.890625 :complexes-emptyitem? [0.0078125 0.05859375 0.03125 0.01953125 0.01171875 0.0] :set-intersection \Ô :vector-flush 139752234M :char-flipstack} :booleans-contains? #{137.18359375 :generator-rerunall 42798650M :input!10 ([] :string-empty? :booleans-flush :refs-contains? ["°0Z²b"]) (:ref-dump :complex-reciprocal [0.12890625 0.04296875 0.1015625 0.08984375] (:ref-empty? false :ref-dump :boolean-storestack "-9$\"Ê[]0½") :input!10) :ref-savestack 763547172} #{171.390625 :refs-dup :vector-flush :scalar-float? :tagspace-values 149812303 :strings-rest [4007]} false [0.0703125 0.0390625 0.1015625 0.125 0.11328125 0.04296875 0.078125]]



    :bindings 

    '{:input!2 (#{255.66796875 :boolean-save :generator-swap 268316403 \l :char-tag :strings-echoall :refs-take}), :input!9 (:ref-print), :input!3 ((:tagspace-values :booleans-savestack [3275 4313 3052 2556 1216] :complexes-echoall :scalar->boolean)), :input!10 (:complexes-take), :input!1 (:refs-rotate), :input!8 (true), :input!4 (:scalars-echo), :input!7 (:boolean-yank), :input!5 (:code-cutstack), :input!6 ("ÛuKc8ÒP^¸#X")}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

