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

    '[:exec-comprehension :generator-savestack #{89.0625 :chars-conj :ref-storestack ([\ \$ \7 \space \] :refs-replace [3283 429 2491 888 2889] :string-liftstack :input!6) "²¿h³1ie?¼8ÆiwWh" :input!1 :vector-conj :vector-later} ":ÓÚf±" false 93.43359375 :vector-rotate 168.6328125 [true true true false false false false false true] :scalars-yankdup :vector-replacefirst [619 520 1857 4357 1029 1437 681 2331 4810] :scalars-nth [] (:scalar-notequal? [0.13671875 0.12109375] :boolean-equal? "e£º<É~pUÒbNx" 104.1328125) :input!5 false :booleans-later :input!4 :scalars-indexof :scalar-shove :vector-do*each (:generator-jumpsome :vector-do*each :char<? :exec-later 417513075) :string-swap :boolean-flipstack :string≤? :code-extract :strings-take :string-yankdup :set-store :refs-stackdepth :strings-rest :refs-byexample :input!6 59.1953125 #{:refs-comprehension :generator-rerunall :strings-echo [3155 4578 1460 324] [2026 4530 1654] 178729576 false :tagspace-againlater} [0.046875 0.12109375 0.109375 0.15234375] [0.125 0.109375 0.01171875 0.0625 0.10546875 0.10546875 0.0859375 0.12890625] :vector-echo ([0.078125 0.13671875 0.0078125 0.09765625] :strings-occurrencesof :scalar-save 327256309M :tagspace-lookup) :refs-againlater [4697 1749 2519 655 486 3358 4479] 23/168 855190559M \Z :code-print 129.49609375 :string-echoall 177115118 195842895 [] :error-empty? \´ \Õ false :code-empty? \ 288.90234375 :chars-length :code-liftstack :input!4 284.14453125 ["=*AwÆ­¿·" "V²j" "&8©"] 49/23 :input!2 :strings-save (:scalars-echo :scalars-later \× true ["-E£«È'" "8T¸1ÐvÐ" "QÄH°ÔrÍ¨\\" "WI\\H" "Å*ÖÊØiÆ©|(ÒË=WQ=¶"]) #{\ :code-equal? "Ä?DÌÇª" (:refs-portion 385177551M :exec-swap 7/17 :booleans-concat) ([3984 2614] "Ø*¸½ä" 9.3515625 :string-replacechar :boolean-later) :scalarsign->boolean 939146697M :vector-sampler} "ÇÃ+1Û+6ã}h" :boolean-return 497148066 :exec-empty? :input!6 :generator-dup :scalars-echoall 43.375 799845144 :string≤? :char-uppercase? :refs-dup 460407015 ["/}¤¾´³®v@"] :scalar-shove (:strings-return :exec-pop :vector-yankdup :refs-sampler :string-shove) ["{ªE3Î" "ÇYKÄâ" "@_%si#PX¤V\"6%ck$Û&" "]§%³LhvUlÚE¶t:7Æ" "×z­ÌTÑCSÒ\\æ*µM" "sáæPAQÎMË/WÔ¹Ú"] 353.40234375 :code-comprehension [\] [\Z \Ý \Ù \u] :code-sampler false :input!9 131/158 [] 69/16 :scalar-E 325.83984375 (125/76 :environment-begin 41/29 (["âttoA¤pÕ(lÂk¼NC?¡" "¹¹s³ÉlähÌª±-¢´Z" "|R4-6å>#"] 53.8515625 (:strings-generalizeall :input!5 :scalar<? :tagspace-rerunall :strings-liftstack) ",B>©" [\A \Z \×]) :string-flush) :code->set ["KÉÂÏZ1ØÐ¼ÆÔfbx¯¡m"] [false false true false true false false false true] [\w \Å \S \] :set-subset? "eNi" :scalar-cosine 549950427 :chars-return 654660858 \ :vector-flush :scalars-cutflip true :scalar-tag :code-container :boolean-stackdepth ["q{!Q0." "A~Ü|1ÀßÉ®$ÂÊË± ÛØÇ%" "[jÅËÄ>ÃD" "ÓL*Ô\"HäXÃk$" "" "tY(&" "L³6Du¹³"] :input!2 :scalars-tag [0.11328125 0.1328125 0.03515625 0.0546875 0.12109375 0.07421875 0.04296875] [\U \w \ \a \ã] :char>? :booleans-emptyitem? [\ \N \9 \^ \ \8 \l \]] [333 829] [0.0078125 0.0234375 0.00390625 0.0703125 0.046875 0.0078125 0.15234375] [\± \¾ \Z \²] :booleans-yankdup \O :chars-replace 889185812M :code-length :input!7 :code-rotate 115.9296875 "+G£h©àh%JwbCH×1-" :strings-generalize :generator-swap #{:chars-set :chars-cutflip [] [true false false true false false false true] :exec-later :strings-liftstack \> :tagspace-return-pop} :code-liftstack [0.0390625 0.14453125] #{:exec-pop :error-stackdepth [true false false true false false true false false] :boolean-3bittable 724967931 [\±] :boolean->integer :print-empty?} :code-contains? :chars-take :strings-new :set-flipstack [\X \x \K] :refs-cycler [false true true false true true] :chars-againlater (:vector-tag :code-list :code-do*times \1 208.42578125) "¨Ú+" :boolean-pop :code->set :refs-length 919753246M :code-points [\ \¿] false :refs-length :input!8 [0.09375 0.0078125 0.02734375] :input!3 :generator-shove :booleans-take :scalars-reverse :input!7 :scalar-rerunall (:exec-do*times :vector-liftstack :input!10 "Ötf" 48/91) :scalars-return-pop :boolean-and :strings-remove :char-whitespace? :chars-reverse :code-cons [1972 3949 3789 4744 2961 406] :vector-echo "dÒ`pfM¤M9Üç'sG" :string-solid? :code-againlater 67/71 (\£ #{214.953125 :boolean->float #{368534717 5/79 :boolean-empty? :scalars-last :string->code 25/77 "lOµ" [false]} :char-return ["W¡z¨£Ê´8" "ÐAZZµ'§D<GÓP9K7" "ÉÜç4" "^£r@µ" "Ó" "¨¦àGw®7¸ÜA*\"°"] :string-return-pop #{:char-whitespace? :chars->code ["ÂrjS[¦" "]}8" "gmKEDÄ·Ç%/h"] :exec-return 93/38 "×OµÂÁ¸" :input!6 :tagspace-save} 167/90} true [true true true true false] \¯) ["N®? ÃsEeÉÉª±ÍRÐÉ`" "gråQ(Ê" "b¶" "ÎoÙ" "~Ôç­usge·«" "_" "ÉÏÕ2¹½ªÔXHF" "':ßBÁ¿%" "¿"] :scalar>? (:tagspace-scale #{:input!9 [] ["k®" "/¤%^ØN±ÖF" "´¹½±Ñ]" "¸¥POM¨Ú" "fLÒ¹zcÇZ[ I°Jæ" "x)æufÕX,"] :char-cutstack :vector-do*each 81/68 348432515 :chars-empty?} :scalars-tag :scalar-many "_áGqÒ") :booleans-pop (:generator-return :scalars-cycler [false true true true false false] (:vector-return-pop 173.3125 :input!9 ["g«" "¿`1" "r×!" "E8?Md¬°#" "ã2±LqÔ-DyC" "6?É1½¢YA2a" "\\ÂÁ2·Y3[£QD" "E\\uÔVÅo¦®C"] :char-store) [4785]) \¥ :code-yank :refs-echo :ref-new (#{322.5703125 :string-sampler [3590 3877] :vector->code :environment-new :push-bindings \R :print-newline} :input!6 (:scalars-first ["À4fåu!!ÁD&Y:Ó8A(" "t&@¨" "$RzÚÙ1x°@" "I1¶Ó(yZ¨´8ØåÒ" "ZA©/Â²§§7E£fp" "\"ÛcV0;¶­S" "ZPF¡âyq"] (:code->set :set-yankdup :booleans-againlater :strings-replacefirst :scalar-cutflip) :boolean-xor :char-letter?) :booleans-nth ["FáÑ¹¼Ý-X#¾T±ß²à°D" "§PL®³Ú«" "TÚ,eJB!Oæ" "åÝ:AWzmIZaã^" "ÅÚY5µIË[9" ")Sv-°u" "­à§o®i­^¬k " "B-¼ÐÖ"]) :string-yank :scalar-fractional :string-store 660275624M ["ÇØdfÑ@»Ub'YÈ±¬" "­7+D\\" "³åÊpXvÊp¥" "up" "ÜZ¤q}a¯gÖl(5!Û½¿" "DÖ´"] :exec-flush :generator-echo ([] :scalars-cutflip false :scalar-float? []) :input!4]



    :bindings 

    '{:input!2 (true), :input!9 (:scalars-set), :input!3 (:vector-rerunall), :input!10 (45/82), :input!1 (true), :input!8 (#{"Ç¸ÌI¨và°U½®ÔÆ@2fµ" [false true false false false false false true true] true "q:&n·Sa­<×[Ñ­R" :string-containschar? :chars-save :ref-againlater "±ÃI=dp"}), :input!4 (:char-pop), :input!7 (115/71), :input!5 (:chars-reverse), :input!6 (:scalar-power)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

