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
                            "\n items on :exec " (count (u/get-stack s :exec))
                            "\n items on :generator " (count (u/get-stack s :generator))
                            "\n items on  :tagspace " (u/get-stack s :tagspace)
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

    '[[\µ \] [2762 2389 4909 4444 158 1738] [1633 2388 4635 1377 627 1400] ["ÄLj¿Å·d ·Ë" "YCGnrÂ¹P;Ñd" "J7¼ZC]Ô Ée0\\" "Är`|Òr?{ $«|Ì" "v¿SÂn6f@" "ÄnFÃ5Ð"] :code-later :string-conjchar [] #{112.140625 :integers-pop :char-notequal? :string-replacefirstchar \4 :tagspace-comprehension :floats-rerunall #{[4815 3408 1352 1612 675] :boolean-liftstack :generator-empty? :code-store :generator-reset ["Â¿s¥Ã±0±Ò^º" "ft«µLdKj!" "ne !~"] :float-flush #{342.1796875 :boolean-return-pop [\ \9 \E \¶ \ \q] [] 899231696 false :boolean-tagwithinteger 412188890}}} :input!1 :chars-comprehension [false false false true false true] [2095 4843 1613 1479 1752] :chars-byexample "¾mde¢ªÔ" :string->float [true true true true false true false true] :char-whitespace? 46.5 [0.015625 0.1015625 0.0625] [true false false false] 316946313 :vector-yankdup 200.99609375 :boolean-savestack "dSTeED'xj,ËdÈ*" \ª [false] \Ç :refs-return :exec-y :generator-rerunall :ref->code :booleans-cycler :chars-save 490192015 :boolean-rotate 371.0390625 :code-empty? :ref-cutstack :chars->tagspacefloat [false false true true] false :floats-butlast :booleans-yank \° [true] :floats-return :refs-nth :set-tagwithfloat (true :tagspace-lookupint :floats-flipstack [\N \£ \½ \e] #{134.6015625 :strings-generalize ((:input!5 :chars->code [true] #{["fHºÁ" "7`sÕ`\\c ¦¯C&" "O" ".È§Á" "­®SÀl" "±~Faq[¾$Kl$\"\\°JÏQ" "?BÏÂ¦©±Ç1." "jDÒ« ­ÇÎÇiW(M3x" "´k4±newË.!KT´yNe"] :boolean-storestack [0.12109375 0.05078125] true \² :float≤? :float-mod :floats-comprehension} :vector-byexample) 100.55859375 :generator-flush :tagspace-flush \.) :tagspace-new [] :float->asciichar :string-echo [2918 3195 309 874 4534 4697]}) :char-save [] :booleans-conj 92.76953125 [] :boolean-echoall [\Â] [3046 4885 349 2717 1322 515] :refs-butlast 995450434 170.26171875 :floats-cycler :string≤? :ref-fullquote :vector-swap :ref-return :refs-sampler 535341800 :refs-rest :set-againlater ["nÐËÎ3bL&nD¤ÕyblM" "TÊ_>:¢Q¸z¯l" "Ï¢Cj¿-ÔÕÕ\"P_¶" "G­Ì­9ÕhºWF¥}|»¾r" "¤G" "6Ò¸ºO|_¡" ")µB«?"] :input!2 (:input!4 :generator-store ["aÍcÈ" "Ä¤{l!JLÈ{.ÍE¨" "A»ÁXÁ~g¡É.Ê" "]¬´³vR//^»xÂ¢aHº" "O,v¯´OC²qÅ}È" "ÈÄÒ¶!ªkB"] ".^|W°«Õt=" "KG \\eÂEJ)") :tagspace-lookupfloat :char-cutstack [\J \f \¦ \l] [] :input!4 #{:vector-againlater :booleans-generalizeall :floats-shove [true false false false true true false true false] :input!1 :string-reverse :integer-stackdepth :integers-flush} (:string-return 141.1484375 :code-wrap :boolean-or 267028121) [0.11328125 0.0859375 0.1015625 0.0234375 0.04296875 0.01953125 0.12890625 0.09375 0.05078125] [\- \E \n \X] true (:chars-return-pop :tagspace-cutflip 961507742 105.6796875 [\u \% \ª \] \`]) :generator-rerunall :char-whitespace? :set-subset? :code-liftstack :exec-cutflip ((:set-againlater [0.08203125 0.03515625 0.140625 0.03125 0.1328125] :chars-stackdepth "Nd¡Î=E-(H°~Ë6Í" [true true true]) :input!5 "t" :float-E [false false true true false true]) (false :code-nth :integer-many :generator-totalisticint3 :floats-new) :integers-pop :input!9 :char-flush 222.51953125 :ref-echo [true false true false true false false] [\B \Ó  \§] :char-tagwithfloat :vector-take (:booleans-reverse :float-multiply #{:strings-cutflip :integers-later [\"] :input!3 :tagspace-merge :booleans->tagspaceint :float-E [\¼ \Y \q \¼]} :floats-replacefirst [true false false false false]) #{[\Ï \£] :integers-return-pop :tagspace-cycler [0.03125] :float-notequal? :refs-save :chars-save (:string-save "Æºev¹NL9" :booleans-occurrencesof [0.0859375 0.15234375 0.03515625] :generator-swap)} 312.6171875 :booleans-do*each :string-pop "´" (:booleans-flipstack (:chars-shove :input!8 :push-counter :boolean-tagwithinteger :strings-portion) :set-swap :char-savestack :refs-replacefirst) :chars-return-pop [0.1328125 0.14453125 0.10546875 0.0234375 0.015625 0.0625 0.09765625 0.125] 40.56640625 :booleans-cutflip [0.0234375] :vector-empty? \Q true :boolean-store :set-empty? [3358 1457 2844 4494] [\u \G \g] [false false false true false false false] :chars-byexample :char-againlater :vector-flipstack 17.48046875 :input!3 #{:vector-return-pop :tagspace-liftstack :strings-generalizeall :refs-notequal? :vector-emptyitem? [2442 277] :boolean-tagwithfloat :boolean-and} :input!5 #{:integer-max 386.9609375 [true false false true] ["kV«q+¿gÔ½LÆh%" "QDN¬E/" "Z¸¸Lb¶9" "c?1(\"¾(62n7Ó" "8¦¡/» vi=³:L/¿`"] :exec-k :integer-dec [\X \³ \Î] :exec-liftstack} :input!6 :environment-end :integer-notequal? :input!1 #{233.7578125 246.4609375 :string-dup [0.1015625 0.0078125 0.1484375 0.1171875] :ref-storestack [true true true] (:exec-y :integer-rotate :refs-later :input!9 :set-equal?) [3442 2790]} :floats-cycler (:vector-echoall :vector-shove :refs-replacefirst :boolean-yankdup :integer-divide) :exec-while ["2/"] :tagspace-cutflip :floats->code ["¶Ä±Ä1" "µJ·"] :refs-dup [0.11328125 0.09375 0.0234375 0.0234375 0.05859375] :integers-do*each :boolean-rerunall :strings-new :refs-stackdepth \ :ref-store 70907018 :string-concat false :chars-replacefirst [\i \x \6] ["&µz#ÄoQ³µ(XÅ" "(ÁeÊ{¨ª9$!D¨" "Æ+®J7b*i" "k¨«G¡ÕÇS" "¦¦N­U" "[@Î" "y;2¼¬m¸" "l·¦,ÒQaÍÌB»Zv" "4Ð³A[i²,®^O2s"] :tagspace-max 223366584 :char-flush \L :boolean-or \ :booleans-cutstack :vector->set 469863439 :vector-portion :strings-length :error-empty? [113 4360 3896 4402] [\É \ª \N \j \9 \Ô] :ref->code :char-yankdup [\T \N] :booleans-echoall ["\"|ypEe¼tkF(" "vÍª³Ë|Ì)" "%O«tÃÈ.~UÅo4ÃÓ' " "<¤"] :integers-comprehension :strings-store :string-splitonspaces :ref-stackdepth [true false false true] 768638563 :ref-tagwithinteger :integers-build :tagspace-splitwithfloat :refs-set [\Ê \Õ \´ \Ñ] :vector-flipstack [\8 \¦] :float->integer [\z] :exec-tagwithinteger :float-π :ref-clear true 387.23046875 ([3371 2054] [false true true true false] :integer-tagwithfloat [0.0546875] :char≥?) :booleans-notequal? :refs-stackdepth :chars-return []]

    :bindings 

    '{:input!2 (:vector-dup), :input!9 (:vector-new), :input!3 (:floats->tagspaceint), :input!10 (:char-yankdup), :input!1 (422776526), :input!8 ("±£M"), :input!4 (:floats-shove), :input!7 (:tagspace-cutflip), :input!5 ("=:¶,op"), :input!6 (:floats-first)}
}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

