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
              (println (str 
                            ; "\n >> generator: " (pr-str (u/get-stack s :generator))
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n items on :unknown " (u/get-stack s :unknown)
                            "\n items on :scalar " (u/get-stack s :scalar)
                            ; "\n >> error: " (pr-str (u/get-stack s :error))
                            "\n\n"
                            (pr-str (u/peek-at-stack s :log))
                            ; "\n" (get-in s [:config :max-collection-size])
                            
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

    '[:booleans-shatter #{:scalars-flipstack :rationals-generalizeall [true false] true :booleans-replacefirst :code-position :string-shove} [false] :char-whitespace? 296.37890625 :refs-againlater [0.1328125 0.09765625 0.12109375 0.02734375 0.08984375] [0.01953125 0.015625 0.15234375 0.125 0.0625] "¬]y§s" :vector-echo [\ß \ \T \ß \¸ \Á] :string-store \ :chars-empty? :refs-yank "^ s²\"Ë" [0.08984375 0.125 0.01953125] :print-empty? :string≤? #{:vector-reverse [\¯ \F \Ë \e \T] :ref-rotate [true false false false false false true] :scalar-π :vector-set ["Ô¤]pVÆ\\¬ßÔ¢Á:" "ä^O{" "½9©z{«ËTg-L" "Æ2·iÙkÞMRUå" "Æ" "Ú»ÛàÔ0Ó(QRi»+" "3ä¤O^)¢4Ä\"$@`" "Â.hÄÝ'T3Ï~}ai" "p,Çc]H@a"] :tagspace-return-pop} :rationals-set 241.953125 [3508 3955 4604 4787 1718 4513 4422 338 1327] [\4 \ \2 \ \5 \9 \° \z] [] [0.0625 0.0625 0.0546875 0.10546875 0.125 0.1484375 0.11328125 0.05078125 0.13671875] :rationals-contains? :string-cutstack ["¡5¡À¹?»Ó!Aw@Å" "qsF|ÃÀ" "§" "6" "G|·" "á)[^Váf"] :refs-emptyitem? :booleans-dup :strings-swap :generator-shove :string-take [2866 3482 2103 4112] 125.2421875 [814 3876 679 4179 3170 1606 3230 2851] :ref-cutflip [4541 3937 3548 373] :rationals-storestack 172.77734375 :scalarsign->boolean :exec->string :exec-yankdup :push-nthref :boolean-empty? "8ã" [4722 522 4425 2445 1664 4780 2569 195] :refs-conj [4799 997 2326 319] :scalar-equal? :ref-tag :code-swap :char-rerunall "PÛ«Y0À5BkPÃ" :scalar-stackdepth :rationals-new :rationals-dup :char-stackdepth [\ß \q \i] #{:string-later :string-pop :generator-flipstack "{}pvUÈ" (false 165436622 :rationals-rest 37.45703125 :input!9) :string-length :chars-portion :string-first} #{137.0078125 \À :char-max 647818818 #{161426712 538779614 :exec-cycler \ [\Ä \± \ \5 \¡ \Æ \>] "ÂCäF$>²Ë@«s©Ä" :code-position :scalar-log10} :refs-byexample [0.06640625 0.05859375] :booleans-cutstack} \ 61.05078125 ["¯À}?(¤}Ú½=#m«" "3h¹¬¬¯o¬\"·ãÂe2nv" "¥WRb-Õ" "åæ_«®xÊæ;T1HekÕ6" "K³¿" "Ax7Ì¿'-ÔCËºwaÓ1¡&T" "ÃRvq" "ªá(¶x!´;¼[0ÈHQ]ØM^c" "<hÎPGßX|qä"] :vector-flush :exec-notequal? [\ \B \å] :exec-pop 946908687 :vector-comprehension #{:vector-yankdup :set-savestack :generator-cutflip :char-letter? [true true] :booleans-emptyitem? \w :scalars-shatter} "Ý<â§ÈÕ|¸ÍZ¾¦)/×ã" [0.0 0.01171875 0.0625 0.0078125] [0.0546875 0.0234375 0.125 0.08984375 0.0625 0.1484375 0.0703125 0.00390625 0.1171875] :strings-cycler :generator-store :input!8 :rationals-sampler true [3059 3368 719 537] \~ 243.6953125 :booleans-first :boolean-equal? ["É%nÙhM{" "=Ë" "ÓblY^±;wã¥+°³ä®±ân=" "?æQ+©²­E¸&Æ" "Oe(uÈ3$-äKTÊSc¬ÏºÞ" "/DÒUÖÌ"] :set-intersection [true true false false false true false] :code-swap ["±Á ½.p×5#]:H¤" "|" "" "ºl^Dmæ§\"8e"] :strings-remove :set-superset? 543310866 :exec-savestack "o+µj/RfÁ6´ßcÍ" :vector-tag 786637360 464961314 :scalars-stackdepth :string-swap ["]1/7@9|Þw[(lÊ" "Ëçµ|3£»«" "k" ":ÏP¥yD{± Ü»" "Ce" "3RTh:10ZÑ2©Î" "HäLÍ¼4¿çÙzA¡8}iÚ"] 53.296875 [] :boolean-shove :vector-stackdepth [\9 \ç] :ref-pop [1322 1073 1760] [true false false false false] :code-flush :boolean-swap [4760 3822 1487 532 2799 678 625 30 4791] [false] :scalar>? 130.5390625 ["/RH1¿¨¯Æe¢¨Ã#Ý)æ" "æ1]Ëå\"gbQÇix5+"] :scalar-some 441970130 true :input!7 #{:scalar-divide :code-nth 140445288 #{:scalar-ln1p :scalars-echoall :string-store \| :refs-concat :tagspace-lookupscalars :chars-indexof #{"À" :strings-cutflip 635465605 :booleans-yank 128243563 "D»BC¸EG" ")áÄ«Q®" :input!6}} :rational-cutstack ["6|w" "9©`2¯&"] :strings-rest :strings-new} [0.0546875 0.12890625 0.109375 0.1328125 0.08984375 0.03515625 0.03125 0.07421875] :input!6 ([true true false false false true false] :input!7 [\X \§ \¹ \2 \I \Þ] :rational-shove :ref-dup) [] [332 4136 372] [\4 \¼ \z \Q \\ \Ô] [true true false true true false false true] false :code-equal? :string-last :scalar->string :ref-later false :input!5 :code-savestack :tagspace-swap :tagspace-equal? :vector-cycler :booleans-build #{341248292 :refs-length [\1 \K \@ \ \° \Ù] :strings-do*each :strings-yank "¼Ò¢JO®)^{" :input!8 \Z} :booleans-cutstack 868839757 :set-superset? \« ["6Ç:&Ë¥Qyp`" "AY#rhàm94±+B ¤Þ" "'¡/á3¼ÖÃµ2ª;*" "ÓÓi{Æ@a£r" "j}t" "N¶Ý" "rbÄ=c+À«¥ÇÝ]­ãf" "ÕevÓÉÐ¬" "£n½LÐz¾63p"] (:string-againlater "nSÅ>Äh²»UÂzyR" [false false false false false] [785 2575 4890 1258 3700] :rational≤?) :chars-nth ["!/;*`v.VÝc"] \x false [] :tagspace-count :scalar-power 173456011 :exec-savestack [\W \8 \ \ \, \y] :rational-pop :chars-sampler :refs-equal? (:ref-cutflip :strings-flipstack :boolean-rerunall [1268 4045] [true true false true true]) ["%X-W¶Á" ";PFÁ8JÍÜ" " 5,+¨Å×¨ÜWå/" "t½(gÜ" "B½1]YQ½ Ë" "ÅÆ|­1Ëa³" ":}]" "§²OÓ>uÝA{npp"] #{720876137 :char-print :chars-sampler true ["`³)kiWâ³uàM¨>±_pnM" "X´FäÕ" "Ô¸~*çÐQ_Í9ÙÃq]" "Fåax}8n" "©"] :exec-cycler :string-store (:error-stackdepth :booleans-pop :exec-storestack :string-concat :boolean-savestack)} :boolean-not false :exec-cutflip :scalars->code \| "0f(r¼>" "(¶'\"¸¬'Á" true :char-yank :string-pop #{:boolean-faircoin [] :string-pop :rationals-return-pop :char-store :exec-sampler :tagspace-lookupscalars :exec-shove} false [2389 3405 4412] [0.07421875 0.0] :boolean->integer :tagspace-cycler :booleans-echo :char-flush [3976 3187 2934] \Í \á :rational-notequal? 971716008 :char-max :generator-stackdepth [479 2929 4479 4314 1967 718 337 1862] 91.46484375 :strings-nth :generator-stepper :scalars-yank :code-flush :rationals-rerunall :vector-shove [\É \ \µ \$ \Ï \° \i \ \3] false :strings-notequal? :rationals-do*each]



    :bindings 

    '{:input!2 (:chars-return-pop), :input!9 (:chars-byexample), :input!3 (:code-yank), :input!10 (:rationals-reverse), :input!1 (:string-cutflip), :input!8 (#{#{"Ó/®" "M`ÛÁ å?q/" :code-pop 323.19921875 :rationals-occurrencesof :booleans-liftstack ([464 87 2649 196 1108 3584 1621 3950] :refs-shatter :char-dup 69642739 [])} :refs-equal? [\ \´ \U \\ \space \º \.] :refs-storestack [\. \/ \\ \£ \] [0.01171875 0.12890625 0.03515625 0.03125 0.08203125 0.1484375] :exec-do*times}), :input!4 (#{:code-sampler :ref-empty? :code-rotate :generator-stepper \­ (:strings-first true false #{:strings-storestack :vector-shove (:code-return-pop ["·¢" "ª´6M" "ecÌK©¤xsÑ°ÎÐ hË"] :scalar>? [3321 4042 714]) true "ÜxU+mÈæ7ÅO" :rationals-yank [false false false true] :rationals-replace} [false true true true true false]) :booleans-replacefirst \}), :input!7 (:chars-equal?), :input!5 (:string-last), :input!6 (:chars-store)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

