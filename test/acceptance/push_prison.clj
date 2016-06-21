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
                            "\n items on :vector " (u/get-stack s :vector)
                            "\n items on :code " (u/get-stack s :code)
                            "\n items on :scalars " (u/get-stack s :scalars)
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

    '[:strings-echo :scalars-last :chars->tagspace 350.64453125 :code-cutstack :booleans-savestack #{:vector-rest :string-sampler [\] :code-yank :set-return-pop 6/107 :string->chars :booleans-echo} :log-empty? 867298044M :scalar-yank :vector-take :char-shove true 958455926 822452648 :chars-yank :string-shove (["¤È^Ô^`1fã©·¥" "F"] 182151932 "mCcJ¤Q¶}¤°K68d" [0.00390625 0.01171875 0.01171875 0.125 0.11328125 0.03515625 0.0078125 0.08203125] :booleans-generalize) #{"L)XÆn µÞÈ*" :code-length 28322161M [\? \Á \( \É \× \Ô \ \F \e] :scalar-yankdup :string-rotate :generator-stackdepth :input!6} [868 565 272 4081] :scalars-do*each "´¤F´«9¶M°§:­" :code-points ([true true] :exec-string-iterate [1955 1738 1085 3174 1056 171 3454 2733] ["jÑooåS" "PÊ+kVÊY¹bF" "­_Ña»sÒÜqCÕG)S«Ùà" "8pO" "Åd" "J#I1" "Z!àARªÄVÀÀ_k3{¢1Å"] true) :refs-sampler [0.046875 0.0703125 0.125 0.09765625 0.01953125 0.01171875 0.046875 0.10546875] :scalars-contains? :boolean-faircoin :ref-flush [0.109375 0.08203125 0.01171875 0.125 0.0859375 0.0390625 0.0703125] :refs-rotate 791533579M :vector-indexof [false false true false true false false true] [\¨ \B \»] [\O \¸ \J \p \E \t \S] "Ä$!ám+°h3PP*" (:scalars-byexample :code-insert #{[0.09375 0.125 0.0859375 0.078125 0.00390625 0.078125 0.03515625 0.109375] :booleans-remove \+ [true false true true false false true] "=3-9´8ag®JxZ+" 81492511 :exec-empty? :input!7} :ref-save 343.27734375) #{:set-subset? :generator-againlater [\[ \  \d \3 \ \space \â] 87645038M [4250] false :scalars->code :strings-emptyitem?} :char-echoall :tagspace-echoall 245511656M :set-storestack \} :generator-swap :ref-rerunall 4/181 " )K¹)5çJf~Yà" #{258837375M [true true true true false] :vector-return-pop :booleans-shove :string<? :char-rerunall :char-rotate [true true false false]} :vector->code :strings-reverse [750 2693 965 3503] :chars-return :vector-do*each :code-wrap #{34.890625 :integer-uniform :exec-rotate #{#{106.11328125 #{349458541 645388827M :string-yankdup 187/147 :scalars-echoall true 385.71484375 :refs-shove} 826434074M \ [true true false true true false false false true] :string-replacefirstchar :scalars-nth :input!6} "â*Wt6º" :chars-echoall [1309 1909 4887 2195 2762 4078 1113] [3403 647 2506 4064 1751 1386 3898 1793] :chars-first "o)" :scalar-yankdup} :strings-portion :vector-new :chars-sampler :code-do*count} :code-return-pop 235.4140625 474687298 :tagspace-cutstack 241771296M :vector-againlater 110224535M 9/146 "UV¦¡;YÔL" :set-rotate :refs->code :scalars-yankdup [0.11328125] :scalar-modulo [\ä \ \~ \[ \Í] :vector-savestack #{["¾)ã¦" "§\\²}¾o«J" "[å"] ["TÒV¨H~°Úå" "åË" "»Bã\"ÍßÃ" "c\\àÎÍÌ6Ò|" "" "çv¼O®Ì" "(Þä1" "i²7xBuc9Ð?Ô"] :boolean-pop [\D \³ \ \ \@ \ \z \Â] ["Ý!á5:KtToÖÓARDiJ" "BOv©ÉÚÍX¿f" "W#ç¯Àh" "g¼«FÀ¬xÃHQH}@"] :boolean->integer 41/23 :generator-cutstack} :exec-later :exec-tag "R;9[iÐÀK4¸ " :tagspace-merge [] "}" \Ó :vector-later :strings-generalizeall :set-difference 15/41 [0.0546875 0.13671875 0.04296875 0.09765625 0.046875 0.02734375 0.10546875 0.08203125] :ref-swap :chars-storestack :input!5 false :vector-nth [true true true] 152.3828125 #{((491801855 :scalar-echoall "2ã<år(>?U»" [true false false true false false false false false] :booleans-save) :tagspace-lookupscalars :char-flipstack true ["A¡ÚOJ\\r¯iÖdÇ" "CP²¨c¹HyÞDá}Ù Ò" ")BÞ¯4­$C´sCOLS" "©vw*`XºØsÌ" "\\¦©"]) 781087287 true :scalars-storestack [false true true false false false] 42/61 :tagspace-print "JY%Ê»É¹ rC3¯kv(¥%"} \; :scalars-rest :boolean-print :code-comprehension [3193 4943] :tagspace-return :scalar-floor :booleans-storestack 166.73046875 155/83 :string-first "ÆO3ªO*$.Æ*\"oÁ" ["Þ>bß}6ØuXQ" "KP+ÖhÛ!­Ü@" "O{" "©·rã" "(VZ?.ÐÝSK#K&Ý6pBRµv" "A%câ{NÀbÕÜ8R]ÌÙR³C" "2Ä" "°©Àp/ÝÁ¸³ÈÐm&>"] [0.02734375 0.078125 0.11328125 0.01953125 0.10546875 0.07421875 0.0390625 0.140625 0.08203125] :scalars-comprehension :string-return-pop :vector-contains? :refs-conj :strings-pop :tagspace-keys [0.015625 0.11328125 0.12890625 0.08984375 0.10546875 0.1015625] :ref-storestack 345648546M :refs-butlast :refs-rerunall :booleans-pop [] 457601530 :string-replacechar :booleans-new 75.12890625 false (:generator-echo #{(:scalar-arctangent ["mÄÆáÃH²" "*d-" ")«³BàH© 4(|"] ["Â[i0" "y " "¨j^" "Ô" "Þy·ud" "T\\"] :tagspace-tag [0.078125 0.109375 0.01171875 0.08984375 0.1484375 0.0234375 0.0625 0.0703125]) :set-storestack :scalar-sine ["6&/-P¥Ï#<ÔDhÛÛz" "â8O\\×yD¹ÅÙL" "Z 1 \\]È§" "S!z%huP:ls×" "zÌ`FBÊ" "(¿ÏH|n#}¿`82" "T4Ú)$5Y>j×È]"] :vector-storestack 864671892M 174185315 846698600} :scalars-shatter :booleans-save :input!9) :exec-k true :booleans-length 13/3 :generator-reset 458896489M :set-flush (\³ 119.78125 143/199 [0.078125] :ref-store) :code-list :tagspace-rerunall [true true false true false false true false false] (:string≤? :booleans-set 349673988 136/161 :boolean-echo) 227928038 :chars-shatter :code-first :scalar-modulo :strings-print 200.74609375 :generator-swap :code-empty? :chars-liftstack :string-indexofchar :scalars-pop false :code-nth true :vector-echo :generator-cutflip :vector-dup :booleans-sampler :chars-cycler false 761227810M :scalars-againlater :char-flush :strings-equal? 99/37 :code-quote [] #{:strings-shatter :vector-shove :char-swap true 336417457M :generator-savestack :strings-flipstack 490283746M} "%×ss<Ç=ÝÏ:k" :strings-do*each :string-length 216757835 :input!9 :scalars-store :vector-sampler :booleans-tag :vector-swap [\¬ \a \Ë \] #{65/2 :exec-return-pop :input!9 :error-empty? 599929249 (:chars-concat 239092062M :char≥? #{342.0703125 [0.109375 0.10546875 0.0 0.03125 0.03125 0.1328125 0.08984375 0.09765625 0.14453125] [\Q \ç \å] 26192849 false :strings->tagspace :code-liftstack 732859688M} :print-space) :generator-echoall} \» (165/137 [0.14453125 0.08984375] :boolean-equal? :refs-do*each [false false false true true false false true false]) \â :generator-counter 226402964 :input!2 :refs-occurrencesof :scalar-ln [0.0859375] :char≤? :refs-do*each :strings-butlast :set->code :input!5 :chars-last (7/82 59/75 1.8046875 69741738M []) ["ÔÃ9¤QTn¥~<-¨V¼" "9×" "d¼¤£BßBDà£Çå¡y^vÆ&" "'IC,Y¯ZÊâ $µâ|Ü \"" "$" "@pMoGÊÉ" "S´¬SÜkSDÕ´¥8" "6<:dæ5ÄµZ}krF¢" "%K*¶©¢iNßIkÇ2¬]³A"] :set-savestack :boolean-cutstack :scalar-return :char-notequal? :char-notequal? 50.234375]



    :bindings 

    '{:input!2 (["·fL&Ù 7*×æ´È)t" "·p" "®W)W«ÀrÞ0 ËÅf­¢E»" "Ö%®UÎiÇeÅÛY|$Ç«"]), :input!9 (:strings-return-pop), :input!3 (:vector->code), :input!10 ([0.015625 0.125 0.0390625 0.08203125 0.09765625]), :input!1 ((["½ÀbJ§A6\\Ô" "&-oÏYÌÀÔ_ 4Hã¶§ux-" "¡å½rR" "Ó9mÈ0T´Üm°yLä3" "RFv¯È$2ÁHªY½O" "¡UmÂ>ádÈ§h" "EHÍ" "ËU²agÖ" ";U§DªKÏ"] :boolean-rerunall \e :string-later)), :input!8 (363.42578125), :input!4 (["k8" "}8,Ï{¸·nlZr¡Õ}D" "¡ÁÖ8k`¡d]Ë" "»£¹wi]ÞËz×á" "b.ÙD\"â±(3»" "ÒÜ·6°¶©#" "³5§x"]), :input!7 (:vector-cutstack), :input!5 (\X), :input!6 ([false true true])}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

