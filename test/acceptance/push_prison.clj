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
  (-> (make-everything-interpreter :config {:step-limit 20000}
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
                            "\n items on :integer " (u/get-stack s :integer)
                            "\n items on  :strings " (u/get-stack s :strings)
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
  ;; caught exception: java.lang.Exception: Push Parsing Error: Cannot interpret '' as a Push item. running
 {
    :program 

 '[:exec-echo [2732 2256 1668 16 3909] :floats-pop :integer->code [false true] #{:integers-save :booleans-replacefirst :generator-rotate #{:exec-echoall \B :tagspace-scaleint [] [0.06640625 0.0625 0.11328125] :generator-flipstack :push-quoterefs :string-rerunall} [true false false true false false true true true] :generator-tagwithinteger :floats-return :tagspace-echo} :floats-return-pop :code->set :exec-pop :string-replacefirst [true false false false true false true false] [0.015625 0.0546875 0.0546875] [true false false true false] 788088312 :string-removechar 203917851 [4777 2892 2567 3032 42] :vector-replace :integers-later :exec-rotate \å :integer>? :set-tagwithinteger :input!7 #{:set-return :float-cosine [] "Éæ;N* m¹cYhÁ1" :code-null? :input!7 :code-points [true false true]} [true true false false false true false] [\W \¯ \; \Õ \¾ \§ \¦ \s] :integers-conj [] \ä [true false true] :floats-notequal? false true \} ("«çy¨>)?¼" :float-echoall false ((:string-empty? :booleans-sampler 419851951 ["¡C/g" "(rGÐäµ4 [å<_JAGâ]" "J¨Âäx c"] :set-liftstack) \S [0.1484375 0.05078125 0.1484375 0.140625 0.0546875 0.0703125] :integers-dup :booleans-contains?) []) :integers-reverse :refs-tagwithinteger "ÚX·ÅÅA¿º!/ÒjÂ5vI\"É" :floats-set [true true false false] :strings-shatter :integer-max :ref-storestack :integers-rerunall :string-empty? :floats-equal? :code-cutflip :exec-rerunall :exec-when [0.0546875 0.140625 0.13671875] [\: \k \'] 868330356 :strings-dup [0.09765625 0.09765625 0.0234375 0.078125 0.0625 0.13671875 0.09765625] true #{:refs-conj :chars-replace :floats-dup :strings-rotate [4314 428 4711 3901 1466 4266] :boolean->string ((:refs-empty? ["Àb·Ïlz©¡pT{Ú§5" "ªX½r©°OI{lyPÐAZ"] :chars-echoall :string-nth (:generator-jump :string-pop (:booleans->tagspaceint :vector-remove :string-save [true] :generator-empty?) :booleans-conj ["Ë_Ìz¯&¾" "n(n" "£EÜp" "Ó+1" "B#;5ã1_ÇæÀ©Ã\\C4-" "O|[¾ç#¢w¾ÏÔ á:ãÞ¦H"])) :refs-dup [0.0703125 0.1171875 0.01953125 0.0] "pÈNv²Õ\"À<" :refs-sampler) :boolean-later} :char-max [0.015625 0.015625 0.15234375 0.04296875 0.02734375] ["7jkÈ3d" "E¥ ÏæÝÌ.J»+" "^9" "ºG" "]PßË}uyÂ{®0yÓ"] \l "5e¹¾um_ \\Ñwâ" :floats-rest [265 391] :float-mod [0.05859375 0.00390625 0.0859375 0.1328125 0.1171875 0.10546875] :integers-store ([false true true true false false false] :exec-while :boolean-storestack (:floats-save [0.08203125 0.13671875 0.15234375 0.0390625 0.08203125 0.0703125 0.046875] 180548321 (:refs-pop #{:chars-echo :floats-cycler :strings-tagwithfloat \, :vector-nth :print-newline :set-sampler :booleans-store} :print-newline [\*] [\v \e]) :vector-refilterall) [true true true false]) [true true false true] [false false true true false false true] :integers-butlast "VxZhq" :strings-yank ([\Ó \_ \W \º \Ê \k \á \Å] :input!5 :booleans-take :refs-conj :float-dec) #{:refs-generalizeall :set-comprehension [] :refs-savestack :string-save :input!7 :vector-occurrencesof 931130277} :vector-replace (:strings-last 326579991 :set-comprehension (:tagspace-print [true false false false] (:float-arccosine :string-store :code-tagwithinteger :string-min :push-instructionset) [false false true false false true] :floats-do*each) :chars-generalize) false #{["rÕZQ_i¶¥hä_" "sµ?q1Bç=A" "«JÚoJ]" "§¾ÙM;ogg¾]Ä" "+ a" "EYÏAÚ?IÕb6S" "j6Ø" "0(T¬^ÐhàPÆ"] (false [0.1015625 0.0625] :strings->tagspacefloat :tagspace-merge :chars-do*each) [0.13671875 0.13671875 0.08203125 0.01171875] [true true false true true] :exec-equal? [2734 1729 1932 589 163] :strings-indexof (\y :ref-swap :string≤? :code-empty? [0.04296875 0.0])} \³ :boolean-savestack :input!7 :boolean-print #{["z=uÉR" "¦|^¤}" "V·ÆµRÃ94" "¥çÒÈ¤ä" "Ú?GÙ'R4zP¸ª¬" "F/" "A¡ßÃ·yàYÐ" "¢k\""] :error-empty? 44160351 :tagspace-flush false :vector-liftstack :generator-store} ["Ëo§À" "æ6db©¾L¢®b\"Êm" "Ä«à«@°ÎÆ½Ä1®ÉÇ¤Óç" "!ym2J²Qm¡$´=¬G" "fsÑ'³¨ z" "Ç©+§Ý*9=" "¿:Ø.Ì]lr59¸aÜ~ " "Ø9;`BÑÈÝ´µ#Ð" "·v|àÓg{GÖÐ¿¨"] [false false false false] ["Å:h^Îm4ÄnivÀC" "á.d¨K©Õ9ª<&¡±" "1;ØÄ«lSX" "9.ZÜ£EÊ1Ös" "«l¹ÞÝ×;wf^¯JU1Âç" "aL" "[à~nv¡ÁhDÂ" "ÕÞÝ4×t" "Q_¯»"] :string-occurrencesofchar :char-return :integer-abs ["×¾ÔzrÓ" "ßÕY1dc$Ëjf@w¬=" "âK°1RàÉ×áQ?ß¨Àªo" ";ÃºÜªB" "(kEv¿áÀEm" "Ú0.EkæÛ>Ø(&h=½" "QÐª¹lk(ÈnÎ" "àf«©" "ÀqCØ~¹9¿~Z"] 16692084 \v :integers-cutstack "|N³¥Y®ÉÔß¸CMy" :float-arctangent :float-save :booleans-last 368.03125 true :char-rotate :tagspace-echo :strings->tagspacefloat [2723 103 711 578 1360] [true] :refs-portion \u :floats-rotate #{:string->float :generator-counter :floats-last :exec-againlater #{:char-shove "%~Ù" \§ :float-tangent [0.07421875] 268.89453125 :chars-remove :booleans-take} :code-null? #{198932333 :boolean-store :exec-if :booleans-return-pop :code-points :chars-indexof :generator-echoall :input!6} [0.0546875 0.13671875 0.03515625 0.140625 0.0390625]} 335113961 [\ \¼] [\+ \W \Ú \x \S \Þ \½] [true false true false false true false true] 63998242 :code-sampler [0.00390625 0.07421875 0.0078125 0.1328125 0.1015625 0.015625 0.03515625 0.1015625 0.046875] :string≥? :vector-cutstack :integer-biggest 587006006 "Zj5D;d¤È¾" :input!1 [0.09765625 0.12109375 0.109375 0.03125 0.12109375 0.1484375 0.015625] 620511381 :integer->float :floats-return-pop :refs-return-pop [0.11328125 0.01953125 0.08203125 0.1015625 0.03515625 0.03515625 0.08203125 0.01953125] :input!7 :floats-shove "[¥m¸Óz£©B" :generator-liftstack :boolean-pop [\| \? \¾ \Q \3 \Ã] :float-savestack :strings-comprehension :exec-do*range 16.6328125 :ref-return-pop :char->code [0.125 0.09375 0.12890625 0.01171875 0.05078125] :code-savestack :floats-cutstack #{[253 2458 1035 1489 4113 3751 2715 963 3382] #{217.703125 :char-empty? :char-equal? :integers-yank 381.52734375 [0.02734375 0.0234375 0.14453125 0.140625 0.0390625 0.0703125 0.015625 0.046875 0.0546875] [973 1307 803] :vector-portion} true :exec-sampler #{:integers-yankdup #{[false true] :booleans-againlater :chars-length :char-swap :string-replacefirst false :code-member? :generator-tagwithinteger} :booleans-last " EX|²S^Ï2¤" :integer-dup :ref-print false :char-savestack} :generator-rotate :code-container :ref-cutflip} :integer-echo :integer-liftstack :integers-concat [false false] :boolean-dup :ref-forget (:booleans-rotate :chars-butlast false [0.078125 0.140625 0.078125] :refs-empty?) :char-empty? :integers-do*each :string-flush ["g¦Ôrb" "|©à]D&ß\"" "6aÇijrOÖ?Dhnmnm" "ÂÙØ¾+7n¥ÄT§i­,Á" "G{_T¹Ó¬WYÑ}bÊ" "]¿XvÈ" "´ånÇT:" "(^gß*©( |" "¥IF_¢eO¡µª/¤"] [0.0703125 0.046875 0.109375] :float-ln1p [";Ö®Y9wÌ#/Ãã¥" "!g¯d-i9:%ä" "º,VØhd²¶ßÛk¬¶j" "y''" "!äÞ¯Öd6R_¤·5Ù{" "9¥peÝ>Ð~Í!^.Ó" "1+rVÛYM/JÊØÛc" "zà;x]ÎÐÒ¾" "À9|%9¾`"] 268.578125 :vector-byexample :refs-tagwithfloat :floats-shatter false false :tagspace-tagwithinteger :integers->tagspacefloat ["¢Ñ," "}2¿]qHQËÕÎW¹Ü" ">zÑ(R£æ8" "QtÍRBÜ°7-bÆ:ßj2¥FTÝ" "-hsWgÃ"] :vector-pop :string-substring :tagspace-scalefloat :input!7 :boolean-flush :input!2 "°6ii9½?&N¹ØåÖ=" :vector-yank [true true false false false true] :refs-generalizeall :strings-tagwithfloat [0.01171875 0.09375 0.06640625 0.0] :refs-emptyitem? :refs-savestack [0.15234375 0.1015625 0.140625] :booleans-return-pop :booleans-sampler :string-min :chars-set :generator-shove false :integer-rotate ["Ð27ÂÍp0£ ´PW" " oCgTÁY=G<áG½[FÜS¬" "4ß"] :char-uppercase? [3211] :floats-set :ref-flipstack [\\ \R \v \Ô \2 \Æ] true :generator-stackdepth :exec-echo]

    :bindings 

    '{:input!2 (:refs-againlater), :input!9 (:exec-dup), :input!3 (:booleans-return), :input!10 (:ref-pop), :input!1 (:refs->tagspacefloat), :input!8 ([2318 896 3699 4830 3758 1530]), :input!4 (:chars-dup), :input!7 ("ß/_6¶O"), :input!5 (:integers-build), :input!6 (\Õ)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

