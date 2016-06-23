(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
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
                            "\n items on :generator " (count (u/get-stack s :generator))
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

    '[["®f^Ô¾" "LwÊ0±Zr¸\"æË´C¤Á8" "¾AQ'³ã¸" "(¶PaÓ0¡;4:g{WO¯" "y&ÞÛmk,2+4>ua©ÝÒ" "tYFà " "1"] [3940 2883 4981 3328 785 3540 286 2154] :vector-byexample 448484856M :scalar-sqrt :code-do* [] #{293.5078125 :complexes-rotate 44/137 [false false true true true] false :booleans-conj :tagspace-empty?} 537323333M 671301091M 931325092 299764497 :chars-print :input!8 "t`/CN" :scalar-dup :exec-cutstack [false false true false true true false true] :exec-stackdepth ["çUWÛ{XÉ§¾Ç¿(Bå" "t¨Ñ" "R\"" "\\¼º¦-º;lçË¼:Z " "i§JÚ" "ºX°Ý.Æ¸Æ!8å0" "YNÒ.gOF¥x¹¨I7" "L!T¥¦¤+@(âr²"] "µàk¾y×IÛÙ?V" :code-do ["¿c°É·+¿£{ç«aÏ" "l%" "ÃÔÉ" "¡ÉÍÌDv<¤jKÀ(ÞP"] 11/191 \K :refs-build 45/52 :complexes-comprehension "u¼yP«'-E¤!Á'w;Ï'" \G [0.0078125 0.06640625 0.09375 0.109375 0.14453125 0.02734375 0.04296875 0.03125 0.05859375] [\¾ \Ï \æ \& \ \u] :scalars-last [\O] :complexes-yankdup 313.67578125 "|¡·-N!¬>ÍV" :vector-reverse [] (#{:char≤? #{170.2109375 :scalar->code :refs-byexample ["¬ÇÑv" "Ö &6\"Ëg ¬:~çW§" "_ØÐÔ'c¿ " " S©O/¬i[Þâ" "0s|\\´4ÊÜtÅP¤lgBA" "W2ÞØÍ*v£t «ªo" ">ç*" "½dWh"] :strings-echoall :refs-store ["â%ß5BÌÁÅáÌÀ" "åAâÁ°qÝÔÃÃ¥z¡«" "3¶~T´[%$³y]." "&Hps¤Ñ^#^kªÂç"] [0.0625 0.12890625 0.08203125 0.01953125 0.140625 0.13671875 0.0078125]} :boolean-stackdepth :set-shove [true true true false] :print-newline [2106 2742 140 4422] :code-flush} :boolean->float #{(41/66 :vector-liftstack :tagspace-return 16/5 [800 1105 1944 2293 2461 545 1058]) :scalar-lots :code-rotate #{:complexes-print [\á \< \M \ \Y \d] [\G \K \`] 352790041M :snapshot-new [true true true] [\5 \| \Õ \à \# \] :push-bindingcount} :boolean-xor :vector->set :complexes-save [410]} [2081 2859 2190 348 2357] false) [] "ØÉ Z+ÁM·7Asm" [0.1171875 0.09765625 0.03515625 0.0703125 0.1484375 0.15234375 0.15234375] [] \& :exec-print :scalars-take 193/67 :string-occurrencesofchar 215.5546875 :booleans-byexample :snapshot-cutflip :refs-shatter :set-superset? :strings-notequal? "@°MS,¨" [false false false true true true true] :scalars-butlast :refs-tag :exec-yank [\ß \Ì \· \s \ \A] 92/83 :scalar-savestack :scalars-dup \2 ["u,>_¦KßÝ" "ÖßÇg7s" "Råd>"] :chars-emptyitem? :refs-cycler [2286 4225 1638 3240 917 2797 3237 571] :input!7 [\ \~ \º \¢ \Õ \¥ \l \\ \à] \T :set-stackdepth [1023 1605 45 3801 1525 1221] :vector-return-pop :input!6 [2219 1214 4006 4316 2870] 138562483 :scalars-shove 14.33203125 :complexes-pop :strings-echo :input!5 :complexes-flipstack :tagspace-merge :booleans-store [\7 \W \Â \¿ \Ë \3 \| \] :string-splitonspaces :string-flipstack [0.0390625 0.046875 0.01953125 0.0625 0.08203125] :string-save :complexes-tag :boolean->code :booleans-yankdup :vector-savestack 355949968 :chars-occurrencesof "}Ä2¹µ+³7ÏÐ:¤ÆÛàÉ" :snapshot-cutflip :code->set :vector-cutflip :scalar->string 136.78125 :input!8 :string-pop :code-yankdup :refs-concat [501 4435 3822 4813 930] :complexes-emptyitem? :scalar-add [\ \¼] :scalar-min [true] [\ \Ç \æ \Ô \1 \3 \S] [\ \µ] :scalar-flipstack :ref-swap :input!9 :booleans-return-pop 270637605 :exec-empty? :ref-storestack [0.08203125] :tagspace-keys :chars-return-pop 63.6875 :exec-y :generator-echoall :complexes->tagspace ["ÞPuKL´" "-0^T©]¤ª¤RN­Ú=gC]" "5Ýæg#ÞnÎs" "Y;_zlÀå¸ã"] :string-save 222.015625 [true false true true false false true true false] :scalar-ln1p 177.91015625 :chars-shove #{:string-swap :refs-conj [\Ø \á \ \2] [] ["}}}å4ºÄÑ" "NsÓVÑ¢ÉU_Ú" ".u5" "AÆ ]±Ñ½vª~®" "x]²¨ " "¹×2\\4Ï"] :scalars-emptyitem? #{:complex-stackdepth [true true false] :exec-againlater :vector-set :tagspace-sampler :vector-sampler (:code-subst :scalar-log10 [false true true true true false true true] :vector-replacefirst 17/9) (:exec-yankdup :complex-add 49717942M 164/43 :scalars-echo)} :char->integer} :code-atom? :push-discardARGS [0.125] [] 649282339M :scalars-print :vector-storestack :input!7 \/ false :ref-known? 533993519M false :exec-return false :refs-cutstack :input!5 498499265M [247 1938 2947 825 1961 1450 2022 508 4549] :refs-replace :generator-storestack 659703882 [\¶ \[ \r \¤ \R \] :ref->code :ref-flipstack :tagspace-lookupscalars [0.01953125 0.0390625 0.0625 0.00390625 0.12109375 0.1328125 0.07421875] :complexes-new :complexes-notequal? (:tagspace-tag :booleans-tag :chars-replace true ["¯ÁjSCC9%k{Ô~" "¦gæk§ÀcÍªt­à©VbH³«" "¨Í×8ã\\!cCPs" "½?`t·M^l4" "µ!fÍãª_M¤f¿²I­78<'" "%Rja¡5ä§Pic¥ºÈZ" "å·Â£^SÛ2Ð" "2Cç{¹Æ¿¹KvÙ" "Å¢(3C*Á9ap¦odÃÍ"]) :snapshot-new 452483708M :scalars-cutstack :scalars-flush :strings-build :exec-sampler 41169269 :generator-stepper :scalar-return-pop :tagspace-tag 603462467 :boolean-arity2 :push-storeARGS :log-empty? :boolean-arity3 :code-pop true :string-return-pop :ref-exchange :booleans-first 5/6 :booleans-swap :set-shove 172410281 :booleans-new (785740557 :string-last [4540 3255 4407 3610 4276] [0.14453125 0.0859375 0.05078125 0.08203125 0.1015625 0.078125 0.0078125 0.10546875 0.0625] \a) "»" [\0 \½ \Á \V \ \A \f \&] \k :string->code :complex-dup :generator-cutflip 391392013M]



    :bindings 

    '{:input!1 (:complexes-first), :input!2 ([\ \¯ \¯ \!]), :input!3 ([0.13671875 0.140625 0.04296875 0.13671875 0.0625 0.02734375]), :input!4 ([false false false true true]), :input!5 (:exec-while), :input!6 (:complexes-liftstack), :input!7 ([false true]), :input!8 (true), :input!9 (:code-rest)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

