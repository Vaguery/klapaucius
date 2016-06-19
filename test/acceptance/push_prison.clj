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

    '[:boolean-liftstack (:input!2 :strings-print :booleans-swap 139/123 "Hv¦(KÕQÁbCfkB") :exec-return :scalars-contains? 998340593M :scalar-rotate ["P5ÄÑ2;Q|<ß" "ºYk*gY.ä§:" "D:Q äÔÒÑ" "©%æB¶-§rNÀ"] :char≥? :ref-later :char-empty? (false :strings->tagspace :boolean-tag :chars-concat 985649653M) :code-nth :refs-cutflip :char-min false :scalars-dup [3939 747 904 4652 3534 2268] ["À " "Y8M[)_XØÒ/g@ :à" "G¾>"] [\ \{] :code-save [",,ÀMy" "ÐÔ©©³Æ@_" "*"] :input!1 :booleans-portion :booleans-replacefirst [\ \@] :string-rest :input!2 false :input!1 :generator-stepper \p :strings-rest "×7R³FRY£¡" [2807 2478 3031] \j :generator-return-pop [\K] :refs->tagspace [0.03515625 0.03515625 0.15234375 0.13671875] :booleans-yankdup false :push-unquoterefs :booleans-echo :strings-conj 127.80078125 :booleans-rest :boolean->integer [4119 2972] [true false false true false true] :scalar≥? :generator-next :strings-echo 369.0 :tagspace-flipstack [\/ \ \B \» \] ["¬4" "¶©ÚÛ·¦ÖÜÓiv_Ø" "JUÑ" "¾¥#®|" "@\\%yÛæCÂµ]" "¹*@ßæsÑaº@§'i±t" "Vsáb&.¹7lÑç" "-Ë*r" "AÞ9<~BªJçÝ"] #{"yÕW½" 192/77 :refs-swap \® :vector-conj :refs-new :string-storestack :string-rotate} false :booleans-later :tagspace-merge ["Óa}qf²·Í¿g" "×·²~©ßq4b5§ËË#Ã" "ÃáIcÕ/Þe¨·Ð" "Ú';ÍLÃÂu­ÐDk)8" "zf¥¯dqÆàT%" "Â7o½Ê" "Øk\"V¼Þ¢" "²C|,´ã¯JUÑ§^£:$o1"] :string-replacechar [2433] :refs-cutstack "zd>\\}ÏO4µ{ZÃÓ" :chars->tagspace [\` \~ \l \³] [false true true false true true] :integer-totalistic3 :boolean-equal? :set-store :exec-when :exec-flush :set-savestack #{384657318 :strings-do*each :boolean-return 465166408M [true false true false] :booleans-yankdup ["¾KFQ»+b37±y" "ÐªbÑX-®¸ÌU¯u@J?" "Ux(` #71" "4Ð¸tÔl0«F"] [\« \ \7 \z \¼ \¼ \o \]} :input!8 :refs-sampler [3600 3889 2260 3039 1273 3358 2342] :exec-string-iterate [0.06640625 0.0703125 0.078125 0.0078125 0.00390625 0.04296875 0.1484375] :scalar-flush :booleans-nth [2520 253 1611 4287 4894] [0.078125 0.1171875 0.046875 0.14453125 0.02734375 0.015625 0.12109375 0.078125] :strings-take :scalar-yankdup :booleans-byexample :scalar-swap 793264893M \ :refs-build [true true false false true false true false true] :boolean->signedfloat [4469 4166 2930] :refs-againlater [0.08984375 0.109375 0.00390625 0.0546875] #{:booleans-stackdepth :tagspace-merge :char≥? :environment-empty? :boolean->string :string-store :chars-generalize 923554627M} [false true false false false false true false true] \4 [0.1328125 0.08203125 0.0390625 0.03125 0.09375 0.0078125 0.12890625 0.05078125 0.02734375] :tagspace-lookup :strings-contains? "Á(amã3iT³ä," :boolean->integer :input!5 ("<FpÒ¢5N®ÌÃaQ¯Ò" :input!2 :ref-shove [0.08984375 0.00390625 0.03125 0.0234375 0.05078125 0.1171875 0.09765625 0.0234375] 271.17578125) :input!10 :refs-store :code-do \¶ :ref-equal? :chars-savestack :code-yankdup [\¾ \¶ \Ê \k \ \É \¯ \±] 141/161 19/70 980059620M #{733906816M [true true false] :exec-y 65/14 true :vector->set \7 695643471} :tagspace-lookup (:chars-set 91.00390625 "q]`NWÁ^ÂG×ÕGm" \B :scalar-inc) 662155873 71/48 :scalars-later [] :refs-return :char-dup [0.13671875 0.05078125] :booleans-contains? 145.59375 [true true false true false true] [false false false false false] 250.02734375 [0.109375 0.09765625 0.0078125 0.046875 0.0234375] ["®3Û\"Å\\O*Î±ÄÔ-" "S¼K­æ¦" "zßx Ö+kG·­XUÄ" "¢KYYºÂtáiw" "}jn­ *²²Ýc»LÑà5%O" "m¿zÓ¤à²°ÛäWbAÑ" "¢åDm<'V" "GæWB7.(ºÍÆ" ""] [] 29/96 ["_au1¨}9" "6p²ç9" "Õ>ßd¿tH~" "OOÉ0Ê82V}qáÀ8C"] false :code-null? :strings-cycler :booleans-dup :integer-totalistic3 :ref-shove :exec-later 374.828125 \6 [] [\7 \R \³ \§ \Z \´ \K \, \X] 45/193 749079039 305.8671875 182084555 false :strings-equal? 553107068 [4107 1668 1869] :char-shove :refs-replace :string-comprehension :refs-nth [\S] 801912004M 803947076M :boolean-empty? \7 988585180M :code-later :scalar-π :char-againlater (:float-uniform false [0.0390625 0.08984375 0.03515625 0.078125 0.09375 0.15234375] :scalar-arccosine :set-tag) [0.0625 0.15234375 0.04296875 0.08984375 0.125] :scalars->tagspace :string-replace :char-againlater :string-replacechar :refs-pop :generator-pop #{:char≤? :string-flipstack :code-empty? :scalars-shatter :ref-cutflip 797185187M :scalar-log10 :scalars-pop} [0.10546875 0.14453125 0.15234375 0.03515625 0.109375 0.1328125 0.03515625 0.078125] 327.09765625 :tagspace-normalize :code-equal? :string≥? :tagspace-cutstack :string-cutstack 72/43 :input!9 350184399M :vector-empty? :code-member? [false false false] (:code-extract :scalars-swap :vector-store :char-flipstack [true false false false false]) :tagspace-count :scalars-return :char->code 157/74 661112519 [] [] :boolean-return-pop]



    :bindings 

    '{:input!2 (194/59), :input!9 (:refs-pop), :input!3 (:string≥?), :input!10 (:scalar-cosine), :input!1 (92380694M), :input!8 (:scalar-log10), :input!4 (:chars-nth), :input!7 ("vkÓÆÙ"), :input!5 ([3409 1252]), :input!6 (["j|`¦f¬²?" "g8FM" "âm" "K±>ª"])}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

