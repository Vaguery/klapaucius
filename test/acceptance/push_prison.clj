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
                            ; "\n items on :floats " (u/get-stack s :floats)
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

    '[["¿P" "?&W©³4F·As+TÚ!å 6%" " £8SO?2À<;\\g¸" "ÅË¦Xx¬²="] :booleans-store :strings-replacefirst :input!8 :set-yank :integer-divide :integer-yank true [0.140625 0.0390625 0.0234375 0.0703125 0.09375] :tagspace-splitwithfloat :exec-savestack :code-tagwithfloat 341206405 :chars-storestack [] (:refs->tagspacefloat "´«ÛGµÐàl¹" :integer-return-pop :floats-flush \) :integers-nth 80.9609375 :float-sign :string-return :integers-comprehension "6D¤¢v8}$·" :integer-min :chars-print :integers-indexof [] :char-later #{:strings-cutflip :ref->code :integers-contains? :integer-store [4215 3096 1706 934 1677] :float-dup :float-empty? :exec-cutstack} (:set-tagwithinteger ((:tagspace-splitwithfloat (:refs->tagspacefloat "\"}¡'Z¼x§ÞW¯Y" \« :tagspace-flush :floats-build) :floats-equal? :tagspace-return :strings-replace) :char-cutflip :char-uppercase? true [\g \¸ \Ó \J \[]) [" ;%ªlÓZBigE" "^P-â5FÒÝAç" "l1'¨I7C^0{¢x?m&P:" "]]-ÂÈ6OÔØ©Î>ÌmÝ"] (:refs-cutstack :char->float ["3LL" ")pDFzS«]W¦^" "å@ÊËwUÍ´²" "ÛÎ:Ór¹IAX°¬´¹ÖRYÆ" "QæÆÑ«pÏ3+´ÄÏZ" "]r33¥²²FE±Ô<~" "r½O|¡³ÓW¡_¿Ëæ3ç" ":N¨8)×1" "x"] :floats-length :input!3) :floats-indexof) :strings-byexample false #{251.9296875 :refs-do*each (:strings-return-pop #{[1819 4907 669 1827 4275] :input!3 289.32421875 :ref-print :input!8 :integer-tagwithinteger :refs-shatter :vector-sampler} :floats-replace :integer->boolean :code->set) :generator-cutflip [] #{#{341.1953125 \L :booleans-pop true [4297 404 326 1224 1541 187 1747 47 4221] "uRR`K,¹={rxQÈyKy" :generator-pop :integer≤?} "Í<Ûws" :float-power :generator-againlater #{652409013 [\ \% \t \ \e \B \ª \;] :chars-conj :char-return :vector-replacefirst false "¯=« bµ" 144441659} [true false] :strings-set [2818 3833 216 2594 2142 1688 947 2308 1997]} [\a \ \® \] :booleans-generalize} false :boolean-xor :tagspace-lookupint :string-replacechar :refs-comprehension [] :float-arcsine [0.0390625 0.06640625 0.125 0.1171875 0.1328125 0.15234375 0.14453125] :float-save #{"_®oË'" [""] [0.1328125 0.08203125 0.11328125] (:exec-empty? :integers-generalizeall 202.69140625 594986071 (:ref-forget :chars-shatter true [".^Dju\"UÂnf" "³Øk¿/ m/p9~ªz8>9$" "{B~;¼" "ÐÂá|²âÌP(Ù3" "ÖÖäØ¬fÁ³~=¦¼tÊ¦¼("] :integers-echoall)) :booleans-storestack :string-concat #{:integers-cutstack :integers-rerunall :float-shove [\Ú \% \n] ["Eàd$Z(ÑÃ$" "@ÇJá2~PÈ" "À¨e&Ü®5"] :tagspace-echoall :tagspace-values :char-flipstack} [0.03125 0.12890625 0.08984375 0.1328125 0.0 0.0859375 0.1328125]} [true true false false] 194.1640625 :input!4 :exec-shove :set-return-pop :strings-byexample #{:generator-rerunall (182.1796875 true :boolean-dup [0.10546875] :float-empty?) [true true true false false false] [0.00390625 0.0078125 0.01953125 0.0 0.0546875 0.01171875 0.0546875] :input!4 false \W :code-yankdup} :code-cutflip #{:string->float [] :string<? (:integers-indexof [\q \ \Ú \ \space] [0.015625 0.1484375] #{[0.08203125 0.08203125 0.0 0.01171875] ["" "|ÅX7-¼ÙfAQÎ" "a¸/¥,*H" "\\lÇkË²mÐÈ >¿rBª" ".P5,"] :vector->set :float-liftstack :vector-echoall :code-atom? \{ :vector-indexof} :vector-storestack) \O 440554811 :exec-swap "ÑQ"} ["  Ét×Ï~À¨>%" "à.ÌOT²CÎ5" "f[dÄ²¿fÍ KÎ" "µ)²" "lPi u:9¾5Ë," "qnM?1"] :ref-flipstack :string-sampler [\Í \Ú \ \0 \% \ \s \ª] [] ["GÁs" "/´ÂÍ¶_xa¸" "J" "v9+ã" "Ý$`Æ§" "[6©K>±5¸¾UÚ3 ¯%"] [\R \y \ \L \¦] :floats-butlast :push-bindings :set-flipstack :strings-yankdup :float-store :tagspace-flipstack :floats-storestack :tagspace-values :tagspace-notequal? :exec-return :string-yank :booleans-cycler :strings-take [true false true false true false false] :booleans-new false [1867 715 3906 4667 730 4417] :integers-emptyitem? :integers-butlast :string-flush :char-echo :strings-cycler :exec-liftstack :char-echoall :ref-save :generator-flush ["ÕX~(\"ç½W9mPàY" "Â^^£×&9Ö¤Íã\"-" "ÝYÊsMÛMÄ+0pp¨P*À"] :floats-reverse 622856764 :generator-liftstack :code-cutstack :integer-max :booleans-butlast :integers-generalize #{:set-yank :float-tangent "¬_\"m2¢Ü" :exec-rerunall [1912 4534] 776299236 166867948 :booleans-generalize} (:set-flipstack :exec-do*range \Æ [4049 989 4995 4324 2707 2347] :boolean-cutflip) :boolean-return [true] ((:integers-echoall :floats-empty? :string->integer :float-empty? true) :vector-empty? :input!5 \X :log-empty?) :tagspace-sampler :log-empty? :input!7 [0.02734375 0.109375 0.0859375 0.0703125 0.0 0.01171875 0.1171875] [] :boolean-rerunall :integers-generalizeall :float-yank :code-flush :push-quoterefs :booleans-butlast :refs-tagwithinteger :code-save :chars-cutflip 338.23046875 :refs-return :float->asciichar :string-occurrencesofchar ["GE¼'ºR Õ-y@\"Ûæ" "ÃÕºÕP[ÆQo+0Âmq" "ÓÏVl1D.~¯±¡Q¯±½¨" "Ì|"] :floats-replace ["e©¼°kn( 1[à,[Iº0C" "­QàÂ" "µ" "5>7RÁ7²+¿¾sÎ" "K¢XWva8_\"8?z ¢SU " "ÃÑ(Â£¨" "%|q¶£=s#" "|q$1n¾ã" "ÚD"] :boolean->integer :strings-contains? :integers->tagspacefloat 385451118 :tagspace-lookupintegers [0.15234375 0.0078125 0.08203125 0.00390625 0.06640625 0.12890625] :char-flipstack \] [2773 1541 4965 3511 163 3470 1084 3338 1563] :ref-exchange :char-echo 664857934 \­ ";°o&£+h¾lB/`" :vector-contains? :refs-return-pop :float-store [3456 1350] :error-stackdepth "Û¿FGpnÝ×" :generator-yankdup :exec-rotate :tagspace-return "}kA¬" \¿ :ref-clear :ref-exchange :refs-store 332646072 :set->tagspaceint 500165632 :float-sqrt #{:float->boolean :vector-return-pop :char≤? :set-comprehension :tagspace-againlater :tagspace-lookupfloat :chars-dup :float-rotate} #{:string≤? [false true] :strings-conj :integer-tagwithfloat :floats-cutflip :input!4 \Z :chars-stackdepth} :ref-new :input!3 ["Ô¹oG]MÄÎ-D3" "©³L_jC{¸"] :set-echoall :refs-cutflip 134.6328125 [607] [1016 772 3376] :floats-againlater :boolean-tagwithinteger "äZ$:=Ô=Ä,u¢Ë\\" (:code-drop [] "Î" ([0.00390625] [false false] [false true] #{:integers-pop [0.0390625 0.0390625 0.07421875 0.0234375 0.10546875 0.046875 0.02734375] :string->tagspaceint :generator-rotate :chars-storestack false ["#\\¯Ü¥¢4O" "ç¥ÔA¯" "¼`" "ÒUt$K%" "s¡µL9x¤'Ë"] "a6¶ÕÄ]3P"} :code-noop) :floats-contains?) :refs-contains? :integer-smallest :float->string :exec-notequal? :char->integer :booleans->code :booleans-conj :tagspace-tidywithints :exec-cutflip :vector-tagwithinteger [false true true false false true true false] ["|Sw" "Ú*Û,@ÀS£¨.-" "@neÙÍEgºç³åâ" "Ö­.Ó°¹h¤ÌI$9Ê6" "¦" "¼¦§\"<'A¯³ÐÖr¶X²"] :vector-return-pop :float-min \µ :exec-later \É :exec-pop :code-shove "Q«>Å]\\aá]­Ê" :string-empty? :exec-pop :exec-sampler :floats-emptyitem? :vector-return-pop #{:set-return :char-storestack :ref-empty? [788 4669 4384 3213 3413 635 3664] #{[false false true false true true true true] :code-first :floats-reverse :integer-some true :integer->float :string-cutflip :strings-replacefirst} false :exec-return [false]} true (:code-cycler true [\ \£ \ \£ \K \( \* \® \h] false ["P]Ð¨Å¯I" "M" "¬4£¹j¥7¥?c" "n¡ZÔ¸Á}8~JX-l²_ÐQ" "ÝÝ\\VÙG'½" "*6µ$" "æ»DO+dwà}M`uL" "À3Ý³4RVØAq#´L" "mªÚ"]) :push-bindingset [\Ð \" \ \i \Ó] :exec-echoall :input!7 \½ :integers-do*each [0.08984375 0.140625 0.03125 0.13671875 0.1171875 0.12109375 0.1484375 0.11328125 0.03125] ["MKjX>t³_v¥'" "»WW¾LgÐl¶Rµ¼" "ËA4l×¯$P." "ÄE;Q"]]

    :bindings 

    '{:input!1 (:booleans-portion), :input!2 (:vector-portion), :input!3 (:float-power), :input!4 (923352291), :input!5 (:float-tangent), :input!6 (:strings-rest), :input!7 ([2908 4798 1080 4948 819 4497 88 4200 3724]), :input!8 ([]), :input!9 (:chars-nth)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

