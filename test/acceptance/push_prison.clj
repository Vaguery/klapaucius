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

    '[162.77734375 :generator-flipstack [0.078125 0.0] \Ï :vector-concat :float-arcsine (:chars->code :floats-later :floats-save (:generator-stepper :integer-rerunall 244739743 :char->integer :string-notequal?) :strings-rerunall) :integers-do*each ">KA^QÆrØa]v" [true true true true false true true false true] :integer-pop 643385547 ["Í[H(¶{" "¢1ºq(7" "¶G=E$s{¬§ÉÄ¯i=ÎÍÃ" "*Hl" "Ù×ÖÞvc1[p"] :chars-return-pop :string-contains? :vector-notequal? true :booleans-conj :vector-length :strings-indexof false false 851615479 :floats-reverse :booleans-cycler :chars-notequal? \X :integers-yankdup (:integer≤? :strings->tagspaceint :integer->code :code-savestack [146]) "o7Ç³OfÀ<¬æ«Ì" [2193 1610 4900 699 2199 2489 2831 3611 2819] :set-echoall :push-nthref ["Ô¿UYu+kß" "Z¸È3áÍtä¶" "!" "ªW2°" "B«"] :floats-pop :boolean-later :exec-liftstack :float<? :integer-empty? (:print-space :string-first :refs-portion "¡1kç¶Øj9" (:float->integer :set-flipstack #{267.015625 #{:float-ln1p :floats-sampler :boolean-yank \* :chars-sampler :integers-emptyitem? :vector-remove :booleans-storestack} :floats-swap "ÖSÎAÓ~<ãÙ" :exec-dup [1359 2899 743 388] #{:generator-rerunall :ref-flipstack #{[4809 4290 2232 1697 1475 4928 2965 1961 4738] :integer-shove :integer-divide :code-do*times :integers-swap \ :string-cycler [1198 1316 1460 2812 3870 3392 3228 10]} ([\9 \) \> \P \@ \© \A] :input!2 :code-length :chars-generalize :refs-cycler) :chars-shatter [0.01171875 0.15234375 0.1484375 0.12890625 0.01953125 0.0703125 0.00390625 0.04296875 0.07421875] :string-solid? :string-rerunall} "rMÊ?j;(³à>x3H"} :strings-cutflip :code-print)) \¥ :integer->char :tagspace-offsetfloat [4695] 389627028 :set-echoall "/¿Ý/zÆ" :chars-rerunall \Æ :ref-equal? :float-max :chars-empty? :generator-stackdepth [0.125 0.0078125 0.0390625] :chars-conj [\[ \q \! \Ï \\ \Ê \­ \d \~] #{:booleans-rotate :chars-concat :ref-empty? [2342 3641 383 35 373] :generator-empty? :refs-butlast [2299] :chars-portion} :floats-tagwithinteger "ÂÊsÅ+" ["Ýb" "¤/¸wær" "¶'®V@=F¥U5¾¤4H9bg" "ÙÝÑG²§" "|6É§" "ßá" "ØÆ%UÀtK¶" "ßURÓ±KnuT 6Î°emA" "[£FmÃz®1¼µ ÔÄC=F"] [true true false] :code-reduce [] :print-space "K?XuÜ" #{:float-dup :code-extract ["/Ã(7X\"µ" "]" "iØ9 ®Æ&/1-«" "å:j'Wu±DßTXÄmMp"] [\Ò \§ \6 \f \space] [1745 3271 1691 2202 1856 4951] :refs-concat :input!6 :strings-contains?} [\ª] :booleans-last \Â 346.0703125 :input!3 :booleans-take :floats-shatter :chars-byexample :input!5 [true true] :exec-rerunall :string-yankdup [\æ \r \ \ \j \Í \G \°] :input!7 :strings-replacefirst :code-return-pop :generator-stepper :refs-savestack :code-yankdup [\r \O \± \ \ \i \S \¾ \+] false :floats-print "XsÓYÂ%É±Y" 339.4765625 :char-yank :refs-reverse [2361 4074 1764] :ref-exchange :strings-againlater :tagspace-comprehension :chars-echo (:char-againlater [1044] #{:input!9 #{:vector-shatter :refs-tagwithinteger [0.12890625 0.00390625 0.140625 0.015625 0.109375 0.15234375 0.06640625 0.109375 0.078125] [\© \t \/] [false true false false false true false] :tagspace-print [false false true false false true] ["*¤gà·Å¡G[" "_}Ñ/ÄÓMaTx" "³/Ö}¿QJÞ}¸^9ä®" "¢ÑÄ!ã,ct4[Î¤[" "u%D=L"]} \J #{91.75 :integers-rotate :integer-return #{:string≤? :string-yankdup :chars-length :booleans->tagspaceint true :integer->numerals #{#{:float<? :string-dup 249379604 (:char-max :generator-shove :string-conjchar true []) :boolean->string :strings-shove :integers-againlater :ref-againlater} :vector-yank :set-intersection :code-do*count false :push-instructionset 801096444 :input!6} :booleans-replace} :tagspace-stackdepth :tagspace-return :strings-reverse :string-rerunall} [true true false true true true true false] :boolean->string :tagspace-savestack :floats-occurrencesof} :code-drop [true false true true false false]) :ref-later :char-cutstack 930437871 :float-notequal? [0.0390625] 199.375 :set-equal? 394212552 :refs-generalize :string-containschar? ["Ã(ªÙ" "l0}¹k" "«D" "¶CÜe×E" "^GÏr®" "g"] :input!9 [\> \Þ \\ \U \¤ \ \t] :ref-pop [\q \Ð] :floats-againlater \ :ref-dup :floats->tagspacefloat 303.19140625 "Â¬bGs1." :integer-divide [true true true true false] (:floats-notequal? [] :generator-againlater #{:integers-liftstack :booleans-do*each [0.03515625 0.09375 0.015625] [\' \Ê \® \Ü] [false] \{ (:input!7 :string-store :string-tagwithinteger ["»A¿P½k¶Qdb6§_¤°" "ÕÜk" "©jÖA³Û" ".<_FM°×" "}Fs×[v" "TOf180ÚsAãâ¶Ã=Õ" "]æÇ"] :floats-portion) :integer-savestack} :chars-conj) :strings-flipstack :integer-tagwithinteger :chars-equal? :float-later :set-later :integer-some (335478233 "G" [4274 2361 1531 3756 2815] :integers-do*each :strings-yankdup) :integers-tagwithinteger :string-substring [false false true true false] #{#{:generator-againlater :char-notequal? 728592309 :integers-generalize :string-notequal? :set-later :string-first :string-echo} :set-print :refs-generalizeall :ref-dump true :float-sqrt :vector->set :char-digit?} :integer-flipstack [\½ \< \­ \× \k \ \Ø] :generator-yankdup :integer-dec [true true true false] [1398 2891 139 3113] :integers-reverse :ref-swap 920713454 ["zl¼_): ~*Ô. egÒ»Â<" "äy¾°oC2" "Þ*·ÓDªca8Ç{¿Jã" "¸\"á¼0&"] :exec-when :floats-generalize :ref-flush :strings-notequal? :floats-rest 214.58984375 (753299919 :generator-cutflip :chars-generalize "foj¯ËÙÊ³ÃÄN" :code-noop) :strings-nth :integers-yank 363.57421875 :chars-return :string-solid? :push-quoterefs [true false true false false true] :strings-byexample ["·¢" "" "oÅ©"] :refs-store 939093113 :chars-later [784 819 1396 4724 1226 2008 35 3076] :strings-butlast [\# \\ \Ð \ \³ \ \] #{:tagspace-later [0.03125 0.05078125 0.1171875 0.0625 0.078125 0.1484375 0.1328125] \ :float-equal? :log-empty? :strings-last :code-echoall :integers-flush} [0.01953125 0.05859375 0.109375 0.05859375 0.1328125 0.11328125 0.0390625 0.13671875] :strings-contains? :floats-generalizeall :integer<? [true false true true] ["#]ãã>" "±àB¸Ë®±.¬²;Ì" "Y¿¡n¡7e@" "" "À°¸»=w¹#'U×+^" "_kÖYb´|N"] :code-empty? :generator-jump #{:integer-mod :exec-tagwithinteger :integers-rotate :set-dup :code-rotate :booleans-return :vector-remove "¢A6ØI\\"} :ref-empty? :set-yank [\] 705661151 true :tagspace-max "sLß#1(<G¨oa¾" :exec-flush :refs-pop #{74.10546875 :booleans-length \ :char-swap :boolean-xor "}" :booleans-echoall [\u]} :input!9 998305094 \Ì :exec-sampler 322.32421875 :chars-generalize #{:chars-rerunall 107.43359375 :exec-pop (:strings-yank "ÚÂ" :exec-y \¢ :chars-pop) :integers-yank :boolean-empty? [0.0390625 0.140625 0.06640625 0.0546875 0.0625 0.0625 0.140625 0.03125] :string-notequal?} :integers-rest 192.42578125 :string-replacefirstchar :refs-againlater 61.9765625 true :strings-comprehension :code-contains?]

    :bindings 

    '{:input!1 (:boolean-and), :input!2 ([0.12890625 0.0625 0.0390625]), :input!3 (:tagspace-return-pop), :input!4 (:generator-flipstack), :input!5 (:integers-rotate), :input!6 (:string-save), :input!7 (:booleans-return-pop), :input!8 ([true false true true]), :input!9 (:integer-echoall)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

