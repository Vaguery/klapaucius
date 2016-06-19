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

    '[891333310M 571725784 #{287.234375 :string-echoall :strings-dup [true false true true false] :scalars-tag :exec-k :tagspace-stackdepth :refs-empty?} [1584 3708 3980] [true false true false true false false] :string-yank 624587626M 439767013 "ÃiE[Î±5ÚÕLËF²o¡" [\R \R \N \M \Î \«] :scalars-pop :booleans-replace :vector-conj ([2577 1646 1028 541] :scalar-add 96/175 :input!10 :scalar-later) ([138 116 1534] :ref-stackdepth :booleans-last :exec-cutflip :booleans-reverse) :vector-liftstack :vector-save 664619814 :input!7 :ref-store 639834543M :code-map [\¡] :scalar-sine [0.140625 0.05859375] 122493077M :string>? :scalars-tag [\ \ß \n \s] :set-savestack :booleans-shatter \Ò :string-save :scalar<? :set-yankdup 163/131 434982733M 237984375 :exec-k \ã :push-quoterefs :string-equal? [0.01171875 0.0859375 0.09375 0.13671875 0.0078125 0.1015625 0.0703125 0.03515625] :ref-notequal? [] 508236836M [2215] #{:generator-return ([] :strings-build [\S \I] :tagspace-return-pop true) (:scalars-againlater 435990139M (:booleans-generalizeall :input!5 #{"hmLNËÏÉÃ¹" (:refs-reverse :scalars-shove [false false false false true] \Z :scalars-nth) :booleans-pop #{:strings-pop [1823] 970044403M \Ò false 143/185 [false false] #{:strings-stackdepth :scalar-lots [\; \ \¾ \¬ \, \{] :vector-remove :code-cycler \y :strings-flipstack :string-notequal?}} :char-later [true false false false true false true true] #{[4764 3033 1759 3841 1313 3441] 133778676 :string-flush true "#Í'¹,F!" 47/60 182725891M :set-echoall} :scalars-occurrencesof} :refs-set :exec-save) :generator-rerunall :input!1) :input!1 309.34765625 :vector-remove (["mb$oC" "ÍRHKÄ5q" "H7¨k" "&<"] [0.09375 0.0390625 0.015625 0.08984375 0.046875 0.10546875 0.140625 0.109375] :generator-rotate :generator-save (:scalars-rest 943988473 :set-pop (:strings-swap (:scalar-arccosine :scalars-cutstack "ÍÇZÒ~Þ" :set->tagspace :vector-later) :input!9 :char-min ["¤Ã&6M&$Ä>¥mIa»" "]\"?ÍÚÁz]Óe£G" "|" "¢Qnr" "ÙTR~S0/h·4(ª Ã " "¦¶;U>»-çunsC£J" "ÎU½X"]) :booleans-occurrencesof)) :scalars-flush} 383.1640625 :strings-nth :chars-length "Ü­.4iË¯PDâ$Jn\\Fw¥" :exec-noop :push-bindingcount :exec-do*while [1937 4528 170 2923 1608 798 3019 1969] #{17.65234375 :strings-dup :char-letter? :log-stackdepth [false true true true true true false] :strings-flipstack 731366740M [\e \@ \i \D]} :booleans-concat [\ \> \t] :exec-s [0.10546875] :code-echo \ 218991576M :input!2 :tagspace->code \e :booleans-reverse :chars-echo :char->integer [397 1476 1534 4993 3586 269 3090 4231] 184.39453125 :scalar-storestack #{390.390625 [true true false false true] :scalar-sign 551477785M [0.0546875 0.0 0.01171875 0.14453125] [\b \g \r \Ã \¹ \ \q \, \{] 262584225 :refs-indexof} :set-comprehension :string-length [0.09375] :code-append :booleans-cutstack "_5" 257856550M :ref-empty? [false true false true] :strings-return-pop :char-whitespace? :tagspace-yank :input!7 :input!8 :boolean-flush :code-empty? :refs-stackdepth 61.27734375 308.48828125 :chars->code \$ :ref-new 481284249 :ref->code [0.04296875 0.11328125 0.04296875 0.15234375 0.03515625] ["¦0ÆCä*}O|ÌqÈ³"] [] 64762248M [true true true true true false false true] :booleans-indexof :code-echo :scalar-echo [4128] :push-bindingcount "±t¡­3" [] :vector-nth :input!1 38/7 883939245M :tagspace-liftstack 946839887M [\V \Á \L \8 \-] 837247345M [0.12109375 0.02734375 0.07421875 0.140625 0.01171875] 303.57421875 "X;Ï" [\ã \ \_ \Ô \ \Ï \>] 101.1796875 326916229M :input!1 :scalar-abs :input!2 [false true] :generator-yank :input!2 :scalars-rotate 74/3 :push-refcycler :input!1 [0.08984375 0.12109375 0.1171875 0.015625 0.125 0.04296875 0.0703125] :refs-echoall :chars-notequal? [2479 1029 934 2425 2911 1576 2773] 348.953125 :vector-cutflip :environment-begin :input!7 :scalar-min :refs-generalize "Âu^qÓ³¤Ð´#Y@ÕºR" :strings-conj 77/34 78/79 [false] \ true :string-reverse [\¹] :generator-again 755846742M :push-bindings 592222590M :scalar-add "->>»H'M6å" [true] [] ["j?®|ÓáO<mBÞcái" "~" "hp~Ë[x;`" "Ëkµ`v§+FOi«´±Â`M:" "dÌBy57'ÎhqÑh{·" "$ßºÁtbP^b" "Eo¶ÀPÕ`S"] [4406 2929 845] 297428614M [\$ \Ø \h \space \]] :ref-store :set->tagspace :refs-yank [0.046875 0.10546875 0.0 0.1484375 0.109375 0.1484375 0.02734375 0.08203125 0.03515625] #{:exec-savestack [0.09765625 0.09375 0.05859375 0.00390625 0.046875 0.03515625 0.1484375 0.1328125] [0.09765625 0.05859375 0.07421875 0.046875] 307.67578125 ([\¬ \Ì \æ \£ \2 \L] 211661842 [] [true false true false false false] :generator-rotate) 257031757 :input!4 :char-digit?} 40.4453125 [3275 3817 1017 358] :strings-flush :vector-conj ["Ç>8nÄ3¨3"] :generator-totalistic3 :error-stackdepth :string-max :chars-rest :refs-length (:chars->tagspace :scalars-generalizeall :booleans-emptyitem? :scalar-π 353.84375) :input!6 :scalar-return 188150107 [3642 115 3244 1369 2037 558 4837 481] :exec-flipstack "~DÑb?" [true false] [0.015625 0.08203125] 297.578125 :tagspace-merge #{308.9296875 ["Ç" "WdÑV;[" "<Ò-~ÒÓy¿ÕH­kov#¶qhØ" "1´q¢@6kA1g]²Â²," "7å×`æ¿<ÛKÌ" "Õ)#fa»" "\"áÎC+HÔ§ÞpWT_)nÔ" "xEct\"cM££uÙ4ãg¯¾t"] ("`´/ª)" :refs-generalize 297.8359375 :refs-replacefirst :input!8) [0.12890625 0.078125] [0.1484375 0.1484375 0.10546875 0.0703125] #{:booleans-flipstack [false false true false false false true true false] #{"¼CXÃdØÑÛnÂ7à!Ò" :boolean->float ["SàÎ¯Q/Á[O Ï<I­FBi:" "´Ö" "/"] 794625614M ([\C \c \~ \b \] :refs-tag 996122658 :strings-notequal? :scalars-build) [0.0 0.09375] [false false] [\ \p \T \} \0 \ \ \ \a]} 172/193 143/2 169/103 :vector-comprehension [0.11328125 0.10546875]} :scalar-yankdup #{:exec-pop :refs-comprehension ["rd7h²253" "KWË7à"] :boolean-empty? true 381.13671875 [true true false true false false false] :refs-echo}} :exec-savestack :refs-empty? ["RÁJL½;ÇYC!TØÃm" "V/aÚ|»ájáAI"] :char-equal? :boolean-flush :vector-cutstack :input!6 284.86328125]



    :bindings 

    '{:input!2 ([0.1171875]), :input!9 (false), :input!3 (\w), :input!10 (:booleans-shatter), :input!1 ([false false true true]), :input!8 (true), :input!4 ("SHI¡n â¸Û!Ñs\\'"), :input!7 (\'), :input!5 (:generator-reset), :input!6 (:boolean-dup)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

