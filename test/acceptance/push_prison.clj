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
                            "\n items on :generator " (u/get-stack s :generator)
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

    '[(:string-conjchar :scalars-dup [true] "C-sMäacÊº),Û}Í~]" :string-echoall) :refs-cutflip true false [] 6/43 \space :set-rotate :code-yank [] ["jÙK4åB" "Ü>È©dY9­/FQ" "¬G©ªw3sC¶{:<" "lÄ­>K1_O@" "ÚA" "ás?r-Å'Ýs%" "Â" "«ßÖ'Ï>"] \Â :scalar-arcsine [0.0234375 0.1484375 0.0234375 0.00390625 0.03125 0.01171875 0.0546875] :char->string 299373489M :ref-swap :scalar-many :vector->code :strings-remove :char-return-pop :input!1 \I 331.125 :code-againlater 218.30859375 :chars-store :generator-stackdepth :scalars-return-pop [2832 2374 2798 503 4595 2966] :strings->tagspace 3/20 :string-removechar 857157133 \w "©GÖ0¿KË" :exec-dup [652 4028 3519 4947 4404 4702 2539] (#{:exec-do*count :vector-byexample :booleans-shove 133074919 [0.0] :generator-rotate [0.08984375 0.00390625 0.1171875 0.05078125 0.02734375] :code->string} 49/65 [0.0 0.0 0.0859375 0.0234375 0.0859375 0.0546875 0.0625 0.01171875] :code-drop true) :char>? true 106.8671875 :booleans-length :chars->code :set->code :scalar-divide :set-swap :scalar-stackdepth 35/76 false \6 true :code-tag :string-flush ["*ÀÍ&·áÂÊ{ÖfmÃ" "HÜ¸´Ín¶jnËY#ãÊ" "" "i" "«mXá»bØ&MH&Q½±p[-" "tb»Ý1«UÎ" "c]âÂiÎËewuT "] :string>? \« [2997 3596 4403 721 3447 1054 356 1378 1382] "yCi" :exec-string-iterate :char-tag [\l \ß \Î] 4.0546875 :char->integer (552574285M [] [] :scalars-indexof true) :boolean->integer :strings-dup [796] [] :set-shove :set-cutflip true :char-cutflip :strings-conj 383.0859375 :strings-storestack :code-dup 113/82 460652015 :tagspace-againlater [] :exec-cutstack :tagspace-lookupscalars [true true] :string-spacey? :scalar-echoall 173.2265625 #{736764410 266368445M :exec-y :input!4 false :booleans-take :scalar-tag :vector-refilter} #{"+Õà¤Í Qá\"uÐâ" :char≤? :code-if [] :vector-last :chars-butlast :boolean-later (:string-tag :vector-contains? :scalars-shatter :code->string "!cQ")} :refs-do*each [3934 3573] [0.078125 0.05078125] :scalars-echo :boolean-yank 68347464 :tagspace-cutflip [0.12109375 0.0546875 0.0390625] :scalars-replace 10519075 :booleans-do*each :push-nthref (:set-yankdup 149.55859375 :scalar->string ["ÕiàÎÉàj¦CÇ" "w¶,1a" "£" "z^WvGbjGKz0}¶" "ÛkÍqàI·]" "ÁÈ-pv_i" "%£åÄuß=HÂiÉ»âÓß·" ")á³£ØÅErÑ6" "OAâ^v "] :chars-length) :ref-later "Â" 761133781M (["d~w¢1>_OàÇäOF" "©¬£\"I©1¦" "eä9)Z"] :refs-return-pop 507789738 :scalars-return \I) :strings-generalizeall :vector-emptyitem? :chars-conj (444502934M 569604544M [\t \ \o \y \ \~ \D \Ã \ç] :vector-return-pop #{[\; \À \_ \I \_] [73 3279] :strings-conj [" äI½p" "Ú¼X)cm²r¯­âÎÇt" "+ÉÎD" "Ë©$" "F$aL4µeN*Ëb2?" "!dàek^ bV.¤>Ì$5S" "´Ö1ºÇ\\Û¼"] :refs-save :refs-echo :booleans-print [4409 97]}) :scalar-arccosine ["¹3s>r-p~`çHÜçrÐ" "¤" "®!¸-[!qâ·º¦[" "UÃßV-;»àH"] [] :strings-empty? "Ì&ÈÒåP¬tUµ©ÂÕ!ßæ,~ã" false :input!2 304.59765625 :code-noop :strings-replace 218.60546875 :ref-savestack :refs-new ["´Ô¹,\\È" "l¨2¹f¯µÚµ.j_¹u¤s~³" "ÌÀ" "/E£E­CvQÌ$ÏW" "ÉO@È!²Ó¿" "8$NZª\"" "nÊeÚ­­$¥ÓwE`"] :environment-empty? :scalar-tag :code-dup :strings-yankdup "×e9×Uà;ÞÐW[l" 335372311 29.2734375 (:strings-cutstack :vector-cutstack :boolean->signedint 17002510M :refs-equal?) 645329452M :set-later 306090745 [\ \µ \ \Õ \á] :code-sampler :set-empty? #{:scalar-cutstack :set-notequal? :char->code :refs-againlater :refs-savestack :vector-do*each :set->code :vector-cutstack} 294839734M :char-digit? :set-flush [0.14453125 0.09765625] :code-points :chars-cutflip :generator-echoall :scalar≤? :vector-echo [true false true false true false false true] 377.7265625 :scalar-inc :booleans-flush (:booleans-store :code->string :set-storestack ("f+o­1$" :ref-equal? :code-noop :strings-sampler \Å) :strings-save) :generator-rotate [0.15234375 0.06640625 0.0 0.109375 0.08984375 0.09375 0.109375 0.02734375 0.08984375] :booleans-echoall [true true] "{ÀES6$T\"" [18 94 959 800 3860 4293 577] :tagspace-cutstack :tagspace-yankdup :input!2 [0.0703125 0.02734375 0.0078125 0.05078125 0.0703125 0.09375 0.07421875 0.03125 0.09375] :vector-liftstack #{[1884 1379 875 3689 2550] [\ \ \y \a] [] :scalar-flipstack \M :char-min (:string-conjchar false "/¡.0#:æ mÒ¦" :char->integer :strings-equal?) :code-tag} :scalars-length :booleans-cycler :char-pop :booleans-take "9l@¿aÏÒtLstFKo" :booleans-echo 127126917M #{63.92578125 [0.07421875 0.0 0.0859375 0.03515625 0.08984375 0.109375 0.046875 0.01171875] :scalars-do*each :strings-rotate \U :code-member? :strings-flush 929176921} :vector-flipstack [\S \] 565119840 "ikhB¿YXs~)p" [0.14453125 0.05859375 0.109375 0.08984375 0.07421875 0.03125] 930570013M :set-swap [\Ã \Ú] :input!6 :ref-equal? false :tagspace-stackdepth 2/3 :set-echo true :ref-return :strings-equal? :input!6 :generator-storestack 653290538M :scalars-byexample :strings-later 10.80078125 :refs-sampler 37/49 60285261M ["y8jÛ»wØ$DkÚ"]]



    :bindings 

    '{:input!1 (327.1796875), :input!2 (:chars-generalize), :input!3 (:string-later), :input!4 (:string-reverse), :input!5 (:chars-echo), :input!6 ([false false true false false]), :input!7 (312985067), :input!8 ((:chars-cutflip :exec-sampler :code-yank :code-pop :refs-comprehension)), :input!9 (true)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

