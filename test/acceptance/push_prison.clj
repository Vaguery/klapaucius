(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
            [push.util.stack-manipulation :as u]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  (:use demo.examples.plane-geometry.definitions)

  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000}
                                   :bindings (:bindings prisoner)
                                   :program (:program prisoner))
      (register-types [precise-circle precise-line precise-point])
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
                            "\n top item on :error " (u/peek-at-stack s :error)
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
    :program [:integers-last [0.05859375 0.0703125 0.11328125 0.0859375 0.03515625 0.109375 0.1328125 0.015625] true '(:line-later \Ì [] "¾" :booleans-first) [\n \] #{202.33984375 :chars-yankdup :refs-replace [0.03125 0.0234375 0.14453125 0.109375 0.01171875 0.0390625 0.125] :code-subst #{["Ä.ÏG¦\\-." "vvªx!LÕ-LV¿" "E^¤;" "Ç8}u\\b"] [] #{231.32421875 :input!2 '(:chars-build :set-cutflip ["Å1_¾N#" "-!µhrm­^" "U¥je%rH¥" "fGF" "cdo\\(" "U±ÐÒG§"] :point-cutflip :vector-nth) :integer-save :floats->code :point-inside? :booleans-storestack :exec-return} :code-stackdepth 444326838 ";_¸kl" :exec-cutstack :point-empty?} \: [0.05859375 0.1484375 0.00390625 0.0390625 0.0 0.02734375 0.13671875]} [3654 1288 359 131 3635 1843 3034 2463 4707] :string-replacefirst :input!7 :boolean->signedfloat ["¹>wed¥^<ÃA©]^" "iÄo3%²=Q3Åº>" "R 2*G·Ë¶*Â¯>q" "5¾&z" "Æ$" "Ò0«8¼^¡£¨4@" "¸l]?C&" "'ª"] :integer-equal? :string-splitonspaces :integer-many [1039 4892 3164 2315 2770 971 2248 2440 1440] [\7 \Z \Ó \¤ \&] \± :set-echo [false false false false false false true true] :floats-print [ \ \H \ ] :line-circle-tangent? :input!8 :booleans-sampler :code-later :vector-print :booleans-stackdepth :vector-butlast ["'Xµ" "»7f*»0j^" "¥«" "]F~»¡SÅÍÀ·Ð" "·KF-É,F<y©" "¥ÏnN¦7¾_XG?¹%Ñr0" "cQZº" "|ÑZRIJÐ" "+¤r}<¦"] :set->code :ref-dump :booleans-length [0.046875 0.0] :integer-many [2541 4119 4782 4338 3136 3582 4757 1925 2285] :char-flush :refs-swap [2942 3945 3335 272] :code-reduce :generator-tagwithinteger [\µ \O  \D \® \+] false "LME'Y?Ô¸£n¢»" 775165731 :integer-print :code-map [0.1328125 0.02734375 0.1328125 0.03125] :tagspace-flush :ref-liftstack :input!7 333.20703125 ["LNw!uª'" "ªBÊÆ>MÌ"] [false false false true true false] :strings-new :refs-generalize :tagspace->code 910303112 :boolean-liftstack [0.0546875 0.08203125 0.00390625 0.0546875 0.0078125 0.078125 0.0390625] :code-null? :set-notequal? false 170.51171875 :set-empty? :generator-return :booleans-new :float-print :ref-equal? 315.03515625 #{:code-first [] :set-flush :float-tagwithfloat 301.76171875 :input!5 :circle-cutstack :tagspace-return-pop} false [true true true true true false true] :string-contains? :generator-flipstack [false false false false true true false] :code-do*range :string-cycler "o=Y0yÕ°dSG§@B¯¹92o" ")Ã­" :set-againlater :float-arcsine :push-quoterefs \J :vector-flush :refs-store :float-ln :chars-conj :floats-build :point-savestack :ref-echo :floats-portion :generator-flush :float->asciichar [4542 678 4528 5 3101] :float-pop :exec-save :float-echoall :input!6 :vector-flipstack :code-tagwithinteger :tagspace-againlater "ÁZ" :booleans-cutstack [] '(["Ã~jpÑLSaÒ­%b" "*~y/Vrj"] :boolean-storestack :generator-again [true true false] :floats-shove) [746] :point->code 963082656 :chars-reverse \Â :strings-indexof :char-return :exec-y :generator-echoall [208 2356] [3928 2690 2569 2966 1131] #{[] :vector-new 142410936 :float-return true :integers-reverse :print-stackdepth [0.06640625 0.08984375 0.0859375 0.109375 0.1171875 0.05078125 0.14453125 0.00390625]} :boolean-echoall :tagspace-scaleint :integers-shove :generator-pop :floats-rest [false true true true true false false] :booleans-tagwithfloat [] :code-do*count :point-storestack :set-empty? [2054] :code-tagwithfloat :exec-dup :booleans-rerunall [402 1136 2489 2554] :vector-yank ["ªi" "\\uvµ+`[='©yALª¸" "¾>'b]­e" "W¬&IÁZ" "\"DY>" "®ÍÎ"] :boolean-empty? [ \0 \l ] :booleans-indexof :integer->boolean :float-arcsine :chars-flush '(:ref-shove :char-tagwithinteger [852 4247] \z :input!4) 344752803 :char<? :string-cycler :char-stackdepth [2876 766] 127771942 ["RIBÇ2bÅ6qSN" "0ª0%Á^|" "\"`©" "#d" "·B$Fµ-¢5¢À"] 128360035 '(\, [4242 739 719] :float-tagwithfloat :line-yank []) [4346 2454 4948 3408 3567] :tagspace-offsetint \@ \¸ :ref-rerunall :float-sign :input!2 '(#{:set-cutflip :code-nth "Ì@!ÌK!J" true ["(ÍE¼¹I§§mr\\³Xº"] :strings-first :string-concat :tagspace-yank} :booleans-later "" :booleans-do*each :ref-store) :refs-cycler :exec-cycler :code-length :vector-remove true :booleans-remove :set-stackdepth :floats-indexof :generator-next :string-last :input!7 [] :circle-pop :integers-tagwithinteger :char-pop [0.0546875 0.11328125 0.1171875 0.0703125 0.14453125 0.08984375 0.01953125 0.05078125] [1004 4310] :integer-flipstack :code-equal? :input!6 ["T¥`ÁÈ:vºYj?7" "Æ=,#lÀÁ<M®¦'" "»\\ª" "v8/Ñ]oÊÂ|WWk?" " ®È-¾Pµu[" "B//EY¹EM" "ºd¯]" "" "7N"] :generator-pop :booleans-replacefirst :input!10 \d :code-tagwithinteger [582 1098] :booleans-take "ZÁ¹'ÒË¶" :input!3 :string-first "P=- #wÇ ª·Bek" '(:float-dup :input!2 (#{25.03125 [0.140625] :exec-do*count :vector-rotate ["79"] :char-pop [false false true false true] [\3 \ \u \ \V \$]} :code-length [4413] #{\@ 64.69140625 :vector-byexample :integers-rest [\¬ ] :chars-tagwithinteger :vector-liftstack} :refs-last) [] "}s¶®_qFºRÈ°º") [\ \ \' \ \e \ \ \E] 417001723 :chars-shatter [0.140625] :code-quote [3060] \± #{:string-yankdup :boolean-cutstack :set-yankdup :vector-take :chars-replacefirst :string->code :floats-rotate "®p:tGÑ¼xCEºb0jÆNX"}]

   :bindings {:input!2 :vector-cutflip, :input!9 :integer->code, :input!3 :refs-liftstack, :input!10 5443695, :input!1 :generator-againlater, :input!8 #{:float-save :set-notequal? :set-return-pop :integer->string #{:float-abs [1765 3644 1070] :float-storestack :strings-rotate :code-liftstack :tagspace-yank [0.12890625]} "°Q nK«4R´|.u}A" :refs-pop}, :input!4 :char->string, :input!7 "glcy5", :input!5 [".V«µ«" " ³ %" "Ð" "h" "1Q%Z" "1("], :input!6 :chars-cutflip}}
  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

