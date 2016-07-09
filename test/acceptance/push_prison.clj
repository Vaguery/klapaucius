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
                            "\n items on :unknown " (u/get-stack s :unknown)
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

    '[#push.type.definitions.interval.Interval{:min 28/179, :max 115.72265625, :min-open? false, :max-open? true} :intervals-store :generator-store :push-bindingcount :code-comprehension 98/51 ["ç@ä?. ãÅNFZQÜ" ";Ü7" "5~P}<$£Ï$>æFâm" "ÀKÍÊ¬Ã_²¹ª^+rN­²¯?" "å0[Q>º*c\"¾²<TÛÔ" "9z~"] :interval-rerunall \M :char-rotate :refs-liftstack :string-replacefirst :complex-add :exec-comprehension :string-notequal? true #push.type.definitions.complex.Complex{:re 224936639, :im 16.40625} [\Ô \;] :input!7 59/55 :refs-replacefirst :string-echoall :complex-pop :input!3 :intervals-save 75/97 :input!3 :set-subset? 152/97 188.07421875 #{:input!9 977431606 \i :intervals-print :booleans-swap ["a,ßa¶" "¸i«¥¢Äp¶ä¥æ" "¢¯h&ÉÕ­_µà1\\¢Î" "Y\\XÚxæ-O6Ù¶" "«" "2Ä"] false :boolean-and} false [true true true false false] 988352457M :push-unquoterefs :booleans-contains? ["ZVUÃl,i" "Dâ'©Û5XÙvoYU)]^" "Õv2å)wæÄU®r®NÚRß" "½áÉ-¿:ÐD»" "fLÅ hVsPQ*" "p_a¨¶=¢(¢â2/d" "Cpo8Ë¦ÔÐÛg" "@adÔD0QåÊwIÓ^¹Ux"] ["´Ò~Z×4" "Ò·^R»" "Om" "%hà~³f7¢Édwb½+" "&¦tiæÙ«¨ ©¿T"] 16/47 #push.type.definitions.interval.Interval{:min 1/2, :max 212.1875, :min-open? true, :max-open? true} :chars-do*each :code-flush :input!3 [] :refs-replace :scalars-swap :set-notequal? :string-return 377288367 :set-cutstack #push.type.definitions.interval.Interval{:min 31/63, :max 253.58203125, :min-open? true, :max-open? true} :string-dup #push.type.definitions.complex.Complex{:re 596508911, :im 290.953125} 390.3984375 #{120.640625 :ref-echoall ["?È¼~NºO4'#l8X*M" "ÊCS5QÈHAÄÄÀÍ¿Øä"] :boolean-shove :scalars-empty? :complex-swap #push.type.definitions.interval.Interval{:min 83/38, :max 31.046875, :min-open? true, :max-open? false} #{:strings-pop :char-echoall 105/26 [1763 606 2991 843 2816 1826 2756] :string-notequal? #push.type.definitions.interval.Interval{:min 103/150, :max 283.7109375, :min-open? false, :max-open? true} :code-position :booleans-save}} :scalar-dup ["Ê«`ºLQszÇºh" "T¡ÐsÒ«\\Ë×`" "dÄ<;P" "¾¾·kiW@²3ÞR" "â3"] :complexes-savestack [4013] [false true false true true true false] [\Å \. \¤ \¼] :scalars-byexample :interval-subtract :complex-subtract false ["Öe" "¨{_*¦JÅ`ÉAÁ]<à" "ETÅ"] :string-contains? [true true false true true true] :vector-first 842753143M :code-wrap :booleans-tag 881751571 :input!9 :input!1 :boolean-return :input!1 [false] #{:char-echo #push.type.definitions.interval.Interval{:min 73/112, :max 387.89453125, :min-open? true, :max-open? false} [\W \? \m \Ñ \° \¹ \Ï] #{:complex-flush :vector-cutflip #{[0.140625 0.12109375 0.07421875 0.14453125] :complexes-set 59/18 ["|¨ràã³µ"] #{["Û# "] :scalars-concat :string-print [0.07421875 0.1171875 0.03125 0.078125 0.140625 0.14453125 0.07421875] :interval-flipstack :vector-tag #push.type.definitions.interval.Interval{:min 23/74, :max 18.078125, :min-open? false, :max-open? false} :intervals-liftstack} false :string-rerunall :vector-refilter} [] false [2224 1725 4706 499 4782 387] 803199390 (:chars-nth false "ãnRQÕØ¸" :snapshot-end :boolean-equal?)} :chars-shatter \V :input!7 #push.type.definitions.complex.Complex{:re 21824243, :im 372.94140625}} :input!2 :exec-yank :interval-reciprocal #push.type.definitions.complex.Complex{:re 398503221, :im 12.52734375} #{:generator-againlater :char-notequal? 86592892 :booleans->code :scalar-sine :interval-notequal? :scalars-echoall :ref-new} :code-flush :intervals-occurrencesof :interval-echo :chars-conj :generator-later 6/7 :intervals-liftstack :scalars-generalize 248.12890625 83079396 "á°:?H£hc½Ø" ["ÍI,|(uK«ÐÆà" "Ï$" "`¼q<Äh(*·kqäCË9" " vFs" "¶å,å\"zÝäÙ2d©¯=*P"] :intervals-shove :intervals-equal? #push.type.definitions.complex.Complex{:re 448398489, :im 300.0859375} :interval-min ["¸Lºz/" "eº^Er¥YÛ`" "ÙONÛ" "Gq¸¶'à" "3"] :tagspace-later [] :vector-yankdup :scalars-tag #push.type.definitions.interval.Interval{:min 171/175, :max 14.125, :min-open? false, :max-open? false} [4884 1570 2570 4360 2937] :chars-cycler 198/173 ["[/:¢JFk§×Fnà,t.äx9" "Íl/Ærk;zpOCËDx 1æ" "¼" "K³~.ÓÏz_l¸#ä" "£e" ".{AÃrC<Ð" "C)_|Eª" "×DçB" "ãx Ú±´©s?¿ß7¡kå"] 898861045 579615387M :char-savestack :boolean-store :booleans-rotate (:scalar-stackdepth 138689498M :vector-swap :complexes-butlast (:vector-print :refs-rotate :interval-echoall [true true false true true true false true false] :chars-save)) :booleans-length :complexes-yank #{135.5 [0.1328125 0.12109375 0.01171875 0.06640625 0.06640625 0.01953125 0.05859375] :ref-ARGS :char-againlater :code-cons 181/194 [0.0546875 0.0390625 0.12890625 0.15234375 0.109375 0.1484375 0.0703125] ["a"]} [true true true true true false false] #push.type.definitions.complex.Complex{:re 662443497, :im 243.65625} 49899760M :ref-forget :exec-print 670616398M #{:scalar-divide [true false true true true false] ["ÇÔ" "ÆÄ@"] :vector-cycler [0.1171875 0.12890625] :print-stackdepth 736194883M #{:exec-do*count [0.0078125 0.08203125 0.09765625 0.06640625 0.09765625 0.02734375 0.0703125] 566110900M :scalars-take :vector-do*each :scalar->boolean 62449549 :complexes-remove}} [] 685715852 87/4 :strings-cutflip :code-position 28 #push.type.definitions.complex.Complex{:re 9908446, :im 136.328125} false true [true true] 353.58203125 (:scalars-conj [0.015625 0.1484375] :intervals-emptyitem? ["®»³¬¼Ü" "½jYçÓ²ã4mg, :" "S3VSuÝÎT" "w)^o¼aJF"] [2934]) 253348508 \® [false] :strings-shatter 31/86 :vector->set :exec-s \O :scalars-take :set->tagspace (:interval-recenter :vector-concat true :tagspace-rotate :snapshot-savestack) :vector-portion 517613102 ["¶ÑXáRÕc\"Ì" "ÔM6)\\M%ÏÚe'l»" "ÅbÝ£ÖÌª" "»5D.S-" "¶¯Y]{«VØkL"] 220.55078125 :input!9 :vector-savestack [\Ï] (:strings-butlast 906786178M :scalar-later true 309504766M) [] "VDáe|_(B³­" :char-save \: false #push.type.definitions.complex.Complex{:re 961893066, :im 316.26171875} :booleans->tagspace #push.type.definitions.interval.Interval{:min 1/34, :max 203.08984375, :min-open? false, :max-open? false} true :intervals-storestack :scalar-return :input!4 :code-do*range [2793 1220 1487 1863 4580 2603 9 3734 185] :chars-replacefirst :char-whitespace? "Dl;\\DS£×" :generator-savestack :scalar-tangent :vector-cycler false 663095376M :exec-flipstack :intervals-portion :booleans-occurrencesof #push.type.definitions.interval.Interval{:min 47/50, :max 22.6953125, :min-open? false, :max-open? true} "¼ZPRf{5|" #push.type.definitions.complex.Complex{:re 472332236, :im 258.53125} :exec-yank [] :char-rotate :input!6 "cvq²c?=\\Ýf[&]" (\« :generator-again true :chars-build []) #push.type.definitions.complex.Complex{:re 37264417, :im 104.51953125} ["vÃÈÈo" "KS{FXåÁÈÎqÒ" "§[·ÞÖSÊ&`e" "{" "Ñ.¢ÖÝ-j" "²Ä¸¥s7Xá³"] false :char-equal? :intervals-sampler 445938257 [0.109375 0.140625 0.05078125 0.06640625 0.02734375 0.0390625 0.0390625] :complex-print [\ \c \O]]



    :bindings 

    '{:input!2 (:interval-return-pop), :input!9 (:intervals-print), :input!3 (#push.type.definitions.complex.Complex{:re 300528216, :im 103.03515625}), :input!10 (:strings-last), :input!1 (:snapshot-againlater), :input!8 ([4901]), :input!4 ([true false true]), :input!7 (:ref-pop), :input!5 (:interval-store), :input!6 (\")}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

